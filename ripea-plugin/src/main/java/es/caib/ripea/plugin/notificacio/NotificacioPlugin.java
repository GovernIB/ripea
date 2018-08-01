package es.caib.ripea.plugin.notificacio;

import es.caib.ripea.plugin.SistemaExternException;

/**
 * Plugin per a l'enviament de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioPlugin {

	/**
	 * Envia una notificació.
	 * 
	 * @param notificacio
	 * @return la informació resultant de l'enviament de la notificació.
	 * @throws SistemaExternException
	 */
	public RespostaEnviar enviar(
			Notificacio notificacio) throws SistemaExternException;

	/**
	 * Consulta l'estat d'una notificació.
	 * 
	 * @param identificador
	 * @return l'estat de la notificació.
	 * @throws SistemaExternException
	 */
	public RespostaConsultaEstatNotificacio consultarNotificacio(
			String identificador) throws SistemaExternException;

	/**
	 * Consulta l'estat d'una notificació.
	 * 
	 * @param referencia la referència de l'enviament a consultar.
	 * @return l'estat de l'enviament.
	 * @throws SistemaExternException
	 */
	public RespostaConsultaEstatEnviament consultarEnviament(
			String referencia) throws SistemaExternException;

}
