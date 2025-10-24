import {useTranslation} from "react-i18next";
import {useState} from "react";
import {GridPage} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {Button, Grid, Icon} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";

const OrganGestorFilterForm = () => {
    return <>
        <GridFormField xs={2} name="codi"/>
        <GridFormField xs={2} name="nom"/>
        <GridFormField xs={3} name="organGestor"/>
        <GridFormField xs={2} name="estat"/>
        <Grid item xs={0.6}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.eq('pare.id', data?.organGestor?.id),
        builder.eq('estat', `'${data?.estat}'`),
    );
}

const OrganGestorFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"organGestorResource"}
        code={"FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <OrganGestorFilterForm/>
    </StyledMuiFilter>
}

// Grid
const OrganGestorForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi" disabled readOnly/>
        <GridFormField xs={12} name="nom" disabled readOnly/>
        <GridFormField xs={12} name="pare" disabled readOnly/>
        <GridFormField xs={12} name="cif"/>
        <GridFormField xs={12} name="utilitzarCifPinbal"/>
        <GridFormField xs={12} name="permetreEnviamentPostal"/>
    </Grid>
}

const sortModel: any = [{field: 'nom', sort: 'asc'}]
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
        field: 'pare',
        flex: 1,
    },
    {
        field: 'cif',
        flex: 1,
    },
    {
        field: 'estat',
        flex: 1,
    },
    {
        filed: 'permis',
        headerName: '',
        sortable: false,
        flex: 0.25,
        renderCell: (params:any) => <Button href={`/organgestor/${params?.id}/permis`} variant={'contained'}>
            <Icon>key</Icon>
        </Button>
    }
]

const OrganGestorGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.organs')}>
            <OrganGestorFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"organGestorResource"}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.organGestor.title')}
                popupEditFormContent={<OrganGestorForm/>}
                columns={columns}
                // TODO: revisar filtre
                filter={springFilter}
                sortModel={sortModel}

                // TODO: revisar accions
                rowAdditionalActions={actions}

                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <ToolbarButton
                            // title={t('common.create')}
                            icon={'cached'} onClick={()=>handelCreate(id)} color={'primary'}/>,
                    },
                ]}

                toolbarHideCreate
                toolbarHideRefresh
                popupEditFormI18nKeys={{
                    updateSuccess: 'page.organGestor.action.update.ok',
                }}
            />
        </CardPage>
    </GridPage>
}
export default OrganGestorGrid;