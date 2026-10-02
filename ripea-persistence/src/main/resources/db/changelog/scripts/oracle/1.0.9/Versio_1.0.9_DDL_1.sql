-- RIPEA 1.0.9
-- Baixa logica d'usuaris (manteniment d'usuaris del superusuari a la interficie REACT).
-- Un usuari inactiu no pot iniciar sessio ni rep correus de l'aplicacio. Es desa qui l'ha
-- donat de baixa, quan i per quin motiu.
ALTER TABLE IPA_USUARI ADD ACTIU NUMBER(1,0) DEFAULT 1 NOT NULL;
ALTER TABLE IPA_USUARI ADD BAIXA_DATA TIMESTAMP(6);
ALTER TABLE IPA_USUARI ADD BAIXA_USUARI VARCHAR2(64 CHAR);
ALTER TABLE IPA_USUARI ADD BAIXA_MOTIU VARCHAR2(1024 CHAR);
