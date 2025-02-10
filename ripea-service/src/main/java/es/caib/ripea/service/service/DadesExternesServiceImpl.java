/**
 * 
 */
package es.caib.ripea.service.service;

import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.DadesExternesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
		if (comunitatCodi != null)
			return cacheHelper.findProvinciesPerComunitat(comunitatCodi);
		return new ArrayList<>();
	}

	@Override
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) {
		LOGGER.debug("Cercant els municipis de la província (" +
				"provinciaCodi=" + provinciaCodi + ")");
		if (provinciaCodi != null)
			return cacheHelper.findMunicipisPerProvincia(provinciaCodi);
		return new ArrayList<>();
	}
	
	@Override
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(String provinciaCodi) {
		LOGGER.debug("Cercant els municipis de la província per a consultes a PINBAL (" +
				"provinciaCodi=" + provinciaCodi + ")");
		if (provinciaCodi != null)
			return cacheHelper.findMunicipisPerProvinciaPinbal(provinciaCodi);
		return new ArrayList<>();
	}


	@Override
	public List<NivellAdministracioDto> findNivellAdministracions() {
		LOGGER.debug("Cercant els nivells de les administracions");
		return cacheHelper.findNivellAdministracio();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesExternesServiceImpl.class);

}
