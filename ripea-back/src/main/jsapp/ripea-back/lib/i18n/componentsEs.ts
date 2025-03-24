const componentsEs = {
    app: {
        menu: {
            home: 'Inicio',
        },
        auth: {
            logout: 'Cerrar sesión',
        },
        error: {
            '404': 'No encontrado',
            '503': 'Sin connexión',
        },
        offline: {
            message: 'Sin conexión con el servidor',
        }
    },
    datacommon: {
        details: {
            title: 'Details',
        },
        create: {
            title: 'Create',
        },
        update: {
            title: 'Update',
        },
        delete: {
            title: 'Delete',
            single: {
                title: 'Delete',
                confirm: 'Are you sure you want to delete this item (this action can\'t be undone)?',
                success: 'Deleted items',
                error: 'Error deleting items',
            },
        },
        export: {
            title: 'Export',
        },
        refresh: {
            title: 'Refresh',
        },
        toolbar: {
            error: 'An error has occurred',
        },
        noRows: 'No data',
    },
    form: {
        goBack: {
            title: 'Ir atrás',
        },
        revert: {
            title: 'Deshacer cambios',
            confirm: '¿Está seguro de que quiere deshacer los cambios hechos al formulario?',
        },
        create: {
            title: 'Crear',
            success: 'Elemento creado',
            error: 'Error creando elemento',
        },
        update: {
            title: 'Modificar',
            success: 'Elemento modificado',
            error: 'Error modificando elemento',
            wrong_resource_type: 'No es posible guardar los formularios con tipo de recurso "{{resourceType}}"',
        },
        delete: {
            title: 'Borrar',
            confirm: '¿Está seguro de que quiere borrar este elemento (esta acción no se puede deshacer)?',
            success: 'Elemento borrado',
            error: 'Error borrando elemento',
        },
        field: {
            reference: {
                open: 'Abrir',
                close: 'Cerrar',
                clear: 'Borrar',
                loading: 'Cargando...',
                noOptions: 'Sin opciones',
                page: 'Mostrando {{size}} de {{totalElements}} elementos',
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
            error: 'Error de validación',
        },
    },
    grid: {
        selection: {
            one: '1 selected row ',
            multiple: '{{count}} selected rows',
        },
    },
    buttons: {
        answerRequired: {
            accept: 'Aceptar',
            cancel: 'Cancelar',
        },
        confirm: {
            accept: 'Aceptar',
            cancel: 'Cancelar',
        },
        form: {
            save: 'Guardar',
            cancel: 'Cancelar',
        },
        action: {
            exec: 'Ejecutar',
            cancel: 'Cancelar',
        },
        report: {
            generate: 'Generar',
            cancel: 'Cancelar',
        },
        close: {
            close: 'Cerrar'
        },
        misc: {
            retry: 'Reintentar',
        },
    },
};

export default componentsEs;