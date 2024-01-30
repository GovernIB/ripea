-- Changeset db/changelog/changes/0.9.106/1387.yaml::1703236956442-1::limit
CREATE TABLE ipa_pinbal_servei (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64 CHAR) NOT NULL, doc_permes_dni NUMBER(1) NOT NULL, doc_permes_nif NUMBER(1) NOT NULL, doc_permes_cif NUMBER(1) NOT NULL, doc_permes_nie NUMBER(1) NOT NULL, doc_permes_pas NUMBER(1) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP);

ALTER TABLE ipa_pinbal_servei ADD CONSTRAINT ipa_pinbal_servei_pk PRIMARY KEY (id);

ALTER TABLE ipa_pinbal_servei ADD CONSTRAINT ipa_usucre_pinbal_servei_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

ALTER TABLE ipa_pinbal_servei ADD CONSTRAINT ipa_usumod_pinbal_servei_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

grant select, update, insert, delete on ipa_pinbal_servei to www_ripea;
