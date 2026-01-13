import {useTranslation} from "react-i18next";
import { useUserSession } from "../../../components/Session.tsx";
import {Divider} from "@mui/material";
import useHistoric from "../../Historic.tsx";
import useModificar from "../actions/Modificar.tsx";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";
import {useCopiar, useMoure} from "../actions/Moure.tsx";

const useActions = (refresh?:()=>void) => {

    const { t } = useTranslation();
    const { artifactReport: apiReport, delete: apiDelete, artifactAction: apiAction, } = useResourceApiService('carpetaResource');
    const { messageDialogShow, temporalMessageShow } = useBaseAppContext();
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

    const report = (id:any, code:string, msg:string, fileType:any) => {
        apiReport(id, {code: code, fileType})
            .then((result) => {
                iniciaDescargaBlob(result);
                temporalMessageShow(null, msg, 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const eliminar= (id:any, row:any) :void => {
        messageDialogShow(
            t('page.carpeta.action.delete.check'),
            t('page.carpeta.action.delete.description'),
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
                            temporalMessageShow(null, t('page.carpeta.action.delete.ok', {data: row}), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }

    const exportarPDF = (id:any) => report(id, 'EXPORTAR_INDEX_PDF', t('page.expedient.action.exportPDF.ok'),'PDF')
    const exportarEXCEL = (id:any) => report(id, 'EXPORTAR_INDEX_XLS', t('page.expedient.action.exportEXCEL.ok'),'XLSX')
    const guardarArxiu = (id:any, row:any) => action(id, 'GUARDAR_ARXIU', t('page.contingut.action.guardarArxiu.ok', {contingut: row?.nom}))

    return {
        eliminar,
        exportarPDF,
        guardarArxiu,
        exportarEXCEL,
    }
}

const useCarpetaActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession()

    const { eliminar, exportarPDF, exportarEXCEL, guardarArxiu } = useActions(refresh)
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleShow: handleModifyCarpeta, content: contentModifyCarpeta} = useModificar(entity, refresh)
    const {handleShow: handleMoure, content: contentMoure} = useMoure(refresh)
    const {handleShow: handleCopiar, content: contentCopiar} = useCopiar(refresh)

	const isUsuariAmbPermis = (row: any) =>
	    row?.restriccions?.some(
	        (restriccio: any) => restriccio?.id === user?.codi
	    ) ?? false;
	const isResponsableRestriccio = (row: any) => row?.responsableRestriccio?.id === user?.codi;
	const isRolActualAdmin = user?.rolActual === 'IPA_ADMIN';
	
	const potGestionarCarpeta = (row: any) =>
	    !row?.restringida || 
		(row?.restringida && (
	    	isResponsableRestriccio(row) ||
	    	isRolActualAdmin ||
	    	isUsuariAmbPermis(row)));
			
    const actions = [
        {
            label: t('page.contingut.action.guardarArxiu.label'),
            icon: 'autorenew',
            showInMenu: true,
            onClick: guardarArxiu,
            hidden: (row:any) => row?.arxiuUuid || user?.sessionScope?.isCreacioCarpetesLogica
        }, 
        {
            label: t('page.carpeta.action.update.label'),
            icon: 'edit',
            showInMenu: true,
            onClick: handleModifyCarpeta,
            hidden: (row:any) => !entity?.potModificar || !user?.sessionScope?.isCreacioCarpetesActiva || !user?.sessionScope?.isCreacioCarpetesLogica || !potGestionarCarpeta(row),
        },
        {
            label: t('page.expedient.action.exportPDF.label'),
            icon: 'format_list_numbered',
            showInMenu: true,
            onClick: exportarPDF,
            hidden: (row:any) => !entity?.potModificar || !user?.sessionScope?.isCreacioCarpetesActiva || !row?.hasDocumentsFills || !potGestionarCarpeta(row),
        },
        {
            label: t('page.expedient.action.exportEXCEL.label'),
            icon: 'lists',
            showInMenu: true,
            onClick: exportarEXCEL,
            hidden: (row:any) => !entity?.potModificar || !user?.sessionScope?.isCreacioCarpetesActiva || !user?.sessionScope?.isExportacioExcelActiva || !row?.hasDocumentsFills || !potGestionarCarpeta(row),
        },
        {
            label: t('page.contingut.action.move.label'),
            icon: "open_with",
            showInMenu: true,
            onClick: handleMoure,
            hidden: (row:any) => !entity?.potModificar || !user?.sessionScope?.isCreacioCarpetesActiva || !potGestionarCarpeta(row),
        },
        /*{
            label: t('page.contingut.action.copy.label'),
            icon: "file_copy",
            showInMenu: true,
            onClick: handleCopiar,
            hidden: !entity?.potModificar || !user?.sessionScope?.isCreacioCarpetesActiva || !user?.sessionScope?.isMostrarCopiar,
        },*/
        {
            label: t('page.carpeta.action.delete.label'),
            icon: "delete",
            showInMenu: true,
            onClick: eliminar,
            hidden: (row:any) => !entity?.potModificar || !user?.sessionScope?.isCreacioCarpetesActiva || !potGestionarCarpeta(row),
        },
        {
            label: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row:any) => !entity?.potModificar || !user?.sessionScope?.isCreacioCarpetesActiva || !potGestionarCarpeta(row),
        },
        {
            label: t('page.contingut.action.history.label'),
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