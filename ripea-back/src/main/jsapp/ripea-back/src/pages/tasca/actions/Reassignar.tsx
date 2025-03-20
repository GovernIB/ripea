import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";

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

const useReassignar = (refresh?: () => void) => {
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
        content: <Reassignar apiRef={apiRef}/>
    }
}
export default useReassignar;