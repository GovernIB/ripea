-- RIPEA 1.0.8
-- Nous indicadors d'explotacio (ipa_explot_fet) sobre les tasques:
--  - TAS_CREADES / TAS_CREADES_TOTAL: tasques creades, sense mirar l'estat (nomes s'exclouen
--    les tasques d'expedients esborrats, igual que la resta d'indicadors de tasques).
--  - TAS_NOTFIN_FORA_TERMINI / TAS_NOTFIN_FORA_TERMINI_TOTAL: tasques amb data limit informada
--    i ja superada que encara no estan finalitzades. Es complementari de TAS_FIN_FORA_TERMINI:
--    la suma dels dos indicadors son totes les tasques fora de termini, sigui quin sigui l'estat.
-- Els dies ja calculats es queden amb el valor per defecte 0; a partir del primer recalcul
-- el valor ja es real.
ALTER TABLE IPA_EXPLOT_FET ADD COLUMN TAS_CREADA BIGINT DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD COLUMN TAS_CREADA_TOT BIGINT DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD COLUMN TAS_NOTFIN_FORA_TERMINI BIGINT DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD COLUMN TAS_NOTFIN_FORA_TERMINI_TOT BIGINT DEFAULT 0 NOT NULL;
