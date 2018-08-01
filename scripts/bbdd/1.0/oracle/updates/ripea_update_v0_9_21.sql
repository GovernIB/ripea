------
--- #94 Canviar el tipus de la propietat numero de RegistreAnotacio a String. Corregir detall
------
-- Crea la columna auxiliar
-- Modificació del manteniment de regles
ALTER TABLE IPA_REGISTRE ADD NUMERO_temp VARCHAR2(100 CHAR);
-- Copia els valors a la nova columna
UPDATE IPA_REGISTRE SET NUMERO_temp = TO_CHAR(NUMERO);
-- Esborra la restricció not null
ALTER TABLE IPA_REGISTRE MODIFY (NUMERO NULL);
-- Esborra la restricció unique de 3 columnes
ALTER TABLE IPA_REGISTRE DROP CONSTRAINT IPA_REG_MULT_UK
-- Esborra la columna antiga
ALTER TABLE IPA_REGISTRE DROP COLUMN NUMERO;
-- Reanomena la columna auxiliar com a la columna vàlida
ALTER TABLE IPA_REGISTRE RENAME COLUMN NUMERO_temp TO NUMERO;
-- Afegeix la restricció de not null
ALTER TABLE IPA_REGISTRE MODIFY NUMERO NOT NULL;
-- Restaura la restricció IPA_REG_MULT_UK
ALTER TABLE IPA_REGISTRE ADD CONSTRAINT constraint_name UNIQUE (ENTITAT_CODI, LLIBRE_CODI, TIPUS, NUMERO, DATA);

------
--- #95 Mostrar data i oficina d'origen al detall de l'anotació
------
-- Modificació de la taula de registres per tenir informació la data i oficina d'origen
-- Data origen
ALTER TABLE IPA_REGISTRE ADD (DATA_ORIG TIMESTAMP(6));
-- Codi de l'oficina d'origen
ALTER TABLE IPA_REGISTRE ADD (OFICINA_ORIG_CODI VARCHAR2(21 CHAR));
-- Descripció de l'oficina d'origen
ALTER TABLE IPA_REGISTRE ADD (OFICINA_ORIG_DESC VARCHAR2(100 CHAR));


------
--- #97 Reordenar informació detall anotació
------
-- Afegeix un camp per guardar el nom de la unitat administrativa corresponent al codi dir3
ALTER TABLE IPA_REGISTRE ADD (UNITAT_ADM_DESC VARCHAR2(100 CHAR));
