package es.caib.ripea.service.intf.config;

/**
 * Propietats de configuració.
 *
 * @author Límit Tecnologies
 */
public class PropertyConfig {

	private static final String PROPERTY_PREFIX = "es.caib.ripea.";

	public static final String APP_NAME = PROPERTY_PREFIX + "app.name";
	public static final String APP_URL = PROPERTY_PREFIX + "app.url";

	public static final String MAIL_FROM = PROPERTY_PREFIX + "mail.from";
	public static final String FILES_PATH = PROPERTY_PREFIX + "files.path";
	public static final String DEFAULT_AUDITOR = PROPERTY_PREFIX + "default.auditor";
	public static final String HTTP_HEADER_ANSWERS = PROPERTY_PREFIX + "http.header.answers";

	public static final String PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";
	public static final String PERSISTENCE_TRANSACTION_MANAGER_ENABLED = PROPERTY_PREFIX + "persist.transaction-manager.enabled";

	public static final String IDIOMA_DEFECTE 	= "es.caib.ripea.usuari.idioma.defecte";
	public static final String APP_DATA_DIR 	= "es.caib.ripea.app.data.dir";
	public static final String EMAIL_REMITENT 	= "es.caib.ripea.email.remitent";
	public static final String BASE_URL 		= "es.caib.ripea.base.url";
	public static final String ENTITAT_LOGO		= "es.caib.ripea.capsalera.logo";
	public static final String INDEX_LOGO		= "es.caib.ripea.index.logo";
	public static final String CAPSALERA_FONS	= "es.caib.ripea.capsalera.color.fons";
	public static final String CAPSALERA_LLETRA	= "es.caib.ripea.capsalera.color.lletra";
	
	public static final String PINBAL_BASE_URL 		= "es.caib.ripea.pinbal.base.url";
	public static final String PINBAL_USER 			= "es.caib.ripea.pinbal.user";
	public static final String PINBAL_PASS 			= "es.caib.ripea.pinbal.password";
	public static final String PINBAL_BASIC_AUTH	= "es.caib.ripea.pinbal.basic.auth";
	public static final String PINBAL_ENDPOINT_DESC = "es.caib.ripea.pinbal.endpointName";
	public static final String PINBAL_DEFAULT_SIA	= "es.caib.ripea.pinbal.codi.sia.peticions";
	
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
	
	public static final String ARXIU_PLUGIN_CLASS					= "es.caib.ripea.plugin.arxiu.class";
	public static final String ARXIU_PLUGIN_URL						= "es.caib.ripea.plugin.arxiu.caib.base.url";
	public static final String ARXIU_PLUGIN_USUARI					= "es.caib.ripea.plugin.arxiu.caib.usuari";
	public static final String ARXIU_PLUGIN_PASS					= "es.caib.ripea.plugin.arxiu.caib.contrasenya";
	public static final String ARXIU_PLUGIN_APLICACIO_CODI			= "es.caib.ripea.plugin.arxiu.caib.aplicacio.codi";
	public static final String ARXIU_PLUGIN_ENDPOINT_NAME			= "es.caib.ripea.arxiu.endpointName";
	public static final String ARXIU_PLUGIN_VERSIO_IMPR_URL			= "es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.url";
	public static final String ARXIU_PLUGIN_VERSIO_IMPR_USR			= "es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.usuari";
	public static final String ARXIU_PLUGIN_VERSIO_IMPR_PAS			= "es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.contrasenya";
	public static final String ARXIU_PLUGIN_TIMEOUT_CONNECT			= "es.caib.ripea.plugin.arxiu.caib.timeout.connect";
	public static final String ARXIU_PLUGIN_TIMEOUT_READ			= "es.caib.ripea.plugin.arxiu.caib.timeout.read";
	public static final String ARXIU_PLUGIN_METADADES_ADICIONALS	= "es.caib.ripea.arxiu.metadades.addicionals.actiu";
	public static final String ARXIU_PLUGIN_FIRMA_DETALLS			= "es.caib.ripea.arxiu.firma.detalls.actiu";
	
	public static final String CONVERSIO_PLUGIN_CLASS			= "es.caib.ripea.plugin.conversio.class";
	public static final String CONVERSIO_PLUGIN_OOFICE_HOST		= "es.caib.ripea.plugin.conversio.ooffice.host";
	public static final String CONVERSIO_PLUGIN_OOFICE_PORT		= "es.caib.ripea.plugin.conversio.ooffice.port";
	public static final String CONVERSIO_PLUGIN_ENDPOINT_NAME	= "es.caib.ripea.plugin.conversio.endpointName";
	
	public static final String DADESEXT_PLUGIN_DIR3_CLASS		= "es.caib.ripea.plugin.dadesext.class";
	public static final String DADESEXT_PLUGIN_DIR3_WS_URL		= "es.caib.ripea.plugin.dadesext.service.url";
	public static final String DADESEXT_PLUGIN_DIR3_REST_URL	= "es.caib.ripea.plugin.dadesext.dir3.rest.url";
	public static final String DADESEXT_PLUGIN_DIR3_ENDPOINT	= "es.caib.ripea.plugin.dadesext.endpointName";
	
	public static final String DADESEXT_PLUGIN_PINBAL_CLASS		= "es.caib.ripea.plugin.dadesextpinbal.class";
	public static final String DADESEXT_PLUGIN_PINBAL_URL		= "es.caib.ripea.plugin.dadesextcaib.service.url";
	public static final String DADESEXT_PLUGIN_PINBAL_ENDPOINT	= "es.caib.ripea.plugin.dadesextpinbal.endpointName";
	
	public static final String VALIDA_FIRMA_PLUGIN_CLASS		= "es.caib.ripea.plugin.validatesignature.class";
	public static final String VALIDA_FIRMA_PLUGIN_URL			= "es.caib.ripea.plugins.validatesignature.afirmacxf.endpoint";
	public static final String VALIDA_FIRMA_PLUGIN_USR			= "es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.username";
	public static final String VALIDA_FIRMA_PLUGIN_PAS			= "es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.password";
	public static final String VALIDA_FIRMA_PLUGIN_APP_ID		= "es.caib.ripea.plugins.validatesignature.afirmacxf.applicationID";
	public static final String VALIDA_FIRMA_PLUGIN_IGNORE_CERTS	= "es.caib.ripea.plugins.validatesignature.afirmacxf.ignoreservercertificates";
	public static final String VALIDA_FIRMA_PLUGIN_TRANSFORMERS	= "es.caib.ripea.plugins.validatesignature.afirmacxf.TransformersTemplatesPath";
	public static final String VALIDA_FIRMA_PLUGIN_ENDPOINT		= "es.caib.ripea.plugins.validatesignature.endpointName";
	
	public static final String ROLSAC_PLUGIN_CLASS		= "es.caib.ripea.plugin.procediment.class";
	public static final String ROLSAC_PLUGIN_URL		= "es.caib.ripea.plugin.procediment.rolsac.service.url";
	public static final String ROLSAC_PLUGIN_USR		= "es.caib.ripea.plugin.procediment.rolsac.service.username";
	public static final String ROLSAC_PLUGIN_PAS		= "es.caib.ripea.plugin.procediment.rolsac.service.password";
	public static final String ROLSAC_PLUGIN_ENDPOINT	= "es.caib.ripea.plugin.procediment.endpointName";
	public static final String ROLSAC_PLUGIN_TIMEOUT	= "es.caib.ripea.plugin.procediment.rolsac.service.timeout";
	
	public static final String SUMMARIZE_PLUGIN_CLASS		= "es.caib.ripea.plugin.summarize.class";
	public static final String SUMMARIZE_PLUGIN_URL			= "es.caib.ripea.plugin.summarize.url";
	public static final String SUMMARIZE_PLUGIN_MODEL		= "es.caib.ripea.plugin.summarize.model";
	public static final String SUMMARIZE_PLUGIN_USR			= "es.caib.ripea.plugin.summarize.usuari";
	public static final String SUMMARIZE_PLUGIN_PAS			= "es.caib.ripea.plugin.summarize.password";
	public static final String SUMMARIZE_PLUGIN_ENDPOINT	= "es.caib.ripea.plugin.summarize.endpointName";
	public static final String SUMMARIZE_PLUGIN_APIKEY		= "es.caib.ripea.plugin.summarize.gpt.apiKey";
	public static final String SUMMARIZE_PLUGIN_TIMEOUT		= "es.caib.ripea.plugin.summarize.service.timeout";
	public static final String SUMMARIZE_PLUGIN_DEBUG		= "es.caib.ripea.plugin.summarize.debug";
	public static final String SUMMARIZE_PLUGIN_MAX_TOKENS	= "es.caib.ripea.plugin.summarize.model.maxTokens";
	
	public static final String CONCSV_BASE_URL				= "es.caib.ripea.concsv.base.url";	
	
	public static final String VIAFIRMA_PLUGIN_CLASS				= "es.caib.ripea.plugin.viafirma.class";
	public static final String VIAFIRMA_PLUGIN_URL					= "es.caib.ripea.plugin.viafirma.caib.apiurl";
	public static final String VIAFIRMA_PLUGIN_CONSUMER_KEY			= "es.caib.ripea.plugin.viafirma.caib.consumerkey";
	public static final String VIAFIRMA_PLUGIN_CONSUMER_SECRET		= "es.caib.ripea.plugin.viafirma.caib.consumersecret";
	public static final String VIAFIRMA_PLUGIN_AUTH_MODE			= "es.caib.ripea.plugin.viafirma.caib.authmode";
	public static final String VIAFIRMA_PLUGIN_GROUP				= "es.caib.ripea.plugin.viafirma.caib.group.codi";
	public static final String VIAFIRMA_PLUGIN_AUTH_TYPE			= "es.caib.ripea.plugin.viafirma.caib.authtype";
	public static final String VIAFIRMA_PLUGIN_CALLBACK_URL			= "es.caib.ripea.plugin.viafirma.caib.callback.url";
	public static final String VIAFIRMA_PLUGIN_CALLBACK_USR			= "es.caib.ripea.plugin.viafirma.caib.callback.username";
	public static final String VIAFIRMA_PLUGIN_CALLBACK_PAS			= "es.caib.ripea.plugin.viafirma.caib.callback.password";
	public static final String VIAFIRMA_PLUGIN_PROXY_HOST			= "es.caib.ripea.plugin.viafirma.caib.proxy.host";
	public static final String VIAFIRMA_PLUGIN_PROXY_PORT			= "es.caib.ripea.plugin.viafirma.caib.proxy.port";
	public static final String VIAFIRMA_PLUGIN_CAIB_APP_CODE		= "es.caib.ripea.plugin.viafirma.caib.app.codi";
	public static final String VIAFIRMA_PLUGIN_ENDPOINT_NAME		= "es.caib.ripea.plugin.viafirma.endpointName";
	public static final String VIAFIRMA_PLUGIN_DISPOSITIUS_ENABLED	= "es.caib.ripea.plugin.viafirma.caib.dispositius.enabled";
	
	public static final String DISTRIBUCIO_PLUGIN_CLASS				= "es.caib.ripea.distribucio.backofficeIntegracio.class";
	public static final String DISTRIBUCIO_PLUGIN_URL				= "es.caib.ripea.distribucio.backofficeIntegracio.ws.url";
	public static final String DISTRIBUCIO_PLUGIN_ENDPOINT			= "es.caib.ripea.distribucio.backofficeIntegracio.endpointName";
	public static final String DISTRIBUCIO_PLUGIN_USR				= "es.caib.ripea.distribucio.backofficeIntegracio.ws.username";
	public static final String DISTRIBUCIO_PLUGIN_PAS				= "es.caib.ripea.distribucio.backofficeIntegracio.ws.password";
	
	public static final String DISTRIBUCIO_REGLA_PLUGIN_URL			= "es.caib.ripea.distribucio.regla.ws.url";
	public static final String DISTRIBUCIO_REGLA_PLUGIN_USR			= "es.caib.ripea.distribucio.regla.ws.username";
	public static final String DISTRIBUCIO_REGLA_PLUGIN_PAS			= "es.caib.ripea.distribucio.regla.ws.password";
	public static final String DISTRIBUCIO_REGLA_PLUGIN_CODI_BACK	= "es.caib.ripea.distribucio.regla.ws.codi.backoffice";
	public static final String DISTRIBUCIO_REGLA_PLUGIN_AUTH_BASIC	= "es.caib.ripea.distribucio.regla.autenticacio.basic";
	
	public static final String DIGITALITZACIO_PLUGIN_CLASS			= "es.caib.ripea.plugin.digitalitzacio.class";
	public static final String DIGITALITZACIO_PLUGIN_URL			= "es.caib.ripea.plugin.digitalitzacio.digitalib.base.url";
	public static final String DIGITALITZACIO_PLUGIN_USR			= "es.caib.ripea.plugin.digitalitzacio.digitalib.username";
	public static final String DIGITALITZACIO_PLUGIN_PAS			= "es.caib.ripea.plugin.digitalitzacio.digitalib.password";
	public static final String DIGITALITZACIO_PLUGIN_PERFIL			= "es.caib.ripea.plugin.digitalitzacio.digitalib.perfil";
	public static final String DIGITALITZACIO_PLUGIN_DEBUG			= "es.caib.ripea.plugin.digitalitzacio.log";
	public static final String DIGITALITZACIO_PLUGIN_ENDPOINT		= "es.caib.ripea.plugin.digitalitzacio.endpointName";
	public static final String DIGITALITZACIO_PLUGIN_SCANNER_MOCK	= "es.caib.ripea.plugin.digitalitzacio.scanner.mock";
	
	public static final String REINTENTAR_CANVI_ESTAT_DISTRIBUCIO 					= "es.caib.ripea.segonpla.reintentar.anotacions.pendents.enviar.distribucio";	
	public static final String ENVIAR_EMAILS_PENDENTS_PROCEDIMENT_COMENTARI_CRON 	= "es.caib.ripea.segonpla.email.enviament.procediment.comentari.cron";	
	public static final String GUARDAR_ARXIU_CONTINGUTS_PENDENTS 					= "es.caib.ripea.segonpla.guardar.arxiu.continguts.pendents";
	public static final String GUARDAR_ARXIU_INTERESSATS 							= "es.caib.ripea.segonpla.guardar.arxiu.interessats";
	public static final String ACTUALITZAR_PROCEDIMENTS 							= "es.caib.ripea.procediment.actualitzar.cron";
	public static final String CONSULTA_CANVIS_ORGANIGRAMA 							= "es.caib.ripea.organs.consulta.canvis";
	public static final String TANCAMENT_LOGIC_CRON 								= "es.caib.ripea.expedient.tancament.logic.cron";
	public static final String CONVERSIO_DEFINITIU	 								= "es.caib.ripea.conversio.definitiu";
	public static final String CONVERSIO_DEFINITIU_PROPAGAR_ARXIU					= "es.caib.ripea.conversio.definitiu.propagar.arxiu";
	public static final String PROPAGAR_RELACIO_EXPEDIENTS							= "es.caib.ripea.propagar.relacio.expedients";
	public static final String GUARDAR_CERTIFICACIO_EXPEDIENT						= "es.caib.ripea.notificacio.guardar.certificacio.expedient";
	public static final String CARPETES_PER_DEFECTE									= "es.caib.ripea.carpetes.defecte";
	public static final String INCORPORAR_JUSTIFICANT								= "es.caib.ripea.incorporar.justificant";
	public static final String METAEXPEDIENT_REVISIO_ACTIVA							= "es.caib.ripea.metaexpedients.revisio.activa";
	public static final String INCORPORACIO_ANOTACIO_DUPLICADA						= "es.caib.ripea.incorporacio.anotacions.duplicada";
	public static final String DOCUMENTS_GENERALS_ACTIUS							= "es.caib.ripea.habilitar.documentsgenerals";
	public static final String TIPUS_DOCUMENT_ACTIUS								= "es.caib.ripea.habilitar.tipusdocument";
	public static final String ADMIN_ORGAN_GESTIO_PERMISOS							= "es.caib.ripea.procediment.gestio.permis.administrador.organ";
	public static final String DOMINIS_HABILITATS									= "es.caib.ripea.habilitar.dominis";
	public static final String PERMETRE_USUARIS_CREAR_FLUX_PORTAFIB					= "es.caib.ripea.plugin.portafirmes.fluxos.usuaris";
	public static final String PERIODE_ACTUALITZACIO_ANOTACIO_PENDENT				= "es.caib.ripea.periode.actualitzacio.contador.anotacions.pendents";
	public static final String SEGUIMENT_ENVIAMENTS_USUARI							= "es.caib.ripea.mostrar.seguiment.enviaments.usuari";
	public static final String CANVI_TIPUS_INTERESSAT_ANOTACIONS					= "es.caib.ripea.interessats.permet.canvi.tipus";
	public static final String CARPETES_CREACIO_ACTIVA								= "es.caib.ripea.creacio.carpetes.activa";
	public static final String CARPETES_ANOTACIONS_ACTIVES							= "es.caib.ripea.mostrar.carpetes.anotacions";
	public static final String CARPETES_LOGIQUES_ACTIVES							= "es.caib.ripea.carpetes.logiques";
	public static final String ORDENACIO_CONTINGUT_ACTIU							= "es.caib.ripea.ordenacio.contingut.habilitada";
	public static final String INDEX_EXPEDIENTS_RELACIONATS							= "es.caib.ripea.index.expedients.relacionats";
	public static final String INDEX_CAMPS_ADDICIONALS								= "es.caib.ripea.index.expedient.camps.addicionals";
	public static final String IMPORTACIO_ACTIVA									= "es.caib.ripea.creacio.importacio.activa";
	public static final String MOURER_DOCUMENTS_ACTIU								= "es.caib.ripea.creacio.documents.copiarMoure.activa";
	public static final String VINCULAR_DOCUMENTS_ACTIU								= "es.caib.ripea.creacio.documents.vincular.activa";
	public static final String IMPRIMIBLE_NO_FIRMAT_ACTIU							= "es.caib.ripea.descarregar.imprimible.nofirmats";
	public static final String REOBRIR_EXPEDIENT_TANCAT								= "es.caib.ripea.expedient.permetre.reobrir";
	public static final String MODIFICAR_DOCUMENTS_CUSTODIATS                       = "es.caib.ripea.document.modificar.custodiats";
	public static final String PUBLICAR_DOCUMENTS_ACTIVA                            = "es.caib.ripea.creacio.documents.publicar.activa";
	public static final String FIRMA_BIOMETRICA_ACTIVA                              = "es.caib.ripea.documents.firma.biometrica.activa";
	public static final String VALIDACIO_URL_IMPRIMIBLES                            = "es.caib.ripea.documents.validacio.url";
	public static final String MOURE_MATEIX_EXPEDIENTS                              = "es.caib.ripea.creacio.documents.moure.mateix.expedient";
	public static final String IMPORTACIO_RELACIONATS_ACTIVA                        = "es.caib.ripea.importacio.expedient.relacionat.activa";
	public static final String PERMATRE_ESBORRAR_FINAL                              = "es.caib.ripea.document.esborrar.finals";
	public static final String ESTAT_ELABORACIO_ENI_OBLIGATORI                      = "es.caib.ripea.estat.elaboracio.identificador.origen.obligat";
	public static final String IDENTIFICADOR_ORIGEN_MASCARA                         = "es.caib.ripea.identificador.origen.mascara";
	public static final String PROPAGAR_MODIFICACIO_ARXIU                           = "es.caib.ripea.document.propagar.modificacio.arxiu";
	public static final String FILTRE_DATA_CREACIO_ACTIU                            = "es.caib.ripea.filtre.data.creacio.actiu";
	public static final String DETECCIO_FIRMA_AUTOMATICA                            = "es.caib.ripea.document.deteccio.firma.automatica";
	public static final String DESACTIVAR_COMPROVACIO_NOMS_DUPLICATS                = "es.caib.ripea.desactivar.comprovacio.duplicat.nom.arxiu";
	public static final String CREAR_FIRMAT_DEFINITIU                               = "es.caib.ripea.document.guardar.definitiu.arxiu";
	public static final String NUMERO_EXPEDIENT_SEPARADOR                           = "es.caib.ripea.numero.expedient.separador";
	public static final String PROPAGAR_NUMERO_EXPEDIENT                            = "es.caib.ripea.numero.expedient.propagar.arxiu";
	public static final String ENVIAR_CONTINGUT_EXISTENT                            = "es.caib.ripea.document.enviar.contingut.existent";
	public static final String PROPAGAR_METADADES                                   = "es.caib.ripea.expedient.propagar.metadades";
	public static final String GENERAR_URL_INSTRUCCIO                               = "es.caib.ripea.expedient.generar.urls.instruccio";
	public static final String CONCATENAR_MULTIPLES_PDFS                            = "es.caib.ripea.notificacio.multiple.pdf.concatenar";
	public static final String NOTIFICAR_MULTIPLE_GENERAR_DOC_VISIBLE               = "es.caib.ripea.notificacio.multiple.document.generat.visible";
	public static final String PERMETRE_PUNTS_NOM_EXPEDIENT                         = "es.caib.ripea.expedient.permetre.punts";
	public static final String EXPORTACIO_EXCEL                                     = "es.caib.ripea.expedient.exportacio.excel";
	public static final String EXPORTACIO_INSIDE                                    = "es.caib.ripea.expedient.exportar.inside";
    public static final String TANCAMENT_LOGIC								        = "es.caib.ripea.expedient.tancament.logic";
    public static final String OBTENIR_DATA_FIRMA_FROM_ATRIBUT_DOC 					= "es.caib.ripea.obtenir.data.firma.atributs.document";
    public static final String TANCAMENT_LOGIC_DIES								    = "es.caib.ripea.expedient.tancament.logic.dies";
    public static final String MANTENIR_ESTAT_CARPETA								= "es.caib.ripea.carpetes.mantenir.estat";
    public static final String ENTORN								                = "es.caib.ripea.entorn";
    public static final String GUARDAR_CONTINGUT_ANNEXOS_DISTRIBUCIO				= "es.caib.ripea.anotacions.annexos.save";
    public static final String REGISTRE_EXPEDIENT_SERIE_DOCUMENTAL 				    = "es.caib.ripea.anotacions.registre.expedient.serie.documental";
    public static final String PERMETRE_OBLIGAR_INTERESSAT 				            = "es.caib.ripea.permetre.obligar.interessat";

    public static final String MOSTRAR_LOGS_EMAIL 				                    = "es.caib.ripea.mostrar.logs.email";
    public static final String MOSTRAR_LOGS_CREACIO_CONTINGUT 				        = "es.caib.ripea.mostrar.logs.creacio.contingut";
    public static final String MOSTRAR_LOGS_PERMISOS 				                = "es.caib.ripea.activar.logs.permisos";
    public static final String MOSTRAR_LOGS_RENDIMENT 				                = "es.caib.ripea.mostrar.logs.rendiment";
    public static final String MOSTRAR_LOGS_RENDIMENT_ANOTACIONS 		            = "es.caib.ripea.mostrar.logs.rendiment.descarregar.anotacio";
    public static final String MOSTRAR_LOGS_GRUPS 				                    = "es.caib.ripea.activar.logs.grups";
    public static final String MOSTRAR_LOGS_CERCADOR_ANOTACIO 				        = "es.caib.ripea.mostrar.logs.cercador.anotacions";
    public static final String MOSTRAR_LOGS_SEGONPLA 				                = "es.caib.ripea.mostrar.logs.segonpla";
    public static final String MOSTRAR_LOGS_INTEGRACIO 				                = "es.caib.ripea.mostrar.logs.integracio";

    public static final String AVIS_FIRMA_PARCIAL 				                    = "es.caib.ripea.portafirmes.avis.firma.parcial";
    public static final String FIRMA_PARCIAL 				                        = "es.caib.ripea.portafirmes.firma.parcial";
    public static final String FILTRAR_USUARI_DESCRIPCIO 				            = "es.caib.ripea.plugin.portafirmes.flux.filtrar.usuari.descripcio";
    public static final String TIPUS_DOC_PORTAFIRMES_ACTIU 				            = "es.caib.ripea.activar.tipus.document.portafirmes";
    public static final String MAX_REINTENTS_CANVI_ESTST_DISTRIBUCIO 		        = "es.caib.ripea.segonpla.max.reintents.anotacions.pendents.enviar.distribucio";
    public static final String MAX_REINTENTS_EXPEDIENT 		                        = "es.caib.ripea.segonpla.guardar.arxiu.max.reintents.expedients";
    public static final String MAX_REINTENTS_DOCUMENTS 		                        = "es.caib.ripea.segonpla.guardar.arxiu.max.reintents.documents";
    public static final String MAX_REINTENTS_INTERESSATS 		                    = "es.caib.ripea.segonpla.guardar.arxiu.max.reintents.interessats";
    public static final String VALIDATE_SIGNATURE_ATTACHED   		                = "es.caib.ripea.firma.detectar.attached.validate.signature";

	public static String getPropertySuffix(String propertyValue) {
		if (propertyValue.startsWith(PROPERTY_PREFIX)) {
			return propertyValue.substring(PROPERTY_PREFIX.length());
		}
		return propertyValue;
	}

}