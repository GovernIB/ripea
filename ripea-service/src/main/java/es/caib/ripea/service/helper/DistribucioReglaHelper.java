package es.caib.ripea.service.helper;

import com.sun.jersey.api.client.ClientResponse;
import es.caib.ripea.plugin.PropertiesHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.CrearReglaResponseDto;
import es.caib.ripea.service.intf.dto.ReglaDistribucioDto;
import es.caib.ripea.service.intf.dto.StatusEnumDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistribucioReglaHelper  {

    @Autowired private ConfigHelper configHelper;
	
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
	
	public CrearReglaResponseDto canviEstat(
			String sia, 
			boolean activa)  {
		
		logger.debug("Canviant estat de la regla en distribucio (sia=" + sia + ")");
		
		try {
			// Creació del client
			ReglesRestClient client = new ReglesRestClient(
					getServiceUrl(),
					getServiceUsername(),
					getServicePassword(),
					isAutenticacioBasic());

			ClientResponse response = client.canviEstat(
					sia,
					activa);
			int status = response.getStatus();
			String reasonPhrase = response.getStatusInfo().getReasonPhrase();
			String resp = response.getEntity(String.class);

			logger.debug("Resposta de la canvi d'estat de la regla " + status + " " + reasonPhrase + ": " + resp);

			StatusEnumDto statusEnumDto = StatusEnumDto.ERROR;
			if (status == 200) {
				statusEnumDto = StatusEnumDto.OK;
			} else if (response.getStatus() == 404) {
				statusEnumDto = StatusEnumDto.WARNING;
			} else {
				statusEnumDto = StatusEnumDto.ERROR;
				logger.error("Error retornat al canvi d'estat de la regla en distribucio: " + status + " " + reasonPhrase + ": " + resp);
			}

			return new CrearReglaResponseDto(
					statusEnumDto,
					resp);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public ReglaDistribucioDto consultarRegla(String sia)  {
		try {
			ReglesRestClient client = new ReglesRestClient(
					getServiceUrl(),
					getServiceUsername(),
					getServicePassword(),
					isAutenticacioBasic());
			ReglaDistribucioDto regla = client.consultarRegla(sia);
			return regla;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private String getServiceUrl() {
		return PropertiesHelper.getProperties().getProperty(PropertyConfig.DISTRIBUCIO_REGLA_PLUGIN_URL);
	}
	private String getServiceUsername() {
		return PropertiesHelper.getProperties().getProperty(PropertyConfig.DISTRIBUCIO_REGLA_PLUGIN_USR);
	}
	private String getServicePassword() {
		return PropertiesHelper.getProperties().getProperty(PropertyConfig.DISTRIBUCIO_REGLA_PLUGIN_PAS);
	}
	private String getCodiBackoffice() {
		return configHelper.getConfig(PropertyConfig.DISTRIBUCIO_REGLA_PLUGIN_CODI_BACK);
	}
	private boolean isAutenticacioBasic() {
		return configHelper.getAsBoolean(PropertyConfig.DISTRIBUCIO_REGLA_PLUGIN_AUTH_BASIC);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DistribucioReglaHelper.class);
}
