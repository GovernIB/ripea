-- Configuració d'avisos de l'usuari: rebre correu en finalitzar una acció massiva
ALTER TABLE IPA_USUARI ADD EMAILS_ACCIO_MASSIVA NUMBER(1,0) DEFAULT 1 NOT NULL;
-- Configuració d'avisos de l'usuari: rebre correu en ser mencionat en un comentari
ALTER TABLE IPA_USUARI ADD EMAILS_MENCIO_COMENTARI NUMBER(1,0) DEFAULT 1 NOT NULL;
