import {useTranslation} from "react-i18next";
import useMoure from "../actions/Moure.tsx";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import useNotificarMassive from "../actions/NotificarMassive.tsx";
import useCanviTipus from "../actions/CanviTipus.tsx";

const useMassiveActions = (refresh?: () => void) => {
    const {temporalMessageShow} = useBaseAppContext();
    const {artifactReport: apiReport} = useResourceApiService('documentResource');

    const massiveReport = (ids:any[], code:string, msg:string, fileType:any) => {
        return apiReport(undefined, {code :code, data:{ ids: ids, masivo: true }, fileType})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, msg, 'info');
            })
            .catch((error) => {
                temporalMessageShow('Error', error?.message, 'error');
            });
    }

    const download 	= (ids: any[]): void => { massiveReport(ids, 'ACTION_DESCARREGAR_MASSIU', '', 'ZIP'); }

    return {download}
}

const useContingutMassiveActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {download} = useMassiveActions(refresh)
    const {handleMassiveShow: handleMoure, content: contentMoure} = useMoure(refresh)
    const {handleMassiveShow: handleNotificar, content: contentNotificar} = useNotificarMassive(refresh)
    const {handleMassiveShow: handleCanviTipus, content: contentCanviTipus} = useCanviTipus(entity, refresh)

    const actions = [
        {
            title: t('common.download'),
            icon: "download",
            onClick: download,
        },
        {
            title: t('page.document.acciones.notificar'),
            icon: "mail",
            onClick: handleNotificar,
        },
        {
            title: t('page.document.acciones.move'),
            icon: "open_with",
            onClick: (ids:any[])=>handleMoure(ids, entity),
        },
        {
            title: "Cambiar tipo",
            icon: "edit",
            onClick: handleCanviTipus,
        },
    ]

    const components = <>
        {contentMoure}
        {contentNotificar}
        {contentCanviTipus}
    </>

    return {
        actions,
        components
    }
}
export default useContingutMassiveActions;