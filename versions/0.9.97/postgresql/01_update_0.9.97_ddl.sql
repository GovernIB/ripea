-- Changeset db/changelog/changes/0.9.97/1156.yaml::1671191152106-1::limit
ALTER TABLE ipa_organ_gestor ADD cif VARCHAR(10);

-- Changeset db/changelog/changes/0.9.97/1157.yaml::1673273997173-1::limit
ALTER TABLE ipa_metadocument ADD pinbal_utilitzar_cif_organ BOOLEAN DEFAULT FALSE;

-- Changeset db/changelog/changes/0.9.97/1164.yaml::1673857608869-1::limit
ALTER TABLE ipa_config ADD configurable_organ BOOLEAN DEFAULT FALSE;

ALTER TABLE ipa_config ADD organ_codi VARCHAR(64);

ALTER TABLE ipa_config ADD configurable_entitat_actiu BOOLEAN DEFAULT FALSE;

ALTER TABLE ipa_config ADD configurable_organ_actiu BOOLEAN DEFAULT FALSE;


-- Changeset db/changelog/changes/0.9.97/1208.yaml::1670837833869-1::limit
ALTER TABLE ipa_metadada ADD no_aplica BOOLEAN DEFAULT FALSE;