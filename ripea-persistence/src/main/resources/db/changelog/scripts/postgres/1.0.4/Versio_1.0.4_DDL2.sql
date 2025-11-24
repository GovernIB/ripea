--1732 Migrar accions massives a REACT
ALTER TABLE ipa_document ADD COLUMN meta_document_id NUMERIC(38,0);
ALTER TABLE ipa_document ADD CONSTRAINT ipa_doc_metadoc_fk FOREIGN KEY (meta_document_id) REFERENCES ipa_metadocument(id);