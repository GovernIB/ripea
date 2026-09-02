-- RIPEA 1.0.8
-- Els indicadors d'explotacio EXP_OBERTS i EXP_OBERTS_TOTAL passen a dir-se
-- EXP_CREATS i EXP_CREAT_TOTAL, perque el que compten realment son els expedients
-- creats (donats d'alta) i no els expedients que resten oberts:
--   EXP_CREATS      expedients creats dins del dia (de les 00:00:00 a les 23:59:59)
--   EXP_CREAT_TOTAL expedients creats fins a la data, tant oberts com tancats
-- Nomes canvia el nom: el contingut de les columnes ja era aquest, i per tant les
-- dades historiques ja calculades continuen sent valides.
ALTER TABLE IPA_EXPLOT_FET RENAME COLUMN EXP_OBERT TO EXP_CREAT;
ALTER TABLE IPA_EXPLOT_FET RENAME COLUMN EXP_OBERT_TOT TO EXP_CREAT_TOT;
