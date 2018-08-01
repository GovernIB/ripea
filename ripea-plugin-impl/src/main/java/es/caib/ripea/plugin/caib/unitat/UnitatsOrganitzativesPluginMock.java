/**
 * 
 */
package es.caib.ripea.plugin.caib.unitat;

import java.util.ArrayList;
import java.util.List;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;

/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * La estructura d'unitats és la següent:
 *   arrel: Limit Tecnologies (00000000T)
 *   filla: Departament de programari (12345678Z)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganitzativesPluginMock implements UnitatsOrganitzativesPlugin {

	private static final String CODI_UNITAT_ARREL = "00000000T";
	private static final String CODI_UNITAT_FILLA = "12345678Z";

	private List<UnitatOrganitzativa> unitats;

	@Override
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi) throws SistemaExternException {
		List<UnitatOrganitzativa> resposta = new ArrayList<UnitatOrganitzativa>();
		resposta.add(findAmbCodi(pareCodi));
		for (UnitatOrganitzativa unitat: getUnitats()) {
			if (unitat.getCodiUnitatSuperior() != null && unitat.getCodiUnitatSuperior().equals(pareCodi)) {
				resposta.add(unitat);
			}
		}
		return resposta;
	}

	@Override
	public UnitatOrganitzativa findAmbCodi(
			String codi) throws SistemaExternException {
		for (UnitatOrganitzativa unitat: getUnitats()) {
			if (unitat.getCodi().equals(codi))
				return unitat;
		}
		return null;
	}

	/*@Override
	public List<UnitatOrganitzativaD3> cercaUnitatsD3(
			String codiUnitat, 
			String denominacioUnitat,
			Long codiNivellAdministracio, 
			Long codiComunitat, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long codiProvincia, 
			String codiLocalitat) throws SistemaExternException {
		throw new SistemaExternException("Mètode no implementat");
	}*/

	private List<UnitatOrganitzativa> getUnitats() {
		if (unitats == null) {
			unitats = new ArrayList<UnitatOrganitzativa>();
			UnitatOrganitzativa pare = new UnitatOrganitzativa();
			pare.setCodi(CODI_UNITAT_ARREL);
			pare.setDenominacio("Límit Tecnologies");
			unitats.add(pare);
			UnitatOrganitzativa fill = new UnitatOrganitzativa();
			fill.setCodi(CODI_UNITAT_FILLA);
			fill.setDenominacio("Departament de programari");
			fill.setCodiUnitatArrel(CODI_UNITAT_ARREL);
			fill.setCodiUnitatSuperior(CODI_UNITAT_ARREL);
			unitats.add(fill);
		}
		return unitats;
	}

	@Override
	public List<UnitatOrganitzativa> cercaUnitats(String codiUnitat, String denominacioUnitat,
			Long codiNivellAdministracio, Long codiComunitat, Boolean ambOficines, Boolean esUnitatArrel,
			Long codiProvincia, String codiLocalitat) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

}
