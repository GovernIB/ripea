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

const useActions = (refresh?: () => void) =>{
    const {temporalMessageShow} = useBaseAppContext();
debugger;
    const {
        patch: apiPatch,
        artifactAction: apiAction,
		fieldDownload: apiDownload,
    } = useResourceApiService('expedientResource');
	debugger;
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
    const follow = (id: any): void => {
        apiAction(id, {code : 'FOLLOW'})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }
    const unfollow = (id: any): void => {
        apiAction(id, {code : 'UNFOLLOW'})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }
    const agafar = (id: any): void => {
        apiAction(id, {code : 'AGAFAR'})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }
    const retornar = (id: any) :void => {
        apiAction(id, {code : 'RETORNAR'})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }
    const lliberar = (id: any): void => {
        apiPatch(id, {
            data: {agafatPer: null,}
        })
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }

    return {follow, unfollow, agafar, retornar, lliberar, apiDownload: downloadAdjunt}
}

export const useCommonActions = (refresh?: () => void) => {
	
    const { t } = useTranslation();
    const { value: user, permisos } = useUserSession();
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';
    const {follow, unfollow, agafar, retornar, lliberar, apiDownload} = useActions(refresh);
    const {handleOpen: handleArxiuOpen, dialog: arxiuDialog} = useInformacioArxiu('expedientResource', 'ARXIU_EXPEDIENT');
    const {handleShow: hanldeAssignar, content: assignarContent} = useAssignar(refresh);
    const {handleShow: hanldeCambiarEstado, content: cambiarEstadoContent} = useCambiarEstat(refresh);
    const {handleShow: hanldeCambiarPrioridad, content: cambiarPrioridadContent} = useCambiarPrioritat(refresh);
    const {handleShow: hanldeRelacionar, content: cambiarRelacionar} = useRelacionar(refresh);

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
            onClick: lliberar,
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
            disabled: (row:any) => !row?.potTancar,
            hidden: (row:any) => !potModificar(row) || isTancat(row),
        },
        {
            title: "Reabrir",
            icon: "undo",
            showInMenu: true,
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
        },
        {
            title: t('page.expedient.acciones.download'),
            icon: "download",
            showInMenu: true,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportPDF'),
            icon: "format_list_numbered",
			onClick: (id:any) => apiDownload(id, 'exportPdf'),
            showInMenu: true,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportEXCEL'),
            icon: "lists",
			onClick: (id:any) => apiDownload(id, 'exportExcel'),
            showInMenu: true,
            hidden: (row:any) => !(row?.conteDocuments && user?.sessionScope?.isExportacioExcelActiva),
        },
        {
            title: t('page.expedient.acciones.exportPDF_ENI'),
            icon: "format_list_numbered",
			onClick: (id:any) => apiDownload(id, 'exportPdfEni'),
            showInMenu: true,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportENI'),
            icon: "folder_code",
			onClick: (id:any) => apiDownload(id, 'exportEni'),
            showInMenu: true,
			disabled: (row:any) => !row?.conteDocumentsDefinitius,
			hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportINSIDE'),
            icon: "folder_zip",
			onClick: (id:any) => apiDownload(id, 'exportInside'),
            showInMenu: true,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !(row?.conteDocuments && user?.sessionScope?.isExportacioInsideActiva),
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
        },
    ]
        .map(({ hidden, ...rest }) => ({
            ...rest,
            hidden: (row: any) => (typeof hidden === 'function' ? hidden(row) : !!hidden) || row?.tipus != 'EXPEDIENT'
        }));

    const components = <>
        {cambiarPrioridadContent}
        {cambiarEstadoContent}
        {arxiuDialog}
        {assignarContent}
        {cambiarRelacionar}
    </>;

    return {
        actions,
        hiddenDelete: (row:any) => isTancat(row),
        components
    }
}