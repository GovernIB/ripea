DECLARE 
    V_ENTITAT_ID NUMBER(19) :=1; 
    V_CREATEDBY_CODI VARCHAR2(64) := 'SYSTEM_RIPEA';
    V_ENTITAT_EXISTS NUMBER;
    V_USER_EXISTS NUMBER;
BEGIN
    -- ============================================================
    -- PASO 1: Verificar y crear la ENTIDAD si no existe
    -- ============================================================
    BEGIN
        SELECT COUNT(*)
        INTO V_ENTITAT_EXISTS
        FROM IPA_ENTITAT
        WHERE ID = V_ENTITAT_ID;
        
        -- Si no existe, crear la entidad
        IF V_ENTITAT_EXISTS = 0 THEN
            -- Crear la entidad con los campos mínimos obligatorios
            INSERT INTO IPA_ENTITAT (
                ID,
                CODI,
                NOM,
                CIF,
                UNITAT_ARREL,
                ACTIVA,
                VERSION,
                PERM_ENV_POSTAL
            ) VALUES (
                V_ENTITAT_ID,
                'GOIB',
                'Govern de les Illes Balears',  -- Ajusta el nombre si es necesario
                'S0711001H',                      -- Ajusta el CIF real
                'A04003003',                      -- Ajusta la unidad raíz real
                1,                                -- ACTIVA
                0,                                -- VERSION
                1                                 -- PERM_ENV_POSTAL (valor por defecto)
            );
            
            DBMS_OUTPUT.PUT_LINE('Entidad GOIB creada con ID: ' || V_ENTITAT_ID);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Entidad con ID ' || V_ENTITAT_ID || ' ya existe');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error al verificar/crear entidad: ' || SQLERRM);
            RAISE;
    END;
    
    -- ============================================================
    -- PASO 2: Verificar y crear el USUARIO si no existe
    -- ============================================================
    
    BEGIN
        SELECT COUNT(*)
        INTO V_USER_EXISTS
        FROM IPA_USUARI
        WHERE CODI = V_CREATEDBY_CODI;
        
        -- Si no existe, crear el usuario
        IF V_USER_EXISTS = 0 THEN
            -- Crear el usuario con los campos mínimos obligatorios
            INSERT INTO IPA_USUARI (
                CODI,
                INICIALITZAT,
                NOM,
                IDIOMA,
                VERSION,
                EMAILS_AGRUPATS,
                NUM_ELEMENTS_PAGINA,
                EXPEDIENT_EXPANDIT,
                EXP_LIST_DATA_DARRER_ENV,
                EXP_LIST_AGAFAT_PER,
                EXP_LIST_INTERESSATS,
                EXP_LIST_COMENTARIS,
                EXP_LIST_GRUP,
                VISTA_MOURE_ACTUAL,
                EMAILS_CANVI_ESTAT_REVISIO,
                MODE_FOSC,
                ENTITAT_DEFECTE_ID
            ) VALUES (
                V_CREATEDBY_CODI,
                1,                              -- INICIALITZAT
                'Usuari de Sistema RIPEA',      -- NOM
                'ca',                           -- IDIOMA
                0,                              -- VERSION
                0,                              -- EMAILS_AGRUPATS
                10,                             -- NUM_ELEMENTS_PAGINA (valor por defecto)
                1,                              -- EXPEDIENT_EXPANDIT (valor por defecto)
                0,                              -- EXP_LIST_DATA_DARRER_ENV (valor por defecto)
                1,                              -- EXP_LIST_AGAFAT_PER (valor por defecto)
                1,                              -- EXP_LIST_INTERESSATS (valor por defecto)
                1,                              -- EXP_LIST_COMENTARIS (valor por defecto)
                0,                              -- EXP_LIST_GRUP (valor por defecto)
                'LLISTA',                       -- VISTA_MOURE_ACTUAL (valor por defecto)
                1,                              -- EMAILS_CANVI_ESTAT_REVISIO (valor por defecto)
                0,                              -- MODE_FOSC (valor por defecto)
                V_ENTITAT_ID                    -- ENTITAT_DEFECTE_ID
            );
            
            DBMS_OUTPUT.PUT_LINE('Usuario SYSTEM_RIPEA creado correctamente');
        ELSE
            DBMS_OUTPUT.PUT_LINE('Usuario SYSTEM_RIPEA ya existe');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error al verificar/crear usuario: ' || SQLERRM);
            RAISE;
    END;

    COMMIT;
END;
/