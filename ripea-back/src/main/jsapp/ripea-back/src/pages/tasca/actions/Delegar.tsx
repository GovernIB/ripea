import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const DelegarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="delegat" required/>
        <GridFormField xs={12} name="comentari"/>
    </Grid>
}

const Delegar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientTascaResource"}
        title={t('page.tasca.action.delegar.title')}
        action={'DELEGAR'}
        {...props}
    >
        <DelegarForm/>
    </FormActionDialog>
}

const useDelegar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.tasca.action.delegar.ok'), 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error.message, 'error');
    }

    return {
        handleShow,
        content: <Delegar apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useDelegar;