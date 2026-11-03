/**
 * Sobrescrituras de los textos de la librería base-react (espacio de nombres `reactlib`).
 * Ver `reactlibCa.ts` para el criterio: toda etiqueta de acción va en imperativo de segunda
 * persona del singular ("Crea", "Guarda", "Cancela"), no en infinitivo.
 */
const reactlibEs = {
    app: {
        auth: {
            logout: 'Cierra la sesión',
        },
    },
    datacommon: {
        back: {
            label: 'Vuelve atrás',
        },
        create: {
            label: 'Crea',
        },
        update: {
            label: 'Modifica',
        },
        delete: {
            label: 'Borra',
        },
        export: {
            label: 'Exporta',
        },
        refresh: {
            label: 'Refresca',
        },
    },
    grid: {
        edit: {
            save: 'Guarda',
            cancel: 'Cancela',
        },
    },
    form: {
        goBack: {
            title: 'Vuelve atrás',
        },
        revert: {
            title: 'Deshaz los cambios',
        },
        create: {
            title: 'Crea',
        },
        update: {
            title: 'Modifica',
        },
        delete: {
            title: 'Borra',
        },
        field: {
            enum: {
                clear: 'Borra',
            },
            reference: {
                open: 'Abre',
                close: 'Cierra',
                clear: 'Borra',
            },
        },
    },
    copyToClipboard: {
        copy: 'Copia',
    },
    buttons: {
        answerRequired: {
            accept: 'Acepta',
            cancel: 'Cancela',
        },
        confirm: {
            accept: 'Acepta',
            cancel: 'Cancela',
        },
        form: {
            save: 'Guarda',
            cancel: 'Cancela',
        },
        action: {
            exec: 'Ejecuta',
            cancel: 'Cancela',
        },
        report: {
            generate: 'Genera',
            cancel: 'Cancela',
        },
        misc: {
            close: 'Cierra',
            retry: 'Reinténtalo',
        },
    },
};

export default reactlibEs;
