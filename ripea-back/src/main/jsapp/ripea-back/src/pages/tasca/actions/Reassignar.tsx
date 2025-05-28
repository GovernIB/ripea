import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const ReassignarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="usuaris" multiple required/>
    </Grid>
}

const Reassignar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientTascaResource"}
        title={t('page.tasca.action.reassignar')}
        action={'REASSIGNAR'}
        {...props}
    >
        <ReassignarForm/>
    </FormActionDialog>
}

const useReassignar = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, '', 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error.message, 'error');
    }

    return {
        handleShow,
        content: <Reassignar apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useReassignar;