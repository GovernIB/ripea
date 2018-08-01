package es.caib.ripea.plugin.registre;

import es.caib.ripea.plugin.SistemaExternException;

/**
 * Plugin per a la integració amb el registre d'entrades i sortides.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistrePlugin {

	/**
	 * Consulta la informació d'una anotació d'entrada al registre.
	 * 
	 * @param identificador
	 *            L'identificador de l'anotació a consultar.
	 * @param entitat
	 *            El codi de l'entitat a la quan pertany l'usuari.
	 * @return La informació de l'anotació.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public RegistreAnotacioResposta entradaConsultar(
			String identificador,
			String usuariCodi,
			String entitat) throws SistemaExternException;

	/**
	 * Consulta la informació d'una anotació de sortida al registre.
	 * 
	 * @param identificador
	 *            L'identificador de l'anotació a consultar.
	 * @param entitat
	 *            El codi de l'entitat a la quan pertany l'usuari.
	 * @return La informació de l'anotació.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public RegistreAnotacioResposta sortidaConsultar(
			String identificador,
			String usuariCodi,
			String entitat) throws SistemaExternException;

}
