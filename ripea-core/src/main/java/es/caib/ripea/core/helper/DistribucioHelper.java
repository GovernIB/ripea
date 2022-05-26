/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.ws.backofficeintegracio.BackofficeIntegracio;
import es.caib.distribucio.ws.client.BackofficeIntegracioWsClientFactory;

/**
 * Mètodes comuns per cridar WebService de Distribucio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DistribucioHelper {
	
	@Autowired
	private ConfigHelper configHelper;
	
	private String url = configHelper.getConfig("es.caib.ripea.distribucio.backofficeIntegracio.ws.url");
	private String usuari = configHelper.getConfig("es.caib.ripea.distribucio.backofficeIntegracio.ws.username");
	private String contrasenya = configHelper.getConfig("es.caib.ripea.distribucio.backofficeIntegracio.ws.password");
	
	public BackofficeIntegracio getBackofficeIntegracioServicePort() throws IOException {
		return BackofficeIntegracioWsClientFactory.getWsClient(
				url,
				usuari,
				contrasenya);
	}

	
	
}
