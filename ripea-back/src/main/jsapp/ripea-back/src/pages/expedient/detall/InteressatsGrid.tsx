import {
    GridPage,
    MuiFormDialog,
    MuiGrid,
} from 'reactlib';
import {useParams} from "react-router-dom";
import {Grid} from "@mui/material";
import React from "react";
import GridFormField from "../../../components/GridFormField.tsx";
import {DataFormDialogApi} from "../../../../lib/components/mui/datacommon/DataFormDialog.tsx";

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

const InteressatsGrid: React.FC = () => {
    const { id } = useParams();
    const formApiRef = React.useRef<DataFormDialogApi>()

    const createRepresentent = (rowId:any) => {
        formApiRef.current?.show(undefined, {
            expedient: {
                id: id
            },
            representat:{
                id: rowId
            }
        })
    }
    const updateRepresentent = (rowId:any, row:any) => {
        formApiRef.current?.show(row?.representant?.id, row?.representant)
    }

    const columns = [
        {
            field: 'documentTipus',
            flex: 0.5,
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
    const actions = [
        {
            title: "Añadir Representante",
            icon: "add",
            showInMenu: true,
            onClick: createRepresentent
        },
        {
            title: "Modificar Representante",
            icon: "edit",
            showInMenu: true,
            onClick: updateRepresentent,
            disabled: (row:any) => !row?.representant,
        },
    ];

    return <GridPage>
        <MuiGrid
            resourceName="interessatResource"
            columns={columns}
            paginationActive
            filter={`expedient.id:${id} AND representats is empty`}
            titleDisabled
            popupEditCreateActive
            popupEditFormContent={<InteressatsGridForm/>}
            formAdditionalData={{
                expedient: {
                    id: id
                },
            }}
            rowAdditionalActions={actions}
            // readOnly
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