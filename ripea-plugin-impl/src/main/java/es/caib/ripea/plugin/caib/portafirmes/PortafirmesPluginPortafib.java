/**
 *
 */
package es.caib.ripea.plugin.caib.portafirmes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.fundaciobit.apisib.apiflowtemplatesimple.v1.ApiFlowTemplateSimple;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplate;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleGetFlowResultResponse;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleGetTransactionIdRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleStatus;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.jersey.ApiFlowTemplateSimpleJersey;
import org.fundaciobit.apisib.core.exceptions.ApisIBClientException;
import org.fundaciobit.apisib.core.exceptions.ApisIBServerException;
import org.fundaciobit.apisib.core.exceptions.ApisIBTimeOutException;
import org.slf4j.LoggerFactory;

import es.caib.portafib.ws.api.v1.FitxerBean;
import es.caib.portafib.ws.api.v1.FluxDeFirmesWs;
import es.caib.portafib.ws.api.v1.PeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWsService;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWs;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWsService;
import es.caib.portafib.ws.api.v1.TipusDocumentInfoWs;
import es.caib.portafib.ws.api.v1.utils.PeticioDeFirmaUtils;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentTipus;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxBloc;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxErrorTipus;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Implementació del plugin de portafirmes emprant el portafirmes
 * de la CAIB desenvolupat per l'IBIT (PortaFIB).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesPluginPortafib implements PortafirmesPlugin {

	@Override
	public String upload(
			PortafirmesDocument document,
			String documentTipus,
			String motiu,
			String remitent,
			PortafirmesPrioritatEnum prioritat,
			Date dataCaducitat,
			List<PortafirmesFluxBloc> flux,
			String plantillaFluxId,
			List<PortafirmesDocument> annexos,
			boolean signarAnnexos) throws SistemaExternException {
		try {
			PeticioDeFirmaWs requestPeticioDeFirmaWs = new PeticioDeFirmaWs();
			requestPeticioDeFirmaWs.setTitol(document.getTitol());
			requestPeticioDeFirmaWs.setDescripcio(document.getDescripcio());
			requestPeticioDeFirmaWs.setMotiu(motiu);
			requestPeticioDeFirmaWs.setRemitentNom(remitent);
			if (prioritat != null) {
				switch (prioritat) {
				case BAIXA:
					requestPeticioDeFirmaWs.setPrioritatID(0);
					break;
				case NORMAL:
					requestPeticioDeFirmaWs.setPrioritatID(5);
					break;
				case ALTA:
					requestPeticioDeFirmaWs.setPrioritatID(9);
				}
			}
			GregorianCalendar gcal = new GregorianCalendar();
			gcal.setTime(dataCaducitat);
			DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
			requestPeticioDeFirmaWs.setDataCaducitat(
					new java.sql.Timestamp(dataCaducitat.getTime()));
			requestPeticioDeFirmaWs.setFluxDeFirmes(
					toFluxDeFirmes(
							flux,
							plantillaFluxId));
			requestPeticioDeFirmaWs.setFitxerAFirmar(
					toFitxerBean(document));
			requestPeticioDeFirmaWs.setTipusDocumentID(
					new Long(documentTipus));
			requestPeticioDeFirmaWs.setModeDeFirma(
					new Boolean(false));
			requestPeticioDeFirmaWs.setIdiomaID("ca");
			PeticioDeFirmaWs responsePeticioDeFirmaWs = getPeticioDeFirmaWs().createAndStartPeticioDeFirma(
					requestPeticioDeFirmaWs);
			return new Long(responsePeticioDeFirmaWs.getPeticioDeFirmaID()).toString();
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut pujar el document al portafirmes (" +
					"titol=" + document.getTitol() + ", " +
					"descripcio=" + document.getDescripcio() + ", " +
					"arxiuNom=" + document.getArxiuNom() + ")",
					ex);
		}
	}

	@Override
	public PortafirmesDocument download(
			String id) throws SistemaExternException {
		try {
			FitxerBean fitxerFirmat = getPeticioDeFirmaWs().getLastSignedFileOfPeticioDeFirma(
					new Long(id).longValue());
			FitxerBean fitxerDescarregat = getPeticioDeFirmaWs().downloadFileUsingEncryptedFileID(
					fitxerFirmat.getEncryptedFileID());
			PortafirmesDocument downloadedDocument = new PortafirmesDocument();
			downloadedDocument.setArxiuNom(fitxerDescarregat.getNom());
			downloadedDocument.setArxiuContingut(fitxerDescarregat.getData());
			downloadedDocument.setFirmat(downloadedDocument.isFirmat());
			return downloadedDocument;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut descarregar el document del portafirmes (id=" + id + ")",
					ex);
		}
	}

	@Override
	public void delete(
			String id) throws SistemaExternException {
		try {
			getPeticioDeFirmaWs().deletePeticioDeFirma(
					new Long(id).longValue());
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut esborrar el document del portafirmes (id=" + id + ")",
					ex);
		}
	}

	@Override
	public List<PortafirmesDocumentTipus> findDocumentTipus() throws SistemaExternException {
		try {
			List<TipusDocumentInfoWs> tipusLlistat = getPeticioDeFirmaWs().getTipusDeDocuments("ca");
			List<PortafirmesDocumentTipus> resposta = new ArrayList<PortafirmesDocumentTipus>();
			for (TipusDocumentInfoWs tipusDocumentWs: tipusLlistat) {
				PortafirmesDocumentTipus tipusDocument = new PortafirmesDocumentTipus();
				tipusDocument.setId(tipusDocumentWs.getTipusDocumentID());
				tipusDocument.setNom(tipusDocumentWs.getNom());
				resposta.add(tipusDocument);
			}
			return resposta;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut obtenir la llista de tipus de documents del portafirmes",
					ex);
		}
	}

	@Override
	public boolean isCustodiaAutomatica() {
		return false;
	}

	@Override
	public Map<String, String> iniciarFluxDeFirma(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible,
			String urlReturn) throws SistemaExternException {
		Map<String, String> transaccioResponse = new HashMap<String, String>();
		try {
			String idTransaccio = getTransaction(
					idioma,
					isPlantilla,
					nom,
					descripcio,
					descripcioVisible);

			String urlRedireccio = startTransaction(
					idTransaccio,
					urlReturn + idTransaccio);
			transaccioResponse.put("idTransaccio", idTransaccio);
			transaccioResponse.put("urlRedireccio", urlRedireccio);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return transaccioResponse;
	}

	@Override
	public PortafirmesFluxResposta recuperarFluxDeFirma(String idTransaccio) throws SistemaExternException {
		PortafirmesFluxResposta resposta = new PortafirmesFluxResposta();
		try {
			FlowTemplateSimpleGetFlowResultResponse result = getFlowTemplateResult(idTransaccio);
			FlowTemplateSimpleStatus transactionStatus = result.getStatus();
			int status = transactionStatus.getStatus();

			switch (status) {

				case FlowTemplateSimpleStatus.STATUS_INITIALIZING:
					{
						resposta.setError(true);
						resposta.setErrorTipus(PortafirmesFluxErrorTipus.INITIALIZING);
						logger.error("S'ha rebut un estat inconsistent del procés de construcció del flux."
								+ " (Inialitzant). Consulti amb el seu administrador.");
						return resposta;
					}
				case FlowTemplateSimpleStatus.STATUS_IN_PROGRESS:
					{
						resposta.setError(true);
						resposta.setErrorTipus(PortafirmesFluxErrorTipus.IN_PROGRESS);
						logger.error("S'ha rebut un estat inconsistent de construcció "
								+ "del flux (En Progrés). Consulti amb el seu administrador.");
						return resposta;
					}
				case FlowTemplateSimpleStatus.STATUS_FINAL_ERROR:
					{
						String desc = transactionStatus.getErrorStackTrace();
						resposta.setError(true);
						resposta.setErrorTipus(PortafirmesFluxErrorTipus.FINAL_ERROR);

						if (desc != null) {
							logger.error(desc);
						}
						logger.error("Error durant la construcció del flux: " + transactionStatus.getErrorMessage());
						return resposta;
					}
				case FlowTemplateSimpleStatus.STATUS_CANCELLED:
					{
						resposta.setError(true);
						resposta.setErrorTipus(PortafirmesFluxErrorTipus.CANCELLED);
						logger.error("L'usuari ha cancelat la construcció del flux");
						return resposta;
					}
				case FlowTemplateSimpleStatus.STATUS_FINAL_OK:
					{
						FlowTemplateSimpleFlowTemplate flux = result.getFlowInfo();

						resposta.setError(false);
						resposta.setFluxId(flux.getIntermediateServerFlowTemplateId());
					}
					break;

				default: {
					throw new Exception("Codi d'estat desconegut (" + status + ")");
				}
			}
		} catch (ApisIBClientException ex) {
			throw new SistemaExternException(
					"S'ha produït un error en el ConnectionManager del Client",
					ex);
		} catch (ApisIBServerException ex) {
			throw new SistemaExternException(
					"S'ha produït un error indeterminat al Servidor",
					ex);
		} catch (ApisIBTimeOutException ex) {
			throw new SistemaExternException(
					"Problemes de comunicació amb el servidor intermedi",
					ex);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error en el ConnectionManager del Client",
					ex);
		} finally {
			try {
				closeTransaction(idTransaccio);
			} catch (Exception ex) {
				throw new SistemaExternException(
						"S'ha produït un error tancant la transacció",
						ex);
			}
		}
		return resposta;
	}

	public void tancarTransaccioFlux (String idTransaccio) throws SistemaExternException {
		try {
			closeTransaction(idTransaccio);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error tancant la transacció",
					ex);
		}
	}

	private String getTransaction(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible) throws SistemaExternException {
		String transactionId = null;
		FlowTemplateSimpleGetTransactionIdRequest transactionRequest;

		try {
			transactionRequest = new FlowTemplateSimpleGetTransactionIdRequest(
					idioma,
					isPlantilla,
					nom,
					descripcio,
					descripcioVisible);

			transactionId = getFluxDeFirmaClient().getTransactionID(transactionRequest);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut recuperar el id de la transacció (" +
					"nom=" + nom + ", " +
					"descripcio=" + descripcio + ")",
					ex);
		}
		return transactionId;
	}

	private String startTransaction(
			String idTransaccio,
			String urlReturn) throws SistemaExternException {
		String urlRedireccio = null;
		FlowTemplateSimpleStartTransactionRequest transactionRequest;

		try {
			transactionRequest = new FlowTemplateSimpleStartTransactionRequest(
					idTransaccio,
					urlReturn);

			urlRedireccio = getFluxDeFirmaClient().startTransaction(transactionRequest);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut iniciar la transacció (" +
					"transactionId=" + idTransaccio + ", " +
					"returnUrl=" + urlReturn + ")",
					ex);
		}
		return urlRedireccio;
	}

	private FlowTemplateSimpleGetFlowResultResponse getFlowTemplateResult(
			String transactionID) {
		FlowTemplateSimpleGetFlowResultResponse result = null;

		try {
			result = getFluxDeFirmaClient().getFlowTemplateResult(transactionID);
			System.out.println(result.getStatus());
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return result;
	}

	private void closeTransaction(
			String transactionID) {
		try {
			getFluxDeFirmaClient().closeTransaction(transactionID);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	private ApiFlowTemplateSimple getFluxDeFirmaClient() throws MalformedURLException {
		String apiRestUrl = getBaseUrl() + "/common/rest/apiflowtemplatesimple/v1";
		ApiFlowTemplateSimple api = new ApiFlowTemplateSimpleJersey(
				apiRestUrl,
				getUsername(),
				getPassword());
		return api;
	}

	private FitxerBean toFitxerBean(
			PortafirmesDocument document) throws Exception {
		if (!"pdf".equalsIgnoreCase(document.getArxiuExtensio())) {
			throw new SistemaExternException(
					"Els arxius per firmar han de ser de tipus PDF");
		}
		FitxerBean fitxer = new FitxerBean();
		fitxer.setNom(document.getArxiuNom());
		fitxer.setMime("application/pdf");
		fitxer.setTamany(document.getArxiuContingut().length);
		fitxer.setData(document.getArxiuContingut());
		return fitxer;
	}

	private FluxDeFirmesWs toFluxDeFirmes(
			List<PortafirmesFluxBloc> flux,
			String plantillaFluxId) throws Exception {
		if (flux == null && plantillaFluxId == null) {
			throw new SistemaExternException(
					"No s'ha especificat cap flux de firma");
		}
		FluxDeFirmesWs fluxWs;
		if (plantillaFluxId != null && flux == null) {
			fluxWs = getPeticioDeFirmaWs().instantiatePlantillaFluxDeFirmes(
					new Long(plantillaFluxId).longValue());
		} else {
			int numNifs = 0;
			if (flux.size() > 0) {
				numNifs = flux.get(0).getDestinataris().length;
			}
			String[][] nifs = new String[flux.size()][numNifs];
			for (int i = 0; i < flux.size(); i++) {
				PortafirmesFluxBloc fluxBloc = flux.get(i);
				for (int j = 0; j < fluxBloc.getDestinataris().length; j++) {
					nifs[i][j] = fluxBloc.getDestinataris()[j];
				}
			}
			fluxWs = PeticioDeFirmaUtils.constructFluxDeFirmesWsUsingBlocDeFirmes(
					getUsuariEntitatWs(),
					nifs);
			/*fluxWs = new FluxDeFirmesWs();
			fluxWs.setNom("qwerty");
			int index = 0;
			for (PortafirmesFluxBloc fluxBloc: flux) {
				BlocDeFirmesWs blocWs = new BlocDeFirmesWs();
				blocWs.setMinimDeFirmes(fluxBloc.getMinSignataris());
				blocWs.setOrdre(index);
				if (fluxBloc.getDestinataris() != null) {
					for (int i = 0; i < fluxBloc.getDestinataris().length; i++) {
						FirmaBean firma = new FirmaBean();
						firma.setDestinatariID(fluxBloc.getDestinataris()[i]);
						firma.setObligatori(fluxBloc.getObligatorietats()[i]);
						blocWs.getFirmes().add(firma);
					}
				}
				fluxWs.getBlocsDeFirmes().add(blocWs);
				index++;
			}*/
		}
		return fluxWs;
	}

	/*private XMLGregorianCalendar toXMLGregorianCalendar(
			Date date) throws Exception {
		if (date == null)
			return null;
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	}*/

	private PortaFIBPeticioDeFirmaWs getPeticioDeFirmaWs() throws MalformedURLException {
		String webServiceUrl = getBaseUrl() + "/ws/v1/PortaFIBPeticioDeFirma";
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		PortaFIBPeticioDeFirmaWsService service = new PortaFIBPeticioDeFirmaWsService(wsdlUrl);
		PortaFIBPeticioDeFirmaWs api = service.getPortaFIBPeticioDeFirmaWs();
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

	private PortaFIBUsuariEntitatWs getUsuariEntitatWs() throws MalformedURLException {
		String webServiceUrl = getBaseUrl() + "/ws/v1/PortaFIBUsuariEntitat";
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		PortaFIBUsuariEntitatWsService service = new PortaFIBUsuariEntitatWsService(wsdlUrl);
		PortaFIBUsuariEntitatWs api = service.getPortaFIBUsuariEntitatWs();
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

	private String getBaseUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.base.url");
	}
	private String getUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.username");
	}
	private String getPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.password");
	}
	private boolean isLogMissatgesActiu() {
		return PropertiesHelper.getProperties().getAsBoolean(
				"es.caib.ripea.plugin.portafirmes.portafib.log.actiu");
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
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PortafirmesPluginPortafib.class);

}
