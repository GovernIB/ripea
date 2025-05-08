import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {Grid, Typography, Icon} from "@mui/material";
import useRemesaActions from "./details/RemesaActions.tsx";
import GridFormField from "../../components/GridFormField.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import * as builder from "../../util/springFilterUtils.ts";

const commonStyle = {p: 0.5, display: 'flex', alignItems: 'center', borderRadius: '5px', width: 'max-content'}
const EstatMessage = (props:any) => {
    const {icon, color, children} = props;

    return <Typography variant="caption" sx={{ ...commonStyle, backgroundColor: color }}>
        <Icon fontSize={"inherit"}>{icon}</Icon>
        {children}
    </Typography>
}
const StyledEstat = (props:any) => {
    const { entity } = props;

    const error = '#ef5350';
    const success = '#4caf50';
    const warning = '#ff9800';
    const info = '#03a9f4';

    switch (entity?.notificacioEstat) {
        case 'PENDENT':
            return <>
                <EstatMessage icon={"schedule"} color={warning}>{entity?.notificacioEstat}</EstatMessage>
                { entity?.error &&
                    <EstatMessage icon={"warning"} color={error}>Error procesando la notificación dentro Notib</EstatMessage>
                }
            </>
        case 'REGISTRADA':
        case 'FINALITZADA':
        case 'PROCESSADA':
        case 'ENVIADA_AMB_ERRORS':
            if (entity?.error) {
                return <EstatMessage icon={"warning"} color={error}>{entity?.notificacioEstat}</EstatMessage>
            } else {
                return <EstatMessage icon={"check"} color={success}>{entity?.notificacioEstat}</EstatMessage>
            }
        case 'ENVIADA':
        case 'FINALITZADA_AMB_ERRORS':
            if (entity?.error) {
                return <EstatMessage icon={"warning"} color={error}>{entity?.notificacioEstat}</EstatMessage>
            } else {
                return <EstatMessage icon={"mail"} color={info}>{entity?.notificacioEstat}</EstatMessage>
            }
    }

    return <></>
}

const RemesaGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus"/>
        <GridFormField xs={12} name="estat" disabled readOnly/>
        <GridFormField xs={12} name="interessats" multiple/>
        <GridFormField xs={12} name="assumpte"/>
        <GridFormField xs={12} name="serveiTipusEnum"/>
        <GridFormField xs={12} name="observacions"/>
        <GridFormField xs={12} name="dataProgramada" type={'date'}/>

        <GridFormField xs={12} name="caducitatDiesNaturals"/>
        <GridFormField xs={12} name="dataCaducitat" type={'date'}/>

        <GridFormField xs={12} name="retard"/>
        <GridFormField xs={12} name="entregaPostal" disabled/>
    </Grid>
}

const sortModel = [{field: 'id', sort: 'asc'}];
const columns = [
    {
        field: 'tipus',
        flex: 0.5
    },
    {
        field: 'createdDate',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'dataEnviada',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'dataFinalitzada',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'assumpte',
        flex: 0.5,
    },
    {
        field: 'document',
        flex: 0.5,
    },
    {
        field: 'notificacioEstat',
        flex: 0.5,
        renderCell: (params:any) => <StyledEstat entity={params?.row}/>
    },
]

const RemesaGrid = (props:any) => {
    const { id, onRowCountChange } = props;

    const apiRef = useMuiDataGridApiRef()
    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = useRemesaActions(refresh);

    return <GridPage>
        <StyledMuiGrid
            resourceName="documentNotificacioResource"
            popupEditActive
            popupEditFormContent={<RemesaGridForm/>}
            filter={builder.and(builder.eq('expedient.id', id))}
            staticSortModel={sortModel}
            // perspectives={['']}
            columns={columns}
            rowAdditionalActions={actions}
            paginationActive
            apiRef={apiRef}
            onRowsChange={(rows:any, info:any) => onRowCountChange?.(info?.totalElements)}
            disableColumnSorting
            toolbarHideCreate
            rowHideUpdateButton={(row:any) => row.tipus != 'MANUAL'}
            rowHideDeleteButton={(row:any) => row.tipus != 'MANUAL'}
        />
        {components}
    </GridPage>
}
export default RemesaGrid;