-- Changeset db/changelog/changes/0.9.95/1063.yaml::1653978777186-1::limit
ALTER TABLE ipa_metaexpedient ADD organ_no_sinc NUMBER(1);

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

grant select, update, insert, delete on ipa_og_sinc_rel to www_ripea;

-- Changeset db/changelog/changes/0.9.95/1067.yaml::1659525430753-1::limit
ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_dni NUMBER(1) DEFAULT 1 NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_nif NUMBER(1) DEFAULT 1 NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_cif NUMBER(1) DEFAULT 1 NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_nie NUMBER(1) DEFAULT 1 NOT NULL;

ALTER TABLE ipa_metadocument ADD pinbal_servei_doc_permes_pas NUMBER(1) DEFAULT 1 NOT NULL;

