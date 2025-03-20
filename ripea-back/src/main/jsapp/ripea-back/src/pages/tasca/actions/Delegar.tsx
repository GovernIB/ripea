import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";

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

const useDelegar = (refresh?: () => void) => {
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
        content: <Delegar apiRef={apiRef}/>
    }
}
export default useDelegar;