/**
 * Tipus de document que es creen automàticament a l'alta de tot procediment.
 *
 * Només un administrador d'entitat (IPA_ADMIN) els pot modificar o eliminar; per a la
 * resta de rols són de només consulta. Ha d'estar sincronitzat amb l'enumerat
 * MetaDocumentPerDefecteEnumDto del backend, que és qui aplica la restricció de veritat.
 */
export const CODIS_METADOC_PER_DEFECTE = [
    'NOTIB_JUSTIFICANT_RECEPCIO',
    'REGISTRE_JUSTIFICANT_ENTRADA',
    'NOTIFICACIO_MULTIPLE',
    'OTROS',
];

/** Indica si el tipus de document és un dels creats per defecte al procediment. */
export const esMetaDocumentPerDefecte = (codi?: string): boolean =>
    codi != null && CODIS_METADOC_PER_DEFECTE.includes(codi);
