import {useMuiFormDialogApiRef, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {FormReportDialog} from "../../../components/FormActionDialog.tsx";
import {useTranslation} from "react-i18next";
import {iniciaDescargaBlob} from "../details/CommonActions.tsx";

const ExportarDocumentsForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="carpetes"/>
        <GridFormField name="versioImprimible"/>
        <GridFormField name="nomFitxer" required/>
    </Grid>
}

const ExportarDocuments = (props:any) => {
    return <FormReportDialog
        resourceName={"expedientResource"}
        report={"EXPORT_DOC"}
        {...props}
    >
        <ExportarDocumentsForm/>
    </FormReportDialog>
}

export const useExportarDocuments = () => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        debugger;
        apiRef.current?.show?.(undefined, {ids: [id], massivo: false})
    }
    const onSuccess = (result:any) :void => {
        iniciaDescargaBlob(result);
        temporalMessageShow(null, t('page.expedient.action.export.ok'), 'success');
    }

    return {
        handleShow,
        content: <ExportarDocuments title={t('page.expedient.action.export.title')}
                                    formDialogButtons={[
                                        {icon: 'folder_zip', text: t('page.expedient.action.export.button'), componentProps: { variant: 'contained' }, value: true },
                                        {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
                                    ]}
                                    apiRef={apiRef}
                                    onSuccess={onSuccess}/>
    }
}

export const useExportarDocumentsMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleMassiveShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, {ids: ids, massivo: true})
    }
    const onSuccess = () :void => {
        refresh?.()
        // temporalMessageShow(null, t('page.expedient.action.exportZIP.ok'), 'success');
        temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'info');
    }

    return {
        handleMassiveShow,
        content: <ExportarDocuments title={t('page.expedient.action.exportZIP.title')}
                                    formDialogButtons={[
                                        {icon: 'download', text: t('page.expedient.action.exportZIP.button'), componentProps: { variant: 'contained' }, value: true },
                                        {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
                                    ]}
                                    apiRef={apiRef}
                                    onSuccess={onSuccess}/>
    }
}