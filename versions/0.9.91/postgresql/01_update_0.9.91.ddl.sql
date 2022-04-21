-- 934
CREATE TABLE ipa_exp_tasca_comment (id bigserial NOT NULL, exp_tasca_id bigserial NOT NULL, text character varying(1024), createdby_codi character varying(64), createddate TIMESTAMP, lastmodifiedby_codi character varying(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_exp_tasca_comment_pk PRIMARY KEY (id));
ALTER TABLE ipa_exp_tasca_comment ADD CONSTRAINT ipa_exptasca_tascacomment_fk FOREIGN KEY (exp_tasca_id) REFERENCES ipa_expedient_tasca (id);
ALTER TABLE ipa_exp_tasca_comment ADD CONSTRAINT ipa_usucre_tascacomment_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);
ALTER TABLE ipa_exp_tasca_comment ADD CONSTRAINT ipa_usumod_tascacomment_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

grant select, update, insert, delete on ipa_exp_tasca_comment to www_ripea;