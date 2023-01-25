-- Changeset db/changelog/changes/0.9.96/1159.yaml::1667905124779-1::limit
ALTER TABLE ipa_document ADD arxiu_estat VARCHAR(16);

ALTER TABLE ipa_document ADD doc_firma_tipus VARCHAR(16);

ALTER TABLE ipa_document ADD arxiu_uuid_firma VARCHAR(36);

-- Changeset db/changelog/changes/0.9.96/1200.yaml::1668499633661-1::limit
ALTER TABLE ipa_interessat ADD amb_oficina_sir BOOLEAN DEFAULT FALSE;

