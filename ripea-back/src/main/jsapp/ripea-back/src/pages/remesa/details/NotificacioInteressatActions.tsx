import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";
import useAmpliarPlac from "../actions/AmpliarPlac.tsx";
import useNotificacioInteressatDetail from "./NotificacioInteressatDetail.tsx";

const useActions = (refresh?: () => void) => {
    const {temporalMessageShow} = useBaseAppContext();

    const {
        artifactReport: apiReport,
    } = useResourceApiService('documentEnviamentInteressatResource');

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

    const certificat = (id:any) => report(id, 'DESCARREGAR_CERTIFICAT', '', 'ZIP')

    return {
        certificat
    }
}

const useNotificacioInteressatActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {certificat} = useActions(refresh);
    const {handleOpen, dialog} = useNotificacioInteressatDetail();
    const {handleShow, content} = useAmpliarPlac(refresh);

    const actions = [
        {
            title: t('common.detail'),
            icon: 'info',
            showInMenu: true,
            onClick: handleOpen,
        },
        {
            title: t('page.notificacioInteressat.acciones.ampliarPlac'),
            icon: "edit_calendar",
            showInMenu: true,
            onClick: handleShow,
            hidden: (row:any) => row?.finalitzat || entity?.notificacioEstat == 'PROCESSADA',
        },
        {
            title: t('page.notificacioInteressat.acciones.certificat'),
            icon: "download",
            showInMenu: true,
            onClick: certificat,
            hidden: (row:any) => !row?.enviamentCertificacioData,
        },
    ]

    const components = <>
        {dialog}
        {content}
    </>;
    return {
        actions,
        components
    }
}
export default useNotificacioInteressatActions;