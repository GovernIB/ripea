package es.caib.ripea.plugin.firmaservidor;

import es.caib.ripea.plugin.SistemaExternException;

/**
 * Plugin permetre la signatura de documents en servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaServidorPlugin {

	SignaturaResposta firmar(
			String nom,
			String motiu,
			byte[] contingut,
			String idioma, 
			String contentType) throws SistemaExternException;

	public static enum TipusFirma {
		PADES,
		CADES,
		XADES
	}

}
