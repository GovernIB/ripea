import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const ModificarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiu" type={"textarea"} required/>
    </Grid>
}

const Modificar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"carpetaResource"}
        action={"MODIFICAR_NOM"}
        title={t('page.carpeta.action.update.title')}
        {...props}
    >
        <ModificarForm/>
    </FormActionDialog>
}

const useModificar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = ((result:any) => {
        refresh?.()
        temporalMessageShow(null, t('page.carpeta.action.update.ok', {carpeta: result?.nom}), 'success');
    })

    return {
        handleShow,
        content: <Modificar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useModificar;