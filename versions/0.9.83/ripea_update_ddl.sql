-- #694 Nou perfil Revisor de tipus d'expedient
ALTER TABLE IPA_METAEXPEDIENT
ADD (
    REVISIO_ESTAT VARCHAR2(8 CHAR),
    REVISIO_COMENTARI VARCHAR2(1024 CHAR)
);

-- #745 Canvis/millores en integració amb viafirma (PUNT2)
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_VALIDATE_CODE_ENABLED NUMBER(1);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_VALIDATE_CODE VARCHAR2(100 CHAR);

-- #756 Al firmar en ViaFirma hauria d'enviar un correu de confirmació si així ho marquen en el formulari
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD VF_REBRE_CORREU NUMBER(1);

-- #767 Filtrar anotacions pendents per tipus d'expedient
ALTER TABLE ipa_expedient_peticio DROP COLUMN meta_expedient_nom;

ALTER TABLE ipa_expedient_peticio ADD metaexpedient_id NUMBER(19);

ALTER TABLE ipa_expedient_peticio ADD CONSTRAINT ipa_exp_pet_metaexp_fk FOREIGN KEY (metaexpedient_id) REFERENCES ipa_metaexpedient (id);
