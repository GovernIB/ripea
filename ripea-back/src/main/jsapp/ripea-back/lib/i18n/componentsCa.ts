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
    datacommon: {
        details: {
            title: 'Detalls',
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
                title: 'Confirmació',
                confirm: 'Estau segur que voleu esborrar aquest element (aquesta acció no es pot desfer)?',
                success: 'Element esborrat',
                error: 'Error esborrant element',
            },
        },
        export: {
            title: 'Exportar',
        },
        refresh: {
            title: 'Refrescar',
        },
        toolbar: {
            error: 'S\'ha produit un error',
        },
        findDisabled: 'Sense consultar',
        noRows: 'Sense dades',
        error: 'Error',
    },
    grid: {
        selection: {
            one: '1 fila seleccionada',
            multiple: '{{count}} files seleccionades',
        },
        pageInfo: '{{from}} a {{to}} de {{count}}',
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
        dialog: {
            create: 'Crear',
            update: 'Modificar',
        },
        validate: {
            error: 'Error de validació',
        },
        submission: {
            defaulterror: 'Error'
        },
    },
    actionreport: {
        action: {
            confirm: {
                title: 'Confirmació',
                message: 'Estau segur que voleu executar l\'acció {{action}}?'
            },
            error: 'Error executant l\'acció'
        },
        report: {
            error: 'Error generant l\'informe'
        }
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
        misc: {
            close: 'Tancar',
            retry: 'Tornar a provar',
        },
    }
};

export default componentsCa;