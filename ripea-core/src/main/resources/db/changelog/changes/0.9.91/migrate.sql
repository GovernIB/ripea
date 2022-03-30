-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 07/03/22 12:41
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.91/934.yaml::1644841333349-1::limit
CREATE TABLE ipa_exp_tasca_comment (id NUMBER(38, 0) NOT NULL, exp_tasca_id NUMBER(38, 0) NOT NULL, text VARCHAR2(1024 CHAR), createdby_codi VARCHAR2(64 CHAR), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64 CHAR), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_exp_tasca_comment_pk PRIMARY KEY (id));

ALTER TABLE ipa_exp_tasca_comment ADD CONSTRAINT ipa_exptasca_tascacomment_fk FOREIGN KEY (exp_tasca_id) REFERENCES ipa_expedient_tasca (id);

ALTER TABLE ipa_exp_tasca_comment ADD CONSTRAINT ipa_usucre_tascacomment_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

ALTER TABLE ipa_exp_tasca_comment ADD CONSTRAINT ipa_usumod_tascacomment_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

grant select, update, insert, delete on ipa_exp_tasca_comment to www_ripea;

insert into ipa_exp_tasca_comment (id, exp_tasca_id, text, createdby_codi, createddate) select ipa_hibernate_seq.nextval, id, comentari, createdby_codi, createddate from ipa_expedient_tasca where comentari is not null;

