import {
    useBaseAppContext,
    useResourceApiService
} from "reactlib";
import {useTranslation} from "react-i18next";
import useNotificacioInteressatGrid from "./NotificacioInteressatGrid.tsx";
import useRemesaDetail from "./RemesaDetail.tsx";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";

const useActions = (refresh?: () => void) => {
    const {temporalMessageShow} = useBaseAppContext();

    const {
        artifactAction: apiAction,
        artifactReport: apiReport,
    } = useResourceApiService('documentNotificacioResource');

    const action = (id:any, code:any, mssg:any) => {
        apiAction(id, {code})
            .then(() => {
                refresh?.();
                temporalMessageShow(null, mssg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }
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

    const actualitzarEstat = (id: any) => action(id, 'ACTUALITZAR_ESTAT', '');
    const justificant = (id: any) => report(id, 'DESCARREGAR_JUSTIFICANT', '', 'ZIP');

    return {
        actualitzarEstat,
        justificant,
    }
}

const useRemesaActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {actualitzarEstat, justificant} = useActions(refresh)

    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = useRemesaDetail();
    const {handleOpen: handleNotificacioOpen, dialog: dialogNotificacio} = useNotificacioInteressatGrid(refresh);

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetallOpen,
        },
        {
            title: t('common.update'),
            icon: 'edit',
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: (row:any) => row.tipus != 'MANUAL',
        },
        {
            title: t('page.notificacio.acciones.actualitzarEstat'),
            icon: "sync",
            showInMenu: true,
            onClick: actualitzarEstat,
            hidden: (row:any) => row.estat == 'PROCESSADA',
        },
        {
            title: t('page.notificacio.acciones.notificacioInteressat'),
            icon: "send",
            showInMenu: true,
            onClick: handleNotificacioOpen,
        },
        {
            title: t('page.notificacio.acciones.justificant'),
            icon: "download",
            showInMenu: true,
            onClick: justificant,
            hidden: (row:any) => row.estat == 'PENDENT',
        },
		{
		    title: t('common.delete'),
		    icon: 'delete',
		    showInMenu: true,
            clickTriggerDelete: true,
		    hidden: (row:any) => row.tipus != 'MANUAL',
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