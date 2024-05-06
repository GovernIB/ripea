-- Changeset db/changelog/changes/0.9.108/1437.yaml::1712301590732-1::limit
ALTER TABLE IPA_CONFIG ADD configurable_org_descendents BOOLEAN DEFAULT FALSE;

-- Changeset db/changelog/changes/0.9.108/1446.yaml::1712301590732-2::limit
ALTER TABLE ipa_usuari ADD entitat_defecte_id numeric(19);
ALTER TABLE ipa_usuari ADD CONSTRAINT ipa_entitat_usuari_fk FOREIGN KEY (entitat_defecte_id) REFERENCES ipa_entitat (id);