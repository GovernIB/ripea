ALTER TABLE IPA_USUARI ADD (
  CONSTRAINT IPA_USUARI_PK PRIMARY KEY (CODI),
  CONSTRAINT IPA_USUARI_NIF_UK UNIQUE (NIF));

ALTER TABLE IPA_ENTITAT ADD (
  CONSTRAINT IPA_ENTITAT_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_ENTITAT_CODI_UK UNIQUE (CODI));

ALTER TABLE IPA_METANODE ADD (
  CONSTRAINT IPA_METANODE_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_METANODE_MULT_UK UNIQUE (ENTITAT_ID, CODI, TIPUS));

ALTER TABLE IPA_METADADA ADD (
  CONSTRAINT IPA_METADADA_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_METADADA_ENTI_CODI_UK UNIQUE (ENTITAT_ID, CODI));

ALTER TABLE IPA_METADOCUMENT ADD (
  CONSTRAINT IPA_METADOCUMENT_PK PRIMARY KEY (ID));

ALTER TABLE IPA_METAEXPEDIENT ADD (
  CONSTRAINT IPA_METAEXPEDIENT_PK PRIMARY KEY (ID));

ALTER TABLE IPA_METAEXP_SEQ ADD (
  CONSTRAINT IPA_METAEXP_SEQ_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_METAEXP_SEQ_MULT_UK UNIQUE (ANIO, META_EXPEDIENT_ID));

ALTER TABLE IPA_INTERESSAT ADD (
  CONSTRAINT IPA_INTERESSAT_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_INTERESSAT_MULT_UK UNIQUE (ENTITAT_ID, NIF, NOM, LLINATGES, IDENTIFICADOR));

ALTER TABLE IPA_ARXIU ADD (
  CONSTRAINT IPA_ARXIU_PK PRIMARY KEY (ID));

ALTER TABLE IPA_CONTENIDOR ADD (
  CONSTRAINT IPA_CONTENIDOR_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_CONTENIDOR_MULT_UK UNIQUE (ENTITAT_ID, NOM, PARE_ID, TIPUS_CONT, ESBORRAT));

ALTER TABLE IPA_CONT_MOV ADD (
  CONSTRAINT IPA_CONTMOV_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_CONTMOV_MULT_UK UNIQUE (CONTENIDOR_ID, DESTI_ID, DATA));

ALTER TABLE IPA_ESCRIPTORI ADD (
  CONSTRAINT IPA_ESCRIPTORI_PK PRIMARY KEY (ID));

ALTER TABLE IPA_METAEXPEDIENT_METADOCUMENT ADD (
  CONSTRAINT IPA_MEXP_MDOC_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_MEXP_MDOC_MULT_UK UNIQUE (METAEXPEDIENT_ID, METADOCUMENT_ID));

ALTER TABLE IPA_NODE ADD (
  CONSTRAINT IPA_NODE_PK PRIMARY KEY (ID));

ALTER TABLE IPA_METANODE_METADADA ADD (
  CONSTRAINT IPA_MNODE_MDADA_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_MNODE_MDADA_MNOD_MDAD_UK UNIQUE (METANODE_ID, METADADA_ID));

ALTER TABLE IPA_CARPETA ADD (
  CONSTRAINT IPA_CARPETA_PK PRIMARY KEY (ID));

ALTER TABLE IPA_DADA ADD (
  CONSTRAINT IPA_DADA_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_DADA_MULT_UK UNIQUE (METADADA_ID, NODE_ID, ORDRE));

ALTER TABLE IPA_EXPEDIENT ADD (
  CONSTRAINT IPA_EXPEDIENT_PK PRIMARY KEY (ID));

ALTER TABLE IPA_DOCUMENT ADD (
  CONSTRAINT IPA_DOCUMENT_PK PRIMARY KEY (ID));

ALTER TABLE IPA_DOCUMENT_VERSIO ADD (
  CONSTRAINT IPA_DOC_VER_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_DOC_VER_DOC_VER_UK UNIQUE (DOCUMENT_ID, VERSIO));

ALTER TABLE IPA_DOCUMENT_PFIRMES ADD (
  CONSTRAINT IPA_DOC_PFIR_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_DOC_PFIR_PFIRID_UK UNIQUE (PFIRMES_ID));
  CONSTRAINT IPA_DOC_PFIR_MULT_UK UNIQUE (DOCUMENT_ID, VERSIO, PFIRMES_ID));

ALTER TABLE IPA_EXPEDIENT_INTERESSAT ADD (
  CONSTRAINT IPA_EXP_INT_PK PRIMARY KEY (EXPEDIENT_ID, INTERESSAT_ID));

ALTER TABLE IPA_BUSTIA ADD (
  CONSTRAINT IPA_BUSTIA_PK PRIMARY KEY (ID));

ALTER TABLE IPA_CONT_LOG ADD (
  CONSTRAINT IPA_CONT_LOG_PK PRIMARY KEY (ID));

ALTER TABLE IPA_REGISTRE ADD (
  CONSTRAINT IPA_REGISTRE_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_REG_MULT_UK UNIQUE (TIPUS, UNITAT_ADM, NUMERO, DATA, OFICINA, LLIBRE));

ALTER TABLE IPA_REGISTRE_ANNEX ADD (
  CONSTRAINT IPA_REGANX_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_REGANX_MULT_UK  UNIQUE (REGISTRE_ID, TITOL, FITXER_NOM, FITXER_TAMANY, TIPUS));

ALTER TABLE IPA_REGISTRE_INTER ADD (
  CONSTRAINT IPA_REGINT_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_REGINT_MULT_UK UNIQUE (REGISTRE_ID, DOC_TIPUS, DOC_NUM, NOM, LLINATGE1, LLINATGE2, RAO_SOCIAL));

ALTER TABLE IPA_REGISTRE_MOV ADD (
  CONSTRAINT IPA_REGMOV_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_REGMOV_MULT_UK UNIQUE (REGISTRE_ID, DESTI_ID, DATA));



ALTER TABLE IPA_ENTITAT ADD (
  CONSTRAINT IPA_USUCRE_ENTITAT_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_ENTITAT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_METANODE ADD (
  CONSTRAINT IPA_ENTITAT_METANODE_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES IPA_ENTITAT (ID),
  CONSTRAINT IPA_USUCRE_METANODE_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_METANODE_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_METADADA ADD (
  CONSTRAINT IPA_ENTITAT_METADADA_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES IPA_ENTITAT (ID),
  CONSTRAINT IPA_USUCRE_METADADA_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_METADADA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_METADOCUMENT ADD (
  CONSTRAINT IPA_METANODE_METADOC_FK FOREIGN KEY (ID) 
    REFERENCES IPA_METANODE (ID));

ALTER TABLE IPA_METAEXPEDIENT ADD (
  CONSTRAINT IPA_PARE_METAEXP_FK FOREIGN KEY (PARE_ID) 
    REFERENCES IPA_METAEXPEDIENT (ID),
  CONSTRAINT IPA_METANODE_METAEXP_FK FOREIGN KEY (ID) 
    REFERENCES IPA_METANODE (ID));

ALTER TABLE IPA_METAEXP_SEQ ADD (
  CONSTRAINT IPA_METAEXP_METAEXPSEQ_FK FOREIGN KEY (META_EXPEDIENT_ID) 
    REFERENCES IPA_METAEXPEDIENT (ID),
  CONSTRAINT IPA_USUCRE_METAEXPSEQ_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_METAEXPSEQ_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_INTERESSAT ADD (
  CONSTRAINT IPA_ENTITAT_INTERESSAT_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES IPA_ENTITAT (ID),
  CONSTRAINT IPA_USUCRE_INTERESSAT_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_INTERESSAT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_ARXIU ADD (
  CONSTRAINT IPA_CONTENIDOR_ARXIU_FK FOREIGN KEY (ID) 
    REFERENCES IPA_CONTENIDOR (ID));

ALTER TABLE IPA_CONTENIDOR ADD (
  CONSTRAINT IPA_CONTMOV_CONTENIDOR_FK FOREIGN KEY (CONTMOV_ID) 
    REFERENCES IPA_CONT_MOV (ID),
  CONSTRAINT IPA_ENTITAT_CONTENIDOR_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES IPA_ENTITAT (ID),
  CONSTRAINT IPA_USUCRE_CONTENIDOR_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_PARE_CONTENIDOR_FK FOREIGN KEY (PARE_ID) 
    REFERENCES IPA_CONTENIDOR (ID),
  CONSTRAINT IPA_USUMOD_CONTENIDOR_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_CONT_MOV ADD (
  CONSTRAINT IPA_CONTENIDOR_CONTMOV_FK FOREIGN KEY (CONTENIDOR_ID) 
    REFERENCES IPA_CONTENIDOR (ID),
  CONSTRAINT IPA_ORIGEN_CONTMOV_FK FOREIGN KEY (ORIGEN_ID) 
    REFERENCES IPA_CONTENIDOR (ID),
  CONSTRAINT IPA_DESTI_CONTMOV_FK FOREIGN KEY (DESTI_ID) 
    REFERENCES IPA_CONTENIDOR (ID),
  CONSTRAINT IPA_REMITENT_CONTMOV_FK FOREIGN KEY (REMITENT_ID) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUCRE_CONTMOV_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_CONTMOV_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_ESCRIPTORI ADD (
  CONSTRAINT IPA_USUARI_ESCRIPTORI_FK FOREIGN KEY (USUARI_ID) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_CONTENIDOR_ESCRIPTORI_FK FOREIGN KEY (ID) 
    REFERENCES IPA_CONTENIDOR (ID));

ALTER TABLE IPA_METAEXPEDIENT_METADOCUMENT ADD (
  CONSTRAINT IPA_USUCRE_METAEXPDOC_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_METAEXP_METAEXPDOC_FK FOREIGN KEY (METAEXPEDIENT_ID) 
    REFERENCES IPA_METAEXPEDIENT (ID),
  CONSTRAINT IPA_USUMOD_METAEXPDOC_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_METADOC_METAEXPDOC_FK FOREIGN KEY (METADOCUMENT_ID) 
    REFERENCES IPA_METADOCUMENT (ID));

ALTER TABLE IPA_NODE ADD (
  CONSTRAINT IPA_CONTENIDOR_NODE_FK FOREIGN KEY (ID) 
    REFERENCES IPA_CONTENIDOR (ID),
  CONSTRAINT IPA_METANODE_NODE_FK FOREIGN KEY (METANODE_ID) 
    REFERENCES IPA_METANODE (ID));

ALTER TABLE IPA_METANODE_METADADA ADD (
  CONSTRAINT IPA_METADADA_METADADANODE_FK FOREIGN KEY (METADADA_ID) 
    REFERENCES IPA_METADADA (ID),
  CONSTRAINT IPA_USUCRE_METADADANODE_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_METADADANODE_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_METANODE_METADADANODE_FK FOREIGN KEY (METANODE_ID) 
    REFERENCES IPA_METANODE (ID));

ALTER TABLE IPA_CARPETA ADD (
  CONSTRAINT IPA_CONTENIDOR_CARPETA_FK FOREIGN KEY (ID) 
    REFERENCES IPA_CONTENIDOR (ID));

ALTER TABLE IPA_DADA ADD (
  CONSTRAINT IPA_METADADA_DADA_FK FOREIGN KEY (METADADA_ID) 
    REFERENCES IPA_METADADA (ID),
  CONSTRAINT IPA_USUCRE_DADA_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_DADA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_NODE_DADA_FK FOREIGN KEY (NODE_ID) 
    REFERENCES IPA_NODE (ID));

ALTER TABLE IPA_EXPEDIENT ADD (
  CONSTRAINT IPA_ARXIU_EXPEDIENT_FK FOREIGN KEY (ARXIU_ID) 
    REFERENCES IPA_ARXIU (ID),
  CONSTRAINT IPA_NODE_EXPEDIENT_FK FOREIGN KEY (ID) 
    REFERENCES IPA_NODE (ID));

ALTER TABLE IPA_DOCUMENT ADD (
  CONSTRAINT IPA_NODE_DOCUMENT_FK FOREIGN KEY (ID) 
    REFERENCES IPA_NODE (ID),
  CONSTRAINT IPA_EXPEDIENT_DOCUMENT_FK FOREIGN KEY (EXPEDIENT_ID) 
    REFERENCES IPA_EXPEDIENT (ID));

ALTER TABLE IPA_DOCUMENT_VERSIO ADD (
  CONSTRAINT IPA_USUCRE_DOCVERSIO_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_DOCVERSIO_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_DOCUMENT_DOCVERSIO_FK FOREIGN KEY (DOCUMENT_ID) 
    REFERENCES IPA_DOCUMENT (ID));

ALTER TABLE IPA_DOCUMENT_PFIRMES ADD (
  CONSTRAINT IPA_USUCRE_DOCPFIR_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_DOCPFIR_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_DOCUMENT_DOCPFIR_FK FOREIGN KEY (DOCUMENT_ID) 
    REFERENCES IPA_DOCUMENT (ID));

ALTER TABLE IPA_EXPEDIENT_INTERESSAT ADD (
  CONSTRAINT IPA_INTERESSAT_EXPINTER_FK FOREIGN KEY (INTERESSAT_ID) 
    REFERENCES IPA_INTERESSAT (ID),
  CONSTRAINT IPA_EXPEDIENT_EXPINTER_FK FOREIGN KEY (EXPEDIENT_ID) 
    REFERENCES IPA_EXPEDIENT (ID));

ALTER TABLE IPA_BUSTIA ADD (
  CONSTRAINT IPA_CONTENIDOR_BUSTIA_FK FOREIGN KEY (ID) 
    REFERENCES IPA_CONTENIDOR (ID));

ALTER TABLE IPA_REGISTRE ADD (
  CONSTRAINT IPA_USUCRE_REGISTRE_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_REGISTRE_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_CONTENIDOR_REGISTRE_FK FOREIGN KEY (CONTENIDOR_ID) 
    REFERENCES IPA_CONTENIDOR (ID));

ALTER TABLE IPA_REGISTRE_ANNEX ADD (
  CONSTRAINT IPA_USUCRE_REGANX_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_REGISTRE_REGANX_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES IPA_REGISTRE (ID),
  CONSTRAINT IPA_USUMOD_REGANX_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_REGISTRE_INTER ADD (
  CONSTRAINT IPA_USUCRE_REGINT_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_REGISTRE_REGINT_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES IPA_REGISTRE (ID),
  CONSTRAINT IPA_INTERESSAT_REPRESENTANT_FK FOREIGN KEY (REPRESENTANT_ID) 
    REFERENCES IPA_REGISTRE_INTER (ID),
  CONSTRAINT IPA_INTERESSAT_REPRESENTAT_FK FOREIGN KEY (REPRESENTAT_ID) 
    REFERENCES IPA_REGISTRE_INTER (ID),
  CONSTRAINT IPA_USUMOD_REGINT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));

ALTER TABLE IPA_REGISTRE_MOV ADD (
  CONSTRAINT IPA_REGISTRE_REGMOV_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES IPA_REGISTRE (ID),
  CONSTRAINT IPA_ORIGEN_REGMOV_FK FOREIGN KEY (ORIGEN_ID) 
    REFERENCES IPA_CONTENIDOR (ID),
  CONSTRAINT IPA_DESTI_REGMOV_FK FOREIGN KEY (DESTI_ID) 
    REFERENCES IPA_CONTENIDOR (ID),
  CONSTRAINT IPA_REMITENT_REGMOV_FK FOREIGN KEY (REMITENT_ID) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUCRE_REGMOV_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI),
  CONSTRAINT IPA_USUMOD_REGMOV_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES IPA_USUARI (CODI));




ALTER TABLE IPA_ACL_CLASS ADD (
  CONSTRAINT IPA_ACL_CLASS_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_ACL_CLASS_CLASS_UK UNIQUE (CLASS));

ALTER TABLE IPA_ACL_ENTRY ADD (
  CONSTRAINT IPA_ACL_ENTRY_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_ACL_ENTRY_IDENT_ORDER_UK UNIQUE (ACL_OBJECT_IDENTITY, ACE_ORDER));

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD (
  CONSTRAINT IPA_ACL_OID_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_ACL_IOD_CLASS_IDENTITY_UK UNIQUE (OBJECT_ID_CLASS, OBJECT_ID_IDENTITY));

ALTER TABLE IPA_ACL_SID ADD (
  CONSTRAINT IPA_ACL_SID_PK PRIMARY KEY (ID),
  CONSTRAINT IPA_ACL_SID_PRINCIPAL_SID_UK UNIQUE (SID, PRINCIPAL));


ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_ENTRY_GRANTING_CK
  CHECK (GRANTING in (1,0));

ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_ENTRY_AUDIT_SUCCESS_CK
  CHECK (AUDIT_SUCCESS in (1,0));

ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_ENTRY_AUDIT_FAILURE_CK
  CHECK (AUDIT_FAILURE in (1,0));

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_OID_ENTRIES_CK
  CHECK (ENTRIES_INHERITING in (1,0));

ALTER TABLE IPA_ACL_SID ADD CONSTRAINT IPA_ACL_SID_PRINCIPAL_CK
  CHECK (PRINCIPAL in (1,0));


ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_OID_ENTRY_FK
  FOREIGN KEY (ACL_OBJECT_IDENTITY)
  REFERENCES IPA_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE IPA_ACL_ENTRY ADD CONSTRAINT IPA_ACL_SID_ENTRY_FK
  FOREIGN KEY (SID)
  REFERENCES IPA_ACL_SID (ID);

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_CLASS_OID_FK
  FOREIGN KEY (OBJECT_ID_CLASS)
  REFERENCES IPA_ACL_CLASS (ID);

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_PARENT_OID_FK
  FOREIGN KEY (PARENT_OBJECT)
  REFERENCES IPA_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE IPA_ACL_OBJECT_IDENTITY ADD CONSTRAINT IPA_ACL_SID_OID_FK
  FOREIGN KEY (OWNER_SID)
  REFERENCES IPA_ACL_SID (ID);
