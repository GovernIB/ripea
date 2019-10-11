/**
 * 
 */
package es.caib.ripea.plugin.usuari;

import java.util.List;

import es.caib.ripea.plugin.SistemaExternException;


/**
 * Plugin per a consultar les dades d'una font d'usuaris externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadesUsuariPlugin {

	/**
	 * Retorna la informació d'un usuari donat el codi d'usuari.
	 * 
	 * @param usuariCodi
	 *            Codi de l'usuari que es vol consultar.
	 * @return la informació de l'usuari o null si no se troba.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public DadesUsuari findAmbCodi(String usuariCodi) throws SistemaExternException;

	/**
	 * Retorna la llista d'usuaris d'un grup.
	 * 
	 * @param usuariNif
	 *            Codi del grup que es vol consultar.
	 * @return La llista d'usuaris del grup.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public List<DadesUsuari> findAmbGrup(String grupCodi) throws SistemaExternException;
	
	
//	/**
//	* Retorna la llista d'usuaris que coincideixen amb el filtre especificat.
//	*
//	* @param filtre
//	*            Filtre per a fer la consulta.
//	* @return La llista d'usuaris que coincideixen amb el filtre.
//	* @throws SistemaExternException
//	*            Si es produeix un error al consultar les dades de l'usuari.
//	*/
//	public List<DadesUsuari> findAmbFiltre(String filtre) throws SistemaExternException;

}
