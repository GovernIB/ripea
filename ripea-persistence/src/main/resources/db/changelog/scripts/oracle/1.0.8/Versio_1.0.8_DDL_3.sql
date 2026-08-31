-- RIPEA 1.0.8
-- Nou indicador d'explotacio (ipa_explot_fet): enviaments a portafirmes pendents
-- de resposta. Son les files de ipa_document_enviament de tipus portafirmes amb
-- PF_CALLBACK_ESTAT a null, es a dir, el document ja s'ha enviat a portafirmes
-- pero encara no n'hem rebut el callback amb l'estat.
-- Abans aquestes files no es comptaven a cap indicador.
-- Els dies ja calculats es queden amb el valor per defecte 0 (no es pot reconstruir
-- l'estat historic del callback); a partir del primer recalcul el valor ja es real.
ALTER TABLE IPA_EXPLOT_FET ADD FIR_ENVIAT NUMBER(38,0) DEFAULT 0 NOT NULL;
ALTER TABLE IPA_EXPLOT_FET ADD FIR_ENVIAT_TOT NUMBER(38,0) DEFAULT 0 NOT NULL;
