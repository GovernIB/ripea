-- RIPEA 1.0.8
-- Els tipus de document generics (els que no depenien de cap procediment, marcats amb
-- META_DOCUMENT_TIPUS_GEN) es retiren i cada procediment passa a tenir els seus:
--   ACUSE_RECIBO_NOTIFICACION -> NOTIB_JUSTIFICANT_RECEPCIO
--   NOTIFICACION              -> NOTIFICACIO_MULTIPLE
--   JUSTIFICANTE_REGISTRO     -> REGISTRE_JUSTIFICANT_ENTRADA
-- Aquest script repunta els documents ja existents d'aquests tres tipus generics al
-- tipus equivalent del procediment del seu expedient. Ha d'executar-se ABANS d'esborrar
-- la columna META_DOCUMENT_TIPUS_GEN i DESPRES dels DML que creen els tipus per defecte
-- a tots els procediments (Versio_1.0.8_DML_1 i Versio_1.0.8_DML_2).
--
-- Nomes toca els tipus de document sense procediment (META_EXPEDIENT_ID IS NULL), que son
-- els generics de veritat. El quart valor de l'enumerat, OTROS, no te equivalent i per tant
-- els seus documents es deixen tal com estan. Un document tampoc no es toca si el seu
-- expedient no te el tipus de desti (no hauria de passar despres dels DML anteriors) o si
-- no penja de cap expedient.
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
                                AND md_nou.codi = CASE md_ant.meta_document_tipus_gen
                                                      WHEN 'ACUSE_RECIBO_NOTIFICACION' THEN 'NOTIB_JUSTIFICANT_RECEPCIO'
                                                      WHEN 'NOTIFICACION'              THEN 'NOTIFICACIO_MULTIPLE'
                                                      WHEN 'JUSTIFICANTE_REGISTRO'     THEN 'REGISTRE_JUSTIFICANT_ENTRADA'
                                                  END
    WHERE md_ant.meta_expedient_id IS NULL
      AND md_ant.meta_document_tipus_gen IN ('ACUSE_RECIBO_NOTIFICACION', 'NOTIFICACION', 'JUSTIFICANTE_REGISTRO')
      AND md_nou.id <> nd.metanode_id
) src
ON (n.id = src.node_id)
WHEN MATCHED THEN UPDATE SET n.metanode_id = src.metanode_nou_id;
