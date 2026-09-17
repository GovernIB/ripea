import {useTranslation} from "react-i18next";
import {GridPage, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Grid, Icon} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";

const TipusDocumentalForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="codi"/>
        <GridFormField name="nomEspanyol"/>
        <GridFormField name="nomCatala"/>
    </Grid>
}

const sortModel: any = [{field: 'codi', sort: 'asc'}]
const columns = [
    {
        field: 'codi',
        flex: 0.5,
    },
    {
        field: 'nomEspanyol',
        flex: 1,
    },
    {
        field: 'nomCatala',
        flex: 1,
    },
    {
        field: 'actiu',
        flex: 0.25,
        renderCell: (params: any) => params?.row?.actiu && <Icon>check</Icon>,
    },
]

const TipusDocumentalGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const {artifactAction: apiAction} = useResourceApiService('tipusDocumentalResource');
    const {temporalMessageShow} = useBaseAppContext();

    // Un tipus documental desactivat no es pot assignar a nous tipus de document, però es conserva per als que ja el tenen.
    const updateActiu = (id: any, code: 'ACTIVAR' | 'DESACTIVAR', okKey: string) => {
        apiAction(id, {code})
            .then(() => {
                apiRef?.current?.refresh?.();
                temporalMessageShow(null, t(okKey), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: t('page.tipusDocumental.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: (id: any) => updateActiu(id, 'ACTIVAR', 'page.tipusDocumental.action.activar.ok'),
            hidden: (row: any) => row?.actiu,
        },
        {
            label: t('page.tipusDocumental.action.desactivar.label'),
            icon: "close",
            showInMenu: true,
            onClick: (id: any) => updateActiu(id, 'DESACTIVAR', 'page.tipusDocumental.action.desactivar.ok'),
            hidden: (row: any) => !row?.actiu,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ]

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.nti')}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"tipusDocumentalResource"}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.tipusDocumental.title')}
                popupEditFormContent={<TipusDocumentalForm/>}
                columns={columns}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarShowQuickFilter
                toolbarCreateTitle={t('page.tipusDocumental.action.new.label')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.tipusDocumental.action.new.ok',
                    updateSuccess: 'page.tipusDocumental.action.update.ok',
                    deleteSuccess: 'page.tipusDocumental.action.delete.ok',
                }}
            />
        </CardPage>
    </GridPage>
}
export default TipusDocumentalGrid;