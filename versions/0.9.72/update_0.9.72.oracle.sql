CREATE TABLE IPA_METAEXP_DOMINI
(
  ID                   NUMBER(19)           NOT NULL,
  CODI                 VARCHAR2(64)			NOT NULL,
  NOM                  VARCHAR2(256)	    NOT NULL,
  DESCRIPCIO           VARCHAR2(1024),
  ENTITAT_ID		   NUMBER(19)			NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  META_EXPEDIENT_ID    NUMBER(19)               NOT NULL
);

ALTER TABLE IPA_METAEXP_DOMINI ADD (
  CONSTRAINT IPA_METAEXP_DOMINI_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_METAEXP_DOMINI_MULT_UK UNIQUE (CODI, META_EXPEDIENT_ID));
  
ALTER TABLE IPA_METAEXP_DOMINI ADD (
  CONSTRAINT IPA_METAEXP_METAEXPDOM_FK FOREIGN KEY (META_EXPEDIENT_ID) 
    REFERENCES IPA_METAEXPEDIENT (ID),
  CONSTRAINT IPA_ENTITAT_METEXP_METAEDOM_Fk FOREIGN KEY (ENTITAT_ID) 
    REFERENCES IPA_ENTITAT (ID));
    
ALTER TABLE IPA_EXPEDIENT ADD METAEXPEDIENT_DOMINI_ID NUMBER(19);

ALTER TABLE IPA_EXPEDIENT ADD ( 
	CONSTRAINT IPA_METAEXPDOM_EXPEDIENT_FK FOREIGN KEY (METAEXPEDIENT_DOMINI_ID) 
    	REFERENCES IPA_METAEXP_DOMINI(ID));   

-- #355: MetaDocuments genèrics
ALTER TABLE IPA_METADOCUMENT MODIFY META_EXPEDIENT_ID NUMBER(19,0) NULL;
ALTER TABLE IPA_METADOCUMENT ADD META_DOCUMENT_TIPUS_GEN VARCHAR2(256);

COMMENT ON COLUMN IPA_METADOCUMENT.META_DOCUMENT_TIPUS_GEN 
   IS 'Tipo metadocumento genérico';
 
INSERT INTO IPA_USUARI (CODI,INICIALITZAT,NIF,NOM,EMAIL,VERSION,IDIOMA) VALUES ('sqlupdate',1,'00000000T','SQL Update','sqlupdate@caib.es',0,'CA');

-- ***** CANVIAR VALORS V_ENTITAT_ID I V_CREATEDBY_CODI ***** 
		
DECLARE 
V_METANODE_ID NUMBER(19); 
V_ENTITAT_ID NUMBER(19) := 1; 
V_CREATEDBY_CODI VARCHAR2(64) := 'sqlupdate'; 
BEGIN
    -- Metadocument OTROS / Metanode id
    SELECT hibernate_sequence.NEXTVAL
    INTO V_METANODE_ID
    FROM   dual;
	    -- Create metanode and return id 
		INSERT INTO IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
	    VALUES (V_METANODE_ID,'OTROS','Otros',null,'DOCUMENT',V_ENTITAT_ID,'1','16',V_CREATEDBY_CODI,to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'),V_CREATEDBY_CODI,to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'));
	    -- Create metadocument 
		INSERT INTO IPA_METADOCUMENT (ID,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN) 
		VALUES (V_METANODE_ID,'2','0',NULL,NULL,NULL,NULL, NULL,'0',NULL,NULL,NULL,NULL,'O1','EE99','TD99','0','0','OTROS');

	-- Metadocument ACUSE_RECIBO / Metanode id
    SELECT hibernate_sequence.NEXTVAL
    INTO V_METANODE_ID
    FROM   dual;
   
	   	-- Create metanode and return id
		INSERT INTO IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
	    VALUES (V_METANODE_ID,'ACUSE_RECIBO_NOTIFICACION','Acuse recibo notificación',null,'DOCUMENT',V_ENTITAT_ID,'1','16',V_CREATEDBY_CODI,to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'),V_CREATEDBY_CODI,to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'));
 		-- Create metadocument 
	   	INSERT INTO IPA_METADOCUMENT (ID,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN) 
		VALUES (V_METANODE_ID,'2','0',NULL,NULL,NULL,NULL, NULL,'0',NULL,NULL,NULL,NULL,'O1','EE01','TD09','0','0','ACUSE_RECIBO_NOTIFICACION');

	-- Metadocument NOTIFICACIÓN / Metanode id
    SELECT hibernate_sequence.NEXTVAL
    INTO V_METANODE_ID
    FROM   dual;
   
   		-- Create metanode and return id
		INSERT INTO IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
	    VALUES (V_METANODE_ID,'NOTIFICACION','Notificación',null,'DOCUMENT',V_ENTITAT_ID,'1','16',V_CREATEDBY_CODI,to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'),V_CREATEDBY_CODI,to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'));
	   	
	   	INSERT INTO IPA_METADOCUMENT (ID,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN) 
		VALUES (V_METANODE_ID,'2','0',NULL,NULL,NULL,NULL, NULL,'0',NULL,NULL,NULL,NULL,'O1','EE02','TD07','0','0','NOTIFICACION');
	
	-- Metadocument JUSTIFICANTE_REGISTRO / Metanode id
    SELECT hibernate_sequence.NEXTVAL
    INTO V_METANODE_ID
    FROM   dual;
   
	   	-- Create metanode and return id
		INSERT INTO IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
	    VALUES (V_METANODE_ID,'JUSTIFICANTE_REGISTRO','Justificante registro',null,'DOCUMENT',V_ENTITAT_ID,'1','16',V_CREATEDBY_CODI,to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'),V_CREATEDBY_CODI,to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'));

		INSERT INTO IPA_METADOCUMENT (ID,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN) 
		VALUES (V_METANODE_ID,'2','0',NULL,NULL,NULL,NULL, NULL,'0',NULL,NULL,NULL,NULL,'O1','EE01','TD99','0','0','JUSTIFICANTE_REGISTRO');
	COMMIT;
END;

ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD NOT_ENV_REGISTRE_DATA DATE;
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD NOT_ENV_REGISTRE_NUMERO NUMBER(19);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD NOT_ENV_REGISTRE_NUM_FORMATAT VARCHAR2(50);

ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER ADD NOT_ENV_REGISTRE_DATA DATE;
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER ADD NOT_ENV_REGISTRE_NUMERO NUMBER(19);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER ADD NOT_ENV_REGISTRE_NUM_FORMATAT VARCHAR2(50);

ALTER TABLE IPA_METADOCUMENT RENAME COLUMN PORTAFIRMES_FLUXTIP TO PORTAFIRMES_SEQTIP;
ALTER TABLE IPA_DOCUMENT_ENVIAMENT RENAME COLUMN PF_FLUX_TIPUS TO PF_SEQ_TIPUS;
COMMIT;

ALTER TABLE IPA_METADOCUMENT ADD PORTAFIRMES_FLUXTIP VARCHAR2(256);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD PF_FLUX_TIPUS VARCHAR2(256);

CREATE TABLE IPA_TIPUS_DOCUMENTAL
(
  ID                   NUMBER(19)           NOT NULL,
  CODI                 VARCHAR2(64)			NOT NULL,
  NOM                  VARCHAR2(256)	    NOT NULL,
  ENTITAT_ID           NUMBER(19)           NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64)
);


ALTER TABLE IPA_TIPUS_DOCUMENTAL ADD (
  CONSTRAINT IPA_TIPUS_DOCUMENTAL_PK PRIMARY KEY (ID));

ALTER TABLE IPA_TIPUS_DOCUMENTAL ADD (
  CONSTRAINT IPA_ENTITAT_TIPUS_DOC_Fk FOREIGN KEY (ENTITAT_ID) 
    REFERENCES IPA_ENTITAT (ID));