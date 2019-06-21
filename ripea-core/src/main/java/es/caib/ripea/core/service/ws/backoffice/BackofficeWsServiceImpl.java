package es.caib.ripea.core.service.ws.backoffice;


import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.ws.backoffice.Backoffice;
import es.caib.ripea.core.helper.ExpedientPeticioHelper;



/**
 * Implementació dels mètodes per al servei de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "Backoffice",
		serviceName = "BackofficeService",
		portName = "BackofficeServicePort",
		endpointInterface = "es.caib.ripea.core.api.service.ws.BackofficeWsServiceBean",
		targetNamespace = "http://www.caib.es/distribucio/ws/backoffice")
public class BackofficeWsServiceImpl implements Backoffice {

	@Autowired
	private ExpedientPeticioHelper expedientPeticioHelper;
	
	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {
		try {
			
			expedientPeticioHelper.crearExpedientsPeticions(ids);

		} catch (Exception ex) {
			logger.error(
					"Error al comunicar anotacions pendents" + ex);
			throw new RuntimeException(ex);
		}
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BackofficeWsServiceImpl.class);
}
