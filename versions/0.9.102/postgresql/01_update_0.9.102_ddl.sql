-- Changeset db/changelog/changes/0.9.102/1317.yaml::1695202086434-1::limit
ALTER TABLE ipa_massiva_contingut ADD element_id numeric(19);

ALTER TABLE ipa_massiva_contingut ADD element_nom VARCHAR(256);

ALTER TABLE ipa_massiva_contingut ADD element_tipus VARCHAR(16);

-- Changeset db/changelog/changes/0.9.102/1332.yaml::1693899071612-1::limit
ALTER TABLE ipa_expedient ADD registres_importats VARCHAR(4000);

CREATE INDEX ipa_expedient_reg_importats_i ON ipa_expedient(registres_importats);

ALTER TABLE ipa_contingut ADD numero_registre VARCHAR(80);
