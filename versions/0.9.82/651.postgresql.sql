CREATE TABLE IPA_METAEXP_ORGAN
(
  ID bigint                NOT NULL,
  META_EXPEDIENT_ID bigint NOT NULL,
  ORGAN_GESTOR_ID  bigint  NOT NULL,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64)
);
ALTER TABLE ONLY IPA_METAEXP_ORGAN ADD CONSTRAINT IPA_METAEXPORG_PK PRIMARY KEY (ID);
ALTER TABLE IPA_METAEXP_ORGAN ADD CONSTRAINT IPA_METAEXP_METAEXPORG_FK FOREIGN KEY (META_EXPEDIENT_ID) REFERENCES IPA_METAEXPEDIENT (ID);
ALTER TABLE IPA_METAEXP_ORGAN ADD CONSTRAINT IPA_ORGAN_METAEXPORG_FK FOREIGN KEY (ORGAN_GESTOR_ID) REFERENCES IPA_ORGAN_GESTOR (ID);
ALTER TABLE IPA_METAEXP_ORGAN ADD CONSTRAINT IPA_USUCRE_METAEXPORG_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_METAEXP_ORGAN ADD CONSTRAINT IPA_USUMOD_METAEXPORG_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_EXPEDIENT
ADD (
    ORGAN_GESTOR_ID BIGINT,
    CONSTRAINT IPA_ORGAN_GESTOR_EXP_FK FOREIGN KEY (ORGAN_GESTOR_ID) REFERENCES IPA_ORGAN_GESTOR (ID)
);
UPDATE IPA_EXPEDIENT EXP SET (EXP.ORGAN_GESTOR_ID) = (SELECT MEX.ORGAN_GESTOR_ID FROM IPA_METAEXPEDIENT MEX WHERE MEX.ID = EXP.METAEXPEDIENT_ID);
UPDATE IPA_EXPEDIENT EXP SET (EXP.ORGAN_GESTOR_ID) = (SELECT OGE.ID FROM IPA_CONTINGUT CNT, IPA_ENTITAT ENT, IPA_ORGAN_GESTOR OGE WHERE EXP.ID = CNT.ID AND ENT.ID = CNT.ENTITAT_ID AND OGE.ENTITAT_ID = ENT.ID AND OGE.CODI = ENT.UNITAT_ARREL) WHERE EXP.ORGAN_GESTOR_ID IS NULL;
ALTER TABLE IPA_EXPEDIENT MODIFY ORGAN_GESTOR_ID NOT NULL;

CREATE TABLE IPA_EXPEDIENT_ORGANPARE
(
    ID                      bigint NOT NULL,
    EXPEDIENT_ID            bigint NOT NULL,
    META_EXPEDIENT_ORGAN_ID bigint NOT NULL,
    CREATEDDATE          timestamp without time zone,
    CREATEDBY_CODI       character varying(64),
    LASTMODIFIEDDATE     timestamp without time zone,
    LASTMODIFIEDBY_CODI  character varying(64)
);
ALTER TABLE ONLY IPA_EXPEDIENT_ORGANPARE ADD CONSTRAINT IPA_EXPORGPARE_PK PRIMARY KEY (ID);
ALTER TABLE IPA_EXPEDIENT_ORGANPARE ADD CONSTRAINT IPA_EXPORGPARE_EXPEDIENT_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT (ID);
ALTER TABLE IPA_EXPEDIENT_ORGANPARE ADD CONSTRAINT IPA_EXPORGPARE_METAEXPORG_FK FOREIGN KEY (META_EXPEDIENT_ORGAN_ID) REFERENCES IPA_METAEXP_ORGAN (ID);
ALTER TABLE IPA_EXPEDIENT_ORGANPARE ADD CONSTRAINT IPA_EXPORGPARE_USUCRE_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_EXPEDIENT_ORGANPARE ADD CONSTRAINT IPA_EXPORGPARE_USUMOD_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);