import {useTranslation} from "react-i18next";
import {useUserSession} from "../../../components/Session.tsx";
import useCanviEstatRevisio from "../actions/CanviEstatRevisio.tsx";
import useMetaExpedientDetail from "./MetaExpedientDetail.tsx";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaJSON} from "../../expedient/details/CommonActions.tsx";
import useReglaDistribucio from "../actions/ReglaDistribucio.tsx";
import useExpedientDialog from "./ExpedientDialog.tsx";

export const useActions = (refresh?: () => void) => {
    const {t} = useTranslation()
    const {
        patch: apiPatch,
        artifactAction: apiAction,
        artifactReport: apiReport,
    } = useResourceApiService('metaExpedientResource');
    const {temporalMessageShow} = useBaseAppContext();

    const active = (id:any) => {
        apiPatch(id, {data: { actiu: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaExpedient.action.activar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desactive = (id:any) => {
        apiPatch(id, {data: { actiu: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaExpedient.action.desactivar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const defecte = (id:any, grupId:any) => {
        apiAction(id, {code: 'TOGGLE_GRUP_DEF', data: { grupId }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.grup.action.default.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const llevarDefecte = (id:any) => {
        apiAction(id, {code: 'TOGGLE_GRUP_DEF', data: { grupId: null }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.grup.action.undefault.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desvincularGrup = (id:any, idGrup:any) => {
        apiAction(id, { code: 'DESVINCULAR_GRUP', data: {grup:{id:idGrup}} })
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.grup.action.unlink.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    const toogleRegla = (id:any, activa:boolean) => {
        apiAction(id, { code: 'TOGGLE_REGLA_ROLSAC', data: {activa} })
            .then(() => {
                refresh?.();
                activa
                    ? temporalMessageShow(null, t('page.metaExpedient.action.regla.active.ok'), 'success')
                    : temporalMessageShow(null, t('page.metaExpedient.action.regla.desactive.ok'), 'success')
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    const crearRegla = (id:any) => {
        apiAction(id, { code: 'CREAR_REGLA_ROLSAC' })
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.metaExpedient.action.regla.create.ok'), 'success')
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    const exportar = (id:any) => {
        apiReport(id, { code: 'REPORT_EXPORT_JSON', fileType: "JSON" })
            .then((result) => {
                iniciaDescargaJSON(result);
                temporalMessageShow(null, t('page.metaExpedient.action.export.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    const canviDisseny = (id:any) => {
        apiAction(id, { code: 'CANVIAR_DISSENY' })
            .then((result) => {
                iniciaDescargaJSON(result);
                temporalMessageShow(null, t('page.metaExpedient.action.canviDisseny.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {active, desactive, exportar, defecte, llevarDefecte, crearRegla, toogleRegla, desvincularGrup, canviDisseny}
}

export const useMetaExpedientActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {rol} = useUserSession()

    const {handleOpen: handleExpedient, dialog: dialogExpedient} = useExpedientDialog();
    const {handleOpen: handleDetail, dialog: dialogDetail} = useMetaExpedientDetail();
    const {handleShow: handleCanviEstat, content: contentCanviEstat} = useCanviEstatRevisio(refresh);
    const {handleOpen: handleRegla, dialog: dialogRegla} = useReglaDistribucio(refresh);
    const {active, desactive, exportar, canviDisseny} = useActions(refresh)

    const actions = [
        {
            label: t('page.metaExpedient.action.consultar.label'),
            icon: "search",
            showInMenu: true,
            onClick: handleDetail,
            hidden: !(rol?.isRevisor || rol?.isAdminLectura),
        },
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            onClick: handleDetail,
            hidden: (row:any) => !(row?.revisioEstat == 'REVISAT' && rol?.isOrganAdmin),
        },
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: (row:any) => rol?.isRevisor || rol?.isAdminLectura || (row?.revisioEstat == 'REVISAT' && rol?.isOrganAdmin),
        },
        {
            label: t('page.metaExpedient.action.canviEstat.label'),
            icon: "edit",
            showInMenu: true,
            onClick: handleCanviEstat,
            hidden: !rol?.isRevisor,
        },
        {
            label: t('page.metaExpedient.action.expedient.label'),
            icon: "business_center",
            showInMenu: true,
            onClick: handleExpedient,
            hidden: rol?.isRevisor,
        },
        {
            label: t('common.export'),
            icon: "upload",
            showInMenu: true,
            onClick: exportar,
            hidden: !(rol?.isAdmin || rol?.isOrganAdmin),
        },
        {
            label: t('page.metaExpedient.action.regla.label'),
            icon: "search",
            showInMenu: true,
            onClick: handleRegla,
            hidden: !(rol?.isAdmin || rol?.isOrganAdmin),
        },
        {
            label: t('page.metaExpedient.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: active,
            hidden: (row:any) => row?.actiu || !(rol?.isAdmin || rol?.isOrganAdmin),
        },
        {
            label: t('page.metaExpedient.action.desactivar.label'),
            icon: "cancel",
            showInMenu: true,
            onClick: desactive,
            hidden: (row:any) => !row?.actiu || !(rol?.isAdmin || rol?.isOrganAdmin),
        },
        {
            label: t('page.metaExpedient.action.canviDisseny.label'),
            icon: "check",
            showInMenu: true,
            onClick: canviDisseny,
            hidden: !rol?.isOrganAdmin,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
            hidden: !rol?.isAdmin,
        },
    ]

    const components = <>
        {dialogExpedient}
        {contentCanviEstat}
        {dialogDetail}
        {dialogRegla}
    </>

    return {
        actions,
        components
    }
}