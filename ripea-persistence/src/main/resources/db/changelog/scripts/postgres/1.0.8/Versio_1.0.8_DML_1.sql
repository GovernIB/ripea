-- RIPEA 1.0.8
-- Alta dels tipus de document per defecte (NOTIB_JUSTIFICANT_RECEPCIO i REGISTRE_JUSTIFICANT_ENTRADA)
-- als procediments que ja existeixen. Els dos amb multiplicitat 0..N (ordinal 2 de MultiplicitatEnumDto),
-- origen administracio (O1) i estat d'elaboracio original (EE01).
-- Es idempotent: nomes crea els tipus de document que el procediment encara no te.
DO $$
DECLARE
    V_METANODE_ID   BIGINT;
    V_ORDRE         INTEGER;
    R               RECORD;
BEGIN
    FOR R IN
        SELECT me.id             AS meta_expedient_id,
               mn.entitat_id     AS entitat_id,
               mn.createdby_codi AS createdby_codi,
               d.codi            AS codi,
               d.nom             AS nom,
               d.nti_tipdoc      AS nti_tipdoc
        FROM ipa_metaexpedient me
        JOIN ipa_metanode mn ON mn.id = me.id
        CROSS JOIN (VALUES
            ('NOTIB_JUSTIFICANT_RECEPCIO', 'Justificant de recepció NOTIB', 'TD09'),
            ('REGISTRE_JUSTIFICANT_ENTRADA', 'Justificant d''entrada de registre', 'TD11')
        ) AS d(codi, nom, nti_tipdoc)
        WHERE NOT EXISTS (
            SELECT 1
            FROM ipa_metadocument md
            WHERE md.meta_expedient_id = me.id
              AND md.codi = d.codi)
        ORDER BY me.id, d.codi
    LOOP

        SELECT NEXTVAL('ipa_hibernate_seq') INTO V_METANODE_ID;

        SELECT COUNT(*) INTO V_ORDRE
        FROM ipa_metadocument
        WHERE meta_expedient_id = R.meta_expedient_id;

        INSERT INTO ipa_metanode (
            id, codi, nom, descripcio, tipus, entitat_id, actiu, version,
            createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate)
        VALUES (
            V_METANODE_ID, R.codi, R.nom, NULL, 'DOCUMENT', R.entitat_id, TRUE, 0,
            R.createdby_codi, CURRENT_TIMESTAMP, R.createdby_codi, CURRENT_TIMESTAMP);

        INSERT INTO ipa_metadocument (
            id, codi, multiplicitat, meta_expedient_id,
            nti_origen, nti_estela, nti_tipdoc,
            firma_pfirma, firma_passarela, firma_biometrica, biometrica_lectura,
            pinbal_actiu, pinbal_utilitzar_cif_organ, per_defecte,
            pinbal_servei_doc_permes_dni, pinbal_servei_doc_permes_nif, pinbal_servei_doc_permes_cif,
            pinbal_servei_doc_permes_nie, pinbal_servei_doc_permes_pas,
            ordre)
        VALUES (
            V_METANODE_ID, R.codi, 2, R.meta_expedient_id,
            'O1', 'EE01', R.nti_tipdoc,
            FALSE, FALSE, FALSE, FALSE,
            FALSE, FALSE, FALSE,
            TRUE, TRUE, TRUE,
            TRUE, TRUE,
            V_ORDRE);

    END LOOP;
END $$;
