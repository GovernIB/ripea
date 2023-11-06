-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 06.11.23 09:05
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.104/1041.yaml::1697009246691-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.plugin.digitalitzacio.log', 'false', 'Activar logs', 'DIGITALITZACIO', '6', '0', 'BOOL', '0');

-- Changeset db/changelog/changes/0.9.104/1369.yaml::1697785157435-1::limit
CREATE TABLE IPA_FLUX_FIRMA_USUARI (ID numeric(19) NOT NULL, NOM VARCHAR(80) NOT NULL, DESCRIPCIO VARCHAR(256), PORTAFIRMES_FLUXID VARCHAR(64) NOT NULL, USUARI_CODI VARCHAR(64) NOT NULL, ENTITAT_ID numeric(19) NOT NULL, CREATEDBY_CODI VARCHAR(64), CREATEDDATE TIMESTAMP WITHOUT TIME ZONE, LASTMODIFIEDBY_CODI VARCHAR(64), LASTMODIFIEDDATE TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT IPA_FLUX_FIRMA_USUARI_PK PRIMARY KEY (ID));

ALTER TABLE IPA_FLUX_FIRMA_USUARI ADD CONSTRAINT IPA_FLUX_FIRMA_USUARI_USU_FK FOREIGN KEY (USUARI_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_FLUX_FIRMA_USUARI ADD CONSTRAINT IPA_FLUX_FIRMA_USUARI_ENT_FK FOREIGN KEY (ENTITAT_ID) REFERENCES IPA_ENTITAT (ID);

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.plugin.portafirmes.fluxos.usuaris', 'false', 'Permetre als usuaris crear fluxos', 'ALTRES', '14', '0', 'BOOL', '0');

