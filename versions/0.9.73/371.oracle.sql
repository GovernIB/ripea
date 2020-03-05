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