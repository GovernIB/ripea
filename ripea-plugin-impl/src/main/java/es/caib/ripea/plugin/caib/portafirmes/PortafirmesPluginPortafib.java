/**
 *
 */
package es.caib.ripea.plugin.caib.portafirmes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

import org.fundaciobit.apisib.apifirmaasyncsimple.v2.ApiFirmaAsyncSimple;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleAnnex;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleDocumentTypeInformation;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleExternalSigner;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleFile;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleReviser;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignature;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureBlock;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestInfo;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestWithFlowTemplateCode;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestWithSignBlockList;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignedFile;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSigner;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignerInfo;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.jersey.ApiFirmaAsyncSimpleJersey;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.ApiFlowTemplateSimple;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleBlock;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleEditFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleExternalSigner;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFilterGetAllByFilter;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplate;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplateList;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleGetFlowResultResponse;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleGetTransactionIdRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleKeyValue;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleReviser;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleSignature;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleStatus;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleViewFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.jersey.ApiFlowTemplateSimpleJersey;
import org.fundaciobit.apisib.core.exceptions.ApisIBClientException;
import org.fundaciobit.apisib.core.exceptions.ApisIBServerException;
import org.fundaciobit.apisib.core.exceptions.ApisIBTimeOutException;
import org.slf4j.LoggerFactory;

import es.caib.portafib.ws.api.v1.BlocDeFirmesWs;
import es.caib.portafib.ws.api.v1.CarrecWs;
import es.caib.portafib.ws.api.v1.FirmaBean;
import es.caib.portafib.ws.api.v1.FluxDeFirmesWs;
import es.caib.portafib.ws.api.v1.PeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWsService;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWs;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWsService;
import es.caib.portafib.ws.api.v1.UsuariEntitatBean;
import es.caib.portafib.ws.api.v1.UsuariPersonaBean;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.portafirmes.PortafirmesBlockInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesBlockSignerInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesCarrec;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentFirmant;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentTipus;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxBloc;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxEstat;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxInfo;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesIniciFluxResposta;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.plugin.PropertiesHelper;

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
			boolean signarAnnexos,
			String idTransaccio) throws SistemaExternException {
		try {
			long peticioDeFirmaId = 0;
//			FirmaAsyncSimpleSignatureRequestWithSignBlockList signatureRequest = new FirmaAsyncSimpleSignatureRequestWithSignBlockList();
			FirmaAsyncSimpleSignatureRequestWithFlowTemplateCode signatureRequest = new FirmaAsyncSimpleSignatureRequestWithFlowTemplateCode();
			signatureRequest.setTitle(document.getTitol());
			signatureRequest.setDescription(document.getDescripcio());
			signatureRequest.setReason(motiu);
			signatureRequest.setSenderName(remitent);
			if (prioritat != null) {
				switch (prioritat) {
				case BAIXA:
					signatureRequest.setPriority(0);
					break;
				case NORMAL:
					signatureRequest.setPriority(5);
					break;
				case ALTA:
					signatureRequest.setPriority(9);
				}
			}
			//Caducitat??
			signatureRequest.setAdditionalInformation(null);
			
			signatureRequest.setFileToSign(toFirmaAsyncSimpleFile(document));
			signatureRequest.setDocumentType(new Long(documentTipus));
			signatureRequest.setLanguageUI("ca");
			signatureRequest.setLanguageDoc("ca");
			signatureRequest.setProfileCode(getPerfil());
			
			if (annexos != null) {
				List<FirmaAsyncSimpleAnnex> portafirmesAnnexos = new ArrayList<FirmaAsyncSimpleAnnex>();
				
				for (PortafirmesDocument annex : annexos) {
					FirmaAsyncSimpleAnnex portafirmesAnnex = new FirmaAsyncSimpleAnnex();
					portafirmesAnnex.setAnnex(toFirmaAsyncSimpleFile(annex));
					portafirmesAnnex.setAttach(false);
					portafirmesAnnex.setSign(false);
					portafirmesAnnexos.add(portafirmesAnnex);
				}
				signatureRequest.setAnnexs(portafirmesAnnexos);
			}
			FirmaAsyncSimpleSignatureBlock[] signatureBlocks  = null;
			if (plantillaFluxId != null || idTransaccio != null) {
//				### convertir en blocs de portafirmes a partir d'un id de transacció o d'una plantilla
				signatureBlocks = idTransaccio != null ? recuperarFluxDeFirma(idTransaccio) : toFirmaAsyncSimpleSignatureBlockFromId(plantillaFluxId, "ca");
			} else if (flux != null && ! flux.isEmpty()) {
//				### convertir en blocs de portafirme a partir d'un llistat de destinataris
				signatureBlocks = simpleBlockToPortafirmesBlock(flux);
			}
			signatureRequest.setFlowTemplateCode("23987518");
//			signatureRequest.setSignatureBlocks(signatureBlocks);
			if (isEnviarUrlExpedientPermitida())
				signatureRequest.setExpedientUrl(getUrlExpedient() + document.getExpedientUuid());
			
			peticioDeFirmaId = getFirmaAsyncSimpleApi().createAndStartSignatureRequestWithFlowTemplateCode(signatureRequest);
					//createAndStartSignatureRequestWithSignBlockList(signatureRequest);
			return new Long(peticioDeFirmaId).toString();
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
			FirmaAsyncSimpleSignatureRequestInfo requestInfo = new FirmaAsyncSimpleSignatureRequestInfo(new Long(id).longValue(), "ca");
			FirmaAsyncSimpleSignedFile fitxerDescarregat = getFirmaAsyncSimpleApi().getSignedFileOfSignatureRequest(requestInfo);
			
			PortafirmesDocument downloadedDocument = new PortafirmesDocument();
			downloadedDocument.setArxiuNom(fitxerDescarregat.getSignedFile().getNom());
			downloadedDocument.setArxiuMime(fitxerDescarregat.getSignedFile().getMime());
			downloadedDocument.setArxiuContingut(fitxerDescarregat.getSignedFile().getData());
			downloadedDocument.setTipusFirma(fitxerDescarregat.getSignedFileInfo().getSignType());
			
			List<PortafirmesDocumentFirmant> firmants = new ArrayList<PortafirmesDocumentFirmant>();
			for (FirmaAsyncSimpleSignerInfo signer: fitxerDescarregat.getSignedFileInfo().getSignersInfo()) {
				PortafirmesDocumentFirmant firmant = new PortafirmesDocumentFirmant();
				firmant.setData(signer.getSignDate());
				firmant.setResponsableNif(signer.getEniSignerAdministrationId());
				firmant.setResponsableNom(signer.getEniSignerName());
				firmant.setEmissorCertificat(signer.getIssuerCert());
				firmants.add(firmant);
			}
			downloadedDocument.setFirmants(firmants);
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
			FirmaAsyncSimpleSignatureRequestInfo requestInfo = new FirmaAsyncSimpleSignatureRequestInfo(new Long(id).longValue(), "ca");
			getFirmaAsyncSimpleApi().deleteSignatureRequest(requestInfo);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut esborrar el document del portafirmes (id=" + id + ")",
					ex);
		}
	}

	@Override
	public List<PortafirmesDocumentTipus> findDocumentTipus() throws SistemaExternException {
		try {
			List<PortafirmesDocumentTipus> resposta = new ArrayList<PortafirmesDocumentTipus>();
			List<FirmaAsyncSimpleDocumentTypeInformation> tipusLlistat = getFirmaAsyncSimpleApi().getAvailableTypesOfDocuments("ca");
			for (FirmaAsyncSimpleDocumentTypeInformation tipusDocumentWs: tipusLlistat) {
				PortafirmesDocumentTipus tipusDocument = new PortafirmesDocumentTipus();
				tipusDocument.setId(tipusDocumentWs.getDocumentType());
				tipusDocument.setNom(tipusDocumentWs.getName());
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
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible,
			String urlReturn) throws SistemaExternException {
		PortafirmesIniciFluxResposta transaccioResponse = new PortafirmesIniciFluxResposta();
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
			transaccioResponse.setIdTransaccio(idTransaccio);
			transaccioResponse.setUrlRedireccio(urlRedireccio);

		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error iniciant la transacció: " + ex.getCause(),
					ex);
		}

		return transaccioResponse;
	}
	
	@Override
	public PortafirmesFluxResposta recuperarFluxDeFirmaByIdTransaccio(String idTransaccio) throws SistemaExternException {
		PortafirmesFluxResposta resposta = new PortafirmesFluxResposta();
		try {
			FlowTemplateSimpleGetFlowResultResponse result = getFlowTemplateResult(idTransaccio);
			FlowTemplateSimpleStatus transactionStatus = result.getStatus();
			int status = transactionStatus.getStatus();

			switch (status) {
				case FlowTemplateSimpleStatus.STATUS_INITIALIZING:
						resposta.setError(true);
						resposta.setEstat(PortafirmesFluxEstat.INITIALIZING);
						logger.error("S'ha rebut un estat inconsistent del procés de construcció del flux. (Inialitzant). Consulti amb el seu administrador.");
						return resposta;
				case FlowTemplateSimpleStatus.STATUS_IN_PROGRESS:
						resposta.setError(true);
						resposta.setEstat(PortafirmesFluxEstat.IN_PROGRESS);
						logger.error("S'ha rebut un estat inconsistent de construcció del flux (En Progrés). Consulti amb el seu administrador.");
						return resposta;
				case FlowTemplateSimpleStatus.STATUS_FINAL_ERROR:
						String desc = transactionStatus.getErrorStackTrace();
						resposta.setError(true);
						resposta.setEstat(PortafirmesFluxEstat.FINAL_ERROR);
						if (desc != null) {
							logger.error(desc);
						}
						logger.error("Error durant la construcció del flux: " + transactionStatus.getErrorMessage());
						return resposta;
				case FlowTemplateSimpleStatus.STATUS_CANCELLED:
						resposta.setError(true);
						resposta.setEstat(PortafirmesFluxEstat.CANCELLED);
						logger.error("L'usuari ha cancelat la construcció del flux");
						return resposta;
				case FlowTemplateSimpleStatus.STATUS_FINAL_OK:
						FlowTemplateSimpleFlowTemplate flux = result.getFlowInfo();

						resposta.setError(false);
						resposta.setEstat(PortafirmesFluxEstat.FINAL_OK);
						resposta.setFluxId(flux.getIntermediateServerFlowTemplateId());
						resposta.setNom(flux.getName());
						resposta.setDescripcio(flux.getDescription());
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
				if (resposta.getFluxId() != null)
					closeTransaction(idTransaccio);
			} catch (Exception ex) {
				throw new SistemaExternException(
						"S'ha produït un error tancant la transacció",
						ex);
			}
		}
		return resposta;
	}
	
	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesDisponibles(String idioma) throws SistemaExternException {
		List<PortafirmesFluxResposta> plantilles = new ArrayList<PortafirmesFluxResposta>();
		try {
			FlowTemplateSimpleFlowTemplateList resposta = getFluxDeFirmaClient().getAllFlowTemplates(idioma);
			
			for (FlowTemplateSimpleKeyValue flowTemplate : resposta.getList()) {
				PortafirmesFluxResposta plantilla = new PortafirmesFluxResposta();
				plantilla.setFluxId(flowTemplate.getKey());
				plantilla.setNom(flowTemplate.getValue());
				plantilles.add(plantilla);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut recuperar les plantilles per l'usuari aplicació actual",
					ex);
		}
		return plantilles;
	}
	
	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesPerFiltre(String idioma, String descripcio) throws SistemaExternException {
		List<PortafirmesFluxResposta> plantilles = new ArrayList<PortafirmesFluxResposta>();
		try {
			FlowTemplateSimpleFilterGetAllByFilter flowTemplateSimpleFilterGetAllByFilter = new FlowTemplateSimpleFilterGetAllByFilter(idioma, null, descripcio);
			
			FlowTemplateSimpleFlowTemplateList resposta = getFluxDeFirmaClient().getAllFlowTemplatesByFilter(flowTemplateSimpleFilterGetAllByFilter);
			
			for (FlowTemplateSimpleKeyValue flowTemplate : resposta.getList()) {
				PortafirmesFluxResposta plantilla = new PortafirmesFluxResposta();
				plantilla.setFluxId(flowTemplate.getKey());
				plantilla.setNom(flowTemplate.getValue());
				plantilles.add(plantilla);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut recuperar les plantilles per l'usuari aplicació actual",
					ex);
		}
		return plantilles;
	}

	@Override
	public void tancarTransaccioFlux (String idTransaccio) throws SistemaExternException {
		try {
			closeTransaction(idTransaccio);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error tancant la transacció",
					ex);
		}
	}

	@Override
	public PortafirmesFluxInfo recuperarFluxDeFirmaByIdPlantilla(
			String plantillaFluxId,
			String idioma) throws SistemaExternException {
		PortafirmesFluxInfo info = null;
		try {
			FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest(idioma, plantillaFluxId);

			FlowTemplateSimpleFlowTemplate result = getFluxDeFirmaClient().getFlowInfoByFlowTemplateID(request);
			
			if (result != null) {
				info = new PortafirmesFluxInfo();
				info.setNom(result.getName());
				info.setDescripcio(result.getDescription());
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error recuperant el detall del flux de firma",
					ex);
		}
		return info;
	}

	@Override
	public String recuperarUrlViewEditPlantilla (
			String idPlantilla, 
			String idioma,
			String urlReturn,
			boolean edicio) throws SistemaExternException {
		String urlPlantilla;
		try {
			if (!edicio) {
				FlowTemplateSimpleViewFlowTemplateRequest request = new FlowTemplateSimpleViewFlowTemplateRequest(idioma, idPlantilla);
				urlPlantilla = getFluxDeFirmaClient().getUrlToViewFlowTemplate(request);
			} else {
				FlowTemplateSimpleEditFlowTemplateRequest request = new FlowTemplateSimpleEditFlowTemplateRequest(idioma, idPlantilla, urlReturn);
				urlPlantilla = getFluxDeFirmaClient().getUrlToEditFlowTemplate(request);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut recuperar la url per visualitzar el flux de firma",
					ex);
		}
		return urlPlantilla;
	}
	
	@Override
	public boolean esborrarPlantillaFirma(String idioma, String plantillaFluxId) throws SistemaExternException {
		boolean esborrat = false;
		try {
			FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest(idioma, plantillaFluxId);
			esborrat = getFluxDeFirmaClient().deleteFlowTemplate(request);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Hi ha hagut un problema esborrant el flux de firma",
					ex);
		}
		return esborrat;
	}

	@Override
	public List<PortafirmesCarrec> recuperarCarrecs() throws SistemaExternException {
		List<PortafirmesCarrec> carrecs = new ArrayList<PortafirmesCarrec>();
		try {
			List<CarrecWs> carrecsWs = getUsuariEntitatWs().getCarrecsOfMyEntitat();
			if (carrecsWs != null) {
				for (CarrecWs carrecWs : carrecsWs) {
					PortafirmesCarrec carrec = new PortafirmesCarrec();
					carrec.setCarrecId(carrecWs.getCarrecID());
					carrec.setCarrecName(carrecWs.getCarrecName());
					carrec.setEntitatId(carrecWs.getEntitatID());
					carrec.setUsuariPersonaId(carrecWs.getUsuariPersonaID());
					UsuariPersonaBean usuariPersona = getUsuariEntitatWs().getUsuariPersona(carrecWs.getUsuariPersonaID());
					if (usuariPersona != null) {
						carrec.setUsuariPersonaNif(usuariPersona.getNif());
						carrec.setUsuariPersonaEmail(usuariPersona.getEmail());
						carrec.setUsuariPersonaNom(usuariPersona.getNom());
					} else {
						throw new SistemaExternException("No s'ha trobat cap usuari persona amb id " + carrecWs.getUsuariPersonaID() + " relacionat amb aquest càrrec");
					}
					carrecs.add(carrec);
				}
			}
			return carrecs;
		} catch (Exception ex) {
			throw new SistemaExternException("Hi ha hagut un problema recuperant els càrrecs per l'usuari aplicació " + getUsername(), ex);
		}
	}
	
	@Override
	public PortafirmesCarrec recuperarCarrec(String carrecId) throws SistemaExternException {
		PortafirmesCarrec carrec = new PortafirmesCarrec();
		try {
			CarrecWs carrecWs = getUsuariEntitatWs().getCarrec(carrecId);
			if (carrecWs != null) {
				carrec.setCarrecId(carrecWs.getCarrecID());
				carrec.setCarrecName(carrecWs.getCarrecName());
				carrec.setEntitatId(carrecWs.getEntitatID());
				carrec.setUsuariPersonaId(carrecWs.getUsuariPersonaID());
				UsuariPersonaBean usuariPersona = getUsuariEntitatWs().getUsuariPersona(carrecWs.getUsuariPersonaID());
				if (usuariPersona != null) {
					carrec.setUsuariPersonaNif(usuariPersona.getNif());
					carrec.setUsuariPersonaEmail(usuariPersona.getEmail());
					carrec.setUsuariPersonaNom(usuariPersona.getNom());
				} else {
					throw new SistemaExternException("No s'ha trobat cap usuari persona amb id " + carrecWs.getUsuariPersonaID() + " relacionat amb aquest càrrec");
				}
			}
			return carrec;
		} catch (Exception ex) {
			throw new SistemaExternException("Hi ha hagut un problema recuperant els càrrecs per l'usuari aplicació " + getUsername(), ex);
		}
	}

	@Override
	public List<PortafirmesBlockInfo> recuperarBlocksFirmes(
			String idPlantilla, 
			String idTransaccio, 
			boolean portafirmesFluxAsync,
			Long portafirmesId,
			String idioma)
			throws SistemaExternException {
		List<FlowTemplateSimpleBlock> blocks = null;
		try {
			if (portafirmesFluxAsync) {
				if (idPlantilla != null) {
					FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest();
					request.setFlowTemplateId(idPlantilla);
					request.setLanguageUI(idioma);
					
					FlowTemplateSimpleFlowTemplate resultPlantilla = getFluxDeFirmaClient().getFlowInfoByFlowTemplateID(request);
					blocks = resultPlantilla.getBlocks();
				}
				if (idTransaccio != null) {
					//sobreescriu l'anterior
					FlowTemplateSimpleGetFlowResultResponse resultTransaccio = getFluxDeFirmaClient().getFlowTemplateResult(idTransaccio);
					blocks = resultTransaccio.getFlowInfo().getBlocks();
					
					try {
						closeTransaction(idTransaccio);
					} catch (Exception ex) {
						throw new SistemaExternException("S'ha produit un error tancant la transacció", ex);
					}
				}
				return asyncBlockToPortafirmesBlockInfo(blocks);
			} else {
				PeticioDeFirmaWs peticioFirmaSimple = getPeticioDeFirmaWs().getPeticioDeFirma(portafirmesId);
				FluxDeFirmesWs fluxFirma = peticioFirmaSimple.getFluxDeFirmes();
				List<BlocDeFirmesWs> blocs = fluxFirma.getBlocsDeFirmes();
				return simpleBlockToPortafirmesBlockInfo(blocs);
			}
		} catch (Exception ex) {
			throw new SistemaExternException("S'ha produit un error transformant en l'objecte FirmaAsyncSimpleSignatureBlock[]", ex);
		}
	}

	private List<PortafirmesBlockInfo> simpleBlockToPortafirmesBlockInfo(List<BlocDeFirmesWs> blocks) throws SistemaExternException {
		List<PortafirmesBlockInfo> portafirmesBlocks = null;
		try {
			if (blocks != null) {
				Collections.sort(blocks, new Comparator<BlocDeFirmesWs>() {
					@Override
					public int compare(BlocDeFirmesWs o1, BlocDeFirmesWs o2) {
						if (o1.getOrdre() < o2.getOrdre())
							return -1;
						else
							return 1;
					}
				});
				portafirmesBlocks = new ArrayList<PortafirmesBlockInfo>();
				for (BlocDeFirmesWs blocDeFirmesWs : blocks) {
					PortafirmesBlockInfo portafirmesBlock = new PortafirmesBlockInfo();
					List<PortafirmesBlockSignerInfo> signers = new ArrayList<PortafirmesBlockSignerInfo>();
					
					for (FirmaBean firmaBean : blocDeFirmesWs.getFirmes()) {
						PortafirmesBlockSignerInfo signer = new PortafirmesBlockSignerInfo();
						UsuariEntitatBean usuariEntitat = getUsuariEntitatWs().getUsuariEntitat(firmaBean.getDestinatariID());
						if (usuariEntitat != null) {
							String usuariPersonaId = usuariEntitat.getUsuariPersonaID();
							UsuariPersonaBean usuariPersona = getUsuariEntitatWs().getUsuariPersona(usuariPersonaId);
							if (usuariPersona != null) {
								signer.setSignerId(usuariPersona.getNif());
								signer.setSignerCodi(usuariPersona.getUsuariPersonaID());
								CarrecWs carrec = getUsuariEntitatWs().getCarrec(firmaBean.getDestinatariID());
								if (carrec != null && carrec.getCarrecName() != null)
									signer.setSignerNom(carrec.getCarrecName());
								else
									signer.setSignerNom(usuariPersona.getNom() + " " + usuariPersona.getLlinatges());
								signers.add(signer);
							} else {
								throw new SistemaExternException("No s'ha trobat cap usuari persona amb id " + usuariPersonaId);
							}
						} else {
							throw new SistemaExternException("No s'ha trobat cap usuari entitat amb el codi " + firmaBean.getDestinatariID());
						}
					}
					portafirmesBlock.setSigners(signers);
					portafirmesBlocks.add(portafirmesBlock);
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		}
		return portafirmesBlocks;
	}
	
	private List<PortafirmesBlockInfo> asyncBlockToPortafirmesBlockInfo(List<FlowTemplateSimpleBlock> blocks)
			throws SistemaExternException {
		List<PortafirmesBlockInfo> portafirmesBlocks = null;
		try {
			if (blocks != null) {
				Collections.sort(blocks, new Comparator<FlowTemplateSimpleBlock>() {
					@Override
					public int compare(FlowTemplateSimpleBlock o1, FlowTemplateSimpleBlock o2) {
						if (o1.getOrder() < o2.getOrder())
							return -1;
						else
							return 1;
					}
				});
				portafirmesBlocks = new ArrayList<PortafirmesBlockInfo>();
				for (FlowTemplateSimpleBlock flowTemplateSimpleBlock : blocks) {
					PortafirmesBlockInfo portafirmesBlock = new PortafirmesBlockInfo();
					List<PortafirmesBlockSignerInfo> signers = new ArrayList<PortafirmesBlockSignerInfo>();

					for (FlowTemplateSimpleSignature flowTemplateSimpleSignature : flowTemplateSimpleBlock.getSignatures()) {

						if (flowTemplateSimpleSignature.getSigner() != null) {
							PortafirmesBlockSignerInfo signer = new PortafirmesBlockSignerInfo();
							
							if (flowTemplateSimpleSignature.getSigner().getExternalSigner() != null) {
								FlowTemplateSimpleExternalSigner externalSigner = flowTemplateSimpleSignature.getSigner().getExternalSigner();
								signer.setSignerId(externalSigner.getAdministrationId());	
								signer.setSignerNom(externalSigner.getName() + " " + externalSigner.getSurnames());
								signer.setSignerCodi(" ");
							} else {
								String intermediateServerUsername = flowTemplateSimpleSignature.getSigner().getIntermediateServerUsername();
								String positionInTheCompany = flowTemplateSimpleSignature.getSigner().getPositionInTheCompany();
								if (intermediateServerUsername != null) {
									UsuariEntitatBean usuariEntitat = getUsuariEntitatWs().getUsuariEntitat(intermediateServerUsername);
									if (usuariEntitat != null) {
										String usuariPersonaId = usuariEntitat.getUsuariPersonaID();
										UsuariPersonaBean usuariPersona = getUsuariEntitatWs().getUsuariPersona(usuariPersonaId);
										if (usuariPersona != null) {
											signer.setSignerId(usuariPersona.getNif());
											signer.setSignerCodi(usuariPersona.getUsuariPersonaID());
											signer.setSignerNom(usuariPersona.getNom() + " " + usuariPersona.getLlinatges());
										} else {
											throw new SistemaExternException("No s'ha trobat cap usuari persona amb id " + usuariPersonaId);
										}
									} else {
										throw new SistemaExternException("No s'ha trobat cap usuari entitat amb el codi " + intermediateServerUsername);
									} 
								} else if (positionInTheCompany != null) {
									CarrecWs carrec = getUsuariEntitatWs().getCarrec(positionInTheCompany);
									if (carrec != null) {
										String usuariPersonaId = carrec.getUsuariPersonaID();
										UsuariPersonaBean usuariPersona = getUsuariEntitatWs().getUsuariPersona(usuariPersonaId);
										if (usuariPersona != null) {
											signer.setSignerId(usuariPersona.getNif());
											signer.setSignerCodi(usuariPersona.getUsuariPersonaID());
											signer.setSignerNom(carrec.getCarrecName());
										} else {
											throw new SistemaExternException("No s'ha trobat cap usuari persona amb id " + usuariPersonaId);
										}
									} else {
										throw new SistemaExternException("No s'ha trobat cap càrrec amb codi " + positionInTheCompany);
									} 
								}
							}
							signers.add(signer);
						}
						portafirmesBlock.setSigners(signers);
					}
					portafirmesBlocks.add(portafirmesBlock);
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		}
		return portafirmesBlocks;
	}

	private FirmaAsyncSimpleSignatureBlock[] recuperarFluxDeFirma(String idTransaccio) throws SistemaExternException {
		List<FlowTemplateSimpleBlock> blocks = null;
		FirmaAsyncSimpleSignatureBlock[] blocsAsyncs = null;
		try {
			FlowTemplateSimpleGetFlowResultResponse result = getFlowTemplateResult(idTransaccio);
			
			if (result != null && result.getFlowInfo() != null) {
				blocks = new ArrayList<FlowTemplateSimpleBlock>();			
				FlowTemplateSimpleFlowTemplate flux = result.getFlowInfo();
				blocks = flux.getBlocks();
				
				blocsAsyncs = toFirmaAsyncSimpleSignatureBlock(blocks);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error transformant en l'objecte FirmaAsyncSimpleSignatureBlock[]",
					ex);
		}
		return blocsAsyncs;
	}
	
	private FirmaAsyncSimpleSignatureBlock[] toFirmaAsyncSimpleSignatureBlockFromId(
			String plantillaFluxId,
			String idioma) throws SistemaExternException {
		FirmaAsyncSimpleSignatureBlock[] blocsAsyncs = null;
		List<FlowTemplateSimpleBlock> blocks = null;
		try {
			FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest();

			request.setFlowTemplateId(plantillaFluxId);
			request.setLanguageUI(idioma);

			FlowTemplateSimpleFlowTemplate result = getFluxDeFirmaClient().getFlowInfoByFlowTemplateID(request);

			if (result != null) {
				blocks = result.getBlocks();
			}
		
			blocsAsyncs = toFirmaAsyncSimpleSignatureBlock(blocks);
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		}
		return blocsAsyncs;
	}
	
	private FirmaAsyncSimpleSignatureBlock[] toFirmaAsyncSimpleSignatureBlock(List<FlowTemplateSimpleBlock> blocks) throws SistemaExternException {
		FirmaAsyncSimpleSignatureBlock[] blocsAsyncs = null;
		int i = 0;
		
		try {
			if (blocks != null) {
				blocsAsyncs = new FirmaAsyncSimpleSignatureBlock[blocks.size()];
		
				for (FlowTemplateSimpleBlock flowTemplateSimpleBlock : blocks) {
					FirmaAsyncSimpleSignatureBlock blocAsync = new FirmaAsyncSimpleSignatureBlock();
					//firmes mínimes
					blocAsync.setMinimumNumberOfSignaturesRequired(flowTemplateSimpleBlock.getSignatureMinimum());
		
					//Firmants
					List<FirmaAsyncSimpleSignature> signatures = new ArrayList<FirmaAsyncSimpleSignature>();
		
					for (FlowTemplateSimpleSignature flowTemplateSimpleSignature : flowTemplateSimpleBlock.getSignatures()) {
						FirmaAsyncSimpleSignature signature = new FirmaAsyncSimpleSignature();
						signature.setMinimumNumberOfRevisers(flowTemplateSimpleSignature.getMinimumNumberOfRevisers());
						signature.setReason(flowTemplateSimpleSignature.getReason());
						signature.setRequired(flowTemplateSimpleSignature.isRequired());
		
						//Revisor
						if (flowTemplateSimpleSignature.getRevisers() != null) {
							List<FirmaAsyncSimpleReviser> revisers = new ArrayList<FirmaAsyncSimpleReviser>();
							for (FlowTemplateSimpleReviser flowTemplateSimpleReviser : flowTemplateSimpleSignature.getRevisers()) {
								FirmaAsyncSimpleReviser reviser = new FirmaAsyncSimpleReviser();
								
								String intermediateServerUsername = flowTemplateSimpleReviser.getIntermediateServerUsername();
								String positionInTheCompany = flowTemplateSimpleReviser.getPositionInTheCompany();
								if (intermediateServerUsername != null)
									reviser.setIntermediateServerUsername(intermediateServerUsername);
								if (positionInTheCompany != null)
									reviser.setPositionInTheCompany(positionInTheCompany);
								reviser.setAdministrationID(flowTemplateSimpleReviser.getAdministrationID());
								reviser.setRequired(flowTemplateSimpleReviser.isRequired());
								reviser.setUsername(flowTemplateSimpleReviser.getUsername());
		
								revisers.add(reviser);
							}
							signature.setRevisers(revisers);
						}
						//Firmant
						FirmaAsyncSimpleSigner signer = new FirmaAsyncSimpleSigner();
		
						if (flowTemplateSimpleSignature.getSigner() != null) {
							signer.setAdministrationID(flowTemplateSimpleSignature.getSigner().getAdministrationID());
		
							if (flowTemplateSimpleSignature.getSigner().getExternalSigner() != null) {
								FirmaAsyncSimpleExternalSigner externalSigner = new FirmaAsyncSimpleExternalSigner();
		
								externalSigner.setAdministrationId(flowTemplateSimpleSignature.getSigner().getExternalSigner().getAdministrationId());
								externalSigner.setEmail(flowTemplateSimpleSignature.getSigner().getExternalSigner().getEmail());
								externalSigner.setLanguage(flowTemplateSimpleSignature.getSigner().getExternalSigner().getLanguage());
								externalSigner.setName(flowTemplateSimpleSignature.getSigner().getExternalSigner().getName());
								externalSigner.setSecurityLevel(flowTemplateSimpleSignature.getSigner().getExternalSigner().getSecurityLevel());
								externalSigner.setSurnames(flowTemplateSimpleSignature.getSigner().getExternalSigner().getSurnames());
		
								signer.setExternalSigner(externalSigner);
							}
							String intermediateServerUsername = flowTemplateSimpleSignature.getSigner().getIntermediateServerUsername();
							String positionInTheCompany = flowTemplateSimpleSignature.getSigner().getPositionInTheCompany();
							if (intermediateServerUsername != null)
								signer.setIntermediateServerUsername(intermediateServerUsername);
							if (positionInTheCompany != null)
								signer.setPositionInTheCompany(positionInTheCompany);
							signer.setUsername(flowTemplateSimpleSignature.getSigner().getUsername());
							
							signature.setSigner(signer);
						}
						signatures.add(signature);
					}
		
					blocAsync.setSigners(signatures);
					blocsAsyncs[i] = blocAsync;
					i++;
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		}
		return blocsAsyncs;
	}

	private FirmaAsyncSimpleSignatureBlock[] simpleBlockToPortafirmesBlock(List<PortafirmesFluxBloc> flux) throws SistemaExternException {
		FirmaAsyncSimpleSignatureBlock[] blocsAsyncs = null;
		try {
			int i = 0;
			blocsAsyncs = new FirmaAsyncSimpleSignatureBlock[flux.size()];
			for (PortafirmesFluxBloc portafirmesFluxBloc : flux) {
				FirmaAsyncSimpleSignatureBlock blocAsync = new FirmaAsyncSimpleSignatureBlock();
				//firmes mínimes
				blocAsync.setMinimumNumberOfSignaturesRequired(portafirmesFluxBloc.getMinSignataris());
				//Firmants
				List<FirmaAsyncSimpleSignature> signatures = new ArrayList<FirmaAsyncSimpleSignature>();
				for (String destinatari : portafirmesFluxBloc.getDestinataris()) {
					FirmaAsyncSimpleSignature signature = new FirmaAsyncSimpleSignature();
					signature.setRequired(true);
					//Firmant
					FirmaAsyncSimpleSigner signer = new FirmaAsyncSimpleSigner();
					if (destinatari.startsWith("CARREC")) {
						String carrecName = destinatari.substring(destinatari.indexOf("[") + 1, destinatari.indexOf("]"));
						signer.setPositionInTheCompany(carrecName);
					} else {
						signer.setUsername(destinatari);
					}
					signature.setSigner(signer);
					signatures.add(signature);
				}
	
				blocAsync.setSigners(signatures);
				blocsAsyncs[i] = blocAsync;
				i++;
			}
		} catch (Exception ex) {
			throw new SistemaExternException("Hi ha hagut un error construint el flux", ex);
		}
		return blocsAsyncs;
	}

	private String getTransaction(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible) throws SistemaExternException {
		String transactionId = null;
		try {
			FlowTemplateSimpleGetTransactionIdRequest transactionRequest = new FlowTemplateSimpleGetTransactionIdRequest(
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
		try {
			FlowTemplateSimpleStartTransactionRequest transactionRequest = new FlowTemplateSimpleStartTransactionRequest(
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
			String transactionID) throws SistemaExternException {
		FlowTemplateSimpleGetFlowResultResponse result = null;

		try {
			result = getFluxDeFirmaClient().getFlowTemplateResult(transactionID);
		} catch (Exception ex) {
			throw new SistemaExternException("", ex);
		}
		return result;
	}

	private void closeTransaction(
			String transactionID) throws SistemaExternException {
		try {
			getFluxDeFirmaClient().closeTransaction(transactionID);
		} catch (Exception ex) {
			throw new SistemaExternException("", ex);
		}
	}
	
	private FirmaAsyncSimpleFile toFirmaAsyncSimpleFile(
			PortafirmesDocument document) throws Exception {
		if (!"pdf".equalsIgnoreCase(document.getArxiuExtensio())) {
			throw new SistemaExternException(
					"Els arxius per firmar han de ser de tipus PDF");
		}
		FirmaAsyncSimpleFile fitxer = new FirmaAsyncSimpleFile();
		fitxer.setNom(document.getArxiuNom());
		fitxer.setMime("application/pdf");
		fitxer.setData(document.getArxiuContingut());
		return fitxer;
	}

	private ApiFirmaAsyncSimple getFirmaAsyncSimpleApi() throws MalformedURLException {
		String apiRestUrl = getBaseUrl() + "/common/rest/apifirmaasyncsimple/v2";
		ApiFirmaAsyncSimple api = new ApiFirmaAsyncSimpleJersey(
				apiRestUrl,
				getUsername(),
				getPassword());
		return api;
	}
	
	private ApiFlowTemplateSimple getFluxDeFirmaClient() throws MalformedURLException {
		String apiRestUrl = getBaseUrl() + "/common/rest/apiflowtemplatesimple/v1";
		ApiFlowTemplateSimple api = new ApiFlowTemplateSimpleJersey(
				apiRestUrl,
				getUsername(),
				getPassword());
		return api;
	}
	
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
	private String getPerfil() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.perfil");
	}
	private Boolean isEnviarUrlExpedientPermitida() {
		return Boolean.valueOf(PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.enviar.url.expedient",
				"false"));
	}
	private String getUrlExpedient() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.url.expedient");
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
