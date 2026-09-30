-- RIPEA 1.0.8
-- Indicadors d'explotacio de firmes (ipa_explot_fet):
--  * Nou indicador FIR_CANCELADES / FIR_CANCELADES_TOTAL: enviaments a portafirmes
--    cancel.lats a RIPEA (ipa_document_enviament de tipus portafirmes amb ESTAT = CANCELAT).
--    La cancel.lacio no modifica PF_CALLBACK_ESTAT, i per aixo fins ara aquests enviaments
--    seguien comptant com a enviats, iniciats o parcials. A partir d'ara nomes es compten
--    a aquest indicador i queden exclosos de la resta d'indicadors d'estat de firma.
--  * S'eliminen les dades diaries dels estats en curs (enviat, iniciat, pausat, parcial):
--    son estocs dels quals s'hi entra i se'n surt, i sense la data del canvi d'estat no
--    es pot saber quants n'hi havia en un dia concret. Es mantenen els seus totals.
--    Nomes els estats finals (firmat, rebutjat, cancel.lat) conserven la dada diaria.
-- Els dies ja calculats es queden amb el valor per defecte 0 a les columnes noves; a
-- partir del primer recalcul el valor ja es real.

ALTER TABLE IPA_EXPLOT_FET ADD COLUMN FIR_CANCELAT BIGINT DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD COLUMN FIR_CANCELAT_TOT BIGINT DEFAULT 0 NOT NULL;

ALTER TABLE IPA_EXPLOT_FET DROP COLUMN FIR_ENVIAT;
ALTER TABLE IPA_EXPLOT_FET DROP COLUMN FIR_INICIAT;
ALTER TABLE IPA_EXPLOT_FET DROP COLUMN FIR_PAUSAT;
ALTER TABLE IPA_EXPLOT_FET DROP COLUMN FIR_PARCIAL;
