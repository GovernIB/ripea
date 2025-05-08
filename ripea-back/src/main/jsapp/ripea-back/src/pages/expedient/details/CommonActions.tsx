import {
    useResourceApiService,
    useBaseAppContext,
} from 'reactlib';
import useAssignar from "../actions/Assignar.tsx";
import useCambiarEstat from "../actions/CambiarEstat.tsx";
import useCambiarPrioritat from "../actions/CambiarPrioritat.tsx";
import {useTranslation} from "react-i18next";
import useRelacionar from "../actions/Relacionar.tsx";
import useInformacioArxiu from "../../InformacioArxiu.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {Divider} from "@mui/material";
import useExportarDocuments from "../actions/ExportarDocuments.tsx";
import useHistoric from "../../Historic.tsx";

const iniciaDescargaBlob = (result: any) => {
    const url = URL.createObjectURL(result.blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = result.fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link); // Limpieza
    URL.revokeObjectURL(url);
}

export const useActions = (refresh?: () => void) => {
	
    const {temporalMessageShow} = useBaseAppContext();
	const { t } = useTranslation();
    const {
        artifactAction: apiAction,
		artifactReport: apiReport,
    } = useResourceApiService('expedientResource');
	
    const action = (id:any, code:string, msg:string) => {
        return apiAction(undefined, {code :code, data:{ ids: [id], massivo: false }})
			.then(() => {
			    refresh?.()
			    temporalMessageShow(null, msg, 'success');
			})
			.catch((error) => {
			    temporalMessageShow('Error', error?.message, 'error');
			});		
    }
	
	const massiveReport = (id:any, code:string, msg:string, fileType:any) => {
	    return apiReport(undefined, {code :code, data:{ ids: [id], masivo: false }, fileType})
			.then((result) => {
				iniciaDescargaBlob(result);
                temporalMessageShow(null, msg, 'info');
			})
			.catch((error) => {
			    temporalMessageShow('Error', error?.message, 'error');
			});		
	}

    const follow	= (id: any): void => { action(id, 'FOLLOW', 	t('page.expedient.results.actionOk')); }
    const unfollow	= (id: any): void => { action(id, 'UNFOLLOW',	t('page.expedient.results.actionOk')); }
    const agafar	= (id: any): void => { action(id, 'AGAFAR',		t('page.expedient.results.actionOk')); }
    const retornar	= (id: any) :void => { action(id, 'RETORNAR',	t('page.expedient.results.actionOk')); }
	const alliberar	= (id: any) :void => { action(id, 'ALLIBERAR', 	t('page.expedient.results.actionOk')); }
	const eliminar	= (id: any) :void => { action(id, 'ESBORRAR',	t('page.expedient.results.actionOk')); }
	
	const exportIndexPdf= (id: any): void => { massiveReport(id, 'EXPORT_INDEX_PDF', t('page.expedient.results.actionBackgroundOk'), 'PDF');}
	const exportIndexXls= (id: any): void => { massiveReport(id, 'EXPORT_INDEX_XLS', t('page.expedient.results.actionBackgroundOk'), 'XLSX');}
	const exportPdfEni	= (id: any): void => { massiveReport(id, 'EXPORT_INDEX_ENI', t('page.expedient.results.actionBackgroundOk'), 'ZIP');}
	const exportEni		= (id: any): void => { massiveReport(id, 'EXPORT_ENI', 		 t('page.expedient.results.actionBackgroundOk'), 'ZIP');}
	const exportInside  = (id: any): void => { massiveReport(id, 'EXPORT_INSIDE', 	 t('page.expedient.results.actionBackgroundOk'), 'ZIP');}

    return {follow, unfollow, agafar, retornar, alliberar, eliminar, exportIndexPdf, exportIndexXls, exportPdfEni, exportEni, exportInside}
}

export const useCommonActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user, permisos } = useUserSession();
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';

    const {follow, unfollow, agafar, retornar, alliberar, eliminar, exportIndexPdf, exportIndexXls, exportPdfEni, exportEni, exportInside} = useActions(refresh);
    const {handleOpen: handelHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleOpen: handleArxiuOpen, dialog: arxiuDialog} = useInformacioArxiu('expedientResource', 'ARXIU_EXPEDIENT');
    const {handleShow: hanldeAssignar, content: assignarContent} = useAssignar(refresh);
    const {handleShow: hanldeCambiarEstado, content: cambiarEstadoContent} = useCambiarEstat(refresh);
    const {handleShow: hanldeCambiarPrioridad, content: cambiarPrioridadContent} = useCambiarPrioritat(refresh);
    const {handleShow: hanldeRelacionar, content: cambiarRelacionar} = useRelacionar(refresh);
    const {handleShow: handleExportDoc, content: contentExportDoc} = useExportarDocuments(refresh);

    const isTancat = (row:any) :boolean => {
        return row?.estat != "OBERT"
    }
    const isAgafatUsuariActual = (row:any) :boolean => {
        return row?.agafatPer?.id == user?.codi
    }
    const isUsuariActualWrite = (row:any) :boolean => {
        return row?.usuariActualWrite
    }
    const isAdminOAdminOrgan = (row:any) :boolean => {
        return (isRolActualAdmin && permisos?.permisAdministrador) || ( isRolActualOrganAdmin && permisos?.organs?.some((e:any)=>e.id == row?.organGestor?.id) )
    }
    const potModificar = (row:any) :boolean => {
        return (isUsuariActualWrite(row) && isAgafatUsuariActual(row)) || (isAdminOAdminOrgan(row) && !isTancat(row));
    }

    const actions = [
        {
            title: t('page.expedient.acciones.detall'),
            icon: "folder",
            linkTo: "/contingut/{{id}}",
            showInMenu: true,
        },
        {
            title: t('common.update'),
            icon: 'edit',
            showInMenu: true,
            hidden: isTancat,
            clickShowUpdateDialog: true,
        },
        {
            title: t('page.expedient.acciones.follow'),
            icon: "person_add",
            showInMenu: true,
            onClick: follow,
            hidden: (row:any) => row?.seguidor || !isUsuariActualWrite(row),// si el usuario actual es seguidor
        },
        {
            title: t('page.expedient.acciones.unfollow'),
            icon: "person_remove",
            showInMenu: true,
            onClick: unfollow,
            hidden: (row:any) => !row?.seguidor || !isUsuariActualWrite(row),// si el usuario actual no es seguidor
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
        },
        {
            title: t('page.expedient.acciones.assignar'),
            icon: "person",
            showInMenu: true,
            onClick: hanldeAssignar,
            hidden: (row:any) => !isAdminOAdminOrgan(row),// si el usuario actual no admin o organo
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
            hidden: (row:any) => !isAdminOAdminOrgan(row),
        },
        {
            title: t('page.expedient.acciones.agafar'),
            icon: "lock",
            showInMenu: true,
            onClick: agafar,
            hidden: isAgafatUsuariActual
        },
        {
            title: t('page.expedient.acciones.retornar'),
            icon: "undo",
            showInMenu: true,
            onClick: retornar,
            hidden: (row:any) => !isAgafatUsuariActual(row) || row?.agafatPer?.id == row?.createdBy,
        },
        {
            title: t('page.expedient.acciones.lliberar'),
            icon: "lock_open",
            showInMenu: true,
            onClick: alliberar,
            hidden: (row:any) => !row?.agafatPer,
        },
        {
            title: t('page.expedient.acciones.upPrioritat'),
            icon: "logout",
            showInMenu: true,
            onClick: hanldeCambiarPrioridad,
            hidden: (row:any) => !potModificar(row),
        },
        {
            title: t('page.expedient.acciones.upEstat'),
            icon: "logout",
            showInMenu: true,
            onClick: hanldeCambiarEstado,
            hidden: (row:any) => isTancat(row) || !potModificar(row),
        },
        {
            title: t('page.expedient.acciones.relacio'),
            icon: "link",
            showInMenu: true,
            onClick: hanldeRelacionar,
            hidden: (row:any) => !potModificar(row),
        },
        {
            title: t('page.expedient.acciones.close'),
            icon: "check",
            showInMenu: true,
            // onClick: ,
            disabled: (row:any) => !row?.potTancar,
            hidden: (row:any) => !potModificar(row) || isTancat(row),
        },
        {
            title: t('page.expedient.acciones.open'),
            icon: "undo",
            showInMenu: true,
            // onClick: ,
            hidden: (row:any) => !isTancat(row) || !user?.sessionScope?.isReobrirPermes || !( !user?.sessionScope?.isTancamentLogicActiu || row?.tancatData),
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
        },
        {
            title: t('page.expedient.acciones.history'),
            icon: "list",
            showInMenu: true,
            onClick: handelHistoricOpen,
        },
        {
            title: t('page.expedient.acciones.download'),
            icon: "download",
            showInMenu: true,
            // onClick: ,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportPDF'),
            icon: "format_list_numbered",
            showInMenu: true,
			onClick: exportIndexPdf,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportEXCEL'),
            icon: "lists",
            showInMenu: true,
			onClick: exportIndexXls,
            hidden: (row:any) => !(row?.conteDocuments && user?.sessionScope?.isExportacioExcelActiva),
        },
        {
            title: t('page.expedient.acciones.exportPDF_ENI'),
            icon: "format_list_numbered",
            showInMenu: true,
			onClick: exportPdfEni,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportENI'),
            icon: "folder_code",
            showInMenu: true,
			onClick: exportEni,
			disabled: (row:any) => !row?.conteDocumentsDefinitius,
			hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportINSIDE'),
            icon: "folder_zip",
            showInMenu: true,
			onClick: exportInside,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !(row?.conteDocuments && user?.sessionScope?.isExportacioInsideActiva),
        },
        {
            title: "Exportar los documentos...",
            icon: "description",
            showInMenu: true,
			onClick: handleExportDoc,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.infoArxiu'),
            icon: "info",
            showInMenu: true,
            onClick: handleArxiuOpen,
            disabled: (row:any) => !row?.arxiuUuid,
        },
        {
            title: t('page.expedient.acciones.sincronitzar'),
            icon: "autorenew",
            showInMenu: true,
            // onClick: ,
        },
		{
		    title: t('page.expedient.acciones.eliminar'),
		    icon: "delete",
		    showInMenu: true,
		    onClick: eliminar,
		    hidden: (row:any) => !potModificar(row),
		},	
    ]
        .map(({ hidden, ...rest }) => ({
            ...rest,
            hidden: (row: any) => (typeof hidden === 'function' ? hidden(row) : !!hidden) || row?.tipus!='EXPEDIENT'
        }));

    const components = <>
        {dialogHistoric}
        {cambiarPrioridadContent}
        {cambiarEstadoContent}
        {arxiuDialog}
        {assignarContent}
        {cambiarRelacionar}
        {contentExportDoc}
    </>;

    return {
        actions,
        components
    }
}