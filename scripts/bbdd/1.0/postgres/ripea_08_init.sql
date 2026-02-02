DO $$
DECLARE 
    V_METANODE_ID BIGINT; 
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

    -- ============================================================
    -- PASO 3: Crear METADOCUMENTOS GENERICOS
    -- ============================================================

    -- Metadocument OTROS / Metanode id
    SELECT NEXTVAL('ipa_hibernate_seq')
    INTO V_METANODE_ID;
    
    -- Create metanode and return id 
    INSERT INTO IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
    VALUES (V_METANODE_ID,'OTROS','Otros',NULL,'DOCUMENT',V_ENTITAT_ID,'1','16',V_CREATEDBY_CODI,CURRENT_TIMESTAMP,V_CREATEDBY_CODI,CURRENT_TIMESTAMP);
    
    -- Create metadocument 
    INSERT INTO IPA_METADOCUMENT (ID,CODI,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN,PINBAL_ACTIU) 
    VALUES (V_METANODE_ID,'OTROS','2','0',NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL,NULL,NULL,'O1','EE99','TD99','0','0','OTROS',0);

    -- Metadocument ACUSE_RECIBO / Metanode id
    SELECT NEXTVAL('ipa_hibernate_seq')
    INTO V_METANODE_ID;
    
    -- Create metanode and return id
    INSERT INTO IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
    VALUES (V_METANODE_ID,'ACUSE_RECIBO_NOTIFICACION','Acuse recibo notificación',NULL,'DOCUMENT',V_ENTITAT_ID,'1','16',V_CREATEDBY_CODI,CURRENT_TIMESTAMP,V_CREATEDBY_CODI,CURRENT_TIMESTAMP);
    
    -- Create metadocument 
    INSERT INTO IPA_METADOCUMENT (ID,CODI,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN,PINBAL_ACTIU) 
    VALUES (V_METANODE_ID,'ACUSE_RECIBO_NOTIFICACION','2','0',NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL,NULL,NULL,'O1','EE01','TD09','0','0','ACUSE_RECIBO_NOTIFICACION',0);

    -- Metadocument NOTIFICACIÓN / Metanode id
    SELECT NEXTVAL('ipa_hibernate_seq')
    INTO V_METANODE_ID;
    
    -- Create metanode and return id
    INSERT INTO IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
    VALUES (V_METANODE_ID,'NOTIFICACION','Notificación',NULL,'DOCUMENT',V_ENTITAT_ID,'1','16',V_CREATEDBY_CODI,CURRENT_TIMESTAMP,V_CREATEDBY_CODI,CURRENT_TIMESTAMP);
    
    INSERT INTO IPA_METADOCUMENT (ID,CODI,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN,PINBAL_ACTIU) 
    VALUES (V_METANODE_ID,'NOTIFICACION','2','0',NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL,NULL,NULL,'O1','EE02','TD07','0','0','NOTIFICACION',0);

    -- Metadocument JUSTIFICANTE_REGISTRO / Metanode id
    SELECT NEXTVAL('ipa_hibernate_seq')
    INTO V_METANODE_ID;
    
    -- Create metanode and return id
    INSERT INTO IPA_METANODE (ID,CODI,NOM,DESCRIPCIO,TIPUS,ENTITAT_ID,ACTIU,VERSION,CREATEDBY_CODI,CREATEDDATE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
    VALUES (V_METANODE_ID,'JUSTIFICANTE_REGISTRO','Justificante registro',NULL,'DOCUMENT',V_ENTITAT_ID,'1','16',V_CREATEDBY_CODI,CURRENT_TIMESTAMP,V_CREATEDBY_CODI,CURRENT_TIMESTAMP);

    INSERT INTO IPA_METADOCUMENT (ID,CODI,MULTIPLICITAT,FIRMA_PFIRMA,PORTAFIRMES_DOCTIP,PORTAFIRMES_FLUXID,PORTAFIRMES_RESPONS,PORTAFIRMES_FLUXTIP,PORTAFIRMES_CUSTIP,FIRMA_PASSARELA,PASSARELA_CUSTIP,PLANTILLA_NOM,PLANTILLA_CONTENT_TYPE,META_EXPEDIENT_ID,NTI_ORIGEN,NTI_ESTELA,NTI_TIPDOC,FIRMA_BIOMETRICA,BIOMETRICA_LECTURA,META_DOCUMENT_TIPUS_GEN,PINBAL_ACTIU) 
    VALUES (V_METANODE_ID,'JUSTIFICANTE_REGISTRO','2','0',NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL,NULL,NULL,'O1','EE01','TD99','0','0','JUSTIFICANTE_REGISTRO',0);
    
    -- PostgreSQL hace commit automático al final de DO blocks exitosos
    RAISE NOTICE 'Script ejecutado correctamente';
    
END $$;