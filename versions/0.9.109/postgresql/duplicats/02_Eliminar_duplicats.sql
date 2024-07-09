-- Eliminar representants que tenen el mateix número de document que el seu interessat (interessat == representant)
DO
$$
DECLARE
v_docNum VARCHAR(17);
    r_duplicat RECORD;
BEGIN
FOR r_duplicat IN (SELECT i.ID, i.DTYPE, i.DOCUMENT_NUM, i.ES_REPRESENTANT, i.REPRESENTANT_ID, i.EXPEDIENT_ID
                          FROM IPA_INTERESSAT i
                          JOIN (SELECT DOCUMENT_NUM, EXPEDIENT_ID
                                FROM IPA_INTERESSAT
                                GROUP BY DOCUMENT_NUM, EXPEDIENT_ID
                                HAVING COUNT(*) > 1) d
                            ON i.DOCUMENT_NUM = d.DOCUMENT_NUM AND i.EXPEDIENT_ID = d.EXPEDIENT_ID
                          WHERE i.ES_REPRESENTANT = 0 AND i.REPRESENTANT_ID IS NOT NULL)
    LOOP
            RAISE NOTICE 'id: %, docNum: %, expId: %, repId:  %', r_duplicat.ID, r_duplicat.DOCUMENT_NUM, r_duplicat.EXPEDIENT_ID, r_duplicat.REPRESENTANT_ID;
SELECT DOCUMENT_NUM INTO v_docNum FROM IPA_INTERESSAT WHERE ID = r_duplicat.REPRESENTANT_ID;
RAISE NOTICE 'Representant docNum: %', v_docNum;
            IF v_docNum = r_duplicat.DOCUMENT_NUM THEN
                RAISE NOTICE 'Interessat i Representant amb el mateix docNum!!';
UPDATE IPA_INTERESSAT SET REPRESENTANT_ID = null WHERE ID = r_duplicat.ID;
DELETE FROM IPA_INTERESSAT WHERE ID = r_duplicat.REPRESENTANT_ID;
RAISE NOTICE 'Eliminat representant: % amb docNum: %', r_duplicat.REPRESENTANT_ID, v_docNum;
END IF;
END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Unificar representants duplicats en el mateix expedient
DO
$$
DECLARE
r_duplicat RECORD;
    r_dup_exp RECORD;
BEGIN
FOR r_duplicat IN (
        SELECT *
        FROM (
            SELECT i.ID, i.DOCUMENT_NUM, i.EXPEDIENT_ID, i.ES_REPRESENTANT,
                row_number() OVER (PARTITION BY i.DOCUMENT_NUM, i.EXPEDIENT_ID ORDER BY i.ES_REPRESENTANT ASC, i.ID DESC) as row_number
            FROM IPA_INTERESSAT i
            JOIN (SELECT DOCUMENT_NUM, EXPEDIENT_ID
                  FROM IPA_INTERESSAT
                  GROUP BY DOCUMENT_NUM, EXPEDIENT_ID
                  HAVING COUNT(*) > 1) d
                  ON i.DOCUMENT_NUM = d.DOCUMENT_NUM AND i.EXPEDIENT_ID = d.EXPEDIENT_ID
        ) sub_query
        WHERE row_number = 1)
    LOOP
        RAISE NOTICE 'id: %, docNum: %, expId: %', r_duplicat.ID, r_duplicat.DOCUMENT_NUM, r_duplicat.EXPEDIENT_ID;

FOR r_dup_exp IN (SELECT i.ID, i.ES_REPRESENTANT, i.REPRESENTANT_ID
                            FROM IPA_INTERESSAT i
                            WHERE i.DOCUMENT_NUM = r_duplicat.DOCUMENT_NUM
                              AND i.EXPEDIENT_ID = r_duplicat.EXPEDIENT_ID
                            ORDER BY i.ES_REPRESENTANT ASC, i.ID DESC)
        LOOP
            IF r_duplicat.ID = r_dup_exp.ID THEN
                CONTINUE;
END IF;
UPDATE IPA_INTERESSAT SET REPRESENTANT_ID = r_duplicat.ID WHERE REPRESENTANT_ID = r_dup_exp.ID;
DELETE FROM IPA_INTERESSAT WHERE ID = r_dup_exp.ID;
END LOOP;
END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Eliminar representants orfes (no pertanyen a cap interessat)
DELETE FROM IPA_INTERESSAT WHERE ID IN (
    SELECT i.ID
    FROM IPA_INTERESSAT i
             JOIN (SELECT DOCUMENT_NUM, EXPEDIENT_ID
                   FROM IPA_INTERESSAT
                   GROUP BY DOCUMENT_NUM, EXPEDIENT_ID
                   HAVING COUNT(*) > 1) d
                  ON i.DOCUMENT_NUM = d.DOCUMENT_NUM AND i.EXPEDIENT_ID = d.EXPEDIENT_ID
    WHERE i.ES_REPRESENTANT = 1
      AND i.ID NOT IN (SELECT REPRESENTANT_ID FROM IPA_INTERESSAT WHERE REPRESENTANT_ID IS NOT NULL)
);