--1684
INSERT INTO ipa_metadocumentflux (
    id,
    metadocument_id,
    portafirmes_flux_id,
    portafirmes_flux_desc,
    createdby_codi,
    createddate
)
SELECT 
    nextval('ipa_hibernate_seq'),  -- equivalente a IPA_HIBERNATE_SEQ.NEXTVAL
    id,
    portafirmes_fluxid,
    'FLUX_' || portafirmes_fluxid,
    'SYSTEM',
    CURRENT_TIMESTAMP             -- equivalente a SYSDATE
FROM ipa_metadocument
WHERE portafirmes_fluxid IS NOT NULL;