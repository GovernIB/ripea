ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD NOT_ENV_REGISTRE_DATA TIMESTAMP WITHOUT TIMEZONE(6);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD NOT_ENV_REGISTRE_NUMERO BIGSERIAL(19);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT ADD NOT_ENV_REGISTRE_NUM_FORMATAT CHARACTER VARYING(50);

ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER ADD NOT_ENV_REGISTRE_DATA TIMESTAMP WITHOUT TIMEZONE(6);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER ADD NOT_ENV_REGISTRE_NUMERO BIGSERIAL(19);
ALTER TABLE IPA_DOCUMENT_ENVIAMENT_INTER ADD NOT_ENV_REGISTRE_NUM_FORMATAT CHARACTER VARYING(50);