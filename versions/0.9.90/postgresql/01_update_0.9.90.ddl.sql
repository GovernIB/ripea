-- 917
ALTER TABLE IPA_METADOCUMENT ADD PINBAL_FINALITAT character varying(512);

-- 931
ALTER TABLE ipa_execucio_massiva ADD pfirmes_responsables character varying(256);
ALTER TABLE ipa_execucio_massiva ADD pfirmes_seqtipus character varying(16);
ALTER TABLE ipa_execucio_massiva ADD pfirmes_fluxid character varying(256);
ALTER TABLE ipa_execucio_massiva ADD pfirmes_transid character varying(256);
ALTER TABLE ipa_execucio_massiva ADD rol_actual character varying(32);

-- 885
ALTER TABLE ipa_document_enviament ADD not_emisor_id bigserial;
ALTER TABLE ipa_document_enviament ADD CONSTRAINT ipa_organ_docenv_fk FOREIGN KEY (not_emisor_id) REFERENCES ipa_organ_gestor (id);
