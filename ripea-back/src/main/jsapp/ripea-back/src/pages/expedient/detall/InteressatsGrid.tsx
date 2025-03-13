import {
    GridPage,
    MuiFormDialog,
    MuiGrid,
    useMuiDataGridApiRef,
} from 'reactlib';
import {Grid} from "@mui/material";
import React from "react";
import GridFormField from "../../../components/GridFormField.tsx";
import useInteressatActions from "../actions/InteressatActions.tsx";

const InteressatsGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus" required/>
        <GridFormField xs={12} name="documentTipus" required/>
        <GridFormField xs={12} name="documentNum"/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={6} name="llinatge1"/>
        <GridFormField xs={6} name="llinatge2"/>
        <GridFormField xs={6} name="pais"/>
        <GridFormField xs={6} name="provincia"/>
        <GridFormField xs={6} name="municipi"/>
        <GridFormField xs={6} name="codiPostal"/>
        <GridFormField xs={12} name="adresa" type={"textarea"}/>
        <GridFormField xs={6} name="email"/>
        <GridFormField xs={6} name="telefon"/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
        <GridFormField xs={12} name="preferenciaIdioma" required/>
    </Grid>
}

interface DetailGridProps {
    id: any,
    onRowCountChange?: (number: number) => void,
}

const InteressatsGrid: React.FC<DetailGridProps> = (props: DetailGridProps) => {
    const {id, onRowCountChange} = props
    const apiRef = useMuiDataGridApiRef()
    const {actions, formApiRef} = useInteressatActions(id, apiRef?.current?.refresh)

    const columns = [
        {
            field: 'tipus',
            flex: 1,
        },
        {
            field: 'documentNum',
            flex: 0.5,
        },
        {
            field: 'nomComplet',
            flex: 0.5,
        },
        {
            field: 'representant',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
    ];

    return <GridPage>
        <MuiGrid
            resourceName="interessatResource"
            columns={columns}
            paginationActive
            apiRef={apiRef}
            filter={`expedient.id:${id} AND esRepresentant:false`}
            staticSortModel={[{field: 'id', sort: 'asc'}]}
            disableColumnSorting
            disableColumnMenu
            titleDisabled
            popupEditCreateActive
            popupEditFormContent={<InteressatsGridForm/>}
            formAdditionalData={{
                expedient: {
                    id: id
                },
            }}
            rowAdditionalActions={actions}
            onRowsChange={(rows) => onRowCountChange && onRowCountChange(rows.length)}
            rowHideDeleteButton
        />

        <MuiFormDialog
            resourceName={"interessatResource"}
            title={`Representante`}
            apiRef={formApiRef}
        >
            <InteressatsGridForm/>
        </MuiFormDialog>
    </GridPage>
}

export default InteressatsGrid;