/**
 *
 */
package es.caib.ripea.plugin.caib.digitalitzacio;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.fundaciobit.apisib.apiscanwebsimple.v1.ApiScanWebSimple;
import org.fundaciobit.apisib.apiscanwebsimple.v1.beans.ScanWebSimpleAvailableProfile;
import org.fundaciobit.apisib.apiscanwebsimple.v1.beans.ScanWebSimpleAvailableProfiles;
import org.fundaciobit.apisib.apiscanwebsimple.v1.beans.ScanWebSimpleGetTransactionIdRequest;
import org.fundaciobit.apisib.apiscanwebsimple.v1.beans.ScanWebSimpleProfileRequest;
import org.fundaciobit.apisib.apiscanwebsimple.v1.beans.ScanWebSimpleResultRequest;
import org.fundaciobit.apisib.apiscanwebsimple.v1.beans.ScanWebSimpleScanResult;
import org.fundaciobit.apisib.apiscanwebsimple.v1.beans.ScanWebSimpleSignatureParameters;
import org.fundaciobit.apisib.apiscanwebsimple.v1.beans.ScanWebSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apiscanwebsimple.v1.beans.ScanWebSimpleStatus;
import org.fundaciobit.apisib.apiscanwebsimple.v1.jersey.ApiScanWebSimpleJersey;
import org.slf4j.LoggerFactory;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioEstat;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioPerfil;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioPlugin;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioResultat;
import es.caib.ripea.plugin.digitalitzacio.DigitalitzacioTransaccioResposta;
import es.caib.ripea.plugin.PropertiesHelper;

/**
 * Implementació del plugin de portafirmes emprant el portafirmes
 * de la CAIB desenvolupat per l'IBIT (PortaFIB).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DigitalitzacioPluginDigitalIB implements DigitalitzacioPlugin {


	@Override
	public List<DigitalitzacioPerfil> recuperarPerfilsDisponibles(String idioma) throws SistemaExternException {
		List<DigitalitzacioPerfil> perfilsDisponibles = new ArrayList<DigitalitzacioPerfil>();
		try {
			ScanWebSimpleAvailableProfiles perfils = getDigitalitzacioClient().getAvailableProfiles(idioma);

			List<ScanWebSimpleAvailableProfile> perfilsList = perfils.getAvailableProfiles();

			if (perfilsList == null || perfilsList.size() == 0) {
		        logger.error("No hi ha perfils disponibles per aquest usuari d'aplicació.");
		    }

			for (ScanWebSimpleAvailableProfile scanWebSimpleAvailableProfile : perfilsList) {
				DigitalitzacioPerfil perfil = new DigitalitzacioPerfil();
				perfil.setCodi(scanWebSimpleAvailableProfile.getCode());
				perfil.setNom(scanWebSimpleAvailableProfile.getName());
				perfil.setDescripcio(scanWebSimpleAvailableProfile.getDescription());
				perfil.setTipus(scanWebSimpleAvailableProfile.getProfileType());
				perfilsDisponibles.add(perfil);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut recuperar els perfils de l'usuari aplicació configurat (" +
					"idioma=" + idioma + ")",
					ex);
		}
		return perfilsDisponibles;
	}

	@Override
	public DigitalitzacioTransaccioResposta iniciarProces(
			String codiPerfil,
			String idioma,
			UsuariDto funcionari,
			String urlReturn) throws SistemaExternException {
		DigitalitzacioTransaccioResposta transaccioResponse = null;
		ScanWebSimpleGetTransactionIdRequest transaccioRequest = null;
		ScanWebSimpleSignatureParameters signaturaParameters = null;
		boolean returnScannedFile = false;
		boolean returnSignedFile = false;
		
		try {
			//En cas de no especificar cap perfil agafar per propietats
			if (codiPerfil == null)
				codiPerfil = getPerfil();

			if (codiPerfil != null) {
				ScanWebSimpleProfileRequest profileRequest = new ScanWebSimpleProfileRequest(
				          codiPerfil,
				          idioma);
				
				ScanWebSimpleAvailableProfile scanWebProfileSelected = getDigitalitzacioClient().getProfile(
						profileRequest);
	
				switch (scanWebProfileSelected.getProfileType()) {
					//Només escaneig
					case ScanWebSimpleAvailableProfile.PROFILE_TYPE_ONLY_SCAN:
						transaccioRequest = new ScanWebSimpleGetTransactionIdRequest(
								idioma,
								ScanWebSimpleGetTransactionIdRequest.VIEW_FULLSCREEN,
								idioma,
								funcionari.getCodi());
						returnScannedFile = true;
			            returnSignedFile = false;
						break;
					//Escaneig + firma
					case ScanWebSimpleAvailableProfile.PROFILE_TYPE_SCAN_AND_SIGNATURE:
						signaturaParameters = getSignaturaParameters(
								idioma,
								funcionari.getNom(),
								funcionari.getNif());
		
						transaccioRequest = new ScanWebSimpleGetTransactionIdRequest(
								codiPerfil,
								ScanWebSimpleGetTransactionIdRequest.VIEW_FULLSCREEN,
								idioma,
								funcionari.getCodi(),
								signaturaParameters);
						returnScannedFile = false;
						returnSignedFile = true;
						break;
				}
				if (transaccioRequest != null) {
					transaccioResponse = new DigitalitzacioTransaccioResposta();
					
					String idTransaccio = getDigitalitzacioClient().getTransactionID(transaccioRequest);
	
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
			throw new SistemaExternException(
					"El procés de digitalització ha fallat (" +
					"perfil=" + codiPerfil + ", " +
					"idioma=" + idioma + ", " +
					"urlReturn=" + urlReturn+ ")",
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
			ScanWebSimpleResultRequest resultRequest = new ScanWebSimpleResultRequest(
					idTransaccio,
					returnScannedFile,
					returnSignedFile);

			ScanWebSimpleScanResult result = getScanWebResult(resultRequest);
			ScanWebSimpleStatus transactionStatus = result.getStatus();
			int status = transactionStatus.getStatus();

			switch (status) {
				case ScanWebSimpleStatus.STATUS_REQUESTED_ID:
					{
						String errorMsg = "S'ha rebut un estat inconsistent del procés"
					              + " (requestedid). Pot ser el PLugin no està ben desenvolupat."
					              + " Consulti amb el seu administrador.";
						logger.error(errorMsg);
						resposta.setError(true);
						resposta.setEstat(DigitalitzacioEstat.REQUESTED_ID);
						return resposta;
					}
				case ScanWebSimpleStatus.STATUS_IN_PROGRESS:
					{
						String errorMsg = "S'ha rebut un estat inconsistent del procés"
					              + " (En Progrés). Pot ser el PLugin no està ben desenvolupat."
					              + " Consulti amb el seu administrador.";
						logger.error(errorMsg);
						resposta.setError(true);
						resposta.setEstat(DigitalitzacioEstat.IN_PROGRESS);
						return resposta;
					}
				case ScanWebSimpleStatus.STATUS_FINAL_ERROR:
					{
						String errorMsg = "Error durant la realització de l'escaneig/còpia autèntica: "
					              + transactionStatus.getErrorMessage();
						resposta.setError(true);
						resposta.setEstat(DigitalitzacioEstat.FINAL_ERROR);
						String desc = transactionStatus.getErrorStackTrace();
						
						if (desc != null) {
							logger.error(desc);
						}
						logger.error(errorMsg);
						return resposta;
					}
				case ScanWebSimpleStatus.STATUS_CANCELLED:
					{
						String errorMsg = "Durant el procés, l'usuari ha cancelat la transacció.";
						logger.error(errorMsg);
						resposta.setError(true);
						resposta.setEstat(DigitalitzacioEstat.CANCELLED);
						return resposta;
					}
				case ScanWebSimpleStatus.STATUS_FINAL_OK:
					{
						
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
					}
					break;
				default: {
					throw new SistemaExternException("Codi d'estat desconegut (" + status + ")");
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error recuperant el resultat de digitalització (" +
					"idTransaccio=" + idTransaccio + ", " +
					"returnScannedFile=" + returnScannedFile + ", " +
					"returnSignedFile=" + returnSignedFile+ ")",
					ex);
		} finally {
			try {
				getDigitalitzacioClient().closeTransaction(idTransaccio);
			} catch (Exception ex) {
				throw new SistemaExternException(
						"S'ha produït un error tancant la transacció",
						ex);
			}
		}
		return resposta;
	}

	public void tancarTransaccio(String idTransaccio) throws SistemaExternException {
		try {
			getDigitalitzacioClient().closeTransaction(idTransaccio);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error tancant la transacció",
					ex);
		}
	}

	private String startTransaction(
			String idTransaccio,
			String urlReturn) throws SistemaExternException {
		logger.debug("Iniciant transacció " + idTransaccio);
		String urlRedireccio = null;
		ScanWebSimpleStartTransactionRequest transactionRequest;

		try {
			transactionRequest = new ScanWebSimpleStartTransactionRequest(
					idTransaccio,
					urlReturn);

			urlRedireccio = getDigitalitzacioClient().startTransaction(transactionRequest);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut iniciar la transacció (" +
					"transactionId=" + idTransaccio + ", " +
					"returnUrl=" + urlReturn + ")",
					ex);
		}
		return urlRedireccio;
	}

	private ScanWebSimpleScanResult getScanWebResult(
			ScanWebSimpleResultRequest transactionID) {
		ScanWebSimpleScanResult result = null;

		try {
			result = getDigitalitzacioClient().getScanWebResult(transactionID);
			System.out.println(result.getStatus());
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return result;
	}

	private ScanWebSimpleSignatureParameters getSignaturaParameters(
			String idioma,
			String funcionariNom,
			String funcionariNif) {
		logger.debug("Configurant paràmetres de signatura.");
	    ScanWebSimpleSignatureParameters signatureParameters = new ScanWebSimpleSignatureParameters(
	    		idioma, 
	    		funcionariNom,
	    		funcionariNif);
	    
	    return signatureParameters;
	}
	private ApiScanWebSimple getDigitalitzacioClient() throws MalformedURLException {
		String apiRestUrl = getBaseUrl() + "/common/rest/apiscanwebsimple/v1";
		ApiScanWebSimple api = new ApiScanWebSimpleJersey(
				apiRestUrl,
				getUsername(),
				getPassword());
		return api;
	}

	private String getBaseUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.digitalitzacio.digitalib.base.url");
	}
	private String getUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.digitalitzacio.digitalib.username");
	}
	private String getPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.digitalitzacio.digitalib.password");
	}
	private String getPerfil() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.digitalitzacio.digitalib.perfil");
	}

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DigitalitzacioPluginDigitalIB.class);
}
