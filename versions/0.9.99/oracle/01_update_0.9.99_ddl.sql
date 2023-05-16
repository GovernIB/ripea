-- Changeset db/changelog/changes/0.9.99/1225.yaml::1680014851572-1::limit
ALTER TABLE ipa_document_enviament ADD not_data_enviada TIMESTAMP;

ALTER TABLE ipa_document_enviament ADD not_data_finalitzada TIMESTAMP;

-- Changeset db/changelog/changes/0.9.99/1228.yaml::1680693564552-1::limit
ALTER TABLE ipa_usuari ADD avisos_noves_anotacions NUMBER(1);

-- Changeset db/changelog/changes/0.9.99/1243.yaml::1679321539857-1::limit
ALTER TABLE ipa_metadada ADD enviable NUMBER(1) DEFAULT '0';

ALTER TABLE ipa_metadada ADD metadada_arxiu VARCHAR2(255 CHAR);

-- Changeset db/changelog/changes/0.9.99/1244.yaml::1680168055519-1::limit
ALTER TABLE ipa_organ_gestor ADD nom_es VARCHAR2(1000 CHAR);

-- Changeset db/changelog/changes/0.9.99/1248.yaml::1680009173416-1::limit
CREATE TABLE IPA_URL_INSTRUCCION (ID NUMBER(19) NOT NULL, NOM VARCHAR2(80 CHAR) NOT NULL, CODI VARCHAR2(80 CHAR) NOT NULL, DESCRIPCIO VARCHAR2(256 CHAR), URL VARCHAR2(256 CHAR) NOT NULL, entitat_id NUMBER(19) NOT NULL, CREATEDBY_CODI VARCHAR2(64 CHAR), CREATEDDATE TIMESTAMP, LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), LASTMODIFIEDDATE TIMESTAMP, CONSTRAINT IPA_URL_INSTRUCCION_PK PRIMARY KEY (ID));

ALTER TABLE IPA_URL_INSTRUCCION ADD CONSTRAINT IPA_URL_INSTRUCCION_ENT_fk FOREIGN KEY (entitat_id) REFERENCES IPA_ENTITAT (id);

GRANT SELECT, UPDATE, INSERT, DELETE ON IPA_URL_INSTRUCCION TO WWW_RIPEA;

-- Changeset db/changelog/changes/0.9.99/930.yaml::1680608723206-1::limit
ALTER TABLE ipa_usuari ADD email_alternatiu VARCHAR2(200 CHAR);

