
CREATE TABLE IPA_USUARI
(
  CODI          character varying(64)           NOT NULL,
  INICIALITZAT  boolean,
  NIF           character varying(9)            NOT NULL,
  NOM           character varying(200),
  EMAIL         character varying(200),
  IDIOMA 		character varying(2) 	 		DEFAULT 'CA'
  VERSION       bigint                          NOT NULL
);


CREATE TABLE IPA_ENTITAT
(
  ID                   BIGSERIAL                NOT NULL,
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
  ID                   BIGSERIAL               NOT NULL,
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


CREATE TABLE IPA_METADOCUMENT
(
  ID                      BIGSERIAL            NOT NULL,
  GLOBAL_EXPEDIENT        boolean,
  GLOBAL_MULTIPLICITAT    character varying(1020),
  GLOBAL_READONLY         boolean,
  FIRMA_APPLET            boolean,
  SIGNATURA_TIPMIME       character varying(64),
  FIRMA_PFIRMA            boolean,
  PORTAFIRMES_DOCTIP      character varying(64),
  PORTAFIRMES_FLUXID      character varying(64),
  CUSTODIA_POLITICA       character varying(64),
  PLANTILLA_NOM           character varying(256),
  PLANTILLA_CONTENT_TYPE  character varying(256),
  PLANTILLA_CONTINGUT     bytea
);


CREATE TABLE IPA_METADADA
(
  ID                    BIGSERIAL              NOT NULL,
  CREATEDDATE           timestamp without time zone,
  LASTMODIFIEDDATE      timestamp without time zone,
  ACTIVA                boolean,
  CODI                  character varying(64)   NOT NULL,
  DESCRIPCIO            character varying(1024),
  GLOBAL_CARPETA        boolean,
  GLOBAL_DOCUMENT       boolean,
  GLOBAL_EXPEDIENT      boolean,
  GLOBAL_MULTIPLICITAT  character varying(1020),
  GLOBAL_READONLY       boolean,
  NOM                   character varying(256)   NOT NULL,
  TIPUS                 character varying(1020)  NOT NULL,
  VERSION               bigint                   NOT NULL,
  CREATEDBY_CODI        character varying(64),
  LASTMODIFIEDBY_CODI   character varying(64),
  ENTITAT_ID            bigint                   NOT NULL
);


CREATE TABLE IPA_METAEXPEDIENT
(
  ID             BIGSERIAL                       NOT NULL,
  PARE_ID        bigint,
  CLASSIFICACIO  character varying(30)
);

CREATE TABLE IPA_METAEXP_SEQ
(
  ID                   BIGSERIAL                 NOT NULL,
  ANIO                 integer,
  VALOR                bigint,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  META_EXPEDIENT_ID    bigint                    NOT NULL
);

CREATE TABLE IPA_INTERESSAT
(
  DTYPE                character varying(256)    NOT NULL,
  ID                   BIGSERIAL                 NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  NOM                  character varying(256)    NOT NULL,
  VERSION              bigint                    NOT NULL,
  IDENTIFICADOR        character varying(9),
  LLINATGES            character varying(256),
  NIF                  character varying(9),
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  ENTITAT_ID           bigint                    NOT NULL
);


CREATE TABLE IPA_ARXIU
(
  ID                   BIGSERIAL                 NOT NULL,
  ACTIU                boolean,
  UNITAT_CODI          character varying(9)      NOT NULL
);


CREATE TABLE IPA_CONTENIDOR
(
  ID                   BIGSERIAL                 NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  ESBORRAT             integer,
  NOM                  character varying(256)    NOT NULL,
  TIPUS_CONT           integer                NOT NULL,
  VERSION              bigint                    NOT NULL,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  CONTMOV_ID           bigint,
  ENTITAT_ID           bigint                    NOT NULL,
  PARE_ID              bigint
);


CREATE TABLE IPA_CONT_MOV
(
  ID                   BIGSERIAL                 NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  COMENTARI            character varying(256),
  DATA                 timestamp without time zone,
  VERSION              bigint                    NOT NULL,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  CONTENIDOR_ID        bigint                    NOT NULL,
  ORIGEN_ID            bigint,
  DESTI_ID             bigint                    NOT NULL,
  REMITENT_ID          character varying(64)
);


CREATE TABLE IPA_CONT_LOG
(
  ID                   BIGSERIAL                  NOT NULL,
  CONTENIDOR_ID        bigint                     NOT NULL,
  DATA                 timestamp without time zone                   NOT NULL,
  USUARI_ID            character varying (255)                 NOT NULL,
  PARE_ID              bigint,
  CONTENIDOR_MOV_ID    bigint,
  OBJECTE_ID           bigint,
  OBJECTE_LOG_TIPUS    integer,
  OBJECTE_TIPUS        integer,
  PARAM1               character varying (256),
  PARAM2               character varying (256),
  TIPUS                integer,
  VERSION              bigint
);


CREATE TABLE IPA_ESCRIPTORI
(
  ID         BIGSERIAL                            NOT NULL,
  USUARI_ID  character varying(64)                NOT NULL
);


CREATE TABLE IPA_METAEXPEDIENT_METADOCUMENT
(
  ID                   BIGSERIAL                  NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  MULTIPLICITAT        character varying(1020),
  ORDRE                integer                 NOT NULL,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  METADOCUMENT_ID      bigint                     NOT NULL,
  METAEXPEDIENT_ID     bigint                     NOT NULL,
  READONLY             boolean
);


CREATE TABLE IPA_NODE
(
  ID                  BIGSERIAL                   NOT NULL,
  METANODE_ID         bigint
);


CREATE TABLE IPA_METANODE_METADADA
(
  ID                   BIGSERIAL                  NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  MULTIPLICITAT        character varying(1020),
  ORDRE                integer                 NOT NULL,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  METADADA_ID          bigint                     NOT NULL,
  METANODE_ID          bigint                     NOT NULL,
  READONLY             boolean
);


CREATE TABLE IPA_CARPETA
(
  TIPUS  character varying(1020)                  NOT NULL,
  ID     BIGSERIAL                                NOT NULL
);


CREATE TABLE IPA_DADA
(
  ID                   BIGSERIAL                  NOT NULL,
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
  ID            BIGSERIAL                         NOT NULL,
  OBERT         boolean,
  TANCAT_MOTIU  character varying(1024),
  ANIO          integer,
  SEQUENCIA     bigint,
  ARXIU_ID      bigint                            NOT NULL
);


CREATE TABLE IPA_DOCUMENT
(
  ID              BIGSERIAL                       NOT NULL,
  DARRERA_VERSIO  integer                      NOT NULL,
  DATA            timestamp without time zone,
  EXPEDIENT_ID    bigint
);


CREATE TABLE IPA_DOCUMENT_VERSIO
(
  ID                    BIGSERIAL                 NOT NULL,
  CREATEDDATE           timestamp without time zone,
  LASTMODIFIEDDATE      timestamp without time zone,
  ARXIU_NOM             character varying(256)    NOT NULL,
  ARXIU_CONTENT_TYPE    character varying(256)    NOT NULL,
  ARXIU_CONTENT_LENGTH  bigint                    NOT NULL,
  ARXIU_CONTINGUT       bytea,
  ARXIU_GESDOC_ID       character varying(100),
  VERSIO                integer                NOT NULL,
  VERSION               bigint                    NOT NULL,
  CREATEDBY_CODI        character varying(64),
  LASTMODIFIEDBY_CODI   character varying(64),
  DOCUMENT_ID           bigint                    NOT NULL,
  CUSTODIAT             boolean,
  CUSTODIA_ID           character varying(256),
  CUSTODIA_URL          character varying(1024)
);


CREATE TABLE IPA_DOCUMENT_PFIRMES
(
  ID                    BIGSERIAL                 NOT NULL,
  CREATEDDATE           timestamp without time zone,
  LASTMODIFIEDDATE      timestamp without time zone,
  PFIRMES_ID            bigint                    NOT NULL,
  VERSIO                integer                NOT NULL,
  VERSION               bigint                    NOT NULL,
  CREATEDBY_CODI        character varying(64),
  LASTMODIFIEDBY_CODI   character varying(64),
  DOCUMENT_ID           bigint                    NOT NULL,
  DATA_CADUCITAT        DATE,
  MOTIU                 character varying(256)    NOT NULL,
  PFIRMES_ESTAT         character varying(255)    NOT NULL,
  PRIORITAT             character varying(255)    NOT NULL,
  CALLBACK_DARRER       timestamp without time zone,
  CALLBACK_COUNT        integer,
  ERROR_DESC            character varying(1024)
);


CREATE TABLE IPA_EXPEDIENT_INTERESSAT
(
  EXPEDIENT_ID   bigint                           NOT NULL,
  INTERESSAT_ID  bigint                           NOT NULL
);


CREATE TABLE IPA_BUSTIA
(
  ID           BIGSERIAL                          NOT NULL,
  UNITAT_CODI  character varying(9)               NOT NULL,
  PER_DEFECTE  boolean,
  ACTIVA       boolean
);


CREATE TABLE IPA_REGISTRE
(
  ID                   bigint                     NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  ACCIO                integer                 NOT NULL,
  APLICACIO_EMISSORA   character varying(4),
  ASSUMPTE_CODI        character varying(16),
  ASSUMPTE_NUMEXP      character varying(80),
  ASSUMPTE_REFERENCIA  character varying(16),
  ASSUMPTE_RESUM       character varying(240)     NOT NULL,
  DATA                 timestamp without time zone             NOT NULL,
  DOCUMENTACIO_FIS     integer,
  ENTITAT_CODI         character varying(21)      NOT NULL,
  ENTITAT_NOM          character varying(80),
  NUMERO               character varying(20)      NOT NULL,
  OBSERVACIONS         character varying(50),
  MOTIU_REBUIG         character varying(1024),
  PROVA                boolean,
  TIPUS                integer                 NOT NULL,
  TRANSPORT_NUM        character varying(20),
  TRANSPORT_TIPUS      integer,
  USUARI_CONTACTE      character varying(160),
  USUARI_NOM           character varying(80),
  VERSION              bigint                     NOT NULL,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  CONTENIDOR_ID        bigint,
  ENTITAT_ID           bigint                     NOT NULL
);


CREATE TABLE IPA_REGISTRE_DOC
(
  ID                   BIGSERIAL                  NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  FITXER_NOM           character varying(80)      NOT NULL,
  GESDOC_ID            character varying(100),
  IDENTIFICADOR        character varying(50)      NOT NULL,
  OBSERVACIONS         character varying(50),
  TIPUS                integer                 NOT NULL,
  VALIDESA             integer,
  VERSION              bigint                     NOT NULL,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  REGISTRE_ID          bigint                     NOT NULL,
  DOCFIRMAT_ID         character varying(50)
);


CREATE TABLE IPA_REGISTRE_INTER
(
  ID                   BIGSERIAL                  NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  ADRESA               character varying(160),
  CANAL_PREF           integer,
  CODI_POSTAL          character varying(5),
  DOC_NUM              character varying(17),
  DOC_TIPUS            integer,
  EMAIL                character varying(160),
  EMAIL_HAB            character varying(160),
  LLINATGE1            character varying(30),
  LLINATGE2            character varying(30),
  MUNICIPI             character varying(5),
  NOM                  character varying(30),
  OBSERVACIONS         character varying(160),
  PAIS                 character varying(4),
  PROVINCIA            character varying(2),
  RAO_SOCIAL           character varying(80),
  REP_ADRESA           character varying(160),
  REP_CANAL_PREF       integer,
  REP_CODI_POSTAL      character varying(5),
  REP_DOC_NUM          character varying(17),
  REP_DOC_TIPUS        integer,
  REP_EMAIL            character varying(160),
  REP_EMAIL_HAB        character varying(160),
  REP_LLINATGE1        character varying(30),
  REP_LLINATGE2        character varying(30),
  REP_MUNICIPI         character varying(5),
  REP_NOM              character varying(30),
  REP_PAIS             character varying(4),
  REP_PROVINCIA        character varying(2),
  REP_RAO_SOCIAL       character varying(80),
  REP_TELEFON          character varying(20),
  TELEFON              character varying(20),
  VERSION              bigint                     NOT NULL,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  REGISTRE_ID          bigint                     NOT NULL
);


CREATE TABLE IPA_REGISTRE_MOV
(
   ID                    BIGSERIAL                NOT NULL,
   CREATEDDATE           timestamp without time zone,
   LASTMODIFIEDDATE      timestamp without time zone,
   COMENTARI             character varying(256),
   DATA                  timestamp without time zone           NOT NULL,
   VERSION               bigint,
   CREATEDBY_CODI        character varying(64),
   LASTMODIFIEDBY_CODI   character varying(64),
   DESTI_ID              bigint                   NOT NULL,
   ORIGEN_ID             bigint,
   REGISTRE_ID           bigint                   NOT NULL,
   REMITENT_ID           character varying (64)
);


CREATE TABLE IPA_ACL_CLASS
(
  ID     BIGSERIAL                                NOT NULL,
  CLASS  character varying(100)                   NOT NULL
);


CREATE TABLE IPA_ACL_SID
(
  ID         BIGSERIAL                            NOT NULL,
  PRINCIPAL  boolean                              NOT NULL,
  SID        character varying(100)               NOT NULL
);


CREATE TABLE IPA_ACL_ENTRY
(
  ID                   BIGSERIAL                  NOT NULL,
  ACL_OBJECT_IDENTITY  bigint                     NOT NULL,
  ACE_ORDER            bigint                     NOT NULL,
  SID                  bigint                     NOT NULL,
  MASK                 bigint                     NOT NULL,
  GRANTING             boolean                    NOT NULL,
  AUDIT_SUCCESS        boolean                    NOT NULL,
  AUDIT_FAILURE        boolean                    NOT NULL
);


CREATE TABLE IPA_ACL_OBJECT_IDENTITY
(
  ID                  BIGSERIAL                   NOT NULL,
  OBJECT_ID_CLASS     bigint                      NOT NULL,
  OBJECT_ID_IDENTITY  bigint                      NOT NULL,
  PARENT_OBJECT       bigint,
  OWNER_SID           bigint                      NOT NULL,
  ENTRIES_INHERITING  boolean                     NOT NULL
);
