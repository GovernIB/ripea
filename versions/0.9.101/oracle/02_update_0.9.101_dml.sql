-- Changeset db/changelog/changes/0.9.101/1302.yaml::1688125180951-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.expedient.exportar.inside', 'false', 'Permetre exportació ENI per importar a INSIDE', 'CONTINGUT', '38', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.101/1304.yaml::1689325988450-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.expedient.tancament.logic', 'false', 'Habilitar tancament lògic dels expedients', 'CONTINGUT', '39', '0', 'BOOL', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.expedient.tancament.logic.dies', '60', 'Dies per tancar un expedient a l''arxiu des del seu tancament a Ripea', 'CONTINGUT', '40', '0', 'INT', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.expedient.tancament.logic.cron', '0 0 20 * * *', 'Consulta diaria dels expedients pendents de tancar i si ha arribat la data programada', 'CONTINGUT', '41', '0', 'TEXT', '1');

-- Changeset db/changelog/changes/0.9.101/1316.yaml::1691495012174-1::limit
UPDATE ipa_document_enviament SET pf_avis_firma_parcial = 0;

UPDATE ipa_execucio_massiva SET pfirmes_avis_firma_parcial = 0;

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.portafirmes.avis.firma.parcial', 'false', 'Mostrar opció per indicar si rebre avisos de firmes parcials', 'PORTAFIRMES', '8', '0', 'BOOL', '1');
