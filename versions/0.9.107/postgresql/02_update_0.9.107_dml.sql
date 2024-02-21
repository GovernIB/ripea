-- Changeset db/changelog/changes/0.9.107/1398.yaml::1708087778804-1::limit
update ipa_usuari set vista_actual = 'TREETABLE_PER_CARPETA';

-- Changeset db/changelog/changes/0.9.107/1417.yaml::1707304073271-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.activar.logs.grups', 'false', 'Activar logs per grups', 'LOGS', '8', '0', 'BOOL', '0', '0');
