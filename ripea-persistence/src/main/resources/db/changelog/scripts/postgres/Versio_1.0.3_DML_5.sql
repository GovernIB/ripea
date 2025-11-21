-- 1791
INSERT INTO IPA_CONFIG_TYPE (CODE, VALUE) VALUES ('COMANDA_CLASS', 'es.caib.ripea.plugin.caib.comanda.ComandaCaibPluginImpl');
INSERT INTO IPA_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) VALUES ('COMANDA', 'PLUGINS', 12, 'Plugin de comanda');

INSERT INTO IPA_CONFIG (
    KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE,
    LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE,
    CONFIGURABLE_ORGAN, ORGAN_CODI,
    CONFIGURABLE_ENTITAT_ACTIU, CONFIGURABLE_ORGAN_ACTIU,
    ENTITAT_CODI, CONFIGURABLE, CONFIGURABLE_ORG_DESCENDENTS
)
VALUES 
('es.caib.ripea.plugin.comanda.baseurl', NULL, 'URL base de aplicacio de comanda, exemple: https://dev.caib.es', 'COMANDA', 1, '1', 'TEXT', NULL, NULL, false, NULL, false, false, NULL, false, false),

('es.caib.ripea.plugin.comanda.user', NULL, 'Usuari aplicacio de comanda', 'COMANDA', 2, '1', 'TEXT', NULL, NULL, false, NULL, false, false, NULL, false, false),

('es.caib.ripea.plugin.comanda.password', NULL, 'Password usuari aplicacio de comanda', 'COMANDA', 3, '1', 'TEXT', NULL, NULL, false, NULL, false, false, NULL, false, false),

('es.caib.ripea.plugin.comanda.endpointName', NULL, 'Descripció endpoint comanda, per exemple: Comanda DEV', 'COMANDA', 4, '0', 'TEXT', NULL, NULL, false, NULL, false, false, NULL, false, false)

('es.caib.ripea.plugin.comanda.class', 'es.caib.ripea.plugin.caib.comanda.ComandaCaibPluginImpl', 'Clase que implementa el plugin de comanda', 'COMANDA', 0, '0', 'COMANDA_CLASS', NULL, NULL, false, NULL, false, false, NULL, false, false)

('es.caib.ripea.plugin.comanda.actiu', 'true', 'El plugin de comanda esta actiu per enviar dades (Avisos i tasques)', 'COMANDA', 5, '0', 'BOOL', NULL, NULL, false, NULL, false, false, NULL, false, false)

('es.caib.ripea.plugin.comanda.entorn', null, 'Entorn per enviar a comanda (DEV, PRE, PRO, SE)', 'COMANDA', 6, '1', 'TEXT', NULL, NULL, false, NULL, false, false, NULL, false, false);