import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const PublicarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus" required/>
        <GridFormField xs={12} name="estat" required/>
        <GridFormField xs={12} name="assumpte"/>
        <GridFormField xs={12} name="dataPublicacio" type={"date"}/>
        <GridFormField xs={12} name="enviatData" type={"date"}/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
    </Grid>
}

const Publicar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"PUBLICAR"}
        title={t('page.document.action.publicar.title')}
        {...props}
    >
        <PublicarForm/>
    </FormActionDialog>
}

const usePublicar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.publicar.ok'), 'success');
    }

    return {
        handleShow,
        content: <Publicar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default usePublicar;