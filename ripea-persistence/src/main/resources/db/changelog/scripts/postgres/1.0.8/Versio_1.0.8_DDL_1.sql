-- RIPEA 1.0.8
-- Indicador de que no s'ha pogut moure el justificant de registre a dins l'expedient
-- de l'arxiu digital. El document queda incorporat a l'expedient de RIPEA amb l'uuid
-- original i no es reintenta el moviment, per aixo es marca per poder avisar a la interficie.
ALTER TABLE IPA_DOCUMENT ADD COLUMN JUST_MOURE_ERROR BOOLEAN DEFAULT FALSE NOT NULL;
