import {Divider} from "@mui/material";
import {
    useBaseAppContext,
    useConfirmDialogButtons,
    useResourceApiService
} from "reactlib";
import {useTranslation} from "react-i18next";
import useInteressatDetail from "./InteressatDetail.tsx";
import useCreate, {useCreateRepresentant} from "../actions/Create.tsx";
import {iniciaDescargaBlob, iniciaDescargaJSON} from "../../expedient/details/CommonActions.tsx";
import useImportarSGD from "../actions/ImportarSGD.tsx";
import useManageInteressatGrups from "../actions/groups/ManageInteressatGrups.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {
        artifactAction: apiAction,
    } = useResourceApiService('interessatResource');

    const guardarArxiu = (id:any, row:any) => {
        apiAction(undefined, {code: 'GUARDAR_ARXIU', data:{ ids: [id], massivo: false}})
            .then(()=>{
                refresh?.()
                temporalMessageShow(null, t('page.contingut.action.guardarArxiu.ok', {contingut: row?.codiNom}), 'success');
            })
            .catch((error) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    const deleteRepresentent = (id: any) => {
        messageDialogShow(
            t('page.interessat.action.deleteRep.check'),
            t('page.interessat.action.deleteRep.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    apiAction(id, {code: 'DELETE_REPRESENTANT'})
                        .then((data:any)=>{
                            refresh?.()
                            temporalMessageShow(null, t('page.interessat.action.deleteRep.ok',{data}), 'success');
                        })
                        .catch((error) => {
                            error?.message && temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }
    const deleteInteressat = (id: any) => {
        messageDialogShow(
            t('page.interessat.action.delete.check'),
            t('page.interessat.action.delete.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    apiAction(id, {code: 'DELETE_INTERESSAT'})
                        .then((data:any) => {
                            refresh?.();
                            temporalMessageShow(null, t('page.interessat.action.delete.ok',{data}), 'success');
                        })
                        .catch((error) => {
                            error?.message && temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }

    return {
        guardarArxiu,
        deleteRepresentent,
        deleteInteressat,
    }
}
export const useMassiveActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
        artifactReport: apiReport
    } = useResourceApiService('interessatResource');
    const {temporalMessageShow} = useBaseAppContext();

    const massiveAction = (ids:any, code:string, msg:string) => {
        return apiAction(undefined, {code :code, data:{ ids, massivo: true }})
            .then((result) => {
                refresh?.();
                iniciaDescargaBlob(result);
                temporalMessageShow(null, msg, 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const guardarArxiu = (ids: any[]): void => { massiveAction(ids, 'GUARDAR_ARXIU', t('page.expedient.results.actionOk')); }
    const exportar = (ids:any[], entity:any) => {
        return apiReport(undefined, {code :'EXPORTAR', data:{ ids: ids, massivo: true, expedient: {id: entity?.id, description: entity?.nom,} }, fileType: 'JSON'})
            .then((result) => {
                iniciaDescargaJSON(result);
                temporalMessageShow(null, t('page.interessat.action.exportar.ok'), 'info');
            })
            .catch((error) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        guardarArxiu,
        exportar,
    }
}

const useInteressatActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {guardarArxiu, deleteRepresentent, deleteInteressat} = useActions(refresh);
    const {handleOpen: handleDetail, dialog: dialogDetail} = useInteressatDetail();
    const {create, content: contentCreate} = useCreate(refresh)
    const {create: createRepresentent, update: updateRepresentent, content} = useCreateRepresentant(refresh)
    const {handleShow: handleImportarSGD, content: contentImportarSGD} = useImportarSGD(entity, refresh)
    const {handleShow: handleManageInteressatGrups, dialog: dialogManageInteressatGrups} = useManageInteressatGrups(refresh);

    const createActions = [
        {
            label: t('page.interessat.title')+"...",
            icon: 'person_add',
            showInMenu: false,
            onClick: () => create({
                expedient: {id: entity?.id},
                esRepresentant: false,
            }),
        },
        {
            label: t('page.interessat.action.importSGD.label'),
            icon: 'group_search',
            showInMenu: true,
            onClick: handleImportarSGD,
        }
    ]

    const actions:any[] = [
        {
            label: t('page.contingut.action.guardarArxiu.label'),
            icon: 'autorenew',
            showInMenu: true,
            onClick: guardarArxiu,
            disabled: !entity?.arxiuPropagat,
            hidden: (row:any) => row?.arxiuPropagat && ( !row?.representant || row?.representantInfo?.arxiuPropagat ),
        },
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetail,
            hidden: entity?.potModificar,
        },
        {
            label: t('common.update'),
            icon: 'edit',
            showInMenu: true,
			clickShowUpdateDialog: true,
            hidden: !entity?.potModificar,
        },
        {
            label: t('page.interessat.action.gestGrups.label'),
            icon: "groups",
            showInMenu: true,
            onClick: handleManageInteressatGrups,
            hidden: !entity?.potModificar,
        },
		{
		    label: t('page.interessat.action.delete.label'),
		    icon: "delete",
		    showInMenu: true,
		    onClick: deleteInteressat,
		    hidden: !entity?.potModificar,
		},
        {
            label: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row: any) => (row?.tipus == 'InteressatAdministracioEntity' || !entity?.potModificar),
        },
        {
            label: t('page.interessat.action.createRep.label'),
            icon: "add",
            showInMenu: true,
            onClick: createRepresentent,
            hidden: (row: any) => (row?.representant || row?.tipus == 'InteressatAdministracioEntity' || !entity?.potModificar),
        },
        {
            label: t('page.interessat.action.updateRep.label'),
            icon: "edit",
            showInMenu: true,
            onClick: updateRepresentent,
            hidden: (row: any) => (!row?.representant || row?.tipus == 'InteressatAdministracioEntity' || !entity?.potModificar),
        },
        {
            label: t('page.interessat.action.deleteRep.label'),
            icon: "delete",
            showInMenu: true,
            onClick: deleteRepresentent,
            hidden: (row: any) => (!row?.representant || row?.tipus == 'InteressatAdministracioEntity' || !entity?.potModificar),
        },
    ];

    const components=<>
        {content}
        {dialogDetail}
        {contentCreate}
        {contentImportarSGD}
		{dialogManageInteressatGrups}
    </>;

    return {
        actions,
        createActions,
        components,
    }
}
export default useInteressatActions;