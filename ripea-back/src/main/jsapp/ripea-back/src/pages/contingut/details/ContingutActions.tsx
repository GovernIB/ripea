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
import {useEntitatSession, useUserSession} from "../../../components/Session.tsx";
import {Divider} from "@mui/material";
import {useCommonActions} from "../../expedient/details/CommonActions.tsx";

export const useActions = (refresh?: () => void) => {
    const {
        fieldDownload: apiDownload,
    } = useResourceApiService('documentResource');

    const downloadAdjunt = (id:any,fieldName:string) :void => {
        apiDownload(id,{fieldName})
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

export const useContingutActions = (expedient:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user, permisos } = useUserSession()
    const { value: entitat } = useEntitatSession()
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';
    const {apiDownload} = useActions(refresh)
    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = useDocumentDetail();
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleOpen: arxiuhandleOpen, dialog: arxiuDialog} = useInformacioArxiu('documentResource', 'ARXIU_DOCUMENT');
    const {handleShow: handleMoureShow, content: contentMoure} = useMoure(refresh);
    const {handleShow: handleEnviarViaEmailShow, content: contentEnviarViaEmail} = useEnviarViaEmail(refresh);
    const {handleShow: handleNotificarShow, content: contentNotificar} = useNotificar(refresh);
    const {handleShow: handlePublicarShow, content: contentPublicar} = usePublicar(refresh);
    const {handleShow: handleEviarPortafirmesShow, content: contentEviarPortafirmes} = useEviarPortafirmes(refresh);

    const permesModificarCustodiats= () => entitat?.isPermesModificarCustodiats;

    const isDocument= (row:any) => row?.tipus=="DOCUMENT";
    const isDigitalOrImportat = (row:any) => {
        return isInOptions(row.documentTipus, 'DIGITAL', 'IMPORTAT');
    };

    const isFirmaActiva = (row:any) => {
        return isInOptions(row?.estat, 'REDACCIO', 'FIRMA_PARCIAL') && isDigitalOrImportat(row) && !isInOptions(row?.fitxerExtension, 'zip')
    }

    // Expedient
    const isTancat= () => expedient?.estat != "OBERT"
    const isAgafatUsuariActual = () => expedient?.agafatPer?.id == user?.codi
    const isAdminOAdminOrgan = () => (isRolActualAdmin && permisos?.permisAdministrador) || ( isRolActualOrganAdmin && permisos?.organs?.some((e:any)=>e.id == expedient?.organGestor?.id) )
    const isUsuariActualWrite = () => expedient?.usuariActualWrite
    const potModificar = () => (isAgafatUsuariActual() && isUsuariActualWrite() || isAdminOAdminOrgan()) && !isTancat();
    // //

    const isInOptions = (value:string, ...options:string[]) => {
        return options.includes(value)
    }

    const documentActions = [
        {
            title: t('page.document.acciones.detall'),
            icon: "folder",
            showInMenu: true,
            onClick: handleDetallOpen,
        },
        {
            title: t('page.document.acciones.move'),
            icon: "open_with",
            showInMenu: true,
            onClick: handleMoureShow,
            disabled: (row:any) => row?.gesDocAdjuntId!=null,
            hidden: !potModificar(),
        },
        {
            title: t('common.copy'),
            icon: "file_copy",
            showInMenu: true,
            hidden: !potModificar() || !user?.sessionScope?.isMostrarCopiar,
        },
        {
            title: t('page.document.acciones.vincular'),
            icon: "link",
            showInMenu: true,
            hidden: !potModificar() || !user?.sessionScope?.isMostrarVincular,
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
        },
        {
            title: t('page.document.acciones.imprimible'),
            icon: "download",
			onClick: (id:any) => apiDownload(id, 'imprimible'),
            showInMenu: true,
            hidden: (row:any) => !isDigitalOrImportat(row) || isInOptions(row?.estat, 'DEFINITIU', 'FIRMA_PARCIAL') || isInOptions(row?.fitxerExtension, 'xsig') || user?.sessionScope?.imprimibleNoFirmats,
        },
        {
            title: t('common.download'),
            icon: "download",
			onClick: (id:any) => apiDownload(id, 'adjunt'),
            showInMenu: true,
            hidden: (row:any) => !isDigitalOrImportat(row),
        },
        {
            title: t('page.document.acciones.original'),
            icon: "download",
			onClick: (id:any) => apiDownload(id, 'original'),
            showInMenu: true,
            hidden: (row:any) => !isDigitalOrImportat(row) || !row?.gesDocOriginalId
        },
        {
            title: t('page.document.acciones.firma'),
            icon: "download",
			onClick: (id:any) => apiDownload(id, 'firmaAdjunt'),
            showInMenu: true,
            hidden: (row:any) => !isDigitalOrImportat(row) || row?.ntiTipoFirma != "TF04"
        },
        {
            title: t('page.document.acciones.view'),
            icon: "search",
            showInMenu: true,
            disabled: (row:any) => !isInOptions(row?.fitxerExtension, 'pdf', 'odt', 'docx'),
            hidden: (row:any) => !isDigitalOrImportat(row),
        },
        {
            title: t('page.document.acciones.csv'),
            icon: "file_copy",
            showInMenu: true,
            hidden: (row:any) => !isInOptions(row?.estat, 'DEFINITIU', 'CUSTODIAT') || !user?.sessionScope?.isUrlValidacioDefinida,
        },
        {
            title: t('page.document.acciones.portafirmes'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEviarPortafirmesShow,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden : (row:any) => !potModificar() || !row?.metaNode?.firmaPortafirmesActiva || !isFirmaActiva(row),
        },
        {
            title: t('page.document.acciones.firmar'),
            icon: "edit_document",
            showInMenu: true,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden: (row:any) => !potModificar() || !row?.metaNode?.firmaPassarelaActiva || !isFirmaActiva(row),
        },
        {
            title: t('page.document.acciones.viaFirma'),
            icon: "edit_document",
            showInMenu: true,
            disabled: (row:any) => !row?.valid || row?.gesDocAdjuntId!=null,
            hidden: (row:any) => !potModificar() || !row?.metaNode?.firmaBiometricaActiva || !isFirmaActiva(row),
        },
        {
            title: expedient?.metaExpedient?.tipusClassificacio == 'SIA' // notificar/comunicar
                ? t('page.document.acciones.notificar')
                : t('page.document.acciones.comunicar'),
            icon: "mail",
            showInMenu: true,
            onClick: handleNotificarShow,
            hidden: (row:any) => !potModificar() || !(row?.documentFirmaTipus != 'SENSE_FIRMA' && row?.arxiuUuid || isInOptions(row?.fitxerExtension, 'zip')),
        },
        {
            title: t('page.document.acciones.publicar'),
            icon: "publish",
            showInMenu: true,
            onClick: handlePublicarShow,
            hidden: (row:any) => !potModificar() || !(row?.documentFirmaTipus != 'SENSE_FIRMA' && row?.arxiuUuid || isInOptions(row?.fitxerExtension, 'zip')) || !user?.sessionScope?.isMostrarPublicar
        },
        {
            title: t('page.document.acciones.mail'),
            icon: "mail",
            showInMenu: true,
            onClick: handleEnviarViaEmailShow,
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
        },
        {
            title: t('page.document.acciones.seguiment'),
            icon: "info",
            showInMenu: true,
            hidden: (row:any) => !(row?.estat == 'FIRMA_PENDENT' && row?.documentTipus == 'DIGITAL'),
        },
        {
            title: t('page.document.acciones.history'),
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
        {
            title: t('page.document.acciones.export'),
            icon: "download",
            showInMenu: true,
            disabled: true,
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
        {arxiuDialog}
        {contentMoure}
        {contentEnviarViaEmail}
        {contentNotificar}
        {contentPublicar}
        {contentEviarPortafirmes}
    </>;
    return {
        actions: [
            ...documentActions,
            ...expedientActions
        ],
        hiddenUpdate: (row:any) => !potModificar() || !isDocument(row) || !permesModificarCustodiats || (row?.arxiuUuid==null || row?.gesDocFirmatId != null) || !isInOptions(row?.estat, 'CUSTODIAT', 'FIRMAT', 'FIRMA_PARCIAL', 'DEFINITIU'),
        hiddenDelete: (row:any) => !potModificar() || !isDocument(row),
        components
    }
}