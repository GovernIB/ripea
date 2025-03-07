import {
    GridPage,
    MuiGrid,
} from 'reactlib';
import {useParams} from "react-router-dom";
import {formatDate} from "../../../util/dateUtils.ts";
import {Button, Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import React from "react";

const TasquesGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaExpedientTasca"/>
        <GridFormField xs={12} name="metaExpedientTasca.descripcio" readOnly disabled/>
        <GridFormField xs={12} name="responsableActual"/>
        <GridFormField xs={12} name="observadors" multiple/>
        <GridFormField xs={6} name="duracio"/>
        <GridFormField xs={6} name="dataLimit" type={"date"} componentProps={{disablePast: true}}/>
        <GridFormField xs={12} name="titol"/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
        <GridFormField xs={12} name="prioritat" required/>
    </Grid>
}

const TasquesGrid: React.FC = () => {
    const { id } = useParams();

    const columns = [
        {
            field: 'metaExpedientTasca',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
        {
            field: 'dataInici',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return formatDate(value);
            }
        },
        {
            field: 'dataLimit',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return formatDate(value);
            }
        },
        {
            field: 'titol',
            flex: 0.5,
        },
        {
            field: 'observacions',
            flex: 0.5,
        },
        {
            field: 'responsablesStr',
            flex: 0.5,
        },
        {
            field: 'responsableActual',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
        {
            field: 'estat',
            flex: 0.5,
        },
        {
            field: 'prioritat',
            flex: 0.5,
        },
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            disableColumnMenu: true,
            flex: 0.5,
            valueFormatter: (value: any) => {
                return <Button>{value}</Button>;
            }
        },
    ];
    return <GridPage>
        <MuiGrid
            resourceName="expedientTascaResource"
            columns={columns}
            paginationActive
            filter={`expedient.id:${id}`}
            titleDisabled
            perspectives={["RESPONSABLES_RESUM"]}
            popupEditCreateActive
            popupEditFormContent={<TasquesGridForm/>}
            formAdditionalData={{
                expedient: {
                    id: id
                },
            }}
            // readOnly
        />
    </GridPage>
}

export default TasquesGrid;