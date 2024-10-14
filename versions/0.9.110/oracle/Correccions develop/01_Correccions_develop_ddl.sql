-- 1507
ALTER TABLE ipa_metaexp_tasca MODIFY duracio DEFAULT NULL;
ALTER TABLE ipa_expedient_tasca MODIFY duracio DEFAULT NULL;

-- 1516
ALTER TABLE ipa_expedient ADD prioritat_motiu VARCHAR2(1024 CHAR);