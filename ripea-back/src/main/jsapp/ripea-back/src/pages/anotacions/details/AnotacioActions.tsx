import useAnotacioDetail from "./AnotacioDetail.tsx";
import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";
import useRebutjar from "../actions/Rebutjar.tsx";
import {useNavigate} from "react-router-dom";
import {icons} from "../../user/UserHeadToolbar.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import useAcceptar from "../actions/Acceptar.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
        artifactReport: apiReport,
    } = useResourceApiService('expedientPeticioResource');
    const {temporalMessageShow} = useBaseAppContext();

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

    const canviEstatDistribucio = (id:any) => action(id, 'ESTAT_DISTRIBUCIO', t('page.anotacio.action.canviEstatDistribucio.ok'))
    const downloadJustificant = (id:any) => report(id, 'DOWNLOAD_JUSTIFICANT', t('page.anotacio.action.justificant.ok'), 'PDF')

    return {
        downloadJustificant,
        canviEstatDistribucio,
    }
}
export const useAnexxActions = () => {
    const { t } = useTranslation();

    const {
        artifactReport: apiReport,
    } = useResourceApiService('registreAnnexResource');
    const {temporalMessageShow} = useBaseAppContext();

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

    const download = (id:any) => report(id, 'DOWNLOAD_ANNEX', t('page.anotacio.action.descargarAnnex.ok'), 'PDF')

    return {
        download
    }
}

const useAnotacioActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const { value: user } = useUserSession();
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';

    const { canviEstatDistribucio } = useActions(refresh)
    const {handleShow: handleRebutjar, content: contentRebutjar} = useRebutjar(refresh)
    const {handleShow: handleAcceptar, content: contentAcceptar} = useAcceptar(refresh)
    const {handleOpen, dialog} = useAnotacioDetail();

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
        },
        {
            title: t('page.anotacio.action.acceptar.label'),
            icon: "check_circle",
            showInMenu: true,
            onClick: handleAcceptar,
            hidden: (row:any) => row?.estat != 'PENDENT' || row?.pendentCanviEstatDistribucio,
        },
        {
            title: t('page.anotacio.action.rebutjar.label'),
            icon: "close",
            showInMenu: true,
            onClick: handleRebutjar,
            hidden: (row:any) => row?.estat != 'PENDENT' || row?.pendentCanviEstatDistribucio,
        },
        {
            title: t('page.anotacio.action.canviProcediment.label'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: (row:any) => row?.estat != 'PENDENT' || row?.pendentCanviEstatDistribucio || !isRolActualAdmin,
        },
        {
            title: t('page.expedient.title'),
            icon: icons.expedient,
            showInMenu: true,
            onClick: (id:any, row:any) => navigate(`/contingut/${row?.expedient?.id}`),
            hidden: (row:any) => row?.estatView != 'ACCEPTAT' || !row?.expedient,
        },
        {
            title: t('page.anotacio.action.canviEstatDistribucio.label'),
            icon: "turn_right",
            showInMenu: true,
            onClick: canviEstatDistribucio,
            hidden: (row:any) => !row?.pendentCanviEstatDistribucio || !isRolActualAdmin,
        },
    ];

    const components = <>
        {dialog}
        {contentRebutjar}
        {contentAcceptar}
    </>;

    return {
        actions,
        components
    }
}
export default useAnotacioActions;