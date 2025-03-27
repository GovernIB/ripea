import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const PublicarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus"/>
        <GridFormField xs={12} name="estat"/>
        <GridFormField xs={12} name="assumpte"/>
        <GridFormField xs={12} name="dataPublicacio"/>
        <GridFormField xs={12} name="dataEnviament"/>
        <GridFormField xs={12} name="descripcio" type={"textarea"}/>
    </Grid>
}

const Publicar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"PUBLICAR"}
        title={t('page.document.action.publicar')}
        {...props}
    >
        <PublicarForm/>
    </FormActionDialog>
}

const usePublicar = (refresh?: () => void) => {
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
        temporalMessageShow('Error', error.message, 'error');
    }

    return {
        handleShow,
        content: <Publicar apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default usePublicar;