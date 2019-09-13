-- #302
ALTER TABLE IPA_METADOCUMENT ADD FIRMA_BIOMETRICA BOOLEAN;
ALTER TABLE IPA_METADOCUMENT ADD BIOMETRICA_LECTURA BOOLEAN;

UPDATE IPA_METADOCUMENT SET FIRMA_BIOMETRICA = 0;
UPDATE IPA_METADOCUMENT SET BIOMETRICA_LECTURA = 0;

CREATE TABLE IPA_VIAFIRMA_USUARI (
	CODI			CHARACTER VARYING(64)			NOT NULL,
	CONTRASENYA		CHARACTER VARYING(64)			NOT NULL,
	DESCRIPCIO		CHARACTER VARYING(64)			NOT NULL
);

CREATE TABLE IPA_USUARI_VIAFIRMA_RIPEA (
    ID                    BIGINT            			NOT NULL,
    VIAFIRMA_USER_CODI    CHARACTER VARYING(64)         NOT NULL,
    RIPEA_USER_CODI       CHARACTER VARYING(64)         NOT NULL
);

ALTER TABLE ONLY IPA_USUARI_VIAFIRMA_RIPEA ADD (
  CONSTRAINT IPA_USERS_VIAFIRMA_RIPEA_PK PRIMARY KEY (ID));
  
ALTER TABLE ONLY IPA_VIAFIRMA_USUARI ADD (
  CONSTRAINT IPA_VIAFIRMA_USERS_PK PRIMARY KEY (CODI));
  
ALTER TABLE ONLY IPA_USUARI_VIAFIRMA_RIPEA ADD (
    CONSTRAINT IPA_VIAFIRMA_USER_FK FOREIGN KEY (VIAFIRMA_USER_CODI) 
        REFERENCES IPA_VIAFIRMA_USUARI (CODI));
        
ALTER TABLE ONLY IPA_USUARI_VIAFIRMA_RIPEA ADD (
    CONSTRAINT IPA_RIPEA_USER_FK FOREIGN KEY (RIPEA_USER_CODI) 
        REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_CODI_USUARI CHARACTER VARYING(64);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_TITOL CHARACTER VARYING(256);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_DESCRIPCIO CHARACTER VARYING(256);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_CODI_DISPOSITIU CHARACTER VARYING(64);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_MESSAGE_CODE CHARACTER VARYING(64);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_CALLBACK_ESTAT NUMBER(10,0);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_CONTRASENYA_USUARI CHARACTER VARYING(64);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_LECTURA_OBLIGATORIA BOOLEAN;
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_VIAFIRMA_DISPOSITIU BIGINT;

UPDATE IPA_DOCUMENT_ENVIAMENT SET VF_LECTURA_OBLIGATORIA = 0;

CREATE TABLE IPA_DOCUMENT_ENVIAMENT_DIS (
    ID                      BIGINT               NOT NULL,
    CODI                    CHARACTER VARYING(64),
    CODI_APLICACIO          CHARACTER VARYING(64),
    CODI_USUARI             CHARACTER VARYING(64),
    DESCRIPCIO              CHARACTER VARYING(255),
    LOCALE                  CHARACTER VARYING(10),
    ESTAT                   CHARACTER VARYING(64),
    TOKEN                   CHARACTER VARYING(255),
    IDENTIFICADOR           CHARACTER VARYING(64),
    TIPUS                   CHARACTER VARYING(64),
    EMAIL_USUARI            CHARACTER VARYING(64),
    IDENTIFICADOR_NAC       CHARACTER VARYING(64),
    CREATEDDATE             timestamp without time zone,
    LASTMODIFIEDDATE        timestamp without time zone,
    CREATEDBY_CODI          CHARACTER VARYING(256),
    LASTMODIFIEDBY_CODI     CHARACTER VARYING(256)
);

ALTER TABLE ONLY IPA_DOCUMENT_ENVIAMENT_DIS ADD (
  CONSTRAINT IPA_DOCUMENT_ENVIAMENT_DIS_PK PRIMARY KEY (ID));
  
ALTER TABLE ONLY IPA_DOCUMENT_ENVIAMENT ADD (
  CONSTRAINT IPA_DOCUMENT_ENVIAMENT_DIS_FK FOREIGN KEY (VF_VIAFIRMA_DISPOSITIU) 
    REFERENCES IPA_DOCUMENT_ENVIAMENT_DIS (ID));
    
-- Relacionar usuaris ViaFirma amb un usuari de Ripea  
-- Usuaris viaFirma
INSERT INTO IPA_VIAFIRMA_USUARI VALUES ('usuari','contrasenya','Usuari recursos humans');
-- Relacionar usuaris viaFirma amb usuari Ripea
INSERT INTO IPA_USUARI_VIAFIRMA_RIPEA VALUES (0,'usuariViaFirma', 'usuariRipea');