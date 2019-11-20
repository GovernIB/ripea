package es.caib.ripea.war.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
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
			EntitatDto entitatActual,
			DocumentConcatenatCommand command,
			Map<String, Long> ordre) {
		FitxerDto fitxer;
		PDDocument resultat = new PDDocument();
		ByteArrayOutputStream resultatOutputStream = new ByteArrayOutputStream();
		PDFMergerUtility PDFmerger = new PDFMergerUtility(); 
		try {
			for (Map.Entry<String, Long> entry : ordre.entrySet()) {
				fitxer = documentService.descarregar(
						entitatActual.getId(),
						entry.getValue(),
						null);
	
				PDDocument document = PDDocument.load(fitxer.getContingut());
				
				//remove signature
				PDDocumentCatalog catalog = document.getDocumentCatalog();
				catalog.setAcroForm(null);
				
				
				PDFmerger.appendDocument(resultat, document);
			}
			resultat.save(resultatOutputStream);
			resultat.close();
			command.setFitxerNom(command.getNom() + ".pdf");
			command.setFitxerContentType("application/pdf");
			command.setFitxerContingut(resultatOutputStream.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			for (Long docId: docsIdx) {
				DocumentDto document = null;
				FitxerDto fitxer;
				ContingutDto contingutDoc = contingutService.findAmbIdUser(
						entitatActual.getId(),
						docId,
						true,
						false);
				if (contingutDoc instanceof DocumentDto)
					document = (DocumentDto) contingutDoc;
				
				if (document.getEstat().equals(DocumentEstatEnumDto.FIRMAT) || document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT)) {
					fitxer = documentService.descarregarImprimible(
							entitatActual.getId(),
							docId,
							null);
				} else {
					fitxer = documentService.descarregar(
							entitatActual.getId(),
							docId,
							null);
				}
				try {
					ZipEntry entry = new ZipEntry(fitxer.getNom());
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
				command.setFitxerNom(((ExpedientDto)contingut).getNom().replaceAll(" ", "_") + ".zip");
				command.setFitxerContentType("application/octet-stream");
				command.setFitxerContingut(reportContent);
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
