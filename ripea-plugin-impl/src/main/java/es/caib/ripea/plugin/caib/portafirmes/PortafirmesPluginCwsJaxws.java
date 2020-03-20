/**
 * 
 */
package es.caib.ripea.plugin.caib.portafirmes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPBinding;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentTipus;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxBloc;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesIniciFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.plugin.utils.PropertiesHelper;
import es.indra.portafirmasws.cws.Annex;
import es.indra.portafirmasws.cws.Annexes;
import es.indra.portafirmasws.cws.Application;
import es.indra.portafirmasws.cws.Cws;
import es.indra.portafirmasws.cws.DeleteRequest;
import es.indra.portafirmasws.cws.DeleteRequestDocument;
import es.indra.portafirmasws.cws.DeleteRequestDocuments;
import es.indra.portafirmasws.cws.DeleteResponse;
import es.indra.portafirmasws.cws.DocumentAttributes;
import es.indra.portafirmasws.cws.DownloadRequest;
import es.indra.portafirmasws.cws.DownloadRequestDocument;
import es.indra.portafirmasws.cws.DownloadResponse;
import es.indra.portafirmasws.cws.ImportanceEnum;
import es.indra.portafirmasws.cws.Result;
import es.indra.portafirmasws.cws.Sender;
import es.indra.portafirmasws.cws.SignModeEnum;
import es.indra.portafirmasws.cws.Signer;
import es.indra.portafirmasws.cws.Signers;
import es.indra.portafirmasws.cws.Steps;
import es.indra.portafirmasws.cws.UploadRequest;
import es.indra.portafirmasws.cws.UploadRequestDocument;
import es.indra.portafirmasws.cws.UploadResponse;
import es.indra.portafirmasws.cws.UploadStep;

/**
 * Implementació del plugin de portafirmes emprant el portafirmes
 * de la CAIB desenvolupat per Indra. L'accés al servei web del
 * portafirmes empra les classes pròpies del JDK però no funciona
 * correctament la descàrrega dels documents firmats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesPluginCwsJaxws implements PortafirmesPlugin {

	@Override
	public String upload(
			PortafirmesDocument document,
			String documentTipus,
			String motiu,
			String remitent,
			PortafirmesPrioritatEnum importancia,
			Date dataCaducitat,
			List<PortafirmesFluxBloc> flux,
			String plantillaFluxId,
			List<PortafirmesDocument> annexos,
			boolean signarAnnexos,
			String transaccioId) throws SistemaExternException {
		try {
			comprovarExtensioArxiu(document.getArxiuExtensio());
			UploadRequest uploadRequest = new UploadRequest();
			Application application = new Application();
			application.setUser(getUsername());
			application.setPassword(getPassword());
			uploadRequest.setApplication(application);
			uploadRequest.setDocument(
					getUploadRequestDocument(
							document,
							documentTipus,
							annexos,
							signarAnnexos,
							flux,
							motiu,
							remitent,
							importancia,
							dataCaducitat));
			Cws cws = getCwsService();
			addAttachments(cws, document, annexos);
			UploadResponse uploadResponse = cws.uploadDocument(
					uploadRequest);
			comprovarResult(uploadResponse.getResult());
			return new Integer(uploadResponse.getDocument().getId()).toString();
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut pujar el document al portafirmes",
					ex);
		}
	}

	@Override
	public PortafirmesDocument download(
			String id) throws SistemaExternException {
		try {
			DownloadRequest downloadRequest = new DownloadRequest();
			Application application = new Application();
			application.setUser(getUsername());
			application.setPassword(getPassword());
			downloadRequest.setApplication(application);
			DownloadRequestDocument downloadRequestDocument = new DownloadRequestDocument();
			downloadRequestDocument.setId(new Long(id).intValue());
			downloadRequest.setDocument(downloadRequestDocument);
			downloadRequest.setDownloadDocuments(
					new JAXBElement<Boolean>(
							new QName("download-documents"),
							Boolean.class,
							new Boolean(true)));
			downloadRequest.setAdditionalInfo(
					new JAXBElement<Boolean>(
							new QName("additional-info"),
							Boolean.class,
							new Boolean(true)));
			downloadRequest.setArchiveInfo(
					new JAXBElement<Boolean>(
							new QName("archive-info"),
							Boolean.class,
							new Boolean(true)));
			Cws cws = getCwsService();
			BindingProvider bp = (BindingProvider)cws;
			DownloadResponse downloadResponse = cws.downloadDocument(
					downloadRequest);
			comprovarResult(downloadResponse.getResult());
			@SuppressWarnings("unchecked")
			Map<String, DataHandler> attachmentsIn = (Map<String, DataHandler>)bp.getResponseContext().get(
					MessageContext.INBOUND_MESSAGE_ATTACHMENTS);
			System.out.println(">>> attachmentsIn: " + attachmentsIn);
			PortafirmesDocument downloadedDocument = new PortafirmesDocument();
			downloadedDocument.setTitol(
					downloadResponse.getDocument().getAttributes().getTitle());
			downloadedDocument.setDescripcio(
					downloadResponse.getDocument().getAttributes().getDescription());
			JAXBElement<Boolean> isFileSign = downloadResponse.getDocument().getAttributes().getIsFileSign();
			if (isFileSign != null) {
				downloadedDocument.setFirmat(isFileSign.getValue());
			}
			return downloadedDocument;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut descarregar el document del portafirmes",
					ex);
		}
	}

	@Override
	public void delete(
			String id) throws SistemaExternException {
		try {
			DeleteRequest deleteRequest = new DeleteRequest();
			Application application = new Application();
			application.setUser(getUsername());
			application.setPassword(getPassword());
			deleteRequest.setApplication(application);
			DeleteRequestDocument deleteRequestDocument = new DeleteRequestDocument();
			deleteRequestDocument.setId(new Long(id).intValue());
			DeleteRequestDocuments deleteRequestDocuments = new DeleteRequestDocuments();
			deleteRequestDocuments.getDocument().add(deleteRequestDocument);
			deleteRequest.setDocuments(deleteRequestDocuments);
			Cws cws = getCwsService();
			DeleteResponse deleteResponse = cws.deleteDocuments(
					deleteRequest);
			comprovarResult(deleteResponse.getResult());
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut esborrar el document del portafirmes",
					ex);
		}
	}

	@Override
	public List<PortafirmesDocumentTipus> findDocumentTipus() throws SistemaExternException {
		return null;
	}

	@Override
	public boolean isCustodiaAutomatica() {
		return true;
	}

	@Override
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible,
			String urlReturn) throws SistemaExternException {
		
		return new PortafirmesIniciFluxResposta();
	}
	
	@Override
	public PortafirmesFluxResposta recuperarIdPlantillaFluxDeFirma(String idTransaccio) {
		return new PortafirmesFluxResposta();
	}

	@Override
	public void tancarTransaccioFlux(String idTransaccio) throws SistemaExternException {}
	
	@Override
	public PortafirmesFluxInfo recuperarDetallPlantillaFluxDeFirma(String idTransaccio, String idioma)
			throws SistemaExternException {
		return new PortafirmesFluxInfo();
	}
	
	private UploadRequestDocument getUploadRequestDocument(
			PortafirmesDocument document,
			String documentTipus,
			List<PortafirmesDocument> annexos,
			boolean signarAnnexos,
			List<PortafirmesFluxBloc> flux,
			String motiu,
			String remitent,
			PortafirmesPrioritatEnum importancia,
			Date dataLimit) throws Exception {
		UploadRequestDocument uploadRequestDocument = new UploadRequestDocument();
		DocumentAttributes documentAttributes = new DocumentAttributes();
		documentAttributes.setTitle(
				limitarString(document.getTitol(), 100));
		documentAttributes.setDescription(
				limitarString(document.getDescripcio(), 250));
		documentAttributes.setExtension(document.getArxiuExtensio());
		documentAttributes.setType(new Integer(documentTipus).intValue());
		documentAttributes.setSubject(motiu);
		Sender sender = new Sender();
		sender.setName(
				limitarString(remitent, 50));
		documentAttributes.setSender(sender);
		if (importancia == null) {
			documentAttributes.setImportance(
					ImportanceEnum.NORMAL);
		} else {
			switch (importancia) {
			case BAIXA:
				documentAttributes.setImportance(
						ImportanceEnum.LOW);
				break;
			case NORMAL:
				documentAttributes.setImportance(
						ImportanceEnum.NORMAL);
				break;
			case ALTA:
				documentAttributes.setImportance(
						ImportanceEnum.HIGH);
				break;
			}
		}
		// 1 - PDF; 2 - P7/CMS/CADES; 3 - XADES;
		documentAttributes.setTypeSign(
				new JAXBElement<Integer>(
						new QName("type-sign"),
						Integer.class,
						new Integer(getSignaturaTipus())));
		documentAttributes.setIsFileSign(
				new JAXBElement<Boolean>(
						new QName("is-file-sign"),
						Boolean.class,
						new Boolean(document.isFirmat())));
		uploadRequestDocument.setAttributes(documentAttributes);
		Steps steps = new Steps();
		steps.setSignMode(
				new JAXBElement<SignModeEnum>(
						new QName("sign-mode"),
						SignModeEnum.class,
						SignModeEnum.ATTACHED));
		for (PortafirmesFluxBloc bloc: flux) {
			UploadStep step = new UploadStep();
			step.setMinimalSigners(
					new JAXBElement<Integer>(
							new QName("minimal-signers"),
							Integer.class,
							new Integer(bloc.getMinSignataris())));
			Signers signers = new Signers();
			for (String signatari: bloc.getDestinataris()) {
				Signer signer = new Signer();
				signer.setId(signatari);
				signer.setCheckCert(
						new JAXBElement<Boolean>(
								new QName("check-cert"),
								Boolean.class,
								new Boolean(isCheckCert())));
				signers.getSigner().add(signer);
			}
			step.setSigners(signers);
			steps.getStep().add(step);
		}
		uploadRequestDocument.setSteps(steps);
		if (annexos != null) {
			Annexes annexes = new Annexes();
			for (PortafirmesDocument annex: annexos) {
				Annex anx = new Annex();
				anx.setDescription(
						limitarString(annex.getDescripcio(), 250));
				anx.setExtension(annex.getArxiuExtensio());
				anx.setTypeSign(
						new JAXBElement<Integer>(
								new QName("type-sign"),
								Integer.class,
								new Integer(getSignaturaTipus())));
				anx.setIsFileSign(
						new JAXBElement<Boolean>(
								new QName("is-file-sign"),
								Boolean.class,
								new Boolean(annex.isFirmat())));
				Sender anxSender = new Sender();
				anxSender.setName(
						limitarString(remitent, 50));
				anx.setSender(anxSender);
				annexes.getAnnex().add(anx);
			}
			uploadRequestDocument.setAnnexes(annexes);
		}
		return uploadRequestDocument;
	}

	private Cws getCwsService() throws Exception {
		Service service = Service.create(
				new URL(getServiceUrl() + "?wsdl"),
				new QName(
						"http://www.indra.es/portafirmasws/cws",
						"CwsService"));
		Cws cws = service.getPort(Cws.class);
		BindingProvider bp = (BindingProvider)cws;
		if (isLogMissatgesActiu()) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		SOAPBinding binding = (SOAPBinding)bp.getBinding();
		binding.setMTOMEnabled(false);
		return cws;
	}

	private void addAttachments(
			Cws cws,
			PortafirmesDocument document,
			List<PortafirmesDocument> annexos) {
		Map<String, DataHandler> attachments = new HashMap<String, DataHandler>();
		DataHandler handler = new DataHandler(
        		new ByteArrayDataSource(
        				document.getArxiuContingut(),
        				document.getArxiuNom(),
    	                null));
		attachments.put(
				handler.getName(),
				handler);
		if (annexos != null) {
			for (PortafirmesDocument annex: annexos) {
				DataHandler annexHandler = new DataHandler(
		        		new ByteArrayDataSource(
		        				annex.getArxiuContingut(),
		        				annex.getArxiuNom(),
		    	                null));
				attachments.put(
						annexHandler.getName(),
						annexHandler);
			}
		}
		BindingProvider bp = (BindingProvider)cws;
		bp.getRequestContext().put(
				MessageContext.OUTBOUND_MESSAGE_ATTACHMENTS,
				attachments);
	}

	private void comprovarResult(Result result) throws SistemaExternException {
		if (result.getCode() != 0 || !"OK".equals(result.getMessage())) {
			throw new SistemaExternException(
					"La resposta del portafirmes no conté un resultat correcte (" +
					"code=" + result.getCode() + ", " +
					"message=" + result.getMessage() + ")");
		}
	}

	private void comprovarExtensioArxiu(
			String extensio) throws SistemaExternException {
		String extensionsPermeses = getExtensionsPermeses();
		if (extensionsPermeses != null && !extensionsPermeses.isEmpty()) {
			String[] permeses = extensionsPermeses.split(",");
			boolean extensioOk = false;
			for (String permesa: permeses) {
				if (extensio.equalsIgnoreCase(permesa.trim())) {
					extensioOk = true;
					break;
				}
			}
			if (!extensioOk) {
				throw new SistemaExternException(
						"L'extensió del document no està permesa (" +
						"permeses=" + extensionsPermeses + ")");
			}
		}
	}

	private String limitarString(String str, int maxChars) {
		if (str == null)
			return null;
		String textFinal = "[...]";
		if (str.length() > maxChars) {
			return str.substring(0, maxChars - textFinal.length()) + textFinal;
		} else {
			return str;
		}
	}

	private String getServiceUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.cws.service.url");
	}
	private String getUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.cws.username");
	}
	private String getPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.cws.password");
	}
	private boolean isCheckCert() {
		return PropertiesHelper.getProperties().getAsBoolean(
				"es.caib.ripea.plugin.portafirmes.cws.check.cert");
	}
	private String getSignaturaTipus() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.cws.signatura.tipus");
	}
	private String getExtensionsPermeses() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.cws.extensions.permeses");
	}
	private boolean isLogMissatgesActiu() {
		return PropertiesHelper.getProperties().getAsBoolean(
				"es.caib.ripea.plugin.portafirmes.cws.log.actiu");
	}

	private class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {
		public boolean handleMessage(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public Set<QName> getHeaders() {
			return Collections.emptySet();
		}
		public boolean handleFault(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public void close(MessageContext context) {
		}
		private void log(SOAPMessageContext messageContext) {
			SOAPMessage msg = messageContext.getMessage();
			try {
				Boolean outboundProperty = (Boolean)messageContext.get(
						MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				if (outboundProperty)
					System.out.print("Missatge SOAP petició: ");
				else
					System.out.print("Missatge SOAP resposta: ");
				msg.writeTo(System.out);
				System.out.println();
			} catch (SOAPException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			} catch (IOException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			}
		}
	}

	private class ByteArrayDataSource implements DataSource {
		private byte[] dades;
		private String name;
		private String contentType;
		public ByteArrayDataSource(
				byte[] dades,
				String name,
				String contentType) {
			this.dades = dades;
			this.name = name;
			this.contentType = contentType;
		}
		public OutputStream getOutputStream() throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(dades);
			return baos;
		}
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(dades);
		}
		public String getName() {
			return name;
		}
		public String getContentType() {
			return contentType;
		}
	}

}
