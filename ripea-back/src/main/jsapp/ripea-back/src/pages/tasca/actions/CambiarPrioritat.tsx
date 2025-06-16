import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const CambiarPrioritatForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={6} name="titol" disabled readOnly/>
        <GridFormField xs={6} name="metaExpedientTasca" disabled readOnly/>
        <GridFormField xs={12} name="prioritat" required/>
    </Grid>
}

const CambiarPrioritat = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientTascaResource"}
        title={t('page.tasca.action.changePrioritat.title')}
        action={'CHANGE_PRIORITAT'}
        {...props}
    >
        <CambiarPrioritatForm/>
    </FormActionDialog>
}

const useCambiarPrioritat = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {
            titol: row?.titol,
            metaExpedientTasca: row?.metaExpedientTasca,
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.tasca.action.changePrioritat.ok'), 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error.message, 'error');
    }

    return {
        handleShow,
        content: <CambiarPrioritat apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useCambiarPrioritat;