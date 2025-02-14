package es.caib.ripea.service.intf.config;

public class PropertyConfig {

	private static final String PROPERTY_PREFIX = "es.caib.ripea.";

	public static final String APP_NAME = PROPERTY_PREFIX + "app.name";
	public static final String APP_URL = PROPERTY_PREFIX + "app.url";

	public static final String MAIL_FROM = PROPERTY_PREFIX + "mail.from";
	public static final String FILES_PATH = PROPERTY_PREFIX + "files.path";
	public static final String DEFAULT_AUDITOR = PROPERTY_PREFIX + "default.auditor";

	public static final String PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";
	public static final String PERSISTENCE_TRANSACTION_MANAGER_ENABLED = PROPERTY_PREFIX + "persist.transaction-manager.enabled";

	public static final String APP_DATA_DIR = "es.caib.ripea.app.data.dir";
	public static final String EMAIL_REMITENT = "es.caib.ripea.email.remitent";
	public static final String BASE_URL = "es.caib.ripea.base.url";
	
	public static final String PINBAL_BASE_URL = "es.caib.ripea.pinbal.base.url";
	public static final String PINBAL_USER = "es.caib.ripea.pinbal.user";
	public static final String PINBAL_PASS = "es.caib.ripea.pinbal.password";
	public static final String PINBAL_BASIC_AUTH = "es.caib.ripea.pinbal.basic.auth";
	public static final String PINBAL_ENDPOINT_DESC = "es.caib.ripea.pinbal.endpointName";
	
	public static final String ARXIU_MAX_MB = "es.caib.ripea.segonpla.arxiu.maxMb";
	public static final String SEGON_PLA_TIMEOUT = "es.caib.ripea.segonpla.arxiu.maxTempsExec";
	public static final String SCAN_ACTIU = "es.caib.ripea.document.nou.escanejar.actiu";
	
	public static final String PROCESSAR_ANOTACIONS_PETICIONS_PENDENTS_RATE = "es.caib.ripea.tasca.consulta.anotacio.temps.espera.execucio";
	public static final String EXECUTAR_EXECUCIONS_MASSIVES_RATE = "es.caib.ripea.segonpla.massives.periode.comprovacio";
	public static final String ENVIAR_EMAILS_PENDENTS_AGRUPATS_CRON = "es.caib.ripea.segonpla.email.enviament.agrupat.cron";
	public static final String TASCA_DURACIO_DEFAULT = "es.caib.ripea.duracio.tasca";
	public static final String TASCA_PREAVIS_DATA_LIMIT = "es.caib.ripea.tasca.preavisDataLimitEnDies";
	public static final String BUIDAR_CACHES_DOMINIS_RATE = "es.caib.ripea.dominis.cache.execucio";
	
	public static final String USUARIS_PLUGIN_CLASS 			= "es.caib.ripea.plugin.dades.usuari.class";
	public static final String USUARIS_KEYCLOAK_SERVER 			= "es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl";
	public static final String USUARIS_KEYCLOAK_REALM 			= "es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm";
	public static final String USUARIS_KEYCLOAK_CLIENT_ID 		= "es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id";
	public static final String USUARIS_KEYCLOAK_CLIENT_ID_AUTH	= "es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication";
	public static final String USUARIS_KEYCLOAK_SECRET 			= "es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret";
	public static final String USUARIS_KEYCLOAK_ADMINID 		= "es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID";
	public static final String USUARIS_KEYCLOAK_DEBUG 			= "es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug";
	
	public static final String DIR3_PLUGIN_CLASS 		= "es.caib.ripea.plugin.unitats.organitzatives.class";
	public static final String DIR3_PLUGIN_URL 			= "es.caib.ripea.plugin.unitats.organitzatives.dir3.service.url";
	public static final String DIR3_PLUGIN_ENDPOINT		= "es.caib.ripea.plugin.unitats.organitzatives.endpointName";
	public static final String DIR3_PLUGIN_USER 		= "es.caib.ripea.plugin.unitats.organitzatives.dir3.service.username";
	public static final String DIR3_PLUGIN_PASS 		= "es.caib.ripea.plugin.unitats.organitzatives.dir3.service.password";
	public static final String DIR3_PLUGIN_DEBUG 		= "es.caib.ripea.plugin.unitats.organitzatives.dir3.service.log.actiu";
	public static final String DIR3_PLUGIN_CERCA_URL	= "es.caib.ripea.plugin.unitats.cerca.dir3.service.url";
	public static final String DIR3_PLUGIN_TIMEOUT 		= "es.caib.ripea.plugin.unitats.organitzatives.dir3.service.timeout";
	
	public static final String NOTIB_PLUGIN_CLASS 		= "es.caib.ripea.plugin.notificacio.class";
	public static final String NOTIB_PLUGIN_URL 		= "es.caib.ripea.plugin.notificacio.url";
	public static final String NOTIB_PLUGIN_USER 		= "es.caib.ripea.plugin.notificacio.username";
	public static final String NOTIB_PLUGIN_PASS 		= "es.caib.ripea.plugin.notificacio.password";
	public static final String NOTIB_PLUGIN_ENDPOINT 	= "es.caib.ripea.plugin.notificacio.endpointName";
	public static final String NOTIB_PLUGIN_DEH_ACTIVA	= "es.caib.ripea.notificacio.enviament.deh.activa";
	public static final String NOTIB_PLUGIN_RETARD 		= "es.caib.ripea.notificacio.retard.num.dies";
	public static final String NOTIB_PLUGIN_CADUCA 		= "es.caib.ripea.notificacio.caducitat.num.dies";
	public static final String NOTIB_PLUGIN_ENTITAT 	= "es.caib.ripea.notificacio.forsar.entitat";
	public static final String NOTIB_PLUGIN_DEBUG 		= "es.caib.ripea.plugin.notificacio.debug";
	
	public static final String FIRMA_SERV_PLUGIN_CLASS			= "es.caib.ripea.plugin.firmaservidor.class";
	public static final String FIRMA_SERV_PLUGIN_URL			= "es.caib.ripea.plugin.firmaservidor.portafib.endpoint";
	public static final String FIRMA_SERV_PLUGIN_USER			= "es.caib.ripea.plugin.firmaservidor.portafib.auth.username";
	public static final String FIRMA_SERV_PLUGIN_PASS			= "es.caib.ripea.plugin.firmaservidor.portafib.auth.password";
	public static final String FIRMA_SERV_PLUGIN_SIGNER_EMAIL	= "es.caib.ripea.plugin.firmaservidor.portafib.signerEmail";
	public static final String FIRMA_SERV_PLUGIN_PERFIL			= "es.caib.ripea.plugin.firmaservidor.portafib.perfil";
	public static final String FIRMA_SERV_PLUGIN_LOCATION		= "es.caib.ripea.plugin.firmaservidor.portafib.location";
	public static final String FIRMA_SERV_PLUGIN_FIRMA_USERNAME	= "es.caib.ripea.plugin.firmaservidor.portafib.username";
	
	public static final String PORTAFIB_PLUGIN_CLASS			= "es.caib.ripea.plugin.portafirmes.class";
	public static final String PORTAFIB_PLUGIN_ENDPOINTNAME		= "es.caib.ripea.plugin.portafirmes.endpointName";
	public static final String PORTAFIB_PLUGIN_URL				= "es.caib.ripea.plugin.portafirmes.firmasimpleasync.url";
	public static final String PORTAFIB_PLUGIN_USER				= "es.caib.ripea.plugin.portafirmes.firmasimpleasync.username";
	public static final String PORTAFIB_PLUGIN_PASS				= "es.caib.ripea.plugin.portafirmes.firmasimpleasync.password";
	public static final String PORTAFIB_PLUGIN_DEBUG			= "es.caib.ripea.plugin.portafirmes.portafib.log.actiu";
	public static final String PORTAFIB_PLUGIN_PERFIL			= "es.caib.ripea.plugin.portafirmes.portafib.perfil";
	public static final String PORTAFIB_PLUGIN_PERSONA_CARREC	= "es.caib.ripea.plugin.portafirmes.carrer.mostrar.persona";
	public static final String PORTAFIB_PLUGIN_ENVIAR_URL_EXP	= "es.caib.ripea.plugin.portafirmes.portafib.enviar.url.expedient";
	
	public static final String PORTAFIB_PLUGIN_FLUX_URL			= "es.caib.ripea.plugin.portafirmes.firmasimpleflux.url";
	public static final String PORTAFIB_PLUGIN_FLUX_USR			= "es.caib.ripea.plugin.portafirmes.firmasimpleflux.username";
	public static final String PORTAFIB_PLUGIN_FLUX_PAS			= "es.caib.ripea.plugin.portafirmes.firmasimpleflux.password";
	
	public static final String PORTAFIB_PLUGIN_USUARISPF_URL	= "es.caib.ripea.plugin.portafirmes.usuarientitatws.url";
	public static final String PORTAFIB_PLUGIN_USUARISPF_USR	= "es.caib.ripea.plugin.portafirmes.usuarientitatws.username";
	public static final String PORTAFIB_PLUGIN_USUARISPF_PAS	= "es.caib.ripea.plugin.portafirmes.usuarientitatws.password";
	
	public static final String PORTAFIB_PLUGIN_FIRMAWEB_CLASS		= "es.caib.ripea.plugin.firmasimpleweb.class";
	public static final String PORTAFIB_PLUGIN_FIRMAWEB_URL			= "es.caib.ripea.plugin.firmasimpleweb.endpoint";
	public static final String PORTAFIB_PLUGIN_FIRMAWEB_USER		= "es.caib.ripea.plugin.firmasimpleweb.username";
	public static final String PORTAFIB_PLUGIN_FIRMAWEB_PASS		= "es.caib.ripea.plugin.firmasimpleweb.password";
	public static final String PORTAFIB_PLUGIN_FIRMAWEB_ENDPOINT	= "es.caib.ripea.plugin.firmasimpleweb.endpointName";
	public static final String PORTAFIB_PLUGIN_FIRMAWEB_LOCATION	= "es.caib.ripea.plugin.firmasimpleweb.location";
	public static final String PORTAFIB_PLUGIN_FIRMAWEB_DEBUG		= "es.caib.ripea.plugin.firmasimpleweb.debug";
	
	public static final String GESDOC_PLUGIN_FILESYSTEM_CLASS		= "es.caib.ripea.plugin.gesdoc.class";
	public static final String GESDOC_PLUGIN_FILESYSTEM_PATH		= "es.caib.ripea.plugin.gesdoc.filesystem.base.dir";
	
	
	public static final String REINTENTAR_CANVI_ESTAT_DISTRIBUCIO = "es.caib.ripea.segonpla.reintentar.anotacions.pendents.enviar.distribucio";	
	public static final String ENVIAR_EMAILS_PENDENTS_PROCEDIMENT_COMENTARI_CRON = "es.caib.ripea.segonpla.email.enviament.procediment.comentari.cron";	
	public static final String GUARDAR_ARXIU_CONTINGUTS_PENDENTS = "es.caib.ripea.segonpla.guardar.arxiu.continguts.pendents";
	public static final String GUARDAR_ARXIU_INTERESSATS = "es.caib.ripea.segonpla.guardar.arxiu.interessats";
	public static final String ACTUALITZAR_PROCEDIMENTS = "es.caib.ripea.procediment.actualitzar.cron";
	public static final String CONSULTA_CANVIS_ORGANIGRAMA = "es.caib.ripea.organs.consulta.canvis";
	public static final String TANCAMENT_LOGIC_CRON = "es.caib.ripea.expedient.tancament.logic.cron";
	
	public static String getPropertySuffix(String propertyValue) {
		if (propertyValue.startsWith(PROPERTY_PREFIX)) {
            return propertyValue.substring(PROPERTY_PREFIX.length());
        }
		return propertyValue;
	}
}