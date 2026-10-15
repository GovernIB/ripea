-- RIPEA 1.0.9
-- Estat inicial de les execucions massives existents: les que no tenen data de fi segueixen a la cua.
UPDATE IPA_EXECUCIO_MASSIVA SET ESTAT = CASE WHEN DATA_FI IS NULL THEN 'PENDENT' ELSE 'FINALITZADA' END WHERE ESTAT IS NULL;
