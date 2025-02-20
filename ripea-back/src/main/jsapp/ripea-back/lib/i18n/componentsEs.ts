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
            title: 'Borrar',
            single: {
                title: 'Borrar elemento',
                confirm: '¿Está seguro de que quiere borrar este elemento (esta acción no se puede deshacer)?',
                success: 'Elemento borrado',
                error: 'Error borrando elementos',
            },
        },
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