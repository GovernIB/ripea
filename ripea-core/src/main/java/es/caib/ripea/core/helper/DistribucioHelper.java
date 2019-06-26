/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.IOException;

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

	
	private static String url = PropertiesHelper.getProperties().getProperty("es.caib.ripea.distribucio.backofficeIntegracio.ws.url");
	private static String usuari = PropertiesHelper.getProperties().getProperty("es.caib.ripea.distribucio.backofficeIntegracio.ws.username");
	private static String contrasenya = PropertiesHelper.getProperties().getProperty("es.caib.ripea.distribucio.backofficeIntegracio.ws.password");
	
	public static BackofficeIntegracio getBackofficeIntegracioServicePort() throws IOException {
		return BackofficeIntegracioWsClientFactory.getWsClient(
				url,
				usuari,
				contrasenya);
	}

	
	
}
