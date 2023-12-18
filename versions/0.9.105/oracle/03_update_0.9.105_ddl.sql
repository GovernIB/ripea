-- Changeset db/changelog/changes/0.9.105/1036.yaml::1701433927769-1::limit
ALTER TABLE ipa_grup ADD CONSTRAINT ipa_grup_codi_uk UNIQUE (entitat_id, codi);