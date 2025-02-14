package es.caib.ripea.service.helper;

import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClientFactory;
import es.caib.ripea.service.intf.config.PropertyConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistribucioHelper {

	@Autowired private ConfigHelper configHelper;

	public BackofficeIntegracioRestClient getBackofficeIntegracioRestClient() {
		String url = configHelper.getEnvironmentProperty(PropertyConfig.DISTRIBUCIO_PLUGIN_URL, null);
		String usuari = configHelper.getEnvironmentProperty(PropertyConfig.DISTRIBUCIO_PLUGIN_USR, null);
		String contrasenya = configHelper.getEnvironmentProperty(PropertyConfig.DISTRIBUCIO_PLUGIN_PAS, null);
		return BackofficeIntegracioRestClientFactory.getRestClient(url, usuari, contrasenya);
	}
}