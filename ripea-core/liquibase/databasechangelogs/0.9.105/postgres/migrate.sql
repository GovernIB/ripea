-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 05.12.23 20:29
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.105/1036.yaml::1701433927769-1::limit
ALTER TABLE ipa_grup ADD codi VARCHAR(50) DEFAULT 'codi' NOT NULL;

update ipa_grup set codi = rol;

insert into ipa_processos_inicials (codi, init, id) values ('GRUPS_PERMISOS', 1, 6);

-- Changeset db/changelog/changes/0.9.105/1188.yaml::1700642617760-1::limit
ALTER TABLE ipa_metaexpedient ADD tipus_classificacio VARCHAR(3) DEFAULT 'SIA' NOT NULL;

alter table ipa_metaexpedient modify clasif_sia varchar2(46 char);

alter table ipa_metaexpedient add constraint ipa_metaexpedient_clasif_uk unique (clasif_sia);

alter table ipa_expedient modify nti_clasif_sia varchar2(46 char);

-- Changeset db/changelog/changes/0.9.105/1300.yaml::1700485243643-1::limit
ALTER TABLE ipa_usuari ADD num_elements_pagina numeric(19) DEFAULT 10;

-- Changeset db/changelog/changes/0.9.105/1377.yaml::1699353474116-1::limit
CREATE TABLE ipa_consulta_pinbal (id BIGINT NOT NULL, entitat_id numeric(19) NOT NULL, servei VARCHAR(64) NOT NULL, pinbal_idpeticion VARCHAR(64), estat VARCHAR(10) NOT NULL, error VARCHAR(4000), expedient_id numeric(19), metaexpedient_id numeric(19), document_id numeric(19), createdby_codi VARCHAR(64), createddate TIMESTAMP WITHOUT TIME ZONE, lastmodifiedby_codi VARCHAR(64), lastmodifieddate TIMESTAMP WITHOUT TIME ZONE);

ALTER TABLE ipa_consulta_pinbal ADD CONSTRAINT ipa_consulta_pinbal_pk PRIMARY KEY (id);

ALTER TABLE ipa_consulta_pinbal ADD CONSTRAINT ipa_entitat_pinbal_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

ALTER TABLE ipa_consulta_pinbal ADD CONSTRAINT ipa_expedient_pinbal_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

ALTER TABLE ipa_consulta_pinbal ADD CONSTRAINT ipa_metaexp_pinbal_fk FOREIGN KEY (metaexpedient_id) REFERENCES ipa_metaexpedient (id);

ALTER TABLE ipa_consulta_pinbal ADD CONSTRAINT ipa_document_pinbal_fk FOREIGN KEY (document_id) REFERENCES ipa_document (id);

ALTER TABLE ipa_consulta_pinbal ADD CONSTRAINT ipa_usucre_pinbal_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

ALTER TABLE ipa_consulta_pinbal ADD CONSTRAINT ipa_usumod_pinbal_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/changes/0.9.105/1382.yaml::1699887361133-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.obtenir.data.firma.atributs.document', 'false', 'Obtenir informació firma utilitzant les metadades del document', 'CONTINGUT', '40', '0', 'BOOL', '0');

-- Changeset db/changelog/changes/0.9.105/1393.yaml::1701419006016-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.activar.logs.permisos', 'false', 'Activar logs per permisos', 'LOGS', '3', '0', 'BOOL', '0', '0');

