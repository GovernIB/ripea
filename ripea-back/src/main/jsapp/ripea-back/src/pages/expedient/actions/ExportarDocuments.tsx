import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import {useRef} from "react";
import GridFormField from "../../../components/GridFormField.tsx";
import {FormReportDialog} from "../../../components/FormActionDialog.tsx";
import {useTranslation} from "react-i18next";

const ExportarDocumentsForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="carpetes"/>
        <GridFormField xs={12} name="versioImprimible"/>
        <GridFormField xs={12} name="nomFitxer" required/>
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

export const useExportarDocuments = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(undefined, {ids: [id], massivo: false})
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.action.export.ok'), 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error.message, 'error');
    }

    return {
        handleShow,
        content: <ExportarDocuments title={t('page.expedient.action.export.title')}
                                    apiRef={apiRef}
                                    onSuccess={onSuccess}
                                    onError={onError}/>
    }
}
export const useExportarDocumentsMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleMassiveShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, {ids: ids, massivo: true})
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.action.exportZIP.ok'), 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error.message, 'error');
    }

    return {
        handleMassiveShow,
        content: <ExportarDocuments title={t('page.expedient.action.exportZIP.title')}
                                    apiRef={apiRef}
                                    onSuccess={onSuccess}
                                    onError={onError}/>
    }
}