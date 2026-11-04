import {useMemo} from "react";
import {GridPage, useFormContext} from "reactlib";
import {Alert, Grid, Icon} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";

const ServeiPinbalGridForm = () => {
    const {data} = useFormContext()
    const alta = data?.id == null

    // Memoritzat perquè el desplegable recarrega les opcions cada vegada que canvia la identitat de l'objecte.
    const serveiRequestParams = useMemo(() => ({
        unitatOrganitzativa: data?.unitatOrganitzativa,
    }), [data?.unitatOrganitzativa]);

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        {/* En l'alta el servei es tria entre els de PINBAL: el codi, el nom, els documents i l'actiu s'emplenen sols */}
        {alta && data?.avisPinbal &&
            <Grid size={12}>
                <Alert severity="warning">{data.avisPinbal}</Alert>
            </Grid>}
        {alta && <GridFormField name={'unitatOrganitzativa'} required/>}
        {alta && <GridFormField name={'serveiPinbal'} requestParams={serveiRequestParams} disabled={!data?.unitatOrganitzativa} required/>}
        <GridFormField name={'codi'} disabled/>
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

    return <GridPage autoHeight>
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
                popupEditFormI18nKeys={{
                    createSuccess: 'page.pinbalServei.action.new.ok',
                    updateSuccess: 'page.pinbalServei.action.update.ok',
                }}
            />
        </CardPage>
    </GridPage>
}