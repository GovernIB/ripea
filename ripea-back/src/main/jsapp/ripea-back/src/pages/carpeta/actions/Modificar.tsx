import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const ModificarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom" required/>
    </Grid>
}

const Modificar = (props:any) => {
    const { t } = useTranslation();
    return <FormActionDialog
        resourceName={"carpetaResource"}
        action={"MODIFICAR_NOM"}
        title={t('page.carpeta.action.update.title')}
        formDialogButtons={[
            {icon: 'save', text: t('common.update'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <ModificarForm/>
    </FormActionDialog>
}

const useModificar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, row)
    }
    const onSuccess = ((data:any) => {
        refresh?.()
        temporalMessageShow(null, t('page.carpeta.action.update.ok', {data}), 'success');
    })

    return {
        handleShow,
        content: <Modificar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useModificar;