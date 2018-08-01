ALTER TABLE IPA_EXPEDIENT ADD AGAFAT_PER_CODI VARCHAR2(64);
ALTER TABLE IPA_EXPEDIENT ADD (
  CONSTRAINT IPA_AGAFATPER_EXPEDIENT_FK FOREIGN KEY (AGAFAT_PER_CODI) 
    REFERENCES IPA_USUARI (CODI));

update ipa_expedient expu set expu.agafat_per_codi = (
    select 
        esc.usuari_id
    from
        ipa_expedient exp,
        ipa_contingut con,
        ipa_escriptori esc
    where
        exp.id = expu.id
    and exp.id = con.id
    and con.pare_id = esc.id
    and con.pare_id in (select esc1.id from ipa_escriptori esc1));    

update ipa_contingut conu
    set conu.pare_id=(
    select 
        exp.arxiu_id
    from
        ipa_expedient exp
    where
        exp.id = conu.id)
where
    conu.id in (
    select 
        exp.id
    from
        ipa_expedient exp);
