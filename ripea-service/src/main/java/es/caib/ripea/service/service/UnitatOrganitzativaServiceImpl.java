package es.caib.ripea.service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.UnitatOrganitzativaHelper;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.service.UnitatOrganitzativaService;

@Service
public class UnitatOrganitzativaServiceImpl implements UnitatOrganitzativaService {

	@Autowired private CacheHelper cacheHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private UnitatOrganitzativaHelper unitatOrganitzativaHelper;

	@Override
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) {
		return cacheHelper.findUnitatsOrganitzativesPerEntitat(entitatCodi).toDadesList();
	}

	@Override
	public UnitatOrganitzativaDto findByCodi(String unitatOrganitzativaCodi) {
		return unitatOrganitzativaHelper.findAmbCodiAndAdressafisica(unitatOrganitzativaCodi);
	}

	@Override
	public List<UnitatOrganitzativaDto> findByFiltre(
			String codiDir3, 
			String denominacio,
			String nivellAdm, 
			String comunitat, 
			String provincia, 
			String localitat, 
			Boolean arrel) {
		return pluginHelper.unitatsOrganitzativesFindByFiltre(
				codiDir3, 
				denominacio,
				nivellAdm, 
				comunitat, 
				provincia, 
				localitat, 
				arrel);
	}
}