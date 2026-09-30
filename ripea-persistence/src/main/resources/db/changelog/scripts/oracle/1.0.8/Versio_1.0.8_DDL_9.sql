-- RIPEA 1.0.8
-- Error produit en incorporar el justificant de registre de l'anotacio com a document
-- de l'expedient. Mentre estigui informat, l'anotacio acceptada ofereix l'accio
-- "Afegir justificant a l'expedient"; es buida quan el justificant s'incorpora correctament.
ALTER TABLE IPA_REGISTRE ADD JUSTIFICANT_ERROR VARCHAR2(4000 CHAR);
