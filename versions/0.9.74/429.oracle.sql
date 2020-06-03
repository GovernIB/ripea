alter table ipa_registre add (temp_solicita varchar2(4000));
update ipa_registre set temp_solicita = solicita;
update ipa_registre set solicita = null;  
alter table ipa_registre modify solicita long;
alter table ipa_registre modify solicita clob;
update ipa_registre set solicita=temp_solicita;
alter table ipa_registre drop column temp_solicita;


alter table ipa_registre add (temp_exposa varchar2(4000));
update ipa_registre set temp_exposa = exposa;
update ipa_registre set exposa = null;  
alter table ipa_registre modify exposa long;
alter table ipa_registre modify exposa clob;
update ipa_registre set exposa=temp_exposa;
alter table ipa_registre drop column temp_exposa;