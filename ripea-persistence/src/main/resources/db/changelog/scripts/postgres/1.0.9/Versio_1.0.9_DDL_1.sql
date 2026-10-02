-- RIPEA 1.0.9
-- Baixa logica d'usuaris (manteniment d'usuaris del superusuari a la interficie REACT).
-- Un usuari inactiu no pot iniciar sessio ni rep correus de l'aplicacio. Es desa qui l'ha
-- donat de baixa, quan i per quin motiu.
ALTER TABLE IPA_USUARI ADD COLUMN ACTIU BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE IPA_USUARI ADD COLUMN BAIXA_DATA TIMESTAMP;
ALTER TABLE IPA_USUARI ADD COLUMN BAIXA_USUARI VARCHAR(64);
ALTER TABLE IPA_USUARI ADD COLUMN BAIXA_MOTIU VARCHAR(1024);
