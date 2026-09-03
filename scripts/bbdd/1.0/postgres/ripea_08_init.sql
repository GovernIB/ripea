DO $$
DECLARE 
    V_ENTITAT_ID BIGINT := 1; 
    V_CREATEDBY_CODI VARCHAR(64) := 'SYSTEM_RIPEA';
    V_ENTITAT_EXISTS INTEGER;
    V_USER_EXISTS INTEGER;
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
                'Govern de les Illes Balears',
                'S0711001H',
                'A04003003',
                1,
                0,
                1
            );
            
            RAISE NOTICE 'Entidad GOIB creada con ID: %', V_ENTITAT_ID;
        ELSE
            RAISE NOTICE 'Entidad con ID % ya existe', V_ENTITAT_ID;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE 'Error al verificar/crear entidad: %', SQLERRM;
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
                1,
                'Usuari de Sistema RIPEA',
                'ca',
                0,
                0,
                10,
                1,
                0,
                1,
                1,
                1,
                0,
                'LLISTA',
                1,
                0,
                V_ENTITAT_ID
            );
            
            RAISE NOTICE 'Usuario SYSTEM_RIPEA creado correctamente';
        ELSE
            RAISE NOTICE 'Usuario SYSTEM_RIPEA ya existe';
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE 'Error al verificar/crear usuario: %', SQLERRM;
            RAISE;
    END;

    -- PostgreSQL hace commit automático al final de DO blocks exitosos
    RAISE NOTICE 'Script ejecutado correctamente';
    
END $$;