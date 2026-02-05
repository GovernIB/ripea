import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {Divider} from "@mui/material";

export const useActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {
        patch: apiPatch,
        artifactAction: apiAction,
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

    const marcarDefecte = (id:any) => {
        apiAction(id, { code: 'MARCAR_DEFECTE' })
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaDocument.action.default.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desmarcarDefecte = (id:any) => {
        apiAction(id, { code: 'DESMARCAR_DEFECTE' })
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaDocument.action.undefault.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const reordering = (id:any, ordre:number) => {
        apiAction(id, { code: 'REORDENAR', data: ordre })
            .then(() => refresh?.())
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {active, desactive, marcarDefecte, desmarcarDefecte, reordering}
}

export const useMetaDocumentActions = (refresh?: () => void) => {
    const {t} = useTranslation()
    const {marcarDefecte, desmarcarDefecte, active, desactive} = useActions(refresh)
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
            label: t('page.metaDocument.action.default.label'),
            icon: "check_box",
            showInMenu: true,
            onClick: marcarDefecte,
            hidden: (row:any) => row?.perDefecte || !row?.metaExpedient,
        },
        {
            label: t('page.metaDocument.action.undefault.label'),
            icon: "close",
            showInMenu: true,
            onClick: desmarcarDefecte,
            hidden: (row:any) => !row?.perDefecte || !row?.metaExpedient,
        },        
        {
            label: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
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