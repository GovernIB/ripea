-- #1653 Evitar duplicats de codi de usuari majuscules/minuscules
UPDATE ipa_usuari SET codi = LOWER(codi);