/**
 * 
 */
package es.caib.ripea.service.intf.config;

/**
 * Configuració de les propietats de l'aplicació.
 * 
 * @author Limit Tecnologies
 */
public class PropertyConfig {

	private static final String PROPERTY_PREFIX = "es.caib.ripea.";

	public static final String APP_NAME = PROPERTY_PREFIX + "app.name";
	public static final String APP_URL = PROPERTY_PREFIX + "app.url";

	public static final String MAIL_FROM = PROPERTY_PREFIX + "mail.from";
	public static final String FILES_PATH = PROPERTY_PREFIX + "files.path";
	public static final String DEFAULT_AUDITOR = PROPERTY_PREFIX + "default.auditor";

	public static final String PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";
	public static final String PERSISTENCE_TRANSACTION_MANAGER_ENABLED = PROPERTY_PREFIX + "persist.transaction-manager.enabled";

	// 1. Enviament d'execucions massives
	public static final String EXECUTAR_EXECUCIONS_MASSIVES_RATE = PROPERTY_PREFIX + "segonpla.massives.periode.comprovacio";

	// 2. Enviament de notificacions registrades a Notific@
	public static final String PROCESSAR_ANOTACIONS_PETICIONS_PENDENTS_RATE = "es.caib.ripea.tasca.consulta.anotacio.temps.espera.execucio";

	// 2.5
	public static final String REINTENTAR_CANVI_ESTAT_DISTRIBUCIO = "es.caib.ripea.segonpla.reintentar.anotacions.pendents.enviar.distribucio";

	// 3. Buidar cachés dominis
	public static final String BUIDAR_CACHES_DOMINIS_RATE = "es.caib.ripea.dominis.cache.execucio";

	// Enviar emails comentaris metaexpedients
	public static final String ENVIAR_EMAILS_PENDENTS_PROCEDIMENT_COMENTARI_CRON = "es.caib.ripea.segonpla.email.enviament.procediment.comentari.cron";

	// 4. Enviar emails pendents agrupats
	public static final String ENVIAR_EMAILS_PENDENTS_AGRUPATS_CRON = "es.caib.ripea.segonpla.email.enviament.agrupat.cron";

	// 5. Guardar en arxiu continguts pendents
	public static final String GUARDAR_ARXIU_CONTINGUTS_PENDENTS = "es.caib.ripea.segonpla.guardar.arxiu.continguts.pendents";

	// 6. Guardar en arxiu interessats
	public static final String GUARDAR_ARXIU_INTERESSATS = "es.caib.ripea.segonpla.guardar.arxiu.interessats";

	// 7. Actualitzar procediments
	public static final String ACTUALITZAR_PROCEDIMENTS = "es.caib.ripea.procediment.actualitzar.cron";

	// 8. Consulta de canvis en l'organigrama
	public static final String CONSULTA_CANVIS_ORGANIGRAMA = "es.caib.ripea.organs.consulta.canvis";

	// 9. Consulta expedients pendents de tancar a l'arxiu
	public static final String TANCAMENT_LOGIC_CRON = "es.caib.ripea.expedient.tancament.logic.cron";

}
