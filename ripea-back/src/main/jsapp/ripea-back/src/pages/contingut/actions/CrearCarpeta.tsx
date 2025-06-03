import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useRef} from "react";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";

const CrearCarpetaForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom"/>
    </Grid>
}

const CrearCarpeta = (props:any) => {
    return <MuiFormDialog
        resourceName={"carpetaResource"}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        {...props}
    >
        <CrearCarpetaForm/>
    </MuiFormDialog>
}

const useCrearCarpeta = (entity:any, refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () => {
        apiRef.current?.show(undefined, { expedientRelacionat: {id: entity?.id} })
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error:any) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {
        handleShow,
        content: <CrearCarpeta apiRef={apiRef}/>,
    }
}
export default useCrearCarpeta;