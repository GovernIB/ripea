ALTER TABLE IPA_METADOCUMENT RENAME COLUMN PORTAFIRMES_FLUXTIP TO PORTAFIRMES_SEQTIP;
ALTER TABLE IPA_METADOCUMENT ADD PORTAFIRMES_FLUXTIP CHARACTER VARYING(256);

ALTER TABLE IPA_DOCUMENT_ENVIAMENT RENAME COLUMN PF_FLUX_TIPUS TO PF_SEQ_TIPUS;
COMMIT;
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD PF_FLUX_TIPUS CHARACTER VARYING(256);