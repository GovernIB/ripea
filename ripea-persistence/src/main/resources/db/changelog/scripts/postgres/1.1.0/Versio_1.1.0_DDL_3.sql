-- RIPEA 1.1.0
-- Pantalla d'inici del superusuari: llista "Darreres integracions registrades" (les 10 accions mes
-- recents de totes les integracions) i targeta d'errors de les darreres 24 hores. L'index 1.0.8_DDL_11
-- (INTEGRACIO_CODI, DATA) nomes serveix per a una integracio concreta: sense aquest, les dues consultes
-- recorren tota la taula IPA_INTEGRACIO_ACCIO. Mentre es crea l'index, les insercions a la taula esperen:
-- si s'executa amb l'aplicacio en marxa, millor fer-ho en hores de poca activitat.

CREATE INDEX IPA_INTACC_DATA_I ON IPA_INTEGRACIO_ACCIO (DATA);
