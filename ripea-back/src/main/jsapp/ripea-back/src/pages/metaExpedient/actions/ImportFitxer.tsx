import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField, {FileFormField} from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {MetaExpedientForm} from "../MetaExpedientGrid.tsx";
import {useUserSession} from "../../../components/Session.tsx";

const ImportFitxerForm = ({ isAdmin }: any) => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <FileFormField xs={12} name={'importJson'} hidden={data?.importJson}/>
        {data?.importJson && <>
            <GridFormField xs={12} name={'procediment'}/>
            <Grid item xs={12}><MetaExpedientForm isAdmin={isAdmin}/></Grid>
        </>}
    </Grid>
}

const ImportFitxer = (props: any) => {
    const { t } = useTranslation();
    const {value: user} = useUserSession();

    return <FormActionDialog
        resourceName={"metaExpedientResource"}
        title={t('page.metaExpedient.action.importFitxer.title')}
        action={"IMPORT_FITXER"}
        // formDialogButtons={[
        //     {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
        //     {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        // ]}
        {...props}
    >
        <ImportFitxerForm isAdmin={user?.rolActual === 'IPA_ADMIN'}/>
    </FormActionDialog>
}

export const useImportFitxer = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () :void => {
        apiRef.current?.show?.()
    }
    const onSuccess = (response:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.metaExpedient.action.importFitxer.ok', {data: response}), 'success');
    }

    return {
        handleShow,
        content: <ImportFitxer apiRef={apiRef} onSuccess={onSuccess}/>
    }
}