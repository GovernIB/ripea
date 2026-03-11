-- 1792

UPDATE ipa_usuari 
SET vista_moure_actual = 'DESPLEGABLE' 
WHERE vista_moure_actual = 'ARBRE';

INSERT INTO ipa_config_group (code, parent_code, position, description) 
VALUES ('FIRMA_AGIL', 'FIRMA', '6', 'Plugin de validació de firmes àgils');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, lastmodifiedby_codi, lastmodifieddate, configurable_organ, organ_codi, configurable_entitat_actiu, configurable_organ_actiu, entitat_codi, configurable, configurable_org_descendents) 
VALUES ('es.caib.ripea.plugin.validarsignatura.agil.activa', null, 'Acitvar validació firmes àgils', 'FIRMA_AGIL', '0', '0', 'BOOL', null, null, '1', null, '1', '1', null, '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, lastmodifiedby_codi, lastmodifieddate, configurable_organ, organ_codi, configurable_entitat_actiu, configurable_organ_actiu, entitat_codi, configurable, configurable_org_descendents) 
VALUES ('es.caib.ripea.plugin.validarsignatura.agil.class', 'es.caib.ripea.plugin.caib.validacio.ValidacioFirmaPluginApiEvidenciesIB', 'Classe de plugin de validació de firmes àgils (EvidenciesIB)', 'FIRMA_AGIL', '1', '0', 'TEXT', null, null, '1', null, '1', '1', null, '1', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, lastmodifiedby_codi, lastmodifieddate, configurable_organ, organ_codi, configurable_entitat_actiu, configurable_organ_actiu, entitat_codi, configurable, configurable_org_descendents) 
VALUES ('es.caib.ripea.plugins.validarsignatura.agil.api.evidenciesib.endpoint', null, 'URL API EXTERNA EvidenciesIB', 'FIRMA_AGIL', '2', '1', 'TEXT', null, null, '0', null, '0', '0', null, '0', '0');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, lastmodifiedby_codi, lastmodifieddate, configurable_organ, organ_codi, configurable_entitat_actiu, configurable_organ_actiu, entitat_codi, configurable, configurable_org_descendents) 
VALUES ('es.caib.ripea.plugins.validarsignatura.agil.api.evidenciesib.username', null, 'Usuari integració de API EvidenciesIB', 'FIRMA_AGIL', '3', '1', 'TEXT', null, null, '0', null, '0', '0', null, '0', '0');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, lastmodifiedby_codi, lastmodifieddate, configurable_organ, organ_codi, configurable_entitat_actiu, configurable_organ_actiu, entitat_codi, configurable, configurable_org_descendents) 
VALUES ('es.caib.ripea.plugins.validarsignatura.agil.api.evidenciesib.password', null, 'Contrasenya usuario integració API EvidenciesIB', 'FIRMA_AGIL', '4', '1', 'PASSWORD', null, null, '0', null, '0', '0', null, '0', '0');