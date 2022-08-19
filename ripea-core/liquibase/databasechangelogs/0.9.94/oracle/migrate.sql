-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 19/08/22 10:47
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.94/945.yaml::11651146740000-3::limit
CREATE TABLE IPA_PROCESSOS_INICIALS (ID NUMBER(19, 0) NOT NULL, CODI VARCHAR2(100 CHAR) NOT NULL, INIT NUMBER(1, 0) NOT NULL, CONSTRAINT IPA_PROCESSOS_INICIALS_PK PRIMARY KEY (ID));
INSERT INTO IPA_PROCESSOS_INICIALS (codi, init, id) VALUES ('PROPIETATS_CONFIG_ENTITATS', 1, 2);

ALTER TABLE ipa_config ADD entitat_codi VARCHAR2(64 CHAR);
ALTER TABLE ipa_config ADD configurable NUMBER(1, 0) DEFAULT '0';

UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.creacio.carpetes.activa';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.mostrar.carpetes.anotacions';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.carpetes.logiques';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.ordenacio.contingut.habilitada';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.index.logo';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.index.expedients.relacionats';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.index.expedient.camps.addicionals';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.creacio.importacio.activa';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.creacio.documents.copiarMoure.activa';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.creacio.documents.vincular.activa';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.descarregar.imprimible.nofirmats';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.expedient.permetre.reobrir';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.document.modificar.custodiats';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.document.nou.escanejar.actiu';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.creacio.documents.publicar.activa';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.documents.firma.biometrica.activa';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.documents.validacio.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.creacio.documents.moure.mateix.expedient';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.base.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.usuari';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.contrasenya';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.usuari';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.contrasenya';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.aplicacio.codi';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.csv.definicio';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.timeout.connect';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.arxiu.caib.timeout.read';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.arxiu.metadades.addicionals.actiu';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.arxiu.firma.detalls.actiu';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.unitats.organitzatives.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.unitats.organitzatives.dir3.service.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.unitats.organitzatives.dir3.service.username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.unitats.organitzatives.dir3.service.password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.unitats.cerca.dir3.service.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.unitats.organitzatives.dir3.connect.timeout';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.unitats.organitzatives.dir3.request.timeout';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.notificacio.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.notificacio.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.notificacio.username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.notificacio.password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.notificacio.retard.num.dies';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.notificacio.caducitat.num.dies';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.notificacio.enviament.postal.actiu';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.notificacio.enviament.deh.activa';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.notificacio.forsar.entitat';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.notificacio.guardar.certificacio.expedient';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.conversio.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.conversio.ooffice.host';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.conversio.ooffice.port';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.dadesext.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.dadesext.service.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.procediment.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.procediment.rolsac.service.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.procediment.rolsac.service.username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.procediment.rolsac.service.password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.digitalitzacio.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.digitalitzacio.digitalib.base.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.digitalitzacio.digitalib.username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.digitalitzacio.digitalib.password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.digitalitzacio.digitalib.perfilClasse del plugin';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.pinbal.basic.auth';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.pinbal.base.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.pinbal.user';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.pinbal.password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.pinbal.codi.sia.peticions';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.gesdoc.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.gesdoc.filesystem.base.dir';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.portafirmes.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.portafirmes.portafib.base.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.portafirmes.portafib.username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.portafirmes.portafib.password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.portafirmes.flux.filtrar.usuari.descripcio';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.apiurl';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.consumerkey';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.consumersecret';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.authmode';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.authtype';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.app.codi';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.callback.username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.callback.password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.proxy.host';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.proxy.port';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.dispositius.enabled';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.viafirma.caib.callback.url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.versio.antiga';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.ids';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.ignorar.modal.ids';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.1.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.1.nom';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.1.desc';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.1.plugins.signatureweb.portafib.api_passarela_url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.1.plugins.signatureweb.portafib.api_passarela_username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.1.plugins.signatureweb.portafib.api_passarela_password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.3.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.3.nom';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.3.desc';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.3.plugins.signatureweb.miniappletinserver.base_dir';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.3.plugins.signatureweb.miniappletinserver.ignore_certificate_filter';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.2.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.2.nom';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.passarelafirma.2.desc';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.endpoint';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.auth.username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.auth.password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.perfil';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.signerEmail';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.location';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.firmaservidor.portafib.username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugin.validatesignature.class';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugins.validatesignature.afirmacxf.endpoint';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.username';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.password';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugins.validatesignature.afirmacxf.applicationID';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugins.validatesignature.afirmacxf.ignoreservercertificates';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.plugins.validatesignature.afirmacxf.TransformersTemplatesPath';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.segonpla.guardar.arxiu.max.reintents.expedients';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.segonpla.guardar.arxiu.max.reintents.documents';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.segonpla.guardar.arxiu.max.reintents.interessats';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.tasca.preavisDataLimitEnDies';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.email.remitent';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.conversio.definitiu';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.propagar.relacio.expedients';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.arxiu.metadocumental.addicional.actiu';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.carpetes.defecte';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.incorporar.justificant';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.metaexpedients.revisio.activa';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.incorporacio.anotacions.duplicada';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.habilitar.documentsgenerals';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.habilitar.tipusdocument';
UPDATE IPA_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.ripea.procediment.gestio.permis.administrador.organ';

-- Changeset db/changelog/changes/0.9.94/1019.yaml::1651146740000-1::limit
UPDATE IPA_INTERESSAT SET INCAPACITAT = 0 WHERE INCAPACITAT = 1;

-- Changeset db/changelog/changes/0.9.94/1111.yaml::1651146740001-1::limit
ALTER TABLE ipa_expedient_peticio ADD pendent_canvi_estat_dis NUMBER(1, 0) DEFAULT '0';
ALTER TABLE ipa_expedient_peticio ADD reintents_canvi_estat_dis NUMBER(10, 0) DEFAULT '0';

INSERT INTO IPA_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.ripea.segonpla.max.reintents.anotacions.pendents.enviar.distribucio', 3, 'Nombre de reintents de canviar estat de anotacions a Distribució', 'SCHEDULLED', 0, 0, 'INT', 0);
INSERT INTO IPA_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.ripea.segonpla.reintentar.anotacions.pendents.enviar.distribucio', 60000, 'Interval de temps (ms) en que executa la tasca periodica de reintentar canviar estat de anotacions a Distribució', 'SCHEDULLED', 0, 0, 'INT', 0);

-- Changeset db/changelog/changes/0.9.94/1119.yaml::1659009409606-1::limit
ALTER TABLE ipa_hist_exp_interessat MODIFY interessat_doc_num NULL;
