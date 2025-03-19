package es.caib.ripea.plugin.caib.distribucio;

import java.util.Properties;

import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClientFactory;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.distribucio.DistribucioPlugin;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DistribucioPluginImpl extends RipeaAbstractPluginProperties implements DistribucioPlugin {

	public DistribucioPluginImpl(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	@Override
	public AnotacioRegistreEntrada consulta(AnotacioRegistreId anotacioRegistreId) throws SistemaExternException {
		try {
			return getRestClient().consulta(anotacioRegistreId);
		} catch (Exception e) {
			log.error("No s'ha pogut consultar la anotació", e);
			throw new SistemaExternException("No s'ha pogut consultar la anotació", e);
		}
	}

	@Override
	public void canviEstat(AnotacioRegistreId anotacioRegistreId, Estat estat, String observacions) throws SistemaExternException {
		try {
			getRestClient().canviEstat(anotacioRegistreId, estat, observacions);
		} catch (Exception e) {
			log.error("No s'ha pogut canviar l'estat de la anotació", e);
			throw new SistemaExternException("No s'ha pogut canviar l'estat de la anotació", e);
		}
	}
	
	private BackofficeIntegracioRestClient getRestClient() {
		BackofficeIntegracioRestClient birt = BackofficeIntegracioRestClientFactory.getRestClient(
				getServiceUrl(),
				getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.DISTRIBUCIO_PLUGIN_USR)),
				getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.DISTRIBUCIO_PLUGIN_PAS)));
		return birt;
	}
	
	@Override
	public String getEndpointURL() {
		String endpoint = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.DISTRIBUCIO_PLUGIN_ENDPOINT));
		if (Utils.isEmpty(endpoint)) {
			endpoint = getServiceUrl();
		}
		return endpoint;
	}
	
	private String getServiceUrl() {
		return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.DISTRIBUCIO_PLUGIN_URL));
	}

}
