import {useTranslation} from "react-i18next";
import {useUserSession} from "../../../components/Session.tsx";
import useCanviEstatRevisio from "../actions/CanviEstatRevisio.tsx";
import useMetaExpedientDetail from "./MetaExpedientDetail.tsx";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob, iniciaDescargaJSON} from "../../expedient/details/CommonActions.tsx";

const useActions = (refresh?: () => void) => {
    const {t} = useTranslation()
    const {
        patch: apiPatch,
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

    return {active, desactive, exportar}
}

export const useMetaExpedientActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {value: user} = useUserSession()

    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualAdminLectura = user?.rolActual == 'IPA_ADMIN_LECTURA';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';
    const isRolActualRevisor = user?.rolActual == 'IPA_REVISIO';

    const {handleOpen: handleDetail, dialog: dialogDetail} = useMetaExpedientDetail();
    const {handleShow: handleCanviEstat, content: contentCanviEstat} = useCanviEstatRevisio(refresh);
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
            hidden: !(isRolActualAdmin || isRolActualOrganAdmin),
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
        {contentCanviEstat}
        {dialogDetail}
    </>

    return {
        actions,
        components
    }
}