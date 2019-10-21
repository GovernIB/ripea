
ALTER TABLE ONLY IPA_ALERTA ADD CONSTRAINT IPA_ALERTA_PK PRIMARY KEY (ID);

ALTER TABLE ONLY IPA_USUARI ADD CONSTRAINT IPA_USUARI_PK PRIMARY KEY (CODI);

ALTER TABLE ONLY IPA_ENTITAT ADD CONSTRAINT IPA_ENTITAT_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_ENTITAT ADD CONSTRAINT IPA_ENTITAT_CODI_UK UNIQUE (CODI);

ALTER TABLE ONLY IPA_METANODE ADD CONSTRAINT IPA_METANODE_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_METANODE ADD CONSTRAINT IPA_METANODE_MULT_UK UNIQUE (ENTITAT_ID, CODI, TIPUS);

ALTER TABLE ONLY IPA_METADADA ADD CONSTRAINT IPA_METADADA_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_METADADA ADD CONSTRAINT IPA_METADADA_METANODE_CODI_UK UNIQUE (META_NODE_ID, CODI);

ALTER TABLE ONLY IPA_METADOCUMENT ADD CONSTRAINT IPA_METADOCUMENT_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_METADOCUMENT ADD CONSTRAINT IPA_METADOC_METAEXP_CODI_UK UNIQUE (META_EXPEDIENT_ID, CODI);

ALTER TABLE ONLY IPA_METAEXPEDIENT ADD CONSTRAINT IPA_METAEXPEDIENT_PK PRIMARY KEY (ID);

ALTER TABLE ONLY IPA_METAEXP_SEQ ADD CONSTRAINT IPA_METAEXP_SEQ_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_METAEXP_SEQ ADD CONSTRAINT IPA_METAEXP_SEQ_MULT_UK UNIQUE (ANIO, META_EXPEDIENT_ID);

ALTER TABLE ONLY IPA_METAEXP_TASCA ADD CONSTRAINT IPA_METAEXP_TASCA_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_METAEXP_TASCA ADD CONSTRAINT IPA_METAEXP_TASCA_MULT_UK UNIQUE (CODI, META_EXPEDIENT_ID);

ALTER TABLE ONLY IPA_INTERESSAT ADD CONSTRAINT IPA_INTERESSAT_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_INTERESSAT ADD CONSTRAINT IPA_INTERESSAT_MULT_UK UNIQUE (EXPEDIENT_ID, DOCUMENT_NUM, NOM, LLINATGE1, LLINATGE2, RAO_SOCIAL, ORGAN_CODI);

ALTER TABLE ONLY IPA_CONTINGUT ADD CONSTRAINT IPA_CONTINGUT_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_CONTINGUT ADD CONSTRAINT IPA_CONTINGUT_MULT_UK UNIQUE (NOM, TIPUS, PARE_ID, ENTITAT_ID, ESBORRAT);

ALTER TABLE ONLY IPA_CONT_MOV ADD CONSTRAINT IPA_CONTMOV_PK PRIMARY KEY (ID);

ALTER TABLE ONLY IPA_CONT_LOG ADD CONSTRAINT IPA_CONT_LOG_PK PRIMARY KEY (ID);

ALTER TABLE ONLY IPA_NODE ADD CONSTRAINT IPA_NODE_PK PRIMARY KEY (ID);

ALTER TABLE ONLY IPA_CARPETA ADD CONSTRAINT IPA_CARPETA_PK PRIMARY KEY (ID);

ALTER TABLE ONLY IPA_DADA ADD CONSTRAINT IPA_DADA_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_DADA ADD CONSTRAINT IPA_DADA_MULT_UK UNIQUE (METADADA_ID, NODE_ID, ORDRE);

ALTER TABLE ONLY IPA_EXPEDIENT ADD CONSTRAINT IPA_EXPEDIENT_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_EXPEDIENT ADD CONSTRAINT IPA_EXPEDIENT_SEQ_UK UNIQUE (METAEXPEDIENT_ID, ANIO, SEQUENCIA);

ALTER TABLE ONLY IPA_EXPEDIENT_REL ADD CONSTRAINT IPA_EXPEDIENT_REL_PK PRIMARY KEY (EXPEDIENT_ID, EXPEDIENT_REL_ID);

ALTER TABLE ONLY IPA_DOCUMENT ADD CONSTRAINT IPA_DOCUMENT_PK PRIMARY KEY (ID);

ALTER TABLE ONLY IPA_DOCUMENT_ENVIAMENT ADD CONSTRAINT IPA_DOCENV_PK PRIMARY KEY (ID);
ALTER TABLE ONLY IPA_DOCUMENT_ENVIAMENT ADD CONSTRAINT IPA_DOCENV_MULT_UK UNIQUE (EXPEDIENT_ID, DOCUMENT_ID, ASSUMPTE, DATA_ENVIAMENT, DTYPE, TIPUS, DESTIN_DOCTIP, DESTIN_DOCNUM);

ALTER TABLE ONLY IPA_DOCUMENT_ENVIAMENT_DOC ADD CONSTRAINT IPA_DOCUMENT_ENVDOC_PK PRIMARY KEY (DOCUMENT_ENVIAMENT_ID, DOCUMENT_ID);

ALTER TABLE ONLY IPA_EXPEDIENT_INTERESSAT ADD CONSTRAINT IPA_EXP_INT_PK PRIMARY KEY (EXPEDIENT_ID, INTERESSAT_ID);

ALTER TABLE ONLY IPA_EXECUCIO_MASSIVA ADD CONSTRAINT IPA_EXECUCIO_MASSIVA_PK PRIMARY KEY (ID);

ALTER TABLE ONLY IPA_MASSIVA_CONTINGUT ADD CONSTRAINT IPA_MASSIVA_CONTINGUT_PK PRIMARY KEY (ID);


ALTER TABLE IPA_ALERTA ADD CONSTRAINT IPA_CONTINGUT_ALERTA_FK FOREIGN KEY (CONTINGUT_ID) REFERENCES IPA_CONTINGUT (ID);
ALTER TABLE IPA_ALERTA ADD CONSTRAINT IPA_USUCRE_ALERTA_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_ALERTA ADD CONSTRAINT IPA_USUMOD_ALERTA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_ENTITAT ADD CONSTRAINT IPA_USUCRE_ENTITAT_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_ENTITAT ADD CONSTRAINT IPA_USUMOD_ENTITAT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_METANODE ADD CONSTRAINT IPA_ENTITAT_METANODE_FK FOREIGN KEY (ENTITAT_ID) REFERENCES IPA_ENTITAT (ID);
ALTER TABLE IPA_METANODE ADD CONSTRAINT IPA_USUCRE_METANODE_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_METANODE ADD CONSTRAINT IPA_USUMOD_METANODE_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_METADADA ADD CONSTRAINT IPA_METANODE_METADADA_FK FOREIGN KEY (META_NODE_ID) REFERENCES IPA_METANODE (ID);
ALTER TABLE IPA_METADADA ADD CONSTRAINT IPA_USUCRE_METADADA_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_METADADA ADD CONSTRAINT IPA_USUMOD_METADADA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_METADOCUMENT ADD CONSTRAINT IPA_METAEXP_METADOC_FK FOREIGN KEY (META_EXPEDIENT_ID) REFERENCES IPA_METAEXPEDIENT (ID);
ALTER TABLE IPA_METADOCUMENT ADD CONSTRAINT IPA_METANODE_METADOC_FK FOREIGN KEY (ID) REFERENCES IPA_METANODE (ID);

ALTER TABLE IPA_METAEXPEDIENT ADD CONSTRAINT IPA_PARE_METAEXP_FK FOREIGN KEY (PARE_ID) REFERENCES IPA_METAEXPEDIENT (ID);
ALTER TABLE IPA_METAEXPEDIENT ADD CONSTRAINT IPA_METANODE_METAEXP_FK FOREIGN KEY (ID) REFERENCES IPA_METANODE (ID);

ALTER TABLE IPA_METAEXP_SEQ ADD CONSTRAINT IPA_METAEXP_METAEXPSEQ_FK FOREIGN KEY (META_EXPEDIENT_ID) REFERENCES IPA_METAEXPEDIENT (ID);
ALTER TABLE IPA_METAEXP_SEQ ADD CONSTRAINT IPA_USUCRE_METAEXPSEQ_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_METAEXP_SEQ ADD CONSTRAINT IPA_USUMOD_METAEXPSEQ_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_METAEXP_TASCA ADD CONSTRAINT IPA_METAEXP_METAEXPTAS_FK FOREIGN KEY (META_EXPEDIENT_ID) REFERENCES IPA_METAEXPEDIENT (ID);
ALTER TABLE IPA_METAEXP_TASCA ADD CONSTRAINT IPA_USUCRE_METAEXPTAS_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_METAEXP_TASCA ADD CONSTRAINT IPA_USUMOD_METAEXPTAS_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_INTERESSAT ADD CONSTRAINT IPA_EXPEDIENT_INTERESSAT_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT (ID);
ALTER TABLE IPA_INTERESSAT ADD CONSTRAINT IPA_REPRESENTANT_INTERESSAT_FK FOREIGN KEY (REPRESENTANT_ID) REFERENCES IPA_INTERESSAT (ID);
ALTER TABLE IPA_INTERESSAT ADD CONSTRAINT IPA_USUCRE_INTERESSAT_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_INTERESSAT ADD CONSTRAINT IPA_USUMOD_INTERESSAT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_CONTINGUT ADD CONSTRAINT IPA_PARE_CONTINGUT_FK FOREIGN KEY (PARE_ID) REFERENCES IPA_CONTINGUT(ID);
ALTER TABLE IPA_CONTINGUT ADD CONSTRAINT IPA_CONTMOV_CONTINGUT_FK FOREIGN KEY (CONTMOV_ID) REFERENCES IPA_CONT_MOV (ID);
ALTER TABLE IPA_CONTINGUT ADD CONSTRAINT IPA_EXPEDIENT_CONTINGUT_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT (ID);
ALTER TABLE IPA_CONTINGUT ADD CONSTRAINT IPA_ENTITAT_CONTINGUT_FK FOREIGN KEY (ENTITAT_ID) REFERENCES IPA_ENTITAT (ID);
ALTER TABLE IPA_CONTINGUT ADD CONSTRAINT IPA_USUCRE_CONTINGUT_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_CONTINGUT ADD CONSTRAINT IPA_USUMOD_CONTINGUT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_CONT_LOG ADD CONSTRAINT IPA_PARE_CONTLOG_FK FOREIGN KEY (PARE_ID) REFERENCES IPA_CONT_LOG (ID);
ALTER TABLE IPA_CONT_LOG ADD CONSTRAINT IPA_CONTMOV_CONTLOG_FK FOREIGN KEY (CONTMOV_ID) REFERENCES IPA_CONT_MOV (ID);
ALTER TABLE IPA_CONT_LOG ADD CONSTRAINT IPA_USUCRE_CONTLOG_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_CONT_LOG ADD CONSTRAINT IPA_USUMOD_CONTLOG_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_CONT_MOV ADD CONSTRAINT IPA_REMITENT_CONTMOV_FK FOREIGN KEY (REMITENT_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_CONT_MOV ADD CONSTRAINT IPA_USUCRE_CONTMOV_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_CONT_MOV ADD CONSTRAINT IPA_USUMOD_CONTMOV_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_NODE ADD CONSTRAINT IPA_CONTINGUT_NODE_FK FOREIGN KEY (ID) REFERENCES IPA_CONTINGUT (ID);
ALTER TABLE IPA_NODE ADD CONSTRAINT IPA_METANODE_NODE_FK FOREIGN KEY (METANODE_ID) REFERENCES IPA_METANODE (ID);

ALTER TABLE IPA_CARPETA ADD CONSTRAINT IPA_CONTINGUT_CARPETA_FK FOREIGN KEY (ID) REFERENCES IPA_CONTINGUT (ID);

ALTER TABLE IPA_DADA ADD CONSTRAINT IPA_NODE_DADA_FK FOREIGN KEY (NODE_ID) REFERENCES IPA_NODE (ID);
ALTER TABLE IPA_DADA ADD CONSTRAINT IPA_METADADA_DADA_FK FOREIGN KEY (METADADA_ID) REFERENCES IPA_METADADA (ID);
ALTER TABLE IPA_DADA ADD CONSTRAINT IPA_USUCRE_DADA_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_DADA ADD CONSTRAINT IPA_USUMOD_DADA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_EXPEDIENT ADD CONSTRAINT IPA_NODE_EXPEDIENT_FK FOREIGN KEY (ID) REFERENCES IPA_NODE (ID);
ALTER TABLE IPA_EXPEDIENT ADD CONSTRAINT IPA_AGAFATPER_EXPEDIENT_FK FOREIGN KEY (AGAFAT_PER_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_EXPEDIENT ADD CONSTRAINT IPA_METAEXP_EXPEDIENT_FK FOREIGN KEY (METAEXPEDIENT_ID) REFERENCES IPA_METAEXPEDIENT(ID));

ALTER TABLE IPA_EXPEDIENT_ESTAT ADD CONSTRAINT IPA_METAEXP_EXPEDIENTESTAT_FK FOREIGN KEY (METAEXPEDIENT_ID) REFERENCES IPA_METAEXPEDIENT(ID);
    
ALTER TABLE IPA_EXPEDIENT_ESTAT ADD CONSTRAINT IPA_EXPEDIENT_ESTAT_PK PRIMARY KEY (ID);

ALTER TABLE IPA_EXPEDIENT ADD CONSTRAINT IPA_EXPESTAT_EXPEDIENT_FK FOREIGN KEY (EXPEDIENT_ESTAT_ID) REFERENCES IPA_EXPEDIENT_ESTAT(ID);

ALTER TABLE IPA_EXPEDIENT_INTERESSAT ADD CONSTRAINT IPA_INTERESSAT_EXPINTER_FK FOREIGN KEY (INTERESSAT_ID) REFERENCES IPA_INTERESSAT (ID);
ALTER TABLE IPA_EXPEDIENT_INTERESSAT ADD CONSTRAINT IPA_EXPEDIENT_EXPINTER_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT (ID);

ALTER TABLE IPA_EXPEDIENT_REL ADD CONSTRAINT IPA_EXPEDIENT_EXPREL_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT (ID);
ALTER TABLE IPA_EXPEDIENT_REL ADD CONSTRAINT IPA_EXPREL_EXPREL_FK FOREIGN KEY (EXPEDIENT_REL_ID) REFERENCES IPA_EXPEDIENT (ID);

ALTER TABLE IPA_DOCUMENT ADD CONSTRAINT IPA_NODE_DOCUMENT_FK FOREIGN KEY (ID) REFERENCES IPA_NODE (ID);

ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD CONSTRAINT IPA_EXPEDIENT_DOCENV_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT (ID);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD CONSTRAINT IPA_DOCUMENT_DOCENV_FK FOREIGN KEY (DOCUMENT_ID) REFERENCES IPA_DOCUMENT (ID);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD CONSTRAINT IPA_USUCRE_DOCENV_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD CONSTRAINT IPA_USUMOD_DOCENV_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DOC ADD CONSTRAINT IPA_DOCENV_DOCENVDOC_FK FOREIGN KEY (DOCUMENT_ENVIAMENT_ID) REFERENCES IPA_DOCUMENT_ENVIAMENT (ID);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DOC ADD CONSTRAINT IPA_DOCUMENT_DOCENVDOC_FK FOREIGN KEY (DOCUMENT_ID) REFERENCES IPA_DOCUMENT (ID);

ALTER TABLE IPA_EXECUCIO_MASSIVA ADD CONSTRAINT IPA_USUCRE_EXMAS_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_EXECUCIO_MASSIVA ADD CONSTRAINT IPA_USUMOD_EXMAS_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);

ALTER TABLE IPA_MASSIVA_CONTINGUT ADD CONSTRAINT IPA_EXMAS_EXMASCON_FK FOREIGN KEY (EXECUCIO_MASSIVA_ID) REFERENCES IPA_EXECUCIO_MASSIVA (ID);
ALTER TABLE IPA_MASSIVA_CONTINGUT ADD CONSTRAINT IPA_CONTINGUT_EXMASCON_FK FOREIGN KEY (CONTINGUT_ID) REFERENCES IPA_CONTINGUT (ID);
ALTER TABLE IPA_MASSIVA_CONTINGUT ADD CONSTRAINT IPA_USUCRE_EXMASCON_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_MASSIVA_CONTINGUT ADD CONSTRAINT IPA_USUMOD_EXMASCON_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);


ALTER TABLE IPA_ACL_CLASS ADD CONSTRAINT IPA_ACL_CLASS_PK PRIMARY KEY (ID);
ALTER TABLE IPA_ACL_CLASS ADD CONSTRAINT IPA_ACL_CLASS_CLASS_UK UNIQUE (CLASS);

ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_ENTRY_PK PRIMARY KEY (ID);
ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_ENTRY_IDENT_ORDER_UK UNIQUE (ACL_OBJECT_IDENTITY, ACE_ORDER);

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_OID_PK PRIMARY KEY (ID);
ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_IOD_CLASS_IDENTITY_UK UNIQUE (OBJECT_ID_CLASS, OBJECT_ID_IDENTITY);

ALTER TABLE IPA_ACL_SID ADD CONSTRAINT IPA_ACL_SID_PK PRIMARY KEY (ID);
ALTER TABLE IPA_ACL_SID ADD CONSTRAINT IPA_ACL_SID_PRINCIPAL_SID_UK UNIQUE (SID, PRINCIPAL);


ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_ENTRY_GRANTING_CK
  CHECK (GRANTING in (true,false));

ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_ENTRY_AUDIT_SUCCESS_CK
  CHECK (AUDIT_SUCCESS in (true,false));

ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_ENTRY_AUDIT_FAILURE_CK
  CHECK (AUDIT_FAILURE in (true,false));

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_OID_ENTRIES_CK
  CHECK (ENTRIES_INHERITING in (true,false));

ALTER TABLE IPA_ACL_SID ADD CONSTRAINT IPA_ACL_SID_PRINCIPAL_CK
  CHECK (PRINCIPAL in (true,false));


ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_OID_ENTRY_FK FOREIGN KEY (ACL_OBJECT_IDENTITY) REFERENCES IPA_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_SID_ENTRY_FK FOREIGN KEY (SID) REFERENCES IPA_ACL_SID (ID);

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_CLASS_OID_FK FOREIGN KEY (OBJECT_ID_CLASS) REFERENCES IPA_ACL_CLASS (ID);

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_PARENT_OID_FK FOREIGN KEY (PARENT_OBJECT) REFERENCES IPA_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_SID_OID_FK FOREIGN KEY (OWNER_SID) REFERENCES IPA_ACL_SID (ID);


ALTER TABLE ONLY IPA_EXP_COMMENT ADD CONSTRAINT IPA_EXP_COMMENT_PK PRIMARY KEY (ID);
  
ALTER TABLE IPA_EXP_COMMENT ADD CONSTRAINT IPA_EXP_EXPCOMMENT_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT (ID);
ALTER TABLE IPA_EXP_COMMENT ADD CONSTRAINT IPA_USUCRE_EXPCOMMENT_FK FOREIGN KEY (CREATEDBY_CODI) REFERENCES IPA_USUARI (CODI);
ALTER TABLE IPA_EXP_COMMENT ADD CONSTRAINT DIS_USUMOD_EXPCOMMENT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) REFERENCES IPA_USUARI (CODI);
  

ALTER TABLE ONLY IPA_EXPEDIENT_PETICIO ADD  CONSTRAINT IPA_EXPEDIENT_PETICIO_PK PRIMARY KEY (ID);
  
ALTER TABLE ONLY IPA_REGISTRE ADD CONSTRAINT IPA_REGISTRE_PK PRIMARY KEY (ID);
  
ALTER TABLE ONLY IPA_REGISTRE_ANNEX ADD CONSTRAINT IPA_REGISTRE_ANNEX_PK PRIMARY KEY (ID);
  
ALTER TABLE ONLY IPA_REGISTRE_INTERESSAT ADD  CONSTRAINT IPA_REGISTRE_INTERESSAT_PK PRIMARY KEY (ID);  
  
  
  
ALTER TABLE IPA_REGISTRE_INTERESSAT ADD CONSTRAINT IPA_REGISTRE_INTERESSAT_FK FOREIGN KEY (REGISTRE_ID) REFERENCES IPA_REGISTRE (ID);
        
ALTER TABLE IPA_REGISTRE_ANNEX ADD CONSTRAINT IPA_REGISTRE_ANNEX_FK FOREIGN KEY (REGISTRE_ID) REFERENCES IPA_REGISTRE (ID);    
    
ALTER TABLE IPA_REGISTRE ADD CONSTRAINT IPA_ENTITAT_REGISTRE_FK FOREIGN KEY (ENTITAT_ID) REFERENCES IPA_ENTITAT (ID);

ALTER TABLE IPA_EXPEDIENT_PETICIO ADD CONSTRAINT IPA_REGISTRE_PETICIO_FK FOREIGN KEY (REGISTRE_ID) REFERENCES IPA_REGISTRE (ID);
    
ALTER TABLE IPA_EXPEDIENT_PETICIO ADD CONSTRAINT DIS_EXPEDIENT_PETICIO_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT (ID);
    
    
ALTER TABLE ONLY IPA_USUARI_VIAFIRMA_RIPEA ADD CONSTRAINT IPA_USERS_VIAFIRMA_RIPEA_PK PRIMARY KEY (ID);
  
ALTER TABLE ONLY IPA_VIAFIRMA_USUARI ADD CONSTRAINT IPA_VIAFIRMA_USERS_PK PRIMARY KEY (CODI);
  
ALTER TABLE ONLY IPA_USUARI_VIAFIRMA_RIPEA ADD CONSTRAINT IPA_VIAFIRMA_USER_FK FOREIGN KEY (VIAFIRMA_USER_CODI) REFERENCES IPA_VIAFIRMA_USUARI (CODI);
        
ALTER TABLE ONLY IPA_USUARI_VIAFIRMA_RIPEA ADD CONSTRAINT IPA_RIPEA_USER_FK FOREIGN KEY (RIPEA_USER_CODI) REFERENCES IPA_USUARI (CODI);    
    
    
ALTER TABLE ONLY IPA_DOCUMENT_ENVIAMENT_DIS ADD CONSTRAINT IPA_DOCUMENT_ENVIAMENT_DIS_PK PRIMARY KEY (ID);
  
ALTER TABLE ONLY IPA_DOCUMENT_ENVIAMENT ADD CONSTRAINT IPA_DOCUMENT_ENVIAMENT_DIS_FK FOREIGN KEY (VF_VIAFIRMA_DISPOSITIU) REFERENCES IPA_DOCUMENT_ENVIAMENT_DIS (ID);

ALTER TABLE IPA_EXPEDIENT_TASCA ADD CONSTRAINT IPA_EXP_TASCA_PK PRIMARY KEY (ID);
ALTER TABLE IPA_EXPEDIENT_TASCA ADD CONSTRAINT IPA_EXP_EXPTASC_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES IPA_EXPEDIENT(ID);
ALTER TABLE IPA_EXPEDIENT_TASCA ADD CONSTRAINT IPA_METAEXPTASCA_EXPTASC_FK FOREIGN KEY (METAEXP_TASCA_ID) REFERENCES IPA_METAEXP_TASCA(ID);
ALTER TABLE IPA_EXPEDIENT_TASCA ADD CONSTRAINT IPA_USUARI_EXPTASC_FK FOREIGN KEY (RESPONSABLE_CODI) REFERENCES IPA_USUARI(CODI);
