-- ============================================================================
-- RIPEA - Creacio del tablespace RIPEA_LOB
-- Optimitzat per a segments LOB (BLOB/CLOB): contingut de documents, annexos
-- de registre, plantilles de metadocument, logotips i traces de monitoritzacio.
--
-- ES PRE-REQUISIT DE:
--   - scripts/bbdd/1.0/oracle/ripea_07_lob.sql
--   - ripea-persistence/.../db/changelog/06-initial_schema_lob.yaml   (context caib)
--   - ripea-persistence/.../scripts/oracle/1.0.6/Versio_1.0.6_DDL_4.sql
--
-- L'HA D'EXECUTAR UN DBA. Requereix el privilegi de sistema CREATE TABLESPACE,
-- que l'usuari de l'aplicacio no te. Per aixo aquest script NO forma part del
-- changelog de Liquibase: si hi fos, el desplegament fallaria amb ORA-01031.
--
-- ABANS D'EXECUTAR, EDITAR:
--   (a) la ruta i mida del datafile al pas 2
--   (b) el nom del propietari de l'esquema al pas 3
-- ============================================================================


-- ----------------------------------------------------------------------------
-- 1. COMPROVACIO PREVIA
--    Si retorna una fila, el tablespace ja existeix: no executar el pas 2
--    (fallaria amb ORA-01543) i saltar directament al pas 3.
-- ----------------------------------------------------------------------------
SELECT tablespace_name,
       bigfile,
       block_size,
       extent_management,
       allocation_type,
       segment_space_management,
       logging,
       status
FROM   dba_tablespaces
WHERE  tablespace_name = 'RIPEA_LOB';


-- ----------------------------------------------------------------------------
-- 2. CREACIO DEL TABLESPACE
--
--    Justificacio de cada clausula per a un tablespace nomes de LOB:
--
--    BIGFILE           Un unic datafile que escala fins a l'ordre de TB. Un
--                      tablespace smallfile amb blocs de 8K esta limitat a 32 GB
--                      per datafile i obligaria a anar afegint fitxers a mida que
--                      creix el repositori documental. Amb BIGFILE l'espai es
--                      gestiona amb un sol RESIZE.
--
--    AUTOEXTEND        Creixement automatic. NEXT gran (1G) perque cada extensio
--    ON NEXT 1G        del datafile es una operacio serialitzada: amb increments
--                      petits, una carrega massiva de documents provoca centenars
--                      d'extensions i espera d'espai. MAXSIZE posa un sostre per
--                      evitar que el LOB ompli el sistema de fitxers sencer.
--
--    EXTENT MANAGEMENT Extents que creixen de 64K a 8M segons la mida del segment.
--    LOCAL AUTOALLOCATE Es el que recomana Oracle quan els segments passen de zero
--                      a diversos GB, com el contingut documental: evita tant la
--                      fragmentacio d'extents uniformes petits com el malbaratament
--                      d'extents uniformes grans en segments encara buits.
--
--    SEGMENT SPACE     CLAU. Gestio automatica de l'espai lliure amb bitmaps en
--    MANAGEMENT AUTO   comptes de freelists: elimina la contencio quan diversos
--                      usuaris pugen documents alhora. A mes es REQUISIT
--                      IMPRESCINDIBLE per als LOB SecureFiles (veure nota A).
--
--    LOGGING           Les operacions es registren al redo log. NO usar NOLOGGING
--                      en un repositori documental: el contingut carregat sense
--                      redo no es recuperable des d'un backup i queda il·legible
--                      despres d'un restore (ORA-26040). A mes, amb Data Guard o
--                      FORCE LOGGING a nivell de BD el NOLOGGING s'ignora.
-- ----------------------------------------------------------------------------
CREATE BIGFILE TABLESPACE RIPEA_LOB
  DATAFILE '/u01/app/oracle/oradata/RIPEA/ripea_lob01.dbf'
    SIZE 10G
    AUTOEXTEND ON NEXT 1G MAXSIZE 2T
  EXTENT MANAGEMENT LOCAL AUTOALLOCATE
  SEGMENT SPACE MANAGEMENT AUTO
  LOGGING
  ONLINE;

-- Variants segons l'emmagatzematge de l'entorn (usar NOMES una de les tres):
--
--   ASM:
--     CREATE BIGFILE TABLESPACE RIPEA_LOB
--       DATAFILE '+DATA' SIZE 10G AUTOEXTEND ON NEXT 1G MAXSIZE 2T
--       EXTENT MANAGEMENT LOCAL AUTOALLOCATE
--       SEGMENT SPACE MANAGEMENT AUTO LOGGING ONLINE;
--
--   Oracle Managed Files (parametre DB_CREATE_FILE_DEST definit):
--     CREATE BIGFILE TABLESPACE RIPEA_LOB
--       DATAFILE SIZE 10G AUTOEXTEND ON NEXT 1G MAXSIZE 2T
--       EXTENT MANAGEMENT LOCAL AUTOALLOCATE
--       SEGMENT SPACE MANAGEMENT AUTO LOGGING ONLINE;
--
--   Windows:
--     DATAFILE 'D:\ORACLE\ORADATA\RIPEA\ripea_lob01.dbf'


-- ----------------------------------------------------------------------------
-- 3. QUOTA PER AL PROPIETARI DE L'ESQUEMA
--    Oracle carrega l'espai al PROPIETARI del segment, no a qui executa la
--    sentencia ni a qui fa l'INSERT. Per tant la quota va al propietari de les
--    taules IPA_*, encara que:
--      - el MOVE LOB el llanci un DBA (els seus privilegis NO substitueixen la
--        quota del propietari: falla igual amb ORA-01950), o
--      - l'aplicacio es connecti amb un usuari diferent (WWW_RIPEA, veure
--        ripea_06_grant.sql), que NO necessita quota per inserir BLOB: l'espai
--        el segueix pagant el propietari.
--    Sense quota, els MOVE LOB de ripea_07_lob.sql i de Versio_1.0.6_DDL_4.sql
--    fallen amb ORA-01950 "no privileges on tablespace 'RIPEA_LOB'", encara que
--    el tablespace existeixi.
--
--    SUBSTITUIR 'RIPEA' pel propietari real de l'esquema. Per identificar-lo:
--      SELECT owner, COUNT(*) FROM dba_tables
--      WHERE table_name LIKE 'IPA\_%' ESCAPE '\' GROUP BY owner;
-- ----------------------------------------------------------------------------
ALTER USER RIPEA QUOTA UNLIMITED ON RIPEA_LOB;


-- ----------------------------------------------------------------------------
-- 4. VERIFICACIO
-- ----------------------------------------------------------------------------
-- 4.1 El tablespace s'ha creat amb les caracteristiques esperades.
--     S'espera: BIGFILE=YES, ALLOCATION_TYPE=SYSTEM, SEGMENT_SPACE_MANAGEMENT=AUTO
SELECT tablespace_name, bigfile, block_size, allocation_type,
       segment_space_management, logging, status
FROM   dba_tablespaces
WHERE  tablespace_name = 'RIPEA_LOB';

-- 4.2 Datafile, mida i autoextend.
SELECT file_name, ROUND(bytes/1024/1024/1024, 2) AS gb,
       autoextensible, ROUND(maxbytes/1024/1024/1024, 2) AS max_gb
FROM   dba_data_files
WHERE  tablespace_name = 'RIPEA_LOB';

-- 4.3 L'usuari de l'aplicacio te quota (MAX_BYTES = -1 vol dir il·limitada).
SELECT username, tablespace_name, max_bytes
FROM   dba_ts_quotas
WHERE  tablespace_name = 'RIPEA_LOB';

-- 4.4 Despres d'executar ripea_07_lob.sql: quins segments LOB hi han quedat.
--     Aquesta consulta la pot executar el mateix usuari de l'aplicacio.
SELECT table_name, column_name, segment_name, tablespace_name, securefile
FROM   user_lobs
ORDER  BY tablespace_name, table_name;


-- ============================================================================
-- NOTES
--
-- A) SECUREFILES
--    Els scripts ripea_07_lob.sql i Versio_1.0.6_DDL_4.sql fan
--    "STORE AS <nom> (TABLESPACE RIPEA_LOB)" sense indicar el tipus de LOB, de
--    manera que el tipus resultant depen del parametre d'instancia DB_SECUREFILE:
--      - PREFERRED (per defecte a 12c i posteriors) -> SecureFile, sempre que el
--        tablespace tingui ASSM, que es el cas d'aquest script.
--      - PERMITTED (per defecte a 11g)              -> BasicFile.
--    Comprovar amb:  SHOW PARAMETER db_securefile;
--    Els SecureFiles son preferibles per a contingut documental: millor
--    rendiment d'escriptura, menys redo i gestio d'espai mes eficient. Verificar
--    el resultat real amb la consulta 4.4 (columna SECUREFILE).
--
-- B) COMPRESSIO I DEDUPLICACIO
--    Sobre SecureFiles es poden activar per segment:
--      ... STORE AS SECUREFILE <nom> (TABLESPACE RIPEA_LOB COMPRESS MEDIUM DEDUPLICATE)
--    Molt efectiu amb PDF/ODT repetits, pero ATENCIO: ambdues opcions requereixen
--    llicencia Advanced Compression Option. No activar sense confirmar-ho.
--
-- C) MIDA DE BLOC
--    Historicament es recomanava un tablespace de LOB amb bloc de 32K. Amb
--    SecureFiles el guany es marginal, perque Oracle gestiona el chunk de manera
--    independent del bloc. A mes, un bloc no estandard obliga a configurar la
--    cache corresponent (DB_32K_CACHE_SIZE) a nivell d'instancia. Per aixo aquest
--    script usa el bloc estandard de la base de dades. Si tot i aixi es vol
--    provar, afegir "BLOCKSIZE 32768" a la clausula CREATE, previa configuracio
--    de DB_32K_CACHE_SIZE.
--
-- D) DIMENSIONAT INICIAL
--    SIZE 10G i MAXSIZE 2T son valors de partida. Per ajustar-los a un entorn ja
--    en produccio, mesurar l'espai que ocupen actualment els segments LOB:
--      SELECT ROUND(SUM(bytes)/1024/1024/1024, 2) AS gb
--      FROM   dba_segments
--      WHERE  segment_type IN ('LOBSEGMENT', 'LOBINDEX');
-- ============================================================================
