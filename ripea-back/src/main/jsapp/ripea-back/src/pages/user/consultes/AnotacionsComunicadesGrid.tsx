import {GridPage} from "reactlib";
import { CardPage } from "../../../components/CardData";
import {useState} from "react";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {Grid, Icon} from "@mui/material";
import GridFormField, {GridButtonField} from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";

const AnotacionsComunicadesFilterForm = () => {
    return <>
        <GridFormField xs={3} name="numRegistre"/>
        <GridFormField xs={3} name="estat"/>
        <GridFormField xs={3} name="dataAltaInici" type={"date"}/>
        <GridFormField xs={3} name="dataAltaFi" type={"date"}/>
        <Grid item xs={9}/>
        <GridButtonField xs={0.6} name={"nomesAmbErrors"} icon={"warning"}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('identificador', data?.numRegistre),
        builder.eq('estat', `'${data?.estat}'`),
        builder.betweenDates('dataAlta', data?.dataAltaInici, data?.dataAltaFi),
        data?.nomesAmbErrors && builder.eq('consultaWsError', true),
    );
}

const AnotacionsComunicadesFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"expedientPeticioResource"}
        code={"ANOTACIONS_COMUNICADES_FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <AnotacionsComunicadesFilterForm/>
    </StyledMuiFilter>
}

// Grid
const sortModel: any = [{field: 'dataAlta', sort: 'desc'}]
const columns = [
    {
        field: 'identificador',
        flex: 0.75,
    },
    {
        field: 'dataAlta',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'estat',
        flex: 0.5,
    },
    {
        field: 'consultaWsError',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.consultaWsError && <Icon>check</Icon>),
    },
    {
        field: 'consultaWsErrorDesc',
        flex: 1,
    },
    {
        field: 'consultaWsErrorDate',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'pendentCanviEstatDistribucio',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.pendentCanviEstatDistribucio && <Icon>check</Icon>),
    },
    {
        field: 'reintentsCanviEstatDistribucio',
        flex: 0.5,
    },
]
const namedQueries = ['LLISTAT_ANOTACIONS', 'CONSULTA_COMUNICADES'];
const AnotacionsComunicadesGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();

    const actions = [
        {
            label: t('page.user.menu.consultar'),
            icon: "autorenew",
            showInMenu: false,
        },
    ]
    const massiveActions = [
        {
            label: t('page.user.menu.consultar'),
            icon: "autorenew",
            showInMenu: false,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.comunicades')}>
            <AnotacionsComunicadesFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"expedientPeticioResource"}
                columns={columns}
                filter={springFilter}
                sortModel={sortModel}
                namedQueries={namedQueries}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default AnotacionsComunicadesGrid;