import {
    useResourceApiService,
    useBaseAppContext,
    useConfirmDialogButtons
} from 'reactlib';
import useInformacioArxiu from "../actions/InformacioArxiu.tsx";
import useAssignar from "../actions/Assignar.tsx";
import useCambiarEstat from "../actions/CambiarEstat.tsx";
import useCambiarPrioritat from "../actions/CambiarPrioritat.tsx";

export const useCommonActions = (refresh?: () => void) => {
    const {
        patch: apiPatch,
        delette: apiDelete
    } = useResourceApiService('expedientResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {handleOpen: arxiuhandleOpen, dialog: arxiuDialog} = useInformacioArxiu("expediente");
    const {handleShow: hanldeAssignar, content: assignarContent} = useAssignar(refresh);
    const {handleShow: hanldeCambiarEstado, content: cambiarEstadoContent} = useCambiarEstat(refresh);
    const {handleShow: hanldeCambiarPrioridad, content: cambiarPrioridadContent} = useCambiarPrioritat(refresh);

    const actions = [
        {
            title: "Seguidores",
            icon: "people",
        },
        ////

        {
            title: "Gestionar",
            icon: "folder",
            linkTo: "/contingut/{{id}}",
            showInMenu: true,
        },
        {
            title: "Seguir",
            icon: "person_add",
            showInMenu: true,
            // hidden: // si el usuario actual es seguidor
        },
        // {
        //     title: "Dejar de seguir",
        //     icon: "person_remove",
        //     showInMenu: true,
        //     // hidden: // si el usuario actual no es seguidor
        // },
        {
            title: "Assignar",
            icon: "person",
            showInMenu: true,
            onClick: hanldeAssignar,
            // hidden: // si el usuario actual no admin o organo
        },
        {
            title: "Coger",
            icon: "lock",
            showInMenu: true,
            onClick: (id: any): void => {
                apiPatch(id, {
                    data: {
                        agafatPer: {
                            // TODO: change user from session
                            id: "rip_admin"
                        },
                    }
                })
                    .then(() => {
                        refresh?.();
                        temporalMessageShow(null, '', 'success');
                    })
            }
        },
        {
            title: "Liberar",
            icon: "lock_open",
            showInMenu: true,
            onClick: (id: any): void => {
                apiPatch(id, {
                    data: {
                        agafatPer: null,
                    }
                })
                    .then(() => {
                        refresh?.();
                        temporalMessageShow(null, '', 'success');
                    })
            }
        },
        {
            title: "Cambiar prioridad...",
            icon: "",
            showInMenu: true,
            onClick: hanldeCambiarPrioridad
        },
        {
            title: "Cambiar estado...",
            icon: "",
            showInMenu: true,
            onClick: hanldeCambiarEstado,
            disabled: (row:any) => row?.estat != "OBERT",
        },
        {
            title: "Relacionar...",
            icon: "link",
            showInMenu: true,
        },
        {
            title: "Cerrar...",
            icon: "check",
            showInMenu: true,
            disabled: (row:any) => row?.estat != "OBERT",
        },
        {
            title: "Borrar",
            icon: "delete",
            showInMenu: true,
            onClick: (rowId:any) => {
                messageDialogShow(
                    'Title',
                    'Message',
                    confirmDialogButtons,
                    confirmDialogComponentProps)
                    .then((value: any) => {
                        if (value) {
                            apiDelete(rowId)
                                .then(() => {
                                    refresh?.();
                                    temporalMessageShow(null, 'Elemento borrado', 'success');
                                })
                                .catch((error) => {
                                    temporalMessageShow('Error', error.message, 'error');
                                });
                        }
                    });
            },
            disabled: (row:any) => row?.estat == "TANCAT",
        },
        {
            title: "Histórico de acciones",
            icon: "list",
            showInMenu: true,
        },
        {
            title: "Descargar documentos...",
            icon: "download",
            showInMenu: true,
        },
        {
            title: "Exportar indice PDF...",
            icon: "format_list_numbered",
            showInMenu: true,
        },
        {
            title: "Indice PDF y exportación EIN...",
            icon: "format_list_numbered",
            showInMenu: true,
            disabled: true,
        },
        {
            title: "Información archivo",
            icon: "info",
            showInMenu: true,
            onClick: arxiuhandleOpen
        },
        {
            title: "Sincronizar estado con archivo",
            icon: "autorenew",
            showInMenu: true,
        },
    ]
    const components = <>
        {cambiarPrioridadContent}
        {cambiarEstadoContent}
        {arxiuDialog}
        {assignarContent}
    </>;
    return {
        actions,
        components
    }
}