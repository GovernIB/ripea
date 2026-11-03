/**
 * Sobreescriptures dels textos de la llibreria base-react (espai de noms `reactlib`).
 *
 * La llibreria viu a `lib/` i no s'hi pot tocar (els canvis es perdrien a la propera
 * sincronització), però registra les seves traduccions a la mateixa instància d'i18next de
 * l'aplicació. Aquest paquet s'hi fusiona a sobre (veure `components/BaseApp.tsx`) per aplicar
 * el criteri de Drassana (el mateix que DISTRIBUCIO, CONCSV i NOTIB): **tota etiqueta d'acció --
 * botons, entrades de menú i tooltips -- va en imperatiu de segona persona del singular** ("Crea", "Desa", "Cancel·la"), no en
 * infinitiu ("Crear", "Desar", "Cancel·lar").
 *
 * Només s'hi toquen les claus que etiqueten accions. Es deixen com estan:
 * - els títols (`form.dialog.*`, `form.field.reference.advanced.title`...),
 * - els noms (`datacommon.details.label`, `datacommon.quickfilter.label`...),
 * - i els missatges (confirmacions, errors...).
 *
 * Excepció inevitable: `datacommon.create.label` i `datacommon.update.label` etiqueten alhora
 * els botons de crear/modificar i el títol del diàleg del formulari emergent, que la llibreria
 * composa com "<etiqueta> <nom del recurs>". En passar-les a imperatiu, aquest títol queda
 * "Crea entitat" / "Modifica entitat". Separar-los demanaria una clau nova a base-react.
 */
const reactlibCa = {
    app: {
        auth: {
            logout: 'Tanca la sessió',
        },
    },
    datacommon: {
        back: {
            label: 'Torna enrere',
        },
        create: {
            label: 'Crea',
        },
        update: {
            label: 'Modifica',
        },
        delete: {
            label: 'Esborra',
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
            save: 'Desa',
            cancel: 'Cancel·la',
        },
    },
    form: {
        goBack: {
            title: 'Torna enrere',
        },
        revert: {
            title: 'Desfés els canvis',
        },
        create: {
            title: 'Crea',
        },
        update: {
            title: 'Modifica',
        },
        delete: {
            title: 'Esborra',
        },
        field: {
            enum: {
                clear: 'Esborra',
            },
            reference: {
                open: 'Obre',
                close: 'Tanca',
                clear: 'Esborra',
            },
        },
    },
    copyToClipboard: {
        copy: 'Copia',
    },
    buttons: {
        answerRequired: {
            accept: 'Accepta',
            cancel: 'Cancel·la',
        },
        confirm: {
            accept: 'Accepta',
            cancel: 'Cancel·la',
        },
        form: {
            save: 'Desa',
            cancel: 'Cancel·la',
        },
        action: {
            exec: 'Executa',
            cancel: 'Cancel·la',
        },
        report: {
            generate: 'Genera',
            cancel: 'Cancel·la',
        },
        misc: {
            close: 'Tanca',
            retry: 'Torna-ho a provar',
        },
    },
};

export default reactlibCa;
