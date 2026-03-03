--1811 Separar integracions de Arxiu i ConCSV
INSERT INTO IPA_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) VALUES ('CONCSV', 'PLUGINS', 2, 'Propietats del plugin de ConCSV');

UPDATE IPA_CONFIG SET GROUP_CODE='CONCSV' WHERE KEY IN (
'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.url'
,'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.usuari'
,'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.contrasenya'
,'es.caib.ripea.plugin.arxiu.caib.timeout.connect'
,'es.caib.ripea.plugin.arxiu.caib.timeout.read'
,'es.caib.ripea.concsv.base.url'
);

INSERT INTO IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) 
values ('es.caib.ripea.concsv.class','es.caib.plugins.arxiu.caib.ArxiuPluginCaib','Classe del plugin','CONCSV','0','0','TEXT');

INSERT INTO IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) 
values ('es.caib.ripea.concsv.endpointName','concsvapi SE','Descripcio del endpoint de CONCSV.','CONCSV','20','0','TEXT');