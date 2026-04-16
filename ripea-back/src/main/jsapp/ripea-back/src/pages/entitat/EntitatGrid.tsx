import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {Badge, Grid2 as Grid, Icon, Typography} from "@mui/material";
import LinkIcon from "../../components/LinkIcon.tsx";
import GridFormField, {FileFormField} from "../../components/GridFormField.tsx";
import {useEntitatActions} from "./details/EntitatActions.tsx";

const EntitatGridForm = () => {
    const {t} = useTranslation()
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name={'codi'} disabled={data?.id}/>
        <GridFormField name={'nom'}/>
        <GridFormField name={'cif'}/>
        <GridFormField name={'unitatArrel'}/>
        <GridFormField name={'permetreEnviamentPostal'}/>

        <Grid size={12}><Typography sx={{ borderBottom: '1px solid gray', mt: 2, mb: 1 }}>{t('page.entitat.form.temaClar')}</Typography></Grid>
        <FileFormField name={'logoImgFile'}/>
        <FileFormField name={'faviconImgFile'}/>
        <FileFormField name={'menuImgFile'}/>
        <GridFormField name={'capsaleraColorFons'} type={'color'}/>
        <GridFormField name={'capsaleraColorLletra'} type={'color'}/>

        <Grid size={12}><Typography sx={{ borderBottom: '1px solid gray', mt: 2, mb: 1 }}>{t('page.entitat.form.temaFosc')}</Typography></Grid>
        <FileFormField name={'blackLogoImgFile'}/>
        <FileFormField name={'blackFaviconImgFile'}/>
        <FileFormField name={'blackMenuImgFile'}/>
        <GridFormField name={'blackCapsaleraColorFons'} type={'color'}/>
        <GridFormField name={'blackCapsaleraColorLletra'} type={'color'}/>
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
        field: 'cif',
        flex: 1,
    },
    {
        field: 'activa',
        flex: 0.5,
        renderCell: (params: any) => params?.row?.activa && <Icon>check</Icon>,
    },
    {
        field: 'numPermisos',
        headerName: '',
        sortable: false,
        flex: 0.25,
        // hidden: !(rol?.isAdmin || rol?.isOrganAdmin),
        renderCell: (params:any) => <LinkIcon
            aria-label="key"
            color="inherit"
            title="Permisos"
            to={`/entitat/${params?.row?.id}/permis`}
        >
            <Badge badgeContent={params?.row?.numPermisos} color="primary" showZero>
                <Icon>key</Icon>
            </Badge>
        </LinkIcon>
    },
]
const perspectives:any[] = ['PERMISOS'];
const sortModel:any[] = [{field: 'nom', sort: 'asc'}];

export const EntitatGrid = () => {
    const {t} = useTranslation()
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = useEntitatActions(refresh)

    return <GridPage disableMargins>
        <CardPage title={t('navigate.entitat')}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"entitatResource"}
                popupEditFormDialogResourceTitle={t('page.entitat.title')}
                columns={columns}
                sortModel={sortModel}
                perspectives={perspectives}
                popupEditCreateActive
                popupEditFormContent={<EntitatGridForm/>}
                rowAdditionalActions={actions}
                toolbarShowQuickFilter
                toolbarCreateTitle={t('page.entitat.action.new.label')}
                onRefresh={refresh}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.entitat.action.new.ok',
                    updateSuccess: 'page.entitat.action.update.ok',
                    deleteSuccess: 'page.entitat.action.delete.ok',
                }}
            />
            {components}
        </CardPage>
    </GridPage>
}