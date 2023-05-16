-- Changeset db/changelog/changes/0.9.98/1231.yaml::1678183976980-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.numero.expedient.separador', '/', 'Separador del número d''expedient', 'CONTINGUT', '29', '0', 'TEXT', '1');

-- Changeset db/changelog/changes/0.9.98/1232.yaml::1678274802658-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.numero.expedient.propagar.arxiu', 'false', 'Propagar el número d''expedient a l''arxiu', 'CONTINGUT', '30', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.98/1269.yaml::1678274802658-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.carrer.mostrar.persona', 'false', 'Mostrar la persona relacionat amb càrrec', 'PORTAFIRMES', '6', '0', 'BOOL', '1', '1');
