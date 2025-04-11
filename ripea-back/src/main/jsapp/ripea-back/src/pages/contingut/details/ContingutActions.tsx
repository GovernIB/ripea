import useDocumentDetail from "./DocumentDetail.tsx";
import {useTranslation} from "react-i18next";
import useEnviarViaEmail from "../actions/EnviarViaEmail.tsx";
import {useResourceApiService} from "reactlib";
import useMoure from "../actions/Moure.tsx";
import useHistoric from "./Historic.tsx";
import useNotificar from "../actions/Notificar.tsx";
import usePublicar from "../actions/Publicar.tsx";
import useEviarPortafirmes from "../actions/EviarPortafirmes.tsx";
import useInformacioArxiu from "../../InformacioArxiu.tsx";

const useActions = (refresh?: () => void) => {
    const {
        fieldDownload: apiDownload,
    } = useResourceApiService('documentResource');

    const downloadAdjunt = (id:any) :void => {
        apiDownload(id,{fieldName: 'adjunt'})
            .then((result)=>{
                const url = URL.createObjectURL(result.blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = result.fileName; // Usa el nombre recibido
                document.body.appendChild(link);
                link.click();

                // Limpieza
                document.body.removeChild(link);
                URL.revokeObjectURL(url);
                refresh?.();
            })
    }

    return {
        apiDownload: downloadAdjunt,
    }
}

export const useContingutActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {apiDownload} = useActions(refresh)

    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = useDocumentDetail();
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleOpen: arxiuhandleOpen, dialog: arxiuDialog} = useInformacioArxiu('documentResource', 'ARXIU_DOCUMENT');
    const {handleShow: handleMoureShow, content: contentMoure} = useMoure(refresh);
    const {handleShow: handleEnviarViaEmailShow, content: contentEnviarViaEmail} = useEnviarViaEmail(refresh);
    const {handleShow: handleNotificarShow, content: contentNotificar} = useNotificar(refresh);
    const {handleShow: handlePublicarShow, content: contentPublicar} = usePublicar(refresh);
    const {handleShow: handleEviarPortafirmesShow, content: contentEviarPortafirmes} = useEviarPortafirmes(refresh);

    const hideIfNotDocument = (row:any) => row?.tipus!="DOCUMENT";

    const actions = [
        {
            title: t('page.document.acciones.detall'),
            icon: "folder",
            showInMenu: true,
            onClick: handleDetallOpen,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.move'),
            icon: "open_with",
            showInMenu: true,
            onClick: handleMoureShow,
            hidden: hideIfNotDocument,
        },
        {
            title: t('common.download'),
            icon: "download",
            showInMenu: true,
            onClick: apiDownload,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.view'),
            icon: "search",
            showInMenu: true,
            disabled: true,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.portafirmes'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEviarPortafirmesShow,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.firmar'),
            icon: "edit_document",
            showInMenu: true,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.notificar'),
            icon: "mail",
            showInMenu: true,
            onClick: handleNotificarShow,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.publicar'),
            icon: "publish",
            showInMenu: true,
            onClick: handlePublicarShow,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.mail'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEnviarViaEmailShow,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.history'),
            icon: "list",
            showInMenu: true,
            onClick: handleHistoricOpen,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.infoArxiu'),
            icon: "info",
            showInMenu: true,
            onClick: arxiuhandleOpen,
            disabled: (row:any) => !row?.arxiuUuid,
            hidden: hideIfNotDocument,
        },
        {
            title: t('page.document.acciones.export'),
            icon: "download",
            showInMenu: true,
            disabled: true,
            hidden: hideIfNotDocument,
        },
    ]

    const components = <>
        {dialogDetall}
        {dialogHistoric}
        {arxiuDialog}
        {contentMoure}
        {contentEnviarViaEmail}
        {contentNotificar}
        {contentPublicar}
        {contentEviarPortafirmes}
    </>;
    return {
        actions,
        components
    }
}