
CREATE TABLE IPA_ALERTA
(
  ID                   BIGINT                          NOT NULL,
  TEXT                 character varying(256)          NOT NULL,
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
  REBRE_EMAILS  		boolean,
  EMAILS_AGRUPATS		boolean,
  VERSION       		bigint                      NOT NULL
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
  LASTMODIFIEDDATE     timestamp without time zone
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
  ID                 BIGINT                     NOT NULL,
  PARE_ID            bigint,
  CLASIF_SIA         character varying(30)       NOT NULL,
  SERIE_DOC          character varying(30)      NOT NULL,
  NOT_ACTIVA         boolean                    NOT NULL,
  NOT_SEU_PROC_CODI  character varying(44),
  NOT_SEU_REG_LIB    character varying(4),
  NOT_SEU_REG_OFI    character varying(9),
  NOT_SEU_REG_ORG    character varying(9),
  NOT_SEU_EXP_UNI    character varying(9),
  NOT_AVIS_TITOL     character varying(256),
  NOT_AVIS_TEXT      character varying(1024),
  NOT_AVIS_TEXTM     character varying(200),
  NOT_OFICI_TITOL    character varying(256),
  NOT_OFICI_TEXT     character varying(1024)
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
  META_EXPEDIENT_ID       bigint                   NOT NULL
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
  LASTMODIFIEDDATE      timestamp without time zone
);


CREATE TABLE IPA_INTERESSAT
(
  ID                   BIGINT                   NOT NULL,
  DTYPE                character varying(256)   NOT NULL,
  NOM                  character varying(30)    NOT NULL,
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
  ORGAN_NOM	       character varying(80),
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
  VERSION              bigint               NOT NULL
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
  VERSION              bigint                   NOT NULL
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
  SISTRA_BANTEL_NUM  character varying(16),
  SISTRA_PUBLICAT    boolean,
  SISTRA_UNITAT_ADM  character varying(9),
  SISTRA_CLAU        character varying(100),
  AGAFAT_PER_CODI    character varying(64),
  EXPEDIENT_ESTAT_ID BIGINT
);


CREATE TABLE IPA_EXPEDIENT_REL
(
  EXPEDIENT_ID       BIGINT                     NOT NULL,
  EXPEDIENT_REL_ID   bigint                     NOT NULL
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
  ID                   BIGINT                      NOT NULL,
  DTYPE                character varying(32)       NOT NULL,
  ESTAT                character varying(255)      NOT NULL,
  ASSUMPTE             character varying(256)      NOT NULL,
  OBSERVACIONS         character varying(256),
  ENVIAT_DATA          timestamp without time zone NOT NULL,
  PROCESSAT_DATA       timestamp without time zone,
  CANCELAT_DATA        timestamp without time zone,
  ERROR                boolean,
  ERROR_DESC           character varying(255),
  INTENT_NUM           integer,
  INTENT_DATA          timestamp without time zone,
  INTENT_PROXIM_DATA   timestamp without time zone,
  NOT_TIPUS            integer,
  NOT_DATA_PROG        timestamp without time zone,
  NOT_RETARD           integer,
  NOT_DATA_CADUCITAT   timestamp without time zone,
  NOT_INTERESSAT_ID    BIGINT,
  NOT_SEU_IDIOMA       character varying(2),
  NOT_SEU_AVIS_TITOL   character varying(256), 
  NOT_SEU_AVIS_TEXT    character varying(1024),
  NOT_SEU_AVIS_TEXTM   character varying(200),
  NOT_SEU_OFICI_TITOL  character varying(256),
  NOT_SEU_OFICI_TEXT   character varying(1024),
  NOT_ENV_ID           character varying(100),
  NOT_ENV_REF          character varying(100),
  NOT_ENV_DAT_ESTAT    character varying(20),
  NOT_ENV_DAT_DATA     timestamp without time zone,
  NOT_ENV_DAT_ORIG     character varying(20),
  NOT_ENV_CERT_DATA    timestamp without time zone,
  NOT_ENV_CERT_ORIG    character varying(20),
  NOT_ENV_CERT_ARXIUID character varying(50),
  PF_PRIORITAT         integer,
  PF_CAD_DATA          timestamp without time zone,
  PF_DOC_TIPUS         character varying(64),
  PF_RESPONSABLES      character varying(1024),
  PF_FLUX_TIPUS        integer,
  PF_FLUX_ID           character varying(64),
  PF_PORTAFIRMES_ID    character varying(64),
  PF_CALLBACK_ESTAT    integer,
  PUB_TIPUS            integer,
  DOCUMENT_ID          BIGINT                      NOT NULL,
  EXPEDIENT_ID         BIGINT                      NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDBY_CODI  character varying(256),
  VERSION              BIGINT                      NOT NULL
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


CREATE TABLE IPA_REGISTRE
(
  ID                   BIGINT                   NOT NULL,
  TIPUS                character varying(1)     NOT NULL,
  UNITAT_ADM           character varying(21)    NOT NULL,
  UNITAT_ADM_DESC      character varying(100),
  NUMERO               character varying(100)   NOT NULL,
  DATA                 timestamp without time zone NOT NULL,
  IDENTIFICADOR        character varying(100)   NOT NULL,
  ENTITAT_CODI         character varying(21)    NOT NULL,
  ENTITAT_DESC         character varying(100),
  OFICINA_CODI         character varying(21)    NOT NULL,
  OFICINA_DESC         character varying(100),
  LLIBRE_CODI          character varying(4)     NOT NULL,
  LLIBRE_DESC          character varying(100),
  EXTRACTE             character varying(240),
  ASSUMPTE_TIPUS_CODI  character varying(16)    NOT NULL,
  ASSUMPTE_TIPUS_DESC  character varying(100),
  ASSUMPTE_CODI        character varying(16),
  ASSUMPTE_DESC        character varying(100),
  REFERENCIA           character varying(16),
  EXPEDIENT_NUM        character varying(80),
  NUM_ORIG 			   character varying(80),
  IDIOMA_CODI          character varying(2)     NOT NULL,
  IDIOMA_DESC          character varying(100),
  TRANSPORT_TIPUS_CODI character varying(2),
  TRANSPORT_TIPUS_DESC character varying(100),
  TRANSPORT_NUM        character varying(20),
  USUARI_CODI          character varying(20),
  USUARI_NOM           character varying(80),
  USUARI_CONTACTE      character varying(160),
  APLICACIO_CODI       character varying(20),
  APLICACIO_VERSIO     character varying(15),
  DOCFIS_CODI          character varying(1),
  DOCFIS_DESC          character varying(100),
  OBSERVACIONS         character varying(50),
  EXPOSA               character varying(4000),
  SOLICITA             character varying(4000),
  MOTIU_REBUIG         character varying(4000),
  PROCES_DATA          timestamp without time zone,
  PROCES_ESTAT         character varying(16)    NOT NULL,
  PROCES_INTENTS       integer,
  PROCES_ERROR         character varying(1024),
  REGLA_ID             bigint,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(256),
  PROCES_ESTAT_SISTRA  character varying(16),
  SISTRA_ID_TRAM       character varying(20),
  SISTRA_ID_PROC       character varying(100),
  DATA_ORIG            timestamp without time zone,
  OFICINA_ORIG_CODI    character varying(21),
  OFICINA_ORIG_DESC    character varying(100),
  JUSTIFICANT_ARXIU_UUID character varying(100),
  LLEGIDA              boolean,
  EXPEDIENT_ARXIU_UUID  character varying(100)
);


CREATE TABLE IPA_REGISTRE_ANNEX
(
  ID                   BIGINT                   NOT NULL,
  TITOL                character varying(200)   NOT NULL,
  FITXER_NOM           character varying(80)    NOT NULL,
  FITXER_TAMANY        integer                  NOT NULL,
  FITXER_MIME          character varying(100),
  FITXER_ARXIU_UUID     character varying(100)   NOT NULL,
  DATA_CAPTURA         timestamp without time zone NOT NULL,
  LOCALITZACIO         character varying(80),
  ORIGEN_CIUADM        character varying(1)     NOT NULL,
  NTI_TIPUS_DOC        character varying(4)     NOT NULL,
  SICRES_TIPUS_DOC     character varying(2)     NOT NULL,
  NTI_ELABORACIO_ESTAT character varying(4),
  OBSERVACIONS         character varying(50),
  FIRMA_MODE           integer,
  FIRMA_FITXER_NOM     character varying(80),
  FIRMA_FITXER_TAMANY  integer,
  FIRMA_FITXER_MIME    character varying(30),
  FIRMA_FITXER_ARXIU_UUID  character varying(100),
  FIRMA_CSV            character varying(50),
  TIMESTAMP            character varying(100),
  VALIDACIO_OCSP       character varying(100),
  REGISTRE_ID          bigint                   NOT NULL,
  VERSION              bigint                   NOT NULL,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(256),
  GESDOC_DOC_ID 	   character varying(50)
);


CREATE TABLE IPA_REGISTRE_ANNEX_FIRMA
(
  ID                   BIGINT                   	NOT NULL,
  TIPUS		           character varying(30),
  PERFIL    	       character varying(30),
  FITXER_NOM           character varying(80),
  TIPUS_MIME           character varying(30),
  CSV_REGULACIO		   character varying(640),
  ANNEX_ID             bigint                    	NOT NULL,
  AUTOFIRMA            boolean,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(256),
  GESDOC_FIR_ID 	   character varying(50)
);


CREATE TABLE IPA_REGISTRE_INTER
(
  ID                   BIGINT                   NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  ADRESA               character varying(640),
  CANAL_PREF           character varying(8),
  CODI_POSTAL          character varying(20),
  DOC_NUM              character varying(68),
  DOC_TIPUS            character varying(4),
  EMAIL                character varying(640),
  EMAIL_HAB            character varying(640),
  LLINATGE1            character varying(120),
  LLINATGE2            character varying(120),
  MUNICIPI             character varying(100),
  NOM                  character varying(120),
  OBSERVACIONS         character varying(640),
  PAIS                 character varying(16),
  PROVINCIA            character varying(100),
  RAO_SOCIAL           character varying(320),
  TELEFON              character varying(80),
  TIPUS                character varying(4)     NOT NULL,
  VERSION              bigint                   NOT NULL,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDBY_CODI  character varying(256),
  REGISTRE_ID          bigint                   NOT NULL,
  REPRESENTANT_ID      bigint,
  REPRESENTAT_ID       bigint
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