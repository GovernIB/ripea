package es.caib.ripea.core.firma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import org.ghost4j.document.Document;
//import org.ghost4j.document.PDFDocument;
//import org.ghost4j.modifier.SafeAppenderModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import es.caib.ripea.plugin.firmaservidor.SignaturaResposta;

@Component
public class DocumentFirmaServidorFirma extends DocumentFirmaHelper{

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	
	public ArxiuFirmaDto firmar(DocumentEntity document, FitxerDto fitxer, String motiu) {

		TipusFirma tipusFirma = TipusFirma.CADES;
		if ("pdf".equals(fitxer.getExtensio())) {
			tipusFirma = TipusFirma.PADES;
		}
		SignaturaResposta firma = pluginHelper.firmaServidorFirmar(document, fitxer, tipusFirma, motiu, "ca");
		ArxiuFirmaDto arxiuFirma = new ArxiuFirmaDto();
//		arxiuFirma.setFitxerNom("firma.cades");
		arxiuFirma.setFitxerNom(firma.getNom());
		arxiuFirma.setContingut(firma.getContingut());
		arxiuFirma.setTipusMime(firma.getMime());
//		arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
		arxiuFirma.setTipus(pluginHelper.toArxiuFirmaTipus(firma.getTipusFirmaEni()));
//		arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
		ArxiuFirmaPerfilEnumDto perfil = pluginHelper.toArxiuFirmaPerfilEnum(firma.getPerfilFirmaEni());
		arxiuFirma.setPerfil(perfil);
		pluginHelper.arxiuDocumentGuardarFirmaCades(document, fitxer, Arrays.asList(arxiuFirma));
		logAll(document, LogTipusEnumDto.SFIRMA_FIRMA);
		return arxiuFirma;
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
	
	
	
	public byte[] removeSignaturesPdfUsingPdfReader(
			byte[] contingut,
			String contentType) {
		if (contentType.equals("application/pdf")) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			try {
				PdfReader reader = new PdfReader(contingut);
				AcroFields acroFields = reader.getAcroFields();
				ArrayList<String> signatureNames = acroFields.getSignatureNames();
				if (!signatureNames.isEmpty()) {
					PdfStamper stamper = null;
					try {
						stamper = new PdfStamper(
								reader,
								baos);
						for (String name : signatureNames) {
							AcroFields.Item signature = (AcroFields.Item) stamper.getAcroFields().getFieldItem(name);
							for (int i = 0; i < signature.size(); ++i) {
								signature.getWidget(i).clear();
								signature.getMerged(i).clear();
								signature.getValue(i).clear();
							}
							
							PdfDictionary dictionary = stamper.getAcroFields().getSignatureDictionary(name);
							if(dictionary!=null){
							   dictionary.clear();
							}
						}


						
					} finally {
						if (stamper != null) {
							stamper.close();
						}
					}
				}
				return baos.toByteArray();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		} else {
			throw new RuntimeException("Only removing signatures of pdf supported");
		}

	}
	
	/**
	 * The copy still has signatures somewhere because on firma en servidor it gives 
	 * es.caib.ripea.plugin.SistemaExternException: Error durant la realitzaciˇ de les firmes: La firma no Ús v?lida. Raˇ: El certificado firmante se encuentra caducado (urn:afirma:dss:1.0:profile:XSS:resultminor:SignerCertificate:Expired)
	 *  org.fundaciobit.genapp.common.i18n.I18NException: genapp.comodi
       at es.caib.portafib.logic.ValidacioCompletaFirmaLogicaEJB.internalValidateCompletaFirma(ValidacioCompletaFirmaLogicaEJB.java:153)
	 */
	public byte[] removeSignaturesPdfUsingPdfReaderCopyPdf(
			byte[] contingut,
			String contentType) {
		if (contentType.equals("application/pdf")) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			try {
				PdfReader reader = new PdfReader(contingut);
				
				com.itextpdf.text.Document document = new com.itextpdf.text.Document();
			    PdfCopy copy = new PdfSmartCopy(document, baos);
			    document.open();
			    for(int page = 1; page <= reader.getNumberOfPages(); page++) {
			        PdfImportedPage importedPage = copy.getImportedPage(reader, page);
			        copy.addPage(importedPage);
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
	
	public byte[] removeSignaturesPdfUsingPdfWriterCopyPdf(
			byte[] contingut,
			String contentType) {
		if (contentType.equals("application/pdf")) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			try {
				
				com.itextpdf.text.Document document = new com.itextpdf.text.Document();
	            @SuppressWarnings("unused")
	            PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
	            document.open();
	             

				
				PdfReader reader = new PdfReader(contingut);

			    
//			    PdfDocument pdfDoc = new PdfDocument(pdfWriter);
//			    PdfDocument srcDoc = new PdfDocument(reader);
//			    srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), pdfDoc);
//			    
//			    
//	            PdfWriter writer = new PdfWriter(f1);
//	            writer.SetSmartMode(true);
//	            PdfDocument pdfDoc = new PdfDocument(writer);
//	            pdfDoc.InitializeOutlines();
//	            ByteArrayOutputStream baos;
//	            PdfReader reader;
//	            PdfDocument pdfInnerDoc;
//
//	            BufferedStream br = new BufferedStream(myStream1);
//	            // String line = br.readLine();
//	            // loop over readers
//	            // create a PDF in memory
//	            baos = new ByteArrayOutputStream();
//	            reader = new PdfReader(f2);
//	            pdfInnerDoc = new PdfDocument(reader, new PdfWriter(baos));
//	            // form = PdfAcroForm.getAcroForm(pdfInnerDoc, true);
//	            //fill and flatten form...
//	            //add the PDF using copyPagesTo
//	            pdfInnerDoc = new PdfDocument(new PdfReader(myStream1));
//	            pdfInnerDoc.CopyPagesTo(1, pdfInnerDoc.GetNumberOfPages(), pdfDoc, new PdfPageFormCopier());
//
//	            byte[] arrb = baos.ToArray();
//	            pdfInnerDoc.Close();
//	            DownloadPDF(arrb, "MergedPdf")
			    
			    
			    
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
	
	

	
	
	
//	
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
//		        result.write(new File("merged.ps"));
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
