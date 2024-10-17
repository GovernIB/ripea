-- 1068
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.mostrar.seguiment.enviaments.usuari', 'false', 'Mostrar les pantalles de seguiments dels enviaments a Portafirmes i Notib al perfil d''''usuari', 'ALTRES', '16', '0', 'BOOL', '0');

-- 1421
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) values ('es.caib.ripea.segonpla.arxiu.maxMb','100','Maxim tamany en Mb que pot ocupar l''arxiu zip que conté els documents dels expedients seleccionats.','SCHEDULLED',3,0,'INT');
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) values ('es.caib.ripea.segonpla.arxiu.maxTempsExec','10','Maxim temps en minuts que pot tardar el procés de  generar l''arxiu zip que conté els documents dels expedients seleccionats.','SCHEDULLED',4,0,'INT');

-- 1506
INSERT INTO ipa_config_group (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES ('12', 'PLUGINS', 'SUMMARIZE', 'Configuració del plugin de Resums');
INSERT INTO ipa_config_type (CODE, VALUE) VALUES ('SUMMARIZE_CLASS', 'es.caib.ripea.plugin.caib.summarize.SummarizePluginChatGPT,es.caib.ripea.plugin.caib.summarize.SummarizePluginBert');
INSERT INTO ipa_config (key, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.summarize.class', 'Classe del plugin', 'SUMMARIZE', '0', '0', 'SUMMARIZE_CLASS', '1', '1');
INSERT INTO ipa_config (key, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.summarize.url', 'Url del servei de resum', 'SUMMARIZE', '1', '1', 'TEXT', '1', '1');
INSERT INTO ipa_config (key, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.summarize.service.timeout', 'Timeout de connexió amb el servei de resum', 'SUMMARIZE', '5', '0', 'INT', '1', '1');
INSERT INTO ipa_config (key, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.summarize.usuari', 'Usuari d''accés al servei si escau', 'SUMMARIZE', '2', '1', 'TEXT', '1', '1');
INSERT INTO ipa_config (key, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.summarize.password', 'Contrasenya d''accés al servei si escau', 'SUMMARIZE', '3', '1', 'PASSWORD', '1', '1');
INSERT INTO ipa_config (key, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.summarize.gpt.apiKey', 'ApiKey d''accés al servei de resum GPT', 'SUMMARIZE', '4', '0', 'TEXT', '1', '1');
INSERT INTO ipa_config (key, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.summarize.model', 'Model de IA a utilitzar si escau.', 'SUMMARIZE', '0', '0', 'TEXT', '1', '1');
INSERT INTO ipa_config (key, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.summarize.model.maxTokens', 'Maxim de tokens (tipicament paraules, pero no sempre) que soporta el model en la peticio i resposta combinats.', 'SUMMARIZE', '0', '0', 'INT', '1', '1');

-- 1502
UPDATE IPA_METANODE SET NOM=TRIM(NOM);

-- 1068
UPDATE ipa_expedient_tasca SET DURACIO = CASE 
WHEN (DATA_LIMIT IS NULL OR DATA_INICI IS NULL) THEN NULL 
WHEN DATA_LIMIT < DATA_INICI THEN NULL
ELSE CEIL(EXTRACT(DAY FROM (DATA_LIMIT - DATA_INICI)) + 1) END;

UPDATE IPA_METAEXP_TASCA SET DURACIO = CASE 
WHEN (DATA_LIMIT IS NULL OR CREATEDDATE IS NULL) THEN NULL 
WHEN DATA_LIMIT < CREATEDDATE THEN NULL
ELSE CEIL(EXTRACT(DAY FROM (DATA_LIMIT - CREATEDDATE)) + 1) END;