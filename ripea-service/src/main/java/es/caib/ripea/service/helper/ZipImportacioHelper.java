package es.caib.ripea.service.helper;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

    @Autowired private DocumentHelper documentHelper;
    @Autowired private MessageHelper messageHelper;

    @Async
    public void processarZip(
    		UsuariDto usuari, 
    		EntitatDto entitat, 
    		Path tempZip,
    		String rolActual, 
    		Long pareId, 
    		Long tascaId) {
        try {
            inicialitzarContext(usuari, entitat);

            inicialitzarProgres(pareId);
            
            comptarEntradesZip(
            		tempZip, 
            		pareId);

			processarEntradesZip(
					tempZip, 
					entitat.getId(), 
					pareId, 
					rolActual);
        } catch (Exception ex) {
            log.error("Error general processant el fitxer ZIP", ex);
            ProgresProcessamentZipDto progres = mapProgres.get(pareId);
            if (progres != null) {
                progres.setError(true);
                progres.setErrorMsg(messageHelper.getMessage("contingut.boto.crear.document.multiple.error", new Object[] {ex.getMessage()}));
            }
        } finally {
            try {
                Files.deleteIfExists(tempZip);
                log.debug("ZIP temporal eliminat: {}", tempZip);
            } catch (IOException e) {
                log.warn("No s'ha pogut eliminar el ZIP temporal {}", tempZip, e);
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
    
    public void setNumOperacionsProgres(Long pareId, int totalOperacions) {
    	mapProgres.get(pareId).setNumOperacions(totalOperacions);
    }
    
    private void comptarEntradesZip(Path tempZip, Long pareId) throws IOException {
    	ProgresProcessamentZipDto progres = mapProgres.get(pareId);
    	
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
    }

    public void processarEntradaZip(
    		Long entitatId,
    		Long pareId,
    		Map<String, List<String>> ubicacioDocuments,
    		ProgresProcessamentZipDto progres,
    		String rutaCompleta,
    		byte[] contingut,
    		String rolActual) {
    	
        progres.addInfo(
        		messageHelper.getMessage("contingut.boto.crear.document.multiple.processant",
        		new Object[] {rutaCompleta}));

        DocumentDto document = crearDocumentDto(rutaCompleta, contingut);

//        documentHelper.processarDocumentNewTransaction(
//                ubicacioDocuments,
//                progres,
//                entitatId,
//                document,
//                pareId,
//                rolActual);
		
		progres.addDocumentCorrecte(document.getFitxerTamany());
    }
    
    private void processarEntradesZip(
    		Path tempZip,
    		Long entitatId, 
    		Long pareId, 
    		String rolActual) throws IOException {
    	try (InputStream in = Files.newInputStream(tempZip);
				ZipArchiveInputStream zis = new ZipArchiveInputStream(in, StandardCharsets.UTF_8.name(), true)) {

    		Map<String, List<String>> ubicacioDocuments = new HashMap<>();
            ProgresProcessamentZipDto progres = mapProgres.get(pareId);

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null && !cancelat.get()) {

                if (entry.isDirectory()) continue;

                String rutaCompleta = entry.getName();
                registrarUbicacio(rutaCompleta, ubicacioDocuments);

                try {
                	processarEntradaZip(entitatId, pareId, ubicacioDocuments, progres, rutaCompleta, zis.readAllBytes(), rolActual);                    
                } catch (Exception ex) {
                    log.error("Error procesant la següent entrada del fitxer ZIP {}", rutaCompleta, ex);
					progres.addError(
							messageHelper.getMessage("contingut.boto.crear.document.multiple.entrada.error",
							new Object[] { rutaCompleta, ex.getMessage() }));
                } finally {
                    progres.incrementOperacionsRealitzades();
                }
            }
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
    
    private void inicialitzarContext(UsuariDto usuari, EntitatDto entitat) {
        createAuthenticationContext(usuari);
        ConfigHelper.setEntitat(entitat);
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
