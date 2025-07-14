package es.caib.ripea.back.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import es.caib.ripea.back.command.DocumentCommand;
import es.caib.ripea.back.command.ProgresProcessamentZipCommand;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.SignatureInfoDto;
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
    private MetaDocumentService metaDocumentService;
    
    @Autowired
    private DocumentService documentService;

    private final Map<String, byte[]> mapDocuments = new HashMap<>();
    private final Map<Long, ProgresProcessamentZipCommand> mapProgres = new HashMap<>();

    public List<DocumentCommand> extreureDocuments(
    		MultipartFile fitxerZip, 
    		Long metaExpedientId, 
    		Long pareId, EntitatDto entitat) 
            throws IOException {
        mapDocuments.clear();
        
        ProgresProcessamentZipCommand progres = incialitzarProgres(pareId);
       
        String contingutCSV = llegirCSVIMapejarDocuments(
        		fitxerZip, 
        		progres);

        inicialitzarDocumentsPendents(
        		fitxerZip, 
        		progres);

        return (contingutCSV != null) ?
	            	processarCSV(
	            		contingutCSV, 
	            		metaExpedientId, 
	            		pareId, 
	            		entitat.getId()) : new ArrayList<DocumentCommand>();
    }

    private ProgresProcessamentZipCommand incialitzarProgres(Long pareId) {
    	logger.debug("Inicialitzant el progrés d'importació de documents");
    	
        ProgresProcessamentZipCommand progres = new ProgresProcessamentZipCommand();
        mapProgres.put(pareId, progres);
        return progres;
    }

    private String llegirCSVIMapejarDocuments(MultipartFile fitxerZip, ProgresProcessamentZipCommand progres) throws IOException {
    	logger.debug("Llegint CSV i relacionant fitxer CSV amb fitxer dins del ZIP");
    	
        String contingutFitxerCSV = null;
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(fitxerZip.getInputStream());
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                String nomFitxer = Paths.get(entry.getName()).getFileName().toString();
                
                if (nomFitxer.toLowerCase().endsWith(".csv")) {
                	contingutFitxerCSV = llegirFitxerCSV(zis);
                } else {
                    mapDocuments.put(nomFitxer, llegirFitxerAdjunt(zis));
                }
                zis.closeEntry();
            }
        } finally {
            if (zis != null) {
                zis.close();
            }
        }
        
        return contingutFitxerCSV;
    }

    private void inicialitzarDocumentsPendents(MultipartFile arxiuZip, ProgresProcessamentZipCommand progres) throws IOException {
    	logger.debug("Comptant els total de documents per processar");
    	
        int totalDocuments = 0;
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(arxiuZip.getInputStream());
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && !entry.getName().toLowerCase().endsWith(".csv")) {
                    totalDocuments++;
                }
            }
        } finally {
            if (zis != null) {
                zis.close();
            }
        }
        
        progres.setNumOperacions(totalDocuments);
        
        if (totalDocuments != mapDocuments.size()) {
            progres.addInfo("Error processant els documents...");
        	throw new RuntimeException("El total de fitxers no conincideix amb el CSV");
        }
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

    private List<DocumentCommand> processarCSV(
    		String contingutCSV, 
    		Long metaExpedientId, 
    		Long pareId, 
    		Long entitatId) 
            throws IOException {
    	logger.debug("Processant files CSV i creant documentCommand");
    	
        List<DocumentCommand> documents = new ArrayList<>();
        ProgresProcessamentZipCommand progres = mapProgres.get(pareId);

        Reader reader = null;
        CSVParser csvParser = null;

        try {
            reader = new StringReader(contingutCSV);
            csvParser = new CSVParser(reader, CSVFormat.EXCEL.withFirstRecordAsHeader());

            for (CSVRecord record : csvParser) {
                progres.addInfo("Processant fila " + record.getRecordNumber());
                DocumentCommand documentCommand = crearDocumentCommand(
                		record, 
                		metaExpedientId, 
                		pareId, 
                		entitatId, 
                		progres);
                documents.add(documentCommand);
            }
        } finally {
            if (csvParser != null) {
                csvParser.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return documents;
    }

    private DocumentCommand crearDocumentCommand(
    		CSVRecord record, 
    		Long metaExpedientId, 
    		Long pareId, 
    		Long entitatId,
    		ProgresProcessamentZipCommand progres) {
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
        	
        DocumentCommand documentCommand = new DocumentCommand();
        documentCommand.setMetaNodeId(metaDocument.getId());
        documentCommand.setNom(nomDocument);
        documentCommand.setOrigen(DocumentCommand.DocumentFisicOrigenEnum.DISC);
        documentCommand.setFitxerNom(nomFitxer);
        documentCommand.setFitxerContentType(fitxerContentType);
        documentCommand.setDescripcio(descripcio);
        documentCommand.setFitxerContingut(fitxerContingut);
        documentCommand.setDataTime(new LocalDateTime());
        documentCommand.setNtiEstadoElaboracion(metaDocument.getNtiEstadoElaboracion());
        documentCommand.setNtiOrigen(metaDocument.getNtiOrigen());
        documentCommand.setPareId(pareId);
        documentCommand.setValidacioFirmaCorrecte(true);

        if (firmat) {
        	logger.debug("Validant la firma del fitxer {} del CSV", nomFitxer);
            SignatureInfoDto signatureInfo = documentService.checkIfSignedAttached(
            		fitxerContingut, 
            		fitxerContentType);
            documentCommand.setAmbFirma(!signatureInfo.isError());
            documentCommand.setValidacioFirmaCorrecte(!signatureInfo.isError());
            documentCommand.setValidacioFirmaErrorMsg(signatureInfo.getErrorMsg());
        }

        progres.incrementOperacionsRealitzades();
        
        return documentCommand;
    }

    public byte[] obtenirContingutFitxer(String nomFitxer) {
        return mapDocuments.get(nomFitxer);
    }

    public ProgresProcessamentZipCommand obtenirProgresActual(Long pareId) {
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
    
    private static final Logger logger = LoggerFactory.getLogger(ZipImportacioHelper.class);
    
}
