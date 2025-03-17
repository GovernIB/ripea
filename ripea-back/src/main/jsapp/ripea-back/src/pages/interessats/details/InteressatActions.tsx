import {useRef} from "react";
import {
    MuiFormDialogApi,
    useBaseAppContext,
    useConfirmDialogButtons,
    useResourceApiService
} from "reactlib";

const useInteressatActions = (expedientId:number, refresh?: () => void) => {
    const {
        delette: apiDelete,
        patch: apiPatch,
        getOne
    } = useResourceApiService('interessatResource');
    const formApiRef = useRef<MuiFormDialogApi>();
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const createRepresentent = (rowId: any) => {
        formApiRef.current?.show(undefined, {
            expedient: {
                id: expedientId
            },
            representat: {
                id: rowId
            },
            esRepresentant: true,
        })
            .then(() => {
                refresh?.();
                temporalMessageShow(null, 'Elemento creado', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }
    const updateRepresentent = (rowId: any, row: any) => {
        formApiRef.current?.show(row?.representant?.id)
            .then(() => {
                refresh?.();
                temporalMessageShow(null, 'Elemento modificado', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }
    const deleteRepresentent = (rowId: any, row: any) => {
        getOne(row?.representant?.id)
            .then((representant) => {
                messageDialogShow(
                    'Title',
                    'Message',
                    confirmDialogButtons,
                    confirmDialogComponentProps)
                    .then((value: any) => {
                        if (value) {
                            if (representant?.esRepresentant) {
                                apiDelete(representant?.id)
                                    .then(() => {
                                        refresh?.();
                                        temporalMessageShow(null, 'Elemento borrado', 'success');
                                    })
                                    .catch((error) => {
                                        temporalMessageShow('Error', error.message, 'error');
                                    });
                            } else {
                                apiPatch(rowId, {
                                    data: {
                                        representant: null,
                                    }
                                })
                                    .then(() => {
                                        refresh?.();
                                        temporalMessageShow(null, 'Elemento borrado', 'success');
                                    })
                            }
                        }
                    });
            })
    }
    const deleteInteressat = (rowId: any, row: any) => {
        messageDialogShow(
            'Title',
            'Message',
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    if (row?.hasRepresentats) {
                        apiPatch(rowId, {
                            data: {
                                esRepresentant: true,
                            }
                        })
                            .then(() => {
                                refresh?.();
                                temporalMessageShow(null, 'Elemento borrado', 'success');
                            })
                    } else {
                        apiDelete(rowId)
                            .then(() => {
                                refresh?.();
                                temporalMessageShow(null, 'Elemento borrado', 'success');
                            })
                            .catch((error) => {
                                temporalMessageShow('Error', error.message, 'error');
                            });
                    }
                }
            });
    }

    const actions = [
        {
            title: "Borrar Interesado",
            icon: "delete",
            showInMenu: true,
            onClick: deleteInteressat,
        },
        {
            title: "Añadir Representante",
            icon: "add",
            showInMenu: true,
            onClick: createRepresentent,
            disabled: (row: any) => row?.representant,
        },
        {
            title: "Modificar Representante",
            icon: "edit",
            showInMenu: true,
            onClick: updateRepresentent,
            disabled: (row: any) => !row?.representant,
        },
        {
            title: "Borrar Representante",
            icon: "delete",
            showInMenu: true,
            onClick: deleteRepresentent,
            disabled: (row: any) => !row?.representant,
        },
    ];

    return {
        actions,
        formApiRef,
    }
}
export default useInteressatActions;