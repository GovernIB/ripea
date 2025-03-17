import {MuiFormDialog} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import React from "react";

const ReassignarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="responsableActual" required/>
    </Grid>
}

const Reassignar = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientTascaResource"}
        title={`Reasignar tarea`}
        apiRef={apiRef}
    >
        <ReassignarForm/>
    </MuiFormDialog>
}
export default Reassignar;