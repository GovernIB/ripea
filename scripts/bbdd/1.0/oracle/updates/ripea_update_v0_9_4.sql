
ALTER TABLE IPA_CONTINGUT ADD ARXIU_UUID VARCHAR2(36);
ALTER TABLE IPA_CONTINGUT ADD ARXIU_DATA_ACT TIMESTAMP(6);

ALTER TABLE IPA_CARPETA DROP COLUMN TIPUS;

ALTER TABLE IPA_DOCUMENT ADD FITXER_NOM VARCHAR2(256);
ALTER TABLE IPA_DOCUMENT ADD FITXER_CONTENT_TYPE VARCHAR2(256);
ALTER TABLE IPA_DOCUMENT ADD FITXER_CONTINGUT BLOB;
ALTER TABLE IPA_DOCUMENT ADD VERSIO_DARRERA VARCHAR2(32);
ALTER TABLE IPA_DOCUMENT ADD VERSIO_COUNT NUMBER(10);
UPDATE IPA_DOCUMENT SET VERSIO_COUNT = 1;
UPDATE IPA_DOCUMENT SET VERSIO_DARRERA = '1.0';
ALTER TABLE IPA_DOCUMENT MODIFY VERSIO_DARRERA NOT NULL;
ALTER TABLE IPA_DOCUMENT MODIFY VERSIO_COUNT NOT NULL;

UPDATE IPA_DOCUMENT DOC SET 
DOC.FITXER_NOM = (SELECT DCV.ARXIU_NOM FROM IPA_DOCUMENT_VERSIO DCV WHERE DCV.ID=DOC.VERSIO_DARRERA_ID),
DOC.FITXER_CONTENT_TYPE = (SELECT DCV.ARXIU_CONTENT_TYPE FROM IPA_DOCUMENT_VERSIO DCV WHERE DCV.ID=DOC.VERSIO_DARRERA_ID),
DOC.FITXER_CONTINGUT = (SELECT DCV.ARXIU_CONTINGUT FROM IPA_DOCUMENT_VERSIO DCV WHERE DCV.ID=DOC.VERSIO_DARRERA_ID);

ALTER TABLE IPA_DOCUMENT DROP COLUMN VERSIO_DARRERA_ID;

DROP TABLE IPA_DOCUMENT_VERSIO;
