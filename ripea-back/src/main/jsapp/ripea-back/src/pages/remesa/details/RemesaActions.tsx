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
            hidden: (row:any) => row.estat == 'PROCESSADA',
        },
        {
            title: t('page.notificacio.acciones.notificacioInteressat'),
            icon: "send",
            showInMenu: true,
            onClick: handleNotificacioOpen,
            hidden: (row:any) => row.estat == 'PROCESSADA',
        },
        {
            title: t('page.notificacio.acciones.justificant'),
            icon: "download",
            showInMenu: true,
            hidden: (row:any) => row.estat == 'PENDENT',
        },
        {
            title: t('page.notificacio.acciones.certificat'),
            icon: "download",
            showInMenu: true,
            hidden: () => true, /* enviamentCertificacio */
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