import {Grid, Icon} from "@mui/material";
import GridFormField from "../../../../components/GridFormField.tsx";
import {StyledBadge} from "../../../../components/StyledBadge.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";

const MetaExpedientEstatForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="color" type={'color'}/>
        <GridFormField xs={12} name="inicial"/>
        <GridFormField xs={12} name="responsable"/>
    </Grid>
}

const sortModel: any = [{field: 'ordre', sort: 'asc'}]
const perspectives: string[] = [];
const columns = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'inicial',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.inicial && <Icon>check</Icon>),
    },
    {
        field: 'responsable',
        flex: 1,
    },
    {
        field: 'color',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.color && <StyledBadge badgecolor={params?.formattedValue} overlap="circular" badgeContent=" "/>)
    },
]
export const MetaExpedientEstatGrid = ({ entity, onRowCountChange } :any) => {
    const {t} = useTranslation()

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

    return <StyledMuiGrid
        resourceName={'metaExpedientEstatResource'}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.expedientEstat.title')}
        popupEditFormContent={<MetaExpedientEstatForm/>}
        columns={columns}
        filter={builder.eq("metaExpedient.id", entity?.id)}
        formAdditionalData={{ metaExpedient: {id: entity?.id} }}
        staticSortModel={sortModel}
        perspectives={perspectives}
        rowAdditionalActions={actions}
        onRowCountChange={onRowCountChange}

        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        toolbarCreateTitle={t('page.expedientEstat.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.expedientEstat.action.new.ok',
            updateSuccess: 'page.expedientEstat.action.update.ok',
            deleteSuccess: 'page.expedientEstat.action.delete.ok',
        }}
    />
}