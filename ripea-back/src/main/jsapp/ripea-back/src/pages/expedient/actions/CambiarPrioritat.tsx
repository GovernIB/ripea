import {MuiFormDialog, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";

const CambiarPrioritatForm = () => {
    const formContext = useFormContext();
    const {data} = formContext;
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom" readOnly/>
        <GridFormField xs={12} name="prioritat" required/>
        <GridFormField xs={12} name="prioritatMotiu" hidden={data?.prioritat=='B_NORMAL'} required/>
    </Grid>
}

const CambiarPrioritat = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={`Modificar prioridad del expediente`}
        apiRef={apiRef}
    >
        <CambiarPrioritatForm/>
    </MuiFormDialog>
}
export default CambiarPrioritat;