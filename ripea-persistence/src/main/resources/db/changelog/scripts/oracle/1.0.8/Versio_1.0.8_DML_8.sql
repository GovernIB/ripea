-- RIPEA 1.0.8
-- Emplena IPA_EXPEDIENT_ORGANPARE per als expedients que no hi tenen cap fila.
-- La taula es va crear a la 0.9.81 (#651) sense omplir-la per als expedients que ja existien, i nomes
-- l'emplena l'aplicacio en crear l'expedient o en canviar-li l'organ (OrganGestorHelper.crearExpedientOrganPares).
-- Sense aquestes files, els permisos d'usuari per organ (parella procediment-organ i procediments comuns)
-- no arriben a l'expedient i no surt al llistat.
-- Replica crearExpedientOrganPares: per a l'organ de l'expedient i cadascun dels seus ancestres (jerarquia
-- actual de IPA_ORGAN_GESTOR) cerca la parella procediment-organ a IPA_METAEXP_ORGAN, la crea si no existeix,
-- i hi enllaça l'expedient.
-- Es idempotent: nomes tracta els expedients sense cap fila a IPA_EXPEDIENT_ORGANPARE.
DECLARE
    V_METAEXP_ORGAN_ID  NUMBER;
    V_ORGANPARE_ID      NUMBER;
BEGIN
    FOR R IN (
        SELECT e.id               AS expedient_id,
               e.metaexpedient_id AS metaexpedient_id,
               e.organ_gestor_id  AS organ_gestor_id,
               c.createdby_codi   AS createdby_codi
        FROM ipa_expedient e
        JOIN ipa_contingut c ON c.id = e.id
        WHERE e.metaexpedient_id IS NOT NULL
          AND e.organ_gestor_id IS NOT NULL
          AND NOT EXISTS (
            SELECT 1
            FROM ipa_expedient_organpare op
            WHERE op.expedient_id = e.id)
        ORDER BY e.id
    ) LOOP

        FOR O IN (
            SELECT og.id AS organ_id
            FROM ipa_organ_gestor og
            START WITH og.id = R.organ_gestor_id
            CONNECT BY NOCYCLE og.id = PRIOR og.pare_id
            ORDER BY LEVEL
        ) LOOP

            SELECT MIN(mo.id) INTO V_METAEXP_ORGAN_ID
            FROM ipa_metaexp_organ mo
            WHERE mo.meta_expedient_id = R.metaexpedient_id
              AND mo.organ_gestor_id = O.organ_id;

            IF V_METAEXP_ORGAN_ID IS NULL THEN
                SELECT ipa_hibernate_seq.NEXTVAL INTO V_METAEXP_ORGAN_ID FROM dual;

                INSERT INTO ipa_metaexp_organ (
                    id, meta_expedient_id, organ_gestor_id,
                    createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate)
                VALUES (
                    V_METAEXP_ORGAN_ID, R.metaexpedient_id, O.organ_id,
                    R.createdby_codi, SYSTIMESTAMP, R.createdby_codi, SYSTIMESTAMP);
            END IF;

            SELECT ipa_hibernate_seq.NEXTVAL INTO V_ORGANPARE_ID FROM dual;

            INSERT INTO ipa_expedient_organpare (
                id, expedient_id, meta_expedient_organ_id,
                createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate)
            VALUES (
                V_ORGANPARE_ID, R.expedient_id, V_METAEXP_ORGAN_ID,
                R.createdby_codi, SYSTIMESTAMP, R.createdby_codi, SYSTIMESTAMP);

        END LOOP;

    END LOOP;
END;
