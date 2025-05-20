import useRemesaDetail from "./RemesaDetail.tsx";
import {useTranslation} from "react-i18next";
import {
    useBaseAppContext,
    useResourceApiService
} from "reactlib";
import useNotificacioInteressatGrid from "./NotificacioInteressatGrid.tsx";
const useActions = (refresh?: () => void) => {
    const {temporalMessageShow} = useBaseAppContext();

    const {
        artifactAction: apiAction,
    } = useResourceApiService('documentNotificacioResource');

    const actualitzarEstat = (id: any) => {
        apiAction(id, {code: 'ACTUALITZAR_ESTAT'})
            .then(() => {
                refresh?.();
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {actualitzarEstat}
}

const useRemesaActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {actualitzarEstat} = useActions(refresh)

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
            title: t('page.notificacio.acciones.actualitzarEstat'),
            icon: "sync",
            showInMenu: true,
            onClick: actualitzarEstat,
            hidden: (row:any) => row.notificacioEstat == 'PROCESSADA',
        },
        {
            title: t('page.notificacio.acciones.notificacioInteressat'),
            icon: "send",
            showInMenu: true,
            onClick: handleNotificacioOpen,
            hidden: (row:any) => !row.hasDocumentInteressats,
        },
        {
            title: t('page.notificacio.acciones.justificant'),
            icon: "download",
            showInMenu: true,
            // onClick: ,
            hidden: (row:any) => row.notificacioEstat == 'PENDENT',
        },
        {
            title: t('common.update'),
            icon: 'edit',
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: (row:any) => row.tipus != 'MANUAL',
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