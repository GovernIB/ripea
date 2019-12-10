package es.caib.ripea.war.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.war.command.DocumentConcatenatCommand;

/**
 * Mètodes d'ajuda per a gestionar els documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentHelper {

	public static void concatenarDocuments(
			DocumentService documentService,
			ContingutService contingutService,
			EntitatDto entitatActual,
			DocumentConcatenatCommand command,
			Map<String, Long> ordre) {
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
	
	public static void generarFitxerZip(
			DocumentService documentService,
			ContingutService contingutService,
			EntitatDto entitatActual,
			DocumentConcatenatCommand command,
			Set<Long> docsIdx,
			ContingutDto contingut,
			ByteArrayOutputStream baos) {
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
						ZipEntry entry = new ZipEntry(revisarContingutNom(fitxer.getNom()));
						entry.setSize(fitxer.getContingut().length);
						zos.putNextEntry(entry);
						zos.write(fitxer.getContingut());
						zos.closeEntry();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				zos.close();
	
				if (command != null) {
					reportContent = baos.toByteArray();
					command.setDocumentTipus(DocumentTipusEnumDto.VIRTUAL);
					command.setFitxerNom(revisarContingutNom(((ExpedientDto)contingut).getNom()) + ".zip");
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
	
	private static String revisarContingutNom(String nom) {
		if (nom == null) {
			return null;
		}
		return nom.replace("&", "&amp;").replaceAll("[\\\\/:*?\"<>|]", "_");
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaginacioHelper.class);
}
