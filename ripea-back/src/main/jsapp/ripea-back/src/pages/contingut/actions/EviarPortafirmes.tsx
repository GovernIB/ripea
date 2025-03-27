import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const EviarPortafirmesForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiu"/>
        <GridFormField xs={12} name="prioritat"/>
        <GridFormField xs={12} name="annexos"/>
        <GridFormField xs={12} name="fluxFirma"/>
    </Grid>
}

const EviarPortafirmes = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"ENVIAR_PORTAFIRMES"}
        title={t('page.document.action.enviarPortafirmes')}
        {...props}
    >
        <EviarPortafirmesForm/>
    </FormActionDialog>
}

const useEviarPortafirmes = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {
            motiu: `Tramitació de l'expedient [${row?.expedient?.description}]`,
        })
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
        content: <EviarPortafirmes apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useEviarPortafirmes;