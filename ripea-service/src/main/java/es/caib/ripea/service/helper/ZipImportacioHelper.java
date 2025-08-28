package es.caib.ripea.service.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.dto.CarpetaDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusFirmaEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.ProgresProcessamentZipDto;
import es.caib.ripea.service.intf.dto.SignatureInfoDto;
import es.caib.ripea.service.intf.exception.ElementNotValidException;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.service.CarpetaService;
import es.caib.ripea.service.intf.service.DocumentService;
import es.caib.ripea.service.intf.service.MetaDocumentService;
import lombok.extern.slf4j.Slf4j;


/**
 * Processa un ZIP amb documents i un CSV descriptor,
 * Crea la jerarquia de carpetes i valida els documents.
 *
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ZipImportacioHelper {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private MetaDocumentService metaDocumentService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private CarpetaService carpetaService;
    @Autowired
    private CarpetaHelper carpetaHelper;
    @Autowired
    private EntityComprovarHelper entityComprovarHelper;
    
    @Autowired
    private javax.validation.Validator validator;
    
    private Map<String, Long> carpetesCreades = new HashMap<String, Long>(); // Ruta de cada fitxer
    private Map<String, List<String>> ubicacioDocuments = new HashMap<String, List<String>>(); // Ruta de cada fitxer
    private Map<String, byte[]> mapDocuments = new HashMap<String, byte[]>(); // Relació nomFitxer - contingut
    private final Map<Long, ProgresProcessamentZipDto> mapProgres = new HashMap<>();

    public List<DocumentDto> extreureDocuments(
    		InputStream zip, 
    		Long metaExpedientId, 
    		Long pareId,
    		Long entitatId) 
            throws IOException {
        ProgresProcessamentZipDto progres = incialitzarProgres(pareId);
    	
    	netejarLlistes();
        
        ZipContent zipContent = extreureContingutZip(zip);
        
        if (zipContent.getCsvContingut() == null) {
            throw new IllegalStateException("No s'ha trobat cap arxiu CSV al ZIP");
        }
        
        int totalDocuments = zipContent.getMapDocuments().size();
        progres.setNumOperacions(totalDocuments);
        
        this.mapDocuments.putAll(zipContent.getMapDocuments());

        return processarCSV(
                zipContent.getCsvContingut(), 
                metaExpedientId, 
                pareId, 
                entitatId);
    }

    public void assignarCarpeta(DocumentDto documentDto, Long entitatId, Long pareId) {
    	String nomFitxer = documentDto.getFitxerNom();
    	List<String> ubicacio = ubicacioDocuments.get(nomFitxer);
        
        if (ubicacio != null) {
            ContingutEntity pare = entityComprovarHelper.comprovarContingut(pareId);
            
        	Long carpetaId = crearCarpetaRecursiva(entitatId, pare, pareId, nomFitxer, ubicacio.iterator());
        	documentDto.setPareId(carpetaId);
        } else {
        	documentDto.setPareId(pareId);
        }
    }

	private ProgresProcessamentZipDto incialitzarProgres(Long pareId) {
    	log.debug("Inicialitzant el progrés d'importació de documents");
    	
    	ProgresProcessamentZipDto progres = new ProgresProcessamentZipDto();
        mapProgres.put(pareId, progres);
        return progres;
    }

    private ZipContent extreureContingutZip(InputStream zipInputStream) throws IOException {
        ZipContent zipContent = new ZipContent();
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                Path path = Paths.get(entry.getName()).getParent();
                String nomFitxer = Paths.get(entry.getName()).getFileName().toString().toLowerCase();
                if (path != null) {
                	List<String> ubicacio = new ArrayList<>();
                    path.forEach(p -> ubicacio.add(p.toString()));
                    ubicacioDocuments.put(nomFitxer, ubicacio);
                }
                
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
    	log.debug("Processant files CSV i creant documentCommand");
    	
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
    		Long expedientId, 
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
        documentDto.setValidacioFirmaCorrecte(true);

        if (!firmat) {
        	documentDto.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.SENSE_FIRMA);
        	documentDto.setAmbFirma(false);
        }
        
        if (firmat) {
        	log.debug("Validant la firma del fitxer {} del CSV", nomFitxer);
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
    
    private Long crearCarpetaRecursiva(Long entitatId, ContingutEntity pare, Long pareId, String nomFitxer, Iterator<String> ubicacio) {
        if (!ubicacio.hasNext()) {
            return pareId; // no hi ha més carpetes
        }
        String nomCarpeta = ubicacio.next();
        
        // Crea o utilitza una carpeta existent
        CarpetaEntity carpeta = carpetaHelper.comprovarCarpetaExpedient(nomCarpeta, pare);
        Long carpetaId = carpeta != null ? carpeta.getId() : crearCarpeta(entitatId, pareId, nomFitxer, nomCarpeta);
        
        return crearCarpetaRecursiva(entitatId, pare, carpetaId, nomFitxer, ubicacio); // llamada recursiva
    }
    
    private Long crearCarpeta(Long entitatId, Long pareId, String nomFitxer, String nomCarpeta) {
    	log.info("Creant la carpeta {} pel document {}", nomCarpeta, nomFitxer);
    	CarpetaDto carpeta = null;
    	try {
    		carpeta = carpetaService.create(
    			entitatId,
    			pareId,
    			nomCarpeta);
    		
    		carpetesCreades.put(nomFitxer, carpeta.getId());
    	} catch (Exception e) {
    		log.error("Error creant la carpeta " + nomCarpeta, e);
    	    throw e;
		}
    	return carpeta != null ? carpeta.getId() : null;
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
			log.warn("No s'ha definit la columna: " + columna);
		}
		return null;
    }

    private void netejarLlistes() {
        mapDocuments.clear();
        ubicacioDocuments.clear();
        carpetesCreades.clear();
	}
    
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
    
}
