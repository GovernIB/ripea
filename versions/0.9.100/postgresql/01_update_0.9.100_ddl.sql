-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 27.06.23 12:27
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.100/1266.yaml::1685347347557-1::limit
ALTER TABLE ipa_tipus_documental ADD nom_catala VARCHAR(256);

-- Changeset db/changelog/changes/0.9.100/1272.yaml::1686225166612-1::limit
ALTER TABLE ipa_document ADD fitxer_tamany INTEGER;
