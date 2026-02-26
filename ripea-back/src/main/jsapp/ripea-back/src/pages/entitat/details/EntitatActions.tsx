import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        patch: apiPatch
    } = useResourceApiService('entitatResource');
    const {temporalMessageShow} = useBaseAppContext();

    const activar = (id:any) => {
        apiPatch(id, {data: { activa: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.entitat.action.activar.ok'), 'success');
            })
            .catch((error) => {
                if (error?.message)
                    temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desactivar = (id:any) => {
        apiPatch(id, {data: { activa: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.entitat.action.desactivar.ok'), 'success');
            })
            .catch((error) => {
                if (error?.message)
                    temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        activar,
        desactivar
    }
}

export const useEntitatActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {activar, desactivar} = useActions(refresh)

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: t('page.entitat.action.config.label'),
            icon: "settings",
            showInMenu: true,
            linkTo: (row:any) => `/entitat/${row?.id}/configurar`,
        },
        {
            label: t('page.entitat.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: activar,
            hidden: (row:any) => row?.activa,
        },
        {
            label: t('page.entitat.action.desactivar.label'),
            icon: "close",
            showInMenu: true,
            onClick: desactivar,
            hidden: (row:any) => !row?.activa,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ]
    const components = <></>

    return {
        actions,
        components
    }
}