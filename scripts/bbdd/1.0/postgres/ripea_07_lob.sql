-- ============================================================================
-- RIPEA - Trasllat al tablespace RIPEA_LOB (PostgreSQL)
--
-- Equivalent de scripts/bbdd/1.0/oracle/ripea_07_lob.sql. A PostgreSQL no es pot
-- reubicar nomes el contingut TOAST d'una columna: viu sempre al mateix tablespace
-- que la taula. Per aixo es mou la taula sencera, i la unitat no es la columna sino
-- la taula que conte columnes BYTEA o TEXT de gran volum.
--
-- Cobreix les mateixes 7 taules que la versio Oracle (alla, 15 columnes LOB).
--
-- PRE-REQUISIT: el tablespace RIPEA_LOB ha d'existir i tenir espai lliure.
--
-- QUI EXECUTA QUE. Cada sentencia va marcada amb una d'aquestes etiquetes:
--
--   [PROPIETARI]  El propietari de les taules IPA_*. Per moure una taula li cal, a
--                 mes, el privilegi CREATE sobre el tablespace de desti:
--                   GRANT CREATE ON TABLESPACE ripea_lob TO ripea;
--                 Sense aquest privilegi el SET TABLESPACE falla amb
--                 "permission denied for tablespace ripea_lob".
--
--   [SUPERUSUARI] Nomes cal per crear i esborrar tablespaces (CREATE TABLESPACE i
--                 DROP TABLESPACE). Les consultes de mida del pas 3 les pot fer el
--                 propietari si te CREATE sobre el tablespace, o qualsevol rol
--                 membre de pg_read_all_stats; si no, donen "permission denied".
--
-- Els passos 1 i 2 son el trasllat. El pas 3 nomes serveix per mesurar.
--
-- NOTES D'EXECUCIO:
--   - ALTER TABLE ... SET TABLESPACE agafa un ACCESS EXCLUSIVE LOCK sobre la taula
--     i en reescriu tot el contingut: executar en finestra de manteniment. Mentre
--     dura, calen les dues copies alhora, de manera que el pic d'espai al
--     tablespace de desti es la mida de la taula mes gran que s'estigui movent.
--   - No cal reconstruir cap index, a diferencia d'Oracle: aqui els indexs no
--     depenen de la ubicacio fisica de les files i queden valids.
--   - Els indexs NO es mouen amb la taula: es queden al seu tablespace actual.
--     Veure el pas 2 si tambe se'ls vol traslladar.
--   - Com que el SET TABLESPACE reescriu la taula des de zero, de passada n'elimina
--     el bloat, igual que faria un VACUUM FULL.
-- ============================================================================


-- ----------------------------------------------------------------------------
-- 1. TRASLLAT DE LES TAULES   [PROPIETARI]
-- ----------------------------------------------------------------------------

-- 1.1 Contingut documental (les taules de mes volum)
ALTER TABLE IPA_DOCUMENT SET TABLESPACE RIPEA_LOB;
ALTER TABLE IPA_METADOCUMENT SET TABLESPACE RIPEA_LOB;

-- 1.2 Anotacions de registre: cos de l'anotacio (EXPOSA, SOLICITA) i annexos amb la seva firma
ALTER TABLE IPA_REGISTRE SET TABLESPACE RIPEA_LOB;
ALTER TABLE IPA_REGISTRE_ANNEX SET TABLESPACE RIPEA_LOB;

-- 1.3 Logotips de l'entitat (tema clar i tema fosc)
ALTER TABLE IPA_ENTITAT SET TABLESPACE RIPEA_LOB;

-- 1.4 Monitoritzacio (taules creades a la 1.0.5)
ALTER TABLE IPA_INTEGRACIO_ACCIO SET TABLESPACE RIPEA_LOB;
ALTER TABLE IPA_EXCEPCIO_LOG SET TABLESPACE RIPEA_LOB;


-- ----------------------------------------------------------------------------
-- 2. INDEXS (OPCIONAL)   [PROPIETARI]
--
-- Els indexs de les taules mogudes segueixen al tablespace on estiguessin, que
-- normalment es pg_default. No cal moure'ls: no contenen contingut TOAST i el seu
-- volum es petit comparat amb el dels documents. Nomes te sentit fer-ho si es vol
-- buidar del tot el tablespace origen.
--
-- Aquesta consulta genera els ALTER INDEX corresponents; revisar la sortida abans
-- d'executar-la.
-- ----------------------------------------------------------------------------
SELECT 'ALTER INDEX ' || i.indexrelid::regclass::text
       || ' SET TABLESPACE RIPEA_LOB;' AS sentencia
FROM   pg_index i
       JOIN pg_class c ON c.oid = i.indrelid
WHERE  c.relname IN ('ipa_document', 'ipa_metadocument', 'ipa_registre',
                     'ipa_registre_annex', 'ipa_entitat',
                     'ipa_integracio_accio', 'ipa_excepcio_log');


-- ============================================================================
-- 3. COMPROVACIO I ESPAI ALLIBERAT
--
-- AQUI NO HI HA EQUIVALENT DEL COALESCE NI DEL RESIZE D'ORACLE, i no cal.
-- A PostgreSQL un tablespace es un directori del sistema de fitxers i cada taula
-- es un fitxer dins seu. Quan el SET TABLESPACE fa commit, PostgreSQL esborra els
-- fitxers antics i l'espai queda lliure al sistema de fitxers immediatament, sense
-- cap pas addicional. No hi ha datafile preassignat que calgui encongir ni extents
-- lliures que calgui fusionar.
--
-- El que si te sentit es mesurar abans i despres, per confirmar que l'espai s'ha
-- mogut d'un directori a l'altre. Executar el pas 3.1 ABANS del pas 1 i tornar-lo
-- a executar DESPRES: la mida del tablespace origen ha de baixar aproximadament el
-- mateix que puja la de RIPEA_LOB.
-- ============================================================================

-- 3.1 Mida de cada tablespace. Executar abans i despres del pas 1.
--     SUBSTITUIR 'pg_default' si el tablespace origen es un altre.
SELECT spcname                                   AS tablespace,
       pg_size_pretty(pg_tablespace_size(spcname)) AS mida,
       pg_tablespace_location(oid)               AS ubicacio
FROM   pg_tablespace
WHERE  spcname IN ('pg_default', 'ripea_lob')
ORDER  BY spcname;

-- 3.2 On ha quedat cada taula i quant ocupa.
--     RELTABLESPACE = 0 vol dir que la taula esta al tablespace per defecte de la
--     base de dades, no que no en tingui.
SELECT c.relname                                     AS taula,
       COALESCE(t.spcname, 'pg_default')             AS tablespace,
       pg_size_pretty(pg_total_relation_size(c.oid)) AS mida_total
FROM   pg_class c
       LEFT JOIN pg_tablespace t ON t.oid = c.reltablespace
WHERE  c.relname IN ('ipa_document', 'ipa_metadocument', 'ipa_registre',
                     'ipa_registre_annex', 'ipa_entitat',
                     'ipa_integracio_accio', 'ipa_excepcio_log')
ORDER  BY c.relname;

-- 3.3 Si despres del trasllat el tablespace origen ha quedat completament buit i
--     no es vol conservar, es pot esborrar. Nomes funciona si no li queda cap
--     objecte, de cap base de dades del cluster.   [SUPERUSUARI]
--     ATENCIO: aixo no aplica mai a pg_default, que no es pot esborrar.
--
--       DROP TABLESPACE nom_tablespace_origen;
--
--     Per comprovar si queda alguna cosa (executar a cada base de dades del cluster):
--       SELECT c.relname, c.relkind
--       FROM   pg_class c
--              JOIN pg_tablespace t ON t.oid = c.reltablespace
--       WHERE  t.spcname = 'nom_tablespace_origen';
