import useAnotacioDetail from "./AnotacioDetail.tsx";
import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";
import useRebutjar from "../actions/Rebutjar.tsx";
import {useNavigate} from "react-router-dom";
import {useUserSession} from "../../../components/Session.tsx";
import useAcceptar from "../actions/Acceptar.tsx";
import useSubsanarAnnexos from "../actions/SubsanarAnnexos.tsx";
import { icons as iconsAppMenu } from '@src/util/icons';

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
        artifactReport: apiReport,
    } = useResourceApiService('expedientPeticioResource');
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

    const downloadJustificant = (id:any) => report(id, 'DOWNLOAD_JUSTIFICANT', t('page.anotacio.action.justificant.ok'), 'PDF')

    const canviEstatDistribucio = (id:any) => {
        apiAction(undefined, {code: 'ESTAT_DISTRIBUCIO', data: {ids: [id], massivo: false}})
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.anotacio.action.canviEstatDistribucio.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }
    const consultar = (id:any) => {
        apiAction(undefined, {code: 'CONSULTAR_I_GUARDAR', data: {ids: [id], massivo: false}})
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.anotacio.action.consultar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {
        consultar,
        downloadJustificant,
        canviEstatDistribucio,
    }
}
export const useMassiveActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
    } = useResourceApiService('expedientPeticioResource');
    const {temporalMessageShow} = useBaseAppContext();

    const canviEstatDistribucio = (ids:any[]) => {
        apiAction(undefined, {code: 'ESTAT_DISTRIBUCIO', data: {ids, massivo: true}})
            .then((data:any) => {
                refresh?.();
                temporalMessageShow(null, t('page.anotacio.action.canviEstatDistribucio.massiveOk', {data}), 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }
    const consultar = (ids:any[]) => {
        apiAction(undefined, {code: 'CONSULTAR_I_GUARDAR', data: {ids, massivo: true}})
            .then((data:any) => {
                refresh?.();
                temporalMessageShow(null, t('page.anotacio.action.consultar.massiveOk', {data}), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {
        consultar,
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
        download,
    }
}

const useAnotacioActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const { rol } = useUserSession();

    const { canviEstatDistribucio } = useActions(refresh)
    const {handleShow: handleRebutjar, content: contentRebutjar} = useRebutjar(refresh)
    const {handleShow: handleAcceptar, content: contentAcceptar} = useAcceptar(refresh)
    const {handleShow: handleSubsanarAnnexos, content: contentSubsanarAnnexos} = useSubsanarAnnexos(refresh)
    const {handleOpen, dialog} = useAnotacioDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
        },
        {
            label: t('page.anotacio.action.acceptar.label'),
            icon: "check_circle",
            showInMenu: true,
            onClick: handleAcceptar,
            hidden: (row:any) => row?.estat != 'PENDENT' || row?.pendentCanviEstatDistribucio,
        },
        {
            label: t('page.anotacio.action.rebutjar.label'),
            icon: "close",
            showInMenu: true,
            onClick: handleRebutjar,
            hidden: (row:any) => row?.estat != 'PENDENT' || row?.pendentCanviEstatDistribucio,
        },
        {
            label: t('page.anotacio.action.canviProcediment.label'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: (row:any) => row?.estat != 'PENDENT' || row?.pendentCanviEstatDistribucio || !rol?.isAdmin,
        },
        {
            label: t('page.expedient.title'),
            icon: iconsAppMenu.expedient,
            showInMenu: true,
            onClick: (_id:any, row:any) => navigate(`/contingut/${row?.expedient?.id}`),
            hidden: (row:any) => row?.estatView != 'ACCEPTAT' || !row?.expedient,
        },
        {
            label: t('page.anotacio.action.canviEstatDistribucio.label'),
            icon: "turn_right",
            showInMenu: true,
            onClick: canviEstatDistribucio,
            hidden: (row:any) => !row?.pendentCanviEstatDistribucio || !rol?.isAdmin,
        },
        {
            label: t('page.anotacio.action.subsanarAnnexos.label'),
            icon: "healing",
            showInMenu: true,
            onClick: handleSubsanarAnnexos,
            hidden: (row:any) => !row?.teAnnexosAmbError,
        },
    ];

    const components = <>
        {dialog}
        {contentRebutjar}
        {contentAcceptar}
        {contentSubsanarAnnexos}
    </>;

    return {
        actions,
        components
    }
}
export default useAnotacioActions;