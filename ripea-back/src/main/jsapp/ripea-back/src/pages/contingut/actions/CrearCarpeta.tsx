import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useRef} from "react";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";

const CrearCarpetaForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom"/>
    </Grid>
}

const CrearCarpeta = (props:any) => {
    const { t } = useTranslation();

    return <MuiFormDialog
        resourceName={"carpetaResource"}
        title={t('page.document.action.crearCarpets.title')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        {...props}
    >
        <CrearCarpetaForm/>
    </MuiFormDialog>
}

const useCrearCarpeta = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () => {
        apiRef.current?.show(undefined, { expedientRelacionat: {id: entity?.id} })
            .then((result:any) => {
                refresh?.()
                temporalMessageShow(null, t('page.document.action.crearCarpets.ok', {carpeta: result?.nom}), 'success');
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