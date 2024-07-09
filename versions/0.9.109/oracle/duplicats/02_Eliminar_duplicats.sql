-- Eliminar representants que tenen el mateix número de document que el seu interessat (interessat == representant)
DECLARE
    v_docNum VARCHAR2(17 CHAR);
    CURSOR v_duplicats IS SELECT i.ID, i.DTYPE, i.DOCUMENT_NUM, i.ES_REPRESENTANT, i.REPRESENTANT_ID, i.EXPEDIENT_ID
                          FROM IPA_INTERESSAT i,
                               (SELECT DOCUMENT_NUM, EXPEDIENT_ID FROM IPA_INTERESSAT GROUP BY DOCUMENT_NUM, EXPEDIENT_ID HAVING COUNT(*) > 1) d
                          WHERE i.DOCUMENT_NUM = d.DOCUMENT_NUM
                            AND i.EXPEDIENT_ID = d.EXPEDIENT_ID
                            AND i.ES_REPRESENTANT = 0
                            AND i.REPRESENTANT_ID IS NOT NULL;
BEGIN
    FOR r_duplicat IN v_duplicats LOOP
            dbms_output.put_line('id: ' || TO_CHAR(r_duplicat.ID) || 'docNum: ' || r_duplicat.DOCUMENT_NUM || ', expId: ' || TO_CHAR(r_duplicat.EXPEDIENT_ID) || ', repId:  ' || TO_CHAR(r_duplicat.REPRESENTANT_ID));
            SELECT DOCUMENT_NUM INTO v_docNum FROM IPA_INTERESSAT WHERE ID = r_duplicat.REPRESENTANT_ID;
            dbms_output.put_line('Representant docNum: ' || v_docNum);
            IF v_docNum = r_duplicat.DOCUMENT_NUM THEN
                dbms_output.put_line('Interessat i Representant amb el mateix docNum!!');
                UPDATE IPA_INTERESSAT SET REPRESENTANT_ID = null WHERE ID = r_duplicat.ID;
                DELETE IPA_INTERESSAT WHERE ID = r_duplicat.REPRESENTANT_ID;
                dbms_output.put_line('Eliminat representant: ' || TO_CHAR(r_duplicat.REPRESENTANT_ID) || ' amb docNum' || v_docNum);
            END IF;
        END LOOP;
END;
/

-- Unificar representants duplicats en el mateix expedient
DECLARE
    CURSOR v_duplicats IS SELECT *
                          FROM (
                                   SELECT i.ID, i.DOCUMENT_NUM, i.EXPEDIENT_ID, i.ES_REPRESENTANT,
                                          ROW_NUMBER() OVER (PARTITION BY i.DOCUMENT_NUM, i.EXPEDIENT_ID ORDER BY i.ES_REPRESENTANT ASC, i.ID DESC) as row_number
                                   FROM IPA_INTERESSAT i,
                                        (SELECT DOCUMENT_NUM, EXPEDIENT_ID FROM IPA_INTERESSAT GROUP BY DOCUMENT_NUM, EXPEDIENT_ID HAVING COUNT(*) > 1) d
                                   WHERE i.DOCUMENT_NUM = d.DOCUMENT_NUM
                                     AND i.EXPEDIENT_ID = d.EXPEDIENT_ID
                                   ORDER BY i.ES_REPRESENTANT ASC, i.ID DESC)
                          WHERE row_number = 1;
    CURSOR v_dup_exp (p_docNum IPA_INTERESSAT.DOCUMENT_NUM%TYPE, p_expId IPA_INTERESSAT.EXPEDIENT_ID%TYPE) IS
        SELECT i.ID, i.ES_REPRESENTANT, i.REPRESENTANT_ID
        FROM IPA_INTERESSAT i
        WHERE i.DOCUMENT_NUM = p_docNum
          AND i.EXPEDIENT_ID = p_expId
        ORDER BY i.ES_REPRESENTANT ASC, i.ID DESC;
BEGIN
    FOR r_duplicat IN v_duplicats LOOP
            dbms_output.put_line('id: ' || TO_CHAR(r_duplicat.ID) || 'docNum: ' || r_duplicat.DOCUMENT_NUM || ', expId: ' || TO_CHAR(r_duplicat.EXPEDIENT_ID));
            dbms_output.put_line('Duplicats de ' || r_duplicat.DOCUMENT_NUM || ' a unificar amb id: ' || TO_CHAR(r_duplicat.ID));
            FOR r_dup_exp IN v_dup_exp(r_duplicat.DOCUMENT_NUM, r_duplicat.EXPEDIENT_ID) LOOP
                    IF r_duplicat.ID = r_dup_exp.ID THEN
                        CONTINUE;
                    END IF;
                    dbms_output.put_line('Duplicat amb id: ' || TO_CHAR(r_dup_exp.ID));
                    UPDATE IPA_INTERESSAT SET REPRESENTANT_ID = r_duplicat.ID WHERE REPRESENTANT_ID = r_dup_exp.ID;
                    DELETE IPA_INTERESSAT WHERE ID = r_dup_exp.ID;
                    dbms_output.put_line('Eliminat duplicat amb id: ' || TO_CHAR(r_dup_exp.ID));
                END LOOP;
        END LOOP;
END;
/

-- Eliminar representants orfes (no pertanyen a cap interessat)
DELETE IPA_INTERESSAT WHERE ID IN (
    SELECT i.ID
    FROM IPA_INTERESSAT i,
         (SELECT DOCUMENT_NUM, EXPEDIENT_ID FROM IPA_INTERESSAT GROUP BY DOCUMENT_NUM, EXPEDIENT_ID HAVING COUNT(*) > 1) d
    WHERE i.DOCUMENT_NUM = d.DOCUMENT_NUM
      AND i.EXPEDIENT_ID = d.EXPEDIENT_ID
      AND i.ES_REPRESENTANT = 1
      AND i.ID NOT IN (SELECT REPRESENTANT_ID FROM IPA_INTERESSAT WHERE REPRESENTANT_ID IS NOT NULL)
    );
