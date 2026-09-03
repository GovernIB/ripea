-- RIPEA 1.0.8
-- Es retira el tipus generic de tipus de document (MetaDocumentTipusGenericEnumDto).
-- Els tres valors que s'utilitzaven passen a ser tipus de document de cada procediment:
--   ACUSE_RECIBO_NOTIFICACION -> NOTIB_JUSTIFICANT_RECEPCIO
--   NOTIFICACION              -> NOTIFICACIO_MULTIPLE
--   JUSTIFICANTE_REGISTRO     -> REGISTRE_JUSTIFICANT_ENTRADA
ALTER TABLE IPA_METADOCUMENT DROP COLUMN META_DOCUMENT_TIPUS_GEN;
