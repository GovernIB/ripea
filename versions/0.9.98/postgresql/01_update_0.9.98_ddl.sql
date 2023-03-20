-- Changeset db/changelog/changes/0.9.98/1186.yaml::1677771425170-1::limit
ALTER TABLE ipa_document ADD expedient_estat_id NUMBER(19);

ALTER TABLE ipa_document ADD CONSTRAINT ipa_expestat_document_fk FOREIGN KEY (expedient_estat_id) REFERENCES ipa_expedient_estat (id);

ALTER TABLE ipa_usuari ADD vista_actual VARCHAR2(64 CHAR);

ALTER TABLE ipa_metadocument ADD ordre INTEGER DEFAULT 0 NOT NULL;