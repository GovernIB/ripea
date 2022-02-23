-- 917
ALTER TABLE IPA_METADOCUMENT ADD PINBAL_FINALITAT VARCHAR2(512);

-- 931
ALTER TABLE ipa_execucio_massiva ADD pfirmes_responsables VARCHAR2(256);
ALTER TABLE ipa_execucio_massiva ADD pfirmes_seqtipus VARCHAR2(16);
ALTER TABLE ipa_execucio_massiva ADD pfirmes_fluxid VARCHAR2(256);
ALTER TABLE ipa_execucio_massiva ADD pfirmes_transid VARCHAR2(256);
ALTER TABLE ipa_execucio_massiva ADD rol_actual VARCHAR2(32);

-- 885
ALTER TABLE ipa_document_enviament ADD not_emisor_id NUMBER(38, 0);
ALTER TABLE ipa_document_enviament ADD CONSTRAINT ipa_organ_docenv_fk FOREIGN KEY (not_emisor_id) REFERENCES ipa_organ_gestor (id);
