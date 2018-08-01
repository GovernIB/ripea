delete from ipa_interessat;
delete from ipa_cont_log;
delete from ipa_document_enviament_doc;
delete from ipa_document_enviament;

delete from ipa_document;
delete from ipa_expedient_rel;
delete from ipa_expedient;
delete from ipa_carpeta;
delete from ipa_dada;
delete from ipa_node;
delete from ipa_escriptori;
update ipa_contingut set contmov_id = null;
delete from ipa_cont_mov;
delete from ipa_registre_annex;
delete from ipa_registre_inter;
delete from ipa_registre;
delete from ipa_metaexpedient_arxiu;
delete from ipa_regla;
delete from ipa_arxiu;
delete from ipa_bustia;
update ipa_contingut c set c.pare_id = null;
delete from ipa_contingut;

delete from ipa_metaexpedient_metadocument;
delete from ipa_metaexp_seq;
delete from ipa_metaexpedient;
delete from ipa_metadocument;
delete from ipa_metanode_metadada;
delete from ipa_metanode;
delete from ipa_metadada;

delete from ipa_entitat;
delete from ipa_usuari;

delete from ipa_acl_entry;
delete from ipa_acl_object_identity;
delete from ipa_acl_class;
delete from ipa_acl_sid;