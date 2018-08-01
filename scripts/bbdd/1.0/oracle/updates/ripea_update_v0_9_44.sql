----------------------------------------------------------
-- #134 Firmar annexos d'anotació de registre sense firma
-- Afegeix una columna per saber si la firma està feta per Ripea en el servidor
----------------------------------------------------------

ALTER TABLE IPA_REGISTRE_ANNEX_FIRMA ADD AUTOFIRMA NUMBER(1);
