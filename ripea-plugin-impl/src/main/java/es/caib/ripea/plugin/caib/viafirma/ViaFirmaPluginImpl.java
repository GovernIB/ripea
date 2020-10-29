/**
 * 
 */
package es.caib.ripea.plugin.caib.viafirma;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viafirma.documents.sdk.java.model.Device;
import com.viafirma.documents.sdk.java.model.Device.StatusEnum;
import com.viafirma.documents.sdk.java.model.Document;
import com.viafirma.documents.sdk.java.model.Document.TemplateTypeEnum;
import com.viafirma.documents.sdk.java.model.Download;
import com.viafirma.documents.sdk.java.model.Evidence;
import com.viafirma.documents.sdk.java.model.Evidence.TypeEnum;
import com.viafirma.documents.sdk.java.model.Message;
import com.viafirma.documents.sdk.java.model.Notification;
import com.viafirma.documents.sdk.java.model.Policy;
import com.viafirma.documents.sdk.java.model.Signature;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.utils.PropertiesHelper;
import es.caib.ripea.plugin.viafirma.OAuthType;
import es.caib.ripea.plugin.viafirma.ViaFirmaDispositiu;
import es.caib.ripea.plugin.viafirma.ViaFirmaDocument;
import es.caib.ripea.plugin.viafirma.ViaFirmaError;
import es.caib.ripea.plugin.viafirma.ViaFirmaParams;
import es.caib.ripea.plugin.viafirma.ViaFirmaPlugin;
import es.caib.ripea.plugin.viafirma.ViaFirmaResponse;

/**
 * Implementació de del plugin de viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ViaFirmaPluginImpl implements ViaFirmaPlugin {

	@Override
	public ViaFirmaResponse uploadDocument(ViaFirmaParams parametresViaFirma) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut enviar el document a viaFirma";
		ViaFirmaResponse response = new ViaFirmaResponse();
		ViaFirmaError error = new ViaFirmaError();
		Message message = new Message();
		try {
			// Create notification info
            Notification notification = new Notification();
            notification.setText(parametresViaFirma.getTitol());
            notification.setDetail(parametresViaFirma.getDescripcio());
            notification.setDevices(new ArrayList<Device>());
            notification.getDevices().add(convertToDevice(parametresViaFirma.getViaFirmaDispositiu()));
            message.setNotification(notification);
            
            // Create a template document
            Document document = new Document();
            document.setTemplateType(TemplateTypeEnum.base64);
            document.setTemplateReference(parametresViaFirma.getContingut());
            message.setDocument(document);
            
            message.setPolicies(new ArrayList<Policy>());
            Policy policy = new Policy();

            policy.setEvidences(new ArrayList<Evidence>());
            Evidence evidence = new Evidence();
            evidence.setType(TypeEnum.SIGNATURE);
            evidence.setHelpText("User signature");
            evidence.setTypeFormatSign("XADES_B");
            policy.getEvidences().add(evidence);

            policy.setSignatures(new ArrayList<Signature>());
            Signature signature = new Signature();
            signature.setType(com.viafirma.documents.sdk.java.model.Signature.TypeEnum.SERVER);
            signature.setHelpText("Server signature");
            signature.setTypeFormatSign(com.viafirma.documents.sdk.java.model.Signature.TypeFormatSignEnum.PADES_LTA);
            policy.getSignatures().add(signature);

            message.getPolicies().add(policy);
            
            message.setCallbackURL(getCallBackUrl());
            message.setCallbackAuthorization(generateAuthenticationHeader());
            message.setGroupCode(getGroupCodi());
            
            String messageCode = getViaFirmaClient(parametresViaFirma.getCodiUsuari(), parametresViaFirma.getContrasenya()).
				getV3MessagesApi().sendMessage(message);
            
			response.setCodiMissatge(messageCode);
			response.setViaFirmaError(error);
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
		return response;
	}

	@Override
	public ViaFirmaDocument downloadDocument(
			String codiUsuari,
			String contrasenya,
			String messageCode) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut recuperar el document de viaFirma";
		ViaFirmaDocument viaFirmaDocument = new ViaFirmaDocument();
		try {
			Download download = getViaFirmaClient(
					codiUsuari, 
					contrasenya).getV3documentsApi().downloadSigned(messageCode);
			
			if (download != null) {
				viaFirmaDocument.setNomFitxer(download.getFileName());
				viaFirmaDocument.setLink(download.getLink());
				viaFirmaDocument.setExpiracio(download.getExpires());
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
		return viaFirmaDocument;
	}

	@Override
	public List<ViaFirmaDispositiu> getDeviceUser(
			String codiUsuari,
			String contrasenya) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut recuperar els dispositius de l'usuari (" +
				"usuariCodi=" + codiUsuari + ")";
		List<ViaFirmaDispositiu> viaFirmaDispositius = new ArrayList<ViaFirmaDispositiu>();
		try {
			List<Device> devices = getViaFirmaClient(codiUsuari, contrasenya).
					getV3DevicesApi().findDeviceByUser(codiUsuari);
			for (Device device : devices) {
				ViaFirmaDispositiu viaFirmaDispositiu = new ViaFirmaDispositiu();
				viaFirmaDispositiu.setCodi(device.getCode());
				viaFirmaDispositiu.setCodiUsuari(device.getUserCode());
				viaFirmaDispositiu.setCodiAplicacio(device.getAppCode());
				viaFirmaDispositiu.setDescripcio(device.getDescription());
				viaFirmaDispositiu.setEmailUsuari(device.getUserEmail());
				viaFirmaDispositiu.setEstat(device.getStatus().name());
				viaFirmaDispositiu.setIdentificador(device.getUniqueIdentifier());
				viaFirmaDispositiu.setLocal(device.getLocale());
				viaFirmaDispositiu.setTipus(device.getType().name());
				viaFirmaDispositiu.setToken(device.getToken());
				viaFirmaDispositiu.setIdentificadorNacional(device.getUserNationalId());
				viaFirmaDispositius.add(viaFirmaDispositiu);
			}
		} catch (Exception ex) {
			logger.error(errorDescripcio, ex);
		}
		return viaFirmaDispositius;
	}

	private ViaFirmaClient viaFirmaClient;
	private ViaFirmaClient getViaFirmaClient(
			String usuari,
			String contrasenya) throws SistemaExternException {
		if (viaFirmaClient == null) {
			viaFirmaClient = new ViaFirmaClient(
					getProxyHost(),
					getProxyPort(),
					getApiUrl(),
					getConsumerKey(),
					getConsumerSecret(),
					getAuthMode(),
					getAuthenticationType(),
					usuari,
					contrasenya);
		}
		return viaFirmaClient;
	}
	
	private Device convertToDevice(ViaFirmaDispositiu viaFiramDispositiu) throws SistemaExternException {
		Device device = new Device();
		try {
			device.setAppCode(viaFiramDispositiu.getCodiAplicacio());
			device.setCode(viaFiramDispositiu.getCodi());
			device.setDescription(viaFiramDispositiu.getDescripcio());
			device.setLocale(viaFiramDispositiu.getLocal());
			device.setStatus(StatusEnum.valueOf(viaFiramDispositiu.getEstat()));
			device.setToken(viaFiramDispositiu.getToken());
			device.setType(com.viafirma.documents.sdk.java.model.Device.TypeEnum.valueOf(viaFiramDispositiu.getTipus()));
			device.setUniqueIdentifier(viaFiramDispositiu.getIdentificador());
			device.setUserCode(viaFiramDispositiu.getCodiUsuari());
			device.setUserEmail(viaFiramDispositiu.getEmailUsuari());
			device.setUserNationalId(viaFiramDispositiu.getIdentificadorNacional());
		} catch (Exception ex) {
			String errorDescripcio = "Error en la conversió de firmaDispositiu a Device";
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
		return device;
	}

	private String generateAuthenticationHeader() throws SistemaExternException {
		String errorDescripcio = "Error generant aunthentication Header";
		String auth = getCallBackUsername() + ":" + getCallBackPassword();
		String callbackAuthorization;
		try {
			byte[] encodedAuth = Base64.encodeBase64(
	        auth.getBytes(StandardCharsets.ISO_8859_1));
			callbackAuthorization = "Basic " + new String(encodedAuth);
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
        
        return callbackAuthorization;
	}
	private String getApiUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.apiurl");
	}
	private String getConsumerKey() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.consumerkey");
	}
	private String getConsumerSecret() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.consumersecret");
	}
	private String getAuthMode() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.authmode");
	}
	private OAuthType getAuthenticationType() {
		String authenticationType = PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.authtype");
		return OAuthType.valueOf(authenticationType);
	}
	private String getCallBackUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.callback.url");
	}
	private String getCallBackUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.callback.username");
	}
	private String getCallBackPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.callback.password");
	}
	private String getGroupCodi() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.group.codi");
	}
	private String getProxyHost() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.proxy.host");
	}
	private int getProxyPort() {
		String proxyPort = PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.proxy.port");
		if (proxyPort != null) {
			return Integer.valueOf(PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.viafirma.caib.proxy.port"));
		} else {
			return 0;
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ViaFirmaPluginImpl.class);

}
