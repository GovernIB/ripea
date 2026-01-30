--1793 Afegir nova funcionalitat per moure tot entre expedients
INSERT INTO IPA_CONFIG (
    KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE,
    LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE,
    CONFIGURABLE_ORGAN, ORGAN_CODI,
    CONFIGURABLE_ENTITAT_ACTIU, CONFIGURABLE_ORGAN_ACTIU,
    ENTITAT_CODI, CONFIGURABLE, CONFIGURABLE_ORG_DESCENDENTS
)
VALUES (
    'es.caib.ripea.expedient.accio.moure.tot.activa',
    'false',
    'Activa l''acció ''moure tot'' entre dos expedients..',
    'CONTINGUT',
    42,
    '0',
    'BOOL',
    NULL,
    NULL,
    '0',
    NULL,
    '0',
    '0',
    NULL,
    '0',
    '0'
);