/**
 * 
 */
package es.caib.ripea.plugin.caib.usuari;

import java.util.ArrayList;
import java.util.List;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.plugin.usuari.DadesUsuariPlugin;

/**
 * Implementació de test del plugin de consulta de dades d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginMock implements DadesUsuariPlugin {


	@Override
	public List<String> findRolsAmbCodi(String usuariCodi) throws SistemaExternException {
		ArrayList<String> rols = new ArrayList<String>();
		rols.add("IPA_ADMIN");
		rols.add("IPA_USER");
		rols.add("IPA_SUPER");
		return rols;
	}

	
	@Override
	public DadesUsuari findAmbCodi(
			String usuariCodi) throws SistemaExternException {
		DadesUsuari dadesUsuari = new DadesUsuari();
		dadesUsuari.setCodi(usuariCodi);
		dadesUsuari.setNomSencer(usuariCodi + " " + usuariCodi);
		dadesUsuari.setNom(usuariCodi);
		dadesUsuari.setLlinatges(usuariCodi);
		dadesUsuari.setNif("12345678Z");
		dadesUsuari.setEmail(usuariCodi + "@aqui.es");
		return dadesUsuari;
	}

	@Override
	public List<DadesUsuari> findAmbGrup(
			String grupCodi) throws SistemaExternException {
		throw new SistemaExternException("Mètode no implementat");
	}
	
	
	@Override
	public List<DadesUsuari> findAmbFiltre(String filtre) throws SistemaExternException{
		
		return null;
	}

}
