/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.caib.ripea.core.api.dto.ComunitatDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.NivellAdministracioDto;
import es.caib.ripea.core.api.dto.PaisDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;
import es.caib.ripea.core.api.service.DadesExternesService;
import es.caib.ripea.core.helper.CacheHelper;

/**
 * Implementació dels mètodes per obtenir dades de fonts
 * externes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DadesExternesServiceImpl implements DadesExternesService {

	@Resource
	private CacheHelper cacheHelper;

	@Override
	public List<PaisDto> findPaisos() {
		LOGGER.debug("Cercant tots els paisos");
		return cacheHelper.findPaisos();
	}

	@Override
	public List<ComunitatDto> findComunitats() {
		LOGGER.debug("Cercant totes les comunitats");
		return cacheHelper.findComunitats();
	}

	@Override
	public List<ProvinciaDto> findProvincies() {
		LOGGER.debug("Cercant totes les províncies");
		return cacheHelper.findProvincies();
	}

	@Override
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) {
		LOGGER.debug("Cercant totes les províncies de la comunitat (" +
				"comunitatCodi=" + comunitatCodi + ")");
		return cacheHelper.findProvinciesPerComunitat(comunitatCodi);
	}

	@Override
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) {
		LOGGER.debug("Cercant els municipis de la província (" +
				"provinciaCodi=" + provinciaCodi + ")");
		return cacheHelper.findMunicipisPerProvincia(provinciaCodi);
	}

	@Override
	public List<NivellAdministracioDto> findNivellAdministracions() {
		LOGGER.debug("Cercant els nivells de les administracions");
		return cacheHelper.findNivellAdministracio();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesExternesServiceImpl.class);

}
