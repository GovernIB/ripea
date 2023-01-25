-- Changeset db/changelog/changes/0.9.96/1159.yaml::1667905124779-1::limit
ALTER TABLE ipa_document ADD arxiu_estat VARCHAR2(16 CHAR);

ALTER TABLE ipa_document ADD doc_firma_tipus VARCHAR2(16 CHAR);

ALTER TABLE ipa_document ADD arxiu_uuid_firma VARCHAR2(36 CHAR);

-- Changeset db/changelog/changes/0.9.96/1200.yaml::1668499633661-1::limit
ALTER TABLE ipa_interessat ADD amb_oficina_sir NUMBER(1) DEFAULT '0';

