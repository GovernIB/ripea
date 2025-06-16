import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const ReobrirForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="responsables" multiple required/>
        <GridFormField xs={12} name="motiu"/>
    </Grid>
}

const Reobrir = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientTascaResource"}
        action={"REABRIR"}
        title={t('page.tasca.action.reobrir.title')}
        {...props}
    >
        <ReobrirForm/>
    </FormActionDialog>
}

const useReobrir = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id,{responsableActual:row?.responsableActual})
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.tasca.action.reobrir.ok'), 'success');
    }

    return {
        handleShow,
        content: <Reobrir apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useReobrir;