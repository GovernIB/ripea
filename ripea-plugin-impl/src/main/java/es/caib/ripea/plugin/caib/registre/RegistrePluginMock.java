/**
 * 
 */
package es.caib.ripea.plugin.caib.registre;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.registre.RegistreAnotacioResposta;
import es.caib.ripea.plugin.registre.RegistrePlugin;

/**
 * Implementació de proves del plugin de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistrePluginMock implements RegistrePlugin {

	@Override
	public RegistreAnotacioResposta entradaConsultar(
			String identificador,
			String usuariCodi,
			String entitat) throws SistemaExternException {
		return null;
	}

	@Override
	public RegistreAnotacioResposta sortidaConsultar(
			String identificador,
			String usuariCodi,
			String entitat) throws SistemaExternException {
		return null;
	}

}
