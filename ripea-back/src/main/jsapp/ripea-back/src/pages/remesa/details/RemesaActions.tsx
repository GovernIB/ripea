import {
    useBaseAppContext,
    useResourceApiService
} from "reactlib";
import {useTranslation} from "react-i18next";
import useNotificacioInteressatGrid from "./NotificacioInteressatGrid.tsx";
import useRemesaDetail from "./RemesaDetail.tsx";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
        artifactReport: apiReport,
    } = useResourceApiService('documentNotificacioResource');
    const {temporalMessageShow} = useBaseAppContext();

    const report = (id:any, code:any, mssg:any, fileType:any) => {
        apiReport(id, {code, fileType})
            .then((result) => {
                iniciaDescargaBlob(result);
                temporalMessageShow(null, mssg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }
    const reportMassivo = (id:any, code:any, mssg:any, fileType:any) => {
        apiReport(undefined, {code, data: { ids: [id], massivo: false }, fileType})
            .then((result) => {
                iniciaDescargaBlob(result);
                temporalMessageShow(null, mssg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    const justificant = (id: any) => reportMassivo(id, 'DESCARREGAR_JUSTIFICANT', t('page.notificacio.action.justificant.ok'), 'ZIP');
    const descarregarDocumentEnviat = (id: any) => report(id, 'DESCARREGAR_DOC_ENVIAT', t('page.notificacio.action.documentEnviat.ok'), 'ZIP');

    const actualitzarEstat = (id: any) => {
        apiAction(undefined, {code: 'ACTUALITZAR_ESTAT', data:{ids: [id], massivo: false}})
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.notificacio.action.actualitzarEstat.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }
    const actualitzarEstatMassive = (ids: any[]) => {
        apiAction(undefined, {code: 'ACTUALITZAR_ESTAT', data:{ids, massivo: true}})
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.notificacio.action.actualitzarEstat.massiveOk'), 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {
        actualitzarEstat,
        actualitzarEstatMassive,
        justificant,
        descarregarDocumentEnviat,
    }
}

const useRemesaActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {actualitzarEstat, justificant} = useActions(refresh)

    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = useRemesaDetail();
    const {handleOpen: handleNotificacioOpen, dialog: dialogNotificacio} = useNotificacioInteressatGrid(refresh);

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetallOpen,
        },
        {
            label: t('common.update')+'...',
            icon: 'edit',
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: (row:any) => row.tipus != 'MANUAL' || !entity?.potModificar,
        },
        {
            label: t('page.notificacio.action.actualitzarEstat.label'),
            icon: "sync",
            showInMenu: true,
            onClick: actualitzarEstat,
            hidden: (row:any) => row.estat == 'PROCESSADA'|| !entity?.potModificar,
        },
        {
            label: t('page.notificacio.action.notificacioInteressat.label'),
            icon: "send",
            showInMenu: true,
            onClick: handleNotificacioOpen,
            hidden: !entity?.potModificar,
        },
        {
            label: t('page.notificacio.action.justificant.label'),
            icon: "download",
            showInMenu: true,
            onClick: justificant,
            hidden: (row:any) => row.estat == 'PENDENT',
        },
		{
		    label: t('common.delete'),
		    icon: 'delete',
		    showInMenu: true,
            clickTriggerDelete: true,
		    hidden: (row:any) => row.tipus != 'MANUAL' || !entity?.potModificar,
		},		
    ];

    const components = <>
        {dialogDetall}
        {dialogNotificacio}
    </>;

    return {
        actions,
        components
    }
}
export default useRemesaActions;