package es.caib.ripea.plugin.caib.validacio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.helper.EvidenciaIdExtractor;
import es.caib.ripea.plugin.validacio.ValidaSignaturaResposta;
import es.caib.ripea.plugin.validacio.ValidacioSignaturaPlugin;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDetallDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Implementació del plugin de validació de firmes emprat per validar firmes no
 * criptogràfiques (firma àgil)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioFirmaPluginApiEvidenciesIB extends RipeaAbstractPluginProperties implements ValidacioSignaturaPlugin {

	private static final ObjectMapper mapper = new ObjectMapper();

	public ValidacioFirmaPluginApiEvidenciesIB() {
		super();
	}
	public ValidacioFirmaPluginApiEvidenciesIB(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	@Override
	public ValidaSignaturaResposta validaSignatura(byte[] documentContingut) throws SistemaExternException {
		try {
			// 1) Extrerue EvidenciaID del PDF
	        Long evidenciaId = null;
	        if (documentContingut != null)
	        	evidenciaId = EvidenciaIdExtractor.extractEvidenciaId(documentContingut);
	        
	        if (evidenciaId == null) {
	            return null;
	        }

	        // 2) Obtenir informació de la evidencia desde la API EXTERNA d'EvidenciesIB
	        EvidenciaWs evidencia = getApi().getEvidencia(evidenciaId);

	        if (evidencia == null || evidencia.getFitxerSignat() == null) {
	            ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
	            resposta.setStatus(ValidaSignaturaResposta.FIRMA_INVALIDA);
	            resposta.setErrMsg("No s'ha pogut obtenir informació de la evidència " + evidenciaId);
	            return resposta;
	        }

	        // 3) Descarregar el document firmat amb l'API EXTERNA d'EvidenciesIB
	        String encryptedFileID = evidencia.getFitxerSignat().getEncryptedFileID();
	        EvidenciaFile file = getApi().getFile(evidenciaId, encryptedFileID);

	        if (file == null || file.getDocument() == null) {
	            ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
	            resposta.setStatus(ValidaSignaturaResposta.FIRMA_INVALIDA);
	            resposta.setErrMsg("No s'ha trobat el fitxer signat per la evidència " + evidenciaId);
	            return resposta;
	        }

	        byte[] originalFirmat = file.getDocument();

	        // 4) Comparar si l'annex és idèntic a l'original
	        if (documentContingut != null && !MessageDigest.isEqual(originalFirmat, documentContingut)) {
	            ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
	            resposta.setStatus(ValidaSignaturaResposta.FIRMA_INVALIDA);
	            resposta.setErrMsg("La firma del document no és vàlida (els fitxers no coincideixen)");
	            return resposta;
	        }

	        ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
	        resposta.setStatus(ValidaSignaturaResposta.FIRMA_VALIDA);
	        ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
	        detall.setResponsableNom(
	        		Optional.ofNullable(evidencia.getPersonaNom()).orElse("") + " " +
	                Optional.ofNullable(evidencia.getPersonaLlinatge1()).orElse("") + " " +
	                Optional.ofNullable(evidencia.getPersonaLlinatge2()).orElse("")
	        );
	        detall.setResponsableNif(evidencia.getPersonaNif());
	        detall.setData(evidencia.getDataInici() != null ? Date.from(evidencia.getDataInici().toInstant()) : null);
	        resposta.getFirmaDetalls().add(detall);

	        return resposta;
	    } catch (Exception e) {
			e.printStackTrace();
			throw new SistemaExternException(e);
	    }
	}
	
    private ApiRestClient getApi() {
        return new ApiRestClient(
                getEndpointURL(),
                getPropertyUsername(),
                getPropertyPassword()
        );
    }

    private static class ApiRestClient {

        private final HttpClient httpClient;
        private final String baseUrl;
        private final String authHeader;

        public ApiRestClient(String baseUrl, String username, String password) {
            this.baseUrl = baseUrl;
            this.httpClient = HttpClient.newHttpClient();
            String basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
            this.authHeader = "Basic " + basicAuth;
            
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }

        public EvidenciaWs getEvidencia(Long evidenciaID) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/secure/evidencies/get/" + evidenciaID))
                    .header("Authorization", authHeader)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Error obteniendo evidencia: HTTP " + response.statusCode());
            }

            return mapper.readValue(
            		response.body(), 
            		EvidenciaWs.class);
        }

        public EvidenciaFile getFile(Long evidenciaID, String encryptedFileID) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/secure/evidencies/getfile/" + evidenciaID + "/" + encryptedFileID))
                    .header("Authorization", authHeader)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Error obteniendo fichero: HTTP " + response.statusCode());
            }

            return mapper.readValue(
            		response.body(), 
            		EvidenciaFile.class);
        }
    }
    
    @Getter @Setter
    private static class EvidenciaFile {
        private String name;
        private String mime;
        private long size;
        private String encryptedFileID;
        private byte[] document;
    }
    
    @Getter @Setter
    private static class EvidenciaWs {
        private Long evidenciaID;
        private String nom;
        private String personaNom;
        private String personaLlinatge1;
        private String personaLlinatge2;
        private String personaNif;
        private Date dataInici;
        private Date dataFi;
        private EvidenciaFile fitxerOriginal;
        private EvidenciaFile fitxerSignat;
    }
    
    @Override
	public String getEndpointURL() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VALIDA_FIRMA_AGIL_PLUGIN_URL));
	}
    
    private String getPropertyUsername() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VALIDA_FIRMA_AGIL_PLUGIN_USERNAME));
	}
    
    private String getPropertyPassword() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VALIDA_FIRMA_AGIL_PLUGIN_PASSWORD));
	}
}
