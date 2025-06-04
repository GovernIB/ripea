import useAnotacioDetail from "./AnotacioDetail.tsx";
import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";

export const useActions = () => {
    const { t } = useTranslation();

    const {
        artifactReport: apiReport
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

    return {
        downloadJustificant
    }
}

const useAnotacioActions = () => {
    const { t } = useTranslation();
    const {handleOpen, dialog} = useAnotacioDetail();

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
        },
    ];

    const components = <>
        {dialog}
    </>;

    return {
        actions,
        components
    }
}
export default useAnotacioActions;