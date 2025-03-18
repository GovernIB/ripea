import {MuiFormDialog} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";

const DelegarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="delegat" required/>
        <GridFormField xs={12} name="comentari"/>
    </Grid>
}

const Delegar = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientTascaResource"}
        title={`Delegar tarea`}
        apiRef={apiRef}
    >
        <DelegarForm/>
    </MuiFormDialog>
}
export default Delegar;