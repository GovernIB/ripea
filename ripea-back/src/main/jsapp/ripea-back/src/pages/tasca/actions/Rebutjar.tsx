import {MuiFormDialog} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";

const RebutjarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiuRebuig" required/>
    </Grid>
}

const Rebutjar = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientTascaResource"}
        title={`Reasignar tarea`}
        apiRef={apiRef}
    >
        <RebutjarForm/>
    </MuiFormDialog>
}
export default Rebutjar;