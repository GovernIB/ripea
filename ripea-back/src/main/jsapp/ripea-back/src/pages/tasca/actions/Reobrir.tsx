import {MuiFormDialog} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";

const ReobrirForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="responsable" required/>
        <GridFormField xs={12} name="motiu"/>
    </Grid>
}

const Reobrir = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientTascaResource"}
        title={`Reasignar tarea`}
        apiRef={apiRef}
    >
        <ReobrirForm/>
    </MuiFormDialog>
}
export default Reobrir;