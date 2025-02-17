Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('BOOL',null);
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('CONVERSIO_CLASS','es.caib.ripea.plugin.caib.conversio.ConversioPluginXdocreport,es.caib.ripea.plugin.caib.conversio.ConversioPluginOpenOffice');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('CRON',null);
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('DADESEXT_CLASS','es.caib.ripea.plugin.caib.dadesext.DadesExternesPluginCaib,es.caib.ripea.plugin.caib.dadesext.DadesExternesPluginDir3,es.caib.ripea.plugin.caib.dadesext.DadesExternesPluginDir3Rest');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('DIGITALITZACIO_CLASS','es.caib.ripea.plugin.caib.digitalitzacio.DigitalitzacioPluginDigitalIB');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('FIRMASERVIDOR_CLASS','es.caib.ripea.plugin.caib.firmaservidor.FirmaSimpleServidorPluginPortafib,es.caib.ripea.plugin.caib.firmaservidor.FirmaServidorPluginMock');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('FLOAT',null);
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('GESCONADM_CLASS','es.caib.ripea.plugin.caib.procediment.ProcedimentPluginRolsac');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('INT',null);
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('NOTIFICACIO_CLASS','es.caib.ripea.plugin.caib.notificacio.NotificacioPluginNotib');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('ORGANISMES_CLASS','es.caib.ripea.plugin.caib.unitat.UnitatsOrganitzativesPluginDir3,es.caib.ripea.plugin.caib.unitat.UnitatsOrganitzativesPluginCaibMock,es.caib.ripea.plugin.caib.unitat.UnitatsOrganitzativesPluginMock');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('PASSWORD',null);
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('PORTAFIRMES_CLASS','es.caib.ripea.plugin.caib.portafirmes.PortafirmesPluginPortafib,es.caib.ripea.plugin.caib.portafirmes.PortafirmesPluginMock,es.caib.ripea.plugin.caib.portafirmes.PortafirmesPluginCwsJaxws');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('SUMMARIZE_CLASS','es.caib.ripea.plugin.caib.summarize.SummarizePluginBert,es.caib.ripea.plugin.caib.summarize.SummarizePluginChatGPT');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('TEXT',null);
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('USUARIS_CLASS','es.caib.ripea.plugin.caib.usuari.DadesUsuariPluginJdbc,es.caib.ripea.plugin.caib.usuari.DadesUsuariPluginLdap,es.caib.ripea.plugin.caib.usuari.DadesUsuariPluginMock');
Insert into IPA_CONFIG_TYPE (CODE,VALUE) values ('VIAFIRMA_CLASS','es.caib.ripea.plugin.caib.viafirma.ViaFirmaPluginImpl,es.caib.ripea.plugin.caib.viafirma.ViaFirmaPluginMock,es.caib.ripea.plugin.caib.usuari.DadesUsuariPluginKeycloak');

Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('PLUGINS',null,'3','Plugins de l''aplicació');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('FIRMA',null,'5','Firma');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('ALTRES',null,'9','Altres configuracions');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('ASPECTE',null,'6','Aspecte per defecte de l''aplicació');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('CONTINGUT',null,'8','Configuració del contingut d''un expedient');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('EMAIL',null,'1','Enviament de correus');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('GENERAL',null,'0','Configuracions generals');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('GES_DOC',null,'4','Gestió documental (Sistema de fitxers)');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('LOGS',null,'5','Logs del servidor');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED',null,'2','Configuració de les tasques periòdiques');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('ARXIU','PLUGINS','1','Configuració de l''arxiu');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('CONVERSIO','PLUGINS','4','Configuració del plugin de conversió de documents');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('DADES_EXT','PLUGINS','6','Configuració del plugin de dades exterbes');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('DADES_EXT_PINBAL','PLUGINS','6','Configuració del plugin de dades externes per consultes a PINBAL');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('DIGITALITZACIO','PLUGINS','8','Plugin de digitalització');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('DISTRIBUCIO','PLUGINS','10','Plugin de DISTRIBUCIO');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('DISTRIBUCIO_REGLA','PLUGINS','11','Plugin de creació de regla en DISTRIBUCIO');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('GESCONADM','PLUGINS','7','Plugin de gestió documental administratiu (ROLSAC)');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('NOTIB','PLUGINS','3','Plugin de notificacions (NOTIB)');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('ORGANISMES','PLUGINS','2','Configuració del plugin de DIR3');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('PINBAL','PLUGINS','9','Plugin de PINBAL');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SUMMARIZE','PLUGINS','12','Configuració del plugin de Resums');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('USUARIS','PLUGINS','5','Configuració del plugin d''usuaris');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('FIRMA_SERVIDOR','FIRMA','4','Configuració del plugin de firma servidor');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('FIRMA_SIMPLE_WEB','FIRMA','3','Configuració del plugin de firma simple web');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('FIRMA_VIAFIRMA','FIRMA','2','Configuració del plugin de viafirma');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('PORTAFIRMES','FIRMA','1','Configuració del plugin de portafirmes');
Insert into IPA_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('VALIDATE_SIGNATURE','FIRMA','5','Configuració del plugin validació de firmes');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','GENERAL','es.caib.ripea.app.data.dir','C:/_LOT_/RIPEA/FILES','Path dels fitxers de l’aplicació','1','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','EMAIL','es.caib.ripea.email.remitent','josepg@limit.es','Remitent dels correus electrònics (correu electrònic)','2','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','GENERAL','es.caib.ripea.base.url','http://localhost:8080/ripea','Especificar la URL base de l''aplicació','2','TEXT','0','0','0','0','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.segonpla.arxiu.maxMb','100','Maxim tamany en Mb que pot ocupar l''arxiu zip que conté els documents dels expedients seleccionats.','3','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.segonpla.arxiu.maxTempsExec','10','Maxim temps en minut que pot tardar el procés de generar l''arxiu zip que conté els documents dels expedients seleccionats.','4','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.document.nou.escanejar.actiu','false','Activar l’escaneig en document nou','13','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.tasca.consulta.anotacio.temps.espera.execucio','30000','Interval de temps (ms) en que s’’executa la tasca periòdica de consultar i guardar anotacions per peticions pedents de creacio del expedients','0','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.segonpla.massives.periode.comprovacio','30000','Interval de temps (ms) en que s’’executa la tasca periòdica que comprova les execucions massives.','0','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.segonpla.email.enviament.agrupat.cron','0 * * * * *','Expressió cron per indicar quan executar la tasca periòdica per enviar els correus electrònics pendents agrupats.','0','CRON','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.duracio.tasca','10','Duració per defecte de una tasca, sempre especificat en dies.','0','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.dominis.cache.execucio','3600000','Interval de temps (ms) en que buidar la cache de dominis','0','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.tasca.preavisDataLimitEnDies','3','Nombre de dies limit en que s’ha d’enviar el preavís','0','INT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.segonpla.reintentar.anotacions.pendents.enviar.distribucio','600000','Interval de temps (ms) en que executa la tasca periodica de reintentar canviar estat de anotacions a Distribució','0','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.segonpla.email.enviament.procediment.comentari.cron','0 0 * * * *','Expressió cron per indicar quan executar la tasca periodica per enviar els correus electronics avisant que s''ha afegit un comentari a procediment','8','CRON','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.segonpla.guardar.arxiu.continguts.pendents','600000','Interval de temps (ms) en que s’’executa la tasca periodica de guardar documents i expedient pendents en arxiu','1','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.segonpla.guardar.arxiu.interessats','600000','Interval de temps (ms) en que s’’executa la tasca periodica de guardar interessats pendents en arxiu','2','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.procediment.actualitzar.cron','0 15 * * * *','Cron per a la actualització de procediments','6','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SCHEDULLED','es.caib.ripea.organs.consulta.canvis','0 45 2 * * *','Cron per a la consulta de canvis a l''organigrama','7','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.expedient.tancament.logic.cron','0 0 20 * * *','Consulta diaria dels expedients pendents de tancar i si ha arribat la data programada','41','TEXT','0','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PINBAL','es.caib.ripea.pinbal.base.url','https://proves.caib.es/pinbalapi','Url del plugin','1','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','PINBAL','es.caib.ripea.pinbal.basic.auth','true','Indica si l’autentificació és de tipus basic','0','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','PINBAL','es.caib.ripea.pinbal.endpointName',null,'Nom del endpoint del plugin de PINBAL','3','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PINBAL','es.caib.ripea.pinbal.user','$ripea_pinbal','Usuari del plugin','2','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PINBAL','es.caib.ripea.pinbal.password',null,'Constrasenya del plugin','3','PASSWORD','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','PINBAL','es.caib.ripea.pinbal.codi.sia.peticions','CODSVDR_GBA_20121107','Codi SIA o codi de procediment a utilitzar en totes les peticions a PINBAL. (En cas de no emplenar-se s''utilitzarà el codi SIA de cada procediment)','4','TEXT','1','1','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','USUARIS','es.caib.ripea.plugin.dades.usuari.class','es.caib.ripea.plugin.caib.usuari.DadesUsuariPluginKeycloak','Classe del plugin de usuaris','0','USUARIS_CLASS','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','USUARIS','es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl',null,'Url del plugin','0','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','USUARIS','es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm','GOIB','Realm keycloak','0','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','USUARIS','es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret',null,'Keycloak secret','0','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','USUARIS','es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id','goib-ws','Client id de keycloak','0','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','USUARIS','es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication',null,'Client id de keycloak','0','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','USUARIS','es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID','nif','Camp amb el NIF','0','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','USUARIS','es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug','false','Camp amb el NIF','0','BOOL','0','0','0','0','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ORGANISMES','es.caib.ripea.plugin.unitats.organitzatives.class','es.caib.ripea.plugin.caib.unitat.UnitatsOrganitzativesPluginDir3','Especificar la classe per a accedir a les unitats organitzatives','1','ORGANISMES_CLASS','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ORGANISMES','es.caib.ripea.plugin.unitats.organitzatives.dir3.service.url','http://dev.caib.es/dir3caib','Url per accedir als organismes','2','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ORGANISMES','es.caib.ripea.plugin.unitats.organitzatives.dir3.service.username','$ripea_dir3caib','Nom de l''usuari per a accedir a DIR3','3','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ORGANISMES','es.caib.ripea.plugin.unitats.organitzatives.dir3.service.password',null,'Password de l''usuari per a accedir a DIR3','3','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ORGANISMES','es.caib.ripea.plugin.unitats.organitzatives.dir3.service.log.actiu','true','Activar logs de les unitats organitzatives','6','BOOL','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ORGANISMES','es.caib.ripea.plugin.unitats.cerca.dir3.service.url','https://proves.caib.es/dir3caib/rest/busqueda/organismos','Url per a cercar organismes.','5','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ORGANISMES','es.caib.ripea.plugin.unitats.organitzatives.dir3.service.timeout','300000','Timeout de connexió del plugin d’unitats organitzatives','7','INT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ORGANISMES','es.caib.ripea.plugin.unitats.organitzatives.endpointName','dev','Nom del endpoint del plugin de Organismes (DIR3)','3','TEXT','0','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','NOTIB','es.caib.ripea.plugin.notificacio.class','es.caib.ripea.plugin.caib.notificacio.NotificacioPluginNotib','Classe del plugin de Notib','0','NOTIFICACIO_CLASS','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','NOTIB','es.caib.ripea.plugin.notificacio.url','https://se.caib.es/notibapi','Url del plugin','0','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','NOTIB','es.caib.ripea.plugin.notificacio.username','$ripea_notib','Usuari del plugin','0','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','NOTIB','es.caib.ripea.plugin.notificacio.password','ripea_notib','Constrasenya del plugin','0','PASSWORD','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','NOTIB','es.caib.ripea.notificacio.retard.num.dies','5','Nombre de dies de retard per defecte de les notificacions que no tenen aquest valor definit.','0','INT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','NOTIB','es.caib.ripea.notificacio.caducitat.num.dies','10','Nombre de dies a caducar de les notificacions','0','INT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','NOTIB','es.caib.ripea.notificacio.forsar.entitat','A04003003','Forçar l’enviament de les notificacions per a una determinada entitat (indicar codi DIR3)','0','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','NOTIB','es.caib.ripea.notificacio.enviament.deh.activa','true','Notificació amb direcció electrònica habilitada activa','7','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','NOTIB','es.caib.ripea.plugin.notificacio.endpointName','SE CAIB','Nom del endpoint del plugin de Notib','3','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','NOTIB','es.caib.ripea.plugin.notificacio.debug','true','Debug','13','BOOL','0','0','0','0','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_SERVIDOR','es.caib.ripea.plugin.firmaservidor.class','es.caib.ripea.plugin.caib.firmaservidor.FirmaSimpleServidorPluginPortafib','Classe per a gestionar la firma servidor','0','FIRMASERVIDOR_CLASS','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_SERVIDOR','es.caib.ripea.plugin.firmaservidor.portafib.endpoint','https://proves.caib.es/portafib/common/rest/apifirmaenservidorsimple/v1/','Url de l''API REST del portafirmes','1','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_SERVIDOR','es.caib.ripea.plugin.firmaservidor.portafib.auth.username','$ripea_portafib_pre','Usuari per accedir a portafirmes','2','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_SERVIDOR','es.caib.ripea.plugin.firmaservidor.portafib.auth.password','ripea_portafib_pre','Password per accedir al Portafirmes','3','PASSWORD','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_SERVIDOR','es.caib.ripea.plugin.firmaservidor.portafib.perfil',null,'Perfil de firma','4','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_SERVIDOR','es.caib.ripea.plugin.firmaservidor.portafib.signerEmail','suport@caib.es','Correu electrònic del firmant.','5','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_SERVIDOR','es.caib.ripea.plugin.firmaservidor.portafib.location','Palma','Ubicació del firmant','6','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_SERVIDOR','es.caib.ripea.plugin.firmaservidor.portafib.username','preprod-dgmad','Nom d’’usuari per a la firma de servidor emprant PortaFIB','7','TEXT','1','1','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.class','es.caib.ripea.plugin.caib.portafirmes.PortafirmesPluginPortafib','Classe del portafirmes','0','PORTAFIRMES_CLASS','1','1','1','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.firmasimpleasync.url','https://dev.caib.es/portafib/common/rest/apifirmaasyncsimple/v2','Url de l''api firma simple async','1','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.firmasimpleasync.username','$ripea_portafib_dev','Usuari de l''api firma simple async','2','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.firmasimpleasync.password','ripea_portafib_dev','Constrasenya de l''api firma simple async','3','PASSWORD','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.endpointName','dev.caib.es','Nom del endpoint del plugin','3','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.portafib.log.actiu','false','Debug','10','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.portafib.perfil',null,'Perfil de firma','11','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.carrer.mostrar.persona','false','Mostrar la persona relacionat amb càrrec','11','BOOL','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.portafib.enviar.url.expedient','false','Enviar URL del expedient al enviar a firmar','12','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.firmasimpleflux.url','https://dev.caib.es/portafib/common/rest/apiflowtemplatesimple/v1','Url de l''api firma simple flux','4','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.firmasimpleflux.username','$ripea_portafib_dev','Usuari de l''api firma simple flux','5','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.firmasimpleflux.password','ripea_portafib_dev','Constrasenya de l''api firma simple flux','6','PASSWORD','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.usuarientitatws.url','https://dev.caib.es/portafib/ws/v1/PortaFIBUsuariEntitat','Url de UsuariEntitat webservice','21','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.usuarientitatws.username','$ripea_portafib_dev','Usuari de UsuariEntitat webservice','22','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','PORTAFIRMES','es.caib.ripea.plugin.portafirmes.usuarientitatws.password','ripea_portafib_dev','Constrasenya de UsuariEntitat webservice','23','PASSWORD','1','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_SIMPLE_WEB','es.caib.ripea.plugin.firmasimpleweb.class','es.caib.ripea.plugin.caib.firmaweb.FirmaSimpleWebPluginPortafib','Classe del plugin','1','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_SIMPLE_WEB','es.caib.ripea.plugin.firmasimpleweb.endpoint','https://dev.caib.es/portafib/common/rest/apifirmawebsimple/v1/','Url del plugin','2','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_SIMPLE_WEB','es.caib.ripea.plugin.firmasimpleweb.username','$ripea_portafib_dev','Usuari del plugin','3','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_SIMPLE_WEB','es.caib.ripea.plugin.firmasimpleweb.password','ripea_portafib_dev','Constrasenya del plugin','4','PASSWORD','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_SIMPLE_WEB','es.caib.ripea.plugin.firmasimpleweb.endpointName',null,'Nom del endpoint del plugin de Firma Simple REST','5','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_SIMPLE_WEB','es.caib.ripea.plugin.firmasimpleweb.location',null,'Localitació a on es realitza la firma','6','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_SIMPLE_WEB','es.caib.ripea.plugin.firmasimpleweb.debug','false','Debug actiu','7','BOOL','1','1','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','GES_DOC','es.caib.ripea.plugin.gesdoc.class','es.caib.ripea.plugin.caib.gesdoc.GestioDocumentalPluginFilesystem','Nom de la classe per a gestionar l''emmagatzament de documents','1','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','GES_DOC','es.caib.ripea.plugin.gesdoc.filesystem.base.dir','d:/_LOT_RIPEAFILES/gesdoc','Directori del sistema de fitxers on emmagatzemar els documents','2','TEXT','0','1','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','GENERAL','es.caib.ripea.usuari.idioma.defecte','ca','Idioma per defecte de l’aplicació','3','TEXT','0','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ARXIU','es.caib.ripea.plugin.arxiu.class','es.caib.plugins.arxiu.caib.ArxiuPluginCaib','Classe del plugin','1','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ARXIU','es.caib.ripea.plugin.arxiu.caib.base.url','https://esbse.caib.es:4430/esb','Url de l’’arxiu','2','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ARXIU','es.caib.ripea.plugin.arxiu.caib.usuari','ripea','Usuari de l’’arxiu','3','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ARXIU','es.caib.ripea.plugin.arxiu.caib.contrasenya',null,'Contrasenya de l’’arxiu','4','PASSWORD','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ARXIU','es.caib.ripea.plugin.arxiu.caib.aplicacio.codi','RIPEA','Codi de l’’aplicació','8','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ARXIU','es.caib.ripea.arxiu.endpointName','esbse.caib.es:4430','Nom del endpoint del plugin','3','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ARXIU','es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.url','https://dev.caib.es/concsv/rest/printable/uuid','Url on generar imprimible per UUID','5','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ARXIU','es.caib.ripea.concsv.base.url','https://dev.caib.es/concsvfront/view.xhtml?hash=','Url base al servei de CONCSV','31','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ARXIU','es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.usuari','$concsv_ripea','Usuari per a generar imprimible','6','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ARXIU','es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.contrasenya','concsv_ripea','Password per a generar imprimible','7','PASSWORD','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ARXIU','es.caib.ripea.plugin.arxiu.caib.timeout.connect','20000','Timeout connect','10','INT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ARXIU','es.caib.ripea.plugin.arxiu.caib.timeout.read','20000','Timeout read','11','INT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ARXIU','es.caib.ripea.arxiu.metadades.addicionals.actiu','false','Metadades addicionals','12','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ARXIU','es.caib.ripea.arxiu.firma.detalls.actiu','true','Firma detalls','13','BOOL','1','1','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONVERSIO','es.caib.ripea.plugin.conversio.class','es.caib.ripea.plugin.caib.conversio.ConversioPluginXdocreport','Classe del plugin','0','CONVERSIO_CLASS','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','CONVERSIO','es.caib.ripea.plugin.conversio.ooffice.port',null,'Port del plugin','0','INT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','CONVERSIO','es.caib.ripea.plugin.conversio.ooffice.host',null,'Url del plugin','0','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONVERSIO','es.caib.ripea.plugin.conversio.endpointName',null,'Nom del endpoint del plugin','3','TEXT','0','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DISTRIBUCIO_REGLA','es.caib.ripea.distribucio.regla.ws.url',null,'Url del plugin','1','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DISTRIBUCIO_REGLA','es.caib.ripea.distribucio.regla.ws.username',null,'Usuari del plugin','2','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DISTRIBUCIO_REGLA','es.caib.ripea.distribucio.regla.ws.password',null,'Constrasenya del plugin','3','PASSWORD','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DISTRIBUCIO_REGLA','es.caib.ripea.distribucio.regla.ws.codi.backoffice',null,'Codi de backoffice','4','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DISTRIBUCIO_REGLA','es.caib.ripea.distribucio.regla.autenticacio.basic','false','Client REST creació regles amb tipus autenticació BASIC','5','BOOL','0','1','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DADES_EXT','es.caib.ripea.plugin.dadesext.class','es.caib.ripea.plugin.caib.dadesext.DadesExternesPluginDir3','Classe del plugin','1','DADESEXT_CLASS','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DADES_EXT','es.caib.ripea.plugin.dadesext.service.url','https://proves.caib.es/dir3caib','Url del plugin','2','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DADES_EXT','es.caib.ripea.plugin.dadesext.endpointName','proves.caib.es DIR3','Nom del endpoint del plugin de dades externes (DIR3)','3','TEXT','0','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','VALIDATE_SIGNATURE','es.caib.ripea.plugin.validatesignature.class','org.fundaciobit.plugins.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin','Classe del plugin','1','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','VALIDATE_SIGNATURE','es.caib.ripea.plugins.validatesignature.afirmacxf.endpoint','https://afirmades2.caib.es/afirmaws/services/DSSAfirmaVerify','Endpoint de AfirmaCxf','2','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','VALIDATE_SIGNATURE','es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.password','RIPEA','Password de AfirmaCxf','4','PASSWORD','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','VALIDATE_SIGNATURE','es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.username','RIPEA','Usuari de AfirmaCxf','3','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','VALIDATE_SIGNATURE','es.caib.ripea.plugins.validatesignature.afirmacxf.applicationID','CAIBDEV2.RIPEA','ID de l’aplicació','5','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','VALIDATE_SIGNATURE','es.caib.ripea.plugins.validatesignature.afirmacxf.ignoreservercertificates','true','Ignorar els certificats','6','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','VALIDATE_SIGNATURE','es.caib.ripea.plugins.validatesignature.afirmacxf.TransformersTemplatesPath','C:\temp\helium_conf\transformers','Path dels transformers','7','TEXT','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','VALIDATE_SIGNATURE','es.caib.ripea.plugins.validatesignature.endpointName',null,'Nom del endpoint del plugin de validació de firma','3','TEXT','0','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','GESCONADM','es.caib.ripea.plugin.procediment.class','es.caib.ripea.plugin.caib.procediment.ProcedimentPluginRolsac','Classe del plugin','1','GESCONADM_CLASS','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','GESCONADM','es.caib.ripea.plugin.procediment.rolsac.service.url','https://dev.caib.es/rolsac/api/rest/v1','Url del plugin','2','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','GESCONADM','es.caib.ripea.plugin.procediment.endpointName','Rolsac DEV','Nom del endpoint del plugin','3','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','GESCONADM','es.caib.ripea.plugin.procediment.rolsac.service.username','$distribucio_rolsac','Usuari del plugin','3','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','GESCONADM','es.caib.ripea.plugin.procediment.rolsac.service.password','distribucio_rolsac','Constrasenya del plugin','4','PASSWORD','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','GESCONADM','es.caib.ripea.plugin.procediment.rolsac.service.timeout','20000','Timeout connect','5','INT','1','1','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.class','es.caib.ripea.plugin.caib.viafirma.ViaFirmaPluginImpl','Classe del plugin','0','VIAFIRMA_CLASS','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.apiurl','https://sandbox.viafirma.com/documents/api','Url del plugin','0','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.consumerkey','com.viafirma.documents.apb','CONSUMER_KEY','0','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.consumersecret','ZV56e2W4LP343WQM','CONSUMER_SECRET','0','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.authmode','client_auth','Mode d’autentificació','0','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.group.codi','apb','Codi de l’’aplicació (informar si la propietat anterior es false)','0','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.authtype','OAUTH_APPLICATION','Tipus d’autentificació OAUTH','0','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.callback.url','https://callbacksviafirma.portsdebalears.com/CallBackViaFirma','Url del callback','0','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.callback.username','ripea-viafirma','Usuari del callback','0','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.callback.password','mjluZ23cNh98','Contrasenya del callback','0','PASSWORD','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.endpointName',null,'Nom del endpoint del plugin de ViaFirma','3','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.proxy.host',null,'Proxy URL opcional','13','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.app.codi',null,'Codi aplicacio CAIB','12','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','FIRMA_VIAFIRMA','es.caib.ripea.plugin.viafirma.caib.proxy.port',null,'Port del proxy opcional','14','INT','1','1','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DISTRIBUCIO','es.caib.ripea.distribucio.backofficeIntegracio.ws.url','http://10.35.3.232:8080/distribucioapi/interna','Url del plugin','1','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DISTRIBUCIO','es.caib.ripea.distribucio.backofficeIntegracio.ws.username','admin','Usuari del plugin','2','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DISTRIBUCIO','es.caib.ripea.distribucio.backofficeIntegracio.ws.password','admin','Constrasenya del plugin','3','PASSWORD','0','0','0','0','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DIGITALITZACIO','es.caib.ripea.plugin.digitalitzacio.class','es.caib.ripea.plugin.caib.digitalitzacio.DigitalitzacioPluginDigitalIB','Classe del plugin','1','DIGITALITZACIO_CLASS','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DIGITALITZACIO','es.caib.ripea.plugin.digitalitzacio.digitalib.base.url',null,'Url del plugin','2','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DIGITALITZACIO','es.caib.ripea.plugin.digitalitzacio.endpointName',null,'Nom del endpoint del plugin de digitalitzacio','3','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DIGITALITZACIO','es.caib.ripea.plugin.digitalitzacio.digitalib.username',null,'Nom d’’usuari de DigitalIB','3','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DIGITALITZACIO','es.caib.ripea.plugin.digitalitzacio.digitalib.password',null,'Contrasenya de DigitalIB','4','PASSWORD','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DIGITALITZACIO','es.caib.ripea.plugin.digitalitzacio.digitalib.perfil',null,'Perfil de DigitalIB','5','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DIGITALITZACIO','es.caib.ripea.plugin.digitalitzacio.log','false','Activar logs','6','BOOL','1','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DADES_EXT_PINBAL','es.caib.ripea.plugin.dadesextpinbal.class','es.caib.ripea.plugin.caib.dadesext.DadesExternesPluginCaib','Classe del plugin','1','DADESEXT_CLASS','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','DADES_EXT_PINBAL','es.caib.ripea.plugin.dadesextcaib.service.url',null,'Url del plugin','2','TEXT','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','DADES_EXT_PINBAL','es.caib.ripea.plugin.dadesextpinbal.endpointName',null,'Nom del endpoint del plugin de dades externes (PINBAL)','3','TEXT','0','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SUMMARIZE','es.caib.ripea.plugin.summarize.class','es.caib.ripea.plugin.caib.summarize.SummarizePluginGPT','Classe del plugin','0','SUMMARIZE_CLASS','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SUMMARIZE','es.caib.ripea.plugin.summarize.model','Llama 3 8B Instruct','Model de IA a utilitzar si escau.','0','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','SUMMARIZE','es.caib.ripea.plugin.summarize.url','http://192.168.0.14/v1/completions','Url del servei de resum','1','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','SUMMARIZE','es.caib.ripea.plugin.summarize.usuari',null,'Usuari d''accés al servei en cas de ser necessari','2','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','SUMMARIZE','es.caib.ripea.plugin.summarize.password',null,'Contrasenya d''accés al servei en cas de ser necessari','3','PASSWORD','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SUMMARIZE','es.caib.ripea.plugin.summarize.endpointName',null,'Nom del endpoint del plugin de IA','3','TEXT','0','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SUMMARIZE','es.caib.ripea.plugin.summarize.gpt.apiKey',null,'ApiKey d''accés al servei de resum GPT','4','TEXT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SUMMARIZE','es.caib.ripea.plugin.summarize.service.timeout','3600000','Timeout de connexió amb el servei de resum','5','INT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SUMMARIZE','es.caib.ripea.plugin.summarize.model.maxTokens','8192','Maxim de tokens (tipicament paraules, pero no sempre) que soporta el model en la peticio i resposta combinats.','0','INT','1','0','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','SUMMARIZE','es.caib.ripea.plugin.summarize.debug','false','Activar logs','10','BOOL','1','0','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.conversio.definitiu','false','Activar la conversió definitiu','0','BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.conversio.definitiu.propagar.arxiu','false','Propagar la conversió a definitiu de documents a l''arxiu','1','BOOL','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.propagar.relacio.expedients','false','Permet propagar la relació dels expedients a l''arxiu digital','0','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.notificacio.guardar.certificacio.expedient','true','Tipus de document especific per certificacio de NOTIB','1','BOOL','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.carpetes.defecte','false','Carpetes per defecte','4','BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.incorporar.justificant','false','Activar incorporació justificant','5','BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.metaexpedients.revisio.activa','true','Activar revisió','6','BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.incorporacio.anotacions.duplicada','false','Permetre incorporació duplicada','7','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.habilitar.documentsgenerals',null,'Activar documents generals','8','BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.habilitar.tipusdocument',null,'Activar gestió de tipus documentals NTI','9','BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.procediment.gestio.permis.administrador.organ','true','Habilitar / deshabilitar la gestió de permisos dels administradors d''òrgan','10','BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.habilitar.dominis','true','Activar dominis','11','BOOL','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.plugin.portafirmes.fluxos.usuaris','false','Permetre als usuaris crear fluxos','14','BOOL','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.periode.actualitzacio.contador.anotacions.pendents','150','Període d''actualització del contador d''anotacions pendents en segons','15','INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.mostrar.seguiment.enviaments.usuari','true','Mostrar les pantalles de seguiments dels enviaments a Portafirmes i Notib al perfil d''usuari','16','BOOL','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ALTRES','es.caib.ripea.interessats.permet.canvi.tipus','false','Permetre canviar el tipus dels interessats al importar anotacions','16','BOOL','0','0','0','0','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','ASPECTE','es.caib.ripea.capsalera.logo',null,'Especifica el path on està ubicat el logo de la capçalera','1','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('1','CONTINGUT','es.caib.ripea.index.logo',null,'Especifica el path del logo a mostrar a index','4','TEXT','0','1','0','1','0');

Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ASPECTE','es.caib.ripea.capsalera.color.fons',null,'Color del fons per defecte de l’’aplicació','2','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','ASPECTE','es.caib.ripea.capsalera.color.lletra',null,'Color de la lletra per defecte en la capçalera','3','TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.creacio.carpetes.activa','false','Activar creació de carpetes','1','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.mostrar.carpetes.anotacions','true','Mostrar carpetes per anotacions','2','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.carpetes.logiques','false','Activar les carpetes lògiques','2','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.ordenacio.contingut.habilitada','false','Habilitar la ordenació del contingut','3','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.index.expedients.relacionats','false','Crear index per expedients relacionats','5','BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.index.expedient.camps.addicionals','false','Mostrar camps adicionals a l’’índex de l’expedient','6','BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.creacio.importacio.activa','true','Activar per a mostrar la importació','7','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.creacio.documents.copiarMoure.activa','false','Activar per a permetre copiar i moure documents','8','BOOL','1','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values 
('0','CONTINGUT','es.caib.ripea.creacio.documents.vincular.activa','false','Activar per a permetre vincular documents','9','BOOL','1','1','0','1','0');

---------------------------





















