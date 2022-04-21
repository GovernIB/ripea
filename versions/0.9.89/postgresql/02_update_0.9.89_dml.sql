-- #877
UPDATE IPA_CONFIG_TYPE SET value = value || ',es.caib.ripea.plugin.caib.firmaservidor.FirmaSimpleServidorPluginPortafib' WHERE CODE = 'FIRMASERVIDOR_CLASS';
UPDATE IPA_CONFIG SET TYPE_CODE = 'FIRMASERVIDOR_CLASS' WHERE KEY = 'es.caib.ripea.plugin.firmaservidor.class';
INSERT INTO IPA_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.ripea.plugin.firmaservidor.portafib.endpoint', null, 'Url de l''API REST del portafirmes', 'FIRMA_SERVIDOR', 1, true, 'TEXT');
INSERT INTO IPA_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.ripea.plugin.firmaservidor.portafib.auth.username', null, 'Usuari per accedir a portafirmes', 'FIRMA_SERVIDOR', 2, true, 'TEXT');
INSERT INTO IPA_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.ripea.plugin.firmaservidor.portafib.auth.password', null, 'Password per accedir al Portafirmes', 'FIRMA_SERVIDOR', 3, true, 'PASSWORD');
INSERT INTO IPA_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.ripea.plugin.firmaservidor.portafib.perfil', 'PADES', 'Perfil de firma', 'FIRMA_SERVIDOR', 4, false, 'TEXT');
UPDATE IPA_CONFIG SET POSITION = 5 WHERE KEY = 'es.caib.ripea.plugin.firmaservidor.portafib.signerEmail';
UPDATE IPA_CONFIG SET POSITION = 6 WHERE KEY = 'es.caib.ripea.plugin.firmaservidor.portafib.location';
UPDATE IPA_CONFIG SET POSITION = 7 WHERE KEY = 'es.caib.ripea.plugin.firmaservidor.portafib.username';

-- #929
INSERT INTO IPA_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.ripea.pinbal.codi.sia.peticions', null, 'Codi SIA a utilitzar en totes les peticions a PINBAL. (En cas de no emplenar-se s''utilitzarà el codi SIA de cada procediment)', 'PINBAL', 4, false, 'TEXT');