-----------------------------------------------------------------
-- #144 Millora. Modificacio de enviament de emails als usuaris
-----------------------------------------------------------------
CREATE TABLE IPA_CONT_MOV_EMAIL 
(
  ID 					NUMBER(19) 				NOT NULL, 
  DESTINATARI_CODI		VARCHAR2(64) 			NOT NULL, 
  DESTINATARI_EMAIL		VARCHAR2(256) 			NOT NULL,
  ENVIAMENT_AGRUPAT		NUMBER(1)				NOT NULL,
  BUSTIA_ID 			NUMBER(19) 				NOT NULL,
  CONTINGUT_MOVIMENT_ID NUMBER(19) 				NOT NULL, 
  CONTINGUT_ID 			NUMBER(19) 				NOT NULL,
  UNITAT_ORGANITZATIVA 	VARCHAR2(256),
  CREATEDDATE          	TIMESTAMP(6),
  LASTMODIFIEDDATE     	TIMESTAMP(6),
  CREATEDBY_CODI       	VARCHAR2(64),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64)
);

ALTER TABLE IPA_CONT_MOV_EMAIL ADD (
  CONSTRAINT IPA_CONT_MOV_EMAIL_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_BUSTIA_CONTMOVEMAIL_FK FOREIGN KEY (BUSTIA_ID) REFERENCES IPA_BUSTIA (ID),
  CONSTRAINT IPA_CONTMOV_CONTMOVEMAIL_FK FOREIGN KEY (CONTINGUT_MOVIMENT_ID) REFERENCES IPA_CONT_MOV (ID),
  CONSTRAINT IPA_CONT_CONTMOVEMAIL_FK FOREIGN KEY (CONTINGUT_ID) REFERENCES IPA_CONTINGUT (ID));
  
CREATE INDEX IPA_DEST_CONTMOVEMAIL_FK_I ON IPA_CONT_MOV_EMAIL(DESTINATARI);

GRANT SELECT, UPDATE, INSERT, DELETE ON IPA_CONT_MOV_EMAIL TO WWW_RIPEA;

ALTER TABLE IPA_USUARI ADD REBRE_EMAILS NUMBER(1,0);
ALTER TABLE IPA_USUARI ADD EMAILS_AGRUPATS NUMBER(1,0);

UPDATE IPA_USUARI SET REBRE_EMAILS = 1 WHERE REBRE_EMAILS IS NULL;
UPDATE IPA_USUARI SET EMAILS_AGRUPATS = 1 WHERE EMAILS_AGRUPATS IS NULL;
