import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";

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

const useReobrir = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) => {
        // TODO: change
        // apiAction(id,{code:'ACTION_REABRIR'})
        return apiRef.current?.show?.(id, {data:{estat: 'PENDENT'}})
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
        content: <Reobrir apiRef={apiRef}/>
    }
}
export default useReobrir;