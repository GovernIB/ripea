-- Esborra els permisos (ACL) orfes: objectes d'IPA_ACL_OBJECT_IDENTITY de les classes de RIPEA
-- l'objecte dels quals ja no existeix a la seva taula, amb les seves entrades d'IPA_ACL_ENTRY.
-- Fins a la 1.0.9, esborrar un procediment (i les seves relacions procediment-òrgan, que s'esborren
-- en cascada) o un grup no n'esborrava els permisos.
--   EntitatEntity                  -> IPA_ENTITAT
--   MetaNodeEntity                 -> IPA_METANODE (procediments)
--   GrupEntity                     -> IPA_GRUP
--   OrganGestorEntity              -> IPA_ORGAN_GESTOR
--   MetaExpedientOrganGestorEntity -> IPA_METAEXP_ORGAN
-- Les altres classes que hi pugui haver a IPA_ACL_CLASS no es toquen. Les files d'IPA_ACL_SID tampoc:
-- un mateix principal pot tenir permisos sobre altres objectes.

-- 1. Entrades (permisos) dels objectes orfes
DELETE FROM IPA_ACL_ENTRY
 WHERE ACL_OBJECT_IDENTITY IN (
    SELECT OI.ID
      FROM IPA_ACL_OBJECT_IDENTITY OI
      JOIN IPA_ACL_CLASS C ON C.ID = OI.OBJECT_ID_CLASS
     WHERE (C.CLASS = 'es.caib.ripea.persistence.entity.EntitatEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_ENTITAT X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.MetaNodeEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_METANODE X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.GrupEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_GRUP X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.OrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_ORGAN_GESTOR X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_METAEXP_ORGAN X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
 );

-- 2. RIPEA no informa PARENT_OBJECT; si algun objecte depengués d'un orfe, la clau forana
--    IPA_ACL_OID_PARENT_FK impediria esborrar-lo: es desvincula.
UPDATE IPA_ACL_OBJECT_IDENTITY
   SET PARENT_OBJECT = NULL
 WHERE PARENT_OBJECT IN (
    SELECT OI.ID
      FROM IPA_ACL_OBJECT_IDENTITY OI
      JOIN IPA_ACL_CLASS C ON C.ID = OI.OBJECT_ID_CLASS
     WHERE (C.CLASS = 'es.caib.ripea.persistence.entity.EntitatEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_ENTITAT X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.MetaNodeEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_METANODE X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.GrupEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_GRUP X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.OrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_ORGAN_GESTOR X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_METAEXP_ORGAN X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
 );

-- 3. Objectes orfes
DELETE FROM IPA_ACL_OBJECT_IDENTITY
 WHERE ID IN (
    SELECT OI.ID
      FROM IPA_ACL_OBJECT_IDENTITY OI
      JOIN IPA_ACL_CLASS C ON C.ID = OI.OBJECT_ID_CLASS
     WHERE (C.CLASS = 'es.caib.ripea.persistence.entity.EntitatEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_ENTITAT X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.MetaNodeEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_METANODE X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.GrupEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_GRUP X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.OrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_ORGAN_GESTOR X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
        OR (C.CLASS = 'es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM IPA_METAEXP_ORGAN X WHERE X.ID = OI.OBJECT_ID_IDENTITY))
 );
