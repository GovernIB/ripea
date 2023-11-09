-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 06.11.23 09:04
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.104/1041.yaml::1697009246691-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.plugin.digitalitzacio.log', 'false', 'Activar logs', 'DIGITALITZACIO', '6', '0', 'BOOL', '0');

-- Changeset db/changelog/changes/0.9.104/1369.yaml::1697785157435-1::limit
CREATE TABLE IPA_FLUX_FIRMA_USUARI (ID NUMBER(19) NOT NULL, NOM VARCHAR2(80 CHAR) NOT NULL, DESCRIPCIO VARCHAR2(256 CHAR), DESTINATARIS VARCHAR2(512 CHAR), PORTAFIRMES_FLUXID VARCHAR2(64 CHAR) NOT NULL, USUARI_CODI VARCHAR2(64 CHAR) NOT NULL, ENTITAT_ID NUMBER(19) NOT NULL, CREATEDBY_CODI VARCHAR2(64 CHAR), CREATEDDATE TIMESTAMP, LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), LASTMODIFIEDDATE TIMESTAMP, CONSTRAINT IPA_FLUX_FIRMA_USUARI_PK PRIMARY KEY (ID));

ALTER TABLE IPA_FLUX_FIRMA_USUARI ADD CONSTRAINT IPA_FLUX_FIRMA_USUARI_USU_FK FOREIGN KEY (USUARI_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_FLUX_FIRMA_USUARI ADD CONSTRAINT IPA_FLUX_FIRMA_USUARI_ENT_FK FOREIGN KEY (ENTITAT_ID) REFERENCES IPA_ENTITAT (ID);

GRANT SELECT, UPDATE, INSERT, DELETE ON IPA_FLUX_FIRMA_USUARI TO WWW_RIPEA;

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.plugin.portafirmes.fluxos.usuaris', 'false', 'Permetre als usuaris crear fluxos', 'ALTRES', '14', '0', 'BOOL', '0');
