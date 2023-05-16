-- Changeset db/changelog/changes/0.9.99/1225.yaml::1680014851572-1::limit
ALTER TABLE ipa_document_enviament ADD not_data_enviada TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE ipa_document_enviament ADD not_data_finalitzada TIMESTAMP WITHOUT TIME ZONE;

-- Changeset db/changelog/changes/0.9.99/1228.yaml::1680693564552-1::limit
ALTER TABLE ipa_usuari ADD avisos_noves_anotacions BOOLEAN;

-- Changeset db/changelog/changes/0.9.99/1243.yaml::1679321539857-1::limit
ALTER TABLE ipa_metadada ADD enviable BOOLEAN DEFAULT FALSE;

ALTER TABLE ipa_metadada ADD metadada_arxiu VARCHAR(255);

-- Changeset db/changelog/changes/0.9.99/1244.yaml::1680168055519-1::limit
ALTER TABLE ipa_organ_gestor ADD nom_es VARCHAR(1000);

-- Changeset db/changelog/changes/0.9.99/1248.yaml::1680009173416-1::limit
CREATE TABLE IPA_URL_INSTRUCCION (ID numeric(19) NOT NULL, NOM VARCHAR(80) NOT NULL, CODI VARCHAR(80) NOT NULL, DESCRIPCIO VARCHAR(256), URL VARCHAR(256) NOT NULL, entitat_id numeric(19) NOT NULL, CREATEDBY_CODI VARCHAR(64), CREATEDDATE TIMESTAMP WITHOUT TIME ZONE, LASTMODIFIEDBY_CODI VARCHAR(64), LASTMODIFIEDDATE TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT IPA_URL_INSTRUCCION_PK PRIMARY KEY (ID));

ALTER TABLE IPA_URL_INSTRUCCION ADD CONSTRAINT "IPA_URL_INSTRUCCION_ENT_fk" FOREIGN KEY (entitat_id) REFERENCES IPA_ENTITAT (id);

-- Changeset db/changelog/changes/0.9.99/930.yaml::1680608723206-1::limit
ALTER TABLE ipa_usuari ADD email_alternatiu VARCHAR(200);

