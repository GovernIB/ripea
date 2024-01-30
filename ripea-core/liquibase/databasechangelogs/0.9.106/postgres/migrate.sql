-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 30.01.24 16:44
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.106/1289.yaml::1705400143333-1::limit
insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (17, 'SVDBECAWS01', false, false, false, false, false);

-- Changeset db/changelog/changes/0.9.106/1321.yaml::1704906653342-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.firmasimpleweb.debug', 'false', 'Debug', 'FIRMA_SIMPLE_WEB', '6', '0', 'BOOL', '0', '0');

-- Changeset db/changelog/changes/0.9.106/1345.yaml::1705400143333-1::limit
insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (16, 'SVDRRCCDEFUNCIONWS01', false, false, false, false, false);

-- Changeset db/changelog/changes/0.9.106/1346.yaml::1705333214600-1::limit
insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (15, 'SVDRRCCMATRIMONIOWS01', false, false, false, false, false);

-- Changeset db/changelog/changes/0.9.106/1347.yaml::1703236956442-1::limit
insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (14, 'SVDRRCCNACIMIENTOWS01', false, false, false, false, false);

-- Changeset db/changelog/changes/0.9.106/1381.yaml::1705668944465-1::limit
delete from ipa_config where key like '%escaneig%';

delete from ipa_config_group where description like '%escaneig%';

-- Changeset db/changelog/changes/0.9.106/1387.yaml::1703236956442-1::limit
CREATE TABLE ipa_pinbal_servei (id BIGINT NOT NULL, codi VARCHAR(64) NOT NULL, doc_permes_dni BOOLEAN NOT NULL, doc_permes_nif BOOLEAN NOT NULL, doc_permes_cif BOOLEAN NOT NULL, doc_permes_nie BOOLEAN NOT NULL, doc_permes_pas BOOLEAN NOT NULL, createdby_codi VARCHAR(64), createddate TIMESTAMP WITHOUT TIME ZONE, lastmodifiedby_codi VARCHAR(64), lastmodifieddate TIMESTAMP WITHOUT TIME ZONE);

ALTER TABLE ipa_pinbal_servei ADD CONSTRAINT ipa_pinbal_servei_pk PRIMARY KEY (id);

ALTER TABLE ipa_pinbal_servei ADD CONSTRAINT ipa_usucre_pinbal_servei_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

ALTER TABLE ipa_pinbal_servei ADD CONSTRAINT ipa_usumod_pinbal_servei_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (1, 'SVDDGPCIWS02', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (2, 'SVDDGPVIWS02', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (3, 'SVDCCAACPASWS01', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (4, 'SVDSCDDWS01', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (5, 'SCDCPAJU', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (6, 'SVDSCTFNWS01', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (7, 'SVDCCAACPCWS01', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (8, 'Q2827003ATGSS001', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (9, 'SVDDELSEXWS01', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (10, 'SCDHPAJU', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (11, 'NIVRENTI', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (12, 'ECOT103', false, false, false, false, false);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (13, 'SVDDGPRESIDENCIALEGALDOCWS01', false, false, false, false, false);

-- Changeset db/changelog/changes/0.9.106/1395.yaml::1702392949975-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.anotacions.annexos.save', 'false', 'Guardar annexos de les anotacions en FileSystem (instal·lació de Ripea i Distribució en servidors separats)', 'GENERAL', '5', '0', 'BOOL', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.anotacions.registre.expedient.serie.documental', NULL, 'Serie documental del contenidor dels annexos de Distribució', 'GENERAL', '6', '0', 'TEXT', '0');

-- Changeset db/changelog/changes/0.9.106/1410.yaml::1705567798214-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.carpetes.mantenir.estat', 'false', 'Mantenir estat carpetes (oberta o tancada) fins que es tanqui el navegador', 'CONTINGUT', '41', '0', 'BOOL', '1');

