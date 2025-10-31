package es.caib.ripea.service.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusFirmaEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.ProgresProcessamentZipDto;
import es.caib.ripea.service.intf.dto.SignatureInfoDto;
import es.caib.ripea.service.intf.service.CarpetaService;
import es.caib.ripea.service.intf.service.DocumentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ZipImportacioHelper {

	private final static String NTI_VERSION = "1.0";
	private final static String NTI_TIPO_DOCUMENTAL = "TD99"; // Altres
	private final static DocumentNtiEstadoElaboracionEnumDto NTI_ESTADO_ELABORACION = DocumentNtiEstadoElaboracionEnumDto.EE99; // Altres
	private final static NtiOrigenEnumDto NTI_ORIGEN = NtiOrigenEnumDto.O0; // Ciutadà
	
    @Autowired
    private DocumentService documentService;
    @Autowired
    private CarpetaService carpetaService;
    @Autowired
    private CarpetaHelper carpetaHelper;
    @Autowired
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
	private MessageHelper messageHelper;
    
    private final Map<String, List<String>> ubicacioDocuments = new HashMap<>();
    private final Map<Long, ProgresProcessamentZipDto> mapProgres = new HashMap<>();

    public int descomprimirZip(
    		InputStream zip, 
    		String rolActual, 
    		Long pareId,
    		Long tascaId,
    		Long entitatId) throws IOException {
    	
        var progres = inicialitzarProgres(pareId);
        var mapDocuments = llegirZip(zip);

        progres.setNumOperacions(mapDocuments.size());

        var documents = convertirDocumentDto(mapDocuments);

        for (var documentDto : documents) {
            assignarCarpeta(
            		documentDto, 
            		entitatId, 
            		pareId);

            progres.addInfo(messageHelper.getMessage("contingut.boto.crear.document.multiple.proces", new Object[]{documentDto.getFitxerNom()}));
            
            documentService.create(entitatId,
                                   documentDto.getPareId(),
                                   documentDto,
                                   false,
                                   rolActual,
                                   tascaId,
                                   false);
            progres.incrementOperacionsRealitzades();
        }
        return progres.getNumOperacions();
    }

    public ProgresProcessamentZipDto obtenirProgresActual(Long pareId) {
        return mapProgres.get(pareId);
    }

    private void assignarCarpeta(DocumentDto documentDto, Long entitatId, Long pareId) {
        var nomFitxer = documentDto.getFitxerNom();
        var ubicacio = ubicacioDocuments.get(documentDto.getRutaZip());

        if (ubicacio != null) {
            var pare = entityComprovarHelper.comprovarContingut(pareId);
            var carpetaId = crearCarpetaRecursiu(
            		entitatId, 
            		pare, 
            		pareId, 
            		nomFitxer, 
            		ubicacio.iterator());
            documentDto.setPareId(carpetaId);
        } else {
            documentDto.setPareId(pareId);
        }
    }

    private Map<String, byte[]> llegirZip(InputStream zipInputStream) throws IOException {
        Map<String, byte[]> mapDocuments = new HashMap<>();

        try (var zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                var path = Paths.get(entry.getName()).getParent();
                var rutaCompleta = entry.getName();
                
                if (path != null) {
                    var ubicacio = new ArrayList<String>();
                    path.forEach(p -> ubicacio.add(p.toString()));
                    ubicacioDocuments.put(rutaCompleta, ubicacio);
                }

                mapDocuments.put(rutaCompleta, llegirBytes(zis));
                zis.closeEntry();
            }
        }

        if (mapDocuments.isEmpty()) {
            throw new IllegalStateException("No s'ha trobat cap arxiu al ZIP");
        }
        return mapDocuments;
    }

    private ProgresProcessamentZipDto inicialitzarProgres(Long pareId) {
        log.debug("Inicialitzant el progrés d'importació de documents");
        var progres = new ProgresProcessamentZipDto();
        mapProgres.put(pareId, progres);
        return progres;
    }

    private byte[] llegirBytes(InputStream inputStream) throws IOException {
        try (var outputStream = new ByteArrayOutputStream()) {
            var buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        }
    }

    private List<DocumentDto> convertirDocumentDto(Map<String, byte[]> mapDocuments) {
        log.debug("Processant documents del ZIP");

        var documents = mapDocuments.entrySet().stream()
                .map(entry -> nouDocument(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return documents;
    }

    private DocumentDto nouDocument(String rutaCompleta, byte[] contingut) {
        var documentDto = new DocumentDto();
        String fitxerNom = Paths.get(rutaCompleta).getFileName().toString();
        String nom = FilenameUtils.removeExtension(fitxerNom);
        String mimeType = MimeTypeUtils.getMimeType(fitxerNom);
        
        documentDto.setRutaZip(rutaCompleta);
        documentDto.setNom(nom);
        documentDto.setFitxerNom(fitxerNom);
        documentDto.setFitxerContingut(contingut);
        documentDto.setFitxerTamany((long) contingut.length);
        documentDto.setFitxerContentType(mimeType);
        documentDto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
        documentDto.setNtiVersion(NTI_VERSION);
        documentDto.setDataCaptura(new Date());
        documentDto.setData(new Date());
        documentDto.setNtiOrigen(NTI_ORIGEN);
        documentDto.setNtiEstadoElaboracion(NTI_ESTADO_ELABORACION);
        documentDto.setNtiTipoDocumental(NTI_TIPO_DOCUMENTAL);
        
        validarFirmes(mimeType, contingut, documentDto);
        
        return documentDto;
    }

	private void validarFirmes(String contentType, byte[] contingut, DocumentDto documentDto) {
		SignatureInfoDto signatureInfo = documentService.checkIfSignedAttached(contingut, contentType);
		
		if (signatureInfo.isSigned()) {
			documentDto.setAmbFirma(!signatureInfo.isError());
			documentDto.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);
			documentDto.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
			documentDto.setValidacioFirmaCorrecte(!signatureInfo.isError());
			documentDto.setValidacioFirmaErrorMsg(signatureInfo.getErrorMsg());
		} else {
			documentDto.setAmbFirma(false);
			documentDto.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.SENSE_FIRMA);
		}
	}
    
    private Long crearCarpetaRecursiu(
    		Long entitatId, 
    		ContingutEntity pare,
    		Long pareId, 
    		String nomFitxer,
    		Iterator<String> ubicacio) {
        if (!ubicacio.hasNext()) return pareId;

        var nomCarpeta = ubicacio.next();
        var carpeta = carpetaHelper.comprovarCarpetaExpedient(nomCarpeta, pare);
        var carpetaId = carpeta != null
                ? carpeta.getId()
                : crearCarpeta(entitatId, pareId, nomFitxer, nomCarpeta);

        return crearCarpetaRecursiu(entitatId, carpeta, carpetaId, nomFitxer, ubicacio);
    }

    private Long crearCarpeta(Long entitatId, Long pareId, String nomFitxer, String nomCarpeta) {
        log.info("Creant la carpeta {} pel document {}", nomCarpeta, nomFitxer);
        var carpeta = carpetaService.create(entitatId, pareId, nomCarpeta);
        return carpeta != null ? carpeta.getId() : null;
    }

}
