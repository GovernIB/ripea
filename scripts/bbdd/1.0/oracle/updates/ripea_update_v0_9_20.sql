
------
--- #81 Distribució de tràmits de SISTRA mitjançant anotacions de registre
------
-- Modificació del manteniment de regles
ALTER TABLE IPA_REGLA ADD TIPUS_BACKOFFICE VARCHAR2(255 CHAR);
ALTER TABLE IPA_REGLA ADD TEMPS_ENTRE_INTENTS NUMBER(10);

-- Modificació de la taula de registres per tenir informació sobre l'estat de procés de SISTRA
-- Estat del procés de sistra
ALTER TABLE IPA_REGISTRE ADD (PROCES_ESTAT_SISTRA VARCHAR2(16 CHAR));
-- Identificador del tramit sistra
ALTER TABLE IPA_REGISTRE ADD (SISTRA_ID_TRAM VARCHAR2(20 CHAR));
-- Identificador del procediment sistra
ALTER TABLE IPA_REGISTRE ADD (SISTRA_ID_PROC VARCHAR2(100 CHAR));
