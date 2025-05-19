import {Divider} from "@mui/material";
import {
    useBaseAppContext,
    useConfirmDialogButtons,
    useResourceApiService
} from "reactlib";
import {useTranslation} from "react-i18next";
import useInteressatDetail from "./InteressatDetail.tsx";
import useCreate from "../actions/Create.tsx";
import {iniciaDescargaJSON} from "../../expedient/details/CommonActions.tsx";
import {potModificar} from "../../expedient/details/Expedient.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {
        delete: apiDelete,
        patch: apiPatch,
        getOne,
        artifactReport: apiReport,
    } = useResourceApiService('interessatResource');

    const exportar = (ids:any[], entity:any) => {
        return apiReport(undefined, {code :'EXPORTAR', data:{ ids: ids, massivo: true, expedient: {id: entity?.id, description: entity?.nom,} }, fileType: 'JSON'})
            .then((result) => {
                iniciaDescargaJSON(result);
                temporalMessageShow(null, '', 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
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
        exportar,
        deleteRepresentent,
        deleteInteressat,
    }
}

const useInteressatActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {deleteRepresentent, deleteInteressat} = useActions(refresh);
    const {handleOpen: handleDetail, dialog: dialogDetail} = useInteressatDetail();
    const {createRepresentent, updateRepresentent, content} = useCreate(t('page.interessat.rep'), refresh)

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetail,
            hidden: potModificar(entity),
        },
        {
            title: t('common.update'),
            icon: 'edit',
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: !potModificar(entity),
        },
        {
            title: t('page.interessat.actions.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: deleteInteressat,
            hidden: !potModificar(entity),
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
            hidden: !potModificar(entity),
        },
        {
            title: t('page.interessat.actions.createRep'),
            icon: "add",
            showInMenu: true,
            onClick: createRepresentent,
            hidden: (row: any) => row?.representant || !potModificar(entity),
        },
        {
            title: t('page.interessat.actions.updateRep'),
            icon: "edit",
            showInMenu: true,
            onClick: updateRepresentent,
            hidden: (row: any) => !row?.representant || !potModificar(entity),
        },
        {
            title: t('page.interessat.actions.deleteRep'),
            icon: "delete",
            showInMenu: true,
            onClick: deleteRepresentent,
            hidden: (row: any) => !row?.representant || !potModificar(entity),
        },
    ];

    const components=<>
        {content}
        {dialogDetail}
    </>;

    return {
        actions,
        components,
    }
}
export default useInteressatActions;