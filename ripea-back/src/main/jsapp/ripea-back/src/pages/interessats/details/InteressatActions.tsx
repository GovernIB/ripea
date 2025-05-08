import {
    MuiFormDialog, MuiFormDialogApi,
    useBaseAppContext,
    useConfirmDialogButtons,
    useResourceApiService
} from "reactlib";
import {InteressatsGridForm} from "../InteressatsGrid.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import {Divider} from "@mui/material";
import useInteressatDetail from "./InteressatDetail.tsx";

const useActions = (apiRef:any,refresh?: () => void) => {
    const { t } = useTranslation();

    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {
        delete: apiDelete,
        patch: apiPatch,
        getOne
    } = useResourceApiService('interessatResource');

    const createRepresentent = (id: any, row:any) => {
        apiRef.current?.show(undefined, {
            expedient: {
                id: row?.expedient?.id
            },
            representat: {
                id: id
            },
            esRepresentant: true,
        })
            .then(() => {
                refresh?.();
                temporalMessageShow(null, 'Elemento creado', 'success');
            })
            .catch((error:any) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }
    const updateRepresentent = (id: any, row: any) => {
        apiRef.current?.show(row?.representant?.id)
            .then(() => {
                refresh?.();
                temporalMessageShow(null, 'Elemento modificado', 'success');
            })
            .catch((error:any) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }
    const deleteRepresentent = (id: any, row: any) => {
        getOne(row?.representant?.id)
            .then((representant) => {
                messageDialogShow(
                    t('page.interessat.dialog.deleteRepTitle'),
                    t('page.interessat.dialog.deleteRepMessage'),
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
                                apiPatch(id, {
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
    const deleteInteressat = (id: any, row: any) => {
        messageDialogShow(
            t('page.interessat.dialog.deleteTitle'),
            t('page.interessat.dialog.deleteMessage'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    if (row?.hasRepresentats) {
                        apiPatch(id, {
                            data: {
                                esRepresentant: true,
                            }
                        })
                            .then(() => {
                                refresh?.();
                                temporalMessageShow(null, 'Elemento borrado', 'success');
                            })
                    } else {
                        apiDelete(id)
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

    return {
        createRepresentent,
        updateRepresentent,
        deleteRepresentent,
        deleteInteressat,
    }
}

const useInteressatActions = (readOnly:boolean,refresh?: () => void) => {
    const { t } = useTranslation();

    const apiRef = useRef<MuiFormDialogApi>();

    const {
        createRepresentent,
        updateRepresentent,
        deleteRepresentent,
        deleteInteressat,
    } = useActions(apiRef, refresh);
    const {handleOpen: handleDetail, dialog: dialogDetail} = useInteressatDetail();

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetail,
            // hidden: !readOnly,
        },
        {
            title: t('page.interessat.actions.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: deleteInteressat,
            hidden: readOnly,
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
            hidden: readOnly,
        },
        {
            title: t('page.interessat.actions.createRep'),
            icon: "add",
            showInMenu: true,
            onClick: createRepresentent,
            hidden: (row: any) => row?.representant || readOnly,
        },
        {
            title: t('page.interessat.actions.updateRep'),
            icon: "edit",
            showInMenu: true,
            onClick: updateRepresentent,
            hidden: (row: any) => !row?.representant || readOnly,
        },
        {
            title: t('page.interessat.actions.deleteRep'),
            icon: "delete",
            showInMenu: true,
            onClick: deleteRepresentent,
            hidden: (row: any) => !row?.representant || readOnly,
        },
    ];

    const components=<>
        <MuiFormDialog
            resourceName={"interessatResource"}
            title={t('page.interessat.rep')}
            apiRef={apiRef}
        >
            <InteressatsGridForm/>
        </MuiFormDialog>
        {dialogDetail}
    </>;

    return {
        actions,
        components,
    }
}
export default useInteressatActions;