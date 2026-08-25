----------------PER ID DE OBJECTE------------------------------------

SELECT ACE_ORDER
     , MASK
     , PRINCIPAL
     , SID.SID
     , CLASS
FROM IPA_ACL_OBJECT_IDENTITY OBJ
   , IPA_ACL_ENTRY ENT
   , IPA_ACL_SID SID
   , IPA_ACL_CLASS CLA
WHERE CLA.ID=OBJ.OBJECT_ID_CLASS
  AND OBJ.ID=ENT.ACL_OBJECT_IDENTITY
  AND ENT.SID=SID.ID
  AND OBJ.OBJECT_ID_IDENTITY IN (3102, 3101, 3108);
  
----------------PER USUARI------------------------------------
  
SELECT
       CASE WHEN BITAND(ENT.MASK, 1)   != 0 THEN 'READ' ELSE '' END ||
       CASE WHEN BITAND(ENT.MASK, 2)   != 0 THEN 'WRITE' ELSE '' END ||
       CASE WHEN BITAND(ENT.MASK, 4)   != 0 THEN 'CREATE' ELSE '' END ||
       CASE WHEN BITAND(ENT.MASK, 8)   != 0 THEN 'DELETE' ELSE '' END ||
       CASE WHEN BITAND(ENT.MASK, 16)  != 0 THEN 'ADMINISTRATION' ELSE '' END ||
       CASE WHEN BITAND(ENT.MASK, 32)  != 0 THEN 'STATISTICS' ELSE '' END ||
       CASE WHEN BITAND(ENT.MASK, 64)  != 0 THEN 'COMU' ELSE '' END ||
       CASE WHEN BITAND(ENT.MASK, 128) != 0 THEN 'ADM_COMU' ELSE '' END ||
       CASE WHEN BITAND(ENT.MASK, 256) != 0 THEN 'DISSENY' ELSE '' END ||
       CASE WHEN BITAND(ENT.MASK, 512) != 0 THEN 'ADM_LECTURA' ELSE '' END
       AS PERMISOS
     , SID.PRINCIPAL
     , SID.SID
     , CLA.CLASS
     , OBJ.OBJECT_ID_IDENTITY
     , CASE WHEN CLA.CLASS LIKE '%EntitatEntity' THEN (SELECT CODI||' - '||NOM FROM IPA_ENTITAT WHERE ID=OBJ.OBJECT_ID_IDENTITY) ELSE '' END ||
       CASE WHEN CLA.CLASS LIKE '%GrupEntity' THEN (SELECT DESCRIPCIO FROM IPA_GRUP WHERE ID=OBJ.OBJECT_ID_IDENTITY) ELSE '' END ||
       CASE WHEN CLA.CLASS LIKE '%MetaNodeEntity' THEN (SELECT CODI||' - '||NOM FROM IPA_METANODE WHERE ID=OBJ.OBJECT_ID_IDENTITY) ELSE '' END ||
       CASE WHEN CLA.CLASS LIKE '%OrganGestorEntity' THEN (SELECT CODI||' - '||NOM FROM IPA_ORGAN_GESTOR WHERE ID=OBJ.OBJECT_ID_IDENTITY) ELSE '' END ||
       CASE WHEN CLA.CLASS LIKE '%MetaExpedientOrganGestorEntity' THEN (SELECT IMN.CODI||' - '||IMN.NOM||' + '||IOG.CODI||' - '||IOG.NOM FROM IPA_METAEXP_ORGAN IME, IPA_METANODE IMN, IPA_ORGAN_GESTOR IOG WHERE IME.META_EXPEDIENT_ID=IMN.ID AND IME.ORGAN_GESTOR_ID=IOG.ID AND IME.ID=OBJ.OBJECT_ID_IDENTITY) ELSE '' END AS OBJECTE_NOM
FROM IPA_ACL_OBJECT_IDENTITY OBJ
   , IPA_ACL_ENTRY ENT
   , IPA_ACL_SID SID
   , IPA_ACL_CLASS CLA
WHERE CLA.ID=OBJ.OBJECT_ID_CLASS
  AND OBJ.ID=ENT.ACL_OBJECT_IDENTITY
  AND ENT.SID=SID.ID
  AND ((SID.PRINCIPAL=1 AND SID.SID='rip_user2') or (SID.PRINCIPAL=0 AND SID.SID IN ('tothom')))
  ORDER BY CLA.CLASS, OBJ.OBJECT_ID_IDENTITY, PERMISOS;
  
----- TOTS ELS PERMISOS DE UN USUARI I ROLS -----

WITH sids AS (
    SELECT id, sid, principal
    FROM   ipa_acl_sid
    WHERE  (principal = 1 AND sid = 'u89776')
       OR  (principal = 0 AND sid IN ('tothom','IPA_2401020','IPA_2401021','IPA_GRUP_HSONLLATZER'))
)
SELECT s.sid                                                AS atorgat_a,
       CASE s.principal WHEN 1 THEN 'USUARI' ELSE 'ROL' END AS tipus_sid,
       REGEXP_SUBSTR(c.class, '[^.]+$')                     AS tipus_objecte,
       oi.object_id_identity                                AS objecte_id,
       CASE REGEXP_SUBSTR(c.class, '[^.]+$')
            WHEN 'MetaNodeEntity' THEN
                 (SELECT mn.codi || ' - ' || mn.nom || ' [' || mn.tipus || ']'
                    FROM ipa_metanode mn WHERE mn.id = oi.object_id_identity)
            WHEN 'OrganGestorEntity' THEN
                 (SELECT o.codi || ' - ' || o.nom || ' (estat=' || o.estat || ')'
                    FROM ipa_organ_gestor o WHERE o.id = oi.object_id_identity)
            WHEN 'GrupEntity' THEN
                 (SELECT g.codi || ' - ' || g.descripcio || ' (rol=' || g.rol || ')'
                    FROM ipa_grup g WHERE g.id = oi.object_id_identity)
            WHEN 'MetaExpedientOrganGestorEntity' THEN
                 (SELECT mn.codi || ' @ ' || o.codi
                    FROM ipa_metaexp_organ mo
                    JOIN ipa_metanode      mn ON mn.id = mo.meta_expedient_id
                    JOIN ipa_organ_gestor  o  ON o.id  = mo.organ_gestor_id
                   WHERE mo.id = oi.object_id_identity)
            WHEN 'EntitatEntity' THEN
                 (SELECT en.codi || ' - ' || en.nom
                    FROM ipa_entitat en WHERE en.id = oi.object_id_identity)
       END                                                  AS objecte,
       e.mask,
       CASE e.mask
            WHEN 1 THEN 'READ' WHEN 2 THEN 'WRITE' WHEN 4 THEN 'CREATE'
            WHEN 8 THEN 'DELETE' WHEN 16 THEN 'ADMINISTRATION' WHEN 32 THEN 'STATISTICS'
            WHEN 64 THEN 'COMU' WHEN 128 THEN 'ADM_COMU' WHEN 256 THEN 'DISSENY'
            WHEN 512 THEN 'ADMINISTRATION_READ' ELSE '?? (' || e.mask || ')'
       END                                                  AS permis
FROM   ipa_acl_entry            e
JOIN   sids                     s  ON s.id  = e.sid
JOIN   ipa_acl_object_identity  oi ON oi.id = e.acl_object_identity
JOIN   ipa_acl_class            c  ON c.id  = oi.object_id_class
WHERE  e.granting = 1
ORDER  BY tipus_objecte, objecte_id, s.principal DESC, e.mask;