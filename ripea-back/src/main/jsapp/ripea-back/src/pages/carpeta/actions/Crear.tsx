import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useRef} from "react";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";

const CrearForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom"/>
    </Grid>
}

const Crear = (props:any) => {
    const { t } = useTranslation();

    return <MuiFormDialog
        resourceName={"carpetaResource"}
        resourceTitle={t('page.carpeta.title')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        {...props}
    >
        <CrearForm/>
    </MuiFormDialog>
}

const useCrear = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () => {
        apiRef.current?.show(undefined, {
            expedient: {id: entity?.id},
            expedientRelacionat: {id: entity?.id},
        })
            .then((result:any) => {
                refresh?.()
                temporalMessageShow(null, t('page.carpeta.action.new.ok', {carpeta: result?.nom}), 'success');
            })
            .catch((error:any) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {
        handleShow,
        content: <Crear apiRef={apiRef}/>,
    }
}
export default useCrear;