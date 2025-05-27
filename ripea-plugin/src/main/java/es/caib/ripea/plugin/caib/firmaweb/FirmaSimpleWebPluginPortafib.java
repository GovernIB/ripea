package es.caib.ripea.plugin.caib.firmaweb;

import java.util.List;
import java.util.Properties;

import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.FirmaResultatDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.StatusEnumDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.utils.Utils;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaWebSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAddFileToSignRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleCommonInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFileInfoSignature;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleGetSignatureResultRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleGetTransactionStatusResponse;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureResult;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaWebSimpleJersey;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.firmaweb.FirmaWebPlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirmaSimpleWebPluginPortafib extends RipeaAbstractPluginProperties implements FirmaWebPlugin {
	
	private ApiFirmaWebSimple api = null;
	
	public FirmaSimpleWebPluginPortafib() {
		super();
	}
	public FirmaSimpleWebPluginPortafib(
			String propertyKeyBase,
			Properties properties) {
		super(propertyKeyBase, properties);
	}

	@Override
	public String firmaSimpleWebStart(
			List<FitxerDto> fitxersPerFirmar,
			String motiu,
			UsuariDto usuariActual,
			String urlReturnToRipea,
			String iframeVista) {
		
		if (getPropertyDebug())
			log.info("firmaSimpleWebStart " + getPropertyEndpoint());
		
		ApiFirmaWebSimple api = getApi();

		String transactionID = null;

		try {

			final String username = usuariActual.getCodi();
			final String administrationID = usuariActual.getNif();
			final String signerEmail = usuariActual.getEmail();

			String language = "ca";

			FirmaSimpleCommonInfo commonInfoSignature = new FirmaSimpleCommonInfo(
					null,
					language,
					username,
					administrationID,
					signerEmail);
			
			if (getPropertyDebug())
				log.info("api.getTransactionID username=" + username);
			
			transactionID = api.getTransactionID(commonInfoSignature);
			
			final String reason = motiu;
			final String location = getPropertyLocation();
			long tipusDocumentalID = 99; // =TD99
			
			
			for (FitxerDto fitxerDto : fitxersPerFirmar) {

				FirmaSimpleFile fileToSign = new FirmaSimpleFile(
						fitxerDto.getNom(),
						fitxerDto.getContentType(),
						fitxerDto.getContingut());

				FirmaSimpleFileInfoSignature fileInfoSignature = new FirmaSimpleFileInfoSignature(
						fileToSign,
						String.valueOf(fitxerDto.getId()),
						fitxerDto.getNom(),
						reason,
						location,
						1,
						language,
						tipusDocumentalID);

					FirmaSimpleAddFileToSignRequest newDocument = new FirmaSimpleAddFileToSignRequest(
							transactionID,
							fileInfoSignature);
					api.addFileToSign(newDocument);
					
			}
			

			// Aquí especificam la URL de retorn un cop finalitzada la transacció
			urlReturnToRipea = urlReturnToRipea + (urlReturnToRipea.contains("?") ? "&" : "?") + "transactionID=" + transactionID;

			if (getPropertyDebug())
				log.info("urlReturnToRipea=" + urlReturnToRipea);
			
			FirmaSimpleStartTransactionRequest startTransactionInfo = new FirmaSimpleStartTransactionRequest(
					transactionID,
					urlReturnToRipea,
					iframeVista);
					//FirmaSimpleStartTransactionRequest.VIEW_IFRAME);

			String urlRedirectToPortafib = api.startTransaction(startTransactionInfo);
			
			if (getPropertyDebug())
				log.info("urlRedirectToPortafib=" + urlRedirectToPortafib);
			
			return urlRedirectToPortafib;

		} catch (Exception e) {

			String msg = "Error processant entrada de dades o inicialitzant el proces de firma simple web: " + e.getMessage();

			if (transactionID != null) {
				try {
					api.closeTransaction(transactionID);
				} catch (Throwable th) {
					th.printStackTrace();
				}
			}
			throw new RuntimeException(msg, e);
		}
	}
	
	@Override
	public FirmaResultatDto firmaSimpleWebEnd(
			String transactionID) {

		FirmaResultatDto firmaResultat = null;
		
		if (getPropertyDebug())
			log.info("firmaSimpleWebEnd transactionID=" + transactionID);

		ApiFirmaWebSimple api = null;

		try {

			api = getApi();
			
			FirmaSimpleGetTransactionStatusResponse fullTransactionStatus = api.getTransactionStatus(transactionID);

			FirmaSimpleStatus transactionStatus = fullTransactionStatus.getTransactionStatus();
			int status = transactionStatus.getStatus();
			
			if (getPropertyDebug())
				log.info("fullTransactionStatus=" + status);
			
			switch (status) {

			case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;
				firmaResultat = new FirmaResultatDto(
								StatusEnumDto.ERROR,
						"S'ha rebut un estat inconsistent del proces de firma (inicialitzant). Pot ser el Plugin de Firma no està ben desenvolupat. Consulti amb el seu administrador.");
				break;

			case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;
				firmaResultat = new FirmaResultatDto(
								StatusEnumDto.ERROR,
						"S'ha rebut un estat inconsistent del proces de firma (En Progrés). Pot ser el Plugin de Firma no està ben desenvolupat. Consulti amb el seu administrador.");
				break;

			case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;
				String errorMsg = "Error durant la realització de les firmes: " + transactionStatus.getErrorMessage() + " \n " + transactionStatus.getErrorStackTrace();
				firmaResultat = new FirmaResultatDto(
								StatusEnumDto.ERROR,
								errorMsg);
				if (getPropertyDebug())
					log.info(errorMsg);
				break;

			case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
				firmaResultat = new FirmaResultatDto(
								StatusEnumDto.WARNING,
						"Durant el proces de firmes, l'usuari ha cancelat la transacció.");
				break;

			case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
				firmaResultat = processStatusFileOfSign(
						api,
						transactionID,
						fullTransactionStatus);
				break;

			}

		} catch (Exception e) {

			String msg = "Error firma simple web: " + e.getMessage();
			log.error(msg, e);
			firmaResultat = new FirmaResultatDto(
					StatusEnumDto.ERROR,
					msg);

		} finally {
			if (api != null && transactionID != null) {
				try {
					api.closeTransaction(transactionID);
				} catch (Throwable th) {
					th.printStackTrace();
				}
			}
		}

		return firmaResultat;
	}
	
	private FirmaResultatDto processStatusFileOfSign(
			ApiFirmaWebSimple api,
			String transactionID,
			FirmaSimpleGetTransactionStatusResponse fullTransactionStatus) throws Exception {

		FirmaResultatDto firmaResultat = new FirmaResultatDto(
				StatusEnumDto.OK,
				null);

		List<FirmaSimpleSignatureStatus> ssl = fullTransactionStatus.getSignaturesStatusList();

		for (FirmaSimpleSignatureStatus signatureStatus : ssl) {

			final String signID = signatureStatus.getSignID();
			FirmaSimpleStatus fss = signatureStatus.getStatus();
			int statusSign = fss.getStatus();
			
			if (getPropertyDebug())
				log.info("signID= " + signID + ", statusSign=" + statusSign);
			
			switch (statusSign) {

			case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;

				firmaResultat.addSignature(
						new FirmaResultatDto.FirmaSignatureStatus(
								signID,
								StatusEnumDto.ERROR,
								"Incoherent Status (STATUS_INITIALIZING)"));
				break;
			case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;

				firmaResultat.addSignature( 
						new FirmaResultatDto.FirmaSignatureStatus(
								signID,
								StatusEnumDto.ERROR,
								"Incoherent Status (STATUS_IN_PROGRESS)"));
				break;

			case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;

				String errorMsg = "Error en la firma: " + fss.getErrorMessage() + " (STATUS_ERROR)";
				firmaResultat.addSignature( 
						new FirmaResultatDto.FirmaSignatureStatus(
								signID,
								StatusEnumDto.ERROR,
								errorMsg));
				if (getPropertyDebug())
					log.info(errorMsg);
				break;

			case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
				firmaResultat.addSignature( 
						new FirmaResultatDto.FirmaSignatureStatus(
								signID,
								StatusEnumDto.WARNING,
								"L'usuari ha cancel.lat la firma. (STATUS_CANCELLED)"));
				break;

			case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
				
				FirmaSimpleSignatureResult fssr = api.getSignatureResult(
						new FirmaSimpleGetSignatureResultRequest(
								transactionID,
								signID));
				
				FirmaSimpleFile fsf = fssr.getSignedFile();
				final String outFile = signID + "_" + fsf.getNom();

				firmaResultat.addSignature( 
						new FirmaResultatDto.FirmaSignatureStatus(
								signID,
								StatusEnumDto.OK,
								outFile,
								fsf.getData()));
				break;
			}
		}

		return firmaResultat;
	}

	private ApiFirmaWebSimple getApi() {
		if (api == null) {
			api = new ApiFirmaWebSimpleJersey(
					getPropertyEndpoint(),
					getPropertyUsername(),
					getPropertyPassword());
		}
		return api;
	}
	
	private String getPropertyEndpoint() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.PORTAFIB_PLUGIN_FIRMAWEB_URL));
	}

	private String getPropertyUsername() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.PORTAFIB_PLUGIN_FIRMAWEB_USER));
	}

	private String getPropertyPassword() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.PORTAFIB_PLUGIN_FIRMAWEB_PASS));
	}
	
	private String getPropertyLocation() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.PORTAFIB_PLUGIN_FIRMAWEB_LOCATION));
	}
	
	private boolean getPropertyDebug() {
		return getAsBoolean(PropertyConfig.getPropertySuffix(PropertyConfig.PORTAFIB_PLUGIN_FIRMAWEB_DEBUG));
	}
	
	@Override
	public String getEndpointURL() {
		String endpoint = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.PORTAFIB_PLUGIN_FIRMAWEB_ENDPOINT));
		if (Utils.isEmpty(endpoint)) {
			endpoint = getPropertyEndpoint();
		}
		return endpoint;
	}
}