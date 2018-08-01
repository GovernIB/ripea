/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ComunitatDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.PaisDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;

/**
 * Mètodes comuns per a gestionar les alertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DadesExternesHelper {

	@Autowired
	private CacheHelper cacheHelper;



	public PaisDto getPaisAmbCodi(
			String paisCodi) {
		PaisDto pais = null;
		List<PaisDto> paisos = cacheHelper.findPaisos();
		if (paisos != null) {
			for (PaisDto p: paisos) {
				if (paisCodi.equals(p.getCodi())) {
					pais = p;
					break;
				}
			}
		}
		return pais;
	}

	public ComunitatDto getComunitatAmbCodi(
			String comunitatCodi) {
		ComunitatDto comunitat = null;
		List<ComunitatDto> comunitats = cacheHelper.findComunitats();
		if (comunitats != null) {
			for (ComunitatDto c: comunitats) {
				if (comunitatCodi.equals(c.getCodi())) {
					comunitat = c;
					break;
				}
			}
		}
		return comunitat;
	}

	public ProvinciaDto getProvinciaAmbCodi(
			String provinciaCodi) {
		ProvinciaDto provincia = null;
		List<ProvinciaDto> provincies = cacheHelper.findProvincies();
		if (provincies != null) {
			for (ProvinciaDto p: provincies) {
				if (provinciaCodi.equals(p.getCodi())) {
					provincia = p;
					break;
				}
			}
		}
		return provincia;
	}

	public ProvinciaDto getProvinciaAmbCodi(
			String comunitatCodi,
			String provinciaCodi) {
		ProvinciaDto provincia = null;
		List<ProvinciaDto> provincies = cacheHelper.findProvinciesPerComunitat(comunitatCodi);
		if (provincies != null) {
			for (ProvinciaDto p: provincies) {
				if (provinciaCodi.equals(p.getCodi())) {
					provincia = p;
					break;
				}
			}
		}
		return provincia;
	}

	public MunicipiDto getMunicipiAmbCodi(
			String provinciaCodi,
			String municipiCodi) {
		MunicipiDto municipi = null;
		List<MunicipiDto> municipis = cacheHelper.findMunicipisPerProvincia(provinciaCodi);
		if (municipis != null) {
			for (MunicipiDto m: municipis) {
				if (municipiCodi.equals(m.getCodi())) {
					municipi = m;
					break;
				}
			}
		}
		return municipi;
	}

}
