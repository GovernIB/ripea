-- RIPEA 1.0.8
-- Index per consultar les darreres accions d'una integracio al monitor d'integracions
-- (p.ex. /salut, que calcula l'estat de cada integracio per a COMANDA amb les 10 darreres).
-- Sense aquest index, cada consulta recorre tota la taula IPA_INTEGRACIO_ACCIO;
-- amb l'index nomes llegeix les files que retorna.
-- Mentre es crea l'index, les insercions a la taula esperen: si s'executa amb l'aplicacio
-- en marxa, millor fer-ho en hores de poca activitat.

CREATE INDEX IPA_INTACC_CODI_DATA_I ON IPA_INTEGRACIO_ACCIO (INTEGRACIO_CODI, DATA);
