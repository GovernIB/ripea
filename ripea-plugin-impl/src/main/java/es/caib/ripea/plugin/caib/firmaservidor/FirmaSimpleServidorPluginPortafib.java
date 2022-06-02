/**
 * 
 */
package es.caib.ripea.plugin.caib.firmaservidor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaEnServidorSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAvailableProfile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleCommonInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFileInfoSignature;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignDocumentRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureResult;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignedFileInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaEnServidorSimpleJersey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.ripea.plugin.firmaservidor.SignaturaConsulta;
import es.caib.ripea.plugin.firmaservidor.SignaturaResposta;
import es.caib.ripea.plugin.firmaservidor.TipusMime;

/**
 * Implementació del plugin de signatura emprant el portafirmes
 * de la CAIB desenvolupat per l'IBIT (PortaFIB).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
	public class FirmaSimpleServidorPluginPortafib extends RipeaAbstractPluginProperties implements FirmaServidorPlugin {


	public FirmaSimpleServidorPluginPortafib() {
		super();
	}
	public FirmaSimpleServidorPluginPortafib(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public SignaturaResposta firmar(String nom, String motiu, byte[] contingut, TipusFirma tipusFirma, String idioma) throws SistemaExternException {

		return signar(SignaturaConsulta.builder().nom(nom).motiu(motiu).tipusFirma(tipusFirma).mime(TipusMime.PDF).contingut(contingut).build());
	}

	public SignaturaResposta signar(SignaturaConsulta consulta) {

		try {
			ApiFirmaEnServidorSimple api = new ApiFirmaEnServidorSimpleJersey(getPropertyEndpoint(), getPropertyUsername(), getPropertyPassword());
			FirmaSimpleFile fileToSign = new FirmaSimpleFile(consulta.getNom(), consulta.getMime().getTipus(), consulta.getContingut());

			// getAvailableProfiles(api);
			String perfil = getPropertyPerfil();
			FirmaSimpleSignatureResult result = internalSignDocument(api, perfil, fileToSign, consulta.getMotiu(), consulta.getTipusDocumental());
			SignaturaResposta resposta = new SignaturaResposta();
			resposta.setContingut(result.getSignedFile().getData());
			if (result.getSignedFile() != null) {
				resposta.setNom(result.getSignedFile().getNom());
				resposta.setMime(result.getSignedFile().getMime());
			}
			if (result.getSignedFileInfo() != null) {
				resposta.setTipusFirma(result.getSignedFileInfo().getSignType());
				resposta.setTipusFirmaEni(result.getSignedFileInfo().getEniTipoFirma());
				resposta.setPerfilFirmaEni(result.getSignedFileInfo().getEniPerfilFirma());
			}
			return resposta;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FirmaSimpleSignatureResult internalSignDocument(
			ApiFirmaEnServidorSimple api,
			final String perfil,
			FirmaSimpleFile fileToSign,
			String motiu,
			String tipusDocumental) throws Exception, FileNotFoundException, IOException {
		String signID = "999";
		String name = fileToSign.getNom();
		String reason = motiu;
		String location = getPropertyLocation();

		int signNumber = 1;
		String languageSign = "ca";
		Long tipusDocumentalID = tipusDocumental != null ? Long.valueOf(tipusDocumental.substring(2)) : null;

		FirmaSimpleFileInfoSignature fileInfoSignature = new FirmaSimpleFileInfoSignature(
				fileToSign,
				signID,
				name,
				reason,
				location,
				signNumber,
				languageSign,
				tipusDocumentalID);

		String languageUI = "ca";
		String certificat = getPropertyUsuariFirma();
		String administrationID = null;
		String signerEmail = getPropertySignerEmail();

		FirmaSimpleCommonInfo commonInfo;
		commonInfo = new FirmaSimpleCommonInfo(perfil, languageUI, certificat, administrationID, signerEmail);

		logger.debug("languageUI = |" + languageUI + "|");

		FirmaSimpleSignDocumentRequest signature;
		signature = new FirmaSimpleSignDocumentRequest(commonInfo, fileInfoSignature);

		FirmaSimpleSignatureResult fullResults = api.signDocument(signature);

		FirmaSimpleStatus transactionStatus = fullResults.getStatus();

		int status = transactionStatus.getStatus();

		switch (status) {

		case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;
			throw new SistemaExternException("API de firma simple ha tornat status erroni: Initializing ...Unknown Error (???)");

		case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;
			throw new SistemaExternException("API de firma simple ha tornat status erroni: In PROGRESS ...Unknown Error (???)");

		case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;
			throw new SistemaExternException("Error durant la realització de les firmes: " + transactionStatus.getErrorMessage() +"\r\n" +transactionStatus.getErrorStackTrace());

		case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
			throw new SistemaExternException("S'ha cancel·lat el procés de firmat.");

		case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
		{
			logger.debug(" ===== RESULTAT  =========");
			logger.debug(" ---- Signature [ " + fullResults.getSignID() + " ]");
			logger.debug(FirmaSimpleSignedFileInfo.toString(fullResults.getSignedFileInfo()));

			return fullResults;
		}
		default:
			throw new SistemaExternException("Status de firma desconegut");
		}
	}

	private void getAvailableProfiles(ApiFirmaEnServidorSimple api) throws Exception {

		    final String languagesUI[] = new String[] { "ca", "es" };

		    for (String languageUI : languagesUI) {
		      logger.info(" ==== LanguageUI : " + languageUI + " ===========");

		      List<FirmaSimpleAvailableProfile> listProfiles = api.getAvailableProfiles(languageUI);
		      if (listProfiles.size() == 0) {
		        logger.info("NO HI HA PERFILS PER AQUEST USUARI APLICACIÓ");
		      } else {
		        for (FirmaSimpleAvailableProfile ap : listProfiles) {
		          logger.info("  + " + ap.getName() + ":");
		          logger.info("      * Codi: " + ap.getCode());
		          logger.info("      * Desc: " + ap.getDescription());
		        }
		      }
		    }
	 }

	private String getPropertyEndpoint() {
		return getProperty("plugin.firmaservidor.portafib.endpoint");
	}

	private String getPropertyUsername() {
		return getProperty("plugin.firmaservidor.portafib.auth.username");
	}

	private String getPropertyPassword() {
		return getProperty("plugin.firmaservidor.portafib.auth.password");
	}

	private String getPropertyPerfil() {
		return getProperty("plugin.firmaservidor.portafib.perfil");
	}

	private String getPropertyLocation() {
		return getProperty("plugin.firmaservidor.portafib.location", "Palma");
	}

	private String getPropertySignerEmail() {
		return getProperty("plugin.firmaservidor.portafib.signerEmail", "suport@caib.es");
	}

	private String getPropertyUsuariFirma() {
		return getProperty("plugin.firmaservidor.portafib.username");
	}

	private static final Logger logger = LoggerFactory.getLogger(FirmaSimpleServidorPluginPortafib.class);
}