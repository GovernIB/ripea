/**
 * 
 */
package es.caib.ripea.service.service;

import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.dto.MunicipiDto;
import es.caib.ripea.service.intf.dto.ProvinciaDto;
import es.caib.ripea.service.intf.dto.TipusViaDto;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.service.UnitatOrganitzativaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class UnitatOrganitzativaServiceImpl implements UnitatOrganitzativaService {

	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private PluginHelper pluginHelper;



	@Override
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) {
		return cacheHelper.findUnitatsOrganitzativesPerEntitat(entitatCodi).toDadesList();
	}

	@Override
	public UnitatOrganitzativaDto findByCodi(
			String unitatOrganitzativaCodi) {
		UnitatOrganitzativaDto unitat = pluginHelper.unitatsOrganitzativesFindByCodi(
				unitatOrganitzativaCodi);
		if (unitat != null) {
			unitat.setAdressa(
					getAdressa(
							unitat.getTipusVia(), 
							unitat.getNomVia(), 
							unitat.getNumVia()));
			if (unitat.getCodiPais() != null && !"".equals(unitat.getCodiPais())) {
				unitat.setCodiPais(("000" + unitat.getCodiPais()).substring(unitat.getCodiPais().length()));
			}
			if (unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
				unitat.setCodiComunitat(("00" + unitat.getCodiComunitat()).substring(unitat.getCodiComunitat().length()));
			}
			if ((unitat.getCodiProvincia() == null || "".equals(unitat.getCodiProvincia())) && 
					unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
				List<ProvinciaDto> provincies = cacheHelper.findProvinciesPerComunitat(unitat.getCodiComunitat());
				if (provincies != null && provincies.size() == 1) {
					unitat.setCodiProvincia(provincies.get(0).getCodi());
				}		
			}
			if (unitat.getCodiProvincia() != null && !"".equals(unitat.getCodiProvincia())) {
				unitat.setCodiProvincia(("00" + unitat.getCodiProvincia()).substring(unitat.getCodiProvincia().length()));
				if (unitat.getLocalitat() == null && unitat.getNomLocalitat() != null) {
					MunicipiDto municipi = findMunicipiAmbNom(
							unitat.getCodiProvincia(), 
							unitat.getNomLocalitat());
					if (municipi != null)
						unitat.setLocalitat(municipi.getCodi());
					else
						logger.error("UNITAT ORGANITZATIVA. No s'ha trobat la localitat amb el nom: '" + unitat.getNomLocalitat() + "'");
				}
			}
		}
		return unitat;
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



	private String getAdressa(
			Long tipusVia,
			String nomVia,
			String numVia) {
		String adressa = "";
		if (tipusVia != null) {
			List<TipusViaDto> tipus = cacheHelper.findTipusVia();
			for (TipusViaDto tvia: tipus) {
				if (tvia.getCodi().equals(tipusVia)) {
					adressa = tvia.getDescripcio() + " ";
					break;
				}
			}
		}
		adressa += nomVia;
		if (numVia != null) {
			adressa += ", " + numVia;
		}
		return adressa;
	}

	private MunicipiDto findMunicipiAmbNom(
			String provinciaCodi,
			String municipiNom) {
		MunicipiDto municipi = null;
		List<MunicipiDto> municipis = cacheHelper.findMunicipisPerProvincia(provinciaCodi);
		if (municipis != null) {
			for (MunicipiDto mun: municipis) {
				if (mun.getNom().equalsIgnoreCase(municipiNom)) { 
					municipi = mun;
					break;
				}
			}
		}
		return municipi;
	}

	private static final Logger logger = LoggerFactory.getLogger(UnitatOrganitzativaServiceImpl.class);

}
