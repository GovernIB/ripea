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

    const defecte = (id:any, idGrup:any) => {
        apiPatch(id, {data: { grupPerDefecte: {id: idGrup} }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.grup.action.default.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const llevarDefecte = (id:any) => {
        apiPatch(id, {data: { grupPerDefecte: null }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.grup.action.default.ok'), 'success');
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

    const exportar = (id:any) => {
        apiReport(id, { code: 'REPORT_EXPORT_JSON', fileType: "JSON" })
            .then((result) => {
                iniciaDescargaJSON(result);
                temporalMessageShow(null, t(''), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {active, desactive, exportar, defecte, llevarDefecte, toogleRegla, desvincularGrup}
}

export const useMetaExpedientActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {value: user} = useUserSession()

    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualAdminLectura = user?.rolActual == 'IPA_ADMIN_LECTURA';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';
    const isRolActualRevisor = user?.rolActual == 'IPA_REVISIO';

    const {handleOpen: handleExpedient, dialog: dialogExpedient} = useExpedientDialog();
    const {handleOpen: handleDetail, dialog: dialogDetail} = useMetaExpedientDetail();
    const {handleShow: handleCanviEstat, content: contentCanviEstat} = useCanviEstatRevisio(refresh);
    const {handleOpen: handleRegla, dialog: dialogRegla} = useReglaDistribucio(refresh);
    const {active, desactive, exportar} = useActions(refresh)

    const actions = [
        {
            label: t('page.metaExpedient.action.consultar.label'),
            icon: "search",
            showInMenu: true,
            onClick: handleDetail,
            hidden: !(isRolActualRevisor || isRolActualAdminLectura),
        },
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: isRolActualRevisor || isRolActualAdminLectura,
        },
        {
            label: t('page.metaExpedient.action.canviEstat.label'),
            icon: "edit",
            showInMenu: true,
            onClick: handleCanviEstat,
            hidden: !isRolActualRevisor,
        },
        {
            label: t('page.metaExpedient.action.expedient.label'),
            icon: "business_center",
            showInMenu: true,
            onClick: handleExpedient,
            hidden: isRolActualRevisor,
        },
        {
            label: t('common.export'),
            icon: "upload",
            showInMenu: true,
            onClick: exportar,
            hidden: !(isRolActualAdmin || isRolActualOrganAdmin),
        },
        {
            label: t('page.metaExpedient.action.regla.label'),
            icon: "search",
            showInMenu: true,
            onClick: handleRegla,
            hidden: !(isRolActualAdmin || isRolActualOrganAdmin),
        },
        {
            label: t('page.metaExpedient.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: active,
            hidden: (row:any) => row?.actiu || !(isRolActualAdmin || isRolActualOrganAdmin),
        },
        {
            label: t('page.metaExpedient.action.desactivar.label'),
            icon: "cancel",
            showInMenu: true,
            onClick: desactive,
            hidden: (row:any) => !row?.actiu || !(isRolActualAdmin || isRolActualOrganAdmin),
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
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