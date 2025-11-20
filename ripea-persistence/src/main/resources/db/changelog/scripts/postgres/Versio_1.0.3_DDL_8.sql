ALTER TABLE ipa_interessat_grup_rel DROP CONSTRAINT ipa_interessat_gruprel_inter_fk;
ALTER TABLE ipa_interessat_grup_rel DROP CONSTRAINT ipa_interessat_gruprel_grup_fk;

ALTER TABLE ipa_interessat_grup_rel ADD CONSTRAINT ipa_inter_gruprel_inter_fk FOREIGN KEY (interessat_id) REFERENCES ipa_interessat(id);
ALTER TABLE ipa_interessat_grup_rel ADD CONSTRAINT ipa_inter_gruprel_grup_fk FOREIGN KEY (grup_id) REFERENCES ipa_interessat_grup(id);