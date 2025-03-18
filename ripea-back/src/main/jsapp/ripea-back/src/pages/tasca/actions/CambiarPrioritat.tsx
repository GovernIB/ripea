import {MuiFormDialog} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";

const CambiarPrioritatForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaExpedientTasca" readOnly disabled/>
        <GridFormField xs={12} name="titol" disabled/>
        <GridFormField xs={12} name="prioritat" required/>
    </Grid>
}

const CambiarPrioritat = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientTascaResource"}
        title={`Modificar prioridad de la tarea`}
        apiRef={apiRef}
    >
        <CambiarPrioritatForm/>
    </MuiFormDialog>
}
export default CambiarPrioritat;