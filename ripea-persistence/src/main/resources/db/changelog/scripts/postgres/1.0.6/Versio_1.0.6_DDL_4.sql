-- Equivalent PostgreSQL del trasllat de LOB a RIPEA_LOB. A PostgreSQL el contingut TOAST
-- de les columnes TEXT viu sempre al mateix tablespace que la taula, aixi que es mou la
-- taula sencera, igual que es fa amb IPA_METADOCUMENT i IPA_DOCUMENT
-- (veure scripts/bbdd/1.0/postgres/ripea_07_lob.sql).
--
-- PRE-REQUISIT: el tablespace RIPEA_LOB ha d'existir i tenir espai lliure.
--
-- Es buiden primer les taules: son dades de monitoritzacio amb una retencio de 3 mesos,
-- i amb les taules buides el trasllat es immediat.
TRUNCATE TABLE IPA_INTEGRACIO_ACCIO;
TRUNCATE TABLE IPA_EXCEPCIO_LOG;

ALTER TABLE IPA_INTEGRACIO_ACCIO SET TABLESPACE RIPEA_LOB;
ALTER TABLE IPA_EXCEPCIO_LOG SET TABLESPACE RIPEA_LOB;
