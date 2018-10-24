
ALTER TABLE IPA_METADOCUMENT DROP COLUMN GLOBAL_EXPEDIENT;
ALTER TABLE IPA_METADOCUMENT DROP COLUMN GLOBAL_MULTIPLICITAT;
ALTER TABLE IPA_METADOCUMENT DROP COLUMN GLOBAL_READONLY;

ALTER TABLE IPA_METADOCUMENT ADD MULTIPLICITAT NUMBER(2);
ALTER TABLE IPA_METADOCUMENT ADD META_EXPEDIENT_ID NUMBER(19);

ALTER TABLE IPA_METADADA DROP COLUMN GLOBAL_CARPETA;
ALTER TABLE IPA_METADADA DROP COLUMN GLOBAL_DOCUMENT;
ALTER TABLE IPA_METADADA DROP COLUMN GLOBAL_EXPEDIENT;
ALTER TABLE IPA_METADADA DROP COLUMN GLOBAL_MULTIPLICITAT;
ALTER TABLE IPA_METADADA DROP COLUMN GLOBAL_READONLY;
ALTER TABLE IPA_METADADA DROP CONSTRAINT IPA_ENTITAT_METADADA_FK;
ALTER TABLE IPA_METADADA DROP CONSTRAINT IPA_METADADA_ENTI_CODI_UK;
ALTER TABLE IPA_METADADA DROP COLUMN ENTITAT_ID;

ALTER TABLE IPA_METADADA ADD MULTIPLICITAT NUMBER(2);
ALTER TABLE IPA_METADADA ADD META_NODE_ID NUMBER(19);
ALTER TABLE IPA_METADADA ADD ORDRE NUMBER(10);
UPDATE IPA_METADADA SET ORDRE = 0;
ALTER TABLE IPA_METADADA MODIFY ORDRE NOT NULL;
ALTER TABLE IPA_METADADA ADD (
  CONSTRAINT IPA_METANODE_METADADA_FK FOREIGN KEY (META_NODE_ID) 
    REFERENCES IPA_METANODE (ID));
ALTER TABLE IPA_METADADA ADD READ_ONLY NUMBER(1);
UPDATE IPA_METADADA SET READ_ONLY = 0;
ALTER TABLE IPA_METADADA MODIFY READ_ONLY NOT NULL;

DROP TABLE IPA_METAEXPEDIENT_METADOCUMENT;
DROP TABLE IPA_METANODE_METADADA;