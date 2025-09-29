--1660
CREATE TABLE ipa_explot_fet (
    id                  BIGINT NOT NULL,
    exp_obert           BIGINT NOT NULL,
    exp_obert_tot       BIGINT NOT NULL,
    exp_tancat          BIGINT NOT NULL,
    exp_tancat_tot      BIGINT NOT NULL,
    tas_pendent		 	BIGINT NOT NULL,
    tas_pendent_tot	 	BIGINT NOT NULL,
    tas_iniciada	 	BIGINT NOT NULL,
    tas_iniciada_tot  	BIGINT NOT NULL,
    tas_finalitzada	 	BIGINT NOT NULL,
    tas_finalitzada_tot	BIGINT NOT NULL,
    tas_cancelada	 	BIGINT NOT NULL,
    tas_cancelada_tot	BIGINT NOT NULL,
    tas_rebutjada	 	BIGINT NOT NULL,
    tas_rebutjada_tot	BIGINT NOT NULL,
    tas_agafada	 		BIGINT NOT NULL,
    tas_agafada_tot	 	BIGINT NOT NULL,
    tas_finalitzada     BIGINT NOT NULL,
    tas_finalitzada_tot BIGINT NOT NULL,
    ano_noves			BIGINT NOT NULL,
    ano_noves_tot		BIGINT NOT NULL,
    ano_processada		BIGINT NOT NULL,
    ano_processada_tot	BIGINT NOT NULL,
    ano_rebutjada		BIGINT NOT NULL,
    ano_rebutjada_tot	BIGINT NOT NULL,
    pin_enviats         BIGINT NOT NULL,
    pin_enviats_tot     BIGINT NOT NULL,
    not_enviada		BIGINT NOT NULL,
    not_enviada_tot	BIGINT NOT NULL,
    not_pendent		BIGINT NOT NULL,
    not_pendent_tot	BIGINT NOT NULL,
    not_registrada	BIGINT NOT NULL,
    not_registrada_tot	BIGINT NOT NULL,
    not_finalitzada		BIGINT NOT NULL,
    not_finalitzada_tot	BIGINT NOT NULL,
    not_processada		BIGINT NOT NULL,
    not_processada_tot	BIGINT NOT NULL,
    not_env_err		BIGINT NOT NULL,
    not_env_err_tot	BIGINT NOT NULL,
    not_fin_err		BIGINT NOT NULL,
    not_fin_err_tot	BIGINT NOT NULL,
    fir_iniciat			BIGINT NOT NULL,
    fir_iniciat_tot		BIGINT NOT NULL,
    fir_pausat			BIGINT NOT NULL,
    fir_pausat_tot		BIGINT NOT NULL,
    fir_firmat			BIGINT NOT NULL,
    fir_firmat_tot		BIGINT NOT NULL,
    fir_rebutjat		BIGINT NOT NULL,
    fir_rebutjat_tot	BIGINT NOT NULL,
    fir_parcial			BIGINT NOT NULL,
    fir_parcial_tot		BIGINT NOT NULL,
    dimensio_id         BIGINT NOT NULL,
    temps_id            BIGINT NOT NULL,
    CONSTRAINT pk_ipa_explot_fet PRIMARY KEY (id)
);

CREATE TABLE ipa_explot_dim (
    id                BIGINT NOT NULL,
    entitat_id        BIGINT NOT NULL,
    entitat_codi      VARCHAR(64) NOT NULL,
    procediment_id    BIGINT,
    procediment_codi  VARCHAR(64),
    organ_id          BIGINT,
    organ_codi        VARCHAR(64),
    usuari_codi       VARCHAR(64),
    CONSTRAINT pk_ipa_explot_dim PRIMARY KEY (id)
);

CREATE TABLE ipa_explot_temps (
    id           BIGINT NOT NULL,
    data         DATE NOT NULL,
    anualitat    INTEGER NOT NULL,
    mes          INTEGER NOT NULL,
    trimestre    INTEGER NOT NULL,
    setmana      INTEGER NOT NULL,
    dia          INTEGER NOT NULL,
    dia_setmana  VARCHAR(2),
    CONSTRAINT pk_ipa_explot_temps PRIMARY KEY (id)
);

ALTER TABLE ipa_explot_fet
    ADD CONSTRAINT fk_ipa_explot_fet_dim FOREIGN KEY (dimensio_id) REFERENCES ipa_explot_dim (id);

ALTER TABLE ipa_explot_fet
    ADD CONSTRAINT fk_ipa_explot_fet_temps FOREIGN KEY (temps_id) REFERENCES ipa_explot_temps (id);

ALTER TABLE ipa_explot_dim
    ADD CONSTRAINT ipa_explot_dim_uk UNIQUE (entitat_id, procediment_id, organ_codi, usuari_codi);