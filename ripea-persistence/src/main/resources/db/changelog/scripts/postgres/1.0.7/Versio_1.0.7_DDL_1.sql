-- Color principal del tema personalitzat per l'usuari (hex #RRGGBB).
-- Valor per defecte: blau corporatiu equivalent al primary del tema clar actual.
ALTER TABLE IPA_USUARI ADD COLUMN COLOR_PRINCIPAL VARCHAR(7) DEFAULT '#337ab7';
ALTER TABLE IPA_USUARI ADD COLUMN COLOR_SECUNDARI VARCHAR(7) DEFAULT '#f1efef';