-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 27.06.23 12:27
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.100/1266.yaml::1685347347557-1::limit
update ipa_tipus_documental set nom_catala = 'Resolució' where codi = 'TD01';

update ipa_tipus_documental set nom_catala = 'Acord' where codi = 'TD02';

update ipa_tipus_documental set nom_catala = 'Contracte' where codi = 'TD03';

update ipa_tipus_documental set nom_catala = 'Conveni' where codi = 'TD04';

update ipa_tipus_documental set nom_catala = 'Declaració' where codi = 'TD05';

update ipa_tipus_documental set nom_catala = 'Comunicació' where codi = 'TD06';

update ipa_tipus_documental set nom_catala = 'Notificació' where codi = 'TD07';

update ipa_tipus_documental set nom_catala = 'Publicació' where codi = 'TD08';

update ipa_tipus_documental set nom_catala = 'Acusament de rebut' where codi = 'TD09';

update ipa_tipus_documental set nom_catala = 'Acta' where codi = 'TD10';

update ipa_tipus_documental set nom_catala = 'Certificat' where codi = 'TD11';

update ipa_tipus_documental set nom_catala = 'Diligència' where codi = 'TD12';

update ipa_tipus_documental set nom_catala = 'Informe' where codi = 'TD13';

update ipa_tipus_documental set nom_catala = 'Sol·licitud' where codi = 'TD14';

update ipa_tipus_documental set nom_catala = 'Denúncia' where codi = 'TD15';

update ipa_tipus_documental set nom_catala = 'Al·legació' where codi = 'TD16';

update ipa_tipus_documental set nom_catala = 'Recursos' where codi = 'TD17';

update ipa_tipus_documental set nom_catala = 'Comunicació ciutadà' where codi = 'TD18';

update ipa_tipus_documental set nom_catala = 'Factura' where codi = 'TD19';

update ipa_tipus_documental set nom_catala = 'Altres incautats' where codi = 'TD20';

update ipa_tipus_documental set nom_catala = 'Altres' where codi = 'TD99';

update ipa_config set description = 'Activar gestió de tipus documentals NTI' where key like '%habilitar.tipusdocument';
delete from ipa_config where key like '%.arxiu.metadocumental.addicional.actiu';  

-- Changeset db/changelog/changes/0.9.100/1272.yaml::1686225166612-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.concsv.base.url', '', 'Url base al servei de CONCSV', 'ARXIU', '31', '1', 'TEXT', '1', '1');

-- Changeset db/changelog/changes/0.9.100/1277.yaml::1684938156277-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.expedient.permetre.punts', 'false', 'Permetre la creació d''expedients amb punts al nom', 'CONTINGUT', '35', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.100/1279.yaml::1685959664822-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.expedient.exportacio.excel', 'false', 'Permetre exportació índex expedient a EXCEL', 'CONTINGUT', '36', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.100/1280.yaml::1682673428002-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.firmasimpleasync.url', NULL, 'Url de l''api firma simple async', 'PORTAFIRMES', '1', '1', 'TEXT', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.firmasimpleasync.username', NULL, 'Usuari de l''api firma simple async', 'PORTAFIRMES', '2', '1', 'TEXT', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.firmasimpleasync.password', NULL, 'Constrasenya de l''api firma simple async', 'PORTAFIRMES', '3', '1', 'PASSWORD', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.firmasimpleflux.url', NULL, 'Url de l''api firma simple flux', 'PORTAFIRMES', '4', '1', 'TEXT', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.firmasimpleflux.username', NULL, 'Usuari de l''api firma simple flux', 'PORTAFIRMES', '5', '1', 'TEXT', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.firmasimpleflux.password', NULL, 'Constrasenya de l''api firma simple flux', 'PORTAFIRMES', '6', '1', 'PASSWORD', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.usuarientitatws.url', NULL, 'Url de UsuariEntitat webservice', 'PORTAFIRMES', '7', '1', 'TEXT', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.usuarientitatws.username', NULL, 'Usuari de UsuariEntitat webservice', 'PORTAFIRMES', '8', '1', 'TEXT', '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.usuarientitatws.password', NULL, 'Constrasenya de UsuariEntitat webservice', 'PORTAFIRMES', '9', '1', 'PASSWORD', '1', '1');

delete from ipa_config where key like '%.plugin.portafirmes.portafib.base.url';

delete from ipa_config where key like '%.plugin.portafirmes.portafib.username';

delete from ipa_config where key like '%.plugin.portafirmes.portafib.password';

update ipa_config set position = 10 where key = 'es.caib.ripea.plugin.portafirmes.flux.filtrar.usuari.descripcio';

update ipa_config set position = 11 where key = 'es.caib.ripea.plugin.portafirmes.carrer.mostrar.persona';

update ipa_config set position = 0 where key = 'es.caib.ripea.plugin.portafirmes.class';

-- Changeset db/changelog/changes/0.9.100/1287.yaml::1685959676540-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.contingut.contreure.carpetes', 'false', 'Contreure per defecte les carpetes', 'CONTINGUT', '37', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.100/1296.yaml::1686745220463-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.mostrar.logs.creacio.contingut', 'false', 'Activar logs per creació de contingut', 'LOGS', '2', '0', 'BOOL', '0', '0');

-- Changeset db/changelog/changes/0.9.100/1326.yaml::1691745729154-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.activar.tipus.document.portafirmes', 'false', 'Activar l''ús del tipus de document de portafirmes', 'ALTRES', '13', '0', 'BOOL', '1', '1');

-- Changeset db/changelog/changes/0.9.100/1328.yaml::1692195649405-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.notificacio.debug', 'false', 'Debug', 'NOTIB', '13', '0', 'BOOL', '0', '0');
