-- Changeset db/changelog/changes/0.9.109/1486.yaml::1716384985679-2::limit
ALTER TABLE ipa_expedient_tasca ADD titol VARCHAR(256);
ALTER TABLE ipa_expedient_tasca ADD observacions VARCHAR(1024);

-- Changeset db/changelog/changes/0.9.109/1497.yaml::1718262082962-1::limit
ALTER TABLE IPA_INTERESSAT ADD CONSTRAINT IPA_INTERESSAT_EXP_UK UNIQUE (DOCUMENT_NUM, EXPEDIENT_ID);
