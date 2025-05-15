import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import useNotificar from "./Notificar.tsx";

const NotificarMassiveForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaDocument" required/>
        <GridFormField xs={12} name="ntiOrigen" required/>
        <GridFormField xs={12} name="ntiEstadoElaboracion" required/>
    </Grid>
}

const NotificarMassive = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"MASSIVE_NOTIFICAR_ZIP"}
        title={t('page.document.action.notificarMasiva')}
        {...props}
    >
        <NotificarMassiveForm/>
    </FormActionDialog>
}

const useNotificarMassive = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const {handleShow: handleNotificar, content} = useNotificar(refresh)

    const handleMassiveShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, {
            ids: ids,
            massivo: true,
            expedient: {
                id: entity?.id,
                description: entity?.nom
            }
        })
    }
    const onSuccess = (result?: any) :void => {
        if (result?.id) {
            temporalMessageShow(null, t('page.document.dialog.generateZip'), 'success');
            handleNotificar(result?.id, result)
        }else {
            onError({message: ''})
        }
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error?.message, 'error');
    }

    return {
        handleMassiveShow,
        content: <>
            <NotificarMassive apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
            {content}
        </>
    }
}
export default useNotificarMassive;