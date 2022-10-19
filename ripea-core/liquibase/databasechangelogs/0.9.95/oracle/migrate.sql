-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 12.10.22 09:14
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.95/1008.yaml::1660919493847-1::limit
INSERT INTO ipa_metaexp_comment ( id, meta_expedient_id, text, createdby_codi, createddate ) SELECT ipa_hibernate_seq.NEXTVAL, id, revisio_comentari, 'sqlupdate', sysdate FROM ipa_metaexpedient WHERE ipa_metaexpedient.revisio_comentari IS NOT NULL;

-- Changeset db/changelog/changes/0.9.95/1063.yaml::1653978777186-1::limit
ALTER TABLE ipa_metaexpedient ADD organ_no_sinc NUMBER(1);

UPDATE ipa_metaexpedient SET organ_no_sinc = 0;

ALTER TABLE ipa_entitat ADD data_sincronitzacio TIMESTAMP;

ALTER TABLE ipa_entitat ADD data_actualitzacio TIMESTAMP;

ALTER TABLE ipa_organ_gestor ADD estat VARCHAR2(1 CHAR) DEFAULT 'V';

ALTER TABLE ipa_organ_gestor ADD tipus_transicio VARCHAR2(12 CHAR);

ALTER TABLE ipa_avis ADD avis_admin NUMBER(1) DEFAULT '0';

ALTER TABLE ipa_avis ADD entitat_id NUMBER(38, 0);

CREATE TABLE ipa_og_sinc_rel (antic_og NUMBER(38, 0) NOT NULL, nou_og NUMBER(38, 0) NOT NULL);

ALTER TABLE ipa_og_sinc_rel ADD CONSTRAINT ipa_organ_antic_fk FOREIGN KEY (antic_og) REFERENCES ipa_organ_gestor (id);

ALTER TABLE ipa_og_sinc_rel ADD CONSTRAINT ipa_organ_nou_fk FOREIGN KEY (nou_og) REFERENCES ipa_organ_gestor (id);

ALTER TABLE ipa_og_sinc_rel ADD CONSTRAINT ipa_uo_sinc_rel_mult_uk UNIQUE (antic_og, nou_og);

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.procediment.actualitzar.cron', NULL, 'Cron per a la actualització de procediments', 'SCHEDULLED', '6', '0', 'TEXT', '0');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.organs.consulta.canvis', NULL, 'Cron per a la consulta de canvis a l''organigrama', 'SCHEDULLED', '7', '0', 'TEXT', '0');

grant select, update, insert, delete on ipa_og_sinc_rel to www_ripea;

update ipa_metaexp_seq set valor = 0 where valor is null;

alter table ipa_metaexp_seq modify ( valor not null);

-- Changeset db/changelog/changes/0.9.95/1067.yaml::1659525430753-1::limit
ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_dni NUMBER(1) DEFAULT 1 NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_nif NUMBER(1) DEFAULT 1 NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_cif NUMBER(1) DEFAULT 1 NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_nie NUMBER(1) DEFAULT 1 NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_pas NUMBER(1) DEFAULT 1 NOT NULL;

-- Changeset db/changelog/changes/0.9.95/1123.yaml::1660219576096-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.document.esborrar.finals', 'true', 'Permetre esborrar documents CUSTODIATS, FIRMATS, DEFINITIUS I IMPORTATS', 'CONTINGUT', '21', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.95/1137.yaml::1662622106104-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.distribucio.regla.autenticacio.basic', 'false', 'Client REST creació regles amb tipus autenticació BASIC', 'DISTRIBUCIO_REGLA', '5', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.95/1142.yaml::1663146757135-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.identificador.origen.mascara', 'true', 'Aplicar màscara al camp "Identificador del document origen"', 'CONTINGUT', '23', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.95/1143.yaml::1663075341965-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.estat.elaboracio.identificador.origen.obligat', 'EE02, EE03, EE04', 'Estats elaboració on és obligatori el camp ''Identificador ENI del document origen'', separats per coma (EE02, EE03...)', 'CONTINGUT', '22', '0', 'TEXT', '1');

-- Changeset db/changelog/changes/0.9.95/1158.yaml::1664193915343-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.document.propagar.modificacio.arxiu', 'false', 'Propagar les modificacions d''un document a l''arxiu (també habilita la modificació de la data del document)', 'CONTINGUT', '24', '0', 'BOOL', '1');

