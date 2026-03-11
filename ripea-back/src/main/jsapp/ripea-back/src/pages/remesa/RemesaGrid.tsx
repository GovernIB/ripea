import {useMuiDataGridApiRef} from "reactlib";
import {Grid, Icon} from "@mui/material";
import useRemesaActions from "./details/RemesaActions.tsx";
import GridFormField from "../../components/GridFormField.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import * as builder from "../../util/springFilterUtils.ts";
import {useTranslation} from "react-i18next";
import useRemesaDetail from "./details/RemesaDetail.tsx";
import {StyledLabel} from "../../components/StyledLabel.tsx";

export const EstatMessage = (props:any) => {
    const {title, icon, color, children} = props;

    return <StyledLabel
        title={title || (typeof children === 'string' ?children :'')}
        sx={{ backgroundColor: `${color}.light`, color: 'white' }}
        icon={icon}
    >
        {children}
    </StyledLabel>
}
export const StyledEstat = (props:any) => {
    const { entity, children } = props;

    switch (entity?.notificacioEstat) {
        case 'PENDENT':
            return <>
                { entity?.error &&
                    <Icon fontSize={"inherit"} 
                        title={entity.errorDescripcio} 
                        sx={{ mr: children!=null  ?1 :0, color: 'error.light' }}>warning</Icon>
                }            
                <EstatMessage icon={"schedule"} color='warning'>{children}</EstatMessage>
            </>
        case 'REGISTRADA':
            if (entity?.error) {
                return <EstatMessage icon={"warning"} color={'error'}>{children}</EstatMessage>
            } else {
                return <EstatMessage icon={"info"} color={'info'}>{children}</EstatMessage>
            }
        case 'PROCESSADA':
        case 'ENVIADA':
            return <EstatMessage icon={"info"} color={'info'}>{children}</EstatMessage>
        case 'FINALITZADA':
            return <EstatMessage icon={"check"} color={'success'}>{children}</EstatMessage>
        case 'ENVIADA_AMB_ERRORS':
            if (entity?.error) {
                return <EstatMessage icon={"warning"} color={'error'}>{children}</EstatMessage>
            } else {
                return <EstatMessage icon={"check"} color={'success'}>{children}</EstatMessage>
            }
        case 'FINALITZADA_AMB_ERRORS':
            if (entity?.error) {
                return <EstatMessage icon={"warning"} color={'error'}>{children}</EstatMessage>
            } else {
                return <EstatMessage icon={"mail"} color={'info'}>{children}</EstatMessage>
            }
    }

    return <></>
}

const RemesaGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus"/>
        <GridFormField xs={12} name="estat" disabled readOnly/>
        {/*<GridFormField xs={12} name="interessats" multiple/>*/}
        <GridFormField xs={12} name="assumpte"/>
        <GridFormField xs={12} name="serveiTipusEnum"/>
        <GridFormField xs={12} name="observacions"/>
        <GridFormField xs={12} name="dataProgramada" type={'date'}/>

        {/*<GridFormField xs={12} name="caducitatDiesNaturals"/>*/}
        <GridFormField xs={12} name="dataCaducitat" type={'date'}/>

        <GridFormField xs={12} name="retard"/>
        <GridFormField xs={12} name="entregaPostal" disabled/>
    </Grid>
}

const sortModel:any = [{field: 'id', sort: 'desc'}];
const columns = [
    {
        field: 'tipus',
        flex: 0.7
    },
    {
        field: 'createdDate',
        flex: 0.6,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'dataEnviada',
        flex: 0.6,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'dataFinalitzada',
        flex: 0.6,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'assumpte',
        flex: 0.75,
    },
    {
        field: 'document',
        flex: 0.5,
    },
    {
        field: 'notificacioEstat',
        flex: 0.5,
        renderCell: (params:any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>
    },
]

const RemesaGrid = (props:any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation()

    const apiRef = useMuiDataGridApiRef()
    const refresh = () => { apiRef?.current?.refresh?.(); }

    const {actions, components} = useRemesaActions(entity, refresh);
    const {handleOpen, dialog} = useRemesaDetail();

    return <>
        <StyledMuiGrid
            resourceName="documentNotificacioResource"
            popupEditFormDialogResourceTitle={t('page.notificacio.title')}
            popupEditActive
            popupEditFormContent={<RemesaGridForm/>}
            filter={builder.eq('expedient.id', entity?.id)}
            staticSortModel={sortModel}
            columns={columns}
            rowAdditionalActions={actions}
            apiRef={apiRef}
            onRowCountChange={onRowCountChange}
            disableColumnSorting
            toolbarHideCreate
            onRowClick={(params: any) => handleOpen(params?.row?.id) }
            popupEditFormI18nKeys={{
                updateSuccess: 'page.notificacio.action.update.ok',
            }}

            paginationActive={false}
            autoHeight
        />
        {components}
        {dialog}
    </>
}
export default RemesaGrid;