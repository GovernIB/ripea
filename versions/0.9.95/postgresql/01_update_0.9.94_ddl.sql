-- Changeset db/changelog/changes/0.9.95/1063.yaml::1653978777186-1::limit
ALTER TABLE ipa_metaexpedient ADD organ_no_sinc BOOLEAN;

ALTER TABLE ipa_entitat ADD data_sincronitzacio TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE ipa_entitat ADD data_actualitzacio TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE ipa_organ_gestor ADD estat VARCHAR(1) DEFAULT 'V';

ALTER TABLE ipa_organ_gestor ADD tipus_transicio VARCHAR(12);

ALTER TABLE ipa_avis ADD avis_admin BOOLEAN DEFAULT FALSE;

ALTER TABLE ipa_avis ADD entitat_id BIGINT;

CREATE TABLE ipa_og_sinc_rel (antic_og BIGINT NOT NULL, nou_og BIGINT NOT NULL);

ALTER TABLE ipa_og_sinc_rel ADD CONSTRAINT ipa_organ_antic_fk FOREIGN KEY (antic_og) REFERENCES ipa_organ_gestor (id);

ALTER TABLE ipa_og_sinc_rel ADD CONSTRAINT ipa_organ_nou_fk FOREIGN KEY (nou_og) REFERENCES ipa_organ_gestor (id);

ALTER TABLE ipa_og_sinc_rel ADD CONSTRAINT ipa_uo_sinc_rel_mult_uk UNIQUE (antic_og, nou_og);

-- Changeset db/changelog/changes/0.9.95/1067.yaml::1659525430753-1::limit
ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_dni BOOLEAN DEFAULT TRUE NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_nif BOOLEAN DEFAULT TRUE NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_cif BOOLEAN DEFAULT TRUE NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_nie BOOLEAN DEFAULT TRUE NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_pas BOOLEAN DEFAULT TRUE NOT NULL;

