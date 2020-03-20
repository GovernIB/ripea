-- #355: MetaDocuments genèrics
ALTER TABLE IPA_METADOCUMENT MODIFY META_EXPEDIENT_ID BIGSERIAL(19) NULL;
ALTER TABLE IPA_METADOCUMENT ADD META_DOCUMENT_TIPUS_GEN CHARACTER VARYING(256);

COMMENT ON COLUMN IPA_METADOCUMENT.META_DOCUMENT_TIPUS_GEN 
   IS 'Tipo metadocumento genérico';
 

-- ***** DE LA TAULA IPA_METANODE S'HAN DE CANVIAR ELS CAMPS ENTITAT I CREATEDBY_CODI ***** 
		
DECLARE V_METANODE_ID bigserial; 
BEGIN
    -- Metadocument OTROS / Metanode id
    SELECT hibernate_sequence.NEXTVAL
    INTO V_METANODE_ID
    FROM   dual;
	    -- Create metanode and return id 
		INSERT INTO GEST.IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
	    VALUES (V_METANODE_ID,'OTROS','Otros',null,'DOCUMENT','1','1','16','admin',to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'),'admin',to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'));
	    -- Create metadocument 
		INSERT INTO IPA_METADOCUMENT (ID,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN) 
		VALUES (V_METANODE_ID,'2','0',NULL,NULL,NULL,NULL, NULL,'0',NULL,NULL,NULL,NULL,'O1','EE99','TD99','0','0','OTROS');

	-- Metadocument ACUSE_RECIBO / Metanode id
    SELECT hibernate_sequence.NEXTVAL
    INTO V_METANODE_ID
    FROM   dual;
   
	   	-- Create metanode and return id
		INSERT INTO GEST.IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
	    VALUES (V_METANODE_ID,'ACUSE_RECIBO_NOTIFICACION','Acuse recibo notificación',null,'DOCUMENT','1','1','16','admin',to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'),'admin',to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'));
 		-- Create metadocument 
	   	INSERT INTO IPA_METADOCUMENT (ID,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN) 
		VALUES (V_METANODE_ID,'2','0',NULL,NULL,NULL,NULL, NULL,'0',NULL,NULL,NULL,NULL,'O1','EE01','TD09','0','0','ACUSE_RECIBO_NOTIFICACION');

	-- Metadocument NOTIFICACIÓN / Metanode id
    SELECT hibernate_sequence.NEXTVAL
    INTO V_METANODE_ID
    FROM   dual;
   
   		-- Create metanode and return id
		INSERT INTO GEST.IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
	    VALUES (V_METANODE_ID,'NOTIFICACION','Notificación',null,'DOCUMENT','1','1','16','admin',to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'),'admin',to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'));
	   	
	   	INSERT INTO IPA_METADOCUMENT (ID,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN) 
		VALUES (V_METANODE_ID,'2','0',NULL,NULL,NULL,NULL, NULL,'0',NULL,NULL,NULL,NULL,'O1','EE02','TD07','0','0','NOTIFICACION');
	
	-- Metadocument JUSTIFICANTE_REGISTRO / Metanode id
    SELECT hibernate_sequence.NEXTVAL
    INTO V_METANODE_ID
    FROM   dual;
   
	   	-- Create metanode and return id
		INSERT INTO GEST.IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
	    VALUES (V_METANODE_ID,'JUSTIFICANTE_REGISTRO','Justificante registro',null,'DOCUMENT','1','1','16','admin',to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'),'admin',to_timestamp(sysdate,'DD/MM/RR HH24:MI:SSXFF'));

		INSERT INTO IPA_METADOCUMENT (ID,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN) 
		VALUES (V_METANODE_ID,'2','0',NULL,NULL,NULL,NULL, NULL,'0',NULL,NULL,NULL,NULL,'O1','EE01','TD99','0','0','JUSTIFICANTE_REGISTRO');
	COMMIT;
END;