-- #651
ALTER TABLE IPA_EXPEDIENT MODIFY ORGAN_GESTOR_ID NOT NULL;

GRANT SELECT, UPDATE, INSERT, DELETE ON IPA_METAEXP_ORGAN TO WWW_RIPEA;
GRANT SELECT, UPDATE, INSERT, DELETE ON IPA_EXPEDIENT_ORGANPARE TO WWW_RIPEA;
GRANT SELECT, UPDATE, INSERT, DELETE ON IPA_DOMINI TO WWW_RIPEA;