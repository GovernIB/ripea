package es.caib.ripea.plugin.caib.procediment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ProcedimentDto;
import org.fundaciobit.genapp.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.procediment.ProcedimentPlugin;

/**
 * Implementació del plugin de consulta de procediments emprant ROLSAC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentPluginRolsac extends RipeaAbstractPluginProperties implements ProcedimentPlugin {

	private static Map<String, String> unitatsAdministratives = new HashMap<String, String>();
	private Client jerseyClient;
	private ObjectMapper mapper;

	public ProcedimentPluginRolsac() {
		super();
	}
	public ProcedimentPluginRolsac(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public ProcedimentDto findAmbCodiSia(
			String codiDir3, 
			String codiSia) throws SistemaExternException {
		logger.debug("Consulta del procediment pel codi SIA i codiDir3 (" +
				"codiSia=" + codiSia + "codiDir3=" + codiDir3 + ")");
		ProcedimientosResponse response = null;
		try {
			StringBuilder sb = new StringBuilder(getServiceUrl()+ "/procedimientos");
			
			String params = "lang=ca&filtro={\"codigoUADir3\":\"" + codiDir3 + "\",\"codigoSia\":\"" + codiSia + "\",\"estadoSia\":\"A\",\"buscarEnDescendientesUA\":\"1\"}";
//			String params = "lang=ca&filtro={\"codigoUADir3\":\" + codiDir3 + \",\"estadoSia\":\"A\",\"buscarEnDescendientesUA\":\"1\"}";
			
			response = findProcedimentsRolsac(
					sb.toString(),
					params);
			
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar el procediment de ROLSAC (" +
					"codiSia=" + codiSia + ")",
					ex);
		}
		
		if (response != null && response.getStatus().equals("200")) {
			if (response.getResultado() != null && !response.getResultado().isEmpty()) {
				for (Procediment procediment: response.getResultado()) {
//					logger.info("Codi sia: " + procediment.getCodigoSIA());
					toProcedmientDto(procediment);
				}
				
				return toProcedmientDto(response.getResultado().get(0));
			} else { 
				return null;
			}
			
		} else if (response != null && response.getStatus().equals("400") && Utils.isEmpty(response.getResultado()) && es.caib.ripea.service.intf.utils.Utils.equals(response.getMensaje(), "La petición recibida es incorrecta(parametro: filtro // Tipo esperado: filtro)")) {
			return null;
		} else {
			throw new SistemaExternException(
					"No s'han pogut consultar el procediment de ROLSAC (" +
					"codiSia=" + codiSia + "). Resposta rebuda amb el codi " + response.getStatus());
		}
	}
	
	
	@Override
	public String getUnitatAdministrativa(String codi) throws SistemaExternException {
		if (unitatsAdministratives.containsKey(codi))
			return unitatsAdministratives.get(codi);
		
		try {
			String urlAmbMetode = getServiceUrl() + "/unidades_administrativas/" + codi;
			
			String json = getJerseyClient().
					resource(urlAmbMetode).
					post(String.class);
			
			logger.debug("Response get unitat administrativa del rolsac (codi=" + codi + "): " + json);
			
			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaUnitatAdministrativa resposta = mapper.readValue(json, RespostaUnitatAdministrativa.class);
			String unitatCodi = null;
			if (resposta.getResultado() != null && !resposta.getResultado().isEmpty()) {
				UnitatAdministrativa unitat = resposta.getResultado().get(0);
//				logger.info(unitat.getCodigoDIR3() + ": " + unitat.getNombre());
				if (unitat.getCodigoDIR3() != null && !unitat.getCodigoDIR3().isEmpty()) {
					unitatCodi = unitat.getCodigoDIR3();
				} else if (unitat.getPadre() != null && unitat.getPadre().getCodigo() != null && !unitat.getPadre().getCodigo().isEmpty()){
//					logger.info("Search pare");
					unitatCodi = getUnitatAdministrativa(unitat.getPadre().getCodigo());
				}
			}
			unitatsAdministratives.put(codi, unitatCodi);
			return unitatCodi;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments via REST",
					ex);
		}

	}
	public ProcedimentDto toProcedmientDto (Procediment procediment) throws  SistemaExternException {
		ProcedimentDto dto = new ProcedimentDto();
		if (procediment != null) {
			dto.setCodi(procediment.getCodigo());
			dto.setCodiSia(procediment.getCodigoSIA());
			dto.setNom(procediment.getNombre());
			dto.setResum(procediment.getResumen());
			dto.setUnitatOrganitzativaCodi(getUnitatAdministrativa(procediment.getUnidadAdministrativa().getCodigo()));
			//Com que Procediment ens ve amb Boolean i al nostre sistema ho tenim amb boolean primitiu, si es null ho tractam com false:
			if (procediment.getComun() != null)
				dto.setComu(procediment.getComun().booleanValue());	
			else 
				dto.setComu(false);
			
		}
		return dto;
	}	

	private Client getJerseyClient() {
		if (jerseyClient == null) {
			jerseyClient = new Client();
			if (getServiceTimeout() != null) {
				jerseyClient.setConnectTimeout(getServiceTimeout());
				jerseyClient.setReadTimeout(getServiceTimeout());
			}
			if (getServiceUsername() != null) {
				jerseyClient.addFilter(new HTTPBasicAuthFilter(getServiceUsername(), getServicePassword()));
			}
			//jerseyClient.addFilter(new LoggingFilter(System.out));
			mapper = new ObjectMapper();
			// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			// Mecanisme de deserialització dels enums
			mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
			// Per a no serialitzar propietats amb valors NULL
			mapper.setSerializationInclusion(Include.NON_NULL);
			// No falla si hi ha propietats que no estan definides a l'objecte destí
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		}
		return jerseyClient;
	}

	private ProcedimientosResponse findProcedimentsRolsac(
			String url,
			String body) throws UniformInterfaceException, ClientHandlerException, IOException {
		logger.debug("Enviant petició a ROLSAC (" +
				"url=" + url + ", " +
				"tipus=application/json, " +
				"body=" + body + ")");
		ClientResponse response = getJerseyClient().
				resource(url).
				accept("application/json").
				type("application/json").
				post(ClientResponse.class, body);
		String json = response.getEntity(String.class);
		
		System.out.println("Response find procediment rolsac: " + json);
		return mapper.readValue(
				json,
				TypeFactory.defaultInstance().constructType(ProcedimientosResponse.class));
	}

	private String getServiceUrl() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.ROLSAC_PLUGIN_URL));
	}
	private String getServiceUsername() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.ROLSAC_PLUGIN_USR));
	}
	private String getServicePassword() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.ROLSAC_PLUGIN_PAS));
	}
	private Integer getServiceTimeout() {
		String key = PropertyConfig.getPropertySuffix(PropertyConfig.ROLSAC_PLUGIN_TIMEOUT);
		if (getProperty(key) != null) {
			return getAsInt(key);
		} else {
			return null;
		}
	}
	@Override
	public String getEndpointURL() {
		String endpoint = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.ROLSAC_PLUGIN_ENDPOINT));
		if (Utils.isEmpty(endpoint)) {
			endpoint = getServiceUrl();
		}
		return endpoint;
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcedimentPluginRolsac.class);

}