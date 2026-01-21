import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Alert, Grid} from "@mui/material";
import GridFormField, {FileFormField} from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const ImportFitxerForm = () => {
    const {data} = useFormContext()
    const {t} = useTranslation()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <FileFormField xs={12} name={'importJson'} hidden={data?.importJson}/>
        {data?.importJson && <>
            <GridFormField xs={12} name={'procediment'}/>
            <GridFormField xs={12} name="codi"/>
            <GridFormField xs={2} name="tipusClassificacio" required/>
            <GridFormField xs={10} name="classificacio" debounce disabled={data?.tipusClassificacio == 'ID'}/>
            <Grid item xs={12} hidden={data?.msgSiaRolsac == null}>
                <Alert severity={'warning'} sx={{ mt: 0.5 }}>{data.msgSiaRolsac}</Alert>
            </Grid>
            <GridFormField xs={12} name="nom"/>
            <GridFormField xs={12} name="descripcio"/>
            <GridFormField xs={12} name="serieDocumental"/>
            <GridFormField xs={4} name="procedimentComu"/>
            <GridFormField xs={8} name="organGestor" required hidden={data?.procedimentComu}/>
            <GridFormField xs={12} name="expressioNumero"
                           componentProps={{ helperText: t('page.metaExpedient.detall.expressioNumero') }}/>
        </>}
    </Grid>
}

const ImportFitxer = (props: any) => {
    const { t } = useTranslation();

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
        <ImportFitxerForm/>
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