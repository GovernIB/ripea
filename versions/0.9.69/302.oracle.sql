-- #302
ALTER TABLE IPA_METADOCUMENT ADD FIRMA_BIOMETRICA NUMBER(1,0);
ALTER TABLE IPA_METADOCUMENT ADD BIOMETRICA_LECTURA NUMBER(1,0);

UPDATE IPA_METADOCUMENT SET FIRMA_BIOMETRICA = 0;
UPDATE IPA_METADOCUMENT SET BIOMETRICA_LECTURA = 0;

CREATE TABLE IPA_VIAFIRMA_USUARI (
	CODI			VARCHAR2(64)			NOT NULL,
	CONTRASENYA		VARCHAR2(64)			NOT NULL,
	DESCRIPCIO		VARCHAR2(64)			NOT NULL
);

CREATE TABLE IPA_USUARI_VIAFIRMA_RIPEA (
    ID                    NUMBER(19)            NOT NULL,
    VIAFIRMA_USER_CODI    VARCHAR2(64)          NOT NULL,
    RIPEA_USER_CODI       VARCHAR2(64)          NOT NULL
);

ALTER TABLE IPA_USUARI_VIAFIRMA_RIPEA ADD (
  CONSTRAINT IPA_USERS_VIAFIRMA_RIPEA_PK PRIMARY KEY (ID));
  
ALTER TABLE IPA_VIAFIRMA_USUARI ADD (
  CONSTRAINT IPA_VIAFIRMA_USERS_PK PRIMARY KEY (CODI));
  
ALTER TABLE IPA_USUARI_VIAFIRMA_RIPEA ADD (
    CONSTRAINT IPA_VIAFIRMA_USER_FK FOREIGN KEY (VIAFIRMA_USER_CODI) 
        REFERENCES IPA_VIAFIRMA_USUARI (CODI));
        
ALTER TABLE IPA_USUARI_VIAFIRMA_RIPEA ADD (
    CONSTRAINT IPA_RIPEA_USER_FK FOREIGN KEY (RIPEA_USER_CODI) 
        REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_CODI_USUARI VARCHAR2(64);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_TITOL VARCHAR2(256);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_DESCRIPCIO VARCHAR2(256);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_CODI_DISPOSITIU VARCHAR2(64);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_MESSAGE_CODE VARCHAR2(64);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_CALLBACK_ESTAT NUMBER(10,0);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_CONTRASENYA_USUARI VARCHAR2(64);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_LECTURA_OBLIGATORIA NUMBER(1,0);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_VIAFIRMA_DISPOSITIU number(19);

UPDATE IPA_DOCUMENT_ENVIAMENT SET VF_LECTURA_OBLIGATORIA = 0;

CREATE TABLE IPA_DOCUMENT_ENVIAMENT_DIS (
    ID                      NUMBER(19)               NOT NULL,
    CODI                    VARCHAR2(64),
    CODI_APLICACIO          VARCHAR2(64),
    CODI_USUARI             VARCHAR2(64),
    DESCRIPCIO              VARCHAR2(255),
    LOCALE                  VARCHAR2(10),
    ESTAT                   VARCHAR2(64),
    TOKEN                   VARCHAR2(255),
    IDENTIFICADOR           VARCHAR2(64),
    TIPUS                   VARCHAR2(64),
    EMAIL_USUARI            VARCHAR2(64),
    IDENTIFICADOR_NAC       VARCHAR2(64),
    CREATEDDATE             TIMESTAMP(6),
    LASTMODIFIEDDATE        TIMESTAMP(6),
    CREATEDBY_CODI          VARCHAR2(256),
    LASTMODIFIEDBY_CODI     VARCHAR2(256)
);

ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS ADD (
  CONSTRAINT IPA_DOCUMENT_ENVIAMENT_DIS_PK PRIMARY KEY (ID));
  
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD (
  CONSTRAINT IPA_DOCUMENT_ENVIAMENT_DIS_FK FOREIGN KEY (VF_VIAFIRMA_DISPOSITIU) 
    REFERENCES IPA_DOCUMENT_ENVIAMENT_DIS (ID));
    
---- Relacionar usuaris ViaFirma amb un usuari de Ripea  
---- Usuaris viaFirma
--INSERT INTO IPA_VIAFIRMA_USUARI VALUES ('usuari','contrasenya','Usuari recursos humans');
---- Relacionar usuaris viaFirma amb usuari Ripea
--INSERT INTO IPA_USUARI_VIAFIRMA_RIPEA VALUES (0,'usuariViaFirma', 'usuariRipea');