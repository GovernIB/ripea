/**
 *
 */
package es.caib.ripea.plugin.caib.digitalitzacio;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.utils.Utils;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.ApiMassiveScanWebSimple;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleAvailableProfile;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleAvailableProfiles;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleGetTransactionIdRequest;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleProfileRequest;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleSignatureParameters;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleStatus;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleSubTransactionsOfTransaction;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleSubtransactionResult;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleSubtransactionResultRequest;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.jersey.ApiMassiveScanWebSimpleJersey;
import org.slf4j.LoggerFactory;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioEstat;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioPerfil;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioPlugin;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioResultat;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioTransaccioResposta;

/**
 * Implementació del plugin de portafirmes emprant el portafirmes de la CAIB desenvolupat per l'IBIT (PortaFIB).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DigitalitzacioPluginDigitalIB extends RipeaAbstractPluginProperties implements DigitalitzacioPlugin {

	public DigitalitzacioPluginDigitalIB() {
		super();
	}
	public DigitalitzacioPluginDigitalIB(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	@Override
	public List<DigitalitzacioPerfil> recuperarPerfilsDisponibles(String idioma) throws SistemaExternException {
		List<DigitalitzacioPerfil> perfilsDisponibles = new ArrayList<DigitalitzacioPerfil>();

		try {
			MassiveScanWebSimpleAvailableProfiles profiles = getDigitalitzacioClient().getAvailableProfiles(idioma);

			List<MassiveScanWebSimpleAvailableProfile> profilesList = profiles.getAvailableProfiles();

			if (profilesList == null || profilesList.size() == 0) {
				if (isDebug()) {
					logger.error("NO HI HA PERFILS PER AQUEST USUARI APLICACIÓ");
				}
			} else {
				if (isDebug()) {
					logger.info(" ---- Perfils Disponibles ----");
				}
				int i = 1;
				Map<Integer, MassiveScanWebSimpleAvailableProfile> profilesByIndex;
				profilesByIndex = new HashMap<Integer, MassiveScanWebSimpleAvailableProfile>();
				for (MassiveScanWebSimpleAvailableProfile profile : profilesList) {
					if (isDebug()) {
						logger.info(i + ".- " + profile.getName() + "(CODI: " + profile.getCode() + "): " + profile.getDescription());
					}
					profilesByIndex.put(i,profile);
					i++;
				}
				if (isDebug()) {
					logger.info(" -----------------------------");
				}
			}

			for (MassiveScanWebSimpleAvailableProfile scanWebSimpleAvailableProfile : profilesList) {
				DigitalitzacioPerfil perfil = new DigitalitzacioPerfil();
				perfil.setCodi(scanWebSimpleAvailableProfile.getCode());
				perfil.setNom(scanWebSimpleAvailableProfile.getName());
				perfil.setDescripcio(scanWebSimpleAvailableProfile.getDescription());
				perfil.setTipus(scanWebSimpleAvailableProfile.getProfileType());
				perfilsDisponibles.add(perfil);
			}

		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut recuperar els perfils de l'usuari aplicació configurat (" + "idioma=" + idioma + ")", ex);
		}

		return perfilsDisponibles;
	}

	@Override
	public DigitalitzacioTransaccioResposta iniciarProces(
			String codiPerfil,
			String idioma,
			UsuariDto funcionari,
			String urlReturn) throws SistemaExternException {

		// Si tipus de perfil val MassiveScanWebSimpleAvailableProfile.PROFILE_TYPE_ONLY_SCAN
		final String profileCode = codiPerfil;
		final int view = MassiveScanWebSimpleGetTransactionIdRequest.VIEW_FULLSCREEN;

		DigitalitzacioTransaccioResposta transaccioResponse = null;
		MassiveScanWebSimpleGetTransactionIdRequest transacctionIdRequest = null;
		boolean returnScannedFile = false;
		boolean returnSignedFile = false;

		final String transactionName = "Transaccio " + System.currentTimeMillis();

		try {
			// En cas de no especificar cap perfil agafar per propietats
			if (codiPerfil == null)
				codiPerfil = getPerfil();

			if (codiPerfil != null) {
				MassiveScanWebSimpleProfileRequest profileRequest = new MassiveScanWebSimpleProfileRequest(
						codiPerfil,
						idioma);

				MassiveScanWebSimpleAvailableProfile scanWebProfileSelected = getDigitalitzacioClient().getProfile(profileRequest);

				switch (scanWebProfileSelected.getProfileType()) {
				// Només escaneig
				case 1:

					transacctionIdRequest = new MassiveScanWebSimpleGetTransactionIdRequest(
							transactionName,
							codiPerfil,
							view,
							idioma,
							funcionari.getCodi());
					returnScannedFile = true;
					returnSignedFile = false;
					break;
				// Escaneig + firma
				case 2:

					MassiveScanWebSimpleSignatureParameters signatureParameters;
					signatureParameters = new MassiveScanWebSimpleSignatureParameters(
							idioma,
							funcionari.getNom(),
							funcionari.getNif());
					transacctionIdRequest = new MassiveScanWebSimpleGetTransactionIdRequest(
							transactionName,
							profileCode,
							view,
							idioma,
							funcionari.getNom(),
							signatureParameters);
					returnScannedFile = false;
					returnSignedFile = true;
					break;
				}
				if (transacctionIdRequest != null) {
					transaccioResponse = new DigitalitzacioTransaccioResposta();

					String idTransaccio = getDigitalitzacioClient().getTransactionID(transacctionIdRequest);

					String urlRedireccio = startTransaction(
							idTransaccio,
							urlReturn + idTransaccio);

					transaccioResponse.setIdTransaccio(idTransaccio);
					transaccioResponse.setUrlRedireccio(urlRedireccio);
					transaccioResponse.setReturnScannedFile(returnScannedFile);
					transaccioResponse.setReturnSignedFile(returnSignedFile);
				}
			} else {
				throw new SistemaExternException("No s'ha especificat cap perfil per poder iniciar el procés de digitalització.");
			}
		} catch (Exception ex) {
			throw new SistemaExternException("El procés de digitalització ha fallat (" + "perfil=" + codiPerfil + ", " + "idioma=" + idioma + ", " + "urlReturn=" + urlReturn + ")",
					ex);
		}

		return transaccioResponse;
	}

	@Override
	public DigitalitzacioResultat recuperarResultat(
			String idTransaccio,
			boolean returnScannedFile,
			boolean returnSignedFile) throws SistemaExternException {
		DigitalitzacioResultat resposta = new DigitalitzacioResultat();
		try {

			MassiveScanWebSimpleSubTransactionsOfTransaction subs = getDigitalitzacioClient().getSubTransactionsOfTransaction(idTransaccio);

			List<String> subtransacions = subs.getSubtransacions();

			String subTransactionID = subtransacions.get(0);

			MassiveScanWebSimpleSubtransactionResultRequest resultRequest;
			resultRequest = new MassiveScanWebSimpleSubtransactionResultRequest(
					subTransactionID,
					returnScannedFile,
					returnSignedFile);

			MassiveScanWebSimpleSubtransactionResult result = getDigitalitzacioClient().getSubTransactionResult(resultRequest);

			MassiveScanWebSimpleStatus transactionStatus = result.getStatus();
			if (isDebug()) {
				logger.info(MassiveScanWebSimpleSubtransactionResult.toString(result));
			}

			int status = transactionStatus.getStatus();

			switch (status) {
			case MassiveScanWebSimpleStatus.STATUS_REQUESTED_ID: {
				String errorMsg = "S'ha rebut un estat inconsistent del procés" + " (requestedid). Pot ser el PLugin no està ben desenvolupat." + " Consulti amb el seu administrador.";
				logger.error(errorMsg);
				resposta.setError(true);
				resposta.setEstat(DigitalitzacioEstat.REQUESTED_ID);
				return resposta;
			}
			case MassiveScanWebSimpleStatus.STATUS_IN_PROGRESS: {
				String errorMsg = "S'ha rebut un estat inconsistent del procés" + " (En Progrés). Pot ser el PLugin no està ben desenvolupat." + " Consulti amb el seu administrador.";
				logger.error(errorMsg);
				resposta.setError(true);
				resposta.setEstat(DigitalitzacioEstat.IN_PROGRESS);
				return resposta;
			}
			case MassiveScanWebSimpleStatus.STATUS_FINAL_ERROR: {
				String errorMsg = "Error durant la realització de l'escaneig/còpia autèntica: " + transactionStatus.getErrorMessage();
				resposta.setError(true);
				resposta.setEstat(DigitalitzacioEstat.FINAL_ERROR);
				String desc = transactionStatus.getErrorStackTrace();

				if (desc != null) {
					logger.error(desc);
				}
				logger.error(errorMsg);
				return resposta;
			}
			case MassiveScanWebSimpleStatus.STATUS_CANCELLED: {
				String errorMsg = "Durant el procés, l'usuari ha cancelat la transacció.";
				logger.error(errorMsg);
				resposta.setError(true);
				resposta.setEstat(DigitalitzacioEstat.CANCELLED);
				return resposta;
			}
			case MassiveScanWebSimpleStatus.STATUS_FINAL_OK: {

				resposta.setError(false);
				if (result.getScannedFile() != null) {
					String errorMsg = "La recuperació del fitxer escanejat s'ha realitzat amb èxit.";
					resposta.setContingut(result.getScannedFile().getData());
					resposta.setNomDocument(result.getScannedFile().getNom());
					resposta.setMimeType(result.getScannedFile().getMime());
					logger.debug(errorMsg);
				}

				if (result.getSignedFile() != null) {
					String errorMsg = "La recuperació del fitxer escanejat i firmat s'ha realitzat amb èxit.";
					resposta.setContingut(result.getSignedFile().getData());
					resposta.setNomDocument(result.getSignedFile().getNom());
					resposta.setMimeType(result.getSignedFile().getMime());
					resposta.setEniTipoFirma(result.getSignedFileInfo().getEniTipoFirma());
					logger.debug(errorMsg);
				}
				
				if (result.getScannedFileInfo() != null) {
					resposta.setIdioma(result.getScannedFileInfo().getDocumentLanguage());
					resposta.setResolucion(result.getScannedFileInfo().getPppResolution());
					
				}
				
				
			}
				break;
			default: {
				throw new SistemaExternException("Codi d'estat desconegut (" + status + ")");
			}
			}

		} catch (Exception ex) {
			throw new SistemaExternException("S'ha produït un error recuperant el resultat de digitalització (" + "idTransaccio=" + idTransaccio + ", " + "returnScannedFile=" + returnScannedFile + ", " + "returnSignedFile=" + returnSignedFile + ")",
					ex);
		} finally {
			try {
				getDigitalitzacioClient().closeTransaction(idTransaccio);
			} catch (Exception ex) {
				throw new SistemaExternException("S'ha produït un error tancant la transacció",
						ex);
			}
		}
		return resposta;
	}

	public void tancarTransaccio(String idTransaccio) throws SistemaExternException {
		try {
			getDigitalitzacioClient().closeTransaction(idTransaccio);
		} catch (Exception ex) {
			throw new SistemaExternException("S'ha produït un error tancant la transacció",
					ex);
		}
	}

	private String startTransaction(
			String idTransaccio,
			String urlReturn) throws SistemaExternException {
		if (isDebug()) {
			logger.info("Iniciant transacció " + idTransaccio);
		}		
		String urlRedireccio = null;

		try {
			MassiveScanWebSimpleStartTransactionRequest startTransactionInfo = new MassiveScanWebSimpleStartTransactionRequest(
					idTransaccio,
					urlReturn);
			urlRedireccio = getDigitalitzacioClient().startTransaction(startTransactionInfo);

		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut iniciar la transacció (" + "transactionId=" + idTransaccio + ", " + "returnUrl=" + urlReturn + ")", ex);
		}
		return urlRedireccio;
	}

	private ApiMassiveScanWebSimple getDigitalitzacioClient() throws MalformedURLException {
		String apiRestUrl = getBaseUrl();

		ApiMassiveScanWebSimple api = new ApiMassiveScanWebSimpleJersey(
				apiRestUrl,
				getUsername(),
				getPassword());
		return api;
	}
	
	private String getBaseUrl() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.DIGITALITZACIO_PLUGIN_URL));
	}
	private String getUsername() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.DIGITALITZACIO_PLUGIN_USR));
	}
	private String getPassword() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.DIGITALITZACIO_PLUGIN_PAS));
	}
	private String getPerfil() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.DIGITALITZACIO_PLUGIN_PERFIL));
	}
	private boolean isDebug() {
		return getAsBoolean(PropertyConfig.getPropertySuffix(PropertyConfig.DIGITALITZACIO_PLUGIN_DEBUG));
	}
	@Override
	public String getEndpointURL() {
		String endpoint = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.DIGITALITZACIO_PLUGIN_ENDPOINT));
		if (Utils.isEmpty(endpoint)) {
			endpoint = getBaseUrl();
		}
		return endpoint;
	}
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DigitalitzacioPluginDigitalIB.class);
}