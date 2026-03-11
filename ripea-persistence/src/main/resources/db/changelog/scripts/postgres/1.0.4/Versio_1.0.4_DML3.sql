-- 1811 Separar integracions de Arxiu i ConCSV

INSERT INTO ipa_config_group (code, parent_code, position, description) 
VALUES ('CONCSV', 'PLUGINS', 2, 'Propietats del plugin de ConCSV');

UPDATE ipa_config 
SET group_code = 'CONCSV' 
WHERE key IN (
     'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.url'
    ,'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.usuari'
    ,'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.contrasenya'
    ,'es.caib.ripea.plugin.arxiu.caib.timeout.connect'
    ,'es.caib.ripea.plugin.arxiu.caib.timeout.read'
    ,'es.caib.ripea.concsv.base.url'
);

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code)
VALUES ('es.caib.ripea.concsv.class', 'es.caib.plugins.arxiu.caib.ArxiuPluginCaib', 'Classe del plugin', 'CONCSV', '0', '0', 'TEXT');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code)
VALUES ('es.caib.ripea.concsv.endpointName', 'concsvapi SE', 'Descripcio del endpoint de CONCSV.', 'CONCSV', '20', '0', 'TEXT');