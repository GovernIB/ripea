
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER 
ADD (
  NOT_ENV_DAT_ESTAT    		VARCHAR2(20),
  NOT_ENV_DAT_DATA     		TIMESTAMP(6),
  NOT_ENV_DAT_ORIG     		VARCHAR2(20),
  NOT_ENV_CERT_DATA    		TIMESTAMP(6),
  NOT_ENV_CERT_ORIG    		VARCHAR2(20),
  ERROR                		NUMBER(1),
  ERROR_DESC           		VARCHAR2(2048)
);


ALTER TABLE IPA_DOCUMENT_ENVIAMENT
ADD ( NOTIFICACIO_ESTAT    		VARCHAR2(255));


ALTER TABLE IPA_INTERESSAT ADD INCAPACITAT NUMBER(1);