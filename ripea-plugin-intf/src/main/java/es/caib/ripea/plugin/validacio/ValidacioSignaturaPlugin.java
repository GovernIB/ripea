package es.caib.ripea.plugin.validacio;

import es.caib.ripea.plugin.RipeaEndpointPluginInfo;
import es.caib.ripea.plugin.SistemaExternException;

/**
 * Interfície del plugin per a la validació de firmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ValidacioSignaturaPlugin extends RipeaEndpointPluginInfo {

	public ValidaSignaturaResposta validaSignatura(byte[] documentContingut) throws SistemaExternException;

}
