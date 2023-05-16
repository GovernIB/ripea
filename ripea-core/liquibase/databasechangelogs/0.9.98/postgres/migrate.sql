-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 20.03.23 15:47
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.98/1186.yaml::1677771425170-1::limit
ALTER TABLE ipa_document ADD expedient_estat_id numeric(19);

ALTER TABLE ipa_document ADD CONSTRAINT ipa_expestat_document_fk FOREIGN KEY (expedient_estat_id) REFERENCES ipa_expedient_estat (id);

ALTER TABLE ipa_usuari ADD vista_actual VARCHAR(64);

ALTER TABLE ipa_metadocument ADD ordre INTEGER DEFAULT 0 NOT NULL;

-- Changeset db/changelog/changes/0.9.98/1231.yaml::1678183976980-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.numero.expedient.separador', '/', 'Separador del número d''expedient', 'CONTINGUT', '29', '0', 'TEXT', '1');

-- Changeset db/changelog/changes/0.9.98/1232.yaml::1678274802658-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.numero.expedient.propagar.arxiu', 'false', 'Propagar el número d''expedient a l''arxiu', 'CONTINGUT', '30', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.98/1269.yaml::1678274802658-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.portafirmes.carrer.mostrar.persona', 'false', 'Mostrar la persona relacionat amb càrrec', 'PORTAFIRMES', '6', '0', 'BOOL', '1', '1');
