
CREATE TABLE IPA_ALERTA
(
  ID                   	NUMBER(19)             	NOT NULL,
  TEXT                 	VARCHAR2(256)           NOT NULL,
  ERROR                	VARCHAR2(2048),
  LLEGIDA              	NUMBER(1)               NOT NULL,
  CONTINGUT_ID         	NUMBER(19),
  CREATEDBY_CODI       	VARCHAR2(64),
  CREATEDDATE          	TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64),
  LASTMODIFIEDDATE     	TIMESTAMP(6)
);

CREATE TABLE IPA_USUARI
(
  CODI          		VARCHAR2(64)            NOT NULL,
  INICIALITZAT  		NUMBER(1),
  NIF           		VARCHAR2(9),
  NOM           		VARCHAR2(200),
  EMAIL         		VARCHAR2(200),
  IDIOMA				VARCHAR2(2) 			NOT NULL,
  VERSION       		NUMBER(19)              NOT NULL,
  EMAILS_AGRUPATS NUMBER(1) DEFAULT 1
);

CREATE TABLE IPA_EMAIL_PENDENT_ENVIAR
(
    ID NUMBER(19) NOT NULL,
    REMITENT VARCHAR2(64) NOT NULL,
    DESTINATARI VARCHAR2(64) NOT NULL,
    SUBJECT VARCHAR2(1024) NOT NULL,
    TEXT VARCHAR2(4000) NOT NULL,
    EVENT_TIPUS_ENUM VARCHAR2(64) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64),
    CREATEDDATE TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64),
    LASTMODIFIEDDATE TIMESTAMP(6)
);


CREATE TABLE IPA_ENTITAT
(
  ID                   NUMBER(19)               NOT NULL,
  CODI                 VARCHAR2(64)             NOT NULL,
  NOM                  VARCHAR2(256)            NOT NULL,
  DESCRIPCIO           VARCHAR2(1024),
  CIF                  VARCHAR2(9)              NOT NULL,
  UNITAT_ARREL         VARCHAR2(9)              NOT NULL,
  ACTIVA               NUMBER(1),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LOGO_IMG BLOB,
  CAPSALERA_COLOR_FONS VARCHAR2(32),
  CAPSALERA_COLOR_LLETRA VARCHAR2(32)
);

CREATE TABLE IPA_EXP_COMMENT
(
  ID                   NUMBER(19)         NOT NULL,
  EXPEDIENT_ID         NUMBER(19) 				NOT NULL,
  TEXT				   			 VARCHAR2 (1024),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64)
);

CREATE TABLE IPA_METANODE
(
  ID                   NUMBER(19)               NOT NULL,
  CODI                 VARCHAR2(256)            NOT NULL,
  NOM                  VARCHAR2(256)            NOT NULL,
  DESCRIPCIO           VARCHAR2(1024),
  TIPUS                VARCHAR2(256)            NOT NULL,
  ENTITAT_ID           NUMBER(19)               NOT NULL,
  ACTIU                NUMBER(1),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6)
);


CREATE TABLE IPA_METAEXPEDIENT
(
  ID                       NUMBER(19)           NOT NULL,
  PARE_ID                  NUMBER(19),
  CLASIF_SIA               VARCHAR2(30)         NOT NULL,
  SERIE_DOC                VARCHAR2(30)         NOT NULL,
  EXPRESSIO_NUMERO         VARCHAR2(100),
  NOT_ACTIVA               NUMBER(1)            NOT NULL,
  ENTITAT_ID               NUMBER(19)           NOT NULL,
  CODI                     VARCHAR2(64)         NOT NULL,
  GESTIO_AMB_GRUPS_ACTIVA  NUMBER(1)  			NOT NULL,
  ORGAN_GESTOR_ID          NUMBER(19),
  PERMET_METADOCS_GENERALS NUMBER(1)            DEFAULT 0 NOT NULL,
);


CREATE TABLE IPA_METAEXP_SEQ
(
  ID                   NUMBER(19)               NOT NULL,
  ANIO                 NUMBER(10),
  VALOR                NUMBER(19),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  META_EXPEDIENT_ID    NUMBER(19)               NOT NULL
);


CREATE TABLE IPA_METAEXP_TASCA
(
  ID                   NUMBER(19)           NOT NULL,
  CODI                 VARCHAR2(64)			NOT NULL,
  NOM                  VARCHAR2(256)	    NOT NULL,
  DESCRIPCIO           VARCHAR2(1024)		NOT NULL,
  RESPONSABLE          VARCHAR2(64),
  ACTIVA               NUMBER(1)			NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  META_EXPEDIENT_ID    NUMBER(19)               NOT NULL,
  DATA_LIMIT TIMESTAMP(6),
  ESTAT_CREAR_TASCA_ID NUMBER(19),
  ESTAT_FINALITZAR_TASCA_ID NUMBER(19)
);

CREATE TABLE IPA_METADOCUMENT
(
  ID                      NUMBER(19)            NOT NULL,
  MULTIPLICITAT           NUMBER(10)            NOT NULL,
  FIRMA_PFIRMA            NUMBER(1),
  PORTAFIRMES_DOCTIP      VARCHAR2(64),
  PORTAFIRMES_FLUXID      VARCHAR2(64),
  PORTAFIRMES_RESPONS     VARCHAR2(512),
  PORTAFIRMES_SEQTIP   	  VARCHAR2(256),
  PORTAFIRMES_FLUXTIP 	  VARCHAR2(256),
  PORTAFIRMES_CUSTIP      VARCHAR2(64),
  FIRMA_PASSARELA         NUMBER(1),
  FIRMA_BIOMETRICA 		  NUMBER(1,0),
  BIOMETRICA_LECTURA	  NUMBER(1,0),
  PASSARELA_CUSTIP        VARCHAR2(64),
  PLANTILLA_NOM           VARCHAR2(256),
  PLANTILLA_CONTENT_TYPE  VARCHAR2(256),
  PLANTILLA_CONTINGUT     BLOB,
  META_EXPEDIENT_ID       NUMBER(19)            NOT NULL,
  NTI_ORIGEN 			  VARCHAR2(2)           NOT NULL,
  NTI_ESTELA 			  VARCHAR2(4),
  NTI_TIPDOC 			  VARCHAR2(4)           NOT NULL,
  CODI                    VARCHAR2(64)          NOT NULL,
  META_DOCUMENT_TIPUS_GEN VARCHAR2(256)
);


CREATE TABLE IPA_METADADA
(
  ID                    NUMBER(19)              NOT NULL,
  CODI                  VARCHAR2(64)            NOT NULL,
  NOM                   VARCHAR2(256)           NOT NULL,
  TIPUS                 NUMBER(10)              NOT NULL,
  MULTIPLICITAT         NUMBER(10)              NOT NULL,
  ACTIVA                NUMBER(1)               NOT NULL,
  READ_ONLY             NUMBER(1)               NOT NULL,
  ORDRE                 NUMBER(10)              NOT NULL,
  DESCRIPCIO            VARCHAR2(1024),
  META_NODE_ID          NUMBER(19)              NOT NULL,
  VERSION               NUMBER(19)              NOT NULL,
  VALOR 				VARCHAR2(255);
  CREATEDBY_CODI        VARCHAR2(64),
  CREATEDDATE           TIMESTAMP(6),
  LASTMODIFIEDBY_CODI   VARCHAR2(64),
  LASTMODIFIEDDATE      TIMESTAMP(6),
  VALOR 				VARCHAR2(255)
);


CREATE TABLE IPA_INTERESSAT
(
  ID                   NUMBER(19)               NOT NULL,
  DTYPE                VARCHAR2(256)            NOT NULL,
  NOM                  VARCHAR2(30),
  LLINATGE1            VARCHAR2(30),
  LLINATGE2            VARCHAR2(30),
  DOCUMENT_TIPUS       VARCHAR2(40)             NOT NULL,
  DOCUMENT_NUM         VARCHAR2(17)             NOT NULL,
  PAIS                 VARCHAR2(4),
  PROVINCIA            VARCHAR2(2),
  MUNICIPI             VARCHAR2(5),
  ADRESA               VARCHAR2(160),
  CODI_POSTAL          VARCHAR2(5),
  EMAIL                VARCHAR2(160),
  TELEFON              VARCHAR2(20),
  OBSERVACIONS         VARCHAR2(160),
  ORGAN_CODI           VARCHAR2(9),
  ORGAN_NOM		       VARCHAR2(80),
  RAO_SOCIAL           VARCHAR2(80),
  NOT_IDIOMA           VARCHAR2(2),
  NOT_AUTORITZAT       NUMBER(1)                NOT NULL,
  ES_REPRESENTANT      NUMBER(1)                NOT NULL,
  REPRESENTANT_ID      NUMBER(19),
  EXPEDIENT_ID         NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  VERSION              NUMBER(19)               NOT NULL,
  ENTREGA_DEH 		   NUMBER(1),
  ENTREGA_DEH_OBLIGAT  NUMBER(1),
  INCAPACITAT 		   NUMBER(1)
);


CREATE TABLE IPA_CONTINGUT
(
  ID                   NUMBER(19)               NOT NULL,
  NOM                  VARCHAR2(1024)           NOT NULL,
  TIPUS                NUMBER(10)               NOT NULL,
  PARE_ID              NUMBER(19),
  ESBORRAT             NUMBER(10),
  ARXIU_UUID           VARCHAR2(36),
  ARXIU_DATA_ACT       TIMESTAMP(6),
  EXPEDIENT_ID         NUMBER(19),
  CONTMOV_ID           NUMBER(19),
  ENTITAT_ID           NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  VERSION              NUMBER(19)               NOT NULL,
  ESBORRAT_DATA 	   TIMESTAMP(6)
);


CREATE TABLE IPA_CONT_MOV
(
  ID                   NUMBER(19)               NOT NULL,
  CONTINGUT_ID         NUMBER(19)               NOT NULL,
  ORIGEN_ID            NUMBER(19),
  DESTI_ID             NUMBER(19)               NOT NULL,
  REMITENT_CODI        VARCHAR2(64),
  COMENTARI            VARCHAR2(256),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64)
);


CREATE TABLE IPA_CONT_LOG
(
  ID                   NUMBER(19)               NOT NULL,
  TIPUS                NUMBER(10)               NOT NULL,
  CONTINGUT_ID         NUMBER(19)               NOT NULL,
  PARE_ID              NUMBER(19),
  CONTMOV_ID           NUMBER(19),
  OBJECTE_ID           VARCHAR2(256),
  OBJECTE_LOG_TIPUS    NUMBER(10),
  OBJECTE_TIPUS        NUMBER(10),
  PARAM1               VARCHAR2(256),
  PARAM2               VARCHAR2(256),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64)
);


CREATE TABLE IPA_NODE
(
  ID                  NUMBER(19)                NOT NULL,
  METANODE_ID         NUMBER(19)
);


CREATE TABLE IPA_CARPETA
(
  ID     NUMBER(19)                             NOT NULL
);


CREATE TABLE IPA_DADA
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  ORDRE                NUMBER(10),
  VALOR                VARCHAR2(256)            NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  METADADA_ID          NUMBER(19)               NOT NULL,
  NODE_ID              NUMBER(19)               NOT NULL
);


CREATE TABLE IPA_EXPEDIENT
(
  ID                 NUMBER(19)                 NOT NULL,
  ESTAT              NUMBER(10)                 NOT NULL,
  TANCAT_DATA        TIMESTAMP(6),
  TANCAT_MOTIU       VARCHAR2(1024),
  ANIO               NUMBER(10)                 NOT NULL,
  SEQUENCIA          NUMBER(19)                 NOT NULL,
  CODI               VARCHAR2(256)              NOT NULL,
  NTI_VERSION        VARCHAR2(5)                NOT NULL,
  NTI_IDENTIF        VARCHAR2(52)               NOT NULL,
  NTI_ORGANO         VARCHAR2(9)                NOT NULL,
  NTI_FECHA_APE      TIMESTAMP(6)               NOT NULL,
  NTI_CLASIF_SIA     VARCHAR2(30)               NOT NULL,
  METAEXPEDIENT_ID   NUMBER(19)                 NOT NULL,
  SISTRA_BANTEL_NUM  VARCHAR2(16),
  SISTRA_PUBLICAT    NUMBER(1),
  SISTRA_UNITAT_ADM  VARCHAR2(9),
  SISTRA_CLAU        VARCHAR2(100),
  AGAFAT_PER_CODI    VARCHAR2(64),
  EXPEDIENT_ESTAT_ID NUMBER(19),
  GRUP_ID            NUMBER(19)
);


CREATE TABLE IPA_EXPEDIENT_REL
(
  EXPEDIENT_ID       NUMBER(19)                 NOT NULL,
  EXPEDIENT_REL_ID   NUMBER(19)                 NOT NULL
);


CREATE TABLE IPA_DOCUMENT
(
  ID                   NUMBER(19)               NOT NULL,
  TIPUS                NUMBER(10)               NOT NULL,
  ESTAT                NUMBER(10)               NOT NULL,
  UBICACIO             VARCHAR2(255),
  DATA                 TIMESTAMP(6)             NOT NULL,
  DATA_CAPTURA         TIMESTAMP(6)             NOT NULL,
  CUSTODIA_DATA        TIMESTAMP(6),
  CUSTODIA_ID          VARCHAR2(256),
  CUSTODIA_CSV         VARCHAR2(256),
  FITXER_NOM           VARCHAR2(256),
  FITXER_CONTENT_TYPE  VARCHAR2(256),
  FITXER_CONTINGUT     BLOB,
  DESCRIPCIO 		   VARCHAR2(512 CHAR),
  VERSIO_DARRERA       VARCHAR2(32),
  VERSIO_COUNT         NUMBER(10)               NOT NULL,
  NTI_VERSION          VARCHAR2(5)              NOT NULL,
  NTI_IDENTIF          VARCHAR2(48)             NOT NULL,
  NTI_ORGANO           VARCHAR2(9)              NOT NULL,
  NTI_ORIGEN           VARCHAR2(2)              NOT NULL,
  NTI_ESTELA           VARCHAR2(4)              NOT NULL,
  NTI_TIPDOC           VARCHAR2(4)              NOT NULL,
  NTI_IDORIG           VARCHAR2(48),
  NTI_TIPFIR           VARCHAR2(4),
  NTI_CSV              VARCHAR2(256),
  NTI_CSVREG           VARCHAR2(512)
);


CREATE TABLE IPA_DOCUMENT_ENVIAMENT
(
  ID                   		NUMBER(19)               NOT NULL,
  DTYPE                		VARCHAR2(32)             NOT NULL,
  ESTAT                		VARCHAR2(255)            NOT NULL,
  ASSUMPTE             		VARCHAR2(256)            NOT NULL,
  OBSERVACIONS         		VARCHAR2(256),
  ENVIAT_DATA          		TIMESTAMP(6),
  PROCESSAT_DATA       		TIMESTAMP(6),
  CANCELAT_DATA        		TIMESTAMP(6),
  ERROR                		NUMBER(1),
  ERROR_DESC           		VARCHAR2(2048),
  INTENT_NUM           		NUMBER(10),
  INTENT_DATA          		TIMESTAMP(6),
  INTENT_PROXIM_DATA   		TIMESTAMP(6),
  NOT_TIPUS            		NUMBER(10),
  NOT_DATA_PROG        		TIMESTAMP(6),
  NOT_RETARD           		NUMBER(10),
  NOT_DATA_CADUCITAT   		TIMESTAMP(6),
  NOT_ENV_ID           		VARCHAR2(100),
  NOT_ENV_REF          		VARCHAR2(100),
  NOT_ENV_DAT_ESTAT    		VARCHAR2(20),
  NOT_ENV_DAT_DATA     		TIMESTAMP(6),
  NOT_ENV_DAT_ORIG     		VARCHAR2(20),
  NOT_ENV_CERT_DATA    		TIMESTAMP(6),
  NOT_ENV_CERT_ORIG    		VARCHAR2(20),
  NOT_ENV_CERT_ARXIUID 		VARCHAR2(50),
  PF_PRIORITAT         		NUMBER(10),
  PF_CAD_DATA          		TIMESTAMP(6),
  PF_DOC_TIPUS         		VARCHAR2(64),
  PF_RESPONSABLES      		VARCHAR2(1024),
  PF_FLUX_TIPUS 			VARCHAR2(256),
  PF_SEQ_TIPUS        		NUMBER(10),
  PF_FLUX_ID           		VARCHAR2(64),
  PF_PORTAFIRMES_ID    		VARCHAR2(64),
  PF_CALLBACK_ESTAT    		NUMBER(10),
  PF_MOTIU_REBUIG 			VARCHAR2(512 CHAR),
  VF_CODI_USUARI 	   		VARCHAR2(64),
  VF_TITOL 	 		   		VARCHAR2(256),
  VF_DESCRIPCIO 	   		VARCHAR2(256),
  VF_CODI_DISPOSITIU   		VARCHAR2(64),
  VF_MESSAGE_CODE 	   		VARCHAR2(64),
  VF_CALLBACK_ESTAT    		NUMBER(10,0),
  VF_CONTRASENYA_USUARI 	VARCHAR2(64),
  VF_LECTURA_OBLIGATORIA 	NUMBER(1,0),
  VF_VIAFIRMA_DISPOSITIU 	number(19),
  PUB_TIPUS            		NUMBER(10),
  DOCUMENT_ID          		NUMBER(19)               NOT NULL,
  EXPEDIENT_ID         		NUMBER(19)               NOT NULL,
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDDATE     		TIMESTAMP(6),
  CREATEDBY_CODI       		VARCHAR2(256),
  LASTMODIFIEDBY_CODI  		VARCHAR2(256),
  VERSION              		NUMBER(19)               NOT NULL,
  SERVEI_TIPUS 		   		VARCHAR(10),
  ENTREGA_POSTAL 	  		NUMBER(1),
  NOTIFICACIO_ESTAT    		VARCHAR2(255),
  NOT_ENV_REGISTRE_DATA		DATE,
  NOT_ENV_REGISTRE_NUMERO	NUMBER(19),
  NOT_ENV_REGISTRE_NUM_FORMATAT VARCHAR2(50)
);f


CREATE TABLE IPA_DOCUMENT_ENVIAMENT_DOC (
  DOCUMENT_ENVIAMENT_ID NUMBER(19)              NOT NULL,
  DOCUMENT_ID           NUMBER(19)              NOT NULL
);


CREATE TABLE IPA_EXPEDIENT_INTERESSAT
(
  EXPEDIENT_ID   NUMBER(19)                     NOT NULL,
  INTERESSAT_ID  NUMBER(19)                     NOT NULL
);


CREATE TABLE IPA_EXECUCIO_MASSIVA
(
  ID                   NUMBER(19)		NOT NULL,
  TIPUS                VARCHAR2(255)	NOT NULL,
  DATA_INICI	       TIMESTAMP(6),
  DATA_FI              TIMESTAMP(6),
  PFIRMES_MOTIU		   VARCHAR2(256),
  PFIRMES_PRIORI	   VARCHAR2(255),
  PFIRMES_DATCAD	   TIMESTAMP(6),
  ENVIAR_CORREU		   NUMBER(1),
  ENTITAT_ID		   NUMBER(19)		NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6)
);


CREATE TABLE IPA_MASSIVA_CONTINGUT
(
  ID                   NUMBER(19)		NOT NULL,
  EXECUCIO_MASSIVA_ID  NUMBER(19),
  CONTINGUT_ID  	   NUMBER(19),
  DATA_INICI	       TIMESTAMP(6),
  DATA_FI              TIMESTAMP(6),
  ESTAT				   VARCHAR2(255),
  ERROR				   VARCHAR2(2046),
  ORDRE				   NUMBER(19),
  CREATEDBY_CODI       VARCHAR2(64),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6)
);


CREATE TABLE IPA_ACL_CLASS
(
  ID     NUMBER(19)                             NOT NULL,
  CLASS  VARCHAR2(100)                          NOT NULL
);


CREATE TABLE IPA_ACL_SID
(
  ID         NUMBER(19)                         NOT NULL,
  PRINCIPAL  NUMBER(1)                          NOT NULL,
  SID        VARCHAR2(100)                      NOT NULL
);


CREATE TABLE IPA_ACL_ENTRY
(
  ID                   NUMBER(19)               NOT NULL,
  ACL_OBJECT_IDENTITY  NUMBER(19)               NOT NULL,
  ACE_ORDER            NUMBER(19)               NOT NULL,
  SID                  NUMBER(19)               NOT NULL,
  MASK                 NUMBER(19)               NOT NULL,
  GRANTING             NUMBER(1)                NOT NULL,
  AUDIT_SUCCESS        NUMBER(1)                NOT NULL,
  AUDIT_FAILURE        NUMBER(1)                NOT NULL
);


CREATE TABLE IPA_ACL_OBJECT_IDENTITY
(
  ID                  NUMBER(19)                NOT NULL,
  OBJECT_ID_CLASS     NUMBER(19)                NOT NULL,
  OBJECT_ID_IDENTITY  NUMBER(19)                NOT NULL,
  PARENT_OBJECT       NUMBER(19),
  OWNER_SID           NUMBER(19)                NOT NULL,
  ENTRIES_INHERITING  NUMBER(1)                 NOT NULL
);





CREATE TABLE IPA_EXPEDIENT_ESTAT
(
	ID                  NUMBER(19)               			NOT NULL,
	CODI 			    VARCHAR2(256)						NOT NULL,
	NOM 			    VARCHAR2(256)						NOT NULL,
	ORDRE 			    NUMBER(10)							NOT NULL,
	COLOR 			    VARCHAR2(256),					  
    METAEXPEDIENT_ID    NUMBER(19),               
    INICIAL             NUMBER(1),
    RESPONSABLE_CODI    VARCHAR2(64),
    
	
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDDATE    TIMESTAMP(6),
    CREATEDBY_CODI      VARCHAR2(256),
    LASTMODIFIEDBY_CODI VARCHAR2(256)
);




CREATE TABLE IPA_EXPEDIENT_PETICIO
(
  ID                   NUMBER(19)               NOT NULL,
  IDENTIFICADOR        VARCHAR2(80)             NOT NULL,
  CLAU_ACCES           VARCHAR2(200)            NOT NULL,
  DATA_ALTA            TIMESTAMP(6)             NOT NULL,
  ESTAT			       VARCHAR2(40)             NOT NULL,
  META_EXPEDIENT_NOM   VARCHAR2(256),
  EXP_PETICIO_ACCIO    VARCHAR2(20),
  REGISTRE_ID 		   NUMBER(19),
  CONSULTA_WS_ERROR    NUMBER(1),
  CONSULTA_WS_ERROR_DESC  VARCHAR2(4000),
  CONSULTA_WS_ERROR_DATE  TIMESTAMP(6),
  NOTIFICA_DIST_ERROR VARCHAR2(4000),
  EXPEDIENT_ID 				NUMBER(19),
  
  CREATEDDATE         TIMESTAMP(6),
  LASTMODIFIEDDATE    TIMESTAMP(6),
  CREATEDBY_CODI      VARCHAR2(256),
  LASTMODIFIEDBY_CODI VARCHAR2(256)

);



CREATE TABLE IPA_REGISTRE (
  ID 						NUMBER(19) 		NOT NULL, 
  APLICACIO_CODI 			VARCHAR2(20), 
  APLICACIO_VERSIO 			VARCHAR2(15), 
  ASSUMPTE_CODI_CODI 		VARCHAR2(16), 
  ASSUMPTE_CODI_DESC 		VARCHAR2(100), 
  ASSUMPTE_TIPUS_CODI		VARCHAR2(16), 
  ASSUMPTE_TIPUS_DESC 		VARCHAR2(100), 
  DATA 						TIMESTAMP(6) 	NOT NULL, 
  DOC_FISICA_CODI 			VARCHAR2(1), 
  DOC_FISICA_DESC 			VARCHAR2(100), 
  ENTITAT_CODI 				VARCHAR2(21) 	NOT NULL, 
  ENTITAT_DESC 				VARCHAR2(100), 
  EXPEDIENT_NUMERO 			VARCHAR2(80), 
  EXPOSA 					CLOB, 
  EXTRACTE					VARCHAR2(240),
  PROCEDIMENT_CODI			VARCHAR2(20),
  IDENTIFICADOR 			VARCHAR2(100) 	NOT NULL, 
  IDIOMA_CODI 				VARCHAR2(2) 	NOT NULL, 
  IDIOMA_DESC 				VARCHAR2(100), 
  LLIBRE_CODI 				VARCHAR2(4) 	NOT NULL, 
  LLIBRE_DESC 				VARCHAR2(100), 
  OBSERVACIONS 				VARCHAR2(50), 
  OFICINA_CODI 				VARCHAR2(21) 	NOT NULL, 
  OFICINA_DESC 				VARCHAR2(100), 
  ORIGEN_DATA 				TIMESTAMP(6), 
  ORIGEN_REGISTRE_NUM 		VARCHAR2(80), 
  REF_EXTERNA 				VARCHAR2(16), 
  SOLICITA 					CLOB, 
  TRANSPORT_NUM 			VARCHAR2(20), 
  TRANSPORT_TIPUS_CODI 		VARCHAR2(2), 
  TRANSPORT_TIPUS_DESC 		VARCHAR2(100), 
  USUARI_CODI 				VARCHAR2(20), 
  USUARI_NOM 				VARCHAR2(80), 
  DESTI_CODI 				VARCHAR2(21) 	NOT NULL, 
  DESTI_DESCRIPCIO 			VARCHAR2(100),
  ENTITAT_ID				NUMBER(19) 		NOT NULL,

  CREATEDDATE         TIMESTAMP(6),
  LASTMODIFIEDDATE    TIMESTAMP(6),
  CREATEDBY_CODI      VARCHAR2(256),
  LASTMODIFIEDBY_CODI VARCHAR2(256)
);


CREATE TABLE IPA_REGISTRE_ANNEX (
	ID 						NUMBER(19) 		NOT NULL, 
	CONTINGUT BLOB,
	FIRMA_CONTINGUT BLOB,
	FIRMA_PERFIL VARCHAR2(4),
	FIRMA_TAMANY NUMBER(10),
	FIRMA_TIPUS VARCHAR2(4),
	NOM VARCHAR2(80) NOT NULL,
	FIRMA_NOM VARCHAR2(80),
	NTI_FECHA_CAPTURA TIMESTAMP(6) NOT NULL,
	NTI_ORIGEN VARCHAR2(20) NOT NULL,
	NTI_TIPO_DOC VARCHAR2(20) NOT NULL,
	OBSERVACIONS VARCHAR2(50),
	SICRES_TIPO_DOC VARCHAR2(20)  NOT NULL,
	SICRES_VALIDEZ_DOC VARCHAR2(30),
	TAMANY NUMBER(10) NOT NULL,
	TIPUS_MIME VARCHAR2(30),
	TITOL VARCHAR2(200) NOT NULL,
	UUID VARCHAR2(100),
	REGISTRE_ID NUMBER(19) NOT NULL,
	ESTAT VARCHAR2(20),
	ERROR VARCHAR2(4000),
	NTI_ESTADO_ELABORACIO VARCHAR2(50) NOT NULL,
	
	CREATEDDATE         TIMESTAMP(6),
  	LASTMODIFIEDDATE    TIMESTAMP(6),
  	CREATEDBY_CODI      VARCHAR2(256),
  	LASTMODIFIEDBY_CODI VARCHAR2(256)
);


CREATE TABLE IPA_REGISTRE_INTERESSAT 
  ( 
     ID                  NUMBER(19) NOT NULL, 
     ADRESA              VARCHAR2(160), 
     CANAL               VARCHAR2(30), 
     CP                  VARCHAR2(5), 
     DOC_NUMERO          VARCHAR2(17), 
     DOC_TIPUS           VARCHAR2(15), 
     EMAIL               VARCHAR2(160), 
     LLINATGE1           VARCHAR2(30), 
     LLINATGE2           VARCHAR2(30), 
     MUNICIPI_CODI       VARCHAR2(100), 
     NOM                 VARCHAR2(30), 
     OBSERVACIONS        VARCHAR2(160), 
     PAIS_CODI           VARCHAR2(4), 
     PROVINCIA_CODI      VARCHAR2(100), 
     RAO_SOCIAL          VARCHAR2(80), 
     TELEFON             VARCHAR2(20), 
     TIPUS               VARCHAR2(40) NOT NULL, 
     REPRESENTANT_ID     NUMBER(19), 
     REGISTRE_ID         NUMBER(19), 
     PAIS                VARCHAR2(200), 
     PROVINCIA           VARCHAR2(200), 
     MUNICIPI            VARCHAR2(200), 
     ORGAN_CODI          VARCHAR(9), 
     CREATEDDATE         TIMESTAMP(6), 
     LASTMODIFIEDDATE    TIMESTAMP(6), 
     CREATEDBY_CODI      VARCHAR2(256), 
     LASTMODIFIEDBY_CODI VARCHAR2(256) 
  ); 

CREATE TABLE IPA_DOCUMENT_ENVIAMENT_INTER
(
  ID                   	NUMBER(19) NOT NULL,
  DOCUMENT_ENVIAMENT_ID NUMBER(19) NOT NULL,
  INTERESSAT_ID 		NUMBER(19) NOT NULL,
  NOT_ENV_REF   		VARCHAR(100),
  CREATEDBY_CODI       VARCHAR2(64),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  NOT_ENV_DAT_ESTAT    		VARCHAR2(20),
  NOT_ENV_DAT_DATA     		TIMESTAMP(6),
  NOT_ENV_DAT_ORIG     		VARCHAR2(20),
  NOT_ENV_CERT_DATA    		TIMESTAMP(6),
  NOT_ENV_CERT_ORIG    		VARCHAR2(20),
  ERROR                		NUMBER(1),
  ERROR_DESC           		VARCHAR2(2048),
  NOT_ENV_REGISTRE_DATA		DATE,
  NOT_ENV_REGISTRE_NUMERO	NUMBER(19),
  NOT_ENV_REGISTRE_NUM_FORMATAT VARCHAR2(50)
);

CREATE TABLE IPA_VIAFIRMA_USUARI (
	CODI			VARCHAR2(64)			NOT NULL,
	CONTRASENYA		VARCHAR2(64)			NOT NULL,
	DESCRIPCIO		VARCHAR2(64)			NOT NULL
);

CREATE TABLE IPA_USUARI_VIAFIRMA_RIPEA (
    ID                    NUMBER(19)            NOT NULL,
    VIAFIRMA_USER_CODI    VARCHAR2(64)          NOT NULL,
    RIPEA_USER_CODI       VARCHAR2(64)          NOT NULL
);

CREATE TABLE IPA_DOCUMENT_ENVIAMENT_DIS (
    ID                      NUMBER(19)               NOT NULL,
    CODI                    VARCHAR2(64),
    CODI_APLICACIO          VARCHAR2(64),
    CODI_USUARI             VARCHAR2(64),
    DESCRIPCIO              VARCHAR2(255),
    LOCALE                  VARCHAR2(10),
    ESTAT                   VARCHAR2(64),
    TOKEN                   VARCHAR2(255),
    IDENTIFICADOR           VARCHAR2(64),
    TIPUS                   VARCHAR2(64),
    EMAIL_USUARI            VARCHAR2(64),
    IDENTIFICADOR_NAC       VARCHAR2(64),
    CREATEDDATE             TIMESTAMP(6),
    LASTMODIFIEDDATE        TIMESTAMP(6),
    CREATEDBY_CODI          VARCHAR2(256),
    LASTMODIFIEDBY_CODI     VARCHAR2(256)
);

 CREATE TABLE IPA_EXPEDIENT_TASCA
(
  ID 					NUMBER(19) NOT NULL,
  EXPEDIENT_ID 			NUMBER(19) NOT NULL,
  METAEXP_TASCA_ID 		NUMBER(19) NOT NULL,
  RESPONSABLE_CODI  	VARCHAR2(64) NOT NULL, 
  DATA_INICI 			TIMESTAMP(6) NOT NULL, 
  DATA_FI 				TIMESTAMP(6), 
  ESTAT 				VARCHAR2(20),
  MOTIU_REBUIG 			VARCHAR2(1024),
  COMENTARI 			VARCHAR2(1024),
  CREATEDDATE          	TIMESTAMP(6),
  CREATEDBY_CODI       	VARCHAR2(64),
  LASTMODIFIEDDATE     	TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64),
  DATA_LIMIT 			TIMESTAMP(6)
);

CREATE TABLE IPA_EXPEDIENT_SEGUIDOR
(
  EXPEDIENT_ID   NUMBER(19)                     NOT NULL,
  SEGUIDOR_CODI	 VARCHAR2(64)                   NOT NULL
);

CREATE TABLE IPA_GRUP
(
    ID NUMBER(19) NOT NULL,
    ROL VARCHAR2(50) NOT NULL,
    DESCRIPCIO VARCHAR2(512) NOT NULL,
    ENTITAT_ID NUMBER(19) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64),
    CREATEDDATE TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64),
    LASTMODIFIEDDATE TIMESTAMP(6)
);

CREATE TABLE IPA_METAEXPEDIENT_GRUP (
  METAEXPEDIENT_ID  NUMBER(19)             NOT NULL,
  GRUP_ID           NUMBER(19)             NOT NULL
);

CREATE TABLE IPA_ORGAN_GESTOR
(
	ID                  NUMBER(19)      NOT NULL,
	CODI                VARCHAR2(64)    NOT NULL,
	NOM                 VARCHAR2(1000)  NOT NULL,
  	ENTITAT_ID          NUMBER(19)      NOT NULL,
    CREATEDBY_CODI      VARCHAR2(64),
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64),
    LASTMODIFIEDDATE    TIMESTAMP(6),
	PARE_ID             NUMBER(19),
	ACTIU               NUMBER(1,0)      DEFAULT 1 NOT NULL
);

CREATE TABLE IPA_PORTAFIRMES_BLOCK
(
	ID						NUMBER(19)		NOT NULL,
	BLK_ORDER 				NUMBER(19)		NOT NULL,
	DOCUMENT_ENVIAMENT_ID	NUMBER(19)		NOT NULL,
	CREATEDBY_CODI 			VARCHAR2(64),
	CREATEDDATE 			TIMESTAMP(6),
    LASTMODIFIEDBY_CODI 	VARCHAR2(64),
    LASTMODIFIEDDATE 		TIMESTAMP(6),
    VERSION       		    NUMBER(19)      NOT NULL
);

CREATE TABLE IPA_PORTAFIRMES_BLOCK_INFO
(
	ID						NUMBER(19)		NOT NULL,
	PORTAFIRMES_BLOCK_ID	NUMBER(19)		NOT NULL,
	PORTAFIRMES_SIGNER_NOM	VARCHAR2(50),
	PORTAFIRMES_SIGNER_CODI	VARCHAR2(50)	NOT NULL,
	PORTAFIRMES_SIGNER_ID	VARCHAR2(9),
	SIGNED					NUMBER(1,0)		NOT NULL,
	CREATEDBY_CODI 			VARCHAR2(64),
	CREATEDDATE 			TIMESTAMP(6),
    LASTMODIFIEDBY_CODI 	VARCHAR2(64),
    LASTMODIFIEDDATE 		TIMESTAMP(6),
    VERSION       		    NUMBER(19)      NOT NULL
);

CREATE TABLE IPA_TIPUS_DOCUMENTAL
(
  ID                   NUMBER(19)           NOT NULL,
  CODI                 VARCHAR2(64)			NOT NULL,
  NOM                  VARCHAR2(256)	    NOT NULL,
  ENTITAT_ID           NUMBER(19)           NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64)
);

CREATE TABLE IPA_DOMINI
(
  ID                   NUMBER(19)           NOT NULL,
  CODI                 VARCHAR2(64)			NOT NULL,
  NOM                  VARCHAR2(256)	    NOT NULL,
  DESCRIPCIO           VARCHAR2(256),
  CONSULTA             VARCHAR2(256)        NOT NULL,
  CADENA               VARCHAR2(256)        NOT NULL,
  CONTRASENYA          VARCHAR2(256)        NOT NULL,
  ENTITAT_ID           NUMBER(19)           NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64)
);

