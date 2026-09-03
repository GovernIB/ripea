-- RIPEA 1.0.8
-- Migracio dels documents del tipus de document generic OTROS al tipus OTROS del seu
-- procediment (creat a Versio_1.0.8_DML_5) i retirada del generic.
-- Els documents que encara son del tipus generic OTROS passen al OTROS del seu procediment.
MERGE INTO ipa_node n
USING (
    SELECT nd.id     AS node_id,
           md_nou.id AS metanode_nou_id
    FROM ipa_document doc
    JOIN ipa_node nd             ON nd.id = doc.id
    JOIN ipa_contingut c         ON c.id = doc.id
    JOIN ipa_metadocument md_ant ON md_ant.id = nd.metanode_id
    JOIN ipa_expedient e         ON e.id = c.expedient_id
    JOIN ipa_metadocument md_nou ON md_nou.meta_expedient_id = e.metaexpedient_id
                                AND md_nou.codi = 'OTROS'
    WHERE md_ant.meta_expedient_id IS NULL
      AND md_ant.codi = 'OTROS'
      AND md_nou.id <> nd.metanode_id
) src
ON (n.id = src.node_id)
WHEN MATCHED THEN UPDATE SET n.metanode_id = src.metanode_nou_id;

-- La segona referencia al tipus de document (FK IPA_DOC_METADOC_FK), com al DML_4.
UPDATE IPA_DOCUMENT d
SET METADOCUMENT_ID = (SELECT n.METANODE_ID FROM IPA_NODE n WHERE n.ID = d.ID)
WHERE d.METADOCUMENT_ID IN (
    SELECT md.ID
    FROM IPA_METADOCUMENT md
    WHERE md.META_EXPEDIENT_ID IS NULL
      AND md.CODI = 'OTROS')
  AND EXISTS (
    SELECT 1 FROM IPA_NODE n
    WHERE n.ID = d.ID
      AND n.METANODE_ID IS NOT NULL
      AND n.METANODE_ID <> d.METADOCUMENT_ID);

-- I ja es pot esborrar el generic OTROS (si el DML_4 no l'havia pogut esborrar abans).
DELETE FROM IPA_METADADA
WHERE META_NODE_ID IN (
    SELECT md.ID
    FROM IPA_METADOCUMENT md
    WHERE md.META_EXPEDIENT_ID IS NULL
      AND md.CODI = 'OTROS'
      AND NOT EXISTS (SELECT 1 FROM IPA_NODE n WHERE n.METANODE_ID = md.ID)
      AND NOT EXISTS (SELECT 1 FROM IPA_DOCUMENT d WHERE d.METADOCUMENT_ID = md.ID));

DELETE FROM IPA_METADOCUMENTFLUX
WHERE METADOCUMENT_ID IN (
    SELECT md.ID
    FROM IPA_METADOCUMENT md
    WHERE md.META_EXPEDIENT_ID IS NULL
      AND md.CODI = 'OTROS'
      AND NOT EXISTS (SELECT 1 FROM IPA_NODE n WHERE n.METANODE_ID = md.ID)
      AND NOT EXISTS (SELECT 1 FROM IPA_DOCUMENT d WHERE d.METADOCUMENT_ID = md.ID));

DELETE FROM IPA_METADOCUMENT md
WHERE md.META_EXPEDIENT_ID IS NULL
  AND md.CODI = 'OTROS'
  AND NOT EXISTS (SELECT 1 FROM IPA_NODE n WHERE n.METANODE_ID = md.ID)
  AND NOT EXISTS (SELECT 1 FROM IPA_DOCUMENT d WHERE d.METADOCUMENT_ID = md.ID);

DELETE FROM IPA_METANODE mn
WHERE mn.TIPUS = 'DOCUMENT'
  AND mn.CODI = 'OTROS'
  AND NOT EXISTS (SELECT 1 FROM IPA_METADOCUMENT md WHERE md.ID = mn.ID)
  AND NOT EXISTS (SELECT 1 FROM IPA_METAEXPEDIENT me WHERE me.ID = mn.ID)
  AND NOT EXISTS (SELECT 1 FROM IPA_NODE n WHERE n.METANODE_ID = mn.ID);

UPDATE IPA_CONFIG SET DESCRIPTION='Incorporar justificant de recepció de la notificació als expedients' WHERE KEY like '%notificacio.guardar.certificacio.expedient';