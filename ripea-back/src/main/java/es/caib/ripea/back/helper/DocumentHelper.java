package es.caib.ripea.back.helper;

import es.caib.ripea.back.command.DocumentGenericCommand;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.ContingutService;
import es.caib.ripea.service.intf.service.DocumentService;
import es.caib.ripea.service.intf.service.MetaDocumentService;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Mètodes d'ajuda per a gestionar els documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentHelper {

	public static final SecureRandom DEFAULT_NUMBER_GENERATOR = new SecureRandom();

    public static final char[] DEFAULT_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static final int DEFAULT_SIZE = 8;
    
	@Autowired
	private MetaDocumentService metaDocumentService;
	
	public DocumentGenericCommand concatenarDocuments(
			Long entitatId,
			DocumentService documentService,
			ContingutService contingutService,
			EntitatDto entitatActual,
			Map<String, Long> ordre) {
		DocumentGenericCommand command = new DocumentGenericCommand();
		MetaDocumentDto metaDocument = metaDocumentService.findByTipusGeneric(
				entitatId, 
				MetaDocumentTipusGenericEnumDto.NOTIFICACION);
		
		FitxerDto fitxer;
		PDDocument resultat = new PDDocument();
		ByteArrayOutputStream resultatOutputStream = new ByteArrayOutputStream();
		PDFMergerUtility PDFmerger = new PDFMergerUtility(); 
		try {
			for (Map.Entry<String, Long> entry : ordre.entrySet()) {
				
				fitxer = documentService.descarregarImprimible(
						entitatActual.getId(),
						entry.getValue(),
						null);
				
				PDDocument document = PDDocument.load(fitxer.getContingut());
				PDFmerger.appendDocument(resultat, document);
				
			}
			
			resultat.save(resultatOutputStream);
			resultat.close();
			
			command.setNom("notificacio_" + new Date().getTime());
			command.setData(new Date());
			command.setMetaNodeId(metaDocument.getId()); //Notificació
			command.setNtiEstadoElaboracion(metaDocument.getNtiEstadoElaboracion());
			command.setNtiIdDocumentoOrigen(metaDocument.getNtiOrigen().name());
			command.setNtiOrigen(metaDocument.getNtiOrigen());
			command.setDocumentTipus(DocumentTipusEnumDto.VIRTUAL);
			command.setFitxerNom(command.getNom() + ".pdf");
			command.setFitxerContentType("application/pdf");
			command.setFitxerContingut(resultatOutputStream.toByteArray());
			

		} catch (Exception ex) {
			LOGGER.error(
					"No s'ha pogut crear el document concatenat a partir del contingut seleccionat",
					ex);
			throw new RuntimeException(ex);
		}
		return command;
	}
	
	public DocumentGenericCommand generarFitxerZip(
			Long entitatId,
			DocumentService documentService,
			ContingutService contingutService,
			EntitatDto entitatActual,
			Set<Long> docsIdx,
			ByteArrayOutputStream baos,
			HttpServletRequest request,
			Long metaDocumentId, 
			Long tascaId,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		
		DocumentGenericCommand command = new DocumentGenericCommand();
		
		MetaDocumentDto metaDocument = null;
		if (metaDocumentId != null) {
			metaDocument = metaDocumentService.findById(metaDocumentId);
		} else {
			metaDocument = metaDocumentService.findByTipusGeneric(
					entitatId, 
					MetaDocumentTipusGenericEnumDto.NOTIFICACION);
		}

		
		byte[] reportContent = null;
		
		if (baos == null)
			baos = new ByteArrayOutputStream();
		
		ZipOutputStream zos = new ZipOutputStream(baos);
		try {
			if (docsIdx != null) {
				for (Long docId: docsIdx) {
					FitxerDto fitxer = null;
					DocumentDto document = documentService.findAmbId(
							docId,
							RolHelper.getRolActual(request),
							PermissionEnumDto.READ, 
							tascaId);
						fitxer = documentService.descarregar(
								entitatActual.getId(),
								docId,
								null, 
								null);
					try {
						ZipEntry entry = new ZipEntry(revisarContingutNom(document.getNom()) + "." + FilenameUtils.getExtension(fitxer.getNom()));
						entry.setSize(fitxer.getContingut().length);
						zos.putNextEntry(entry);
						zos.write(fitxer.getContingut());
						zos.closeEntry();
					} catch (Exception ex) {
						LOGGER.error(
								"No s'ha generar el document a partir del contingut seleccionat",
								ex);
					}
				}
				zos.close();
	
				reportContent = baos.toByteArray();
				command.setNom("notificacio_" + randomUUID());
				command.setData(new Date());
				command.setMetaNodeId(metaDocument.getId()); //Notificació
				command.setNtiEstadoElaboracion(ntiEstadoElaboracion != null ? ntiEstadoElaboracion : metaDocument.getNtiEstadoElaboracion());
				command.setNtiIdDocumentoOrigen(metaDocument.getNtiOrigen().name());
				command.setNtiOrigen(ntiOrigen != null ? ntiOrigen : metaDocument.getNtiOrigen());
				command.setDocumentTipus(metaDocumentId != null ? DocumentTipusEnumDto.DIGITAL : DocumentTipusEnumDto.VIRTUAL);
				command.setFitxerNom(command.getNom() + ".zip");
				command.setFitxerContentType("application/zip");
				command.setFitxerContingut(reportContent);
			}		

		} catch (IOException ex) {
			LOGGER.error(
					"No s'ha generar el fitxer zip a partir del contingut seleccionat",
					ex);
		}
		
		return command;
	}
	
	private static String randomUUID() {
        return randomUUID(DEFAULT_NUMBER_GENERATOR, DEFAULT_ALPHABET, DEFAULT_SIZE);
    }

	private static String randomUUID(final Random random, final char[] alphabet, final int size) {
        if (random == null) throw new IllegalArgumentException("Random no puede ser nulo");
        if (alphabet == null) throw new IllegalArgumentException("El alfabeto no puede ser nulo");
        if (alphabet.length == 0 || alphabet.length >= 256) throw new IllegalArgumentException("El alfabeto debe contener entre 1 y 255 símbolos");
        if (size <= 0) throw new IllegalArgumentException("El tatamañomannyo debe ser mayor que cero");
        final int mask = (2 << (int) Math.floor(Math.log(alphabet.length - 1) / Math.log(2))) - 1;
        final int step = (int) Math.ceil(1.6 * mask * size / alphabet.length);
        final StringBuilder idBuilder = new StringBuilder();
        while (true) {
            final byte[] bytes = new byte[step];
            random.nextBytes(bytes);
            for (int i = 0; i < step; i++) {
                final int alphabetIndex = bytes[i] & mask;
                if (alphabetIndex < alphabet.length) {
                    idBuilder.append(alphabet[alphabetIndex]);
                    if (idBuilder.length() == size) {
                        return idBuilder.toString();
                    }
                }
            }
        }
    }
	
	private static String revisarContingutNom(String nom) {
		if (nom == null) {
			return null;
		}
		return nom.replace("&", "&amp;").replaceAll("[\\\\/:*?\"<>|]", "_");
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaginacioHelper.class);
}
