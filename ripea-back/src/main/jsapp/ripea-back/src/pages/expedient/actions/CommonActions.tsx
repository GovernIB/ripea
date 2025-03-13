import React from 'react';
import CambiarEstado from './CambiarEstado';
import CambiarPrioritat from './CambiarPrioritat';
import {
    MuiFormDialogApi,
    useResourceApiService,
    useBaseAppContext, useConfirmDialogButtons
} from 'reactlib';
import useInformacioArxiu from "../detall/InformacioArxiu.tsx";

export const useCommonActions = (refresh?: () => void) => {
    const {
        patch: apiPatch,
        delette: apiDelete
    } = useResourceApiService('expedientResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const cambiarPrioridadApiRef = React.useRef<MuiFormDialogApi>();
    const cambiarEstadoApiRef = React.useRef<MuiFormDialogApi>();

    const {handleOpen: arxiuhandleOpen, dialog: arxiuDialog} = useInformacioArxiu();

    const actions = [
        // {
        //     title: "",
        //     icon: "forum",
        // },
        {
            title: "",
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
            onClick: (id: any) => {
                cambiarPrioridadApiRef.current?.show(id)?.then(() => {
                    refresh?.()
                })
            }
        },
        {
            title: "Cambiar estado...",
            icon: "",
            showInMenu: true,
            onClick: (id: any) => {
                cambiarEstadoApiRef.current?.show(id)?.then(() => {
                    refresh?.()
                })
            },
            disabled: (row:any) => {
                return row?.estat != "OBERT"
            },
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
            disabled: (row:any) => {
                return row?.estat != "OBERT"
            },
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
            disabled: (row:any) => {
                return row?.estat == "TANCAT"
            },
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
            onClick: (id:number,row:any)=>{
                arxiuhandleOpen(row)
            }
        },
        {
            title: "Sincronizar estado con archivo",
            icon: "autorenew",
            showInMenu: true,
        },
    ]
    const components = <>
        <CambiarPrioritat apiRef={cambiarPrioridadApiRef} />
        <CambiarEstado apiRef={cambiarEstadoApiRef} />
        {arxiuDialog}
    </>;
    return {
        actions,
        components
    }
}