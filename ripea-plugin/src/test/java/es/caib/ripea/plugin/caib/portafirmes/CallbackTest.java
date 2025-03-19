/**
 * 
 */
package es.caib.ripea.plugin.caib.portafirmes;

import es.caib.ripea.plugin.caib.cws.Application;
import es.caib.ripea.plugin.caib.cws.Cws;
import es.caib.ripea.plugin.caib.cws.DocumentAttributes;
import es.caib.ripea.plugin.caib.cws.ImportanceEnum;
import es.caib.ripea.plugin.caib.cws.Sender;
import es.caib.ripea.plugin.caib.cws.SignModeEnum;
import es.caib.ripea.plugin.caib.cws.Signer;
import es.caib.ripea.plugin.caib.cws.Signers;
import es.caib.ripea.plugin.caib.cws.Steps;
import es.caib.ripea.plugin.caib.cws.UploadRequest;
import es.caib.ripea.plugin.caib.cws.UploadRequestDocument;
import es.caib.ripea.plugin.caib.cws.UploadResponse;
import es.caib.ripea.plugin.caib.cws.UploadStep;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.MTOMFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe de proves pel portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CallbackTest {

	private static final String ENDPOINT_ADDRESS = "https://proves.caib.es/portafirmasws/web/services/CWS";
	private static final String USERNAME = "HELIUM";
	private static final String PASSWORD = "HELIUM";

	public static void main(String[] args) {
		try {
			int documentId = new CallbackTest().uploadTest();
			System.out.println(">>> Document id: " + documentId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private int uploadTest() throws Exception {
		UploadRequest uploadRequest = new UploadRequest();
		Application application = new Application();
		application.setUser(USERNAME);
		application.setPassword(PASSWORD);
		uploadRequest.setApplication(application);
		uploadRequest.setDocument(getUploadRequestDocument());
		uploadRequest.setVersion(null);
		UploadResponse uploadResponse = getCwsService().uploadDocument(uploadRequest);
		return uploadResponse.getDocument().getId();
	}

	private UploadRequestDocument getUploadRequestDocument() throws Exception {
		UploadRequestDocument uploadRequestDocument = new UploadRequestDocument();
		DocumentAttributes documentAttributes = new DocumentAttributes();
		documentAttributes.setTitle("(RIP) Document per firmar");
		documentAttributes.setExtension("pdf");
		documentAttributes.setDescription("(RIP) Descripció del document");
		documentAttributes.setType(new Integer(999));
		Sender sender = new Sender();
		sender.setName("(RIP) Remitent");
		documentAttributes.setSender(sender);
		documentAttributes.setImportance(ImportanceEnum.NORMAL);
		// 1 - PDF; 2 - P7/CMS/CADES; 3 - XADES;
		documentAttributes.setTypeSign(
				new JAXBElement<Integer>(
						new QName("type-sign"),
						Integer.class,
						new Integer(1)));
		documentAttributes.setIsFileSign(
				new JAXBElement<Boolean>(
						new QName("is-file-sign"),
						Boolean.class,
						new Boolean(false)));
		uploadRequestDocument.setAttributes(documentAttributes);
		Steps steps = new Steps();
		steps.setSignMode(
				new JAXBElement<SignModeEnum>(
						new QName("sign-mode"),
						SignModeEnum.class,
						SignModeEnum.ATTACHED));
		UploadStep step = new UploadStep();
		step.setMinimalSigners(
				new JAXBElement<Integer>(
						new QName("minimal-signers"),
						Integer.class,
						new Integer(1)));
		Signers signers = new Signers();
		Signer signer = new Signer();
		signer.setId("37340638T");
		signer.setCheckCert(
				new JAXBElement<Boolean>(
						new QName("check-cert"),
						Boolean.class,
						new Boolean(false)));
		signers.getSigner().add(signer);
		step.setSigners(signers);
		steps.getStep().add(step);
		uploadRequestDocument.setSteps(steps);
		return uploadRequestDocument;
	}

	private Cws getCwsService() throws Exception {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		QName qname = new QName(
				"http://www.indra.es/portafirmasws/cws",
				"CwsService");
		Service service = Service.create(url, qname);
		Cws cws = service.getPort(
				Cws.class,
				new MTOMFeature());
		BindingProvider bp = (BindingProvider)cws;
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new AttachmentMessageHandler());
		handlerChain.add(new LogMessageHandler());
		bp.getBinding().setHandlerChain(handlerChain);
		/*bp.getRequestContext().put(
				BindingProvider.USERNAME_PROPERTY,
				USERNAME);
		bp.getRequestContext().put(
				BindingProvider.PASSWORD_PROPERTY,
				PASSWORD);*/
		return cws;
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

	private class AttachmentMessageHandler implements SOAPHandler<SOAPMessageContext> {
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
			try {
				Boolean outboundProperty = (Boolean)messageContext.get(
						MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				if (outboundProperty) {
	                SOAPMessage objSOAPMessage = messageContext.getMessage();
	                DataHandler dh = new DataHandler(
	                		new UrlDataSource(
	            	                "document_firma.pdf",
	            	                getClass().getResource(
	            	                		"/es/caib/ripea/plugin/caib/document_firma.pdf"),
	            	                null));
	                AttachmentPart objAttachment = objSOAPMessage
	                        .createAttachmentPart(dh);
	                objAttachment.setContentId("document_firma.pdf");
	                objSOAPMessage.addAttachmentPart(objAttachment);
	                messageContext.setMessage(objSOAPMessage);
	                objSOAPMessage.writeTo(System.out);
				}
			} catch (Exception ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			}
		}
	}

	public class UrlDataSource implements DataSource {
		private String name;
		private URL url;
		private String contentType;
		public UrlDataSource(
				String name,
				URL url,
				String contentType) {
			this.name = name;
		    this.url = url;
		    this.contentType = contentType;
		}
	    @Override
	    public InputStream getInputStream() throws IOException {
	        return url.openStream();
	    }
	    @Override
	    public OutputStream getOutputStream() throws IOException {
	        throw new UnsupportedOperationException("Not implemented");
	    }
	    @Override
	    public String getName() {
	        return name;
	    }
	    @Override
	    public String getContentType() {
	        if (contentType != null)
	        	return contentType;
	        else
	        	return "*/*";
	    }
	}
	/*private class ByteArrayDataSource implements DataSource {
		private byte[] dades;
		private String name;
		private String contentType;
		public ByteArrayDataSource(byte[] dades, String name, String contentType) {
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
	}*/

}
