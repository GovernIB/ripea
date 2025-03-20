import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";

const RetomarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiu"/>
    </Grid>
}

const Retomar = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientTascaResource"}
        title={`Retomar tarea`}
        apiRef={apiRef}
    >
        <RetomarForm/>
    </MuiFormDialog>
}

const useRetomar = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) => {
        // TODO: change
        // apiAction(id,{code:'ACTION_RETOMAR'})
        return apiRef.current?.show?.(id, {data:{delegat: null}})
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
        content: <Retomar apiRef={apiRef}/>
    }
}
export default useRetomar;