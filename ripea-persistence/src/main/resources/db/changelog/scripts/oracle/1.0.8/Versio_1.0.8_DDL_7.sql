-- RIPEA 1.0.8
-- Amb els tipus de document generics retirats, cap procediment no pot fer servir tipus
-- de document sense procediment: l'indicador PERMET_METADOCS_GENERALS ja no te sentit.
ALTER TABLE IPA_METAEXPEDIENT DROP COLUMN PERMET_METADOCS_GENERALS;
