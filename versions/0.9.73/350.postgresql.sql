CREATE TABLE IPA_DOMINI
(
  ID                   BIGSERIAL(19)           			NOT NULL,
  CODI                 CHARACTER VARYING(64)			NOT NULL,
  NOM                  CHARACTER VARYING(256)	    	NOT NULL,
  DESCRIPCIO           CHARACTER VARYING(256),
  CONSULTA             CHARACTER VARYING(256)       	NOT NULL,
  CADENA               CHARACTER VARYING(256)       	NOT NULL,
  USUARI               CHARACTER VARYING(256)       	NOT NULL,
  CONTRASENYA          CHARACTER VARYING(256)       	NOT NULL,
  ENTITAT_ID           BIGSERIAL(19)           			NOT NULL,
  CREATEDDATE          TIMESTAMP WITHOUT TIMEZONE(6),
  CREATEDBY_CODI       CHARACTER VARYING(64),
  LASTMODIFIEDDATE     TIMESTAMP WITHOUT TIMEZONE(6),
  LASTMODIFIEDBY_CODI  CHARACTER VARYING(64)
);


ALTER TABLE ONLY IPA_DOMINI ADD (
  CONSTRAINT IPA_DOMINI_PK PRIMARY KEY (ID));

ALTER TABLE ONLY IPA_DOMINI ADD (
  CONSTRAINT IPA_ENTITAT_DOMINI_Fk FOREIGN KEY (ENTITAT_ID) 
    REFERENCES IPA_ENTITAT (ID));