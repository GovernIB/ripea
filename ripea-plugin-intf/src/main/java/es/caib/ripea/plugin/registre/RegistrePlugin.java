package es.caib.ripea.plugin.registre;

import es.caib.ripea.plugin.RipeaEndpointPluginInfo;

public interface RegistrePlugin extends RipeaEndpointPluginInfo {
	
	/**
	 * Recupera un registre de sortida
	 * 
	 * @param codiDir3Entitat codi DIR3 de l'entitat
	 * @param numeroRegistre número de l'assentament que es vol recuperar
	 * @param tipusOperacio	indicar si és un registre d'entrada o sortida
	 * @param ambAnnexos indicar si s'han de recuperar els annexos
	 * @return Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * @throws RegistrePluginException
	 */
	public RespostaConsultaRegistre obtenerAsientoRegistral(
			String codiDir3Entitat, 
			String numeroRegistre, 
			Long tipusOperacio, 
			boolean ambAnnexos);
	
}
