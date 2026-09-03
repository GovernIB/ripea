-- RIPEA 1.0.8
-- Es retira la funcionalitat de tipus de document generals (els que no depenien de cap
-- procediment): cada procediment ja te els seus tipus per defecte. S'esborra la propietat
-- que l'habilitava, incloses les possibles sobreescriptures per entitat o per organ.
DELETE FROM IPA_CONFIG WHERE KEY LIKE '%.habilitar.documentsgenerals';

-- IPA_DOCUMENT te una segona referencia al tipus de document (METADOCUMENT_ID, FK
-- IPA_DOC_METADOC_FK) a mes de la de IPA_NODE.METANODE_ID, que es d'on el programa llegeix
-- realment el tipus (DocumentEntity.getMetaDocument() retorna getMetaNode()). El script
-- Versio_1.0.8_DML_3 nomes va repuntar METANODE_ID, aixi que aqui s'hi sincronitza
-- METADOCUMENT_ID: sense aixo la FK impedeix esborrar els tipus de document generics.
UPDATE IPA_DOCUMENT d
SET METADOCUMENT_ID = (SELECT n.METANODE_ID FROM IPA_NODE n WHERE n.ID = d.ID)
WHERE d.METADOCUMENT_ID IN (
    SELECT md.ID
    FROM IPA_METADOCUMENT md
    WHERE md.META_EXPEDIENT_ID IS NULL
      AND md.CODI IN ('OTROS', 'ACUSE_RECIBO_NOTIFICACION', 'NOTIFICACION', 'JUSTIFICANTE_REGISTRO'))
  AND EXISTS (
    SELECT 1 FROM IPA_NODE n
    WHERE n.ID = d.ID
      AND n.METANODE_ID IS NOT NULL
      AND n.METANODE_ID <> d.METADOCUMENT_ID);

-- Els quatre tipus de document generics que donava d'alta el script d'instal.lacio (sense
-- procediment) ja no els fa servir res: els documents dels tres que tenien equivalent s'han
-- repuntat al tipus del procediment (Versio_1.0.8_DML_3) i el manteniment de tipus de document
-- generals s'ha retirat.
-- Nomes s'esborren els que no referencia cap document, ni per METANODE_ID ni per
-- METADOCUMENT_ID. OTROS no tenia equivalent i els seus documents no s'han migrat: si en queda
-- algun, la fila es deixa tal com esta.
DELETE FROM IPA_METADADA
WHERE META_NODE_ID IN (
    SELECT md.ID
    FROM IPA_METADOCUMENT md
    WHERE md.META_EXPEDIENT_ID IS NULL
      AND md.CODI IN ('OTROS', 'ACUSE_RECIBO_NOTIFICACION', 'NOTIFICACION', 'JUSTIFICANTE_REGISTRO')
      AND NOT EXISTS (SELECT 1 FROM IPA_NODE n WHERE n.METANODE_ID = md.ID)
      AND NOT EXISTS (SELECT 1 FROM IPA_DOCUMENT d WHERE d.METADOCUMENT_ID = md.ID));

DELETE FROM IPA_METADOCUMENTFLUX
WHERE METADOCUMENT_ID IN (
    SELECT md.ID
    FROM IPA_METADOCUMENT md
    WHERE md.META_EXPEDIENT_ID IS NULL
      AND md.CODI IN ('OTROS', 'ACUSE_RECIBO_NOTIFICACION', 'NOTIFICACION', 'JUSTIFICANTE_REGISTRO')
      AND NOT EXISTS (SELECT 1 FROM IPA_NODE n WHERE n.METANODE_ID = md.ID)
      AND NOT EXISTS (SELECT 1 FROM IPA_DOCUMENT d WHERE d.METADOCUMENT_ID = md.ID));

DELETE FROM IPA_METADOCUMENT md
    WHERE md.META_EXPEDIENT_ID IS NULL
      AND md.CODI IN ('OTROS', 'ACUSE_RECIBO_NOTIFICACION', 'NOTIFICACION', 'JUSTIFICANTE_REGISTRO')
      AND NOT EXISTS (SELECT 1 FROM IPA_NODE n WHERE n.METANODE_ID = md.ID)
      AND NOT EXISTS (SELECT 1 FROM IPA_DOCUMENT d WHERE d.METADOCUMENT_ID = md.ID);

-- La fila de IPA_METANODE de cada tipus de document esborrat (herencia JOINED).
DELETE FROM IPA_METANODE mn
WHERE mn.TIPUS = 'DOCUMENT'
  AND mn.CODI IN ('OTROS', 'ACUSE_RECIBO_NOTIFICACION', 'NOTIFICACION', 'JUSTIFICANTE_REGISTRO')
  AND NOT EXISTS (SELECT 1 FROM IPA_METADOCUMENT md WHERE md.ID = mn.ID)
  AND NOT EXISTS (SELECT 1 FROM IPA_METAEXPEDIENT me WHERE me.ID = mn.ID)
  AND NOT EXISTS (SELECT 1 FROM IPA_NODE n WHERE n.METANODE_ID = mn.ID);
