-- Changeset db/changelog/changes/0.9.101/1304.yaml::1689325988450-1::limit
ALTER TABLE ipa_expedient ADD tancat_programat date;

CREATE INDEX ipa_expedient_tancat_prog_i ON ipa_expedient(tancat_programat);

-- Changeset db/changelog/changes/0.9.101/1311.yaml::1692879211491-1::limit
ALTER TABLE ipa_organ_gestor ADD utilitzar_cif_pinbal NUMBER(1) DEFAULT 0;

-- Changeset db/changelog/changes/0.9.101/1316.yaml::1691495012174-1::limit
ALTER TABLE ipa_document_enviament ADD pf_avis_firma_parcial NUMBER(1);

ALTER TABLE ipa_execucio_massiva ADD pfirmes_avis_firma_parcial NUMBER(1);

