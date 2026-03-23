import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useTranslation} from "react-i18next";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {useState} from "react";
import {CardPage} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField, {GridButtonField} from "../../../components/GridFormField.tsx";
import {useActions, useMassiveActions} from "../../anotacions/details/AnotacioActions.tsx";

const ActualitzarEstatAnotacioFilterFrom = () => {
    return <>
        <GridFormField size={{xs: 12, sm: 4, md: 3}} name="numero"/>
        <GridFormField size={{xs: 12, sm: 4, md: 3}} name="dataAltaInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 4, md: 3}} name="dataAltaFi" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 3, md: 2}} name="estat"/>
        <GridButtonField size={{xs: 12, sm: 3, md: 1}} icon={"warning"} name="nomesPendents"/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like("identificador", data?.numero),
        builder.betweenDates("dataAlta", data?.dataAltaInici, data?.dataAltaFi),
        builder.eq("estat", `'${data?.estat}'`),
        data.nomesPendents && builder.eq("pendentCanviEstatDistribucio", 'true'),
    );
}

const ActualitzarEstatAnotacioFilter = (props: any) => {
    const {onSpringFilterChange} = props;
    return <StyledMuiFilter
        resourceName={"expedientPeticioResource"}
        code={"ACTUALITZAR_ESTAT_FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
    >
        <ActualitzarEstatAnotacioFilterFrom/>
    </StyledMuiFilter>

}

const namedQuery: string[] = ['MASSIU_ANOTACIONS_ESTAT']
const sortModel: any = [{field: 'dataAlta', sort: 'desc'}]
const columns = [
    {
        field: 'identificador',
        flex: 1,
    },
    {
        field: 'dataAlta',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'dataActualitzacio',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'reintentsCanviEstatDistribucio',
        flex: 0.7,
    },
    {
        field: 'estat',
        flex: 1,
    },
]

const ActualitzarEstatAnotacioGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const { canviEstatDistribucio } = useActions(refresh)
    const { canviEstatDistribucio: canviMassiuEstatDis } = useMassiveActions(refresh)

    const actions = [
        {
            label: t('page.anotacio.action.canviEstatDistribucio.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: canviEstatDistribucio,
        },
    ]
    const massiveActions = [
        {
            label: t('page.anotacio.action.canviEstatDistribucio.label'),
            icon: "autorenew",
            showInMenu: false,
            onClick: canviMassiuEstatDis,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.expedientPeticioCanviEstatDistribucio')}>
            <ActualitzarEstatAnotacioFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"expedientPeticioResource"}
                columns={columns}
                filter={springFilter}
                sortModel={sortModel}
                namedQueries={namedQuery}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default ActualitzarEstatAnotacioGrid;