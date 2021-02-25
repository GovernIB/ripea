CREATE TABLE IPA_ALERTA
(
  ID                   BIGINT                          NOT NULL,
  TEXT                 character varying(1024)          NOT NULL,
  ERROR                character varying(2048),
  LLEGIDA              boolean                         NOT NULL,
  CONTINGUT_ID         bigint,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);

CREATE TABLE IPA_USUARI
(
  CODI          		character varying(64)       NOT NULL,
  INICIALITZAT  		boolean,
  NIF           		character varying(9)        NOT NULL,
  NOM           		character varying(200),
  EMAIL         		character varying(200),
  IDIOMA 				character varying(2) 	 	NOT NULL,
  VERSION       		bigint                      NOT NULL,
  EMAILS_AGRUPATS 		BOOLEAN DEFAULT TRUE
);


CREATE TABLE IPA_EMAIL_PENDENT_ENVIAR
(
    ID BIGSERIAL NOT NULL,
    REMITENT CHARACTER VARYING(64) NOT NULL,
    DESTINATARI CHARACTER VARYING(64) NOT NULL,
    SUBJECT CHARACTER VARYING(1024) NOT NULL,
    TEXT CHARACTER VARYING(4000) NOT NULL,
    EVENT_TIPUS_ENUM VARCHAR2(64) NOT NULL,
    CREATEDBY_CODI CHARACTER VARYING(64),
    CREATEDDATE TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI CHARACTER VARYING(64),
    LASTMODIFIEDDATE TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IPA_ENTITAT
(
  ID                   BIGINT                   NOT NULL,
  CODI                 character varying(64)    NOT NULL,
  NOM                  character varying(256)   NOT NULL,
  DESCRIPCIO           character varying(1024),
  CIF                  character varying(9)     NOT NULL,
  UNITAT_ARREL         character varying(9)     NOT NULL,
  ACTIVA               boolean,
  VERSION              bigint                   NOT NULL,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone,
  LOGO_IMG 			   oid,
  CAPSALERA_COLOR_FONS character varying(32),
  CAPSALERA_COLOR_LLETRA character varying(32)
);


CREATE TABLE IPA_METANODE
(
  ID                   BIGINT                  NOT NULL,
  CODI                 character varying(256)  NOT NULL,
  NOM                  character varying(256)  NOT NULL,
  DESCRIPCIO           character varying(1024),
  TIPUS                character varying(256)  NOT NULL,
  ENTITAT_ID           bigint                  NOT NULL,
  ACTIU                boolean,
  VERSION              bigint                  NOT NULL,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);


CREATE TABLE IPA_METAEXPEDIENT
(
  ID                 		BIGINT                     NOT NULL,
  PARE_ID            		BIGINT,
  CLASIF_SIA         		character varying(30)      NOT NULL,
  SERIE_DOC          		character varying(30)      NOT NULL,
  EXPRESSIO_NUMERO   		character varying(100),
  NOT_ACTIVA         		boolean                    NOT NULL,
  ENTITAT_ID         		BIGINT                     NOT NULL,
  CODI               		character varying(64)      NOT NULL,
  GESTIO_AMB_GRUPS_ACTIVA 	BOOLEAN 			    NOT NULL,
  PERMET_METADOCS_GENERALS 	boolean DEFAULT '0' NOT NULL,
  ORGAN_GESTOR_ID 			BIGINT,
);


CREATE TABLE IPA_METAEXP_SEQ
(
  ID                   BIGINT                    NOT NULL,
  ANIO                 integer,
  VALOR                bigint,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  META_EXPEDIENT_ID    bigint                    NOT NULL
);


CREATE TABLE IPA_METAEXP_TASCA
(
  ID                   bigint               NOT NULL,
  CODI                 character varying(64)			NOT NULL,
  NOM                  character varying(256)	    NOT NULL,
  DESCRIPCIO           character varying(1024)		NOT NULL,
  RESPONSABLE          character varying(64),
  ACTIVA               boolean			NOT NULL,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  META_EXPEDIENT_ID    bigint               NOT NULL,
  DATA_LIMIT TIMESTAMP WITHOUT TIMEZONE,
  ESTAT_CREAR_TASCA_ID BIGINT,
  ESTAT_FINALITZAR_TASCA_ID BIGINT
);


CREATE TABLE IPA_METAEXP_ORGAN
(
  ID                   bigint NOT NULL,
  META_EXPEDIENT_ID    bigint NOT NULL,
  ORGAN_GESTOR_ID      bigint NOT NULL,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64)
);


CREATE TABLE IPA_METADOCUMENT
(
  ID                      BIGINT               NOT NULL,
  MULTIPLICITAT           int                  NOT NULL,
  FIRMA_PFIRMA            boolean,
  PORTAFIRMES_DOCTIP      character varying(64),
  PORTAFIRMES_FLUXID      character varying(64),
  PORTAFIRMES_RESPONS     character varying(512),
  PORTAFIRMES_FLUXTIP     character varying(256),
  PORTAFIRMES_CUSTIP      character varying(64),
  FIRMA_PASSARELA         boolean,
  PASSARELA_CUSTIP        character varying(64),
  PLANTILLA_NOM           character varying(256),
  PLANTILLA_CONTENT_TYPE  character varying(256),
  PLANTILLA_CONTINGUT     oid,
  META_EXPEDIENT_ID       bigint                   NOT NULL,
  NTI_ORIGEN 			  character varying(2)     NOT NULL,
  NTI_ESTELA 			  character varying(4),
  NTI_TIPDOC 			  character varying(4)     NOT NULL,
  FIRMA_BIOMETRICA 		  BOOLEAN,
  BIOMETRICA_LECTURA 	  BOOLEAN,
  CODI                    character varying(64)    NOT NULL
);


CREATE TABLE IPA_METADADA
(
  ID                    BIGINT                  NOT NULL,
  CODI                  character varying(64)   NOT NULL,
  NOM                   character varying(256)  NOT NULL,
  TIPUS                 int                     NOT NULL,
  MULTIPLICITAT         int                     NOT NULL,
  ACTIVA                boolean                 NOT NULL,
  READONLY              boolean                 NOT NULL,
  ORDRE                 int                     NOT NULL,
  DESCRIPCIO            character varying(1024),
  META_NODE_ID          bigint                  NOT NULL,
  VERSION               bigint                  NOT NULL,
  CREATEDBY_CODI        character varying(64),
  CREATEDDATE           timestamp without time zone,
  LASTMODIFIEDBY_CODI   character varying(64),
  LASTMODIFIEDDATE      timestamp without time zone,
  VALOR 				character varying(255)
);


CREATE TABLE IPA_INTERESSAT
(
  ID                   BIGINT                   NOT NULL,
  DTYPE                character varying(256)   NOT NULL,
  NOM                  character varying(30),
  LLINATGE1            character varying(30),
  LLINATGE2            character varying(30),
  DOCUMENT_TIPUS       character varying(40)    NOT NULL,
  DOCUMENT_NUM         character varying(17)    NOT NULL,
  PAIS                 character varying(4),
  PROVINCIA            character varying(2),
  MUNICIPI             character varying(5),
  ADRESA               character varying(160),
  CODI_POSTAL          character varying(5),
  EMAIL                character varying(160),
  TELEFON              character varying(20),
  OBSERVACIONS         character varying(160),
  ORGAN_CODI           character varying(9),
  ORGAN_NOM	           character varying(80),
  RAO_SOCIAL           character varying(80),
  NOT_IDIOMA           character varying(2),
  NOT_AUTORITZAT       boolean                  NOT NULL,
  ES_REPRESENTANT      boolean                  NOT NULL,
  REPRESENTANT_ID      bigint,
  EXPEDIENT_ID         bigint                   NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  VERSION              bigint               NOT NULL,
  ENTREGA_DEH 		   BOOLEAN,
  ENTREGA_DEH_OBLIGAT  BOOLEAN,
  INCAPACITAT 		   BOOLEAN
);


CREATE TABLE IPA_CONTINGUT
(
  ID                   BIGINT                   NOT NULL,
  NOM                  character varying(1024)  NOT NULL,
  TIPUS                integer                  NOT NULL,
  PARE_ID              bigint,
  ESBORRAT             integer,
  ARXIU_UUID           character varying(36),
  ARXIU_DATA_ACT       timestamp without time zone,
  EXPEDIENT_ID         bigint,
  CONTMOV_ID           bigint,
  ENTITAT_ID           bigint                   NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  VERSION              bigint                   NOT NULL,
  ESBORRAT_DATA 	   TIMESTAMP WITHOUT TIMEZONE
);


CREATE TABLE IPA_CONT_MOV
(
  ID                   BIGINT                   NOT NULL,
  CONTINGUT_ID         bigint                   NOT NULL,
  ORIGEN_ID            bigint,
  DESTI_ID             bigint                   NOT NULL,
  REMITENT_CODI        character varying(64),
  COMENTARI            character varying(256),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64)
);


CREATE TABLE IPA_CONT_LOG
(
  ID                   BIGINT                   NOT NULL,
  TIPUS                integer                  NOT NULL,
  CONTINGUT_ID         bigint                   NOT NULL,
  PARE_ID              bigint,
  CONTMOV_ID           bigint,
  OBJECTE_ID           character varying(256),
  OBJECTE_LOG_TIPUS    integer,
  OBJECTE_TIPUS        integer,
  PARAM1               character varying(256),
  PARAM2               character varying(256),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64)
);


CREATE TABLE IPA_NODE
(
  ID                  BIGINT                      NOT NULL,
  METANODE_ID         bigint
);


CREATE TABLE IPA_CARPETA
(
  ID     BIGINT                                   NOT NULL
);


CREATE TABLE IPA_DADA
(
  ID                   BIGINT                     NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  ORDRE                integer,
  VALOR                character varying(256)     NOT NULL,
  VERSION              bigint                     NOT NULL,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  METADADA_ID          bigint                     NOT NULL,
  NODE_ID              bigint                     NOT NULL
);


CREATE TABLE IPA_EXPEDIENT
(
  ID                 BIGINT                     NOT NULL,
  ESTAT              integer                    NOT NULL,
  TANCAT_DATA        timestamp without time zone,
  TANCAT_MOTIU       character varying(1024),
  ANIO               integer                    NOT NULL,
  SEQUENCIA          bigint                     NOT NULL,
  CODI               character varying(256)     NOT NULL,
  NTI_VERSION        character varying(5)       NOT NULL,
  NTI_IDENTIF        character varying(52)      NOT NULL,
  NTI_ORGANO         character varying(9)       NOT NULL,
  NTI_FECHA_APE      timestamp without time zone NOT NULL,
  NTI_CLASIF_SIA     character varying(30)       NOT NULL,
  METAEXPEDIENT_DOMINI_ID   BIGINT,
  SISTRA_BANTEL_NUM  character varying(16),
  SISTRA_PUBLICAT    boolean,
  SISTRA_UNITAT_ADM  character varying(9),
  SISTRA_CLAU        character varying(100),
  AGAFAT_PER_CODI    character varying(64),
  METAEXPEDIENT_ID   bigint                     NOT NULL,
  ORGAN_GESTOR_ID    bigint                     NOT NULL,
  EXPEDIENT_ESTAT_ID BIGINT,
  GRUP_ID BIGINT
);


CREATE TABLE IPA_EXPEDIENT_REL
(
  EXPEDIENT_ID       BIGINT                     NOT NULL,
  EXPEDIENT_REL_ID   bigint                     NOT NULL
);

CREATE TABLE IPA_EXPEDIENT_ORGANPARE
(
    ID                      bigint NOT NULL,
    EXPEDIENT_ID            bigint NOT NULL,
    META_EXPEDIENT_ORGAN_ID bigint NOT NULL,
    CREATEDDATE          timestamp without time zone,
    CREATEDBY_CODI       character varying(64),
    LASTMODIFIEDDATE     timestamp without time zone,
    LASTMODIFIEDBY_CODI  character varying(64)
);


CREATE TABLE IPA_DOCUMENT
(
  ID                   BIGINT                   NOT NULL,
  TIPUS                integer                  NOT NULL,
  ESTAT                integer                  NOT NULL,
  UBICACIO             character varying(255),
  DATA                 timestamp without time zone NOT NULL,
  DATA_CAPTURA         timestamp without time zone NOT NULL,
  CUSTODIA_DATA        timestamp without time zone,
  CUSTODIA_ID          character varying(256),
  CUSTODIA_CSV         character varying(256),
  FITXER_NOM           character varying(256),
  FITXER_CONTENT_TYPE  character varying(256),
  FITXER_CONTINGUT     oid,
  VERSIO_DARRERA       character varying(32),
  VERSIO_COUNT         integer                  NOT NULL,
  NTI_VERSION          character varying(5)     NOT NULL,
  NTI_IDENTIF          character varying(48)    NOT NULL,
  NTI_ORGANO           character varying(9)     NOT NULL,
  NTI_ORIGEN           character varying(2)     NOT NULL,
  NTI_ESTELA           character varying(4)     NOT NULL,
  NTI_TIPDOC           character varying(4)     NOT NULL,
  NTI_IDORIG           character varying(48),
  NTI_TIPFIR           character varying(4),
  NTI_CSV              character varying(256),
  NTI_CSVREG           character varying(512)
);


CREATE TABLE IPA_DOCUMENT_ENVIAMENT
(
  ID                   		BIGINT                      NOT NULL,
  DTYPE                		character varying(32)       NOT NULL,
  ESTAT                		character varying(255)      NOT NULL,
  ASSUMPTE             		character varying(256)      NOT NULL,
  OBSERVACIONS         		character varying(256),
  ENVIAT_DATA          		timestamp without time zone NOT NULL,
  PROCESSAT_DATA       		timestamp without time zone,
  CANCELAT_DATA        		timestamp without time zone,
  ERROR                		boolean,
  ERROR_DESC           		character varying(255),
  INTENT_NUM           		integer,
  INTENT_DATA          		timestamp without time zone,
  INTENT_PROXIM_DATA   		timestamp without time zone,
  NOT_TIPUS            		integer,
  NOT_DATA_PROG        		timestamp without time zone,
  NOT_RETARD           		integer,
  NOT_DATA_CADUCITAT   		timestamp without time zone,
  NOT_ENV_ID           		character varying(100),
  NOT_ENV_REF          		character varying(100),
  NOT_ENV_DAT_ESTAT    		character varying(20),
  NOT_ENV_DAT_DATA     		timestamp without time zone,
  NOT_ENV_DAT_ORIG     		character varying(20),
  NOT_ENV_CERT_DATA    		timestamp without time zone,
  NOT_ENV_CERT_ORIG    		character varying(20),
  NOT_ENV_CERT_ARXIUID 		character varying(50),
  PF_PRIORITAT         		integer,
  PF_CAD_DATA          		timestamp without time zone,
  PF_DOC_TIPUS         		character varying(64),
  PF_RESPONSABLES      		character varying(1024),
  PF_FLUX_TIPUS        		integer,
  PF_FLUX_ID          		character varying(64),
  PF_PORTAFIRMES_ID    		character varying(64),
  PF_CALLBACK_ESTAT    		integer,
  VF_CODI_USUARI 	   		CHARACTER VARYING(64),
  VF_TITOL 					CHARACTER VARYING(256),
  VF_DESCRIPCIO 			CHARACTER VARYING(256),
  VF_CODI_DISPOSITIU 		CHARACTER VARYING(64),
  VF_MESSAGE_CODE 			CHARACTER VARYING(64),
  VF_CALLBACK_ESTAT 		NUMBER(10,0),
  VF_CONTRASENYA_USUARI 	CHARACTER VARYING(64),
  VF_LECTURA_OBLIGATORIA 	BOOLEAN,
  VF_VIAFIRMA_DISPOSITIU 	BIGINT,
  PUB_TIPUS            		integer,
  DOCUMENT_ID          		BIGINT                      NOT NULL,
  EXPEDIENT_ID         		BIGINT                      NOT NULL,
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDDATE     		timestamp without time zone,
  CREATEDBY_CODI       		character varying(256),
  LASTMODIFIEDBY_CODI  		character varying(256),
  VERSION             		BIGINT                      NOT NULL,
  SERVEI_TIPUS 		   		CHARACTER VARYING(10),
  ENTREGA_POSTAL       		BOOLEAN,
  NOTIFICACIO_ESTAT         CHARACTER VARYING(255)
);


CREATE TABLE IPA_DOCUMENT_ENVIAMENT_DOC (
  DOCUMENT_ENVIAMENT_ID BIGINT                  NOT NULL,
  DOCUMENT_ID           bigint                  NOT NULL
);


CREATE TABLE IPA_EXPEDIENT_INTERESSAT
(
  EXPEDIENT_ID   BIGINT                         NOT NULL,
  INTERESSAT_ID  bigint                         NOT NULL
);



CREATE TABLE IPA_EXECUCIO_MASSIVA
(
  ID                   BIGINT   		NOT NULL,
  TIPUS                character varying(255)   NOT NULL,
  DATA_INICI	       timestamp without time zone,
  DATA_FI              timestamp without time zone,
  PFIRMES_MOTIU	       character varying(256),
  PFIRMES_PRIORI       character varying(255),
  PFIRMES_DATCAD       timestamp without time zone,
  ENVIAR_CORREU	       boolean,
  ENTITAT_ID	       bigint		NOT NULL,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);


CREATE TABLE IPA_MASSIVA_CONTINGUT
(
  ID                   BIGINT                   NOT NULL,
  EXECUCIO_MASSIVA_ID  bigint,
  CONTINGUT_ID         bigint,
  DATA_INICI	       timestamp without time zone,
  DATA_FI              timestamp without time zone,
  ESTAT	               character varying(255),
  ERROR	               character varying(2046),
  ORDRE	               bigint,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);


CREATE TABLE IPA_ACL_CLASS
(
  ID     BIGSERIAL                              NOT NULL,
  CLASS  character varying(100)                 NOT NULL
);


CREATE TABLE IPA_ACL_SID
(
  ID         BIGSERIAL                          NOT NULL,
  PRINCIPAL  boolean                            NOT NULL,
  SID        character varying(100)             NOT NULL
);


CREATE TABLE IPA_ACL_ENTRY
(
  ID                   BIGSERIAL                NOT NULL,
  ACL_OBJECT_IDENTITY  bigint                   NOT NULL,
  ACE_ORDER            bigint                   NOT NULL,
  SID                  bigint                   NOT NULL,
  MASK                 bigint                   NOT NULL,
  GRANTING             boolean                  NOT NULL,
  AUDIT_SUCCESS        boolean                  NOT NULL,
  AUDIT_FAILURE        boolean                  NOT NULL
);


CREATE TABLE IPA_ACL_OBJECT_IDENTITY
(
  ID                  BIGSERIAL                 NOT NULL,
  OBJECT_ID_CLASS     bigint                    NOT NULL,
  OBJECT_ID_IDENTITY  bigint                    NOT NULL,
  PARENT_OBJECT       bigint,
  OWNER_SID           bigint                    NOT NULL,
  ENTRIES_INHERITING  boolean                   NOT NULL
);


CREATE TABLE IPA_EXP_COMMENT
(
  ID                   BIGINT         NOT NULL,
  EXPEDIENT_ID         BIGINT 		  NOT NULL,
  TEXT				   character varying (1024),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64)
);

CREATE TABLE IPA_EXPEDIENT_ESTAT
(
	ID                  BIGINT               			NOT NULL,
	CODI 			    character varying(256)						NOT NULL,
	NOM 			    character varying(256)						NOT NULL,
	ORDRE 			    BIGINT							NOT NULL,
	COLOR 			    character varying(256),					  
    METAEXPEDIENT_ID    BIGINT,               
    INICIAL             boolean,
    RESPONSABLE_CODI    character varying(64),
    
	
    CREATEDDATE         timestamp without time zone,
    LASTMODIFIEDDATE    timestamp without time zone,
    CREATEDBY_CODI      character varying(256),
    LASTMODIFIEDBY_CODI character varying(256)
);


CREATE TABLE IPA_EXPEDIENT_PETICIO
(
  ID                   BIGINT               NOT NULL,
  IDENTIFICADOR        character varying (80)             NOT NULL,
  CLAU_ACCES           character varying (200)            NOT NULL,
  DATA_ALTA            timestamp without time zone             NOT NULL,
  ESTAT			       character varying (40)             NOT NULL,
  META_EXPEDIENT_NOM   character varying (256),
  EXP_PETICIO_ACCIO    character varying (20),
  REGISTRE_ID 		   BIGINT,
  CONSULTA_WS_ERROR    boolean,
  CONSULTA_WS_ERROR_DESC  character varying (4000),
  CONSULTA_WS_ERROR_DATE  timestamp without time zone,
  NOTIFICA_DIST_ERROR character varying (4000),
  EXPEDIENT_ID 				BIGINT,
  
  CREATEDDATE         timestamp without time zone,
  LASTMODIFIEDDATE    timestamp without time zone,
  CREATEDBY_CODI      character varying (256),
  LASTMODIFIEDBY_CODI character varying (256)

);



CREATE TABLE IPA_REGISTRE (
  ID 						BIGINT 		NOT NULL, 
  APLICACIO_CODI 			character varying (20), 
  APLICACIO_VERSIO 			character varying (15), 
  ASSUMPTE_CODI_CODI 		character varying (16), 
  ASSUMPTE_CODI_DESC 		character varying (100), 
  ASSUMPTE_TIPUS_CODI		character varying (16), 
  ASSUMPTE_TIPUS_DESC 		character varying (100), 
  DATA 						timestamp without time zone 	NOT NULL, 
  DOC_FISICA_CODI 			character varying (1), 
  DOC_FISICA_DESC 			character varying (100), 
  ENTITAT_CODI 				character varying (21) 	NOT NULL, 
  ENTITAT_DESC 				character varying (100), 
  EXPEDIENT_NUMERO 			character varying (80), 
  EXPOSA 					text, 
  EXTRACTE					character varying (240),
  PROCEDIMENT_CODI			character varying (20),
  IDENTIFICADOR 			character varying (100) 	NOT NULL, 
  IDIOMA_CODI 				character varying (2) 	NOT NULL, 
  IDIOMA_DESC 				character varying (100), 
  LLIBRE_CODI 				character varying (4) 	NOT NULL, 
  LLIBRE_DESC 				character varying (100), 
  OBSERVACIONS 				character varying (50), 
  OFICINA_CODI 				character varying (21) 	NOT NULL, 
  OFICINA_DESC 				character varying (100), 
  ORIGEN_DATA 				timestamp without time zone, 
  ORIGEN_REGISTRE_NUM 		character varying (80), 
  REF_EXTERNA 				character varying (16), 
  SOLICITA 					text, 
  TRANSPORT_NUM 			character varying (20), 
  TRANSPORT_TIPUS_CODI 		character varying (2), 
  TRANSPORT_TIPUS_DESC 		character varying (100), 
  USUARI_CODI 				character varying (20), 
  USUARI_NOM 				character varying (80), 
  DESTI_CODI 				character varying (21) 	NOT NULL, 
  DESTI_DESCRIPCIO 			character varying (100),
  ENTITAT_ID				BIGINT 		NOT NULL,

  CREATEDDATE         timestamp without time zone,
  LASTMODIFIEDDATE    timestamp without time zone,
  CREATEDBY_CODI      character varying (256),
  LASTMODIFIEDBY_CODI character varying (256)
);


CREATE TABLE IPA_REGISTRE_ANNEX (
	ID 						BIGINT 		NOT NULL, 
	CONTINGUT BLOB,
	FIRMA_CONTINGUT BLOB,
	FIRMA_PERFIL character varying (4),
	FIRMA_TAMANY integer,
	FIRMA_TIPUS character varying (4),
	NOM character varying (80) NOT NULL,
	FIRMA_NOM character varying(80),
	NTI_FECHA_CAPTURA timestamp without time zone NOT NULL,
	NTI_ORIGEN character varying (20) NOT NULL,
	NTI_TIPO_DOC character varying (20) NOT NULL,
	OBSERVACIONS character varying (50),
	SICRES_TIPO_DOC character varying (20)  NOT NULL,
	SICRES_VALIDEZ_DOC character varying (30),
	TAMANY integer NOT NULL,
	TIPUS_MIME character varying (30),
	TITOL character varying (200) NOT NULL,
	UUID character varying (100),
	REGISTRE_ID BIGINT NOT NULL,
	ESTAT character varying (20),
	ERROR character varying (4000),
	NTI_ESTADO_ELABORACIO character varying(50) NOT NULL,
	
	CREATEDDATE         timestamp without time zone,
  	LASTMODIFIEDDATE    timestamp without time zone,
  	CREATEDBY_CODI      character varying (256),
  	LASTMODIFIEDBY_CODI character varying (256)
);


CREATE TABLE IPA_REGISTRE_INTERESSAT (
	ID 						BIGINT 		NOT NULL, 
	ADRESA character varying (160),
	CANAL character varying (30),
	CP character varying (5),
	DOC_NUMERO character varying (17),
	DOC_TIPUS character varying (15),
	EMAIL character varying (160),
	LLINATGE1 character varying (30),
	LLINATGE2 character varying (30),
	MUNICIPI_CODI character varying (100),
	NOM character varying (30),
	OBSERVACIONS character varying (160),
	PAIS_CODI character varying (4),
	PROVINCIA_CODI character varying (100),
	RAO_SOCIAL character varying (80),
	TELEFON character varying (20),
	TIPUS character varying (40) NOT NULL,
	REPRESENTANT_ID BIGINT,
	REGISTRE_ID BIGINT,
	PAIS character varying(200),
	PROVINCIA character varying(200),
	MUNICIPI character varying(200),	
	ORGAN_CODI CHARACTER VARYING(9),
	
    CREATEDDATE         timestamp without time zone,
    LASTMODIFIEDDATE    timestamp without time zone,
    CREATEDBY_CODI      character varying (256),
    LASTMODIFIEDBY_CODI character varying (256)
);



CREATE TABLE IPA_DOCUMENT_ENVIAMENT_INTER
(
  ID                   	BIGINT NOT NULL,
  DOCUMENT_ENVIAMENT_ID BIGINT NOT NULL,
  INTERESSAT_ID 		BIGINT NOT NULL,
  NOT_ENV_REF   		CHARACTER VARYING (100),
  CREATEDDATE         timestamp without time zone,
  LASTMODIFIEDDATE    timestamp without time zone,
  CREATEDBY_CODI      character varying (256),
  LASTMODIFIEDBY_CODI character varying (256),
  NOT_ENV_DAT_DATA     		TIMESTAMP WITHOUT TIMEZONE,
  NOT_ENV_DAT_ORIG     		CHARACTER VARYING(20),
  NOT_ENV_CERT_DATA    		TIMESTAMP WITHOUT TIMEZONE,
  NOT_ENV_CERT_ORIG    		CHARACTER VARYING(20),
  ERROR                		BOOLEAN,
  ERROR_DESC           		CHARACTER VARYING(2048)
);

CREATE TABLE IPA_VIAFIRMA_USUARI (
	CODI			CHARACTER VARYING(64)			NOT NULL,
	CONTRASENYA		CHARACTER VARYING(64)			NOT NULL,
	DESCRIPCIO		CHARACTER VARYING(64)			NOT NULL
);

CREATE TABLE IPA_USUARI_VIAFIRMA_RIPEA (
    ID                    BIGINT            			NOT NULL,
    VIAFIRMA_USER_CODI    CHARACTER VARYING(64)         NOT NULL,
    RIPEA_USER_CODI       CHARACTER VARYING(64)         NOT NULL
);

CREATE TABLE IPA_DOCUMENT_ENVIAMENT_DIS (
    ID                      BIGINT               NOT NULL,
    CODI                    CHARACTER VARYING(64),
    CODI_APLICACIO          CHARACTER VARYING(64),
    CODI_USUARI             CHARACTER VARYING(64),
    DESCRIPCIO              CHARACTER VARYING(255),
    LOCALE                  CHARACTER VARYING(10),
    ESTAT                   CHARACTER VARYING(64),
    TOKEN                   CHARACTER VARYING(255),
    IDENTIFICADOR           CHARACTER VARYING(64),
    TIPUS                   CHARACTER VARYING(64),
    EMAIL_USUARI            CHARACTER VARYING(64),
    IDENTIFICADOR_NAC       CHARACTER VARYING(64),
    CREATEDDATE             timestamp without time zone,
    LASTMODIFIEDDATE        timestamp without time zone,
    CREATEDBY_CODI          CHARACTER VARYING(256),
    LASTMODIFIEDBY_CODI     CHARACTER VARYING(256)
);

 CREATE TABLE IPA_EXPEDIENT_TASCA
(
  ID BIGINT NOT NULL,
  EXPEDIENT_IDBIGINT NOT NULL,
  METAEXP_TASCA_ID BIGINT NOT NULL,
  RESPONSABLE_CODI  CHARACTER VARYING(64) NOT NULL, 
  DATA_INICI TIMESTAMP WITHOUT TIMEZONE NOT NULL, 
  DATA_FI TIMESTAMP WITHOUT TIMEZONE, 
  ESTAT CHARACTER VARYING(20),
  MOTIU_REBUIG CHARACTER VARYING(1024),
  CREATEDDATE          TIMESTAMP WITHOUT TIMEZONE,
  CREATEDBY_CODI        CHARACTER VARYING(64),
  LASTMODIFIEDDATE     TIMESTAMP WITHOUT TIMEZONE,
  LASTMODIFIEDBY_CODI   CHARACTER VARYING(64),
  DATA_LIMIT TIMESTAMP WITHOUT TIMEZONE
);

CREATE TABLE IPA_METAEXP_DOMINI
(
  ID                   BIGINT           NOT NULL,
  CODI                 CHARACTER VARYING(64)			NOT NULL,
  NOM                  CHARACTER VARYING(256)	    NOT NULL,
  DESCRIPCIO           CHARACTER VARYING(1024),
  ENTITAT_ID           BIGINT           NOT NULL,
  CREATEDDATE          TIMESTAMP WITHOUT TIMEZONE,
  CREATEDBY_CODI       CHARACTER VARYING(64),
  LASTMODIFIEDDATE     TIMESTAMP WITHOUT TIMEZONE,
  LASTMODIFIEDBY_CODI  CHARACTER VARYING(64),
  META_EXPEDIENT_ID    NUMBER(19)           NOT NULL
);

CREATE TABLE IPA_GRUP
(
    ID BIGSERIAL NOT NULL,
    ROL CHARACTER VARYING(50) NOT NULL,
    DESCRIPCIO CHARACTER VARYING(512) NOT NULL,
    ENTITAT_ID BIGINT NOT NULL,
    CREATEDBY_CODI CHARACTER VARYING(64),
    CREATEDDATE TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI CHARACTER VARYING(64),
    LASTMODIFIEDDATE TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IPA_METAEXPEDIENT_GRUP (
  METAEXPEDIENT_ID  BIGSERIAL             NOT NULL,
  GRUP_ID           BIGSERIAL             NOT NULL
);

CREATE TABLE IPA_ORGAN_GESTOR
(
	ID					BIGSERIAL				NOT NULL,
	CODI				CHARACTER VARYING(64)	NOT NULL,
	NOM					CHARACTER VARYING(1000)	NOT NULL,
  	ENTITAT_ID			BIGINT					NOT NULL,
  	PARE_ID 			BIGINT,
  	ACTIU 				boolean DEFAULT '1' 	NOT NULL,
    CREATEDBY_CODI 		CHARACTER VARYING(64),
    CREATEDDATE 		TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI CHARACTER VARYING(64),
    LASTMODIFIEDDATE 	TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IPA_EXPEDIENT_SEGUIDOR
(
  EXPEDIENT_ID   BIGINT                     NOT NULL,
  SEGUIDOR_CODI	 CHARACTER VARYING(64)      NOT NULL
);

CREATE TABLE IPA_PORTAFIRMES_BLOCK
(
	ID						BIGSERIAL					NOT NULL,
	BLK_ORDER 				BIGSERIAL					NOT NULL,
	DOCUMENT_ENVIAMENT_ID	BIGSERIAL					NOT NULL,
	CREATEDBY_CODI 			CHARACTER VARYING(64),
	CREATEDDATE 			TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI 	CHARACTER VARYING(64),
    LASTMODIFIEDDATE 		TIMESTAMP WITHOUT TIME ZONE,
    VERSION       		    BIGSERIAL     				NOT NULL
);

CREATE TABLE IPA_PORTAFIRMES_BLOCK_INFO
(
	ID						BIGSERIAL				NOT NULL,
	PORTAFIRMES_BLOCK_ID	BIGSERIAL				NOT NULL,
	PORTAFIRMES_SIGNER_NOM	CHARACTER VARYING(50),
	PORTAFIRMES_SIGNER_CODI	CHARACTER VARYING(50)	NOT NULL,
	PORTAFIRMES_SIGNER_ID	CHARACTER VARYING(9),
	SIGNED					BOOLEAN		NOT NULL,
	CREATEDBY_CODI 			CHARACTER VARYING(64),
	CREATEDDATE 			TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI 	CHARACTER VARYING(64),
    LASTMODIFIEDDATE 		TIMESTAMP WITHOUT TIME ZONE,
    VERSION       		    BIGSERIAL     	 		NOT NULL
);


CREATE TABLE IPA_HISTORIC
(
	ID						 BIGSERIAL	    NOT NULL,
    ENTITAT_ID               BIGSERIAL     NOT NULL,
    ORGAN_ID                 BIGSERIAL,
    METAEXPEDIENT_ID         BIGSERIAL     NOT NULL,
    TIPUS                    BIGSERIAL     NOT NULL,
    DATA                     TIMESTAMP WITHOUT TIME ZONE   NOT NULL,
    N_EXPED_CREATS	         BIGSERIAL	    NOT NULL,
    N_EXPED_CREATS_ACUM      BIGSERIAL	    NOT NULL,
    N_EXPED_OBERTS	         BIGSERIAL	    NOT NULL,
    N_EXPED_OBERTS_ACUM      BIGSERIAL	    NOT NULL,
    N_EXPED_TANCATS          BIGSERIAL	    NOT NULL,
    N_EXPED_TANCATS_ACUM     BIGSERIAL	    NOT NULL,
    CREATEDBY_CODI           CHARACTER VARYING(64),
    CREATEDDATE              TIMESTAMP(6),
    LASTMODIFIEDBY_CODI      CHARACTER VARYING(64),
    LASTMODIFIEDDATE         TIMESTAMP WITHOUT TIME ZONE,
    VERSION                  BIGSERIAL      NOT NULL
);

CREATE TABLE IPA_HIST_EXPEDIENT
(
	ID						 BIGSERIAL	    NOT NULL,
	N_EXPED_AMB_ALERTES      BIGSERIAL	    NOT NULL,
	N_EXPED_ERRORS_VALID     BIGSERIAL	    NOT NULL,
	N_DOCS_PENDENTS_SIGN     BIGSERIAL     NOT NULL,
	N_DOCS_SIGN              BIGSERIAL		NOT NULL,
	N_DOCS_PENDENTS_NOTIF    BIGSERIAL		NOT NULL,
	N_DOCS_NOTIF             BIGSERIAL	    NOT NULL
);

CREATE TABLE IPA_HIST_EXP_USUARI
(
	ID						 BIGSERIAL	    NOT NULL,
  	USUARI_CODI              CHARACTER VARYING(64)   NOT NULL,
	N_TASQUES_TRAMITADES     BIGSERIAL	    NOT NULL
);

CREATE TABLE IPA_HIST_EXP_INTERESSAT
(
	ID						 BIGSERIAL					NOT NULL,
  	INTERESSAT_DOC_NUM       CHARACTER VARYING(17)		NOT NULL
);

CREATE TABLE IPA_METAEXPEDIENT_CARPETA
(
	ID						 BIGSERIAL	        		NOT NULL,
  	NOM                      CHARACTER VARYING(1024)    NOT NULL,
	PARE_ID                  BIGSERIAL	        		NOT NULL,
    META_EXPEDIENT_ID        BIGSERIAL         			NOT NULL,
    CREATEDBY_CODI           CHARACTER VARYING(64),
    CREATEDDATE              TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI      CHARACTER VARYING(64),
    LASTMODIFIEDDATE         TIMESTAMP WITHOUT TIME ZONE,
    VERSION                  BIGSERIAL(19)      		NOT NULL
);
