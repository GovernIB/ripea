



--select * from all_constraints where constraint_name = 'fkf787434de9bd5701';

delete from ipa_document_enviament_inter;
delete from ipa_interessat;
delete from ipa_cont_log;
delete from ipa_document_enviament_doc;
delete from ipa_document_enviament;

update ipa_contingut set expedient_id = null;
update ipa_expedient_peticio set expedient_id = null;
update ipa_expedient_tasca set expedient_id = null;
update ipa_expedient_tasca set expedient_id = null;
update ipa_expedient_tasca set expedient_id = null;
update ipa_registre_interessat set registre_id = null;
update ipa_expedient_peticio set registre_id = null;
update ipa_massiva_contingut set contingut_id = null;
update ipa_massiva_contingut set contingut_id = null;
update ipa_historic set metaexpedient_id = null;
update ipa_metaexp_comment set meta_expedient_id = null;


delete from ipa_document;
delete from ipa_expedient_rel;
delete from ipa_expedient_seguidor;
delete from ipa_expedient;
delete from ipa_carpeta;
delete from ipa_dada;
delete from ipa_node;
update ipa_contingut set contmov_id = null;
delete from ipa_cont_mov;
delete from ipa_registre_annex;
delete from ipa_registre;
update ipa_contingut c set c.pare_id = null;
delete from ipa_contingut;



delete from ipa_metaexp_seq;
delete from ipa_metaexpedient_grup;
delete from ipa_metaexpedient;
delete from ipa_metadocument;
delete from ipa_metanode;
delete from ipa_metadada;

delete from ipa_grup;
delete from ipa_organ_gestor;

delete from ipa_registre_interessat;
delete from ipa_exp_tasca_comment;
delete from ipa_hist_exp_usuari;
delete from ipa_massiva_contingut;  
delete from ipa_execucio_massiva;   
delete from ipa_tipus_documental;   
delete from ipa_domini;
delete from ipa_usuari;
delete from ipa_entitat;


delete from ipa_acl_entry;
delete from ipa_acl_object_identity;
delete from ipa_acl_class;
delete from ipa_acl_sid;


delete from ipa_email_pendent_enviar;





delete from ipa_document_enviament_inter;
delete from ipa_interessat;
delete from ipa_cont_log;
delete from ipa_document_enviament_doc;
delete from ipa_document_enviament;

update ipa_contingut set expedient_id = null;
update ipa_expedient_peticio set expedient_id = null;
update ipa_expedient_tasca set expedient_id = null;
update ipa_expedient_tasca set expedient_id = null;
update ipa_expedient_tasca set expedient_id = null;
update ipa_registre_interessat set registre_id = null;
update ipa_expedient_peticio set registre_id = null;
update ipa_massiva_contingut set contingut_id = null;
update ipa_massiva_contingut set contingut_id = null;
update ipa_historic set metaexpedient_id = null;
update ipa_metaexp_comment set meta_expedient_id = null;


delete from ipa_document;
delete from ipa_expedient_rel;
delete from ipa_expedient_seguidor;
delete from ipa_expedient;
delete from ipa_carpeta;
delete from ipa_dada;
delete from ipa_node;
update ipa_contingut set contmov_id = null;
delete from ipa_cont_mov;
delete from ipa_registre_annex;
delete from ipa_registre;
update ipa_contingut c set c.pare_id = null;
delete from ipa_contingut;



delete from ipa_metaexp_seq;
delete from ipa_metaexpedient_grup;
delete from ipa_metaexpedient;
delete from ipa_metadocument;
delete from ipa_metanode;
delete from ipa_metadada;

delete from ipa_grup;
delete from ipa_organ_gestor;

delete from ipa_registre_interessat;
delete from ipa_exp_tasca_comment;
delete from ipa_hist_exp_usuari;
delete from ipa_massiva_contingut;  
delete from ipa_execucio_massiva;   
delete from ipa_tipus_documental;   
delete from ipa_domini;
delete from ipa_usuari;
delete from ipa_entitat;


delete from ipa_acl_entry;
delete from ipa_acl_object_identity;
delete from ipa_acl_class;
delete from ipa_acl_sid;


delete from ipa_email_pendent_enviar;

delete from ipa_historic;
delete from ipa_hist_exp_interessat;
delete from ipa_hist_exp_usuari;
delete from ipa_hist_expedient;
