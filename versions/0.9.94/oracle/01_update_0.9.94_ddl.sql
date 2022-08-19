-- Changeset db/changelog/changes/0.9.94/945.yaml::11651146740000-3::limit
CREATE TABLE IPA_PROCESSOS_INICIALS (ID NUMBER(19, 0) NOT NULL, CODI VARCHAR2(100 CHAR) NOT NULL, INIT NUMBER(1, 0) NOT NULL, CONSTRAINT IPA_PROCESSOS_INICIALS_PK PRIMARY KEY (ID));

ALTER TABLE ipa_config ADD entitat_codi VARCHAR2(64 CHAR);
ALTER TABLE ipa_config ADD configurable NUMBER(1, 0) DEFAULT '0';

-- Changeset db/changelog/changes/0.9.94/1111.yaml::1651146740001-1::limit
ALTER TABLE ipa_expedient_peticio ADD pendent_canvi_estat_dis NUMBER(1, 0) DEFAULT '0';
ALTER TABLE ipa_expedient_peticio ADD reintents_canvi_estat_dis NUMBER(10, 0) DEFAULT '0';

-- Changeset db/changelog/changes/0.9.94/1119.yaml::1659009409606-1::limit
ALTER TABLE ipa_hist_exp_interessat MODIFY interessat_doc_num NULL;