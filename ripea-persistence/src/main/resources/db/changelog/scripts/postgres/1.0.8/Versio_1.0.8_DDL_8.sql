-- RIPEA 1.0.8
-- Indicador d'actiu dels tipus documentals: un tipus documental desactivat ja no es pot
-- assignar a nous tipus de document, pero es conserva per mostrar el nom dels tipus de
-- document i documents que ja el tenen assignat (la relacio es fa pel codi, sense FK).
ALTER TABLE IPA_TIPUS_DOCUMENTAL ADD COLUMN ACTIU BOOLEAN DEFAULT TRUE NOT NULL;
