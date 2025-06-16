import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const EnviarViaEmailForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
		<GridFormField xs={12} name="versioDocument" required/>
	    <GridFormField xs={12} name="email" type={"text"}/>
        <GridFormField xs={12} name="responsables" multiple/>
    </Grid>
}

const EnviarViaEmail = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"ENVIAR_VIA_EMAIL"}
        title={t('page.document.action.mail.title')}
        {...props}
    >
        <EnviarViaEmailForm/>
    </FormActionDialog>
}

const useEnviarViaEmail = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = (result:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.mail.ok', {document: result?.nom}), 'success');
    }

    return {
        handleShow,
        content: <EnviarViaEmail apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useEnviarViaEmail;