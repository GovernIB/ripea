import {MuiFormDialog} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";

const CambiarEstatForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom" readOnly/>
        <GridFormField xs={12} name="estat" required/>
    </Grid>
}

const CambiarEstat = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={`Modificar estado del expediente`}
        apiRef={apiRef}
    >
        <CambiarEstatForm/>
    </MuiFormDialog>
}
export default CambiarEstat;