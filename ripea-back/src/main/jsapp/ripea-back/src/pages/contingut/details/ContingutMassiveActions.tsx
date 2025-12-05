import {useTranslation} from "react-i18next";
import {useMoure} from "../actions/Moure.tsx";
import {useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import useNotificarMassive from "../actions/NotificarMassive.tsx";
import useCanviTipus from "../actions/CanviTipus.tsx";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";

export const useMassiveActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {artifactAction: apiAction, artifactReport: apiReport} = useResourceApiService('documentResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const massiveAction = (ids:any, code:string, msg:string) => {
        return apiAction(undefined, {code :code, data:{ ids, massivo: true }})
            .then((result) => {
                refresh?.();
                iniciaDescargaBlob(result);
                temporalMessageShow(null, msg, 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }
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
    const definitiu = (ids: any[]) => {
        messageDialogShow(
            '',
            t('page.document.action.definitive.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    massiveAction(ids, 'CONVERTIR_DEFINITIU', t('page.expedient.results.actionBackgroundOk'))
                }
            });
    }

    return {download, definitiu}
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
            label: t('common.download'),
            icon: "download",
            showInMenu: true,
            onClick: download,
        },
        {
            label: t('page.document.action.notificarMasiva.title'),
            icon: "mail",
            showInMenu: true,
            onClick: handleNotificar,
            hidden: !entity?.potModificar,
        },
        {
            label: t('page.contingut.action.move.title'),
            icon: "open_with",
            showInMenu: true,
            onClick: (ids:any[])=>handleMoure(ids, entity),
            hidden: !entity?.potModificar,
        },
        {
            label: t('page.document.action.changeType.title'),
            icon: "edit",
            showInMenu: true,
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