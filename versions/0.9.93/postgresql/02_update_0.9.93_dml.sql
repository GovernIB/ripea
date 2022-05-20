-- 937
INSERT INTO IPA_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (11, 'PLUGINS', 'DISTRIBUCIO_REGLA', 'Plugin de creació de regla en DISTRIBUCIO' );
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.url',null,'Url del plugin','DISTRIBUCIO_REGLA','1','1','TEXT',null,null);
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.username',null,'Usuari del plugin','DISTRIBUCIO_REGLA','2','1','TEXT',null,null);
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.password',null,'Constrasenya del plugin','DISTRIBUCIO_REGLA','3','1','PASSWORD',null,null);
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.codi.backoffice',null,'Codi de backoffice','DISTRIBUCIO_REGLA','4','0','TEXT',null,null);

-- 1030
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.importacio.expedient.relacionat.activa','false','Importar expedients relacionats a la llista de documents','CONTINGUT','20','0','BOOL',null,null);

-- 1033
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.conversio.definitiu.propagar.arxiu','false','Propagar la conversió a definitiu de documents a l''arxiu','ALTRES','1','0','BOOL',null,null);