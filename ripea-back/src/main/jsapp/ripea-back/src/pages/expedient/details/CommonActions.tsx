import {
    useResourceApiService,
    useBaseAppContext, useConfirmDialogButtons,
} from 'reactlib';
import useAssignar from "../actions/Assignar.tsx";
import useCambiarEstat from "../actions/CambiarEstat.tsx";
import useCambiarPrioritat from "../actions/CambiarPrioritat.tsx";
import {useTranslation} from "react-i18next";
import useRelacionar from "../actions/Relacionar.tsx";
import useInformacioArxiu from "../../InformacioArxiu.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {Divider} from "@mui/material";
import {useExportarDocuments} from "../actions/ExportarDocuments.tsx";
import useHistoric from "../../Historic.tsx";
import useTancar from "../actions/Tancar.tsx";
import useDescargarDocuments from "../actions/DescargarDocuments.tsx";
import useModifyExpedient from "../actions/ModifyExpedient.tsx";

export const iniciaDescarga = (url:string, fileName:string) => {
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link); // Limpieza
    URL.revokeObjectURL(url);
}
export const iniciaDescargaBlob = (result: any) => {
    const url = URL.createObjectURL(result.blob);
    iniciaDescarga(url, result.fileName)
}
export const iniciaDescargaJSON = (result: any) => {
    const data = result.blob;

    const fileName = result.fileName;

    // 1. Convertir el objeto a una cadena JSON
    const jsonStr = JSON.stringify(data, null, 2); // `null, 2` para formato legible

    // 2. Crear un Blob con el contenido
    const blob = new Blob([jsonStr], { type: "application/json" });

    iniciaDescargaBlob({fileName, blob})
}

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    const {
        patch: apiPatch,
        artifactAction: apiAction,
		artifactReport: apiReport,
    } = useResourceApiService('expedientResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};
	
    const action = (id:any, code:string, msg:string) => {
        apiAction(undefined, {code: code, data:{ ids: [id], massivo: false }})
			.then(() => {
			    refresh?.()
			    temporalMessageShow(null, msg, 'success');
			})
			.catch((error) => {
			    temporalMessageShow(null, error?.message, 'error');
			});		
    }
	
	const report = (id:any, code:string, msg:string, fileType:any) => {
	    apiReport(undefined, {code: code, data:{ ids: [id], massivo: false }, fileType})
			.then((result) => {
				iniciaDescargaBlob(result);
                temporalMessageShow(null, msg, 'info');
			})
			.catch((error) => {
			    temporalMessageShow(null, error?.message, 'error');
			});		
	}

    const reobrir= (id:any, row:any): void => {
        messageDialogShow(
            '',
            t('page.expedient.action.open.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    action(id, 'REOBRIR', t('page.expedient.action.open.ok', {expedient: row?.nom}));
                }
            });
    }
    const follow= (id:any, row:any): void => action(id, 'FOLLOW', t('page.expedient.action.follow.ok', {user: user?.nom, expedient: row?.nom}));
    const unfollow= (id:any, row:any): void => action(id, 'UNFOLLOW', t('page.expedient.action.unfollow.ok', {user: user?.nom, expedient: row?.nom}));
    const agafar= (id:any, row:any): void => action(id, 'AGAFAR', t('page.expedient.action.agafar.ok', {user: user?.nom, expedient: row?.nom}));
    const retornar= (id:any, row:any) :void => action(id, 'RETORNAR', t('page.expedient.action.retornar.ok', {user: user?.nom, expedient: row?.nom}));
	const alliberar= (id:any, row:any) :void => action(id, 'ALLIBERAR', t('page.expedient.action.lliberar.ok', {expedient: row?.nom}));
    const syncArxiu= (id:any): void => {
        apiAction(undefined, {code: 'SYNC_ARXIU', data:{ ids: [id], massivo: false }})
            .then((result) => {
                const success = result.filter((r:any)=>r?.codi=='OK')
                const info = result.filter((r:any)=>r?.codi=='INFO')
                const error = result.filter((r:any)=>r?.codi=='ERROR')

                success?.length>0 && temporalMessageShow(null, success.map((r:any)=><p>{r?.valor}</p>), 'success');
                info?.length>0 && temporalMessageShow(null, info.map((r:any)=><p>{r?.valor}</p>), 'info');
                error?.length>0 && temporalMessageShow(null, error.map((r:any)=><p>{r?.valor}</p>), 'error');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }
	const eliminar= (id:any, row:any) :void => {
        messageDialogShow(
            '',
            t('page.expedient.results.checkDelete'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    action(id, 'ESBORRAR', t('page.expedient.action.eliminar.ok', {expedient: row?.nom}));
                }
            });
    }

    const excelInteressats= (id:any): void => {
        apiReport(id, {code: 'PLANTILLA_EXCEL_INTERESSATS', fileType: 'XLSX'})
            .then((result) => {
                iniciaDescargaBlob(result);
                temporalMessageShow(null, t('page.expedient.action.excelInteressats.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const exportIndexPdf= (id:any) => report(id, 'EXPORT_INDEX_PDF', t('page.expedient.results.actionOk'), 'PDF')
	const exportIndexXls= (id:any) => report(id, 'EXPORT_INDEX_XLS', t('page.expedient.results.actionOk'), 'XLSX')
	const exportPdfEni= (id:any) => report(id, 'EXPORT_INDEX_ENI', t('page.expedient.results.actionOk'), 'ZIP')
	const exportEni= (id:any) => report(id, 'EXPORT_ENI', t('page.expedient.results.actionOk'), 'ZIP')
	const exportInside= (id:any) => report(id, 'EXPORT_INSIDE', t('page.expedient.results.actionOk'), 'ZIP')
    const eliminarRelacio = (id:any, row:any, relacioId:any) => {
        messageDialogShow(
            '',
            t('page.expedient.results.checkRelacio', {expedient: row?.nom}),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    const relacionatsPer:any = row?.relacionatsPer.filter((r:any)=>r.id!=relacioId);
                    const relacionatsAmb:any = row?.relacionatsAmb.filter((r:any)=>r.id!=relacioId);

                    apiPatch(id,{data: {relacionatsPer, relacionatsAmb} })
                        .then(() => {
                            refresh?.()
                            temporalMessageShow(null, t('page.expedient.action.relacio.ok', {expedient: row?.nom}), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }

    const importarExpedient = (id:any, idOrigen:any) => {
        return apiAction(id, { code: 'IMPORTAR', data: {expedientOrigen: {id: idOrigen} } })
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.expedient.action.importar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        reobrir,
        follow,
        unfollow,
        agafar,
        retornar,
        alliberar,
        eliminar,
        exportIndexPdf,
        exportIndexXls,
        exportPdfEni,
        exportEni,
        exportInside,
        syncArxiu,
        eliminarRelacio,
        excelInteressats,
        importarExpedient,
    }
}

export const useCommonActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user, permisos } = useUserSession();
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';

    const {
        reobrir,
        follow,
        unfollow,
        agafar,
        retornar,
        alliberar,
        eliminar,
        exportIndexPdf,
        exportIndexXls,
        exportPdfEni,
        exportEni,
        exportInside,
        syncArxiu,
    } = useActions(refresh);
    const {handleOpen: handelHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleOpen: handleArxiuOpen, dialog: arxiuDialog} = useInformacioArxiu('expedientResource', 'ARXIU_EXPEDIENT');
    const {handleShow: hanldeAssignar, content: assignarContent} = useAssignar(refresh);
    const {handleShow: hanldeCambiarEstado, content: cambiarEstadoContent} = useCambiarEstat(refresh);
    const {handleShow: hanldeCambiarPrioridad, content: cambiarPrioridadContent} = useCambiarPrioritat(refresh);
    const {handleShow: hanldeRelacionar, content: cambiarRelacionar} = useRelacionar(refresh);
    const {handleShow: handleExportDoc, content: contentExportDoc} = useExportarDocuments();
    const {handleShow: handleTancar, content: contentTancar} = useTancar(refresh);
    const {handleShow: handleDescargarDocuments, content: contentDescargarDocuments} = useDescargarDocuments();

    const {handleShow: handleModifyExpedient, content: contentModifyExpedient} = useModifyExpedient(refresh)

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

    const actions = [
        {
            title: t('page.expedient.action.detall.label'),
            icon: "folder",
            linkTo: "/contingut/{{id}}",
            showInMenu: true,
        },
        {
            title: t('page.expedient.action.update.label'),
            icon: 'edit',
            showInMenu: true,
            onClick: handleModifyExpedient,
            hidden: (row:any) => isTancat(row) || !row?.potModificar,
        },
        {
            title: t('page.expedient.action.follow.label'),
            icon: "person_add",
            showInMenu: true,
            onClick: follow,
            hidden: (row:any) => row?.seguidor || !isUsuariActualWrite(row),// si el usuario actual es seguidor
        },
        {
            title: t('page.expedient.action.unfollow.label'),
            icon: "person_remove",
            showInMenu: true,
            onClick: unfollow,
            hidden: (row:any) => !row?.seguidor || !isUsuariActualWrite(row),// si el usuario actual no es seguidor
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            title: t('page.expedient.action.assignar.label'),
            icon: "person",
            showInMenu: true,
            onClick: hanldeAssignar,
            hidden: (row:any) => !isAdminOAdminOrgan(row),// si el usuario actual no admin o organo
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row:any) => !isAdminOAdminOrgan(row),
        },
        {
            title: t('page.expedient.action.agafar.label'),
            icon: "lock",
            showInMenu: true,
            onClick: agafar,
            hidden: isAgafatUsuariActual
        },
        {
            title: t('page.expedient.action.retornar.label'),
            icon: "undo",
            showInMenu: true,
            onClick: retornar,
            hidden: (row:any) => !isAgafatUsuariActual(row) || row?.agafatPer?.id == row?.createdBy,
        },
        {
            title: t('page.expedient.action.lliberar.label'),
            icon: "lock_open",
            showInMenu: true,
            onClick: alliberar,
            hidden: (row:any) => !row?.agafatPer,
        },
        {
            title: t('page.expedient.action.changePrioritat.label'),
            icon: "logout",
            showInMenu: true,
            onClick: hanldeCambiarPrioridad,
            hidden: (row:any) => !row?.potModificar,
        },
        {
            title: t('page.expedient.action.changeEstat.label'),
            icon: "logout",
            showInMenu: true,
            onClick: hanldeCambiarEstado,
            hidden: (row:any) => isTancat(row) || !row?.potModificar,
        },
        {
            title: t('page.expedient.action.relacio.label'),
            icon: "link",
            showInMenu: true,
            onClick: hanldeRelacionar,
            hidden: (row:any) => !row?.potModificar,
        },
        {
            title: t('page.expedient.action.close.label'),
            icon: "check",
            showInMenu: true,
            onClick: handleTancar,
            disabled: (row:any) => !row?.potTancar,
            hidden: (row:any) => !row?.potModificar || isTancat(row),
        },
        {
            title: t('page.expedient.action.open.label'),
            icon: "undo",
            showInMenu: true,
            onClick: reobrir,
            hidden: (row:any) => !isTancat(row) || !user?.sessionScope?.isReobrirPermes || !( !user?.sessionScope?.isTancamentLogicActiu || row?.tancatData),
        },
        {
            title: t('page.expedient.action.eliminar.label'),
            icon: "delete",
            showInMenu: true,
            onClick: eliminar,
            hidden: (row:any) => !row?.potModificar || row.conteDocumentsDefinitius,
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            title: t('page.contingut.action.history.label'),
            icon: "list",
            showInMenu: true,
            onClick: handelHistoricOpen,
        },
        {
            title: t('page.expedient.action.download.label'),
            icon: "download",
            showInMenu: true,
            onClick: handleDescargarDocuments,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.action.exportPDF.label'),
            icon: "format_list_numbered",
            showInMenu: true,
			onClick: exportIndexPdf,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.action.exportEXCEL.label'),
            icon: "lists",
            showInMenu: true,
			onClick: exportIndexXls,
            hidden: (row:any) => !(row?.conteDocuments && user?.sessionScope?.isExportacioExcelActiva),
        },
        {
            title: t('page.expedient.action.exportPDF_ENI.label'),
            icon: "format_list_numbered",
            showInMenu: true,
			onClick: exportPdfEni,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.action.exportENI.label'),
            icon: "folder_code",
            showInMenu: true,
			onClick: exportEni,
			disabled: (row:any) => !row?.conteDocumentsDefinitius,
			hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.action.exportINSIDE.label'),
            icon: "folder_zip",
            showInMenu: true,
			onClick: exportInside,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !(row?.conteDocuments && user?.sessionScope?.isExportacioInsideActiva),
        },
        {
            title: t('page.expedient.action.export.label'),
            icon: "description",
            showInMenu: true,
			onClick: handleExportDoc,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.contingut.action.infoArxiu.label'),
            icon: "info",
            showInMenu: true,
            onClick: handleArxiuOpen,
            disabled: (row:any) => !row?.arxiuUuid,
        },
        {
            title: t('page.expedient.action.sincronitzar.label'),
            icon: "autorenew",
            showInMenu: true,
            onClick: syncArxiu,
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
        {contentTancar}
        {contentDescargarDocuments}
        {contentModifyExpedient}
    </>;

    return {
        actions,
        components
    }
}