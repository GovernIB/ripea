import {Divider} from "@mui/material";
import {MuiDataGridApiRef, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useUserSession} from "../../../components/Session.tsx";
import useDocumentDetail from "./DocumentDetail.tsx";
import useHistoric from "../../Historic.tsx";
import useInformacioArxiu from "../../InformacioArxiu.tsx";
import {iniciaDescargaBlob, useCommonActions} from "../../expedient/details/CommonActions.tsx";
import {potModificar} from "../../expedient/details/Expedient.tsx";
import useMoure, {useCopiar, useVincular} from "../actions/Moure.tsx";
import useNotificar from "../actions/Notificar.tsx";
import usePublicar from "../actions/Publicar.tsx";
import useEnviarPortafirmes from "../actions/EnviarPortafirmes.tsx";
import useVisualitzar from "../actions/Visualitzar.tsx";
import useEnviarViaEmail from "../actions/EnviarViaEmail.tsx";
import useSeguimentPortafirmes from "../actions/SeguimentPortafirmes.tsx";
import useFirmaNavegador from "../actions/FirmaNavegador.tsx";
import useDocPinbal from "../actions/DocPinbal.tsx";
import useEnviarViaFirma from "../actions/EnviarViaFirma.tsx";
import useCrearCarpeta from "../actions/CrearCarpeta.tsx";

export const useActions = () => {
    const {temporalMessageShow} = useBaseAppContext();
    const {
        artifactAction: apiAction,
        artifactReport: apiReport,
        fieldDownload: apiDownload,
    } = useResourceApiService('documentResource');

    const downloadAdjunt = (id:any,fieldName:string) :void => {
        apiDownload(id,{fieldName})
            .then((result)=>{
                iniciaDescargaBlob(result);
                temporalMessageShow(null, '', 'info');
            })
    }

    const enllacCSV = (id:any) => {
        apiAction(id, {code: 'GET_CSV_LINK'})
            .then((result) => {
                navigator.clipboard.writeText(result?.url)
                    .then(()=>{
                        temporalMessageShow(null, '', 'success');
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
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        apiDownload: downloadAdjunt,
        getLinkCSV: enllacCSV,
        descarregarVersio,
    }
}

export const useContingutActions = (entity:any, apiRef:MuiDataGridApiRef, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession()

    const {handleShow: handleDocPinbal, content: contentDocPinbal} = useDocPinbal(entity, refresh)
    const {handleShow: handleCrearCarpeta, content: contentCrearCarpeta} = useCrearCarpeta(entity, refresh)

    const {apiDownload, getLinkCSV} = useActions()
    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = useDocumentDetail();
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleOpen: handleVisualitzarOpen, dialog: dialogVisualitzar} = useVisualitzar();
    const {handleOpen: handleSeguimentOpen, dialog: dialogSeguiment} = useSeguimentPortafirmes(potModificar(entity), refresh);
    const {handleOpen: arxiuhandleOpen, dialog: arxiuDialog} = useInformacioArxiu('documentResource', 'ARXIU_DOCUMENT');
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
            title: t('page.document.acciones.pinbal'),
            icon: "description",
            onClick: handleDocPinbal,
            disabled: !entity?.ambDocumentsPinbal,
        },
        {
            title: "Carpeta...",
            icon: "folder",
            // onClick: handleCrearCarpeta,
            disabled: !user?.sessionScope?.isCreacioCarpetesActiva,
        },
        {
            title: t('page.document.acciones.import'),
            icon: "upload_file",
            // onClick: ,
            disabled: true,
        },
    ];

    const documentActions = [
        {
            title: t('page.document.acciones.detall'),
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
            hidden: (row:any) => !potMod || !isDocument(row) || (isInOptions(row?.arxiuEstat, 'DEFINITIU') && !isPermesModificarCustodiatsVar(row)) ||  isInOptions(row?.estat, 'FIRMA_PENDENT'),
        },
        {
            title: t('page.document.acciones.move'),
            icon: "open_with",
            showInMenu: true,
            onClick: handleMoureShow,
            disabled: (row:any) => row?.gesDocAdjuntId!=null,
            hidden: !potMod,
        },
        {
            title: t('page.document.acciones.copy'),
            icon: "file_copy",
            showInMenu: true,
            onClick: handleCopiarShow,
            hidden: !potMod || !user?.sessionScope?.isMostrarCopiar,
        },
        {
            title: t('page.document.acciones.vincular'),
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
            title: t('page.document.acciones.imprimible'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'imprimible'),
            disabled: (row:any) => isInOptions(row?.fitxerExtension, 'xsig'),
            hidden: (row:any) => !isDigitalOrImportat(row) || !( (row?.arxiuEstat=='DEFINITIU' || row?.estat=='FIRMA_PARCIAL') || user?.sessionScope?.imprimibleNoFirmats),
        },
        {
            title: t('common.download'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'adjunt'),
            hidden: (row:any) => !isDigitalOrImportat(row),
        },
        {
            title: t('page.document.acciones.original'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'original'),
            hidden: (row:any) => !isDigitalOrImportat(row) || !row?.gesDocOriginalId
        },
        {
            title: t('page.document.acciones.firma'),
            icon: "download",
            showInMenu: true,
			onClick: (id:any) => apiDownload(id, 'firmaAdjunt'),
            hidden: (row:any) => !isDigitalOrImportat(row) || row?.ntiTipoFirma != "TF04"
        },
        {
            title: t('page.document.acciones.view'),
            icon: "search",
            showInMenu: true,
            onClick: handleVisualitzarOpen,
            disabled: (row:any) => !isInOptions(row?.fitxerExtension, 'pdf', 'odt', 'docx'),
            hidden: (row:any) => !isDigitalOrImportat(row),
        },
        {
            title: t('page.document.acciones.csv'),
            icon: "file_copy",
            showInMenu: true,
            onClick: getLinkCSV,
            hidden: (row:any) => !isInOptions(row?.estat, 'DEFINITIU', 'CUSTODIAT') || !user?.sessionScope?.isUrlValidacioDefinida,
        },
        {
            title: t('page.document.acciones.portafirmes'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEviarPortafirmesShow,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden : (row:any) => !potMod || !row?.metaDocumentInfo?.firmaPortafirmesActiva || !isFirmaActiva(row),
        },
        {
            title: t('page.document.acciones.firmar'),
            icon: "edit_document",
            showInMenu: true,
            onClick: handleFirmaShow,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden: (row:any) => !potMod || !row?.metaDocumentInfo?.firmaPassarelaActiva || !isFirmaActiva(row),
        },
        {
            title: t('page.document.acciones.viaFirma'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEnviarViaFirma,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden: (row:any) => !potMod || !row?.metaDocumentInfo?.firmaBiometricaActiva || !isFirmaActiva(row),
        },
        {
            title: entity?.metaExpedient?.tipusClassificacio == 'SIA' // notificar/comunicar
                ? t('page.document.acciones.notificar')
                : t('page.document.acciones.comunicar'),
            icon: "mail",
            showInMenu: true,
            onClick: handleNotificarShow,
            hidden: (row:any) => !potMod || !(row?.documentFirmaTipus != 'SENSE_FIRMA' && row?.arxiuUuid || isInOptions(row?.fitxerExtension, 'zip')),
        },
        {
            title: t('page.document.acciones.publicar'),
            icon: "publish",
            showInMenu: true,
            onClick: handlePublicarShow,
            hidden: (row:any) => !potMod || !(row?.documentFirmaTipus != 'SENSE_FIRMA' && row?.arxiuUuid || isInOptions(row?.fitxerExtension, 'zip')) || !user?.sessionScope?.isMostrarPublicar
        },
        {
            title: t('page.document.acciones.mail'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEnviarViaEmailShow,
        },
        {
            title: t('page.document.acciones.seguiment'),
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
            title: t('page.contingut.acciones.history'),
            icon: "list",
            showInMenu: true,
            onClick: handleHistoricOpen,
        },
        {
            title: t('page.document.acciones.infoArxiu'),
            icon: "info",
            showInMenu: true,
            onClick: arxiuhandleOpen,
            disabled: (row:any) => !row?.arxiuUuid,
        },
    ]
        .map(({ hidden, ...rest }) => ({
            ...rest,
            hidden: (row: any) => (typeof hidden === 'function' ? hidden(row) : !!hidden) || !isDocument(row)
        }));

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
    </>;
    return {
        createActions: createDocumentActions,
        actions: [
            ...documentActions,
            ...expedientActions
        ],
        hiddenDelete: (row:any) => !potMod || !isDocument(row),
        components
    }
}