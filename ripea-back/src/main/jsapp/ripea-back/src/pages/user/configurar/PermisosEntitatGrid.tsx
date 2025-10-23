import {useTranslation} from "react-i18next";
import {
    GridPage,
    MuiFormDialogApi,
    useBaseAppContext,
    useConfirmDialogButtons, useFormContext,
    useMuiDataGridApiRef,
    useResourceApiService
} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {Grid, Icon} from "@mui/material";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {useRef} from "react";
import GridFormField from "../../../components/GridFormField.tsx";

const useAction = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
    } = useResourceApiService('aclSidResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const eliminar = (id:any, data:any) :void => {
        messageDialogShow(
            t('page.permision.action.delete.check'),
            t('page.permision.action.delete.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    // classType
                    // objectId
                    apiAction(id, {code: 'DELETE_PERMISION', data})
                        .then((result) => {
                            refresh?.();
                            temporalMessageShow(null, t('page.permision.action.delete.ok', {data: result}), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }
    return {
        eliminar
    }
}

// Form
const PermisosEntitatModifyForm = () => {
    const { data } = useFormContext();
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="principal" disabled={!!data?.id} readOnly={!!data?.id} required/>
        <GridFormField xs={12} name="sid" disabled={!!data?.id} readOnly={!!data?.id}/>
        <GridFormField xs={12} name="admin"/>
        <GridFormField xs={12} name="adminLectura"/>
        <GridFormField xs={12} name="user"/>
    </Grid>
}
const PermisosEntitatModify = (props:any) => {
    return <FormActionDialog
        resourceName={"aclSidResource"}
        action={"MODIFY_PERMISION"}
        initialOnChange
        {...props}
    >
        <PermisosEntitatModifyForm/>
    </FormActionDialog>
}
const usePermisosEntitatCreate = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () :void => {
        apiRef.current?.show?.(undefined, {
            classType: 'ENTITY',
        })
    }
    const onSuccess = (result:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.permision.action.update.ok', {data: result}), 'success');
    }

    return {
        handleShow,
        content: <PermisosEntitatModify apiRef={apiRef} onSuccess={onSuccess}
                                        title={t('page.permision.action.new.title')}
                                        formDialogButtons={[
                                            {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
                                            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
                                        ]}/>
    }
}
const usePermisosEntitatModify = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(undefined, {
            ...row,
            classType: 'ENTITY',
        })
    }
    const onSuccess = (result:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.permision.action.update.ok', {data: result}), 'success');
    }

    return {
        handleShow,
        content: <PermisosEntitatModify apiRef={apiRef} onSuccess={onSuccess}
                                        title={t('page.permision.action.update.title')}
                                        formDialogButtons={[
                                            {icon: 'save', text: t('common.update'), componentProps: { variant: 'contained' }, value: true },
                                            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
                                        ]}/>
    }
}

// Grid
const sortModel: any = [{field: 'principal', sort: 'asc'}]
const columns = [
    {
        field: 'principal',
        flex: 1,
    },
    {
        field: 'sid',
        flex: 1,
    },
    {
        field: 'admin',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.admin && <Icon>check</Icon>),
    },
    {
        field: 'adminLectura',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.adminLectura && <Icon>check</Icon>),
    },
    {
        field: 'user',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.user && <Icon>check</Icon>),
    },
]

const PermisosEntitatGrid = ()=> {
    const {t} = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();

    const refresh = () => {
        gridApiRef?.current?.refresh?.();
    }

    const { eliminar } = useAction(refresh)
    const { handleShow: handelCreate, content: contentCreate } = usePermisosEntitatCreate(refresh);
    const { handleShow: handelModify, content: contentModify } = usePermisosEntitatModify(refresh);
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            onClick: handelModify,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: (id:any) => eliminar(id, {classType: 'ENTITY'}),
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.permisos')}>
            <StyledMuiGrid
                apiRef={gridApiRef}
                resourceName={"aclSidResource"}
                popupEditUpdateActive
                columns={columns}
                sortModel={sortModel}
                namedQueries={['ENTITY']}
                perspectives={['PERMISION#ENTITY']}

                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <ToolbarButton title={t('common.create')} icon={'add'} onClick={()=>handelCreate()} color={'primary'}/>,
                    },
                ]}
                toolbarHideCreate
                // popupEditFormI18nKeys={{
                //     createSuccess: 'page.permision.action.new.ok',
                //     updateSuccess: 'page.permision.action.update.ok',
                //     deleteSuccess: 'page.permision.action.delete.ok',
                // }}
            />
        </CardPage>
        {contentCreate}
        {contentModify}
    </GridPage>
}
export default PermisosEntitatGrid;