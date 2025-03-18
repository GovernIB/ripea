/**
 * 
 */
package es.caib.ripea.plugin.caib.custodia;

import java.io.InputStream;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import es.caib.signatura.cliente.custodia.CustodiaRequestBuilder;
import es.caib.signatura.cliente.services.custodia.CustodiaService;
import es.caib.signatura.cliente.services.custodia.CustodiaServiceLocator;
import es.caib.signatura.cliente.services.custodia.CustodiaSoapBindingStub;
import es.caib.signatura.cliente.services.custodia.Custodia_PortType;

/**
 * Client per a l'aplicació de custòdia documental de la CAIB
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClienteCustodia {

	private String url;
	private String usuario;
	private String password;

	private CustodiaService service = null;
	private Custodia_PortType custodiaPort = null;

	public ClienteCustodia(String url, String usuario, String password) {
		this.url = url;
		this.usuario = usuario;
		this.password = password;
	}

	public CustodiaResponse custodiarDocumentoSMIME(
			InputStream documento,
			String nombreDocumento,
			String codigoExterno,
			String codigoExternoTipoDocumento) throws Exception {
		CustodiaRequestBuilder custodiaRequestBuilder = new CustodiaRequestBuilder(
				usuario,
				password);
		byte[] xml = custodiaRequestBuilder.buildXML(documento, nombreDocumento, codigoExterno, codigoExternoTipoDocumento);
		return parseResponse(
				getCustodiaPort().custodiarDocumentoSMIME_v2(xml));
	}

	public CustodiaResponse custodiarDocumento(
			InputStream documento,
			String nombreDocumento,
			String codigoExterno,
			String codigoExternoTipoDocumento) throws Exception {
		CustodiaRequestBuilder custodiaRequestBuilder = new CustodiaRequestBuilder(
				usuario,
				password);
		byte[] xml = custodiaRequestBuilder.buildXML(documento, nombreDocumento, codigoExterno, codigoExternoTipoDocumento);
		return parseResponse(
				getCustodiaPort().custodiarDocumento_v2(xml));
	}

	public CustodiaResponse custodiarPDFFirmado(
			InputStream documento,
			String nombreDocumento,
			String codigoExterno,
			String codigoExternoTipoDocumento) throws Exception {
		CustodiaRequestBuilder custodiaRequestBuilder = new CustodiaRequestBuilder(
				usuario,
				password);
		byte[] xml = custodiaRequestBuilder.buildXML(documento, nombreDocumento, codigoExterno, codigoExternoTipoDocumento);
		return parseResponse(
				getCustodiaPort().custodiarPDFFirmado_v2(xml));
	}

	public CustodiaResponse purgarDocumento(
			String codigoExterno) throws Exception {
		return parseResponse(
				getCustodiaPort().purgarDocumento_v2(
						usuario,
						password,
						codigoExterno));
	}

	public CustodiaResponse recuperarDocumento(
			String codigoExterno) throws Exception {
		return parseResponse(
				getCustodiaPort().recuperarDocumento_v2(
						usuario,
						password,
						codigoExterno));
	}

	public CustodiaResponse eliminarDocumento(String codigoExterno) throws Exception {
		return parseResponse(
				getCustodiaPort().eliminarDocumento_v2(
						usuario,
						password,
						codigoExterno));
	}

	public CustodiaResponse verificarDocumento(String codigoExterno) throws Exception {
		return parseResponse(
				getCustodiaPort().verificarDocumento_v2(
						usuario,
						password,
						codigoExterno));
	}

	public CustodiaResponse obtenerInformeDocumento(String codigoExterno) throws Exception {
		return parseResponse(
				getCustodiaPort().obtenerInformeDocumento_v2(
						usuario,
						password,
						codigoExterno));
	}

	public CustodiaResponse consultarDocumento(String codigoExterno) throws Exception {
		return parseResponse(
				getCustodiaPort().consultarDocumento_v2(
							usuario,
							password,
							codigoExterno));
	}

	public CustodiaResponse reservarDocumento(String codigoExterno) throws Exception {
		return parseResponse(
				getCustodiaPort().reservarDocumento(
						usuario,
						password,
						codigoExterno));
	}

	public CustodiaResponse consultarReservaDocumento(String hash) throws Exception {
		return parseResponse(
				getCustodiaPort().consultarReservaDocumento(
					usuario,
					password,
					hash));
	}



	/*
	 * Els missatges de resposta son XML amb el següent format:
	 * <Operacion>
	 *   <Result>
	 *     <ResultMajor></ResultMajor>
	 *     <ResultMinor></ResultMinor>
	 *     <ResultMessage></ResultMessge>
	 *   </Result>
	 *   <InformacionAdicionalOperacion>[...]</InformacionAdicionalOperacion>
	 * </Operacion>
	 */
	private CustodiaResponse parseResponse(byte[] bytes) throws DocumentException {
		CustodiaResponse response = new CustodiaResponse(bytes);
		if (response.isXml()) {
			Document document = DocumentHelper.parseText(new String(bytes));
			Element resultMajorElement = null;
			Element resultMinorElement = null;
			Element resultMessageElement = null;
			if ("CustodiaResponse".equals(document.getRootElement().getName())) {
				resultMajorElement = document.getRootElement().element("VerifyResponse").element("Result").element("ResultMajor");
				resultMinorElement = document.getRootElement().element("VerifyResponse").element("Result").element("ResultMinor");
				resultMessageElement = document.getRootElement().element("VerifyResponse").element("Result").element("ResultMessage");
			} else {
				resultMajorElement = document.getRootElement().element("Result").element("ResultMajor");
				resultMinorElement = document.getRootElement().element("Result").element("ResultMinor");
				resultMessageElement = document.getRootElement().element("Result").element("ResultMessage");
			}
			if (resultMajorElement != null)
				response.setResultMajor(resultMajorElement.getText());
			if (resultMinorElement != null)
				response.setResultMinor(resultMinorElement.getText());
			if (resultMessageElement != null)
				response.setResultMessage(resultMessageElement.getText());
		} else {
			
		}
		return response;
	}

	private Custodia_PortType getCustodiaPort() throws Exception {
		if (custodiaPort == null) {
			service = new CustodiaServiceLocator();
			custodiaPort = service.getCustodia(new URL(url));
			((CustodiaSoapBindingStub)custodiaPort).setUsername(usuario);
			((CustodiaSoapBindingStub)custodiaPort).setPassword(password);
		}
		return custodiaPort;
	}

	public class CustodiaResponse {
		private byte[] bytes;
		private String resultMajor;
		private String resultMinor;
		private String resultMessage;
		public CustodiaResponse(byte[] bytes) {
			this.bytes = bytes;
		}
		public byte[] getBytes() {
			return bytes;
		}
		public String getResultMajor() {
			return resultMajor;
		}
		public void setResultMajor(String resultMajor) {
			this.resultMajor = resultMajor;
		}
		public String getResultMinor() {
			return resultMinor;
		}
		public void setResultMinor(String resultMinor) {
			this.resultMinor = resultMinor;
		}
		public String getResultMessage() {
			return resultMessage;
		}
		public void setResultMessage(String resultMessage) {
			this.resultMessage = resultMessage;
		}
		public boolean isXml() {
			return bytes[0] == '<' && bytes[bytes.length - 1] == '>';
		}
		public String getBytesAsString() {
			return new String(bytes);
		}
		public boolean isError() {
			return (resultMajor != null && (
					resultMajor.contains("error") ||
					resultMajor.contains("Error") ||
					resultMajor.contains("ERROR")));
		}
		public String errorToString() {
			return	"\n" +
					"\tresultMajor: " + resultMajor + "\n" +
					"\tresultMinor: " + resultMinor + "\n" +
					"\tresultMessage: " + resultMessage;
		}
	}

}
