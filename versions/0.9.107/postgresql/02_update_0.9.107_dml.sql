-- Changeset db/changelog/changes/0.9.107/1398.yaml::1708087778804-1::limit
update ipa_usuari set vista_actual = 'TREETABLE_PER_CARPETA';

-- Changeset db/changelog/changes/0.9.107/1417.yaml::1707304073271-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.activar.logs.grups', 'false', 'Activar logs per grups', 'LOGS', '8', '0', 'BOOL', '0', '0');

-- Changeset db/changelog/changes/0.9.107/1433.yaml::1709132700705-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.mostrar.logs.cercador.anotacions', 'false', 'Activar logs per cercador d''anotacions', 'LOGS', '9', '0', 'BOOL', '0');

-- Changeset db/changelog/changes/0.9.107/1431.yaml::1709132700705-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.mostrar.logs.segonpla', 'false', 'Activar logs per segon pla', 'LOGS', '10', '0', 'BOOL', '0');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.mostrar.logs.integracio', 'false', 'Activar logs per integracions', 'LOGS', '11', '0', 'BOOL', '0');
