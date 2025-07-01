import {useTranslation} from "react-i18next";
import {useMoure} from "../actions/Moure.tsx";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import useNotificarMassive from "../actions/NotificarMassive.tsx";
import useCanviTipus from "../actions/CanviTipus.tsx";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";

const useMassiveActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();
    const {artifactReport: apiReport} = useResourceApiService('documentResource');

    const massiveReport = (ids:any, code:string, msg:string, fileType:any) => {
        return apiReport(undefined, {code :code, data:{ ids, massivo: true }, fileType})
            .then((result) => {
                refresh?.();
                iniciaDescargaBlob(result);
                temporalMessageShow(null, msg, 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const download = (ids: any[]): void => { massiveReport(ids, 'DESCARREGAR_MASSIU', t('page.expedient.results.actionOk'), 'ZIP'); }

    return {download}
}

const useContingutMassiveActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {download} = useMassiveActions(refresh)
    const {handleMassiveShow: handleMoure, content: contentMoure} = useMoure(refresh)
    // const {handleMassiveShow: handleCopiar, content: contentCopiar} = useCopiar(refresh)
    // const {handleMassiveShow: handleVincular, content: contentVincular} = useVincular(refresh)
    const {handleMassiveShow: handleNotificar, content: contentNotificar} = useNotificarMassive(entity, refresh)
    const {handleMassiveShow: handleCanviTipus, content: contentCanviTipus} = useCanviTipus(entity, refresh)

    const actions = [
        {
            title: t('common.download'),
            icon: "download",
            onClick: download,
        },
        {
            title: t('page.document.action.notificarMasiva.title'),
            icon: "mail",
            onClick: handleNotificar,
            hidden: !entity?.potModificar,
        },
        {
            title: t('page.contingut.action.move.title'),
            icon: "open_with",
            onClick: (ids:any[])=>handleMoure(ids, entity),
            hidden: !entity?.potModificar,
        },
        {
            title: t('page.document.action.changeType.title'),
            icon: "edit",
            onClick: handleCanviTipus,
            hidden: !entity?.potModificar,
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