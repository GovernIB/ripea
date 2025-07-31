package es.caib.ripea.service.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusFirmaEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.ProgresProcessamentZipDto;
import es.caib.ripea.service.intf.dto.SignatureInfoDto;
import es.caib.ripea.service.intf.exception.ElementNotValidException;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.service.DocumentService;
import es.caib.ripea.service.intf.service.MetaDocumentService;


/**
 * Helper per processar el fitxer zip en la importació de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ZipImportacioHelper {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private MetaDocumentService metaDocumentService;    
    @Autowired
    private DocumentService documentService;
    @Autowired
    private javax.validation.Validator validator;
    
    private Map<String, byte[]> mapDocuments = new HashMap<>();
    private final Map<Long, ProgresProcessamentZipDto> mapProgres = new HashMap<>();

    public List<DocumentDto> extreureDocuments(
    		InputStream zip, 
    		Long metaExpedientId, 
    		Long pareId, EntitatDto entitat) 
            throws IOException {
        mapDocuments.clear();
        
        ProgresProcessamentZipDto progres = incialitzarProgres(pareId);
       
        ZipContent zipContent = parseZip(zip);

        int totalDocuments = zipContent.getMapDocuments().size();
        progres.setNumOperacions(totalDocuments);
        
        if (zipContent.getCsvContingut() == null) {
            throw new RuntimeException("No s'ha trobat cap arxiu CSV al ZIP");
        }
        
        this.mapDocuments = zipContent.getMapDocuments();

        return processarCSV(
                zipContent.getCsvContingut(), 
                metaExpedientId, 
                pareId, 
                entitat.getId());
    }

    private ProgresProcessamentZipDto incialitzarProgres(Long pareId) {
    	logger.debug("Inicialitzant el progrés d'importació de documents");
    	
    	ProgresProcessamentZipDto progres = new ProgresProcessamentZipDto();
        mapProgres.put(pareId, progres);
        return progres;
    }

    private ZipContent parseZip(InputStream zipInputStream) throws IOException {
        ZipContent zipContent = new ZipContent();
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                String nomFitxer = Paths.get(entry.getName()).getFileName().toString().toLowerCase();
                if (nomFitxer.endsWith(".csv")) {
                    zipContent.setCsvContingut(llegirFitxerCSV(zis));
                } else {
                    zipContent.putFile(nomFitxer, llegirFitxerAdjunt(zis));
                }
                zis.closeEntry();
            }
        }
        return zipContent;
    }
    
    private String llegirFitxerCSV(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toString("UTF-8");
    }

    private byte[] llegirFitxerAdjunt(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }

    private List<DocumentDto> processarCSV(
    		String contingutCSV, 
    		Long metaExpedientId, 
    		Long pareId, 
    		Long entitatId) 
            throws IOException {
    	logger.debug("Processant files CSV i creant documentCommand");
    	
        List<DocumentDto> documents = new ArrayList<DocumentDto>();
        ProgresProcessamentZipDto progres = mapProgres.get(pareId);

        Reader reader = null;
        CSVParser csvParser = null;

        try {
            reader = new StringReader(contingutCSV);
            csvParser = new CSVParser(reader, CSVFormat.EXCEL.withFirstRecordAsHeader());

            for (CSVRecord record : csvParser) {
                progres.addInfo("Processant fila " + record.getRecordNumber());
                DocumentDto documentDto = crearDocumentDto(
                		record, 
                		metaExpedientId, 
                		pareId, 
                		entitatId, 
                		progres);
                documents.add(documentDto);
            }
        } finally {
            if (csvParser != null) {
                csvParser.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        
        validarLlistaDocuments(documents);
        
        return documents;
    }
    
	private void validarLlistaDocuments(List<DocumentDto> documents) {
		for (DocumentDto documentDto : documents) {
			DocumentResource documentResource = conversioTipusHelper.convertir(
					documentDto, 
					DocumentResource.class);
			
			documentResource.setHasFirma(documentDto.isAmbFirma());
			documentResource.setAdjunt(new FileReference(
					documentDto.getFitxerNom(),
					documentDto.getFitxerContingut(),
					documentDto.getFitxerContentType(),
					documentDto.getFitxerTamany()
	        ));
			
			Set<ConstraintViolation<DocumentResource>> violations = validator.validate(
					documentResource, 
					Default.class, Resource.OnCreate.class,  Resource.OnUpdate.class);

	        if (!violations.isEmpty()) {
	    		StringBuilder validacions = new StringBuilder();
	            validacions.append("<ul>");
	            for (ConstraintViolation<DocumentResource> v : violations) {
	            	String camp = v.getPropertyPath().toString();
	            	
	            	if (! camp.isBlank())
	            		validacions.append("<li>").append(camp).append(": ").append(v.getMessage()).append("</li>");
	            	else
	            		validacions.append("<li>").append(v.getMessage()).append("</li>");
	            }
	            validacions.append("</ul>");
	            
	            throw new ElementNotValidException(validacions.toString());
	        }
		}
	}

    private DocumentDto crearDocumentDto(
    		CSVRecord record, 
    		Long metaExpedientId, 
    		Long pareId, 
    		Long entitatId,
    		ProgresProcessamentZipDto progres) {
        String tipusDocumentCodi = obtenirValorFila(record, "Tipo de documento ENI", true);
        String nomFitxer = obtenirValorFila(record, "Nombre del fichero", true);
        String nomDocument = obtenirValorFila(record, "Nombre del documento", true);
        String descripcio = obtenirValorFila(record, "Descripción del documento", false);
        String campFirmat = obtenirValorFila(record, "Estado firma", false);
        boolean firmat = "firmado".equalsIgnoreCase(campFirmat) || "firmat".equalsIgnoreCase(campFirmat);

        if (tipusDocumentCodi == null || nomFitxer == null || nomDocument == null)
            throw new RuntimeException("Falten camps obligatoris a la fila CSV: " + record.getRecordNumber());
        
        byte[] fitxerContingut = mapDocuments.get(nomFitxer);
        String fitxerContentType = new MimetypesFileTypeMap().getContentType(nomFitxer);
        
        if (fitxerContingut == null)
            throw new RuntimeException("Fitxer no trobat al ZIP: " + nomFitxer);
        
        if (fitxerContentType == null)
            throw new RuntimeException("No s'ha pogut obtenir l'extensió del fitxer: " + nomFitxer);

        MetaDocumentDto metaDocument = metaDocumentService.findByCodi(entitatId, metaExpedientId, tipusDocumentCodi);

        if (metaDocument == null)
        	throw new RuntimeException("No s'ha trobat cap tipus de document amb el codi " + tipusDocumentCodi);
        	
        DocumentDto documentDto = new DocumentDto();
        documentDto.setMetaNode(metaDocument);
        documentDto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
        documentDto.setNom(nomDocument);
        documentDto.setNtiVersion("1.0");
        //        documentCommand.setOrigen(DocumentCommand.DocumentFisicOrigenEnum.DISC);
        documentDto.setFitxerNom(nomFitxer);
        documentDto.setFitxerContentType(fitxerContentType);
        documentDto.setFitxerContingut(fitxerContingut);
        documentDto.setFitxerTamany((long) fitxerContingut.length);
        documentDto.setDescripcio(descripcio);
        documentDto.setDataCaptura(new Date());
        documentDto.setData(new Date());
        documentDto.setNtiEstadoElaboracion(metaDocument.getNtiEstadoElaboracion());
        documentDto.setNtiOrigen(metaDocument.getNtiOrigen());
        documentDto.setPareId(pareId);
        documentDto.setValidacioFirmaCorrecte(true);

        if (!firmat) {
        	documentDto.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.SENSE_FIRMA);
        	documentDto.setAmbFirma(false);
        }
        
        if (firmat) {
        	logger.debug("Validant la firma del fitxer {} del CSV", nomFitxer);
            SignatureInfoDto signatureInfo = documentService.checkIfSignedAttached(
            		fitxerContingut, 
            		fitxerContentType);
            documentDto.setAmbFirma(!signatureInfo.isError());
            documentDto.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);
            documentDto.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
            documentDto.setValidacioFirmaCorrecte(!signatureInfo.isError());
            documentDto.setValidacioFirmaErrorMsg(signatureInfo.getErrorMsg());
        }

        progres.incrementOperacionsRealitzades();
        
        return documentDto;
    }

    public byte[] obtenirContingutFitxer(String nomFitxer) {
        return mapDocuments.get(nomFitxer);
    }

    public ProgresProcessamentZipDto obtenirProgresActual(Long pareId) {
        return mapProgres.get(pareId);
    }
    
    private String obtenirValorFila(
    		CSVRecord record, 
    		String columna, 
    		boolean throwExcepcio) {
    	try {
			return record.get(columna);
		} catch (IllegalArgumentException e) {
			if (throwExcepcio)
				throw e;
			logger.warn("No s'ha definit la columna: " + columna);
		}
		return null;
    }
    
//    private static Date convertToDate(LocalDateTime dateToConvert) throws ParseException {
//		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(date);
//	}

    class ZipContent {
        private Map<String, byte[]> mapDocuments = new HashMap<>();
        private String csvContingut;

        public Map<String, byte[]> getMapDocuments() {
            return mapDocuments;
        }

        public void putFile(String name, byte[] content) {
            mapDocuments.put(name, content);
        }

		public String getCsvContingut() {
			return csvContingut;
		}

		public void setCsvContingut(String csvContingut) {
			this.csvContingut = csvContingut;
		}
    }

    
    private static final Logger logger = LoggerFactory.getLogger(ZipImportacioHelper.class);
    
}
