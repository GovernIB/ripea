import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";

const useActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {
        patch: apiPatch,
    } = useResourceApiService('metaDocumentResource');
    const {temporalMessageShow} = useBaseAppContext();

    const active = (id:any) => {
        apiPatch(id, {data: { actiu: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaDocument.action.activar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desactive = (id:any) => {
        apiPatch(id, {data: { actiu: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaDocument.action.desactivar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {active, desactive}
}

export const useMetaDocumentActions = (refresh?: () => void) => {
    const {t} = useTranslation()
    const {active, desactive} = useActions(refresh)
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: t('page.metaDocument.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: active,
            hidden: (row:any) => row?.actiu,
        },
        {
            label: t('page.metaDocument.action.desactivar.label'),
            icon: "close",
            showInMenu: true,
            onClick: desactive,
            hidden: (row:any) => !row?.actiu,
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