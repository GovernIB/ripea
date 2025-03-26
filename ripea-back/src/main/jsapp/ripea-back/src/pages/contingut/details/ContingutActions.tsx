import useDocumentDetail from "./DocumentDetail.tsx";
import useInformacioArxiu from "../../expedient/actions/InformacioArxiu.tsx";
import {useTranslation} from "react-i18next";
import useEnviarViaEmail from "../actions/EnviarViaEmail.tsx";

export const useContingutActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    // const { temporalMessageShow } = useBaseAppContext();
    // const {
    //     patch: apiPatch,
    //     fieldDownload: apiDownload
    // } = useResourceApiService('documentResource');

    const {handleOpen: detallhandleOpen, dialog: detallDialog} = useDocumentDetail();
    const {handleOpen: arxiuhandleOpen, dialog: arxiuDialog} = useInformacioArxiu("archivo");
    const {handleShow: handleEnviarViaEmailShow, content: contentEnviarViaEmail} = useEnviarViaEmail(refresh);

    const actions = [
        {
            title: t('page.document.acciones.detall'),
            icon: "folder",
            showInMenu: true,
            onClick: detallhandleOpen
        },
        {
            title: t('page.document.acciones.move'),
            icon: "open_with",
            showInMenu: true,
        },
        {
            title: t('common.download'),
            icon: "download",
            showInMenu: true,
            // onClick: (id:any)=>apiDownload(id,{fieldName: ''})
        },
        {
            title: t('page.document.acciones.view'),
            icon: "search",
            showInMenu: true,
            disabled: true,
        },
        {
            title: t('page.document.acciones.portafirmes'),
            icon: "mail",
            showInMenu: true,
        },
        {
            title: t('page.document.acciones.firmar'),
            icon: "edit_document",
            showInMenu: true,
        },
        {
            title: t('page.document.acciones.mail'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEnviarViaEmailShow,
        },
        {
            title: t('page.document.acciones.history'),
            icon: "list",
            showInMenu: true,
        },
        {
            title: t('page.document.acciones.infoArxiu'),
            icon: "info",
            showInMenu: true,
            onClick: arxiuhandleOpen
        },
        {
            title: t('page.document.acciones.export'),
            icon: "download",
            showInMenu: true,
            disabled: true,
        },
    ]
    const components = <>
        {detallDialog}
        {arxiuDialog}
        {contentEnviarViaEmail}
    </>;
    return {
        actions,
        components
    }
}