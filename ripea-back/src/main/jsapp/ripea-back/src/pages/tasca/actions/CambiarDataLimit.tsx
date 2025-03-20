import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";

const CambiarFechaLimiteForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="dataInici" type={"date"} readOnly disabled/>
        <GridFormField xs={6} name="duracio"/>
        <GridFormField xs={6} name="dataLimit" type={"date"} componentProps={{disablePast: true}}/>
    </Grid>
}

const CambiarDataLimit = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientTascaResource"}
        title={`Cambiar fecha limite`}
        apiRef={apiRef}
    >
        <CambiarFechaLimiteForm/>
    </MuiFormDialog>
}

const useCambiarDataLimit = (refresh?: () => void) => {
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
        content: <CambiarDataLimit apiRef={apiRef}/>
    }
}
export default useCambiarDataLimit;