import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

export const ClonarProcedimentForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={12} name="classificacio"/>
    </Grid>
}

const ClonarProcediment = (props: any) => {
    const {t} = useTranslation();

    return <FormActionDialog
        resourceName={"metaExpedientResource"}
        action={"CLONAR"}
        title={t('page.metaExpedient.action.clonar.title')}
        initialOnChange
        formDialogButtons={[
            {icon: 'content_copy', text: t('page.metaExpedient.action.clonar.label'), componentProps: {variant: 'contained'}, value: true},
            {text: t('common.cancel'), componentProps: {variant: 'outlined'}, value: false},
        ]}
        formDialogComponentProps={{maxWidth: 'sm', fullWidth: true}}
        {...props}
    >
        <ClonarProcedimentForm/>
    </FormActionDialog>
}

const useClonarProcediment = (refresh?: () => void) => {
    const {t} = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id: any) => {
        apiRef.current?.show?.(id);
    }

    const onSuccess = (result: any) => {
        refresh?.();
        temporalMessageShow(null, t('page.metaExpedient.action.clonar.ok', {codi: result?.codi}), 'success');
    }

    return {
        handleShow,
        content: <ClonarProcediment apiRef={apiRef} onSuccess={onSuccess}/>
    }
}

export default useClonarProcediment;
