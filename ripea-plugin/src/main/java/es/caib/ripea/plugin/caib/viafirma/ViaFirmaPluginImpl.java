package es.caib.ripea.plugin.caib.viafirma;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.viafirma.documents.sdk.java.model.Notification.NotificationTypeEnum;
import com.viafirma.documents.sdk.java.model.Param;
import com.viafirma.documents.sdk.java.model.Policy;
import com.viafirma.documents.sdk.java.model.Position;
import com.viafirma.documents.sdk.java.model.Rectangle;
import com.viafirma.documents.sdk.java.model.SharedLink;
import com.viafirma.documents.sdk.java.model.Signature;
import com.viafirma.documents.sdk.java.model.Signature.CertificationLevelEnum;
import com.viafirma.documents.sdk.java.model.Workflow;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.viafirma.OAuthType;
import es.caib.ripea.plugin.viafirma.ViaFirmaDispositiu;
import es.caib.ripea.plugin.viafirma.ViaFirmaDocument;
import es.caib.ripea.plugin.viafirma.ViaFirmaError;
import es.caib.ripea.plugin.viafirma.ViaFirmaParams;
import es.caib.ripea.plugin.viafirma.ViaFirmaPlugin;
import es.caib.ripea.plugin.viafirma.ViaFirmaResponse;
import es.caib.ripea.plugin.viafirma.ViaFirmaTipusDestinatari;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.utils.Utils;

/**
 * Implementació de del plugin de viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ViaFirmaPluginImpl extends RipeaAbstractPluginProperties implements ViaFirmaPlugin {
	
	public ViaFirmaPluginImpl() {
		super();
	}
	public ViaFirmaPluginImpl(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public ViaFirmaResponse uploadDocument(ViaFirmaParams params) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut enviar el document a viaFirma";
		ViaFirmaResponse response = new ViaFirmaResponse();
		ViaFirmaError error = new ViaFirmaError();
		try {
			Message message = (ViaFirmaTipusDestinatari.EMAIL.equals(params.getTipusDestinatari()))
	                ? prepararMessagePerEmail(params)
	                : prepararMessagePerTablet(params);
			
            String messageCode = getViaFirmaClient(params.getCodiUsuari(), params.getContrasenya()).
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

    private Message prepararMessagePerEmail(ViaFirmaParams params) throws SistemaExternException {
        Message message = new Message();

        message.setNotification(buildNotificationForEmail(params));
        message.setDocument(buildDocument(params, false));
        message.setPolicies(buildPolicies(params, false));
        setCommonMessageAttributes(message, params);

        Workflow workflow = new Workflow();
        workflow.setType(com.viafirma.documents.sdk.java.model.Workflow.TypeEnum.WEB);
        message.setWorkflow(workflow);

        return message;
    }

    private Message prepararMessagePerTablet(ViaFirmaParams params) throws SistemaExternException {
        Message message = new Message();

        message.setNotification(buildNotificationForTablet(params));
        message.setDocument(buildDocument(params, true));
        message.setPolicies(buildPolicies(params, true));
        setCommonMessageAttributes(message, params);

        Workflow workflow = new Workflow();
        workflow.setType(com.viafirma.documents.sdk.java.model.Workflow.TypeEnum.APP);
        message.setWorkflow(workflow);

        return message;
    }

    // Construye notificación para email
    private Notification buildNotificationForEmail(ViaFirmaParams params) {
        Notification notification = new Notification();
        notification.setText(params.getTitol());
        notification.setNotificationType(NotificationTypeEnum.MAIL);

        SharedLink shared = new SharedLink();
        shared.setEmail(params.getSignantEmail());
        shared.setSubject("Firma documento viafirma");
        notification.setSharedLink(shared);

        notification.setDetail(params.getDescripcio());

        if (params.isValidateCodeEnabled()) {
            notification.setValidateCode(params.getValidateCode());
        }

        return notification;
    }

    // Construye notificación para tablet
    private Notification buildNotificationForTablet(ViaFirmaParams params) throws SistemaExternException {
        Notification notification = new Notification();
        notification.setText(params.getTitol());
        notification.setDetail(params.getDescripcio());

        List<Device> devices = new ArrayList<>();

        if (params.isDeviceEnabled()) {
            devices.add(convertToDevice(params.getViaFirmaDispositiu(), null));
        } else {
            devices.add(convertToDevice(null, params.getCodiUsuari()));
        }

        notification.setDevices(devices);

        if (params.isValidateCodeEnabled()) {
            notification.setValidateCode(params.getValidateCode());
        }

        return notification;
    }

    // Construye documento base
    private Document buildDocument(ViaFirmaParams params, boolean isTablet) {
        Document document = new Document();
        document.setTemplateType(TemplateTypeEnum.base64);
        document.setTemplateReference(params.getContingut());
        if (!isTablet) {
        	document.setFormRequired(true);
        }
        return document;
    }

    // Construye políticas comunes, ajusta según sea email o tablet
    private List<Policy> buildPolicies(ViaFirmaParams params, boolean isTablet) {
        List<Policy> policies = new ArrayList<>();

        Policy policy = new Policy();
        policy.setEvidences(new ArrayList<>());

        Evidence evidence = new Evidence();
        evidence.setType(TypeEnum.SIGNATURE);
        evidence.setHelpText("Firma de " + cleanName(params.getSignantNom()));

        String helpDetail = String.format("Yo, %s, con NIF número %s he leído y entendido el contenido del documento que voy a firmar.",
                cleanName(params.getSignantNom()),
                params.getSignantNif());

        if (params.getObservaciones() != null && !params.getObservaciones().isEmpty()) {
            helpDetail += " [Observaciones: " + params.getObservaciones() + "]";
        }

        evidence.setHelpDetail(helpDetail);

        if (isTablet) {
            evidence.setTypeFormatSign("XADES_B");
        }

        if (!isTablet) {
            List<Position> positions = new ArrayList<>();
            Position position = new Position();
            Rectangle rectangle = new Rectangle();
            rectangle.setHeight(62);
            rectangle.setWidth(125);
            //rectangle.setX(105);
            //rectangle.setY(532);
            position.setPage(0);
            position.setRectangle(rectangle);
            positions.add(position);
            
            evidence.setPositions(positions);
        }

        policy.getEvidences().add(evidence);

        policy.setSignatures(new ArrayList<>());

        Signature signature = new Signature();
        if (isTablet) {
            signature.setType(com.viafirma.documents.sdk.java.model.Signature.TypeEnum.SERVER);
            signature.setHelpText("Server signature");
            signature.setTypeFormatSign(com.viafirma.documents.sdk.java.model.Signature.TypeFormatSignEnum.PADES_LTA);
            signature.setCertificationLevel(CertificationLevelEnum.NOT_CERTIFIED);
        } else {
            signature.setType(com.viafirma.documents.sdk.java.model.Signature.TypeEnum.SERVER);
        }

        policy.getSignatures().add(signature);

        policies.add(policy);

        return policies;
    }

    private void setCommonMessageAttributes(Message message, ViaFirmaParams params) throws SistemaExternException {
        message.setMetadataList(buildMetadataList(params));
        message.setCallbackURL(getCallBackUrl());
        message.setCallbackAuthorization(generateAuthenticationHeader());
        message.setGroupCode(getGroupCodi());
    }

    private List<Param> buildMetadataList(ViaFirmaParams params) {
        List<Param> metadataList = new ArrayList<>();

        metadataList.add(createParam("TITULAR", cleanName(params.getSignantNom())));
        metadataList.add(createParam("TITULAR_DNI", params.getSignantNif()));
        metadataList.add(createParam("EXPEDIENTE", params.getExpedientCodi()));
        metadataList.add(createParam("OBSERVACIONES", params.getObservaciones()));

        return metadataList;
    }

    private Param createParam(String key, String value) {
        Param param = new Param();
        param.setKey(key);
        param.setValue(value != null ? value : "");
        return param;
    }

    private String cleanName(String name) {
        return name != null ? name.replaceAll(",", "") : "";
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
	
	private Device convertToDevice(
			ViaFirmaDispositiu viaFiramDispositiu,
			String codiUsuari) throws SistemaExternException {
		Device device = new Device();
		try {
			if (viaFiramDispositiu != null) { //### s'ha informat un dispositiu
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
			} else {
				device.setAppCode(getAppCodi());
				device.setCode(codiUsuari);
				device.setUserCode(codiUsuari);
			}
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
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_URL));
	}
	private String getConsumerKey() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_CONSUMER_KEY));
	}
	private String getConsumerSecret() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_CONSUMER_SECRET));
	}
	private String getAuthMode() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_AUTH_MODE));
	}
	private OAuthType getAuthenticationType() {
		String authenticationType = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_AUTH_TYPE));
		return OAuthType.valueOf(authenticationType);
	}
	private String getCallBackUrl() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_CALLBACK_URL));
	}
	private String getCallBackUsername() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_CALLBACK_USR));
	}
	private String getCallBackPassword() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_CALLBACK_PAS));
	}
	private String getGroupCodi() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_GROUP));
	}
	private String getProxyHost() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_PROXY_HOST));
	}
	private String getAppCodi() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_CAIB_APP_CODE));
	}
	private int getProxyPort() {
		String proxyPort = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_PROXY_PORT));
		if (proxyPort != null) {
			return Integer.valueOf(proxyPort);
		} else {
			return 0;
		}
	}
	@Override
	public String getEndpointURL() {
		String endpoint = PropertyConfig.getPropertySuffix(PropertyConfig.VIAFIRMA_PLUGIN_ENDPOINT_NAME);
		if (Utils.isEmpty(endpoint)) {
			endpoint = getApiUrl();
		}
		return endpoint;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ViaFirmaPluginImpl.class);
}