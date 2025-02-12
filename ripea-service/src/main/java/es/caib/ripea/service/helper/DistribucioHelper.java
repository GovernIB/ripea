/**
 * 
 */
package es.caib.ripea.service.helper;

import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mètodes comuns per cridar WebService de Distribucio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DistribucioHelper {

	@Autowired
	private ConfigHelper configHelper;

	public BackofficeIntegracioRestClient getBackofficeIntegracioRestClient() {
		String url = configHelper.getEnvironmentProperty("es.caib.ripea.distribucio.backofficeIntegracio.ws.url", null);
		String usuari = configHelper.getEnvironmentProperty("es.caib.ripea.distribucio.backofficeIntegracio.ws.username", null);
		String contrasenya = configHelper.getEnvironmentProperty("es.caib.ripea.distribucio.backofficeIntegracio.ws.password", null);
		return BackofficeIntegracioRestClientFactory.getRestClient(url, usuari, contrasenya);
	}

}
