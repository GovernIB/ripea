-- Configuració d'avisos de l'usuari: rebre correu en finalitzar una acció massiva
ALTER TABLE IPA_USUARI ADD COLUMN EMAILS_ACCIO_MASSIVA BOOLEAN DEFAULT TRUE NOT NULL;
-- Configuració d'avisos de l'usuari: rebre correu en ser mencionat en un comentari
ALTER TABLE IPA_USUARI ADD COLUMN EMAILS_MENCIO_COMENTARI BOOLEAN DEFAULT TRUE NOT NULL;
