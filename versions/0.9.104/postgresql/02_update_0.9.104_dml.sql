-- Changeset db/changelog/changes/0.9.104/1041.yaml::1697009246691-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.plugin.digitalitzacio.log', 'false', 'Activar logs', 'DIGITALITZACIO', '6', '0', 'BOOL', '0');

-- Changeset db/changelog/changes/0.9.104/1172.yaml::1699878361884-1::limit
update ipa_interessat set document_tipus = 'NIF' where document_tipus = 'CIF';

-- Changeset db/changelog/changes/0.9.104/1369.yaml::1697785157435-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.plugin.portafirmes.fluxos.usuaris', 'false', 'Permetre als usuaris crear fluxos', 'ALTRES', '14', '0', 'BOOL', '0');