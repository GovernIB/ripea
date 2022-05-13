-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 10/05/22 08:26
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/0.9.93/1030.yaml::1649068192899-1::limit
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.importacio.expedient.relacionat.activa','false','Importar expedients relacionats a la llista de documents','CONTINGUT','20','0','BOOL',null,null);

-- Changeset db/changelog/changes/0.9.93/1030.yaml::1649080293727-1::limit
ALTER TABLE ipa_carpeta ADD expedient_relacionat NUMBER(19);
ALTER TABLE ipa_carpeta ADD CONSTRAINT ipa_carpeta_exprel_fk FOREIGN KEY (expedient_relacionat) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/changes/0.9.93/1033.yaml::1649333286379-1::limit
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.conversio.definitiu.propagar.arxiu','false','Propagar la conversió a definitiu de documents a l''arxiu','ALTRES','1','0','BOOL',null,null);

-- Changeset db/changelog/changes/0.9.93/1039.yaml::1650447264424-1::limit
ALTER TABLE ipa_portafirmes_block_info ADD sign_date TIMESTAMP;

-- Changeset db/changelog/changes/0.9.93/1043.yaml::1651143025000-1::limit
CREATE TABLE IPA_AVIS (ID NUMBER(19) NOT NULL, ID NUMBER(19) NOT NULL, ASSUMPTE VARCHAR2(256 CHAR) NOT NULL, MISSATGE VARCHAR2(2048 CHAR) NOT NULL, DATA_INICI TIMESTAMP NOT NULL, DATA_FINAL TIMESTAMP NOT NULL, ACTIU NUMBER(1) NOT NULL, AVIS_NIVELL VARCHAR2(10 CHAR) NOT NULL, CREATEDBY_CODI VARCHAR2(64 CHAR), CREATEDDATE TIMESTAMP, LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), LASTMODIFIEDDATE TIMESTAMP, CONSTRAINT IPA_AVIS_PK PRIMARY KEY (ID));
CREATE INDEX IPA_AVIS_DATA_INICI_I ON IPA_AVIS(DATA_INICI);
CREATE INDEX IPA_AVIS_DATA_FINAL_I ON IPA_AVIS(DATA_FINAL);

GRANT SELECT, UPDATE, INSERT, DELETE ON IPA_AVIS TO WWW_RIPEA;

-- Changeset db/changelog/changes/0.9.93/937.yaml::1648212798512-1::limit
ALTER TABLE ipa_metaexpedient ADD crear_regla_dist_estat VARCHAR2(10);
ALTER TABLE ipa_metaexpedient ADD crear_regla_dist_error VARCHAR2(1024 CHAR);

INSERT INTO IPA_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (11, 'PLUGINS', 'DISTRIBUCIO_REGLA', 'Plugin de creació de regla en DISTRIBUCIO' );
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.url',null,'Url del plugin','DISTRIBUCIO_REGLA','1','1','TEXT',null,null);
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.username',null,'Usuari del plugin','DISTRIBUCIO_REGLA','2','1','TEXT',null,null);
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.password',null,'Constrasenya del plugin','DISTRIBUCIO_REGLA','3','1','PASSWORD',null,null);
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.codi.backoffice',null,'Codi de backoffice','DISTRIBUCIO_REGLA','4','0','TEXT',null,null);

