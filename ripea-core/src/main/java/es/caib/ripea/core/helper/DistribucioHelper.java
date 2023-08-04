/**
 * 
 */
package es.caib.ripea.core.helper;

import org.springframework.stereotype.Component;

import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClientFactory;

/**
 * Mètodes comuns per cridar WebService de Distribucio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DistribucioHelper {
	
	private static String url = ConfigHelper.JBossPropertiesHelper.getProperties().getProperty("es.caib.ripea.distribucio.backofficeIntegracio.ws.url");
	private static String usuari = ConfigHelper.JBossPropertiesHelper.getProperties().getProperty("es.caib.ripea.distribucio.backofficeIntegracio.ws.username");
	private static String contrasenya = ConfigHelper.JBossPropertiesHelper.getProperties().getProperty("es.caib.ripea.distribucio.backofficeIntegracio.ws.password");
	
	public static BackofficeIntegracioRestClient getBackofficeIntegracioRestClient() {
		return BackofficeIntegracioRestClientFactory.getRestClient(url, usuari, contrasenya);
	}

	
	
}
