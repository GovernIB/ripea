CREATE TABLE IPA_EXPEDIENT_PETICIO
(
  ID                   BIGINT               NOT NULL,
  IDENTIFICADOR        character varying (80)             NOT NULL,
  CLAU_ACCES           character varying (200)            NOT NULL,
  DATA_ALTA            timestamp without time zone             NOT NULL,
  ESTAT			       character varying (40)             NOT NULL,
  META_EXPEDIENT_NOM   character varying (256),
  EXP_PETICIO_ACCIO    character varying (20),
  REGISTRE_ID 		   BIGINT,
  CONSULTA_WS_ERROR    boolean,
  CONSULTA_WS_ERROR_DESC  character varying (4000),
  CONSULTA_WS_ERROR_DATE  timestamp without time zone,
  NOTIFICA_DIST_ERROR character varying (4000),
  EXPEDIENT_ID 				BIGINT,
  
  CREATEDDATE         timestamp without time zone,
  LASTMODIFIEDDATE    timestamp without time zone,
  CREATEDBY_CODI      character varying (256),
  LASTMODIFIEDBY_CODI character varying (256)

);


drop table IPA_REGISTRE_ANNEX_FIRMA;
drop table IPA_REGISTRE_ANNEX;
drop table IPA_REGISTRE_INTER;
drop table IPA_REGISTRE;


CREATE TABLE IPA_REGISTRE (
  ID 						BIGINT 		NOT NULL, 
  APLICACIO_CODI 			character varying (20), 
  APLICACIO_VERSIO 			character varying (15), 
  ASSUMPTE_CODI_CODI 		character varying (16), 
  ASSUMPTE_CODI_DESC 		character varying (100), 
  ASSUMPTE_TIPUS_CODI		character varying (16) 	NOT NULL, 
  ASSUMPTE_TIPUS_DESC 		character varying (100), 
  DATA 						timestamp without time zone 	NOT NULL, 
  DOC_FISICA_CODI 			character varying (1), 
  DOC_FISICA_DESC 			character varying (100), 
  ENTITAT_CODI 				character varying (21) 	NOT NULL, 
  ENTITAT_DESC 				character varying (100), 
  EXPEDIENT_NUMERO 			character varying (80), 
  EXPOSA 					character varying (4000), 
  EXTRACTE					character varying (240),
  PROCEDIMENT_CODI			character varying (20),
  IDENTIFICADOR 			character varying (100) 	NOT NULL, 
  IDIOMA_CODI 				character varying (2) 	NOT NULL, 
  IDIOMA_DESC 				character varying (100), 
  LLIBRE_CODI 				character varying (4) 	NOT NULL, 
  LLIBRE_DESC 				character varying (100), 
  OBSERVACIONS 				character varying (50), 
  OFICINA_CODI 				character varying (21) 	NOT NULL, 
  OFICINA_DESC 				character varying (100), 
  ORIGEN_DATA 				timestamp without time zone, 
  ORIGEN_REGISTRE_NUM 		character varying (80), 
  REF_EXTERNA 				character varying (16), 
  SOLICITA 					character varying (4000), 
  TRANSPORT_NUM 			character varying (20), 
  TRANSPORT_TIPUS_CODI 		character varying (2), 
  TRANSPORT_TIPUS_DESC 		character varying (100), 
  USUARI_CODI 				character varying (20), 
  USUARI_NOM 				character varying (80), 
  DESTI_CODI 				character varying (21) 	NOT NULL, 
  DESTI_DESCRIPCIO 			character varying (100),
  ENTITAT_ID				BIGINT 		NOT NULL,

  CREATEDDATE         timestamp without time zone,
  LASTMODIFIEDDATE    timestamp without time zone,
  CREATEDBY_CODI      character varying (256),
  LASTMODIFIEDBY_CODI character varying (256)
);


CREATE TABLE IPA_REGISTRE_ANNEX (
	ID 						BIGINT 		NOT NULL, 
	CONTINGUT BLOB,
	FIRMA_CONTINGUT BLOB,
	FIRMA_PERFIL character varying (4),
	FIRMA_TAMANY integer,
	FIRMA_TIPUS character varying (4),
	NOM character varying (80) NOT NULL,
	NTI_FECHA_CAPTURA timestamp without time zone NOT NULL,
	NTI_ORIGEN character varying (20) NOT NULL,
	NTI_TIPO_DOC character varying (20) NOT NULL,
	OBSERVACIONS character varying (50),
	SICRES_TIPO_DOC character varying (20)  NOT NULL,
	SICRES_VALIDEZ_DOC character varying (30),
	TAMANY integer NOT NULL,
	TIPUS_MIME character varying (30),
	TITOL character varying (200) NOT NULL,
	UUID character varying (100),
	REGISTRE_ID BIGINT NOT NULL,
	ESTAT character varying (20),
	ERROR character varying (4000),
	
	CREATEDDATE         timestamp without time zone,
  	LASTMODIFIEDDATE    timestamp without time zone,
  	CREATEDBY_CODI      character varying (256),
  	LASTMODIFIEDBY_CODI character varying (256)
);


CREATE TABLE IPA_REGISTRE_INTERESSAT (
	ID 						BIGINT 		NOT NULL, 
	ADRESA character varying (160),
	CANAL character varying (30),
	CP character varying (5),
	DOC_NUMERO character varying (17),
	DOC_TIPUS character varying (15),
	EMAIL character varying (160),
	LLINATGE1 character varying (30),
	LLINATGE2 character varying (30),
	MUNICIPI_CODI character varying (100),
	NOM character varying (30),
	OBSERVACIONS character varying (160),
	PAIS_CODI character varying (4),
	PROVINCIA_CODI character varying (100),
	RAO_SOCIAL character varying (80),
	TELEFON character varying (20),
	TIPUS character varying (40) NOT NULL,
	REPRESENTANT_ID BIGINT,
	REGISTRE_ID BIGINT,
	
  CREATEDDATE         timestamp without time zone,
  LASTMODIFIEDDATE    timestamp without time zone,
  CREATEDBY_CODI      character varying (256),
  LASTMODIFIEDBY_CODI character varying (256)
);



ALTER TABLE ONLY IPA_EXPEDIENT_PETICIO ADD  CONSTRAINT IPA_EXPEDIENT_PETICIO_PK PRIMARY KEY (ID);
  
ALTER TABLE ONLY IPA_REGISTRE ADD CONSTRAINT IPA_REGISTRE_PK PRIMARY KEY (ID);
  
ALTER TABLE ONLY IPA_REGISTRE_ANNEX ADD CONSTRAINT IPA_REGISTRE_ANNEX_PK PRIMARY KEY (ID);
  
ALTER TABLE ONLY IPA_REGISTRE_INTERESSAT ADD  CONSTRAINT IPA_REGISTRE_INTERESSAT_PK PRIMARY KEY (ID);  
  
  
  
ALTER TABLE IPA_REGISTRE_INTERESSAT ADD CONSTRAINT IPA_REGISTRE_INTERESSAT_FK FOREIGN KEY (REGISTRE_ID) REFERENCES IPA_REGISTRE (ID);
        
ALTER TABLE IPA_REGISTRE_ANNEX ADD CONSTRAINT IPA_REGISTRE_ANNEX_FK FOREIGN KEY (REGISTRE_ID) REFERENCES IPA_REGISTRE (ID);    
    
ALTER TABLE IPA_REGISTRE ADD CONSTRAINT IPA_ENTITAT_REGISTRE_FK FOREIGN KEY (ENTITAT_ID) REFERENCES IPA_ENTITAT (ID);

ALTER TABLE IPA_EXPEDIENT_PETICIO ADD CONSTRAINT IPA_REGISTRE_PETICIO_FK FOREIGN KEY (REGISTRE_ID) REFERENCES IPA_REGISTRE (ID);
    
ALTER TABLE IPA_EXPEDIENT_PETICIO ADD CONSTRAINT DIS_EXPEDIENT_PETICIO_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT (ID);
    
    
    
    
    
