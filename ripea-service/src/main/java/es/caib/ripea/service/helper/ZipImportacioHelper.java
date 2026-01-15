package es.caib.ripea.service.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.ProgresProcessamentZipDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ZipImportacioHelper {

    private static final String NTI_VERSION = "1.0";
    private static final String NTI_TIPO_DOCUMENTAL = "TD99";
    private static final DocumentNtiEstadoElaboracionEnumDto NTI_ESTADO_ELABORACION = DocumentNtiEstadoElaboracionEnumDto.EE99;
    private static final NtiOrigenEnumDto NTI_ORIGEN = NtiOrigenEnumDto.O0;

    private final Map<Long, ProgresProcessamentZipDto> mapProgres = new HashMap<>();
    private final AtomicBoolean cancelat = new AtomicBoolean(false);

    private final DocumentHelper documentHelper;
    
    public ZipImportacioHelper(DocumentHelper documentHelper) {
        this.documentHelper = documentHelper;
    }

    public void processarZip(
    		UsuariDto usuari, 
    		EntitatDto entitat, 
    		Path tempZip,
    		String rolActual, 
    		Long pareId, 
    		Long tascaId) {
        try {
            inicialitzarContext(usuari, entitat);

//            byte[] zipBytes = zipInputStream.readAllBytes();
//            int totalEntradas = comptarEntradasZip(new ByteArrayInputStream(zipBytes));
//
            ProgresProcessamentZipDto progres = inicialitzarProgres(pareId);
//            progres.setNumOperacions(totalEntradas);
			try (InputStream in = Files.newInputStream(tempZip);
					ZipArchiveInputStream zis = new ZipArchiveInputStream(in, StandardCharsets.UTF_8.name(), true)) {

				int total = 0;
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					if (!entry.isDirectory())
						total++;
				}
				progres.setNumOperacions(total);
			}

			try (InputStream in = Files.newInputStream(tempZip);
					ZipArchiveInputStream zis = new ZipArchiveInputStream(in, StandardCharsets.UTF_8.name(), true)) {

				processarEntradesZip(
						zis, 
						entitat.getId(), 
						pareId, 
						rolActual);
			}

        } catch (Exception e) {
            log.error("Error general processant el fitxer ZIP", e);
            ProgresProcessamentZipDto progres = mapProgres.get(pareId);
            if (progres != null) {
                progres.setError(true);
                progres.setErrorMsg("Error general processant el fitxer ZIP: " + e.getMessage());
            }
        }
    }

    public void cancelarProcessamentZip(Long pareId) {
        cancelat.set(true);
        this.mapProgres.remove(pareId);
    }

    public ProgresProcessamentZipDto inicialitzarProgres(Long pareId) {
    	cancelat.set(false);
        ProgresProcessamentZipDto progres = new ProgresProcessamentZipDto();
        mapProgres.put(pareId, progres);
        return progres;
    }

    public ProgresProcessamentZipDto obtenirProgresActual(Long pareId) {
        return mapProgres.get(pareId);
    }
    
    private void inicialitzarContext(UsuariDto usuari, EntitatDto entitat) {
        createAuthenticationContext(usuari);
        ConfigHelper.setEntitat(entitat);
    }

    
    /** Mètodes privats **/
    
    private int comptarEntradasZip(InputStream zipInputStream) throws IOException {
        int total = 0;
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(zipInputStream, StandardCharsets.UTF_8.name(), true)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) total++;
            }
        }
        return total;
    }

    // Processa cada entrada del ZIP en streaming
    private void processarEntradesZip(
            ZipArchiveInputStream zis,
            Long entitatId,
            Long pareId,
            String rolActual) throws IOException {

        Map<String, List<String>> ubicacioDocuments = new HashMap<>();
        ProgresProcessamentZipDto progres = mapProgres.get(pareId);

        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null && !cancelat.get()) {

            if (entry.isDirectory()) continue;

            String rutaCompleta = entry.getName();
            registrarUbicacio(rutaCompleta, ubicacioDocuments);

            try {
                progres.addInfo("Processant: " + rutaCompleta);

                byte[] contingut = zis.readAllBytes();
                DocumentDto document = crearDocumentDto(rutaCompleta, contingut);

                documentHelper.processarDocumentNewTransaction(
                        ubicacioDocuments,
                        progres,
                        entitatId,
                        document,
                        pareId,
                        rolActual
                );
            } catch (Exception ex) {
                progres.setError(true);
                progres.setErrorMsg("Error processant: " + rutaCompleta);
                log.error("Error procesant {}", rutaCompleta, ex);
            } finally {
                progres.incrementOperacionsRealitzades();
            }
        }
    }


    private void procesarEntradaZip(
    		InputStream zis, 
    		String rutaCompleta, 
    		Map<String, List<String>> ubicacioDocuments,
    		Long entitatId, 
    		Long pareId, 
    		String rolActual) {
    	ProgresProcessamentZipDto progres = mapProgres.get(pareId);
    	
        try {
            progres.addInfo("Processant: " + rutaCompleta);

            byte[] contingut = zis.readAllBytes();
            DocumentDto document = crearDocumentDto(rutaCompleta, contingut);

            documentHelper.processarDocumentNewTransaction(
            		ubicacioDocuments, 
            		progres, 
            		entitatId, 
            		document, 
            		pareId, 
            		rolActual);

        } catch (Exception ex) {
            progres.setError(true);
            progres.setErrorMsg("Error procesando: " + rutaCompleta);
            log.error("Error procesando {}", rutaCompleta, ex);
        } finally {
            progres.incrementOperacionsRealitzades();
        }
    }

    private DocumentDto crearDocumentDto(String rutaCompleta, byte[] contingut) {
        String fitxerNom = Paths.get(rutaCompleta).getFileName().toString();
        String nom = FilenameUtils.removeExtension(fitxerNom);

        DocumentDto documentDto = new DocumentDto();
        documentDto.setRutaZip(rutaCompleta);
        documentDto.setNom(nom);
        documentDto.setFitxerNom(fitxerNom);
        documentDto.setFitxerContingut(contingut);
        documentDto.setFitxerTamany((long) contingut.length);
        documentDto.setFitxerContentType(MimeTypeUtils.getMimeType(fitxerNom));
        documentDto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
        documentDto.setNtiVersion(NTI_VERSION);
        documentDto.setDataCaptura(new Date());
        documentDto.setData(new Date());
        documentDto.setNtiOrigen(NTI_ORIGEN);
        documentDto.setNtiEstadoElaboracion(NTI_ESTADO_ELABORACION);
        documentDto.setNtiTipoDocumental(NTI_TIPO_DOCUMENTAL);

        return documentDto;
    }

    private void registrarUbicacio(String rutaCompleta, Map<String, List<String>> ubicacioDocuments) {
        var path = Paths.get(rutaCompleta).getParent();
        if (path == null) return;

        List<String> ubicacio = new ArrayList<>();
        path.forEach(p -> ubicacio.add(p.toString()));
        ubicacioDocuments.put(rutaCompleta, ubicacio);
    }

    private void createAuthenticationContext(UsuariDto usuariActual) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) return;

        List<SimpleGrantedAuthority> authorities = Arrays.stream(usuariActual.getRols())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User user = new User(usuariActual.getCodi(), "", authorities);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
