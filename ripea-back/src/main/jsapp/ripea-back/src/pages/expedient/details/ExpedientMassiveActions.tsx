import {useBaseAppContext, useResourceApiService} from "reactlib";
import useExportarDocuments from "../actions/ExportarDocuments.tsx";

const useMassiveActions = (refresh?: () => void)=> {
    const {temporalMessageShow} = useBaseAppContext();
    const {artifactAction: apiAction} = useResourceApiService('expedientResource')

    const massiveAction = (ids:any[], code:string) => {
        return apiAction(undefined, {code :code, data:{ ids: ids, massivo: true }})
    }

    const agafar = (ids: any[]): void => {
        massiveAction(ids, 'AGAFAR')
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error?.message, 'error');
            });
    }
    const follow = (ids: any[]): void => {
        massiveAction(ids, 'FOLLOW')
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error?.message, 'error');
            });
    }
    const unfollow = (ids: any[]): void => {
        massiveAction(ids, 'UNFOLLOW')
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error?.message, 'error');
            });
    }

    return {
        agafar,
        follow,
        unfollow,
    }
}

const useExpedientMassiveActions = (refresh?: () => void)=> {

    const {agafar, follow, unfollow} = useMassiveActions(refresh);

    const {handleMassiveShow: handleExportDoc, content: contentExportDoc} = useExportarDocuments(refresh);

    const actions = [
        {
            title: "Agafar",
            icon: "lock",
            onClick: agafar,
        },
        {
            title: "Alliberar",
            icon: "lock_open",
        },
        {
            title: "Seguir",
            icon: "person_add_alt1",
            onClick: follow,
        },
        {
            title: "Deixar de seguir",
            icon: "person_remove",
            onClick: unfollow,
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
            title: "Exportar els documents dels expedients seleccionats...",
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