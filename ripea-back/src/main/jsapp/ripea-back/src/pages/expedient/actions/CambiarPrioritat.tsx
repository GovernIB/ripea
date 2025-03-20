import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";

const CambiarPrioritatForm = () => {
    const formContext = useFormContext();
    const {data} = formContext;
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom" readOnly/>
        <GridFormField xs={12} name="prioritat" required/>
        <GridFormField xs={12} name="prioritatMotiu" hidden={data?.prioritat=='B_NORMAL'} required/>
    </Grid>
}

export const CambiarPrioritat = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={`Modificar prioridad del expediente`}
        apiRef={apiRef}
    >
        <CambiarPrioritatForm/>
    </MuiFormDialog>
}

const useCambiarPrioritat = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) => {
        return apiRef.current?.show?.(id)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }

    return {
        handleShow,
        content: <CambiarPrioritat apiRef={apiRef}/>
    }
}
export default useCambiarPrioritat;