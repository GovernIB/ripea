import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const AmpliarPlacForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="diesAmpliacio" required/>
        <GridFormField xs={12} name="motiu"/>
    </Grid>
}

const AmpliarPlac = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentEnviamentInteressatResource"}
        action={"AMPLIAR_PLAC"}
        title={t('page.notificacioInteressat.action.ampliarPlac.title')}
        {...props}
    >
        <AmpliarPlacForm/>
    </FormActionDialog>
}

const useAmpliarPlac = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.notificacioInteressat.action.ampliarPlac.ok'), 'success');
    }

    return {
        handleShow,
        content: <AmpliarPlac apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useAmpliarPlac;