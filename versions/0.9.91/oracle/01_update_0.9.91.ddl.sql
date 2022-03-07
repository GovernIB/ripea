-- 934
CREATE TABLE ipa_exp_tasca_comment (id NUMBER(38, 0) NOT NULL, exp_tasca_id NUMBER(38, 0) NOT NULL, text VARCHAR2(1024 CHAR), createdby_codi VARCHAR2(64 CHAR), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64 CHAR), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_exp_tasca_comment_pk PRIMARY KEY (id));
ALTER TABLE ipa_exp_tasca_comment ADD CONSTRAINT ipa_exptasca_tascacomment_fk FOREIGN KEY (exp_tasca_id) REFERENCES ipa_expedient_tasca (id);
ALTER TABLE ipa_exp_tasca_comment ADD CONSTRAINT ipa_usucre_tascacomment_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);
ALTER TABLE ipa_exp_tasca_comment ADD CONSTRAINT ipa_usumod_tascacomment_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

grant select, update, insert, delete on ipa_exp_tasca_comment to www_ripea;