-- Changeset db/changelog/changes/0.9.102/1317.yaml::1695202086434-1::limit
ALTER TABLE ipa_massiva_contingut ADD element_id NUMBER(19);

ALTER TABLE ipa_massiva_contingut ADD element_nom VARCHAR2(256 CHAR);

ALTER TABLE ipa_massiva_contingut ADD element_tipus VARCHAR2(16 CHAR);

update ipa_massiva_contingut set element_id = contingut_id;

update ipa_massiva_contingut set element_tipus = 'DOCUMENT';

update ipa_massiva_contingut set element_nom = (select nom from ipa_contingut where ipa_contingut.id = ipa_massiva_contingut.contingut_id);

-- Changeset db/changelog/changes/0.9.102/1332.yaml::1693899071612-1::limit
ALTER TABLE ipa_expedient ADD registres_importats VARCHAR2(1024 CHAR);

CREATE INDEX ipa_expedient_reg_importats_i ON ipa_expedient(registres_importats);

ALTER TABLE ipa_contingut ADD numero_registre VARCHAR2(80 CHAR);

-- Changeset db/changelog/changes/0.9.102/954.yaml::1694769935247-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.entorn', NULL, 'Entorn on es troba l''aplicació', 'GENERAL', '4', '0', 'TEXT', '0');

