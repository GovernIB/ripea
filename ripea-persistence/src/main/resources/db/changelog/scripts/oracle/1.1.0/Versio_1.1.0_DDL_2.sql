-- RIPEA 1.1.0
-- Pantalla d'inici (issue #1510): preferencia de l'usuari sobre la pantalla on entra a la interficie REACT.
--   ROL   = pantalla per defecte del rol actiu (comportament anterior; valor per defecte)
--   INICI = pantalla d'inici amb els indicadors del rol actiu
ALTER TABLE IPA_USUARI ADD PAGINA_ENTRADA VARCHAR2(16 CHAR) DEFAULT 'ROL' NOT NULL;
