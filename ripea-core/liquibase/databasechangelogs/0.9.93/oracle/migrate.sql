-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 13/07/22 13:01
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
CREATE TABLE IPA_AVIS (ID NUMBER(19) NOT NULL, ASSUMPTE VARCHAR2(256 CHAR) NOT NULL, MISSATGE VARCHAR2(2048 CHAR) NOT NULL, DATA_INICI TIMESTAMP NOT NULL, DATA_FINAL TIMESTAMP NOT NULL, ACTIU NUMBER(1) NOT NULL, AVIS_NIVELL VARCHAR2(10 CHAR) NOT NULL, CREATEDBY_CODI VARCHAR2(64 CHAR), CREATEDDATE TIMESTAMP, LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), LASTMODIFIEDDATE TIMESTAMP, CONSTRAINT IPA_AVIS_PK PRIMARY KEY (ID));
CREATE INDEX IPA_AVIS_DATA_INICI_I ON IPA_AVIS(DATA_INICI);
CREATE INDEX IPA_AVIS_DATA_FINAL_I ON IPA_AVIS(DATA_FINAL);
GRANT SELECT, UPDATE, INSERT, DELETE ON IPA_AVIS TO WWW_RIPEA;

-- Changeset db/changelog/changes/0.9.93/1084.yaml::1654161395063-1::limit
alter table ipa_interessat modify (document_tipus null);
alter table ipa_interessat modify (document_num null);
ALTER TABLE IPA_INTERESSAT DROP CONSTRAINT IPA_INTERESSAT_MULT_UK;
DROP INDEX IPA_INTERESSAT_MULT_UK;
alter table ipa_hist_exp_interessat modify (interessat_doc_num null);

-- Changeset db/changelog/changes/0.9.93/1096.yaml::1655365368195-1::limit
ALTER TABLE ipa_registre_annex ADD document_id NUMBER(19);
ALTER TABLE ipa_registre_annex ADD CONSTRAINT ipa_annex_document_fk FOREIGN KEY (document_id) REFERENCES ipa_document (id);
-- Comprovar si els elements són únics
 SELECT LISTAGG(ipa_document.id, ',') WITHIN GROUP (ORDER BY ipa_document.id) docs, LISTAGG(ipa_expedient.id, ',') WITHIN GROUP (ORDER BY ipa_document.id) exps FROM ipa_document INNER JOIN ipa_contingut ON ipa_document.id = ipa_contingut.id INNER JOIN ipa_expedient ON ipa_contingut.expedient_id = ipa_expedient.id INNER JOIN ipa_expedient_peticio ON ipa_expedient_peticio.expedient_id = ipa_expedient.id INNER JOIN ipa_registre ON ipa_expedient_peticio.registre_id = ipa_registre.id INNER JOIN ipa_registre_annex ON ipa_registre_annex.registre_id = ipa_registre.id WHERE ipa_registre.id = ipa_registre_annex.registre_id AND ipa_document.data_captura = ipa_registre_annex.nti_fecha_captura AND ipa_contingut.nom LIKE ipa_registre_annex.titol || '%' AND ipa_document.fitxer_nom = ipa_registre_annex.nom AND ipa_contingut.esborrat = 0 GROUP BY ipa_registre.id, ipa_document.data_captura, ipa_contingut.nom, ipa_document.fitxer_nom HAVING COUNT(*) > 1;
-- relacionar annexos amb documents creats, només funcionarà si l'script anterior no retorna cap resultat
 -- si l'script anterior retorna algun resultat y els documents trobats no son duplicats s'ha de eliminar-los 
 -- si l'script anterior retorna algun resultat y els documents trobats no son duplicats s'ha de pensar si existeix un altre paràmetre per diferenciar els documents entre si o relacionar documents trobats amb annexos manualment (UPDATE ipa_registre_annex SET document_id = DOCUMENT_ID where id = ANNEX_ID) 
 UPDATE ipa_registre_annex SET document_id = ( SELECT ipa_document.id FROM ipa_document INNER JOIN ipa_contingut ON ipa_document.id = ipa_contingut.id INNER JOIN ipa_expedient ON ipa_contingut.expedient_id = ipa_expedient.id INNER JOIN ipa_expedient_peticio ON ipa_expedient_peticio.expedient_id = ipa_expedient.id INNER JOIN ipa_registre ON ipa_expedient_peticio.registre_id = ipa_registre.id WHERE ipa_registre.id = ipa_registre_annex.registre_id AND ipa_document.data_captura = ipa_registre_annex.nti_fecha_captura AND ipa_contingut.nom LIKE ipa_registre_annex.titol || '%' AND ipa_document.fitxer_nom = ipa_registre_annex.nom AND ipa_contingut.esborrat = 0) where document_id is null;

-- Changeset db/changelog/changes/0.9.93/1109.yaml::1657536749461-1::limit
ALTER TABLE ipa_registre_annex ADD val_ok NUMBER(1) DEFAULT '1';
ALTER TABLE ipa_registre_annex ADD val_error VARCHAR2(1000 CHAR);
ALTER TABLE ipa_registre_annex ADD annex_estat VARCHAR2(16 CHAR);
ALTER TABLE ipa_document ADD val_ok NUMBER(1) DEFAULT '1';
ALTER TABLE ipa_document ADD val_error VARCHAR2(1000 CHAR);
ALTER TABLE ipa_document ADD annex_estat VARCHAR2(16 CHAR);

-- Changeset db/changelog/changes/0.9.93/937.yaml::1648212798512-1::limit
ALTER TABLE ipa_metaexpedient ADD crear_regla_dist_estat VARCHAR2(10 CHAR);
ALTER TABLE ipa_metaexpedient ADD crear_regla_dist_error VARCHAR2(1024 CHAR);
INSERT INTO IPA_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (11, 'PLUGINS', 'DISTRIBUCIO_REGLA', 'Plugin de creació de regla en DISTRIBUCIO' );
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.url',null,'Url del plugin','DISTRIBUCIO_REGLA','1','1','TEXT',null,null);
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.username',null,'Usuari del plugin','DISTRIBUCIO_REGLA','2','1','TEXT',null,null);
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.password',null,'Constrasenya del plugin','DISTRIBUCIO_REGLA','3','1','PASSWORD',null,null);
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.distribucio.regla.ws.codi.backoffice',null,'Codi de backoffice','DISTRIBUCIO_REGLA','4','0','TEXT',null,null);

