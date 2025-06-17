import {Divider} from "@mui/material";
import {MuiDataGridApiRef, useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useUserSession} from "../../../components/Session.tsx";
import useDocumentDetail from "./DocumentDetail.tsx";
import useHistoric from "../../Historic.tsx";
import useInformacioArxiu from "../../InformacioArxiu.tsx";
import {iniciaDescargaBlob, useCommonActions} from "../../expedient/details/CommonActions.tsx";
import {potModificar} from "../../expedient/details/Expedient.tsx";
import {useMoure, useCopiar, useVincular} from "../actions/Moure.tsx";
import useNotificar from "../actions/Notificar.tsx";
import usePublicar from "../actions/Publicar.tsx";
import useEnviarPortafirmes from "../actions/EnviarPortafirmes.tsx";
import useVisualitzar from "../actions/Visualitzar.tsx";
import useEnviarViaEmail from "../actions/EnviarViaEmail.tsx";
import useSeguimentPortafirmes from "../actions/SeguimentPortafirmes.tsx";
import useFirmaNavegador from "../actions/FirmaNavegador.tsx";
import useDocPinbal from "../actions/DocPinbal.tsx";
import useEnviarViaFirma from "../actions/EnviarViaFirma.tsx";
import useCrearCarpeta from "../../carpeta/actions/CrearCarpeta.tsx";
import useImportar from "../actions/Importar.tsx";
import useCarpetaActions from "../../carpeta/details/CarpetaActions.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
        artifactReport: apiReport,
        fieldDownload: apiDownload,
    } = useResourceApiService('documentResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const downloadAdjunt = (id:any, fieldName:string, mssg:string) :void => {
        apiDownload(id,{fieldName})
            .then((result)=>{
                iniciaDescargaBlob(result);
                temporalMessageShow(null, mssg, 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const enllacCSV = (id:any) => {
        apiAction(id, {code: 'GET_CSV_LINK'})
            .then((result) => {
                navigator.clipboard.writeText(result?.url)
                    .then(()=>{
                        temporalMessageShow(null, t('page.document.action.csv.ok'), 'success');
                    })
                    .catch((error) => {
                        temporalMessageShow(null, error?.message, 'error');
                    });
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const descarregarVersio = (id:any, version:string, fileType:any = 'PDF') => {
        apiReport(id, {code: "DESCARREGAR_VERSIO", data: { version }, fileType})
            .then((result)=>{
                iniciaDescargaBlob(result);
                temporalMessageShow(null, t('page.expedient.results.actionOk'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const definitiu = (id:any) => {
        messageDialogShow(
            '',
            t('page.document.action.definitive.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    apiAction(id, {code: 'CONVERTIR_DEFINITIU'})
                        .then((result:any)=>{
                            refresh?.()
                            temporalMessageShow(null, t('page.document.action.definitive.ok', {document: result?.nom}), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }

    return {
        apiDownload: downloadAdjunt,
        getLinkCSV: enllacCSV,
        descarregarVersio,
        definitiu
    }
}

export const useContingutActions = (entity:any, apiRef:MuiDataGridApiRef, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession()

    const {handleShow: handleDocPinbal, content: contentDocPinbal} = useDocPinbal(entity, refresh)
    const {handleShow: handleCrearCarpeta, content: contentCrearCarpeta} = useCrearCarpeta(entity, refresh)
    const {handleShow: handleImportar, content: contentImportar} = useImportar(entity, refresh)

    const {apiDownload, getLinkCSV, definitiu} = useActions()
    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = useDocumentDetail();
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleOpen: handleVisualitzarOpen, dialog: dialogVisualitzar} = useVisualitzar();
    const {handleOpen: handleSeguimentOpen, dialog: dialogSeguiment} = useSeguimentPortafirmes(potModificar(entity), refresh);
    const {handleOpen: handleArxiuOpen, dialog: arxiuDialog} = useInformacioArxiu('documentResource', 'ARXIU_DOCUMENT');
    const {handleShow: handleMoureShow, content: contentMoure} = useMoure(refresh);
    const {handleShow: handleCopiarShow, content: contentCopiar} = useCopiar(refresh);
    const {handleShow: handleVincularShow, content: contentVincular} = useVincular(refresh);
    const {handleShow: handleEnviarViaEmailShow, content: contentEnviarViaEmail} = useEnviarViaEmail(refresh);
    const {handleShow: handleNotificarShow, content: contentNotificar} = useNotificar(refresh);
    const {handleShow: handlePublicarShow, content: contentPublicar} = usePublicar(refresh);
    const {handleShow: handleEviarPortafirmesShow, content: contentEviarPortafirmes} = useEnviarPortafirmes(refresh);
    const {handleShow: handleFirmaShow, content: contentFirma} = useFirmaNavegador(refresh);
    const {handleShow: handleEnviarViaFirma, content: contentEnviarViaFirma} = useEnviarViaFirma(refresh)

    const isDocument= (row:any) => row?.tipus=="DOCUMENT";
    const isDigitalOrImportat = (row:any) => {
        return isInOptions(row.documentTipus, 'DIGITAL', 'IMPORTAT');
    };

    const isFirmaActiva = (row:any) => {
        return isInOptions(row?.estat, 'REDACCIO', 'FIRMA_PARCIAL') && isDigitalOrImportat(row) && !isInOptions(row?.fitxerExtension, 'zip')
    }
    const isPermesModificarCustodiatsVar = (row:any) => {
        return user?.sessionScope?.isPermesModificarCustodiats && isInOptions(row?.estat, 'CUSTODIAT', 'FIRMAT', 'FIRMA_PARCIAL', 'DEFINITIU')
    }

    const isInOptions = (value:string, ...options:string[]) => {
        return options.includes(value)
    }

    const potMod = potModificar(entity)

    const createDocumentActions = [
        {
            title: t('common.create')+"...",
            icon: "description",
            onClick: () => apiRef?.current?.showCreateDialog?.(),
        },
        {
            title: t('page.document.action.pinbal.label'),
            icon: "description",
            onClick: handleDocPinbal,
            disabled: !entity?.ambDocumentsPinbal,
        },
        {
            title: t('page.carpeta.action.new.label'),
            icon: "folder",
            onClick: handleCrearCarpeta,
            disabled: !user?.sessionScope?.isCreacioCarpetesActiva,
        },
        {
            title: t('page.document.action.import.label'),
            icon: "upload_file",
            onClick: handleImportar,
            disabled: !user?.sessionScope?.isMostrarImportacio,
        },
    ];

    const documentActions = [
        {
            title: t('page.contingut.action.guardarArxiu.label'),
            icon: 'autorenew',
            showInMenu: true,
            // onClick: ,
            disabled: !entity?.arxiuUuid,
            hidden: (row:any) => row?.arxiuUuid,
        },
        {
            title: t('page.document.action.detall.label'),
            icon: "folder",
            showInMenu: true,
            onClick: handleDetallOpen,
        },
        {
            title: t('common.update')+'...',
            icon: 'edit',
            showInMenu: true,
            clickShowUpdateDialog: true,
            disabled: (row:any) => (row?.arxiuUuid == null || row?.gesDocFirmatId != null),
            hidden: (row:any) => !potMod || (isInOptions(row?.arxiuEstat, 'DEFINITIU') && !isPermesModificarCustodiatsVar(row)) ||  isInOptions(row?.estat, 'FIRMA_PENDENT'),
        },
        {
            title: t('page.document.action.move.label'),
            icon: "open_with",
            showInMenu: true,
            onClick: handleMoureShow,
            disabled: (row:any) => row?.gesDocAdjuntId!=null,
            hidden: !potMod,
        },
        {
            title: t('page.document.action.copy.label'),
            icon: "file_copy",
            showInMenu: true,
            onClick: handleCopiarShow,
            hidden: !potMod || !user?.sessionScope?.isMostrarCopiar,
        },
        {
            title: t('page.document.action.vincular.label'),
            icon: "link",
            showInMenu: true,
            onClick: handleVincularShow,
            hidden: !potMod || !user?.sessionScope?.isMostrarVincular,
        },
        {
            title: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            title: t('page.document.action.imprimible.label'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'imprimible', t('page.document.action.imprimible.ok')),
            disabled: (row:any) => isInOptions(row?.fitxerExtension, 'xsig'),
            hidden: (row:any) => !isDigitalOrImportat(row) || !( (row?.arxiuEstat=='DEFINITIU' || row?.estat=='FIRMA_PARCIAL') || user?.sessionScope?.imprimibleNoFirmats),
        },
        {
            title: t('common.download'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'adjunt', t('page.expedient.results.actionOk')),
            hidden: (row:any) => !isDigitalOrImportat(row),
        },
        {
            title: t('page.document.action.original.label'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'original', t('page.document.action.original.ok')),
            hidden: (row:any) => !isDigitalOrImportat(row) || !row?.gesDocOriginalId
        },
        {
            title: t('page.document.action.firma.label'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'firmaAdjunt', t('page.document.action.firma.ok')),
            hidden: (row:any) => !isDigitalOrImportat(row) || row?.ntiTipoFirma != "TF04"
        },
        {
            title: t('page.document.action.view.label'),
            icon: "search",
            showInMenu: true,
            onClick: handleVisualitzarOpen,
            disabled: (row:any) => !isInOptions(row?.fitxerExtension, 'pdf', 'odt', 'docx'),
            hidden: (row:any) => !isDigitalOrImportat(row),
        },
        {
            title: t('page.document.action.csv.label'),
            icon: "file_copy",
            showInMenu: true,
            onClick: getLinkCSV,
            hidden: (row:any) => !isInOptions(row?.estat, 'DEFINITIU', 'CUSTODIAT') || !user?.sessionScope?.isUrlValidacioDefinida,
        },
        {
            title: t('page.document.action.portafirmes.label'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEviarPortafirmesShow,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden : (row:any) => !potMod || !row?.metaDocumentInfo?.firmaPortafirmesActiva || !isFirmaActiva(row),
        },
        {
            title: t('page.document.action.firmar.label'),
            icon: "edit_document",
            showInMenu: true,
            onClick: handleFirmaShow,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden: (row:any) => !potMod || !row?.metaDocumentInfo?.firmaPassarelaActiva || !isFirmaActiva(row),
        },
        {
            title: t('page.document.action.viaFirma.label'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEnviarViaFirma,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden: (row:any) => !potMod || !row?.metaDocumentInfo?.firmaBiometricaActiva || !isFirmaActiva(row),
        },
        {
            title: entity?.metaExpedient?.tipusClassificacio == 'SIA' // notificar/comunicar
                ? t('page.document.action.notifica.labelr')
                : t('page.document.action.comunicar.label'),
            icon: "mail",
            showInMenu: true,
            onClick: handleNotificarShow,
            hidden: (row:any) => !potMod || !(row?.documentFirmaTipus != 'SENSE_FIRMA' && row?.arxiuUuid || isInOptions(row?.fitxerExtension, 'zip')),
        },
        {
            title: t('page.document.action.publicar.label'),
            icon: "publish",
            showInMenu: true,
            onClick: handlePublicarShow,
            hidden: (row:any) => !potMod || !(row?.documentFirmaTipus != 'SENSE_FIRMA' && row?.arxiuUuid || isInOptions(row?.fitxerExtension, 'zip')) || !user?.sessionScope?.isMostrarPublicar
        },
        {
            title: t('page.document.action.mail.label'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEnviarViaEmailShow,
        },
        {
            title: t('page.document.action.seguiment.label'),
            icon: "info",
            showInMenu: true,
            onClick: handleSeguimentOpen,
            hidden: (row:any) => !(row?.estat == 'FIRMA_PENDENT' && row?.documentTipus == 'DIGITAL'),
        },
        {
            title: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            title: t('page.document.action.definitive.label'),
            icon: "check_circle",
            showInMenu: true,
            onClick: definitiu,
            hidden: (row:any) => !potMod || !isInOptions(row?.estat, 'REDACCIO' , 'FIRMA_PARCIAL') || !isInOptions(row?.documentTipus, 'DIGITAL') || !user?.sessionScope?.isConvertirDefinitiuActiu,
        },
        {
            title: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row:any) => !potMod || !isInOptions(row?.estat, 'REDACCIO' , 'FIRMA_PARCIAL') || !isInOptions(row?.documentTipus, 'DIGITAL') || !user?.sessionScope?.isConvertirDefinitiuActiu,
        },
        {
            title: t('page.contingut.action.history.label'),
            icon: "list",
            showInMenu: true,
            onClick: handleHistoricOpen,
        },
        {
            title: t('page.contingut.action.infoArxiu.label'),
            icon: "info",
            showInMenu: true,
            onClick: handleArxiuOpen,
            disabled: (row:any) => !row?.arxiuUuid,
        },
    ]
        .map(({ hidden, ...rest }) => ({
            ...rest,
            hidden: (row: any) => (typeof hidden === 'function' ? hidden(row) : !!hidden) || !isDocument(row)
        }));

    const {actions: carpetaActions, components: componentsActions} = useCarpetaActions(entity, refresh)
    const {actions: expedientActions, components: expedientComponents} = useCommonActions(refresh);

    const components = <>
        {expedientComponents}
        {dialogDetall}
        {dialogHistoric}
        {dialogVisualitzar}
        {arxiuDialog}
        {contentMoure}
        {contentCopiar}
        {contentVincular}
        {contentEnviarViaEmail}
        {contentNotificar}
        {contentPublicar}
        {contentEviarPortafirmes}
        {dialogSeguiment}
        {contentFirma}
        {contentDocPinbal}
        {contentEnviarViaFirma}
        {contentCrearCarpeta}
        {contentImportar}
        {componentsActions}
    </>;
    return {
        createActions: createDocumentActions,
        actions: [
            ...documentActions,
            ...carpetaActions,
            ...expedientActions,
        ],
        hiddenDelete: (row:any) => !potMod || !isDocument(row) || row?.estat == 'DEFINITIU',
        components
    }
}