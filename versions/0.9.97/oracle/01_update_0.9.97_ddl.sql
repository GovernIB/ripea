-- Changeset db/changelog/changes/0.9.97/1156.yaml::1671191152106-1::limit
ALTER TABLE ipa_organ_gestor ADD cif VARCHAR2(10 CHAR);

-- Changeset db/changelog/changes/0.9.97/1157.yaml::1673273997173-1::limit
ALTER TABLE ipa_metadocument ADD pinbal_utilitzar_cif_organ NUMBER(1) DEFAULT 0;

-- Changeset db/changelog/changes/0.9.97/1164.yaml::1673857608869-1::limit
ALTER TABLE ipa_config ADD configurable_organ NUMBER(1) DEFAULT 0;

ALTER TABLE ipa_config ADD organ_codi VARCHAR2(64 CHAR);

ALTER TABLE ipa_config ADD configurable_entitat_actiu NUMBER(1) DEFAULT 0;

ALTER TABLE ipa_config ADD configurable_organ_actiu NUMBER(1) DEFAULT 0;


-- Changeset db/changelog/changes/0.9.97/1208.yaml::1670837833869-1::limit
ALTER TABLE ipa_metadada ADD no_aplica NUMBER(1) DEFAULT '0';

