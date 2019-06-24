CREATE TABLE IPA_EXPEDIENT_PETICIO
(
  ID                   NUMBER(19)               NOT NULL,
  IDENTIFICADOR        VARCHAR2(80)             NOT NULL,
  CLAU_ACCES           VARCHAR2(200)            NOT NULL,
  DATA_ALTA            TIMESTAMP(6)             NOT NULL,
  ESTAT			       VARCHAR2(40)             NOT NULL,
  META_EXPEDIENT_NOM   VARCHAR2(256),
  EXP_PETICIO_ACCIO    VARCHAR2(20),
  REGISTRE_ID 		   NUMBER(19),
  CONSULTA_WS_ERROR    NUMBER(1),
  CONSULTA_WS_ERROR_DESC  VARCHAR2(4000),
  CONSULTA_WS_ERROR_DATE  TIMESTAMP(6),
  NOTIFICA_DIST_ERROR VARCHAR2(4000),
  EXPEDIENT_ID 				NUMBER(19),
  
  CREATEDDATE         TIMESTAMP(6),
  LASTMODIFIEDDATE    TIMESTAMP(6),
  CREATEDBY_CODI      VARCHAR2(256),
  LASTMODIFIEDBY_CODI VARCHAR2(256)

);


drop table IPA_REGISTRE_ANNEX_FIRMA;
drop table IPA_REGISTRE_ANNEX;
drop table IPA_REGISTRE_INTER;
drop table IPA_REGISTRE;


CREATE TABLE IPA_REGISTRE (
  ID 						NUMBER(19) 		NOT NULL, 
  APLICACIO_CODI 			VARCHAR2(20), 
  APLICACIO_VERSIO 			VARCHAR2(15), 
  ASSUMPTE_CODI_CODI 		VARCHAR2(16), 
  ASSUMPTE_CODI_DESC 		VARCHAR2(100), 
  ASSUMPTE_TIPUS_CODI		VARCHAR2(16) 	NOT NULL, 
  ASSUMPTE_TIPUS_DESC 		VARCHAR2(100), 
  DATA 						TIMESTAMP(6) 	NOT NULL, 
  DOC_FISICA_CODI 			VARCHAR2(1), 
  DOC_FISICA_DESC 			VARCHAR2(100), 
  ENTITAT_CODI 				VARCHAR2(21) 	NOT NULL, 
  ENTITAT_DESC 				VARCHAR2(100), 
  EXPEDIENT_NUMERO 			VARCHAR2(80), 
  EXPOSA 					VARCHAR2(4000), 
  EXTRACTE					VARCHAR2(240),
  PROCEDIMENT_CODI			VARCHAR2(20),
  IDENTIFICADOR 			VARCHAR2(100) 	NOT NULL, 
  IDIOMA_CODI 				VARCHAR2(2) 	NOT NULL, 
  IDIOMA_DESC 				VARCHAR2(100), 
  LLIBRE_CODI 				VARCHAR2(4) 	NOT NULL, 
  LLIBRE_DESC 				VARCHAR2(100), 
  OBSERVACIONS 				VARCHAR2(50), 
  OFICINA_CODI 				VARCHAR2(21) 	NOT NULL, 
  OFICINA_DESC 				VARCHAR2(100), 
  ORIGEN_DATA 				TIMESTAMP(6), 
  ORIGEN_REGISTRE_NUM 		VARCHAR2(80), 
  REF_EXTERNA 				VARCHAR2(16), 
  SOLICITA 					VARCHAR2(4000), 
  TRANSPORT_NUM 			VARCHAR2(20), 
  TRANSPORT_TIPUS_CODI 		VARCHAR2(2), 
  TRANSPORT_TIPUS_DESC 		VARCHAR2(100), 
  USUARI_CODI 				VARCHAR2(20), 
  USUARI_NOM 				VARCHAR2(80), 
  DESTI_CODI 				VARCHAR2(21) 	NOT NULL, 
  DESTI_DESCRIPCIO 			VARCHAR2(100),
  ENTITAT_ID				NUMBER(19) 		NOT NULL,

  CREATEDDATE         TIMESTAMP(6),
  LASTMODIFIEDDATE    TIMESTAMP(6),
  CREATEDBY_CODI      VARCHAR2(256),
  LASTMODIFIEDBY_CODI VARCHAR2(256)
);


CREATE TABLE IPA_REGISTRE_ANNEX (
	ID 						NUMBER(19) 		NOT NULL, 
	CONTINGUT BLOB,
	FIRMA_CONTINGUT BLOB,
	FIRMA_PERFIL VARCHAR2(4),
	FIRMA_TAMANY NUMBER(10),
	FIRMA_TIPUS VARCHAR2(4),
	NOM VARCHAR2(80) NOT NULL,
	NTI_FECHA_CAPTURA TIMESTAMP(6) NOT NULL,
	NTI_ORIGEN VARCHAR2(20) NOT NULL,
	NTI_TIPO_DOC VARCHAR2(20) NOT NULL,
	OBSERVACIONS VARCHAR2(50),
	SICRES_TIPO_DOC VARCHAR2(20)  NOT NULL,
	SICRES_VALIDEZ_DOC VARCHAR2(30),
	TAMANY NUMBER(10) NOT NULL,
	TIPUS_MIME VARCHAR2(30),
	TITOL VARCHAR2(200) NOT NULL,
	UUID VARCHAR2(100),
	REGISTRE_ID NUMBER(19) NOT NULL,
	ESTAT VARCHAR2(20),
	ERROR VARCHAR2(4000),
	NTI_ESTADO_ELABORACIO VARCHAR2(50) NOT NULL,
	
	CREATEDDATE         TIMESTAMP(6),
  	LASTMODIFIEDDATE    TIMESTAMP(6),
  	CREATEDBY_CODI      VARCHAR2(256),
  	LASTMODIFIEDBY_CODI VARCHAR2(256)
);


CREATE TABLE IPA_REGISTRE_INTERESSAT (
	ID 						NUMBER(19) 		NOT NULL, 
	ADRESA VARCHAR2(160),
	CANAL VARCHAR2(30),
	CP VARCHAR2(5),
	DOC_NUMERO VARCHAR2(17),
	DOC_TIPUS VARCHAR2(15),
	EMAIL VARCHAR2(160),
	LLINATGE1 VARCHAR2(30),
	LLINATGE2 VARCHAR2(30),
	MUNICIPI_CODI VARCHAR2(100),
	NOM VARCHAR2(30),
	OBSERVACIONS VARCHAR2(160),
	PAIS_CODI VARCHAR2(4),
	PROVINCIA_CODI VARCHAR2(100),
	RAO_SOCIAL VARCHAR2(80),
	TELEFON VARCHAR2(20),
	TIPUS VARCHAR2(40) NOT NULL,
	REPRESENTANT_ID NUMBER(19),
	REGISTRE_ID NUMBER(19),
	
  CREATEDDATE         TIMESTAMP(6),
  LASTMODIFIEDDATE    TIMESTAMP(6),
  CREATEDBY_CODI      VARCHAR2(256),
  LASTMODIFIEDBY_CODI VARCHAR2(256)
);



ALTER TABLE IPA_EXPEDIENT_PETICIO ADD (
  CONSTRAINT IPA_EXPEDIENT_PETICIO_PK PRIMARY KEY (ID));
  
ALTER TABLE IPA_REGISTRE ADD (
  CONSTRAINT IPA_REGISTRE_PK PRIMARY KEY (ID));
  
ALTER TABLE IPA_REGISTRE_ANNEX ADD (
  CONSTRAINT IPA_REGISTRE_ANNEX_PK PRIMARY KEY (ID));
  
ALTER TABLE IPA_REGISTRE_INTERESSAT ADD (
  CONSTRAINT IPA_REGISTRE_INTERESSAT_PK PRIMARY KEY (ID));  
  
  
ALTER TABLE IPA_REGISTRE_INTERESSAT ADD (
  CONSTRAINT IPA_REGISTRE_INTERESSAT_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES IPA_REGISTRE (ID));
        
ALTER TABLE IPA_REGISTRE_ANNEX ADD (
  CONSTRAINT IPA_REGISTRE_ANNEX_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES IPA_REGISTRE (ID));    
    
ALTER TABLE IPA_REGISTRE ADD (
  CONSTRAINT IPA_ENTITAT_REGISTRE_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES IPA_ENTITAT (ID));

ALTER TABLE IPA_EXPEDIENT_PETICIO ADD (
  CONSTRAINT IPA_REGISTRE_PETICIO_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES IPA_REGISTRE (ID));
    
ALTER TABLE IPA_EXPEDIENT_PETICIO ADD (
  CONSTRAINT DIS_EXPEDIENT_PETICIO_FK FOREIGN KEY (EXPEDIENT_ID) 
    REFERENCES IPA_EXPEDIENT (ID));
