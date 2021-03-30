-- #711
-- Executar en cas de tenir metadades de tipus domini creades
UPDATE IPA_METADADA SET MULTIPLICITAT = 1 WHERE TIPUS = 6;

-- #732
-- Esborra les carpetes que hi ha creades per defecte a nivell de tipus d'expedient
DELETE FROM IPA_METAEXPEDIENT_CARPETA;