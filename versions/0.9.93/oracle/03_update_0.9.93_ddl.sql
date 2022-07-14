ALTER TABLE ipa_registre_annex ADD val_ok NUMBER(1) DEFAULT '1';
ALTER TABLE ipa_registre_annex ADD val_error VARCHAR2(1000 CHAR);
ALTER TABLE ipa_registre_annex ADD annex_estat VARCHAR2(16 CHAR) DEFAULT 'DEFINITIU';
ALTER TABLE ipa_document ADD val_ok NUMBER(1) DEFAULT '1';
ALTER TABLE ipa_document ADD val_error VARCHAR2(1000 CHAR);
ALTER TABLE ipa_document ADD annex_estat VARCHAR2(16 CHAR) DEFAULT 'DEFINITIU';