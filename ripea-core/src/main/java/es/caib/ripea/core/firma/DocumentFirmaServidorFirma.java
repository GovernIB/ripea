package es.caib.ripea.core.firma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

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
import es.caib.ripea.core.api.dto.SignatureInfoDto;
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

	@Autowired private PluginHelper pluginHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private DocumentHelper documentHelper;
	@Autowired private DocumentRepository documentRepository;
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public ArxiuFirmaDto firmar(Long documentId, String motiu, List<Long> documentsClonar) {
		return removeFirmesInvalidesAndFirmaServidor(documentId, motiu, documentsClonar);
	}
	
	/**
	 * Casos possibles de firma en servidor:
	 * 1.- Annex de anotació de registre no mogut (esPerClonar=true), tendrà uuid de distribució.
	 * 2.- Altres documents:
	 * 		> Té firmes inválides i ja es troba a l'arxiu: Copia en local, firmar en servidor i guardar a arxiu.
	 *  	> Té firmes inválides i NO es troba a l'arxiu: Copia en local, firmar en servidor i guardar a arxiu.
	 * 		> No té firmes inválides i ja es troba a l'arxiu: Firmar en servidor i actualitzar a arxiu.
	 * 		> No té firmes inválides i NO es troba a l'arxiu: Firmar en servidor i guardar a arxiu.
	 */
	public ArxiuFirmaDto removeFirmesInvalidesAndFirmaServidor(Long documentId, String motiu, List<Long> documentsClonar) {

		DocumentEntity document = documentRepository.getOne(documentId);
				
		if (document != null) {
			
			try {
				
				FitxerDto fitxer = documentHelper.getFitxerAssociat(document, null);
				boolean esPerClonar = documentsClonar.contains(document.getId());
				boolean eliminaFirmes = false;
				
				//Si s'ha detectat previament alguna firma incorrecte, l'eliminarem posteriorment (si es per clonar s'eliminaríen igualment)
				if (!document.isValidacioFirmaCorrecte() || esPerClonar) {
					eliminaFirmes = true;
				} else {
					logger.info("Detectant firmes per el document "+documentId);
					//Si no s'ha detectat previament, ho comprovam ara. En cas de detectar firmes incorrectes, s'eliminarán
					SignatureInfoDto firmes = pluginHelper.detectSignedAttachedUsingValidateSignaturePlugin(fitxer.getContingut(), fitxer.getContentType());
					if (firmes!=null && firmes.isSigned() && firmes.isError()) {
						logger.info("Firmes erronies detectades per el document "+documentId);
						eliminaFirmes = true;
					}
				}
				
				if (eliminaFirmes || document.getArxiuUuid() == null || esPerClonar) {
					//Guarda copia del arxiu original
					if (eliminaFirmes || esPerClonar) {
						logger.info("Creant còpia per el document "+documentId);
						preparaDocumentPerFirmaEnServidor(fitxer.getContingut(), document);
					}
					//Elimina firmes del PDF
					fitxer.setContingut(removeSignaturesPdfUsingPdfWriterCopyPdf(fitxer.getContingut(), fitxer.getContentType()));
					logger.info("Firmes eliminades per el document "+documentId);
				}
				
				SignaturaResposta firma = pluginHelper.firmaServidorFirmar(document, fitxer, motiu, "ca");
				logger.info("El document "+documentId +" s'ha firma en servidor.");
				
				/*SignaturaResposta firma = null;
				try {
					firma = pluginHelper.firmaServidorFirmar(document, fitxer, motiu, "ca");
				} catch (Exception e) {
					logger.error("Error al firmar en servidor el documento, documentId=" + document.getId() + ", documentNom=" + document.getNom(), e);
					//El document té firma errònia que no ha estat detectada previament.
					//Per tant no ha passat per el removeSignaturesPdfUsingPdfWriterCopyPdf anterior.
					if (Utils.getRootMsg(e).contains("Error no controlat cridant al validador de firmes Plugin Validacio Firmes afirma CXF") ||
						Utils.getRootMsg(e).contains("InvalidNotSignerCertificate")) {
						
						//Guarda copia del arxiu original
						preparaDocumentPerFirmaEnServidor(fitxer.getContingut(), document);
						//Elimina firmes del PDF
						fitxer.setContingut(removeSignaturesPdfUsingPdfWriterCopyPdf(fitxer.getContingut(), fitxer.getContentType()));
						firma = pluginHelper.firmaServidorFirmar(document, fitxer, motiu, "ca");
						
					} else {
						throw e;
					}
				}*/
				
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
				
				//Ja podem eliminar el error de validacio de firma ara que hem clonat i firmat en servidor.
				document.setValidacioFirmaCorrecte(true);
				document.setValidacioFirmaErrorMsg(null);
				
				logAll(document, LogTipusEnumDto.SFIRMA_FIRMA);
				return arxiuFirma;

			} catch (Exception e) {
				throw new FirmaServidorException(document.getNom(), Utils.getRootMsg(e));
			}
			
		} else {
			throw new NotFoundException(documentId, DocumentEntity.class);
		}
	}
	
	private void preparaDocumentPerFirmaEnServidor(byte[] contingut, DocumentEntity document) {

		//Es guarda a fileSystem
		String gestioDocumentalAdjuntId = pluginHelper.gestioDocumentalCreate(
				PluginHelper.GESDOC_AGRUPACIO_DOCS_ORIGINALS,
				new ByteArrayInputStream(contingut));

		//Realment no es un uuid, pero d'aquesta manera tenim un sol cap per apuntar al document original.
		//El métode de descarregar document original ja s'encarrega de anar-lo a cercar a arxiu o file system. 
		document.setGesDocOriginalId(gestioDocumentalAdjuntId); 
		
		//borram el camp uuid per que al "arxiuPropagarModificacio" crei un document nou al arxiu dins la carpeta del contingut pare.
		document.setArxiuUuid(null);
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

	private static final Logger logger = LoggerFactory.getLogger(DocumentFirmaServidorFirma.class);
	
}