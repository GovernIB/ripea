ALTER TABLE IPA_EXPEDIENT ADD METAEXPEDIENT_ID BIGINT;
UPDATE IPA_EXPEDIENT EX SET METAEXPEDIENT_ID = (SELECT ND.METANODE_ID FROM IPA_NODE ND WHERE ND.ID = EX.ID);
ALTER TABLE IPA_EXPEDIENT MODIFY METAEXPEDIENT_ID NOT NULL;
ALTER TABLE IPA_EXPEDIENT ADD (
  CONSTRAINT IPA_METAEXP_EXPEDIENT_FK FOREIGN KEY (METAEXPEDIENT_ID) REFERENCES IPA_METAEXPEDIENT(ID));
ALTER TABLE IPA_EXPEDIENT ADD CONSTRAINT IPA_EXPEDIENT_SEQ_UK UNIQUE (METAEXPEDIENT_ID, ANIO, SEQUENCIA);
ALTER TABLE IPA_METAEXPEDIENT ADD EXPRESSIO_NUMERO character varying(100);