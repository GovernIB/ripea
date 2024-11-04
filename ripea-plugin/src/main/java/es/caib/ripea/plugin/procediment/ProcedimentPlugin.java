package es.caib.ripea.plugin.procediment;

import es.caib.ripea.core.api.dto.ProcedimentDto;
import es.caib.ripea.plugin.RipeaEndpointPluginInfo;
import es.caib.ripea.plugin.SistemaExternException;

/**
 * Plugin per a consultar la llista de procediments d'una font externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentPlugin extends RipeaEndpointPluginInfo {

	/**
	 * Retorna el procediment associats a un codi SIA.
	 * @param codiDir3 
				Codi DIR3
	 * @param codiSia
	 *            Codi SIA.
	 * 
	 * @return Procediment.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar el procediment.
	 */
	public ProcedimentDto findAmbCodiSia(String codiDir3, String codiSia) throws SistemaExternException;

	String getUnitatAdministrativa(String codi) throws SistemaExternException;

}
