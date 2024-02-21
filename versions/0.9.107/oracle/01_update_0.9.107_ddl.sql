-- Changeset db/changelog/changes/0.9.107/1399.yaml::1708339656292-1::limit
ALTER TABLE ipa_usuari ADD expedient_expandit NUMBER(1) DEFAULT 1;

-- Changeset db/changelog/changes/0.9.107/1400.yaml::1708071266432-1::limit
ALTER TABLE ipa_usuari ADD metaexpedient_id NUMBER(19);

ALTER TABLE ipa_usuari ADD CONSTRAINT ipa_metaexp_usuari_fk FOREIGN KEY (metaexpedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/changes/0.9.107/1402.yaml::1707212671824-1::limit
ALTER TABLE ipa_usuari ADD exp_list_data_darrer_env NUMBER(1) DEFAULT 0;

ALTER TABLE ipa_usuari ADD exp_list_agafat_per NUMBER(1) DEFAULT 1;

ALTER TABLE ipa_usuari ADD exp_list_interessats NUMBER(1) DEFAULT 1;

ALTER TABLE ipa_usuari ADD exp_list_comentaris NUMBER(1) DEFAULT 1;

ALTER TABLE ipa_usuari ADD exp_list_grup NUMBER(1) DEFAULT 0;

-- Changeset db/changelog/changes/0.9.107/1413.yaml::1706870816380-1::limit
ALTER TABLE ipa_expedient_peticio ADD data_actualitzacio TIMESTAMP;

ALTER TABLE ipa_expedient_peticio ADD usuari_actualitzacio VARCHAR2(64);

ALTER TABLE ipa_expedient_peticio ADD CONSTRAINT ipa_usuari_actual_exp_pet_fk FOREIGN KEY (usuari_actualitzacio) REFERENCES ipa_usuari (codi);

ALTER TABLE ipa_expedient_peticio ADD observacions VARCHAR2(4000 CHAR);


-- Changeset db/changelog/changes/0.9.107/1417.yaml::1707304073271-1::limit
ALTER TABLE ipa_grup ADD organ_id NUMBER(19);

ALTER TABLE ipa_grup ADD CONSTRAINT ipa_organ_grup_fk FOREIGN KEY (organ_id) REFERENCES ipa_organ_gestor (id);

ALTER TABLE ipa_metaexpedient ADD grup_per_defecte NUMBER(19) NOT NULL;

ALTER TABLE ipa_metaexpedient ADD CONSTRAINT ipa_grup_metaexp_fk FOREIGN KEY (grup_per_defecte) REFERENCES ipa_grup (id);

ALTER TABLE ipa_expedient_peticio ADD grup_id NUMBER(19);

ALTER TABLE ipa_expedient_peticio ADD CONSTRAINT ipa_grup_exp_pet_fk FOREIGN KEY (grup_id) REFERENCES ipa_grup (id);
