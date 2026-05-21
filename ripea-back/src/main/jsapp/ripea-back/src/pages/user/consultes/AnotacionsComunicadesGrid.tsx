import {GridPage, useMuiDataGridApiRef} from "reactlib";
import { CardPage } from "../../../components/CardData";
import {useState} from "react";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {Icon} from "@mui/material";
import GridFormField, {GridButtonField} from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {useActions, useMassiveActions} from "../../anotacions/details/AnotacioActions.tsx";

const AnotacionsComunicadesFilterForm = () => {
    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 2.15}} name="numRegistre"/>
        <GridFormField size={{xs: 12, sm: 6, md: 2.15}} name="estat"/>
        <GridFormField size={{xs: 12, sm: 6, md: 2.15}} name="dataAltaInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 2.15}} name="dataAltaFi" type={"date"}/>
        <GridButtonField size={{xs: 12, sm: 2, md: 1}} name={"nomesAmbErrors"} icon={"warning"}/>
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
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {consultar} = useActions(refresh);
    const {consultar: consultarMassive} = useMassiveActions(refresh);

    const actions = [
        {
            label: t('page.anotacio.action.consultar.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: consultar,
        },
    ]
    const massiveActions = [
        {
            label: t('page.anotacio.action.consultar.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: consultarMassive,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.comunicades')}>
            <AnotacionsComunicadesFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
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