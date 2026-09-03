-- RIPEA 1.0.8
-- Ultim tipus de document per defecte: OTROS. Amb ell, els quatre tipus generics que hi havia
-- (sense procediment) queden coberts per tipus de cada procediment i no cal cap metadocument
-- sense procediment.
-- Als procediments que ja existeixen es crea sense marcar-lo com a tipus per defecte del
-- procediment (els nous si que el marquen, ho fa MetaDocumentHelper en crear el procediment),
-- per no canviar el tipus per defecte que cada procediment pugui tenir ja triat.
DECLARE
    V_METANODE_ID   NUMBER;
    V_ORDRE         NUMBER;
    V_CODI          VARCHAR2(64) := 'OTROS';
    V_NOM           VARCHAR2(255) := 'Otros';
    V_DESCRIPCIO    VARCHAR2(1000) := 'Altres documents del procediment';
BEGIN
    FOR R IN (
        SELECT me.id             AS meta_expedient_id,
               mn.entitat_id     AS entitat_id,
               mn.createdby_codi AS createdby_codi
        FROM ipa_metaexpedient me
        JOIN ipa_metanode mn ON mn.id = me.id
        WHERE NOT EXISTS (
            SELECT 1
            FROM ipa_metadocument md
            WHERE md.meta_expedient_id = me.id
              AND md.codi = V_CODI)
        ORDER BY me.id
    ) LOOP

        SELECT ipa_hibernate_seq.NEXTVAL INTO V_METANODE_ID FROM dual;

        SELECT COUNT(*) INTO V_ORDRE
        FROM ipa_metadocument
        WHERE meta_expedient_id = R.meta_expedient_id;

        INSERT INTO ipa_metanode (
            id, codi, nom, descripcio, tipus, entitat_id, actiu, version,
            createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate)
        VALUES (
            V_METANODE_ID, V_CODI, V_NOM, V_DESCRIPCIO, 'DOCUMENT', R.entitat_id, 1, 0,
            R.createdby_codi, SYSTIMESTAMP, R.createdby_codi, SYSTIMESTAMP);

        INSERT INTO ipa_metadocument (
            id, codi, multiplicitat, meta_expedient_id,
            nti_origen, nti_estela, nti_tipdoc,
            firma_pfirma, firma_passarela, firma_biometrica, biometrica_lectura,
            pinbal_actiu, pinbal_utilitzar_cif_organ, per_defecte,
            pinbal_servei_doc_permes_dni, pinbal_servei_doc_permes_nif, pinbal_servei_doc_permes_cif,
            pinbal_servei_doc_permes_nie, pinbal_servei_doc_permes_pas,
            ordre)
        VALUES (
            V_METANODE_ID, V_CODI, 2, R.meta_expedient_id,
            'O1', 'EE01', 'TD99',
            0, 0, 0, 0,
            0, 0, 0,
            1, 1, 1,
            1, 1,
            V_ORDRE);

    END LOOP;
END;
