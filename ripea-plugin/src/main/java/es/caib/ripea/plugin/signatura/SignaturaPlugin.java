package es.caib.ripea.plugin.signatura;

import es.caib.ripea.plugin.SistemaExternException;

/**
 * Plugin permetre la signatura de documents des del servidor de Ripea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SignaturaPlugin {

	public byte[] signar(
			String id, 
			String nom, 
			String motiu,
			String tipusFirma,
			byte[] contingut) throws SistemaExternException;

}
