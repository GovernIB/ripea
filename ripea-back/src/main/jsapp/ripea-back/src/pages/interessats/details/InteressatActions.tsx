import {Divider} from "@mui/material";
import {
    useBaseAppContext,
    useConfirmDialogButtons,
    useResourceApiService
} from "reactlib";
import {useTranslation} from "react-i18next";
import useInteressatDetail from "./InteressatDetail.tsx";
import useCreate, {useCreateRepresentant} from "../actions/Create.tsx";
import {iniciaDescargaJSON} from "../../expedient/details/CommonActions.tsx";
import useImportarSGD from "../actions/ImportarSGD.tsx";
import useManageInteressatGrups from "../groups/actions/ManageInteressatGrups.tsx";
import useModifyGrup from "../groups/actions/ModifyGrup.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {
        artifactAction: apiAction,
        artifactReport: apiReport,
    } = useResourceApiService('interessatResource');

    const guardarArxiu = (id:any, row:any) => {
        apiAction(id, {code: 'GUARDAR_ARXIU'})
            .then(()=>{
                refresh?.()
                temporalMessageShow(null, t('page.contingut.action.guardarArxiu.ok', {contingut: row?.codiNom}), 'success');
            })
            .catch((error) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

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
        exportar,
        deleteRepresentent,
        deleteInteressat,
    }
}

const useInteressatActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

	const {updateGrup: handleModifyGrup, deleteGrup: deleteGrup, content: contentGrup} = useModifyGrup(entity, refresh);
	const {handleShow: handleManageInteressatGrups, dialog: dialogManageInteressatGrups} = useManageInteressatGrups(entity, refresh);
    const {guardarArxiu, deleteRepresentent, deleteInteressat} = useActions(refresh);
    const {handleOpen: handleDetail, dialog: dialogDetail} = useInteressatDetail();
    const {create, content: contentCreate} = useCreate(refresh)
    const {create: createRepresentent, update: updateRepresentent, content} = useCreateRepresentant(refresh)
    const {handleShow: handleImportarSGD, content: contentImportarSGD} = useImportarSGD(entity, refresh)

	const getOriginalId = (row: any) => row._originalId ?? row.id;

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

    const actions = [
        {
            label: t('page.contingut.action.guardarArxiu.label'),
            icon: 'autorenew',
            showInMenu: true,
            onClick: (row:any) => guardarArxiu(getOriginalId(row), row),
            disabled: !entity?.arxiuPropagat,
            hidden: (row:any) => row?.isGroup || row?.arxiuPropagat && ( !row?.representant || row?.representantInfo?.arxiuPropagat ),
        },
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetail,
            hidden: (row:any) => row?.isGroup || entity?.potModificar,
        },
        {
            label: t('common.update'),
            icon: 'edit',
            showInMenu: true,
			clickShowUpdateDialog: true,
            hidden: (row:any) => row?.isGroup || !entity?.potModificar,
        },
		{
		    label: t('common.update'),
		    icon: 'edit',
		    showInMenu: (row:any) => true,
			onClick: (id:number, row:any) => handleModifyGrup(getOriginalId(row), row?.interessats),
		    hidden: (row:any) => !row?.isGroup,
		},
        {
            label: t('page.interessat.action.gestGrups.label'),
            icon: "groups",
            showInMenu: true,
            onClick: (value:any, row: any) => handleManageInteressatGrups(getOriginalId(row), row?.grups),
            hidden: (row:any) => row?.isGroup || !entity?.potModificar,
        },
		{
		    label: t('page.interessat.action.delete.label'),
		    icon: "delete",
		    showInMenu: true,
		    onClick: (row:any) => deleteInteressat(getOriginalId(row)),
		    hidden: (row:any) => row?.isGroup || !entity?.potModificar,
		},
		{
		    label: t('page.interessat.grup.action.delete.label'),
		    icon: 'delete',
		    showInMenu: (row:any) => true,
			onClick: (id:number, row:any) => deleteGrup(getOriginalId(row), row?.interessats),
		    hidden: (row:any) => !row?.isGroup || !entity?.potModificar,
		},
        {
            label: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row: any) => row?.isGroup || (row?.tipus == 'InteressatAdministracioEntity' || !entity?.potModificar),
        },
        {
            label: t('page.interessat.action.createRep.label'),
            icon: "add",
            showInMenu: true,
            onClick: (row:any) => createRepresentent(getOriginalId(row), row),
            hidden: (row: any) => row?.isGroup || (row?.representant || row?.tipus == 'InteressatAdministracioEntity' || !entity?.potModificar),
        },
        {
            label: t('page.interessat.action.updateRep.label'),
            icon: "edit",
            showInMenu: true,
            onClick: (row:any) => updateRepresentent(getOriginalId(row), row),
            hidden: (row: any) => row?.isGroup || (!row?.representant || row?.tipus == 'InteressatAdministracioEntity' || !entity?.potModificar),
        },
        {
            label: t('page.interessat.action.deleteRep.label'),
            icon: "delete",
            showInMenu: true,
            onClick: (row:any) => deleteRepresentent(getOriginalId(row)),
            hidden: (row: any) => row?.isGroup || (!row?.representant || row?.tipus == 'InteressatAdministracioEntity' || !entity?.potModificar),
        },
    ];

    const components=<>
        {content}
        {dialogDetail}
        {contentCreate}
        {contentImportarSGD}
		{contentGrup}
		{dialogManageInteressatGrups}
    </>;

    return {
        actions,
        createActions,
        components,
    }
}
export default useInteressatActions;