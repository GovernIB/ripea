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
    tas_finalitzada	NUMBER(38, 0) NOT NULL,
    tas_finalitzada_tot	NUMBER(38, 0) NOT NULL,
    ano_pendent		NUMBER(38, 0) NOT NULL,
    ano_pendent_tot	NUMBER(38, 0) NOT NULL,
    ano_processada	NUMBER(38, 0) NOT NULL,
    ano_processada_tot	NUMBER(38, 0) NOT NULL,
    pin_enviats		NUMBER(38, 0) NOT NULL,
    pin_enviats_tot	NUMBER(38, 0) NOT NULL,
    not_enviada		NUMBER(38, 0) NOT NULL,
    not_enviada_tot	NUMBER(38, 0) NOT NULL,
    com_enviada		NUMBER(38, 0) NOT NULL,
    com_enviada_tot	NUMBER(38, 0) NOT NULL,
    fir_enviada		NUMBER(38, 0) NOT NULL,
    fir_enviada_tot	NUMBER(38, 0) NOT NULL,
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