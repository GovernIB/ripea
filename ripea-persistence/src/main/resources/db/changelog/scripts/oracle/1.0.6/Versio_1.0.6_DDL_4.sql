-- Les taules de monitoritzacio creades a la versio 1.0.5 (IPA_INTEGRACIO_ACCIO i
-- IPA_EXCEPCIO_LOG) es van crear sense clausula d'emmagatzematge, de manera que els seus
-- segments LOB van anar a parar al tablespace per defecte (RIPEA), competint per l'espai
-- amb les dades de l'aplicacio i provocant ORA-01691 en inserir traces d'error.
-- Es traslladen a RIPEA_LOB, igual que els LOB de IPA_METADOCUMENT i IPA_DOCUMENT
-- (veure scripts/bbdd/1.0/oracle/ripea_07_lob.sql).
--
-- PRE-REQUISIT: el tablespace RIPEA_LOB ha d'existir i tenir espai lliure.
--
-- Es buiden primer les taules: son dades de monitoritzacio amb una retencio de 3 mesos,
-- i amb els segments buits el MOVE es immediat en comptes d'haver de reescriure GB de CLOB.
-- El TRUNCATE allibera a mes l'espai ocupat al tablespace RIPEA.
TRUNCATE TABLE IPA_INTEGRACIO_ACCIO;
TRUNCATE TABLE IPA_EXCEPCIO_LOG;

ALTER TABLE IPA_INTEGRACIO_ACCIO MOVE LOB (EXCEPCIO_STACKTRACE) STORE AS IPA_INTACC_STACKTRACE_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_INTEGRACIO_ACCIO MOVE LOB (PARAMETRES) STORE AS IPA_INTACC_PARAMETRES_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_EXCEPCIO_LOG MOVE LOB (STACKTRACE) STORE AS IPA_EXCLOG_STACKTRACE_LOB (TABLESPACE RIPEA_LOB);
