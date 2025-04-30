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
        },
        {
            title: "Alliberar",
            icon: "lock_open",
        },
        {
            title: "Seguir",
            icon: "person_add_alt1",
        },
        {
            title: "Deixar de seguir",
            icon: "person_remove",
        },
        {
            title: "Esborrar",
            icon: "delete",
        },
        {
            title: "Exportar full de càlcul",
            icon: "download",
        },
        {
            title: "Exportar CSV",
            icon: "download",
        },
        {
            title: "Exportar índex ZIP",
            icon: "download",
        },
        {
            title: "Exportar índex PDF",
            icon: "download",
        },
        {
            title: "Exportació ENI",
            icon: "download",
        },
        {
            title: "Exportar els documents dels expedients seleccionats",
            icon: "description",
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