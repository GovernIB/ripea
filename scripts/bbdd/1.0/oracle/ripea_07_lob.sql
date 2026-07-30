-- ============================================================================
-- RIPEA - Trasllat dels segments LOB al tablespace RIPEA_LOB
--
-- Cobreix TOTES les columnes BLOB/CLOB de l'esquema. Sense aquest trasllat els
-- segments LOB es creen al tablespace per defecte de l'usuari i competeixen per
-- l'espai amb les dades de l'aplicacio (ORA-01691 en inserir).
--
-- PRE-REQUISIT: scripts/bbdd/1.0/oracle/ripea_00_tablespace_lob.sql
--               (el tablespace RIPEA_LOB ha d'existir i tenir espai lliure)
--
-- NOTES D'EXECUCIO:
--   - Cada MOVE LOB bloqueja la taula en exclusiva mentre dura i reescriu tot el
--     segment: executar en finestra de manteniment. Els temps els marquen
--     IPA_DOCUMENT.FITXER_CONTINGUT i IPA_REGISTRE_ANNEX.CONTINGUT, que son els
--     segments grans; la resta son immediats.
--   - IMPORTANT: el MOVE LOB deixa UNUSABLE tots els indexs de la taula (PK, UK
--     i FK inclosos). Si no es reconstrueixen, qualsevol INSERT/UPDATE posterior
--     falla amb ORA-01502. Per aixo el pas final d'aquest script NO es opcional.
--     Comprovat sobre Oracle 21c: despres dels MOVE queden UNUSABLE els indexs
--     de les 7 taules afectades.
-- ============================================================================


-- ----------------------------------------------------------------------------
-- 1. TRASLLAT DELS SEGMENTS LOB
-- 1.1 Contingut documental (els segments de mes volum)   [PROPIETARI]
-- ----------------------------------------------------------------------------
ALTER TABLE IPA_DOCUMENT MOVE LOB (FITXER_CONTINGUT) STORE AS IPA_DOCUME_FITXER_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_METADOCUMENT MOVE LOB (PLANTILLA_CONTINGUT) STORE AS IPA_METADOC_PLANTILLA_LOB (TABLESPACE RIPEA_LOB);


-- ----------------------------------------------------------------------------
-- 1.2 Anotacions de registre: cos de l'anotacio i annexos amb la seva firma   [PROPIETARI]
-- ----------------------------------------------------------------------------
ALTER TABLE IPA_REGISTRE MOVE LOB (EXPOSA) STORE AS IPA_REGIST_EXPOSA_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_REGISTRE MOVE LOB (SOLICITA) STORE AS IPA_REGIST_SOLICITA_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_REGISTRE_ANNEX MOVE LOB (CONTINGUT) STORE AS IPA_REGANX_CONTINGUT_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_REGISTRE_ANNEX MOVE LOB (FIRMA_CONTINGUT) STORE AS IPA_REGANX_FIRMA_CONT_LOB (TABLESPACE RIPEA_LOB);


-- ----------------------------------------------------------------------------
-- 1.3 Logotips de l'entitat (tema clar i tema fosc)   [PROPIETARI]
-- LOGO_IMG existeix des de la 1.0; la resta es van afegir a la 1.0.3
-- (scripts/oracle/Versio_1.0.3_DDL_7.sql)
-- ----------------------------------------------------------------------------
ALTER TABLE IPA_ENTITAT MOVE LOB (LOGO_IMG) STORE AS IPA_ENTITA_LOGO_IMG_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_ENTITAT MOVE LOB (LOGO_RIPEA) STORE AS IPA_ENTITA_LOGO_RIPEA_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_ENTITAT MOVE LOB (LOGO_MENU) STORE AS IPA_ENTITA_LOGO_MENU_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_ENTITAT MOVE LOB (BLACK_LOGO_IMG) STORE AS IPA_ENTITA_BLK_LOGO_IMG_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_ENTITAT MOVE LOB (BLACK_LOGO_RIPEA) STORE AS IPA_ENTITA_BLK_LOGO_RIPEA_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_ENTITAT MOVE LOB (BLACK_LOGO_MENU) STORE AS IPA_ENTITA_BLK_LOGO_MENU_LOB (TABLESPACE RIPEA_LOB);


-- ----------------------------------------------------------------------------
-- 1.4 Monitoritzacio (taules creades a la 1.0.5)   [PROPIETARI]
-- Si s'executa sobre una base de dades ja en servei, veure tambe
-- scripts/oracle/1.0.6/Versio_1.0.6_DDL_4.sql, que fa aquest mateix trasllat
-- previ TRUNCATE de les taules.
-- ----------------------------------------------------------------------------
ALTER TABLE IPA_INTEGRACIO_ACCIO MOVE LOB (PARAMETRES) STORE AS IPA_INTACC_PARAMETRES_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_INTEGRACIO_ACCIO MOVE LOB (EXCEPCIO_STACKTRACE) STORE AS IPA_INTACC_STACKTRACE_LOB (TABLESPACE RIPEA_LOB);
ALTER TABLE IPA_EXCEPCIO_LOG MOVE LOB (STACKTRACE) STORE AS IPA_EXCLOG_STACKTRACE_LOB (TABLESPACE RIPEA_LOB);


-- ----------------------------------------------------------------------------
-- 2. RECONSTRUCCIO DELS INDEXS (OBLIGATORI)   [PROPIETARI]
-- Els MOVE anteriors han deixat UNUSABLE els indexs de les taules afectades.
-- Es reconstrueixen de forma dinamica en lloc de llistar-los un a un perque
-- aixi no cal mantenir aquest script quan s'afegeixin indexs nous.
-- ----------------------------------------------------------------------------
BEGIN
  FOR r IN (SELECT index_name
            FROM   user_indexes
            WHERE  status = 'UNUSABLE'
            AND    table_name IN ('IPA_DOCUMENT',
                                  'IPA_METADOCUMENT',
                                  'IPA_REGISTRE',
                                  'IPA_REGISTRE_ANNEX',
                                  'IPA_ENTITAT',
                                  'IPA_INTEGRACIO_ACCIO',
                                  'IPA_EXCEPCIO_LOG'))
  LOOP
    EXECUTE IMMEDIATE 'ALTER INDEX ' || r.index_name || ' REBUILD';
  END LOOP;
END;
/
