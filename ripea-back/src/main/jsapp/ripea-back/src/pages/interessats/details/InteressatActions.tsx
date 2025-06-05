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
import {potModificar as potModificarExpedient} from "../../expedient/details/Expedient.tsx";

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
                temporalMessageShow(null, t('page.interessat.action.exportar.ok'), 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const deleteRepresentent = (id: any, row: any) => {
        getOne(row?.representant?.id)
            .then((representant) => {
                messageDialogShow(
                    t('page.interessat.action.deleteRep.check'),
                    t('page.interessat.dialog.deleteRep.description'),
                    confirmDialogButtons,
                    confirmDialogComponentProps)
                    .then((value: any) => {
                        if (value) {
                            if (representant?.esRepresentant) {
                                apiDelete(representant?.id)
                                    .then(() => {
                                        refresh?.();
                                        temporalMessageShow(null, t('page.interessat.action.deleteRep.ok'), 'success');
                                    })
                                    .catch((error) => {
                                        temporalMessageShow(null, error.message, 'error');
                                    });
                            } else {
                                apiPatch(id, {
                                    data: { representant: null }
                                })
                                    .then(() => {
                                        refresh?.();
                                        temporalMessageShow(null, t('page.interessat.action.deleteRep.ok'), 'success');
                                    })
                            }
                        }
                    });
            })
    }
    const deleteInteressat = (id: any, row: any) => {
        messageDialogShow(
            t('page.interessat.action.delete.check'),
            t('page.interessat.dialog.delete.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    if (row?.hasRepresentats) {
                        apiPatch(id, {
                            data: { esRepresentant: true }
                        })
                            .then(() => {
                                refresh?.();
                                temporalMessageShow(null, t('page.interessat.action.delete.ok'), 'success');
                            })
                    } else {
                        apiDelete(id)
                            .then(() => {
                                refresh?.();
                                temporalMessageShow(null, t('page.interessat.action.delete.ok'), 'success');
                            })
                            .catch((error) => {
                                temporalMessageShow(null, error.message, 'error');
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

    const potModificar:boolean = potModificarExpedient(entity)

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetail,
            hidden: () => potModificar,
        },
        {
            title: t('common.update'),
            icon: 'edit',
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: () => !potModificar,
        },
        {
            title: t('page.interessat.action.delete.label'),
            icon: "delete",
            showInMenu: true,
            onClick: deleteInteressat,
            hidden: () => !potModificar,
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: () => !potModificar,
        },
        {
            title: t('page.interessat.action.createRep.label'),
            icon: "add",
            showInMenu: true,
            onClick: createRepresentent,
            hidden: (row: any) => row?.representant || !potModificar,
        },
        {
            title: t('page.interessat.action.updateRep.label'),
            icon: "edit",
            showInMenu: true,
            onClick: updateRepresentent,
            hidden: (row: any) => !row?.representant || !potModificar,
        },
        {
            title: t('page.interessat.action.deleteRep.label'),
            icon: "delete",
            showInMenu: true,
            onClick: deleteRepresentent,
            hidden: (row: any) => !row?.representant || !potModificar,
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