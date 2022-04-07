-- 940
ALTER TABLE IPA_ACL_CLASS MODIFY CLASS VARCHAR2(100 CHAR);
ALTER TABLE IPA_ACL_SID MODIFY SID VARCHAR2(100 CHAR);
ALTER TABLE IPA_ALERTA MODIFY TEXT VARCHAR2(256 CHAR);
ALTER TABLE IPA_ALERTA MODIFY ERROR VARCHAR2(2048 CHAR);
ALTER TABLE IPA_ALERTA MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_ALERTA MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONFIG MODIFY KEY VARCHAR2(256 CHAR);
ALTER TABLE IPA_CONFIG MODIFY VALUE VARCHAR2(2048 CHAR);
ALTER TABLE IPA_CONFIG MODIFY DESCRIPTION VARCHAR2(2048 CHAR);
ALTER TABLE IPA_CONFIG MODIFY GROUP_CODE VARCHAR2(128 CHAR);
ALTER TABLE IPA_CONFIG MODIFY TYPE_CODE VARCHAR2(128 CHAR);
ALTER TABLE IPA_CONFIG MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONFIG_GROUP MODIFY CODE VARCHAR2(128 CHAR);
ALTER TABLE IPA_CONFIG_GROUP MODIFY PARENT_CODE VARCHAR2(128 CHAR);
ALTER TABLE IPA_CONFIG_GROUP MODIFY DESCRIPTION VARCHAR2(512 CHAR);
ALTER TABLE IPA_CONFIG_TYPE MODIFY CODE VARCHAR2(128 CHAR);
ALTER TABLE IPA_CONFIG_TYPE MODIFY VALUE VARCHAR2(1024 CHAR);
ALTER TABLE IPA_CONTINGUT MODIFY NOM VARCHAR2(1024 CHAR);
ALTER TABLE IPA_CONTINGUT MODIFY ARXIU_UUID VARCHAR2(36 CHAR);
ALTER TABLE IPA_CONTINGUT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONTINGUT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONT_COMMENT MODIFY TEXT VARCHAR2(1024 CHAR);
ALTER TABLE IPA_CONT_COMMENT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONT_COMMENT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONT_LOG MODIFY OBJECTE_ID VARCHAR2(256 CHAR);
ALTER TABLE IPA_CONT_LOG MODIFY PARAM1 VARCHAR2(256 CHAR);
ALTER TABLE IPA_CONT_LOG MODIFY PARAM2 VARCHAR2(256 CHAR);
ALTER TABLE IPA_CONT_LOG MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONT_LOG MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONT_MOV MODIFY REMITENT_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONT_MOV MODIFY COMENTARI VARCHAR2(256 CHAR);
ALTER TABLE IPA_CONT_MOV MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_CONT_MOV MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DADA MODIFY VALOR VARCHAR2(256 CHAR);
ALTER TABLE IPA_DADA MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DADA MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY UBICACIO VARCHAR2(255 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY CUSTODIA_ID VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY CUSTODIA_CSV VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY FITXER_NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY FITXER_CONTENT_TYPE VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY VERSIO_DARRERA VARCHAR2(32 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_VERSION VARCHAR2(5 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_IDENTIF VARCHAR2(48 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_ORGANO VARCHAR2(9 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_ORIGEN VARCHAR2(2 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_ESTELA VARCHAR2(4 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_TIPDOC VARCHAR2(4 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_IDORIG VARCHAR2(48 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_TIPFIR VARCHAR2(4 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_CSV VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NTI_CSVREG VARCHAR2(512 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY DESCRIPCIO VARCHAR2(512 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY GES_DOC_FIRMAT_ID VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY GES_DOC_ADJUNT_ID VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY GES_DOC_ADJUNT_FIRMA_ID VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY NOM_FITXER_FIRMAT VARCHAR2(512 CHAR);
ALTER TABLE IPA_DOCUMENT MODIFY PINBAL_IDPETICION VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY DTYPE VARCHAR2(32 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY ESTAT VARCHAR2(255 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY ASSUMPTE VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY OBSERVACIONS VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY ERROR_DESC VARCHAR2(2048 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY SERVEI_TIPUS VARCHAR2(10 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY NOT_ENV_REF VARCHAR2(100 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY NOT_ENV_DAT_ESTAT VARCHAR2(20 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY NOT_ENV_DAT_ORIG VARCHAR2(20 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY NOT_ENV_CERT_ORIG VARCHAR2(20 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY NOT_ENV_CERT_ARXIUID VARCHAR2(50 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY NOT_ENV_REGISTRE_NUM_FORMATAT VARCHAR2(50 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY NOT_ENV_ID VARCHAR2(100 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY NOTIFICACIO_ESTAT VARCHAR2(255 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY PF_DOC_TIPUS VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY PF_RESPONSABLES VARCHAR2(1024 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY PF_FLUX_ID VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY PF_PORTAFIRMES_ID VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY PF_FLUX_TIPUS VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY PF_MOTIU_REBUIG VARCHAR2(512 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY VF_CODI_USUARI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY VF_TITOL VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY VF_DESCRIPCIO VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY VF_CODI_DISPOSITIU VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY VF_MESSAGE_CODE VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY VF_CONTRASENYA_USUARI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY CODI_APLICACIO VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY CODI_USUARI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY DESCRIPCIO VARCHAR2(255 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY LOCALE VARCHAR2(10 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY ESTAT VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY TOKEN VARCHAR2(255 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY IDENTIFICADOR VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY TIPUS VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY EMAIL_USUARI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY IDENTIFICADOR_NAC VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_DIS MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER MODIFY NOT_ENV_REF VARCHAR2(100 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER MODIFY NOT_ENV_DAT_ESTAT VARCHAR2(20 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER MODIFY NOT_ENV_DAT_ORIG VARCHAR2(20 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER MODIFY NOT_ENV_CERT_ORIG VARCHAR2(20 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER MODIFY ERROR_DESC VARCHAR2(2048 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER MODIFY NOT_ENV_REGISTRE_NUM_FORMATAT VARCHAR2(50 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOMINI MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOMINI MODIFY NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOMINI MODIFY DESCRIPCIO VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOMINI MODIFY CONSULTA VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOMINI MODIFY CADENA VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOMINI MODIFY CONTRASENYA VARCHAR2(256 CHAR);
ALTER TABLE IPA_DOMINI MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_DOMINI MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EMAIL_PENDENT_ENVIAR MODIFY REMITENT VARCHAR2(64 CHAR);
ALTER TABLE IPA_EMAIL_PENDENT_ENVIAR MODIFY DESTINATARI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EMAIL_PENDENT_ENVIAR MODIFY SUBJECT VARCHAR2(1024 CHAR);
ALTER TABLE IPA_EMAIL_PENDENT_ENVIAR MODIFY TEXT VARCHAR2(4000 CHAR);
ALTER TABLE IPA_EMAIL_PENDENT_ENVIAR MODIFY EVENT_TIPUS_ENUM VARCHAR2(64 CHAR);
ALTER TABLE IPA_EMAIL_PENDENT_ENVIAR MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EMAIL_PENDENT_ENVIAR MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_ENTITAT MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_ENTITAT MODIFY NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_ENTITAT MODIFY DESCRIPCIO VARCHAR2(1024 CHAR);
ALTER TABLE IPA_ENTITAT MODIFY CIF VARCHAR2(9 CHAR);
ALTER TABLE IPA_ENTITAT MODIFY UNITAT_ARREL VARCHAR2(9 CHAR);
ALTER TABLE IPA_ENTITAT MODIFY CAPSALERA_COLOR_FONS VARCHAR2(32 CHAR);
ALTER TABLE IPA_ENTITAT MODIFY CAPSALERA_COLOR_LLETRA VARCHAR2(32 CHAR);
ALTER TABLE IPA_ENTITAT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_ENTITAT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXECUCIO_MASSIVA MODIFY TIPUS VARCHAR2(255 CHAR);
ALTER TABLE IPA_EXECUCIO_MASSIVA MODIFY PFIRMES_MOTIU VARCHAR2(256 CHAR);
ALTER TABLE IPA_EXECUCIO_MASSIVA MODIFY PFIRMES_PRIORI VARCHAR2(255 CHAR);
ALTER TABLE IPA_EXECUCIO_MASSIVA MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXECUCIO_MASSIVA MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY TANCAT_MOTIU VARCHAR2(1024 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY CODI VARCHAR2(256 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY NTI_VERSION VARCHAR2(5 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY NTI_IDENTIF VARCHAR2(52 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY NTI_ORGANO VARCHAR2(9 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY NTI_CLASIF_SIA VARCHAR2(30 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY SISTRA_BANTEL_NUM VARCHAR2(16 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY SISTRA_UNITAT_ADM VARCHAR2(9 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY SISTRA_CLAU VARCHAR2(100 CHAR);
ALTER TABLE IPA_EXPEDIENT MODIFY AGAFAT_PER_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_ESTAT MODIFY CODI VARCHAR2(256 CHAR);
ALTER TABLE IPA_EXPEDIENT_ESTAT MODIFY NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_EXPEDIENT_ESTAT MODIFY COLOR VARCHAR2(256 CHAR);
ALTER TABLE IPA_EXPEDIENT_ESTAT MODIFY RESPONSABLE_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_ESTAT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_ESTAT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_FILTRE MODIFY NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_EXPEDIENT_FILTRE MODIFY NUMERO VARCHAR2(256 CHAR);
ALTER TABLE IPA_EXPEDIENT_FILTRE MODIFY ESTAT VARCHAR2(256 CHAR);
ALTER TABLE IPA_EXPEDIENT_FILTRE MODIFY FILTER_NAME VARCHAR2(256 CHAR);
ALTER TABLE IPA_EXPEDIENT_FILTRE MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_FILTRE MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_ORGANPARE MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_ORGANPARE MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_PETICIO MODIFY IDENTIFICADOR VARCHAR2(80 CHAR);
ALTER TABLE IPA_EXPEDIENT_PETICIO MODIFY CLAU_ACCES VARCHAR2(200 CHAR);
ALTER TABLE IPA_EXPEDIENT_PETICIO MODIFY ESTAT VARCHAR2(40 CHAR);
ALTER TABLE IPA_EXPEDIENT_PETICIO MODIFY EXP_PETICIO_ACCIO VARCHAR2(20 CHAR);
ALTER TABLE IPA_EXPEDIENT_PETICIO MODIFY CONSULTA_WS_ERROR_DESC VARCHAR2(4000 CHAR);
ALTER TABLE IPA_EXPEDIENT_PETICIO MODIFY NOTIFICA_DIST_ERROR VARCHAR2(4000 CHAR);
ALTER TABLE IPA_EXPEDIENT_PETICIO MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_PETICIO MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_SEGUIDOR MODIFY SEGUIDOR_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_TASCA MODIFY RESPONSABLE_ACTUAL_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_TASCA MODIFY ESTAT VARCHAR2(20 CHAR);
ALTER TABLE IPA_EXPEDIENT_TASCA MODIFY MOTIU_REBUIG VARCHAR2(1024 CHAR);
ALTER TABLE IPA_EXPEDIENT_TASCA MODIFY COMENTARI VARCHAR2(1024 CHAR);
ALTER TABLE IPA_EXPEDIENT_TASCA MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_TASCA MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXPEDIENT_TASCA_RESP MODIFY RESPONSABLE_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXP_COMMENT MODIFY TEXT VARCHAR2(1024 CHAR);
ALTER TABLE IPA_EXP_COMMENT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_EXP_COMMENT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_GRUP MODIFY ROL VARCHAR2(50 CHAR);
ALTER TABLE IPA_GRUP MODIFY DESCRIPCIO VARCHAR2(512 CHAR);
ALTER TABLE IPA_GRUP MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_GRUP MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_HISTORIC MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_HISTORIC MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_HIST_EXP_INTERESSAT MODIFY INTERESSAT_DOC_NUM VARCHAR2(17 CHAR);
ALTER TABLE IPA_HIST_EXP_USUARI MODIFY USUARI_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY DTYPE VARCHAR2(256 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY NOM VARCHAR2(30 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY LLINATGE1 VARCHAR2(30 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY LLINATGE2 VARCHAR2(30 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY DOCUMENT_TIPUS VARCHAR2(40 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY DOCUMENT_NUM VARCHAR2(17 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY PAIS VARCHAR2(4 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY PROVINCIA VARCHAR2(2 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY MUNICIPI VARCHAR2(5 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY ADRESA VARCHAR2(160 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY CODI_POSTAL VARCHAR2(5 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY EMAIL VARCHAR2(160 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY TELEFON VARCHAR2(20 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY OBSERVACIONS VARCHAR2(160 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY ORGAN_CODI VARCHAR2(9 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY ORGAN_NOM VARCHAR2(80 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY RAO_SOCIAL VARCHAR2(80 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY NOT_IDIOMA VARCHAR2(2 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_INTERESSAT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_MASSIVA_CONTINGUT MODIFY ESTAT VARCHAR2(255 CHAR);
ALTER TABLE IPA_MASSIVA_CONTINGUT MODIFY ERROR VARCHAR2(2046 CHAR);
ALTER TABLE IPA_MASSIVA_CONTINGUT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_MASSIVA_CONTINGUT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METADADA MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METADADA MODIFY NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_METADADA MODIFY DESCRIPCIO VARCHAR2(1024 CHAR);
ALTER TABLE IPA_METADADA MODIFY GLOBAL_MULTIPLICITAT VARCHAR2(255 CHAR);
ALTER TABLE IPA_METADADA MODIFY VALOR VARCHAR2(255 CHAR);
ALTER TABLE IPA_METADADA MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METADADA MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PORTAFIRMES_DOCTIP VARCHAR2(64 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PORTAFIRMES_FLUXID VARCHAR2(64 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PORTAFIRMES_RESPONS VARCHAR2(512 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PORTAFIRMES_SEQTIP VARCHAR2(256 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PORTAFIRMES_CUSTIP VARCHAR2(64 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PASSARELA_CUSTIP VARCHAR2(64 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PLANTILLA_NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PLANTILLA_CONTENT_TYPE VARCHAR2(256 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY GLOBAL_MULTIPLICITAT VARCHAR2(255 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY NTI_ORIGEN VARCHAR2(2 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY NTI_ESTELA VARCHAR2(4 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY NTI_TIPDOC VARCHAR2(4 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY META_DOCUMENT_TIPUS_GEN VARCHAR2(256 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PORTAFIRMES_FLUXTIP VARCHAR2(256 CHAR);
ALTER TABLE IPA_METADOCUMENT MODIFY PINBAL_FINALITAT VARCHAR2(512 CHAR);
ALTER TABLE IPA_METAEXPEDIENT MODIFY CLASIF_SIA VARCHAR2(30 CHAR);
ALTER TABLE IPA_METAEXPEDIENT MODIFY SERIE_DOC VARCHAR2(30 CHAR);
ALTER TABLE IPA_METAEXPEDIENT MODIFY EXPRESSIO_NUMERO VARCHAR2(100 CHAR);
ALTER TABLE IPA_METAEXPEDIENT MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXPEDIENT MODIFY REVISIO_ESTAT VARCHAR2(8 CHAR);
ALTER TABLE IPA_METAEXPEDIENT MODIFY REVISIO_COMENTARI VARCHAR2(1024 CHAR);
ALTER TABLE IPA_METAEXPEDIENT_CARPETA MODIFY NOM VARCHAR2(1024 CHAR);
ALTER TABLE IPA_METAEXPEDIENT_CARPETA MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXPEDIENT_CARPETA MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXPEDIENT_METADOCUMENT MODIFY MULTIPLICITAT VARCHAR2(255 CHAR);
ALTER TABLE IPA_METAEXPEDIENT_METADOCUMENT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXPEDIENT_METADOCUMENT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_COMMENT MODIFY TEXT VARCHAR2(1024 CHAR);
ALTER TABLE IPA_METAEXP_COMMENT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_COMMENT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_ORGAN MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_ORGAN MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_SEQ MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_SEQ MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_TASCA MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_TASCA MODIFY NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_METAEXP_TASCA MODIFY DESCRIPCIO VARCHAR2(1024 CHAR);
ALTER TABLE IPA_METAEXP_TASCA MODIFY RESPONSABLE VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_TASCA MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METAEXP_TASCA MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METANODE MODIFY CODI VARCHAR2(256 CHAR);
ALTER TABLE IPA_METANODE MODIFY NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_METANODE MODIFY DESCRIPCIO VARCHAR2(1024 CHAR);
ALTER TABLE IPA_METANODE MODIFY TIPUS VARCHAR2(256 CHAR);
ALTER TABLE IPA_METANODE MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METANODE MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METANODE_METADADA MODIFY MULTIPLICITAT VARCHAR2(256 CHAR);
ALTER TABLE IPA_METANODE_METADADA MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_METANODE_METADADA MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_ORGAN_GESTOR MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_ORGAN_GESTOR MODIFY NOM VARCHAR2(1000 CHAR);
ALTER TABLE IPA_ORGAN_GESTOR MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_ORGAN_GESTOR MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_PORTAFIRMES_BLOCK MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_PORTAFIRMES_BLOCK MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_PORTAFIRMES_BLOCK_INFO MODIFY PORTAFIRMES_SIGNER_NOM VARCHAR2(50 CHAR);
ALTER TABLE IPA_PORTAFIRMES_BLOCK_INFO MODIFY PORTAFIRMES_SIGNER_CODI VARCHAR2(50 CHAR);
ALTER TABLE IPA_PORTAFIRMES_BLOCK_INFO MODIFY PORTAFIRMES_SIGNER_ID VARCHAR2(9 CHAR);
ALTER TABLE IPA_PORTAFIRMES_BLOCK_INFO MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_PORTAFIRMES_BLOCK_INFO MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY APLICACIO_CODI VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY APLICACIO_VERSIO VARCHAR2(15 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY ASSUMPTE_CODI_CODI VARCHAR2(16 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY ASSUMPTE_CODI_DESC VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY ASSUMPTE_TIPUS_CODI VARCHAR2(16 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY ASSUMPTE_TIPUS_DESC VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY DOC_FISICA_CODI VARCHAR2(1 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY DOC_FISICA_DESC VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY ENTITAT_CODI VARCHAR2(21 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY ENTITAT_DESC VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY EXPEDIENT_NUMERO VARCHAR2(80 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY EXTRACTE VARCHAR2(240 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY PROCEDIMENT_CODI VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY IDENTIFICADOR VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY IDIOMA_CODI VARCHAR2(2 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY IDIOMA_DESC VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY LLIBRE_CODI VARCHAR2(4 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY LLIBRE_DESC VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY OBSERVACIONS VARCHAR2(50 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY OFICINA_CODI VARCHAR2(21 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY OFICINA_DESC VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY ORIGEN_REGISTRE_NUM VARCHAR2(80 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY REF_EXTERNA VARCHAR2(16 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY TRANSPORT_NUM VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY TRANSPORT_TIPUS_CODI VARCHAR2(2 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY TRANSPORT_TIPUS_DESC VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY USUARI_CODI VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY USUARI_NOM VARCHAR2(80 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY DESTI_CODI VARCHAR2(21 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY DESTI_DESCRIPCIO VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY JUSTIFICANT_ARXIU_UUID VARCHAR2(256 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_REGISTRE MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY FIRMA_PERFIL VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY FIRMA_TIPUS VARCHAR2(4 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY NOM VARCHAR2(80 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY NTI_ORIGEN VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY NTI_TIPO_DOC VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY OBSERVACIONS VARCHAR2(50 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY SICRES_TIPO_DOC VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY SICRES_VALIDEZ_DOC VARCHAR2(30 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY TIPUS_MIME VARCHAR2(30 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY TITOL VARCHAR2(200 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY UUID VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY ESTAT VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY ERROR VARCHAR2(4000 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY NTI_ESTADO_ELABORACIO VARCHAR2(50 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY FIRMA_NOM VARCHAR2(80 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_REGISTRE_ANNEX MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY ADRESA VARCHAR2(160 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY CANAL VARCHAR2(30 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY CP VARCHAR2(5 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY DOC_NUMERO VARCHAR2(17 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY DOC_TIPUS VARCHAR2(15 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY EMAIL VARCHAR2(160 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY LLINATGE1 VARCHAR2(30 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY LLINATGE2 VARCHAR2(30 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY MUNICIPI_CODI VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY NOM VARCHAR2(30 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY OBSERVACIONS VARCHAR2(160 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY PAIS_CODI VARCHAR2(4 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY PROVINCIA_CODI VARCHAR2(100 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY RAO_SOCIAL VARCHAR2(80 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY TELEFON VARCHAR2(20 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY TIPUS VARCHAR2(40 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY PAIS VARCHAR2(200 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY PROVINCIA VARCHAR2(200 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY MUNICIPI VARCHAR2(200 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY ORGAN_CODI VARCHAR2(9 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_REGISTRE_INTERESSAT MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_TIPUS_DOCUMENTAL MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_TIPUS_DOCUMENTAL MODIFY NOM VARCHAR2(256 CHAR);
ALTER TABLE IPA_TIPUS_DOCUMENTAL MODIFY CREATEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_TIPUS_DOCUMENTAL MODIFY LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_USUARI MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_USUARI MODIFY NIF VARCHAR2(9 CHAR);
ALTER TABLE IPA_USUARI MODIFY NOM VARCHAR2(200 CHAR);
ALTER TABLE IPA_USUARI MODIFY EMAIL VARCHAR2(200 CHAR);
ALTER TABLE IPA_USUARI MODIFY IDIOMA VARCHAR2(2 CHAR);
ALTER TABLE IPA_USUARI MODIFY ROL_ACTUAL VARCHAR2(64 CHAR);
ALTER TABLE IPA_USUARI_VIAFIRMA_RIPEA MODIFY VIAFIRMA_USER_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_USUARI_VIAFIRMA_RIPEA MODIFY RIPEA_USER_CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_VIAFIRMA_USUARI MODIFY CODI VARCHAR2(64 CHAR);
ALTER TABLE IPA_VIAFIRMA_USUARI MODIFY CONTRASENYA VARCHAR2(64 CHAR);
ALTER TABLE IPA_VIAFIRMA_USUARI MODIFY DESCRIPCIO VARCHAR2(64 CHAR);
