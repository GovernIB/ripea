-- #638 ESTRUCTURA DE CARPETES PER DEFECTE PER TIPUS D'EXPEDIENT
CREATE TABLE IPA_METAEXPEDIENT_CARPETA
(
	ID						 NUMBER(19)	        NOT NULL,
  	NOM                      VARCHAR2(1024)     NOT NULL,
	PARE_ID                  NUMBER(19),
    META_EXPEDIENT_ID        NUMBER(19)         NOT NULL,
    CREATEDBY_CODI           VARCHAR2(64),
    CREATEDDATE              TIMESTAMP(6),
    LASTMODIFIEDBY_CODI      VARCHAR2(64),
    LASTMODIFIEDDATE         TIMESTAMP(6),
    VERSION                  NUMBER(19)      NOT NULL
);

-- PK
ALTER TABLE IPA_METAEXPEDIENT_CARPETA ADD (
    CONSTRAINT IPA_METAEXPEDIENT_CARPETA_PK PRIMARY KEY (ID));  
  
-- FK
ALTER TABLE IPA_METAEXPEDIENT_CARPETA ADD (
    CONSTRAINT IPA_METAEXPEDIENT_CARPETA_PARE_FK FOREIGN KEY (PARE_ID) 
    REFERENCES IPA_METAEXPEDIENT_CARPETA(ID));

ALTER TABLE IPA_METAEXPEDIENT_CARPETA ADD (
    CONSTRAINT IPA_METAEXP_METAEXPCARP_FK FOREIGN KEY (META_EXPEDIENT_ID) 
    REFERENCES IPA_METAEXPEDIENT (ID));
    
-- ÍNDEXOS
CREATE INDEX IPA_METAEXPEDIENT_CARPETA_PARE_FK_I ON IPA_METAEXPEDIENT_CARPETA(PARE_ID);
CREATE INDEX IPA_METAEXP_METAEXPCARP_FK_I ON IPA_METAEXPEDIENT_CARPETA(META_EXPEDIENT_ID);

GRANT SELECT, UPDATE, INSERT, DELETE ON IPA_METAEXPEDIENT_CARPETA TO WWW_RIPEA;