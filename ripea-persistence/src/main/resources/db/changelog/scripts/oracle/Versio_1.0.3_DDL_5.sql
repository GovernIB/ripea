--1660
CREATE TABLE ipa_explot_fet
(
    id				NUMBER(38, 0) NOT NULL,
    exp_obert		NUMBER(38, 0) NOT NULL,
    exp_obert_tot	NUMBER(38, 0) NOT NULL,
    exp_tancat		NUMBER(38, 0) NOT NULL,
    exp_tancat_tot	NUMBER(38, 0) NOT NULL,
    tas_pendent		NUMBER(38, 0) NOT NULL,
    tas_pendent_tot	NUMBER(38, 0) NOT NULL,
    tas_iniciada	NUMBER(38, 0) NOT NULL,
    tas_iniciada_tot NUMBER(38, 0) NOT NULL,    
    tas_finalitzada	NUMBER(38, 0) NOT NULL,
    tas_finalitzada_tot	NUMBER(38, 0) NOT NULL,
    tas_cancelada	NUMBER(38, 0) NOT NULL,
    tas_cancelada_tot	NUMBER(38, 0) NOT NULL,
    tas_rebutjada	NUMBER(38, 0) NOT NULL,
    tas_rebutjada_tot	NUMBER(38, 0) NOT NULL,
    tas_agafada	NUMBER(38, 0) NOT NULL,
    tas_agafada_tot	NUMBER(38, 0) NOT NULL,
    ano_noves		NUMBER(38, 0) NOT NULL,
    ano_noves_tot	NUMBER(38, 0) NOT NULL,
    ano_processada	NUMBER(38, 0) NOT NULL,
    ano_processada_tot	NUMBER(38, 0) NOT NULL,
    ano_rebutjada	NUMBER(38, 0) NOT NULL,
    ano_rebutjada_tot	NUMBER(38, 0) NOT NULL,
    pin_enviats		NUMBER(38, 0) NOT NULL,
    pin_enviats_tot	NUMBER(38, 0) NOT NULL,
    not_enviada		NUMBER(38, 0) NOT NULL,
    not_enviada_tot	NUMBER(38, 0) NOT NULL,
    not_pendent		NUMBER(38, 0) NOT NULL,
    not_pendent_tot	NUMBER(38, 0) NOT NULL,
    not_registrada	NUMBER(38, 0) NOT NULL,
    not_registrada_tot	NUMBER(38, 0) NOT NULL,
    not_finalitzada		NUMBER(38, 0) NOT NULL,
    not_finalitzada_tot	NUMBER(38, 0) NOT NULL,
    not_processada		NUMBER(38, 0) NOT NULL,
    not_processada_tot	NUMBER(38, 0) NOT NULL,
    not_env_err		NUMBER(38, 0) NOT NULL,
    not_env_err_tot	NUMBER(38, 0) NOT NULL,
    not_fin_err		NUMBER(38, 0) NOT NULL,
    not_fin_err_tot	NUMBER(38, 0) NOT NULL,
    fir_iniciat		NUMBER(38, 0) NOT NULL,
    fir_iniciat_tot	NUMBER(38, 0) NOT NULL,
    fir_pausat		NUMBER(38, 0) NOT NULL,
    fir_pausat_tot	NUMBER(38, 0) NOT NULL,
    fir_firmat		NUMBER(38, 0) NOT NULL,
    fir_firmat_tot	NUMBER(38, 0) NOT NULL,
    fir_rebutjat		NUMBER(38, 0) NOT NULL,
    fir_rebutjat_tot	NUMBER(38, 0) NOT NULL,
    fir_parcial		NUMBER(38, 0) NOT NULL,
    fir_parcial_tot	NUMBER(38, 0) NOT NULL,
    dimensio_id		NUMBER(38, 0) NOT NULL,
    temps_id		NUMBER(38, 0) NOT NULL,
    CONSTRAINT PK_IPA_EXPLOT_FET PRIMARY KEY (id)
);

CREATE TABLE ipa_explot_dim
(
    id             NUMBER(38, 0)      NOT NULL,
    entitat_id     NUMBER(38, 0)      NOT NULL,
    entitat_codi   VARCHAR2(64 CHAR)  NOT NULL,
    procediment_id NUMBER(38, 0),
    procediment_codi VARCHAR2(64 CHAR),
    organ_id	   NUMBER(38, 0),
    organ_codi	   VARCHAR2(64 CHAR),
    usuari_codi    VARCHAR2(64 CHAR),
    CONSTRAINT PK_IPA_EXPLOT_DIM PRIMARY KEY (id)
);

CREATE TABLE ipa_explot_temps
(
    id          NUMBER(38, 0) NOT NULL,
    data        date          NOT NULL,
    anualitat   INTEGER       NOT NULL,
    mes         INTEGER       NOT NULL,
    trimestre   INTEGER       NOT NULL,
    setmana     INTEGER       NOT NULL,
    dia         INTEGER       NOT NULL,
    dia_setmana VARCHAR2(2 CHAR),
    CONSTRAINT PK_IPA_EXPLOT_TEMPS PRIMARY KEY (id)
);

ALTER TABLE ipa_explot_fet ADD CONSTRAINT fk_ipa_explot_fet_dim FOREIGN KEY (dimensio_id) REFERENCES ipa_explot_dim (id);
ALTER TABLE ipa_explot_fet ADD CONSTRAINT fk_ipa_explot_fet_temps FOREIGN KEY (temps_id) REFERENCES ipa_explot_temps (id);
ALTER TABLE ipa_explot_dim ADD CONSTRAINT ipa_explot_dim_uk UNIQUE (entitat_id, procediment_id, organ_codi, usuari_codi);