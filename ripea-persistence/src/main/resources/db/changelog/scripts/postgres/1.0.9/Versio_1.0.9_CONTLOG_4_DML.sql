-- RIPEA 1.0.9
-- Esborra les traces d'actualitzacio des de ROLSAC que deixava el proces en segon pla (usuari SYSTEM_RIPEA).
-- Nomes les actualitzacions demanades per un usuari deixen traca a l'historic del procediment. A mes, una
-- descripcio de ROLSAC de mes de 1000 caracters es detectava com a canvi a cada execucio i en deixava una cada hora.
-- S'executa despres de Versio_1.0.9_CONTLOG_3_DDL: TIPUS ja es desa pel nom.
DELETE FROM IPA_CONT_LOG
 WHERE CONTINGUT_TIPUS = 'METANODE'
   AND TIPUS = 'UPDATE_PROCED_ROLSAC'
   AND CREATEDBY_CODI = 'SYSTEM_RIPEA';
