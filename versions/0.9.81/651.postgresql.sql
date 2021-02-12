ALTER TABLE IPA_EXPEDIENT
ADD (
    ORGAN_GESTOR_ID BIGINT,
    CONSTRAINT IPA_ORGAN_GESTOR_EXP_FK FOREIGN KEY (ORGAN_GESTOR_ID) REFERENCES IPA_ORGAN_GESTOR (ID)
);
UPDATE IPA_EXPEDIENT EXP SET (EXP.ORGAN_GESTOR_ID) = (SELECT MEX.ORGAN_GESTOR_ID FROM IPA_METAEXPEDIENT MEX WHERE MEX.ID = EXP.METAEXPEDIENT_ID);
UPDATE IPA_EXPEDIENT EXP SET (EXP.ORGAN_GESTOR_ID) = (SELECT OGE.ID FROM IPA_CONTINGUT CNT, IPA_ENTITAT ENT, IPA_ORGAN_GESTOR OGE WHERE EXP.ID = CNT.ID AND ENT.ID = CNT.ENTITAT_ID AND OGE.ENTITAT_ID = ENT.ID AND OGE.CODI = ENT.UNITAT_ARREL) WHERE EXP.ORGAN_GESTOR_ID IS NULL;
ALTER TABLE IPA_EXPEDIENT MODIFY ORGAN_GESTOR_ID NOT NULL;