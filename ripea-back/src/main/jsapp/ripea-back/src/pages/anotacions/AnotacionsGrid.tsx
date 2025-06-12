import {GridPage, useFormContext, useMuiDataGridApiRef} from 'reactlib';
import {useTranslation} from "react-i18next";
import useAnotacioActions from "./details/AnotacioActions.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import {CardPage} from "../../components/CardData.tsx";
import AnotacioFilter from "./AnotacioFilter.tsx";
import Load from "../../components/Load.tsx";
import {useState} from "react";
import {Grid} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";

const sortModel:any = [{field: 'registre.data', sort: 'desc'}];
const perspectives = ['REGISTRE', 'ESTAT_VIEW'];
const namedQueries = ['LLISTAT_ANOTACIONS'];

const AnotacionsGridForm = () => {
    const {data} = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="identificador" disabled readOnly/>
        <GridFormField xs={12} name="metaExpedient"/>
        <GridFormField xs={12} name="grup"
                       namedQueries={[`BY_PROCEDIMENT#${data?.metaExpedient?.id}`]}
                       disabled={!data?.metaExpedient}
                       readOnly={!data?.metaExpedient}/>
    </Grid>
}

const AnotacionsGrid = () => {
    const { t } = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();
    const [load, setLoad] = useState<boolean>(false);
    const apiRef = useMuiDataGridApiRef();

    const columns = [
        {
            field: 'identificador',
            flex: 0.75,
        },
        {
            field: 'registreInfo.data',
            headerName: t('page.registre.grid.dataRecepcio'),
            flex: 0.75,
            valueFormatter: (value: any) => formatDate(value)
        },
        {
            field: 'registreInfo.extracte',
            headerName: t('page.registre.grid.extracte'),
            flex: 1,
        },
        {
            field: 'registreInfo.destiDescripcio',
            headerName: t('page.registre.grid.destiDescripcio'),
            flex: 0.5,
        },
        {
            field: 'metaExpedient',
            flex: 0.5,
        },
        {
            field: 'registreInfo.interessats',
            headerName: t('page.registre.grid.interessats'),
            flex: 0.5,
            valueFormatter: (value: any) => value.map((i:any)=>i?.description).join(", \n")
        },
        {
            field: 'grup',
            flex: 0.5,
        },
        {
            field: 'estat',
            flex: 0.5,
        },
        {
            field: 'dataActualitzacio',
            flex: 0.5,
        },
    ];

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = useAnotacioActions(refresh);

    return <GridPage>

        <CardPage title={t('page.anotacio.filter.title')}>
            <AnotacioFilter onSpringFilterChange={(value:any)=>{
                setSpringFilter(value)
                setLoad(true)
            }}/>

            <Load value={load} noEffect>
                <StyledMuiGrid
                    resourceName="expedientPeticioResource"
                    filter={springFilter}
                    staticSortModel={sortModel}
                    perspectives={perspectives}
                    columns={columns}
                    rowAdditionalActions={actions}
                    namedQueries={namedQueries}
                    apiRef={apiRef}

                    popupEditFormDialogTitle={t('page.anotacio.action.canviProcediment.title')}
                    popupEditUpdateActive
                    popupEditFormContent={<AnotacionsGridForm/>}
                    disableColumnSorting
                    toolbarHideCreate
                />
            </Load>
        </CardPage>

        {components}
    </GridPage>
}

export default AnotacionsGrid;