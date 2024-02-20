package es.caib.ripea.core.firma;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.ghost4j.document.Document;
//import org.ghost4j.document.PDFDocument;
//import org.ghost4j.modifier.SafeAppenderModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.pdf.PdfReader;

import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.exception.FirmaServidorException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.helper.ArxiuConversions;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.plugin.firmaservidor.SignaturaResposta;

@Component
public class DocumentFirmaServidorFirma extends DocumentFirmaHelper{

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private DocumentRepository documentRepository;
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public ArxiuFirmaDto firmar(Long documentId, String motiu) {

		return doFirmar(documentId, motiu);

	}
	
	
	public ArxiuFirmaDto doFirmar(Long documentId, String motiu) {

		DocumentEntity document = documentRepository.getOne(documentId);
		if (document != null) {
			try {
				FitxerDto fitxer = documentHelper.getFitxerAssociat(document, null);
				
				if (!document.isValidacioFirmaCorrecte() || document.getArxiuUuid() == null) {
					//remove invalid signature
					fitxer.setContingut(removeSignaturesPdfUsingPdfWriterCopyPdf(fitxer.getContingut(), fitxer.getContentType()));
				}
				
				SignaturaResposta firma = null;
				try {
					firma = pluginHelper.firmaServidorFirmar(
							document,
							fitxer,
							motiu,
							"ca");
				} catch (Exception e) {
					logger.error("Error al firmar en servidor el documento, documentId=" + document.getId() + ", documentNom=" + document.getNom(), e);
					// if document has signature but it was not detected by ripea/distribucio (this can happen if signature is corrupted as in issue #1375) then remove signature and try signing again
					if (Utils.getRootMsg(e).contains("Error no controlat cridant al validador de firmes Plugin Validacio Firmes afirma CXF")) {

						fitxer.setContingut(removeSignaturesPdfUsingPdfWriterCopyPdf(fitxer.getContingut(), fitxer.getContentType()));
						firma = pluginHelper.firmaServidorFirmar(
								document,
								fitxer,
								motiu,
								"ca");
					} else {
						throw e;
					}
				}
				
				ArxiuFirmaDto arxiuFirma = new ArxiuFirmaDto();
				arxiuFirma.setFitxerNom(firma.getNom());
				arxiuFirma.setContingut(firma.getContingut());
				arxiuFirma.setTipusMime(firma.getMime());
				arxiuFirma.setTipus(ArxiuConversions.toArxiuFirmaTipus(firma.getTipusFirmaEni()));
				ArxiuFirmaPerfilEnumDto perfil = ArxiuConversions.toArxiuFirmaPerfilEnum(firma.getPerfilFirmaEni());
				arxiuFirma.setPerfil(perfil);
				
				
				ArxiuEstatEnumDto arxiuEstat = ArxiuEstatEnumDto.DEFINITIU;
				
				DocumentFirmaTipusEnumDto documentFirmaTipus = getDocumentFirmaTipus(firma);
				document.updateDocumentFirmaTipus(documentFirmaTipus);
				
				if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {

					contingutHelper.arxiuPropagarModificacio(
							document,
							null,
							documentFirmaTipus,
							Arrays.asList(arxiuFirma),
							arxiuEstat);
					
				} else if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA){ 
					contingutHelper.arxiuPropagarModificacio(
							document,
							null, // if we pass fitxer not null to arxiuPropagarModificacio() ArxiuPluginCaib throws ArxiuValidacioException: No és possible marcar el document com a definitiu si es vol modificar el seu contingut.
							documentFirmaTipus,
							Arrays.asList(arxiuFirma),
							arxiuEstat);
				}

				if (document.getGesDocAdjuntId() != null) {
					pluginHelper.gestioDocumentalDelete(document.getGesDocAdjuntId(),
							PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
					document.setGesDocAdjuntId(null);
				}
				if (document.getGesDocAdjuntFirmaId() != null) {
					pluginHelper.gestioDocumentalDelete(document.getGesDocAdjuntFirmaId(),
							PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
					document.setGesDocAdjuntFirmaId(null);
				}

				document.setArxiuUuidFirma(null);
				logAll(document, LogTipusEnumDto.SFIRMA_FIRMA);
				return arxiuFirma;
			} catch (Exception e) {
				throw new FirmaServidorException(document.getNom(), Utils.getRootMsg(e));
			}
			
		} else {
			throw new NotFoundException(documentId, DocumentEntity.class);
		}

	}
	
	
	private DocumentFirmaTipusEnumDto getDocumentFirmaTipus(SignaturaResposta firma){
		DocumentFirmaTipusEnumDto documentFirmaTipus = null;
		if (firma.getTipusFirmaEni().equals("TF04")) {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_SEPARADA;
		} else {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA;
		}
		return documentFirmaTipus;
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
			throw new RuntimeException("L'eliminació de la firma invàlida només està suportada pels fitxers pdf");
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
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentFirmaServidorFirma.class);

	
	
}
