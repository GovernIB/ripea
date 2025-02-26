const componentsCa = {
    app: {
        menu: {
            home: 'Inici',
        },
        auth: {
            logout: 'Tancar sessió',
        },
        error: {
            '404': 'No trobat',
            '503': 'Sense connexió',
        },
        offline: {
            message: 'Sense connexió amb el servidor',
        }
    },
    grid: {
        refresh: {
            title: 'Refrescar',
        },
        create: {
            title: 'Crear',
        },
        update: {
            title: 'Modificar',
        },
        delete: {
            title: 'Esborrar',
            single: {
                title: 'Esborrar element',
                confirm: 'Estau segur que voleu esborrar aquest element (aquesta acció no es pot desfer)?',
                success: 'Element esborrat',
                error: 'Error esborrant element',
            },
        },
        error: {
            toolbar: 'S\'ha produit un error',
        },
        noRows: 'Sense dades',
    },
    form: {
        goBack: {
            title: 'Tornar enrere',
        },
        revert: {
            title: 'Desfer canvis',
            confirm: 'Estau segur que voleu desfer els canvis fets al formulari?',
        },
        create: {
            title: 'Crear',
            success: 'Element creat',
            error: 'Error creant element'
        },
        update: {
            title: 'Modificar',
            success: 'Element modificat',
            error: 'Error modificant element',
            wrong_resource_type: 'No es possible desar els formularis amb tipus de recurs "{{resourceType}}"',
        },
        delete: {
            title: 'Esborrar',
            confirm: 'Estau segur que voleu esborrar aquest element (aquesta acció no es pot desfer)?',
            success: 'Element esborrat',
            error: 'Error esborrant element',
        },
        field: {
            reference: {
                open: 'Obrir',
                close: 'Tancar',
                clear: 'Esborrar',
                loading: 'Carregant...',
                noOptions: 'Sense opcions',
                page: 'Mostrant {{size}} de {{totalElements}} elements',
                advanced: {
                    title: 'Seleccionar valor'
                }
            },
            checkboxSelect: {
                true: 'Si',
                false: 'No',
            },
        },
    },
    buttons: {
        answerRequired: {
            accept: 'Acceptar',
            cancel: 'Cancel·lar',
        },
        confirm: {
            accept: 'Acceptar',
            cancel: 'Cancel·lar',
        },
        form: {
            save: 'Desar',
            cancel: 'Cancel·lar',
        },
        action: {
            exec: 'Executar',
            cancel: 'Cancel·lar',
        },
        report: {
            generate: 'Generar',
            cancel: 'Cancel·lar',
        },
        close: {
            close: 'Tancar'
        },
        misc: {
            retry: 'Tornar a provar',
        },
    }
};

export default componentsCa;