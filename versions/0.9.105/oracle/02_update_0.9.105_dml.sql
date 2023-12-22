-- Changeset db/changelog/changes/0.9.105/1036.yaml::1701433927769-1::limit
update ipa_grup set codi = rol;

insert into ipa_processos_inicials (codi, init, id) values ('GRUPS_PERMISOS', 1, 6);

-- Changeset db/changelog/changes/0.9.105/1393.yaml::1701419006016-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.activar.logs.permisos', 'false', 'Activar logs per permisos', 'LOGS', '3', '0', 'BOOL', '0', '0');

-- Changeset db/changelog/changes/0.9.105/1382.yaml::1699887361133-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.obtenir.data.firma.atributs.document', 'false', 'Obtenir informaciÃ³ firma utilitzant les metadades del document', 'CONTINGUT', '40', '0', 'BOOL', '0');

-- Changeset db/changelog/changes/0.9.105/1397.yaml::1703170006766-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.mostrar.logs.rendiment', 'false', 'Generar logs de rendiment', 'LOGS', '7', '0', 'BOOL', '0');
