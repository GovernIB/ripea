import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
    } = useResourceApiService('avisResource');
    const {temporalMessageShow} = useBaseAppContext();

    const activar = (id:any, active:boolean) => {
        massiveActivar([id], active)
    }
    const massiveActivar = (ids:any[], active:boolean) => {
        apiAction(undefined, {code: 'MASSIVE_ACTIVE', data: { ids, active }})
            .then(() => {
                refresh?.();
                if (active) temporalMessageShow(null, t('page.avis.action.activar.ok'), 'success')
                else temporalMessageShow(null, t('page.avis.action.desactivar.ok'), 'success')
            })
            .catch((error) => {
                if (error?.message)
                    temporalMessageShow(null, error?.message, 'error');
            });
    }
    const massiveDelete = (ids:any[]) => {
        apiAction(undefined, {code: 'MASSIVE_DELETE', data: { ids }})
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.avis.action.delete.ok'), 'success')
            })
            .catch((error) => {
                if (error?.message)
                    temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        activar,
        massiveActivar,
        massiveDelete,
    }
}

export const useAvisActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {activar, massiveActivar, massiveDelete} = useActions(refresh)

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: t('page.avis.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: (id:any) => activar(id, true),
            hidden: (row:any) => row?.actiu,
        },
        {
            label: t('page.avis.action.desactivar.label'),
            icon: "close",
            showInMenu: true,
            onClick: (id:any) => activar(id, false),
            hidden: (row:any) => !row?.actiu,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ]
    const massiveActions = [
        {
            label: t('page.avis.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: (ids:any[]) => massiveActivar(ids, true),
        },
        {
            label: t('page.avis.action.desactivar.label'),
            icon: "close",
            showInMenu: true,
            onClick: (ids:any[]) => massiveActivar(ids, false),
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: massiveDelete,
        },
    ]
    const components = <></>

    return {
        actions,
        massiveActions,
        components
    }
}