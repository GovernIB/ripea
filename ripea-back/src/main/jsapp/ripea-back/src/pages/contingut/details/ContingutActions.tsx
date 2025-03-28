import useDocumentDetail from "./DocumentDetail.tsx";
import useInformacioArxiu from "../../expedient/actions/InformacioArxiu.tsx";
import {useTranslation} from "react-i18next";
import useEnviarViaEmail from "../actions/EnviarViaEmail.tsx";
import {MuiDataGridApi, useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import useMoure from "../actions/Moure.tsx";
import useHistoric from "./Historic.tsx";
import useNotificar from "../actions/Notificar.tsx";
import usePublicar from "../actions/Publicar.tsx";
import useEviarPortafirmes from "../actions/EviarPortafirmes.tsx";
import {MutableRefObject} from "react";

export const useContingutActions = (dataGridApiRef: MutableRefObject<MuiDataGridApi>) => {
    const { t } = useTranslation();
    const {
        fieldDownload: apiDownload,
        delette: apiDelete,
    } = useResourceApiService('documentResource');

    const refresh = () => {
        dataGridApiRef.current?.refresh?.();
    }

    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = useDocumentDetail();
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleOpen: arxiuhandleOpen, dialog: arxiuDialog} = useInformacioArxiu("archivo");
    const {handleShow: handleMoureShow, content: contentMoure} = useMoure(refresh);
    const {handleShow: handleEnviarViaEmailShow, content: contentEnviarViaEmail} = useEnviarViaEmail(refresh);
    const {handleShow: handleNotificarShow, content: contentNotificar} = useNotificar(refresh);
    const {handleShow: handlePublicarShow, content: contentPublicar} = usePublicar(refresh);
    const {handleShow: handleEviarPortafirmesShow, content: contentEviarPortafirmes} = useEviarPortafirmes(refresh);

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
            })
    }
    const deleteDocument = (id:any) :void => {
        messageDialogShow(
            t('page.document.dialog.deleteTitle'),
            t('page.document.dialog.deleteMessage'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    apiDelete(id)
                        .then(() => {
                            refresh?.();
                            temporalMessageShow(null, 'Elemento borrado', 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow('Error', error.message, 'error');
                        });
                }
            });
    }

    const hideIfNotDocument = (row:any) => {
        return !(row?.id && row?.tipus=="DOCUMENT")
    }

    const actions = [
        {
            title: t('common.update'),
            icon: 'edit',
            onClick: dataGridApiRef.current.showUpdateDialog,
            hidden: hideIfNotDocument,
        },

        ///////////////////////////////

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
            title: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: deleteDocument,
            hidden: hideIfNotDocument,
        },
        {
            title: t('common.download'),
            icon: "download",
            showInMenu: true,
            onClick: downloadAdjunt,
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