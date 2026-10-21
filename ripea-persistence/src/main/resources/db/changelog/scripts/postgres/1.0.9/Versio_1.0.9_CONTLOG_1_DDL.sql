-- RIPEA 1.0.9
-- IPA_CONT_LOG: columnes temporals amb el nom dels tipus (enum desat per nom en lloc de per ordinal).
-- Es reomplen a Versio_1.0.9_CONTLOG_2_DML i substitueixen les numeriques a Versio_1.0.9_CONTLOG_3_DDL.
ALTER TABLE IPA_CONT_LOG ADD COLUMN TIPUS_NOM VARCHAR(64);
ALTER TABLE IPA_CONT_LOG ADD COLUMN OBJECTE_TIPUS_NOM VARCHAR(64);
ALTER TABLE IPA_CONT_LOG ADD COLUMN OBJECTE_LOG_TIPUS_NOM VARCHAR(64);
