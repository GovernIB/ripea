-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 16.05.23 10:14
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.99/1224.yaml::1681300001535-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.notificacio.multiple.pdf.concatenar', 'false', 'Al notificar documents multiples concatenar versions imprimibles de documents si tots els documents seleccionats per notificar són PDFs', 'CONTINGUT', '33', '0', 'BOOL', '0');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.notificacio.multiple.document.generat.visible', 'true', 'Al notificar documents multiples guardar el document generat com a el document visible a l''usuari', 'CONTINGUT', '34', '0', 'BOOL', '0');

-- Changeset db/changelog/changes/0.9.99/1225.yaml::1680014851572-1::limit
ALTER TABLE ipa_document_enviament ADD not_data_enviada TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE ipa_document_enviament ADD not_data_finalitzada TIMESTAMP WITHOUT TIME ZONE;

-- Changeset db/changelog/changes/0.9.99/1228.yaml::1680693564552-1::limit
ALTER TABLE ipa_usuari ADD avisos_noves_anotacions BOOLEAN;

UPDATE ipa_usuari SET avisos_noves_anotacions = FALSE;

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.mostrar.logs.email', 'false', 'Activar logs per als correus electrònics', 'LOGS', '0', '0', 'BOOL', '0');

-- Changeset db/changelog/changes/0.9.99/1233.yaml::1678723004412-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.document.enviar.contingut.existent', 'true', 'Enviar contingut document a l''arxiu sense modificar', 'CONTINGUT', '31', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.99/1243.yaml::1679321539857-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.expedient.propagar.metadades', 'true', 'Permetre enviar les metadades marcades com ''Enviables arxiu'' a l''arxiu', 'CONTINGUT', '32', '0', 'BOOL', '1');

ALTER TABLE ipa_metadada ADD enviable BOOLEAN DEFAULT FALSE;

ALTER TABLE ipa_metadada ADD metadada_arxiu VARCHAR(255);

-- Changeset db/changelog/changes/0.9.99/1244.yaml::1680168055519-1::limit
ALTER TABLE ipa_organ_gestor ADD nom_es VARCHAR(1000);

UPDATE ipa_organ_gestor SET NOM_ES = NOM;

INSERT INTO IPA_PROCESSOS_INICIALS (codi, init, id) VALUES ('ORGANS_DESCARREGAR_NOM_CATALA', 1, 5);

-- Changeset db/changelog/changes/0.9.99/1248.yaml::1680009173416-1::limit
CREATE TABLE IPA_URL_INSTRUCCION (ID numeric(19) NOT NULL, NOM VARCHAR(80) NOT NULL, CODI VARCHAR(80) NOT NULL, DESCRIPCIO VARCHAR(256), URL VARCHAR(256) NOT NULL, entitat_id numeric(19) NOT NULL, CREATEDBY_CODI VARCHAR(64), CREATEDDATE TIMESTAMP WITHOUT TIME ZONE, LASTMODIFIEDBY_CODI VARCHAR(64), LASTMODIFIEDDATE TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT IPA_URL_INSTRUCCION_PK PRIMARY KEY (ID));

ALTER TABLE IPA_URL_INSTRUCCION ADD CONSTRAINT "IPA_URL_INSTRUCCION_ENT_fk" FOREIGN KEY (entitat_id) REFERENCES IPA_ENTITAT (id);

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.expedient.generar.urls.instruccio', 'false', 'Permetre generar URLs d''instrucció', 'CONTINGUT', '32', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.99/1257.yaml::1682673428002-1::limit
INSERT INTO ipa_config_group (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES ('3', 'FIRMA', 'FIRMA_SIMPLE_WEB', 'Configuració del plugin de firma simple web');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.firmasimpleweb.class', 'es.caib.ripea.plugin.caib.firmaweb.FirmaSimpleWebPluginPortafib', 'Classe del plugin', 'FIRMA_SIMPLE_WEB', '1', '0', 'TEXT', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.firmasimpleweb.endpoint', NULL, 'Url del plugin', 'FIRMA_SIMPLE_WEB', '2', '1', 'TEXT', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.firmasimpleweb.username', NULL, 'Usuari del plugin', 'FIRMA_SIMPLE_WEB', '3', '1', 'TEXT', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.firmasimpleweb.password', NULL, 'Constrasenya del plugin', 'FIRMA_SIMPLE_WEB', '4', '1', 'PASSWORD', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.firmasimpleweb.location', 'Palma', 'Lloc on es realitza la firma', 'FIRMA_SIMPLE_WEB', '4', '0', 'TEXT', '1', '1');

delete from ipa_config where group_code = 'FIRMA_PASSARELA';

delete from ipa_config where group_code = 'FIRMA_PASSARELA-1';

delete from ipa_config where group_code = 'FIRMA_PASSARELA-2';

delete from ipa_config where group_code = 'FIRMA_PASSARELA-3';

delete from ipa_config_group where code = 'FIRMA_PASSARELA';

delete from ipa_config_group where code = 'FIRMA_PASSARELA-1';

delete from ipa_config_group where code = 'FIRMA_PASSARELA-2';

delete from ipa_config_group where code = 'FIRMA_PASSARELA-3';

delete from ipa_config where key like '%.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username';

delete from ipa_config where key like '%.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url';

delete from ipa_config where key like '%.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password';

-- Changeset db/changelog/changes/0.9.99/833.yaml::1680260169203-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.habilitar.dominis', 'false', 'Activar dominis', 'ALTRES', '11', '0', 'BOOL', '0');

-- Changeset db/changelog/changes/0.9.99/930.yaml::1680608723206-1::limit
ALTER TABLE ipa_usuari ADD email_alternatiu VARCHAR(200);

-- Changeset db/changelog/changes/0.9.99/1282.yaml::1685627826297-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.mostrar.logs.rendiment.descarregar.anotacio', 'false', 'Generar logs de rendiment en la descàrrega d''anotacions de distribució', 'LOGS', '1', '0', 'BOOL', '0');

