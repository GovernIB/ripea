-- RIPEA 1.0.9
-- IPA_CONT_LOG: les columnes amb el nom dels tipus substitueixen les numeriques (ordinal).
-- Si TIPUS_NOM te algun null (ordinal que no existeix a cap versio de l enum), falla aqui abans d esborrar res.
-- Si s executa a ma, aturar-se al primer error: el mode script de SQL Developer continua i esborraria igualment
-- les columnes numeriques amb els tipus sense traduir.
ALTER TABLE IPA_CONT_LOG MODIFY TIPUS_NOM NOT NULL;
ALTER TABLE IPA_CONT_LOG DROP (TIPUS, OBJECTE_TIPUS, OBJECTE_LOG_TIPUS);
ALTER TABLE IPA_CONT_LOG RENAME COLUMN TIPUS_NOM TO TIPUS;
ALTER TABLE IPA_CONT_LOG RENAME COLUMN OBJECTE_TIPUS_NOM TO OBJECTE_TIPUS;
ALTER TABLE IPA_CONT_LOG RENAME COLUMN OBJECTE_LOG_TIPUS_NOM TO OBJECTE_LOG_TIPUS;
