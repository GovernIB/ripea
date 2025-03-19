import {MuiFormDialog, MuiFormDialogApi} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";

const AssignarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="agafatPer" required/>
    </Grid>
}

export const Assignar = (props: { apiRef:any }) => {
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={`Assignar expediente a usuario`}
        apiRef={apiRef}
    >
        <AssignarForm/>
    </MuiFormDialog>
}

const useAssignar = () => {
    const apiRef = useRef<MuiFormDialogApi>();
    const hanldeShow = (id:any) => {
        return apiRef.current?.show?.(id)
    }

    return {
        hanldeShow,
        content: <Assignar apiRef={apiRef}/>
    }
}

export default useAssignar;