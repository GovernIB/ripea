-- #732
-- *** Executar només si NO s'activa la propietat es.caib.ripea.carpetes.defecte per evitar possibles errors a l'hora de crear expedients ***

-- Esborra les carpetes que hi ha creades per defecte a nivell de tipus d'expedient

DELETE FROM IPA_METAEXPEDIENT_CARPETA;