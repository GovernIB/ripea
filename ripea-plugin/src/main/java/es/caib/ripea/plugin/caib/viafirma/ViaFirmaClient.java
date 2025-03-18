package es.caib.ripea.plugin.caib.viafirma;

import com.viafirma.documents.sdk.java.ApiException;
import com.viafirma.documents.sdk.java.api.V3Api;
import com.viafirma.documents.sdk.java.api.V3devicesApi;
import com.viafirma.documents.sdk.java.api.V3documentsApi;
import com.viafirma.documents.sdk.java.api.V3messagesApi;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.viafirma.OAuthType;

public class ViaFirmaClient {
	
	public ViaFirmaClient(
			String proxyHost,
			int proxyPort,
			String apiUrl, 
			String consumerKey, 
			String consumerSecret, 
			String authMode,
			OAuthType authenticationType,
			String usuari,
			String contrasenya) throws SistemaExternException {
		V3Api api = new V3Api();
        try {
        	if (proxyHost != null) {
        		api.setProxyHost(proxyHost);
        		api.setProxyPort(proxyPort);
        	}
            api.setBasePath(apiUrl);
            api.setConsumerKey(consumerKey);
            api.setConsumerSecret(consumerSecret);
           if (authenticationType == OAuthType.OAUTH_USER) {
           		api.setUser(usuari);
           		api.setPassword(contrasenya);
           		api.setAuth_mode(authMode);
           		api.generateNewToken();
           }
		} catch (ApiException ex) {
			throw new SistemaExternException("Hi ha hagut un error generant el client de viaFirma", ex);
		}
	}

	public V3devicesApi getV3DevicesApi() {
		return V3devicesApi.getInstance();
	}
	
	public V3messagesApi getV3MessagesApi() {
		return V3messagesApi.getInstance();
	}
	
	public V3documentsApi getV3documentsApi() {
		return V3documentsApi.getInstance();
	}
}
