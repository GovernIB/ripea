-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 01/07/24 11:18
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.109/1460.yaml::1715094291129-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.periode.actualitzacio.contador.anotacions.pendents', '150', 'Període d''actualització del contador d''anotacions pendents en segons', 'ALTRES', '15', '0', 'INT', '0');

-- Changeset db/changelog/changes/0.9.109/1477.yaml::1715760072391-1::limit
UPDATE ipa_config SET key = 'es.caib.ripea.plugin.arxiu.caib.csv_generation_definition' WHERE key='es.caib.ripea.plugin.arxiu.caib.csv.definicio';

-- Changeset db/changelog/changes/0.9.109/1486.yaml::1716384985679-2::limit
ALTER TABLE ipa_expedient_tasca ADD titol VARCHAR(256);

ALTER TABLE ipa_expedient_tasca ADD observacions VARCHAR(1024);

-- Changeset db/changelog/changes/0.9.109/1497.yaml::1718262082962-1::limit
ALTER TABLE IPA_INTERESSAT ADD CONSTRAINT IPA_INTERESSAT_EXP_UK UNIQUE (DOCUMENT_NUM, EXPEDIENT_ID);

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.interessats.permet.canvi.tipus', 'false', 'Permetre canviar el tipus dels interessats al importar anotacions', 'ALTRES', '16', '0', 'BOOL', '0', '0');
