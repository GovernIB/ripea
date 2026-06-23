import {GridPage, useFormContext, useMuiDataGridApiRef} from 'reactlib';
import {useTranslation} from "react-i18next";
import useAnotacioActions from "./details/AnotacioActions.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import {CardPage} from "../../components/CardData.tsx";
import AnotacioFilter from "./AnotacioFilter.tsx";
import Load from "../../components/Load.tsx";
import {useMemo, useState} from "react";
import {Box, Grid} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import useAnotacioDetail from "./details/AnotacioDetail.tsx";
import {useUserSession} from "../../components/Session.tsx";

const sortModel:any = [{field: 'registreInfo.data', sort: 'desc'}];
const perspectives = ['REGISTRE', 'ESTAT_VIEW'];
const namedQueries = ['LLISTAT_ANOTACIONS'];

const AnotacionsGridForm = () => {
    const {data} = useFormContext();
    const { value: user } = useUserSession();

    const mostrarGrups = user?.sessionScope?.isFiltreGrupsVisible;
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="identificador" disabled readOnly/>
        <GridFormField name="registreExtracte" disabled readOnly/>
        <GridFormField name="metaExpedient"/>
        <GridFormField name="grup"
                       namedQueries={[`BY_PROCEDIMENT#${data?.metaExpedient?.id}`]}
                       disabled={!data?.metaExpedient}
                       readOnly={!data?.metaExpedient}
                       hidden={!mostrarGrups}/>
    </Grid>
}

const AnotacionsGrid = () => {
    const { t } = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();
    const [load, setLoad] = useState<boolean>(false);
    const apiRef = useMuiDataGridApiRef();

    const columns = useMemo(() => [
        {
            field: 'identificador',
            flex: 0.75,
        },
        {
            field: 'registreInfo.data',
            headerName: t('page.registre.grid.dataRecepcio'),
            flex: 0.75,
            valueFormatter: (value: any) => formatDate(value),
            sortProcessor: (_field: string, sort: GridSortDirection) => {
                return [{field: 'registre.data', sort}];
            },
        },
        {
            field: 'registreInfo.extracte',
            headerName: t('page.registre.grid.extracte'),
            flex: 1,
            sortProcessor: (_field: string, sort: GridSortDirection) => {
                return [{field: 'registre.extracte', sort}];
            },
        },
        {
            field: 'registreInfo.destiDescripcio',
            headerName: t('page.registre.grid.destiDescripcio'),
            flex: 0.5,
            sortProcessor: (_field: string, sort: GridSortDirection) => {
                return [{field: 'registre.destiDescripcio', sort}];
            },
        },
        {
            field: 'metaExpedient',
            sortable: false,
            flex: 0.5,
        },
        {
            field: 'registreInfo.interessats',
            headerName: t('page.registre.grid.interessats'),
            sortable: false,
            flex: 0.5,
            valueFormatter: (value: any) => value.map((i:any)=>i?.description).join(", \n"),
            renderCell: (params: any)=> (
                <Box sx={{ whiteSpace: 'pre-line' }} title={params?.formattedValue}>
                    {params?.formattedValue}
                </Box>
            ),
        },
        {
            field: 'grup',
            sortable: false,
            flex: 0.5,
        },
        {
            field: 'estat',
            sortable: false,
            flex: 0.5,
        },
        {
            field: 'dataActualitzacio',
            sortable: false,
            flex: 0.5,
            valueFormatter: (value: any) => formatDate(value)
        },
    ], [t]);

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = useAnotacioActions(refresh);
    const {handleOpen, dialog} = useAnotacioDetail();

    return <GridPage autoHeight>
        <CardPage title={t('page.anotacio.filter.title')}>
            <AnotacioFilter onSpringFilterChange={(value:any)=>{
                setSpringFilter(value)
                setLoad(true)
            }}/>

            <Load value={load} noEffect>
                <StyledMuiGrid
                    resourceName="expedientPeticioResource"
                    filter={springFilter}
                    sortModel={sortModel}
                    perspectives={perspectives}
                    columns={columns}
                    rowAdditionalActions={actions}
                    namedQueries={namedQueries}
                    apiRef={apiRef}
                    paginationActive
                    popupEditUpdateActive
                    popupEditFormContent={<AnotacionsGridForm/>}
                    toolbarHide
                    popupEditFormDialogTitle={t('page.anotacio.action.canviProcediment.title')}
                    popupEditFormComponentProps={{ initOnChangeRequest: true }}
                    popupEditFormI18nKeys={{
                        updateSuccess: 'page.anotacio.action.canviProcediment.ok',
                    }}
                    onRowClick={(params: any) => handleOpen(params?.row?.id, params?.row) }
                />
            </Load>
            {components}
            {dialog}
        </CardPage>
    </GridPage>
}

export default AnotacionsGrid;