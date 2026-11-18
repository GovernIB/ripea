-- RIPEA 1.0.8
-- El proces en segon pla que incorpora els justificants de registre torna a comencar per les anotacions
-- mes recents: ara tracta totes les anotacions d'expedients oberts (no nomes les que no tenen justificant)
-- per recol.locar a la carpeta "Registre entrada: X" els justificants que ja hi ha a l'expedient.
-- Nomes es buida el darrer id: el cron d'inici (es.caib.ripea.segonpla.justificants.registre.inici)
-- ha de tenir valor perque el proces s'executi.

UPDATE IPA_CONFIG SET "VALUE" = NULL WHERE "KEY" = 'es.caib.ripea.segonpla.justificants.registre.darrerId';
