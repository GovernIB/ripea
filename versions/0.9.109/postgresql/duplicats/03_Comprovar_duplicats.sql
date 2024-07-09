-- Duplicats
SELECT i.ID, i.DTYPE, i.DOCUMENT_NUM, i.ES_REPRESENTANT, i.REPRESENTANT_ID, i.EXPEDIENT_ID
FROM IPA_INTERESSAT i
         JOIN (SELECT DOCUMENT_NUM, EXPEDIENT_ID
               FROM IPA_INTERESSAT
               GROUP BY DOCUMENT_NUM, EXPEDIENT_ID
               HAVING COUNT(*) > 1) d
              ON i.DOCUMENT_NUM = d.DOCUMENT_NUM
                  AND i.EXPEDIENT_ID = d.EXPEDIENT_ID;

-- Interessats amb representants duplicats
SELECT ii.ID, DTYPE, DOCUMENT_NUM, ES_REPRESENTANT, REPRESENTANT_ID, EXPEDIENT_ID
FROM IPA_INTERESSAT ii
         JOIN (SELECT i.ID
               FROM IPA_INTERESSAT i
                        JOIN (SELECT DOCUMENT_NUM, EXPEDIENT_ID
                              FROM IPA_INTERESSAT
                              GROUP BY DOCUMENT_NUM, EXPEDIENT_ID
                              HAVING COUNT(*) > 1) d
                             ON i.DOCUMENT_NUM = d.DOCUMENT_NUM
                                 AND i.EXPEDIENT_ID = d.EXPEDIENT_ID) dup
              ON ii.REPRESENTANT_ID = dup.ID;