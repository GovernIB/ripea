-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 25/06/24 18:07
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.109/1497.yaml::1718262082962-1::limit
ALTER TABLE IPA_INTERESSAT ADD CONSTRAINT IPA_INTERESSAT_EXP_UK UNIQUE (DOCUMENT_NUM, EXPEDIENT_ID);

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.interessats.permet.canvi.tipus', 'false', 'Permetre canviar el tipus dels interessats al importar anotacions', 'ALTRES', '16', '0', 'BOOL', '0', '0');

