-- Changeset db/changelog/changes/0.9.102/1317.yaml::1695202086434-1::limit
ALTER TABLE ipa_massiva_contingut ADD element_id NUMBER(19);

ALTER TABLE ipa_massiva_contingut ADD element_nom VARCHAR2(256 CHAR);

ALTER TABLE ipa_massiva_contingut ADD element_tipus VARCHAR2(16 CHAR);

-- Changeset db/changelog/changes/0.9.102/1332.yaml::1693899071612-1::limit
ALTER TABLE ipa_expedient ADD registres_importats VARCHAR2(4000 CHAR);

CREATE INDEX ipa_expedient_reg_importats_i ON ipa_expedient(registres_importats);

ALTER TABLE ipa_contingut ADD numero_registre VARCHAR2(80 CHAR);

