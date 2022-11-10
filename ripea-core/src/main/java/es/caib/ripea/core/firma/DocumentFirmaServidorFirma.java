package es.caib.ripea.core.firma;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

//import org.ghost4j.document.Document;
//import org.ghost4j.document.PDFDocument;
//import org.ghost4j.modifier.SafeAppenderModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.PdfReader;

import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.plugin.firmaservidor.SignaturaResposta;

@Component
public class DocumentFirmaServidorFirma extends DocumentFirmaHelper{

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	
	
	public ArxiuFirmaDto firmar(DocumentEntity document, FitxerDto fitxer, String motiu) {


		SignaturaResposta firma = pluginHelper.firmaServidorFirmar(document, fitxer, motiu, "ca");
		ArxiuFirmaDto arxiuFirma = new ArxiuFirmaDto();
		arxiuFirma.setFitxerNom(firma.getNom());
		arxiuFirma.setContingut(firma.getContingut());
		arxiuFirma.setTipusMime(firma.getMime());
		arxiuFirma.setTipus(pluginHelper.toArxiuFirmaTipus(firma.getTipusFirmaEni()));
		ArxiuFirmaPerfilEnumDto perfil = pluginHelper.toArxiuFirmaPerfilEnum(firma.getPerfilFirmaEni());
		arxiuFirma.setPerfil(perfil);
		if (document.getArxiuUuid() != null) {
			
			ArxiuEstatEnumDto arxiuEstat = ArxiuEstatEnumDto.DEFINITIU;
			pluginHelper.arxiuDocumentActualitzar(
					document,
					fitxer,
					true,
					true,
					Arrays.asList(arxiuFirma), 
					arxiuEstat);
			
			
		} else {
			guardarDocumentFirmatArxiu(document, fitxer, Arrays.asList(arxiuFirma));
		}

		logAll(document, LogTipusEnumDto.SFIRMA_FIRMA);
		return arxiuFirma;
	}
	
	
	public void guardarDocumentFirmatArxiu(
			DocumentEntity documentEntity,
			FitxerDto fitxer,
			List<ArxiuFirmaDto> firmes) {
	
		fitxer.setNom(documentEntity.getFitxerNom());
		fitxer.setContentType(documentEntity.getFitxerContentType());
		fitxer.setContingut(firmes.get(0).getContingut());

		ArxiuEstatEnumDto arxiuEstat = ArxiuEstatEnumDto.ESBORRANY;
		contingutHelper.arxiuPropagarModificacio(
				documentEntity,
				fitxer,
				true,
				false,
				firmes, 
				arxiuEstat);
			
		if (documentEntity.getGesDocAdjuntId() != null) {
			pluginHelper.gestioDocumentalDelete(documentEntity.getGesDocAdjuntId(),
					PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
			documentEntity.setGesDocAdjuntId(null);
		}
		if (documentEntity.getGesDocAdjuntFirmaId() != null) {
			pluginHelper.gestioDocumentalDelete(documentEntity.getGesDocAdjuntFirmaId(),
					PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
			documentEntity.setGesDocAdjuntFirmaId(null);
		}

	}
	
	
	
	
	
	

	/**
	 * Registra el log al document i al expedient on està el document.
	 * Pots especificar directament el document firmat.
	 * Usar quan el document no està associat a l'objecte documentPortafirmes.
	 *   
	 * @param document
	 * @param tipusLog
	 */
	private void logAll(DocumentEntity document, LogTipusEnumDto tipusLog) {
		contingutLogHelper.log(
				document,
				tipusLog,
				null,
				null,
				false,
				false);
		logExpedient(document, tipusLog);
	}
	
	

	public byte[] removeSignaturesPdfUsingPdfWriterCopyPdf(
			byte[] contingut,
			String contentType) {
		if (contentType.equals("application/pdf")) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			try {
				PdfReader reader = new PdfReader(contingut);
				ByteArrayInputStream bais = null;

				com.lowagie.text.Document document = new com.lowagie.text.Document();

				bais = new ByteArrayInputStream(contingut);
				baos = new ByteArrayOutputStream();

				com.lowagie.text.pdf.PdfReader inputPDF = new com.lowagie.text.pdf.PdfReader(bais);

				// create a writer for the outputstream
				com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, baos);

				document.open();
				com.lowagie.text.pdf.PdfContentByte cb = writer.getDirectContent();

				com.lowagie.text.pdf.PdfImportedPage page;

				for (int pageC = 1; pageC <= reader.getNumberOfPages(); pageC++) {
					document.newPage();
					page = writer.getImportedPage(inputPDF, pageC);
					cb.addTemplate(page, 0, 0);
				}

				document.close();
				reader.close();

				return baos.toByteArray();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		} else {
			throw new RuntimeException("Only removing signatures of pdf supported");
		}

	}
	

	
	
	
	
//	public byte[] removeSignaturesPdfUsingGhost4J(
//			byte[] contingut,
//			String contentType) {
//		if (contentType.equals("application/pdf")) {
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//			try {
//				 //load PostScript document
//				PDFDocument document = new PDFDocument();
//			    document.load(new ByteArrayInputStream(contingut));
//			 
//			 
//			    // gives org.ghost4j.document.DocumentException: Documents of class org.ghost4j.document.PDFDocument are not supported by the component 
//			    // at org.ghost4j.AbstractComponent.assertDocumentSupported(AbstractComponent.java:58)
////			    //create converter
////			    PDFConverter converter = new PDFConverter();
////			    //set options
////			    converter.setPDFSettings(PDFConverter.OPTION_PDFSETTINGS_PREPRESS);
////			    //convert
////			    converter.convert(document, baos);
//			    
//
//
//		 
//		        // prepare modifier
//		        SafeAppenderModifier modifier = new SafeAppenderModifier();
//		 
//		        // prepare modifier parameters
//		        Map<String, Serializable> parameters = new HashMap<String, Serializable>();
//		        parameters.put(SafeAppenderModifier.PARAMETER_APPEND_DOCUMENT,
//		        		document);
//		 
//		        // run modifier
//		        Document result = modifier.modify(document, parameters);
//		 
//		        // write resulting document to file
//		        result.write(baos);
//			    
//			    return baos.toByteArray();
//			    
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//
//		} else {
//			throw new RuntimeException("Only removing signatures of pdf supported");
//		}
//
//	}
	
	
	


	
	
}
