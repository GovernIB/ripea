import {useTranslation} from "react-i18next";
import {GridPage} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Grid} from "@mui/material";
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
]

const TipusDocumentalGrid = () => {
    const {t} = useTranslation();

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
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