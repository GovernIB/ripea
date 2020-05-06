package es.caib.ripea.war.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.DocumentGenericCommand;

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
	
	public void concatenarDocuments(
			Long entitatId,
			DocumentService documentService,
			ContingutService contingutService,
			EntitatDto entitatActual,
			DocumentGenericCommand command,
			Map<String, Long> ordre) {
		MetaDocumentDto metaDocument = metaDocumentService.findByTipusGeneric(
				entitatId, 
				MetaDocumentTipusGenericEnumDto.NOTIFICACION);
		
		FitxerDto fitxer;
		PDDocument resultat = new PDDocument();
		ByteArrayOutputStream resultatOutputStream = new ByteArrayOutputStream();
		PDFMergerUtility PDFmerger = new PDFMergerUtility(); 
		try {
			for (Map.Entry<String, Long> entry : ordre.entrySet()) {
				
				ContingutDto contingut = contingutService.findAmbIdUser(
						entitatActual.getId(),
						entry.getValue(),
						true,
						false);
				if (contingut.isDocument() && (((DocumentDto)contingut).isFirmat() || ((DocumentDto)contingut).isCustodiat())) {
					fitxer = documentService.descarregarImprimible(
							entitatActual.getId(),
							entry.getValue(),
							null);
					

					PDDocument document = PDDocument.load(fitxer.getContingut());
					PDFmerger.appendDocument(resultat, document);
				} 
			}
			resultat.save(resultatOutputStream);
			resultat.close();
			
			command.setNom("notificacio_" + new Date().getTime());
			command.setData(new Date());
			command.setMetaNodeId(metaDocument.getId()); //Notificació
			command.setNtiEstadoElaboracion(metaDocument.getNtiEstadoElaboracion());
			command.setNtiIdDocumentoOrigen(metaDocument.getNtiOrigen().name());
			command.setDocumentTipus(DocumentTipusEnumDto.VIRTUAL);
			command.setFitxerNom(command.getNom() + ".pdf");
			command.setFitxerContentType("application/pdf");
			command.setFitxerContingut(resultatOutputStream.toByteArray());
		} catch (IOException ex) {
			LOGGER.error(
					"No s'ha pogut crear el document concatenat a partir del contingut seleccionat",
					ex);
		}
	}
	
	public void generarFitxerZip(
			Long entitatId,
			DocumentService documentService,
			ContingutService contingutService,
			EntitatDto entitatActual,
			DocumentGenericCommand command,
			Set<Long> docsIdx,
			ContingutDto contingut,
			ByteArrayOutputStream baos) {
		
		MetaDocumentDto metaDocument = metaDocumentService.findByTipusGeneric(
				entitatId, 
				MetaDocumentTipusGenericEnumDto.NOTIFICACION);
		
		byte[] reportContent = null;
		
		if (baos == null)
			baos = new ByteArrayOutputStream();
		
		ZipOutputStream zos = new ZipOutputStream(baos);
		try {
			if (docsIdx != null) {
				for (Long docId: docsIdx) {
					FitxerDto fitxer = null;
					ContingutDto contingutDoc = contingutService.findAmbIdUser(
							entitatActual.getId(),
							docId,
							true,
							false);
					if (contingutDoc instanceof DocumentDto)
						fitxer = documentService.descarregar(
								entitatActual.getId(),
								docId,
								null);
					try {
						ZipEntry entry = new ZipEntry(revisarContingutNom(contingutDoc.getNom()) + "." + FilenameUtils.getExtension(fitxer.getNom()));
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
	
				if (command != null) {
					reportContent = baos.toByteArray();
					command.setNom("notificacio_" + randomUUID());
					command.setData(new Date());
					command.setMetaNodeId(metaDocument.getId()); //Notificació
					command.setNtiEstadoElaboracion(metaDocument.getNtiEstadoElaboracion());
					command.setNtiIdDocumentoOrigen(metaDocument.getNtiOrigen().name());
					command.setDocumentTipus(DocumentTipusEnumDto.VIRTUAL);
					command.setFitxerNom(command.getNom() + ".zip");
					command.setFitxerContentType("application/zip");
					command.setFitxerContingut(reportContent);
				}	
			}		
		} catch (IOException ex) {
			LOGGER.error(
					"No s'ha generar el fitxer zip a partir del contingut seleccionat",
					ex);
		}
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
