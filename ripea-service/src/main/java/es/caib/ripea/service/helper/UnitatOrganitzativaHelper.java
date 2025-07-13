package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.service.intf.dto.ArbreDto;
import es.caib.ripea.service.intf.dto.ArbreNodeDto;
import es.caib.ripea.service.intf.dto.MunicipiDto;
import es.caib.ripea.service.intf.dto.ProvinciaDto;
import es.caib.ripea.service.intf.dto.TipusViaDto;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;

@Component
public class UnitatOrganitzativaHelper {

	@Autowired private CacheHelper cacheHelper;
	@Autowired private PluginHelper pluginHelper;

	public UnitatOrganitzativaDto findPerEntitatAndCodi(
			String entitatCodi,
			String unitatOrganitzativaCodi) {
		ArbreDto<UnitatOrganitzativaDto> arbre = cacheHelper.findUnitatsOrganitzativesPerEntitat(entitatCodi);
		for (UnitatOrganitzativaDto unitat: arbre.toDadesList()) {
			if (unitat.getCodi().equals(unitatOrganitzativaCodi)) {
				return unitat;
			}
		}
		return null;
	}

	public UnitatOrganitzativaDto findAmbCodi(String unitatOrganitzativaCodi) {
		return pluginHelper.unitatsOrganitzativesFindByCodi(unitatOrganitzativaCodi);
	}
	
	public UnitatOrganitzativaDto findAmbCodiAndAdressafisica(String unitatOrganitzativaCodi) {
		UnitatOrganitzativaDto unitat = pluginHelper.unitatsOrganitzativesFindByCodi(unitatOrganitzativaCodi);
		
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
	
	public List<UnitatOrganitzativaDto> findPath(
			String entitatCodi,
			String unitatOrganitzativaCodi) {
		ArbreDto<UnitatOrganitzativaDto> arbre = cacheHelper.findUnitatsOrganitzativesPerEntitat(entitatCodi);
		List<UnitatOrganitzativaDto> superiors = new ArrayList<UnitatOrganitzativaDto>();
		String codiActual = unitatOrganitzativaCodi;
		do {
			UnitatOrganitzativaDto unitatActual = cercaDinsArbreAmbCodi(
					arbre,
					codiActual);
			if (unitatActual != null) {
				superiors.add(unitatActual);
				codiActual = unitatActual.getCodiUnitatSuperior();
			} else {
				codiActual = null;
			}
		} while (codiActual != null);
		return superiors;
	}

	public ArbreDto<UnitatOrganitzativaDto> findPerEntitatAmbCodisPermesos(
			String entitatCodi,
			Set<String> unitatCodiPermesos) {
		ArbreDto<UnitatOrganitzativaDto> arbre = cacheHelper.findUnitatsOrganitzativesPerEntitat(entitatCodi).clone();
		if (unitatCodiPermesos != null) {
			// Calcula els nodes a "salvar" afegint els nodes permesos
			// i tots els seus pares.
			List<ArbreNodeDto<UnitatOrganitzativaDto>> nodes = arbre.toList();
			Set<String> unitatCodiSalvats = new HashSet<String>();
			for (ArbreNodeDto<UnitatOrganitzativaDto> node: nodes) {
				if (unitatCodiPermesos.contains(node.dades.getCodi())) {
					unitatCodiSalvats.add(node.dades.getCodi());
					ArbreNodeDto<UnitatOrganitzativaDto> pare = node.getPare();
					while (pare != null) {
						unitatCodiSalvats.add(pare.dades.getCodi());
						pare = pare.getPare();
					}
				}
			}
			// Esborra els nodes no "salvats"
			for (ArbreNodeDto<UnitatOrganitzativaDto> node: nodes) {
				if (!unitatCodiSalvats.contains(node.dades.getCodi())) {
					if (node.getPare() != null)
						node.getPare().removeFill(node);
					else
						arbre.setArrel(null);
				}
					
			}
		}
		return arbre;
	}

	public UnitatOrganitzativaDto findConselleria(
			String entitatCodi,
			String unitatOrganitzativaCodi) {
		ArbreDto<UnitatOrganitzativaDto> arbre = cacheHelper.findUnitatsOrganitzativesPerEntitat(entitatCodi).clone();
		UnitatOrganitzativaDto unitatConselleria = null;
		for (ArbreNodeDto<UnitatOrganitzativaDto> node: arbre.toList()) {
			UnitatOrganitzativaDto uo = node.getDades();
			if (uo.getCodi().equals(unitatOrganitzativaCodi)) {
				ArbreNodeDto<UnitatOrganitzativaDto> nodeActual = node;
				while (nodeActual.getNivell() > 1) {
					nodeActual = nodeActual.getPare();
				}
				if (nodeActual.getNivell() == 1)
					unitatConselleria = nodeActual.getDades();
				break;
			}
		}
		return unitatConselleria;
	}



	private UnitatOrganitzativaDto cercaDinsArbreAmbCodi(
			ArbreDto<UnitatOrganitzativaDto> arbre,
			String unitatOrganitzativaCodi) {
		UnitatOrganitzativaDto trobada = null;
		for (UnitatOrganitzativaDto unitat: arbre.toDadesList()) {
			if (unitat.getCodi().equals(unitatOrganitzativaCodi)) {
				trobada = unitat;
				break;
			}
		}
		return trobada;
	}

	private static final Logger logger = LoggerFactory.getLogger(UnitatOrganitzativaHelper.class);
}