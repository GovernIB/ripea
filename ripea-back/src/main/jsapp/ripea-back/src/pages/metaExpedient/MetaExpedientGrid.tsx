import {useTranslation} from "react-i18next";
import {useState} from "react";
import {GridPage} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Typography} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import {formatDate} from "../../util/dateUtils.ts";

const MetaExpedientFilterForm = () => {
    return <>
        <GridFormField xs={4} name="codi"/>
        <GridFormField xs={4} name="classificacio"/>
        <GridFormField xs={4} name="nom"/>
        <GridFormField xs={4} name="revisioEstat"/>
        <GridFormField xs={4} name="organGestor"/>
        <Grid item xs={1.6}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('classificacio', data?.classificacio),
        builder.like('nom', data?.nom),
        builder.eq('revisioEstat', `'${data?.revisioEstat}'`),
        builder.eq('organGestor.id', data?.organGestor?.id),
    );
}

const MetaExpedientFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"metaExpedientResource"}
        code={"FILTER_REVISIO"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <MetaExpedientFilterForm/>
    </StyledMuiFilter>
}

// Grid
const labelStyle = {padding: '1px 4px', fontSize: '11px', fontWeight: '500', borderRadius: '2px', display: 'flex', alignItems: 'center', width: 'max-content'}
const obertStyle = {border: '1px dashed #AAA'}

const StyledEstat = (props:any) => {
    const { entity, children } = props;
    // const { t } = useTranslation()

    let style: any = {};
    switch (entity?.revisioEstat) {
        case 'PENDENT':
            style = {backgroundColor: '#ffebae'}
            break;
        case 'REVISAT':
            style = {backgroundColor: '#c3e8d1'}
            break;
        case 'REBUTJAT':
            style = {backgroundColor: '#d99b9d', color: 'white'}
            break;
        case 'DISSENY':
            style = obertStyle
            break;
    }

    return <Typography variant="caption" sx={{...labelStyle, ...style}}>{children}</Typography>
}

const sortModel: any = [{field: 'lastModifiedDate', sort: 'desc'}]
const columns = [
    {
        field: 'codi',
        flex: 0.75,
    },
    {
        field: 'classificacio',
        flex: 0.75,
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
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.comu && <Icon>check</Icon>),
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
    {
        field: 'revisioEstat',
        flex: 0.75,
        renderCell: (params:any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>
    },
    {
        field: 'lastModifiedByFullName',
        flex: 1,
    },
    {
        field: 'lastModifiedDate',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const MetaExpedientGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.revisar')}>
            <MetaExpedientFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"metaExpedientResource"}
                columns={columns}
                // TODO: revisar filtre
                filter={springFilter}
                sortModel={sortModel}

                // TODO: revisar actions
                // rowAdditionalActions={actions}
                // toolbarMassiveActions={massiveActions}

                readOnly
            />
        </CardPage>
    </GridPage>
}
export default MetaExpedientGrid;