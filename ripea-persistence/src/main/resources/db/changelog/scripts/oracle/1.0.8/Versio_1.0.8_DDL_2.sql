-- RIPEA 1.0.8
-- Nous indicadors d'explotacio (ipa_explot_fet):
--  * Procediments i serveis actius.
--  * Tasques finalitzades desglossades en dins/fora de termini.
--  * Enviaments PINBAL desglossats en processats OK / amb error.
-- El valor historic de tas_finalitzada i pin_enviats no es equivalent als nous
-- indicadors, per aixo s'esborren les dades acumulades i les columnes antigues.
-- Tambe s'esborra ipa_explot_temps: existeixenEstadistiques() comprova aquesta
-- taula per decidir si cal recalcular un dia, i si no es buida el recalcul es
-- salta i les dades no es tornarien a generar mai.

-- Reset de les dades d'explotacio (es regeneraran amb generarEstadistiquesDiaries)
TRUNCATE TABLE IPA_EXPLOT_FET;
DELETE FROM IPA_EXPLOT_TEMPS;

-- Procediments i serveis actius
ALTER TABLE IPA_EXPLOT_FET ADD PRC_ACTIUS_TOT NUMBER(38,0) DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD SRV_ACTIUS_TOT NUMBER(38,0) DEFAULT 0 NOT NULL;

-- Tasques finalitzades dins / fora de termini
ALTER TABLE IPA_EXPLOT_FET DROP COLUMN TAS_FINALITZADA;
ALTER TABLE IPA_EXPLOT_FET DROP COLUMN TAS_FINALITZADA_TOT;
ALTER TABLE IPA_EXPLOT_FET ADD TAS_FINALITZADA_OK NUMBER(38,0) DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD TAS_FINALITZADA_KO NUMBER(38,0) DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD TAS_FINALITZADA_TOTAL_OK NUMBER(38,0) DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD TAS_FINALITZADA_TOTAL_KO NUMBER(38,0) DEFAULT 0 NOT NULL;

-- Enviaments PINBAL processats OK / amb error
ALTER TABLE IPA_EXPLOT_FET DROP COLUMN PIN_ENVIATS;
ALTER TABLE IPA_EXPLOT_FET DROP COLUMN PIN_ENVIATS_TOT;
ALTER TABLE IPA_EXPLOT_FET ADD PIN_ENVIATS_OK NUMBER(38,0) DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD PIN_ENVIATS_KO NUMBER(38,0) DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD PIN_ENVIATS_TOT_OK NUMBER(38,0) DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD PIN_ENVIATS_TOT_KO NUMBER(38,0) DEFAULT 0 NOT NULL;
