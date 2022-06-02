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
@Component
public class DistribucioHelper {
	
	@Autowired
	private ConfigHelper configHelper;

	
	public BackofficeIntegracio getBackofficeIntegracioServicePort() throws IOException {
		
		String url = configHelper.getConfig("es.caib.ripea.distribucio.backofficeIntegracio.ws.url");
		String usuari = configHelper.getConfig("es.caib.ripea.distribucio.backofficeIntegracio.ws.username");
		String contrasenya = configHelper.getConfig("es.caib.ripea.distribucio.backofficeIntegracio.ws.password");
		
		return BackofficeIntegracioWsClientFactory.getWsClient(
				url,
				usuari,
				contrasenya);
	}

	
	
}
