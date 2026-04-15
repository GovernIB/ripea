import {GridPage, useFormContext} from "reactlib";
import {Grid2 as Grid, Icon} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";

const ServeiPinbalGridForm = () => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name={'codi'} disabled={data?.id}/>
        <GridFormField name={'nom'} type={'textarea'}/>
        <GridFormField name={'pinbalServeiDocPermesEnum'} multiple/>
        <GridFormField name={'actiu'}/>
    </Grid>
}

const columns = [
    {
        field: 'codi',
        flex: 0.5,
    },
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'pinbalServeiDocPermesEnum',
        flex: 0.75,
    },
    {
        field: 'actiu',
        flex: 0.25,
        renderCell: (params: any) => params?.row?.actiu && <Icon>check</Icon>,
    },
]
const perspectives:any[] = [];
const sortModel:any[] = [{field: 'codi', sort: 'asc'}];

export const ServeiPinbalGrid = () => {
    const {t} = useTranslation()

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: false,
            clickShowUpdateDialog: true,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.pinbal')}>
            <StyledMuiGrid
                resourceName={"pinbalServeiResource"}
                popupEditFormDialogResourceTitle={t('page.pinbalServei.title')}
                columns={columns}
                sortModel={sortModel}
                perspectives={perspectives}
                popupEditCreateActive
                popupEditFormContent={<ServeiPinbalGridForm/>}
                rowAdditionalActions={actions}
                toolbarShowQuickFilter
                toolbarHideCreate
                popupEditFormI18nKeys={{
                    updateSuccess: 'page.entitat.action.update.ok',
                }}
            />
        </CardPage>
    </GridPage>
}