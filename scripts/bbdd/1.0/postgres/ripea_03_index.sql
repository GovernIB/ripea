
CREATE INDEX IPA_CONTINGUT_ALERTA_FK_I ON IPA_ALERTA(CONTINGUT_ID);
CREATE INDEX IPA_USUCRE_ALERTA_FK_I ON IPA_ALERTA(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_ALERTA_FK_I ON IPA_ALERTA(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_USUCRE_ENTITAT_FK_I ON IPA_ENTITAT(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_ENTITAT_FK_I ON IPA_ENTITAT(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_ENTITAT_METANODE_FK_I ON IPA_METANODE(ENTITAT_ID);
CREATE INDEX IPA_USUCRE_METANODE_FK_I ON IPA_METANODE(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_METANODE_FK_I ON IPA_METANODE(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_ENTITAT_METADADA_FK_I ON IPA_METADADA(ENTITAT_ID);
CREATE INDEX IPA_USUCRE_METADADA_FK_I ON IPA_METADADA(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_METADADA_FK_I ON IPA_METADADA(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_PARE_METAEXP_FK_I ON IPA_METAEXPEDIENT(PARE_ID);

CREATE INDEX IPA_METAEXP_METAEXPSEQ_FK_I ON IPA_METAEXP_SEQ(META_EXPEDIENT_ID);
CREATE INDEX IPA_USUCRE_METAEXPSEQ_FK_I ON IPA_METAEXP_SEQ(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_METAEXPSEQ_FK_I ON IPA_METAEXP_SEQ(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_USUCRE_INTERESSAT_FK_I ON IPA_INTERESSAT(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_INTERESSAT_FK_I ON IPA_INTERESSAT(LASTMODIFIEDBY_CODI);
CREATE INDEX IPA_EXPEDIENT_INTERESSAT_FK_I ON IPA_INTERESSAT(EXPEDIENT_ID);
CREATE INDEX IPA_INTERESSAT_INTERESSAT_FK_I ON IPA_INTERESSAT(REPRESENTANT_ID);

CREATE INDEX IPA_PARE_CONTINGUT_FK_I ON IPA_CONTINGUT(PARE_ID);
CREATE INDEX IPA_CONTMOV_CONTINGUT_FK_I ON IPA_CONTINGUT(CONTMOV_ID);
CREATE INDEX IPA_ENTITAT_CONTINGUT_FK_I ON IPA_CONTINGUT(ENTITAT_ID);
CREATE INDEX IPA_USUCRE_CONTINGUT_FK_I ON IPA_CONTINGUT(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_CONTINGUT_FK_I ON IPA_CONTINGUT(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_CONTINGUT_CONTMOV_FK_I ON IPA_CONT_MOV(CONTINGUT_ID);
CREATE INDEX IPA_DEST_CONTMOVEMAIL_FK_I ON IPA_CONT_MOV_EMAIL(DESTINATARI);
CREATE INDEX IPA_ORIGEN_CONTMOV_FK_I ON IPA_CONT_MOV(ORIGEN_ID);
CREATE INDEX IPA_DESTI_CONTMOV_FK_I ON IPA_CONT_MOV(DESTI_ID);
CREATE INDEX IPA_USUCRE_CONTMOV_FK_I ON IPA_CONT_MOV(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_CONTMOV_FK_I ON IPA_CONT_MOV(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_CONTINGUT_CONTLOG_FK_I ON IPA_CONT_LOG(CONTINGUT_ID);
CREATE INDEX IPA_PARE_CONTLOG_FK_I ON IPA_CONT_LOG(PARE_ID);
CREATE INDEX IPA_CONTMOV_CONTLOG_FK_I ON IPA_CONT_LOG(CONTMOV_ID);
CREATE INDEX IPA_USUCRE_CONTLOG_FK_I ON IPA_CONT_LOG(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_CONTLOG_FK_I ON IPA_CONT_LOG(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_USUARI_ESCRIPTORI_FK_I ON IPA_ESCRIPTORI(USUARI_ID);

CREATE INDEX IPA_USUCRE_METAEXPDOC_FK_I ON IPA_METAEXPEDIENT_METADOCUMENT(CREATEDBY_CODI);
CREATE INDEX IPA_METAEXP_METAEXPDOC_FK_I ON IPA_METAEXPEDIENT_METADOCUMENT(METAEXPEDIENT_ID);
CREATE INDEX IPA_USUMOD_METAEXPDOC_FK_I ON IPA_METAEXPEDIENT_METADOCUMENT(LASTMODIFIEDBY_CODI);
CREATE INDEX IPA_METADOC_METAEXPDOC_FK_I ON IPA_METAEXPEDIENT_METADOCUMENT(METADOCUMENT_ID);

CREATE INDEX IPA_METANODE_NODE_FK_I ON IPA_NODE(METANODE_ID);

CREATE INDEX IPA_METADADA_METADADANODE_FK_I ON IPA_METANODE_METADADA(METADADA_ID);
CREATE INDEX IPA_USUCRE_METADADANODE_FK_I ON IPA_METANODE_METADADA(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_METADADANODE_FK_I ON IPA_METANODE_METADADA(LASTMODIFIEDBY_CODI);
CREATE INDEX IPA_METANODE_METADADANODE_FK_I ON IPA_METANODE_METADADA(METANODE_ID);

CREATE INDEX IPA_METADADA_DADA_FK_I ON IPA_DADA(METADADA_ID);
CREATE INDEX IPA_USUCRE_DADA_FK_I ON IPA_DADA(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_DADA_FK_I ON IPA_DADA(LASTMODIFIEDBY_CODI);
CREATE INDEX IPA_NODE_DADA_FK_I ON IPA_DADA(NODE_ID);

CREATE INDEX IPA_ARXIU_EXPEDIENT_FK_I ON IPA_EXPEDIENT(ARXIU_ID);

CREATE INDEX IPA_EXPEDIENT_DOCUMENT_FK_I ON IPA_DOCUMENT(EXPEDIENT_ID);

CREATE INDEX IPA_INTERESSAT_EXPINTER_FK_I ON IPA_EXPEDIENT_INTERESSAT(INTERESSAT_ID);
CREATE INDEX IPA_EXPEDIENT_EXPINTER_FK_I ON IPA_EXPEDIENT_INTERESSAT(EXPEDIENT_ID);

CREATE INDEX IPA_USUCRE_REGISTRE_FK_I ON IPA_REGISTRE(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_REGISTRE_FK_I ON IPA_REGISTRE(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_USUCRE_REGANX_FK_I ON IPA_REGISTRE_ANNEX(CREATEDBY_CODI);
CREATE INDEX IPA_REGISTRE_REGNX_FK_I ON IPA_REGISTRE_ANNEX(REGISTRE_ID);
CREATE INDEX IPA_USUMOD_REGNX_FK_I ON IPA_REGISTRE_ANNEX(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_USUCRE_REGANXFIR_FK_I ON IPA_REGISTRE_ANNEX_FIRMA(CREATEDBY_CODI);
CREATE INDEX IPA_FIRMA_ANNEX_FK_I ON IPA_REGISTRE_ANNEX_FIRMA(ANNEX_ID);
CREATE INDEX IPA_USUMOD_REGANXFIR_FK_I ON IPA_REGISTRE_ANNEX_FIRMA(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_USUCRE_REGINT_FK_I ON IPA_REGISTRE_INTER(CREATEDBY_CODI);
CREATE INDEX IPA_REGISTRE_REGINT_FK_I ON IPA_REGISTRE_INTER(REGISTRE_ID);
CREATE INDEX IPA_REGISTRE_REGRPS_FK_I ON IPA_REGISTRE_INTER(REPRESENTANT_ID);
CREATE INDEX IPA_REGISTRE_REGRPT_FK_I ON IPA_REGISTRE_INTER(REPRESENTAT_ID);
CREATE INDEX IPA_USUMOD_REGINT_FK_I ON IPA_REGISTRE_INTER(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_ENTITAT_REGLA_FK_I ON IPA_REGLA(ENTITAT_ID);
CREATE INDEX IPA_METAEXPEDIENT_REGLA_FK_I ON IPA_REGLA(METAEXPEDIENT_ID);
CREATE INDEX IPA_ARXIU_REGLA_FK_I ON IPA_REGLA(ARXIU_ID);
CREATE INDEX IPA_BUSTIA_REGLA_FK_I ON IPA_REGLA(BUSTIA_ID);
CREATE INDEX IPA_USUCRE_REGLA_FK_I ON IPA_REGLA(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_REGLA_FK_I ON IPA_REGLA(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_ACL_OID_ENTRY_FK_I ON IPA_ACL_ENTRY(ACL_OBJECT_IDENTITY);
CREATE INDEX IPA_ACL_SID_ENTRY_FK_I ON IPA_ACL_ENTRY(SID);

CREATE INDEX IPA_ACL_CLASS_OID_FK_I ON IPA_ACL_OBJECT_IDENTITY(OBJECT_ID_CLASS);
CREATE INDEX IPA_ACL_PARENT_OID_FK_I ON IPA_ACL_OBJECT_IDENTITY(PARENT_OBJECT);

CREATE INDEX IPA_ACL_SID_OID_FK_I ON IPA_ACL_OBJECT_IDENTITY(OWNER_SID);

CREATE INDEX IPA_USUCRE_EXMAS_FK_I ON IPA_EXECUCIO_MASSIVA(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_EXMAS_FK_I ON IPA_EXECUCIO_MASSIVA(LASTMODIFIEDBY_CODI);

CREATE INDEX IPA_USUCRE_EXMASCON_FK_I ON IPA_MASSIVA_CONTINGUT(CREATEDBY_CODI);
CREATE INDEX IPA_USUMOD_EXMASCON_FK_I ON IPA_MASSIVA_CONTINGUT(LASTMODIFIEDBY_CODI);
