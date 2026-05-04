import {MuiFormDialogApi, useFormContext, useBaseAppContext} from "reactlib";
import {Grid, Alert} from "@mui/material";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import { FormReportDialog } from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {iniciaDescargaBlob} from "../details/CommonActions.tsx";

const ExportarExpedientsMassiveForm = () => {
    
    const {data} = useFormContext();
    const { value: user } = useUserSession();
    const { t } = useTranslation();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        {data.massivo  && <Alert severity={"info"}>{t('page.expedient.action.exportMass.info')}</Alert>}
        {!data.massivo && <Alert severity={"info"}>{t('page.expedient.action.exportMass.info2')}</Alert>}
        <GridFormField name="exportarExcel" hidden={!data.massivo}/>
        <GridFormField name="exportarCsv" hidden={!data.massivo}/>
        <GridFormField name="exportarIndexXls" hidden={!(user?.sessionScope?.isExportacioExcelActiva)}/>
        <GridFormField name="exportarIndexZip"/>
        <GridFormField name="exportarIndexPdf"/>
        <GridFormField name="exportarEni"/>
        <Grid size={1}/><GridFormField size={11} name="inlourerEstructEni" hidden={!data.exportarEni}/>
        <GridFormField name="exportarInside" hidden={!(user?.sessionScope?.isExportacioInsideActiva)}/>
    </Grid>
}

export const ExportarExpedientsMassive = (props:any) => {
    const { t } = useTranslation();

    return <FormReportDialog
        resourceName={"expedientResource"}
        report={"EXPORT_GENERIC"}
        formDialogButtons={[
            {icon: 'file_download', text: t('common.export'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        initialOnChange
        {...props}
    >
        <ExportarExpedientsMassiveForm/>
    </FormReportDialog>
}

export const useExportarExpedient = () => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(undefined, { ids: [id], massivo: false })
    }
    const onSuccess = (result:any) :void => {
        iniciaDescargaBlob(result);
    }
    return {
        handleShow,
        content: <ExportarExpedientsMassive apiRef={apiRef} onSuccess={onSuccess} title={t('page.expedient.action.exportMass.titleUni')}/>
    }
}

export const useExportarExpedientsMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
    const handleShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, { ids, massivo: true })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'info');
    }
    return {
        handleShow,
        content: <ExportarExpedientsMassive apiRef={apiRef} onSuccess={onSuccess} title={t('page.expedient.action.exportMass.title')}/>
    }
}