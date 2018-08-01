/**
 * 
 */
package es.caib.ripea.plugin.caib.registre;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.caib.regweb3.ws.api.v3.AnexoWs;
import es.caib.regweb3.ws.api.v3.DatosInteresadoWs;
import es.caib.regweb3.ws.api.v3.InteresadoWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroEntradaWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroEntradaWsService;
import es.caib.regweb3.ws.api.v3.RegWebRegistroSalidaWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroSalidaWsService;
import es.caib.regweb3.ws.api.v3.RegistroEntradaResponseWs;
import es.caib.regweb3.ws.api.v3.RegistroResponseWs;
import es.caib.regweb3.ws.api.v3.RegistroSalidaResponseWs;
import es.caib.ripea.core.api.registre.RegistreAnnex;
import es.caib.ripea.core.api.registre.RegistreAnotacio;
import es.caib.ripea.core.api.registre.RegistreInteressat;
import es.caib.ripea.core.api.registre.RegistreTipusEnum;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.registre.RegistreAnotacioResposta;
import es.caib.ripea.plugin.registre.RegistrePlugin;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Implementació per a l'aplicació REGWEB3 del plugin de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistrePluginRegweb3 implements RegistrePlugin {

	@Override
	public RegistreAnotacioResposta entradaConsultar(
			String identificador,
			String usuariCodi,
			String entitat) throws SistemaExternException {
		String requestUser = getRequestUser();
		if (requestUser == null || requestUser.isEmpty())
			requestUser = usuariCodi;
		try {
			RegistroEntradaResponseWs registro = getRegistroEntradaWs().obtenerRegistroEntrada(
					identificador,
					requestUser,
					entitat);
			RegistreAnotacioResposta resposta = new RegistreAnotacioResposta();
			resposta.setTipus(RegistreTipusEnum.ENTRADA);
			resposta.setUnitatAdministrativa(registro.getDestinoCodigo());
			resposta.setRegistreAnotacio(toRegistreAnotacio(registro));
			return resposta;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar l'anotació d'entrada (" +
					"identificador=" + identificador + "," +
					"usuari=" + requestUser + "," +
					"entitat=" + entitat + ")",
					ex);
		}
	}

	@Override
	public RegistreAnotacioResposta sortidaConsultar(
			String identificador,
			String usuariCodi,
			String entitat) throws SistemaExternException {
		String requestUser = getRequestUser();
		if (requestUser == null || requestUser.isEmpty())
			requestUser = usuariCodi;
		try {
			RegistroSalidaResponseWs registro = getRegistroSalidaWs().obtenerRegistroSalida(
					identificador,
					usuariCodi,
					entitat);
			RegistreAnotacioResposta resposta = new RegistreAnotacioResposta();
			resposta.setTipus(RegistreTipusEnum.SORTIDA);
			resposta.setUnitatAdministrativa(registro.getOrigenCodigo());
			resposta.setRegistreAnotacio(toRegistreAnotacio(registro));
			return resposta;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar l'anotació de sortida (" +
							"identificador=" + identificador + "," +
							"usuari=" + requestUser + "," +
							"entitat=" + entitat + ")",
							ex);
		}
	}



	private RegistreAnotacio toRegistreAnotacio(
			RegistroResponseWs registro) {
		RegistreAnotacio anotacio = new RegistreAnotacio();
		anotacio.setNumero(String.valueOf(registro.getNumeroRegistro()));
		anotacio.setData(registro.getFechaRegistro());
		anotacio.setIdentificador(registro.getNumeroRegistroFormateado());
		anotacio.setEntitatCodi(registro.getEntidadCodigo());
		anotacio.setEntitatDescripcio(registro.getEntidadDenominacion());
		anotacio.setOficinaCodi(registro.getOficinaCodigo());
		anotacio.setOficinaDescripcio(registro.getOficinaDenominacion());
		anotacio.setLlibreCodi(registro.getLibroCodigo());
		anotacio.setLlibreDescripcio(registro.getLibroDescripcion());
		anotacio.setExtracte(registro.getExtracto());
		anotacio.setAssumpteCodi(registro.getCodigoAsuntoCodigo());
		anotacio.setAssumpteDescripcio(registro.getCodigoAsuntoDescripcion());
		anotacio.setAssumpteTipusCodi(registro.getTipoAsuntoCodigo());
		anotacio.setAssumpteTipusDescripcio(registro.getTipoAsuntoDescripcion());
		anotacio.setReferencia(registro.getRefExterna());
		anotacio.setExpedientNumero(registro.getNumExpediente());
		anotacio.setIdiomaCodi(registro.getIdiomaCodigo());
		anotacio.setIdiomaDescripcio(registro.getIdiomaDescripcion());
		anotacio.setTransportTipusCodi(registro.getTipoTransporteCodigo());
		anotacio.setTransportTipusDescripcio(registro.getTipoTransporteDescripcion());
		anotacio.setTransportNumero(registro.getNumTransporte());
		anotacio.setUsuariCodi(registro.getCodigoUsuario());
		anotacio.setUsuariNom(registro.getNombreUsuario());
		anotacio.setUsuariContacte(registro.getContactoUsuario());
		anotacio.setAplicacioCodi(registro.getAplicacion());
		anotacio.setAplicacioVersio(registro.getVersion());
		anotacio.setDocumentacioFisicaCodi(registro.getDocFisicaCodigo());
		anotacio.setDocumentacioFisicaDescripcio(registro.getDocFisicaDescripcion());
		anotacio.setObservacions(registro.getObservaciones());
		anotacio.setExposa(registro.getExpone());
		anotacio.setSolicita(registro.getSolicita());
		if (registro.getInteresados() != null) {
			List<RegistreInteressat> interessats = new ArrayList<RegistreInteressat>();
			for (InteresadoWs interesado: registro.getInteresados()) {
				RegistreInteressat interessat = toInteressat(
						interesado.getInteresado());
				if (interesado.getRepresentante() != null) {
					RegistreInteressat representant = toInteressat(
							interesado.getRepresentante());
					interessat.setRepresentant(representant);
				}
				interessats.add(interessat);
			}
			anotacio.setInteressats(interessats);
		}
		if (registro.getAnexos() != null) {
			List<RegistreAnnex> annexos = new ArrayList<RegistreAnnex>();
			for (AnexoWs anexo: registro.getAnexos()) {
				RegistreAnnex annex = new RegistreAnnex();
				annex.setTitol(anexo.getTitulo());
				annex.setFitxerNom(anexo.getNombreFicheroAnexado());
				annex.setFitxerTipusMime(anexo.getTipoMIMEFicheroAnexado());
				annex.setEniTipusDocumental(anexo.getTipoDocumental());
				annex.setEniEstatElaboracio(anexo.getValidezDocumento());
				annex.setSicresTipusDocument(anexo.getTipoDocumento());
				annex.setObservacions(anexo.getObservaciones());
				if (anexo.getOrigenCiudadanoAdmin() != null)
					annex.setEniOrigen(anexo.getOrigenCiudadanoAdmin().toString());
				annex.setEniDataCaptura(anexo.getFechaCaptura());
				annexos.add(annex);
			}
			anotacio.setAnnexos(annexos);
		}
		return anotacio;
	}

	private RegistreInteressat toInteressat(DatosInteresadoWs interesado) {
		RegistreInteressat interessat = new RegistreInteressat();
		if (interesado.getTipoInteresado() != null)
			interessat.setTipus(interesado.getTipoInteresado().toString());
		interessat.setDocumentTipus(interesado.getTipoDocumentoIdentificacion());
		interessat.setDocumentNum(interesado.getDocumento());
		interessat.setNom(interesado.getNombre());
		interessat.setLlinatge1(interesado.getApellido1());
		interessat.setLlinatge2(interesado.getApellido2());
		interessat.setRaoSocial(interesado.getRazonSocial());
		if (interesado.getPais() != null)
			interessat.setPais(interesado.getPais().toString());
		if (interesado.getProvincia() != null)
			interessat.setProvincia(interesado.getProvincia().toString());
		if (interesado.getLocalidad() != null)
			interessat.setMunicipi(interesado.getLocalidad().toString());
		interessat.setAdresa(interesado.getDireccion());
		interessat.setCodiPostal(interesado.getCp());
		interessat.setEmail(interesado.getEmail());
		interessat.setTelefon(interesado.getTelefono());
		interessat.setEmailHabilitat(
				interesado.getDireccionElectronica());
		if (interesado.getCanal() != null)
			interessat.setCanalPreferent(interesado.getCanal().toString());
		interessat.setObservacions(interesado.getObservaciones());
		return interessat;
	}

	/*private void arreglarAnotacio(
			RegistreAnotacio anotacio) {
		anotacio.setIdioma("ca");
		anotacio.setDocumentacioFisica("1");
		anotacio.setLlibre("1234");
		//anotacio.setAssumpteTipus("");
		anotacio.setOficina("O00009560");
		anotacio.setUnitatAdministrativa("A04013612");
		anotacio.setData(new Date(0));
	}*/

	private RegWebRegistroEntradaWs getRegistroEntradaWs() throws MalformedURLException {
		String webServiceUrl = getBaseUrl() + "/ws/v3/RegWebRegistroEntrada";
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		RegWebRegistroEntradaWsService service = new RegWebRegistroEntradaWsService(wsdlUrl);
		RegWebRegistroEntradaWs api = service.getRegWebRegistroEntradaWs();
		BindingProvider bp = (BindingProvider)api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				webServiceUrl);
		reqContext.put(
				BindingProvider.USERNAME_PROPERTY,
				getUsername());
		reqContext.put(
				BindingProvider.PASSWORD_PROPERTY,
				getPassword());
		if (isLogMissatgesActiu()) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		return api;
	}

	private RegWebRegistroSalidaWs getRegistroSalidaWs() throws MalformedURLException {
		String webServiceUrl = getBaseUrl() + "/ws/v3/RegWebRegistroSalida";
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		RegWebRegistroSalidaWsService service = new RegWebRegistroSalidaWsService(wsdlUrl);
		RegWebRegistroSalidaWs api = service.getRegWebRegistroSalidaWs();
		BindingProvider bp = (BindingProvider)api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				webServiceUrl);
		reqContext.put(
				BindingProvider.USERNAME_PROPERTY,
				getUsername());
		reqContext.put(
				BindingProvider.PASSWORD_PROPERTY,
				getPassword());
		if (isLogMissatgesActiu()) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		return api;
	}

	/*private RegWebInfoWs getInfoWs() throws MalformedURLException {
		String webServiceUrl = getBaseUrl() + "/ws/v3/RegWebInfo";
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		RegWebInfoWsService service = new RegWebInfoWsService(wsdlUrl);
		RegWebInfoWs api = service.getRegWebInfoWs();
		BindingProvider bp = (BindingProvider)api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				webServiceUrl);
		reqContext.put(
				BindingProvider.USERNAME_PROPERTY,
				getUsername());
		reqContext.put(
				BindingProvider.PASSWORD_PROPERTY,
				getPassword());
		if (isLogMissatgesActiu()) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		return api;
	}*/

	private String getBaseUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.registre.regweb3.base.url");
	}
	private String getUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.registre.regweb3.username");
	}
	private String getPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.registre.regweb3.password");
	}
	private String getRequestUser() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.registre.regweb3.request.user");
	}
	private boolean isLogMissatgesActiu() {
		return PropertiesHelper.getProperties().getAsBoolean(
				"es.caib.ripea.plugin.registre.regweb3.log.actiu");
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

}
