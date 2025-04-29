import useExportarDocuments from "../actions/ExportarDocuments.tsx";

const useMassiveActions = (refresh?: () => void)=> {
    // const {artifactAction} = useResourceApiService('expedientResource')
    return {}
}

const useExpedientMassiveActions = (refresh?: () => void)=> {

    const {} = useMassiveActions(refresh);

    const {handleMassiveShow: handleExportDoc, content: contentExportDoc} = useExportarDocuments(refresh);

    const actions = [
        {
            title: "Agafar",
            icon: "lock",
            showInMenu: true,
        },
        {
            title: "Alliberar",
            icon: "lock_open",
            showInMenu: true,
        },
        {
            title: "Seguir",
            icon: "person_add_alt1",
            showInMenu: true,
        },
        {
            title: "Deixar de seguir",
            icon: "person_remove",
            showInMenu: true,
        },
        {
            title: "Esborrar",
            icon: "delete",
            showInMenu: true,
        },
        {
            title: "Exportar full de càlcul",
            icon: "download",
            showInMenu: true,
        },
        {
            title: "Exportar CSV",
            icon: "download",
            showInMenu: true,
        },
        {
            title: "Exportar índex ZIP",
            icon: "download",
            showInMenu: true,
        },
        {
            title: "Exportar índex PDF",
            icon: "download",
            showInMenu: true,
        },
        {
            title: "Exportació ENI",
            icon: "download",
            showInMenu: true,
        },
        {
            title: "Exportar els documents dels expedients seleccionats",
            icon: "description",
            showInMenu: true,
            onClick: handleExportDoc,
        },
    ]

    const components = <>
        {contentExportDoc}
    </>

    return {
        actions,
        components
    }
}
export default useExpedientMassiveActions;