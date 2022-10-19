delete from ipa_document_enviament where document_id in (select id from ipa_contingut where ipa_contingut.expedient_id = ID_EXPEDIENT);
delete from ipa_dada where node_id in (select id from ipa_contingut where ipa_contingut.expedient_id = ID_EXPEDIENT);
delete from ipa_document where id in (select id from ipa_contingut where ipa_contingut.expedient_id = ID_EXPEDIENT);
delete from ipa_node where id in (select id from ipa_contingut where ipa_contingut.expedient_id = ID_EXPEDIENT);
delete from ipa_contingut where expedient_id = ID_EXPEDIENT;

delete from ipa_alerta where contingut_id = ID_EXPEDIENT;
delete from ipa_dada where node_id = ID_EXPEDIENT;
delete from ipa_interessat where expedient_id = ID_EXPEDIENT;
delete from ipa_expedient_organpare where expedient_id = ID_EXPEDIENT;
delete from ipa_expedient where id=ID_EXPEDIENT;
delete from ipa_node where id=ID_EXPEDIENT;
delete from ipa_contingut where id=ID_EXPEDIENT;