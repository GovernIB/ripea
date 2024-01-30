-- Changeset db/changelog/changes/0.9.106/1387.yaml::1703236956442-1::limit
CREATE TABLE ipa_pinbal_servei (id BIGINT NOT NULL, codi VARCHAR(64) NOT NULL, doc_permes_dni BOOLEAN NOT NULL, doc_permes_nif BOOLEAN NOT NULL, doc_permes_cif BOOLEAN NOT NULL, doc_permes_nie BOOLEAN NOT NULL, doc_permes_pas BOOLEAN NOT NULL, createdby_codi VARCHAR(64), createddate TIMESTAMP WITHOUT TIME ZONE, lastmodifiedby_codi VARCHAR(64), lastmodifieddate TIMESTAMP WITHOUT TIME ZONE);

ALTER TABLE ipa_pinbal_servei ADD CONSTRAINT ipa_pinbal_servei_pk PRIMARY KEY (id);

ALTER TABLE ipa_pinbal_servei ADD CONSTRAINT ipa_usucre_pinbal_servei_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

ALTER TABLE ipa_pinbal_servei ADD CONSTRAINT ipa_usumod_pinbal_servei_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);
