import {potModificar} from "../../expedient/details/Expedient.tsx";
import {useTranslation} from "react-i18next";
import { useUserSession } from "../../../components/Session.tsx";
import {Divider} from "@mui/material";
import useHistoric from "../../Historic.tsx";
import useModificar from "../actions/Modificar.tsx";
import {useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";
import {useCopiar, useMoure} from "../actions/Moure.tsx";

const useActions = (refresh?:()=>void) => {
    const { t } = useTranslation();

    const {
        artifactReport: apiReport,
        delete: apiDelete,
    } = useResourceApiService('carpetaResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const report = (id:any, code:string, msg:string, fileType:any) => {
        return apiReport(undefined, {code: code, data:{ ids: [id], massivo: false }, fileType})
            .then((result) => {
                iniciaDescargaBlob(result);
                temporalMessageShow(null, msg, 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const eliminar= (id:any) :void => {
        messageDialogShow(
            t('page.carpeta.action.delete.check'),
            t('page.carpeta.action.delete.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    apiDelete(id)
                        .then(() => {
                            refresh?.();
                            temporalMessageShow(null, t('page.carpeta.action.delete.ok'), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }

    const exportarPDF = (id:any) => report(id, 'EXPORTAR_INDEX_PDF', t('page.expedient.action.exportPDF.ok'),'PDF')
    const exportarEXCEL = (id:any) => report(id, 'EXPORTAR_INDEX_XLS', t('page.expedient.action.exportEXCEL.ok'),'XLS')

    return {
        eliminar,
        exportarPDF,
        exportarEXCEL,
    }
}

const useCarpetaActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession()
    const potMod = potModificar(entity)

    const { eliminar, exportarPDF, exportarEXCEL } = useActions()
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleShow: handleModifyCarpeta, content: contentModifyCarpeta} = useModificar(refresh)
    const {handleShow: handleMoure, content: contentMoure} = useMoure(refresh)
    const {handleShow: handleCopiar, content: contentCopiar} = useCopiar(refresh)

    const actions = [
        {
            title: t('page.carpeta.action.update.label'),
            icon: 'edit',
            showInMenu: true,
            onClick: handleModifyCarpeta,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva,
        },
        {
            title: t('page.expedient.action.exportPDF.label'),
            icon: 'format_list_numbered',
            showInMenu: true,
            onClick: exportarPDF,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva,// TODO: L’acció només apareix si tenen documents (no son buides).
        },
        {
            title: t('page.expedient.action.exportEXCEL.label'),
            icon: 'lists',
            showInMenu: true,
            onClick: exportarEXCEL,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva || !user?.sessionScope?.isExportacioExcelActiva,// TODO: L’acció només apareix si tenen documents (no son buides).
        },
        {
            title: t('page.contingut.action.move.label'),
            icon: "open_with",
            showInMenu: true,
            onClick: handleMoure,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva,
        },
        {
            title: t('page.contingut.action.copy.label'),
            icon: "file_copy",
            showInMenu: true,
            onClick: handleCopiar,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva || !user?.sessionScope?.isMostrarCopiar,
        },
        {
            title: t('page.carpeta.action.delete.label'),
            icon: "delete",
            showInMenu: true,
            onClick: eliminar,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva,
        },
        {
            title: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            title: t('page.contingut.action.history.label'),
            icon: "list",
            showInMenu: true,
            onClick: handleHistoricOpen,
        },
    ]
        .map(({ hidden, ...rest }) => ({
            ...rest,
            hidden: (row: any) => (typeof hidden === 'function' ? hidden(row) : !!hidden) || row?.tipus!="CARPETA"
        }));

    const components = <>
        {dialogHistoric}
        {contentModifyCarpeta}
        {contentMoure}
        {contentCopiar}
    </>

    return {
        actions,
        components,
    }
}
export default useCarpetaActions;