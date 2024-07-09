-- Duplicats. Guardar les dades de tots els duplicats, per si hi ha alguna pèrdua de informació
SELECT *
FROM IPA_INTERESSAT i
    JOIN (SELECT DOCUMENT_NUM, EXPEDIENT_ID FROM IPA_INTERESSAT GROUP BY DOCUMENT_NUM, EXPEDIENT_ID HAVING COUNT(*) > 1) d
      ON i.DOCUMENT_NUM = d.DOCUMENT_NUM AND i.EXPEDIENT_ID = d.EXPEDIENT_ID;