package es.caib.ripea.core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.client.ClientResponse;

import es.caib.ripea.core.api.dto.CrearReglaResponseDto;
import es.caib.ripea.core.api.dto.StatusEnumDto;
import es.caib.ripea.plugin.PropertiesHelper;

@Component
public class DistribucioReglaHelper  {

    @Autowired
	private ConfigHelper configHelper;
	
	public CrearReglaResponseDto crearRegla(
			String entitat, 
			String sia)  {
		
		logger.debug("Creant regla en distribucio (" + "entitat=" + entitat + ", sia=" + sia + ")");
		
		try {
			// Creació del client
			ReglesRestClient client = new ReglesRestClient(
					getServiceUrl(),
					getServiceUsername(),
					getServicePassword(),
					isAutenticacioBasic());

			ClientResponse response = client.add(
					entitat,
					sia,
					getCodiBackoffice());
			int status = response.getStatus();
			String reasonPhrase = response.getStatusInfo().getReasonPhrase();
			String resp = response.getEntity(String.class);

			logger.debug("Resposta de la creació de la regla " + status + " " + reasonPhrase + ": " + resp);

			StatusEnumDto statusEnumDto = StatusEnumDto.ERROR;
			if (status == 200) {
				statusEnumDto = StatusEnumDto.OK;
			} else if (response.getStatus() == 404) {
				statusEnumDto = StatusEnumDto.WARNING;
			} else {
				statusEnumDto = StatusEnumDto.ERROR;
				logger.error("Error retornat al crear regla en distribucio: " + status + " " + reasonPhrase + ": " + resp);
			}

			return new CrearReglaResponseDto(
					statusEnumDto,
					resp);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}
	
	


	private String getServiceUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.distribucio.regla.ws.url");
	}
	private String getServiceUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.distribucio.regla.ws.username");
	}
	private String getServicePassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.distribucio.regla.ws.password");
	}
	
	private String getCodiBackoffice() {
		return configHelper.getConfig("es.caib.ripea.distribucio.regla.ws.codi.backoffice");
	}
	
	private boolean isAutenticacioBasic() {
		return configHelper.getAsBoolean("es.caib.ripea.distribucio.regla.autenticacio.basic");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DistribucioReglaHelper.class);

}
