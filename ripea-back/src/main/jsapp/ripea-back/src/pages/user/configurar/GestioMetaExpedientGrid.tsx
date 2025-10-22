import {useTranslation} from "react-i18next";
import {useState} from "react";
import {GridPage, useFormContext} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Grid, Icon} from "@mui/material";
import GridFormField, {GridButtonField} from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";

const GestioMetaExpedientFilterForm = () => {
    const { data } = useFormContext();
    return <>
        <GridFormField xs={3} name="codi"/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="classificacio"/>
        <GridFormField xs={3} name="actiu"/>
        <GridFormField xs={4} name="organGestor" disabled={data?.ambit == 'COMUNS'} readOnly={data?.ambit == 'COMUNS'}/>
        <GridFormField xs={4} name="ambit"/>
        <Grid item xs={1}/>
        <GridButtonField xs={0.6} name="permisDirecte" icon={"pan_tool_alt"}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.like('classificacio', data?.classificacio),
        builder.eq('organGestor.id', data?.organGestor?.id),
        builder.eq('actiu', data?.actiu),
        builder.eq('permisDirecte', data?.permisDirecte),
        builder.equals('organGestor.id', null, data?.ambit == 'COMUNS'),
    );
}

const GestioMetaExpedientFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"metaExpedientResource"}
        code={"FILTER_GESTIO"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <GestioMetaExpedientFilterForm/>
    </StyledMuiFilter>
}

// Grid
const sortModel: any = [{field: 'nom', sort: 'asc'}]
const columns = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'classificacio',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'serieDocumental',
        flex: 1,
    },
    {
        field: 'organGestor',
        flex: 1,
    },
    {
        field: 'comu',
        flex: 1,
        renderCell: (params:any) => (params?.row?.comu && <Icon>check</Icon>),
    },
    {
        field: 'permisDirecte',
        flex: 1,
        renderCell: (params:any) => (params?.row?.permisDirecte && <Icon>check</Icon>),
    },
    {
        field: 'gestioAmbGrupsActiva',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.gestioAmbGrupsActiva && <Icon>check</Icon>),
    },
    {
        field: 'actiu',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.actiu && <Icon>check</Icon>),
    },
]

const GestioGestioMetaExpedientGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.procediments')}>
            <GestioMetaExpedientFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"metaExpedientResource"}
                columns={columns}
                // TODO: revisar filtre
                filter={springFilter}
                sortModel={sortModel}

                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default GestioGestioMetaExpedientGrid;