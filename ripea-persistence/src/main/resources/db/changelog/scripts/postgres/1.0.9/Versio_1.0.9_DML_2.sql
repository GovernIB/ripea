-- Esborra els permisos (ACL) orfes: objectes d'ipa_acl_object_identity de les classes de RIPEA
-- l'objecte dels quals ja no existeix a la seva taula, amb les seves entrades d'ipa_acl_entry.
-- Fins a la 1.0.9, esborrar un procediment (i les seves relacions procediment-òrgan, que s'esborren
-- en cascada) o un grup no n'esborrava els permisos.
--   EntitatEntity                  -> ipa_entitat
--   MetaNodeEntity                 -> ipa_metanode (procediments)
--   GrupEntity                     -> ipa_grup
--   OrganGestorEntity              -> ipa_organ_gestor
--   MetaExpedientOrganGestorEntity -> ipa_metaexp_organ
-- Les altres classes que hi pugui haver a ipa_acl_class no es toquen. Les files d'ipa_acl_sid tampoc:
-- un mateix principal pot tenir permisos sobre altres objectes.

-- 1. Entrades (permisos) dels objectes orfes
DELETE FROM ipa_acl_entry
 WHERE acl_object_identity IN (
    SELECT oi.id
      FROM ipa_acl_object_identity oi
      JOIN ipa_acl_class c ON c.id = oi.object_id_class
     WHERE (c.class = 'es.caib.ripea.persistence.entity.EntitatEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_entitat x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.MetaNodeEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_metanode x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.GrupEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_grup x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.OrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_organ_gestor x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_metaexp_organ x WHERE x.id = oi.object_id_identity))
 );

-- 2. RIPEA no informa parent_object; si algun objecte depengués d'un orfe, la clau forana
--    ipa_acl_oid_parent_fk impediria esborrar-lo: es desvincula.
UPDATE ipa_acl_object_identity
   SET parent_object = NULL
 WHERE parent_object IN (
    SELECT oi.id
      FROM ipa_acl_object_identity oi
      JOIN ipa_acl_class c ON c.id = oi.object_id_class
     WHERE (c.class = 'es.caib.ripea.persistence.entity.EntitatEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_entitat x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.MetaNodeEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_metanode x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.GrupEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_grup x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.OrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_organ_gestor x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_metaexp_organ x WHERE x.id = oi.object_id_identity))
 );

-- 3. Objectes orfes
DELETE FROM ipa_acl_object_identity
 WHERE id IN (
    SELECT oi.id
      FROM ipa_acl_object_identity oi
      JOIN ipa_acl_class c ON c.id = oi.object_id_class
     WHERE (c.class = 'es.caib.ripea.persistence.entity.EntitatEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_entitat x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.MetaNodeEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_metanode x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.GrupEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_grup x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.OrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_organ_gestor x WHERE x.id = oi.object_id_identity))
        OR (c.class = 'es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity'
            AND NOT EXISTS (SELECT 1 FROM ipa_metaexp_organ x WHERE x.id = oi.object_id_identity))
 );
