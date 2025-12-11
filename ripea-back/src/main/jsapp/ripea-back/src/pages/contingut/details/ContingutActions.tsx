import {Divider} from "@mui/material";
import {MuiDataGridApiRef, useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useUserSession} from "../../../components/Session.tsx";
import useDocumentDetail from "./DocumentDetail.tsx";
import useHistoric from "../../Historic.tsx";
import useInformacioArxiu from "../../InformacioArxiu.tsx";
import {iniciaDescargaBlob, useCommonActions} from "../../expedient/details/CommonActions.tsx";
import {useMoure, useCopiar, useVincular} from "../actions/Moure.tsx";
import useNotificar from "../actions/Notificar.tsx";
import usePublicar from "../actions/Publicar.tsx";
import {useEnviarPortafirmes} from "../actions/EnviarPortafirmes.tsx";
import useVisualitzar from "../actions/Visualitzar.tsx";
import useEnviarViaEmail from "../actions/EnviarViaEmail.tsx";
import useSeguimentPortafirmes from "../actions/SeguimentPortafirmes.tsx";
import useSeguimentViafirma from "../actions/SeguimentViafirma.tsx";
import useFirmaNavegador from "../actions/FirmaNavegador.tsx";
import useDocPinbal from "../actions/DocPinbal.tsx";
import useEnviarViaFirma from "../actions/EnviarViaFirma.tsx";
import useCrearCarpeta from "../../carpeta/actions/Crear.tsx";
import useImportar from "../actions/Importar.tsx";
import useCarpetaActions from "../../carpeta/details/CarpetaActions.tsx";
import useImportarExpedient from "../../expedient/actions/ImportarExpedient.tsx";
import useImportarZip from "../actions/ImportarZip.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
        artifactReport: apiReport,
        fieldDownload: apiDownload,
        delete: apiDelete
    } = useResourceApiService('documentResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const action = (id:any, code:string, mssg:string) => {
        apiAction(id, {code})
            .then(()=>{
                refresh?.()
                temporalMessageShow(null, mssg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

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
        apiAction(undefined, {code: 'GET_CSV_LINK', data: {ids: [id], massivo: false}})
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
                    apiAction(undefined, {code: 'CONVERTIR_DEFINITIU', data: {ids: [id], massivo: false}})
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

    const eliminar= (id:any, row:any) :void => {
        messageDialogShow(
            t('page.document.action.delete.check'),
            t('page.document.action.delete.description'),
            [{
                value: true,
                text: t('common.accepta'),
                componentProps: { variant: 'contained' }
            },
            {
                value: false,
                text: t('common.cancel'),
                componentProps: { variant: 'outlined' }
            }],
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    apiDelete(id)
                        .then(() => {
                            refresh?.();
                            temporalMessageShow(null, t('page.document.action.delete.ok', {data: row}), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }

    const guardarArxiu = (id:any, row:any) => {
        apiAction(undefined, {code: 'GUARDAR_ARXIU', data: {ids: [id], massivo: false}})
            .then(()=>{
                refresh?.()
                temporalMessageShow(null, t('page.contingut.action.guardarArxiu.ok', {contingut: row?.nom}), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        apiDownload: downloadAdjunt,
        getLinkCSV: enllacCSV,
        descarregarVersio,
        definitiu,
        guardarArxiu,
        eliminar,
    }
}

export const useContingutActions = (entity:any, apiRef:MuiDataGridApiRef, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession()

    const {handleShow: handleDocPinbal, content: contentDocPinbal} = useDocPinbal(entity, refresh)
    const {handleShow: handleCrearCarpeta, content: contentCrearCarpeta} = useCrearCarpeta(entity, refresh)
    const {handleShow: handleImportar, content: contentImportar} = useImportar(entity, refresh)
    const {handleOpen: handleImportarExpedient, dialog: dialogImportarExpedient} = useImportarExpedient(entity, refresh)
	const {handleShow: handleImportarZip, content: contentImportarZip} = useImportarZip(entity, refresh)

    const {eliminar, apiDownload, getLinkCSV, definitiu, guardarArxiu} = useActions(refresh)
    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = useDocumentDetail(entity, refresh);
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleOpen: handleVisualitzarOpen, dialog: dialogVisualitzar, isValid} = useVisualitzar();
    const {handleOpen: handleSeguimentOpen, dialog: dialogSeguiment} = useSeguimentPortafirmes(entity?.potModificarContingut, refresh);
    const {handleOpen: handleSeguimentVfOpen, dialog: dialogSeguimentVf} = useSeguimentViafirma(entity?.potModificarContingut, refresh);
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

    const createDocumentActions = [
        {
            label: t('page.document.title')+"...",
            icon: "description",
            onClick: () => apiRef?.current?.showCreateDialog?.(),
            hidden: !(entity?.potModificarContingut || entity?.potModificar),
        },
        {
            label: t('page.document.action.pinbal.label'),
            icon: "description",
            onClick: handleDocPinbal,
            disabled: !entity?.ambDocumentsPinbal,
			hidden: !entity?.potModificar,
        },
        {
            label: t('page.carpeta.action.new.label'),
            icon: "folder",
            onClick: handleCrearCarpeta,
            disabled: !user?.sessionScope?.isCreacioCarpetesActiva,
			hidden: !entity?.potModificar,
        },
        {
            label: t('page.document.action.import.label'),
            icon: "upload_file",
            onClick: handleImportar,
            hidden: !(user?.sessionScope?.isMostrarImportacio && entity?.potModificar),
        },
		{
		    label: t('page.document.action.importZip.label'),
		    icon: "upload_file",
		    onClick: handleImportarZip,
		},
        {
            label: t('page.contingut.action.importarExpedient.label'),
            icon: "link",
            onClick: handleImportarExpedient,
            hidden: !(user?.sessionScope?.isImportacioRelacionatsActiva && entity?.potModificar),
        },
    ];

    const documentActions = [
        {
            label: t('page.contingut.action.guardarArxiu.label'),
            icon: 'autorenew',
            showInMenu: true,
            onClick: guardarArxiu,
            disabled: !entity?.arxiuUuid,
            hidden: (row:any) => (row?.arxiuUuid && !row?.gesDocFirmatId) || !entity?.potModificar,
        },
        {
            label: t('page.document.action.detall.label'),
            icon: "folder",
            showInMenu: true,
            onClick: handleDetallOpen,
        },
        {
            label: t('common.update')+'...',
            icon: 'edit',
            showInMenu: true,
            clickShowUpdateDialog: true,
            disabled: (row:any) => row?.arxiuUuid == null || row?.gesDocFirmatId != null,
            hidden: (row:any) => !entity?.potModificar || (isInOptions(row?.arxiuEstat, 'DEFINITIU') && !isPermesModificarCustodiatsVar(row)) ||  isInOptions(row?.estat, 'FIRMA_PENDENT'),
        },
        {
            label: t('page.contingut.action.move.label'),
            icon: "open_with",
            showInMenu: true,
            onClick: handleMoureShow,
            disabled: (row:any) => row?.gesDocAdjuntId!=null,
            hidden: !(entity?.potModificar),
        },
        {
            label: t('page.contingut.action.copy.label'),
            icon: "file_copy",
            showInMenu: true,
            onClick: handleCopiarShow,
            hidden: !entity?.potModificar || !user?.sessionScope?.isMostrarCopiar,
        },
        {
            label: t('page.contingut.action.vincular.label'),
            icon: "link",
            showInMenu: true,
            onClick: handleVincularShow,
            hidden: !entity?.potModificar || !user?.sessionScope?.isMostrarVincular,
        },
        {
            label: t('page.document.action.delete.label'),
            icon: "delete",
            showInMenu: true,
            onClick: eliminar,
            hidden: (row:any) => !(entity?.potModificarContingut || entity?.potModificar) || !isDocument(row) || (row?.arxiuEstat == 'DEFINITIU' && !user?.sessionScope?.permesEsborrarFinals)
        },
        {
            label: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            label: t('page.document.action.descarregarOriginal.label'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'adjunt', t('page.document.action.descarregarOriginal.ok')),
            hidden: (row:any) => !isDigitalOrImportat(row),
        },
        {
            label: t('page.document.action.imprimible.label'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'imprimible', t('page.document.action.imprimible.ok')),
            disabled: (row:any) => isInOptions(row?.fitxerExtension, 'xsig'),
            hidden: (row:any) => !isDigitalOrImportat(row) || !( (row?.arxiuEstat=='DEFINITIU' || row?.estat=='FIRMA_PARCIAL') || user?.sessionScope?.imprimibleNoFirmats),
        },
        {
            label: t('page.document.action.original.label'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'original', t('page.document.action.original.label.ok')),
            hidden: (row:any) => !isDigitalOrImportat(row) || !row?.gesDocOriginalId
        },
        {
            label: t('page.document.action.firma.label'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'firmaAdjunt', t('page.document.action.download.ok')),
            hidden: (row:any) => !isDigitalOrImportat(row) || row?.ntiTipoFirma != "TF04"
        },
        {
            label: t('page.document.action.view.label'),
            title: t('page.document.alert.view'),
            icon: "search",
            showInMenu: true,
            onClick: handleVisualitzarOpen,
            disabled: (row:any) => !isValid(row),
            hidden: (row:any) => !isDigitalOrImportat(row),
        },
        {
            label: t('page.document.action.csv.label'),
            icon: "file_copy",
            showInMenu: true,
            onClick: getLinkCSV,
            hidden: (row:any) => !isInOptions(row?.estat, 'DEFINITIU', 'CUSTODIAT') || !user?.sessionScope?.isUrlValidacioDefinida,
        },
        {
            label: t('page.document.action.portafirmes.label'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEviarPortafirmesShow,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden : (row:any) => !(entity?.potModificarContingut || entity?.potModificar) || !row?.metaDocumentInfo?.firmaPortafirmesActiva || !isFirmaActiva(row),
        },
        {
            label: t('page.document.action.firmar.label'),
            icon: "edit_document",
            showInMenu: true,
            onClick: handleFirmaShow,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden: (row:any) => !(entity?.potModificarContingut || entity?.potModificar) || !row?.metaDocumentInfo?.firmaPassarelaActiva || !isFirmaActiva(row),
        },
        {
            label: t('page.document.action.viaFirma.label'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEnviarViaFirma,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden: (row:any) => !(entity?.potModificarContingut || entity?.potModificar) || !row?.metaDocumentInfo?.firmaBiometricaActiva || !isFirmaActiva(row),
        },
        {
            label: entity?.metaExpedientInfo?.tipusClassificacio == 'SIA' // notificar/comunicar
                ? t('page.document.action.notificar.label')
                : t('page.document.action.comunicar.label'),
            icon: "mail",
            showInMenu: true,
            onClick: handleNotificarShow,
            hidden: (row:any) => !entity?.potModificar || !(row?.documentFirmaTipus != 'SENSE_FIRMA' && row?.arxiuUuid || isInOptions(row?.fitxerExtension, 'zip')),
        },
        {
            label: t('page.document.action.publicar.label'),
            icon: "publish",
            showInMenu: true,
            onClick: handlePublicarShow,
            hidden: (row:any) => !entity?.potModificar || !(row?.documentFirmaTipus != 'SENSE_FIRMA' && row?.arxiuUuid || isInOptions(row?.fitxerExtension, 'zip')) || !user?.sessionScope?.isMostrarPublicar
        },
        {
            label: t('page.document.action.mail.label'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEnviarViaEmailShow,
        },
        {
            label: t('page.document.action.seguiment.label'),
            icon: "info",
            showInMenu: true,
            onClick: handleSeguimentOpen,
            hidden: (row:any) => !(row?.estat == 'FIRMA_PENDENT' && row?.documentTipus == 'DIGITAL'),
        },
        {
            label: t('page.contingut.action.seguimentvf.label'),
            icon: "info",
            showInMenu: true,
            onClick: handleSeguimentVfOpen,
            hidden: (row:any) => !(row?.estat == 'FIRMA_PENDENT_VIAFIRMA' && row?.documentTipus == 'DIGITAL'),
        },
        {
            label: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            label: t('page.document.action.definitive.label'),
            icon: "check_circle",
            showInMenu: true,
            onClick: definitiu,
            hidden: (row:any) => !(entity?.potModificarContingut || entity?.potModificar) || !isInOptions(row?.estat, 'REDACCIO' , 'FIRMA_PARCIAL') || !isInOptions(row?.documentTipus, 'DIGITAL') || !user?.sessionScope?.isConvertirDefinitiuActiu,
        },
        {
            label: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row:any) => !entity?.potModificar || !isInOptions(row?.estat, 'REDACCIO' , 'FIRMA_PARCIAL') || !isInOptions(row?.documentTipus, 'DIGITAL') || !user?.sessionScope?.isConvertirDefinitiuActiu,
        },
        {
            label: t('page.contingut.action.history.label'),
            icon: "list",
            showInMenu: true,
            onClick: handleHistoricOpen,
        },
        {
            label: t('page.contingut.action.infoArxiu.label'),
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
        {componentsActions}
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
        {dialogSeguimentVf}
        {contentFirma}
        {contentDocPinbal}
        {contentEnviarViaFirma}
        {contentCrearCarpeta}
        {contentImportar}
		{contentImportarZip}
        {dialogImportarExpedient}
    </>;
    return {
        createActions: createDocumentActions,
        actions: [
            ...documentActions,
            ...carpetaActions,
            ...expedientActions,
        ],
        components
    }
}