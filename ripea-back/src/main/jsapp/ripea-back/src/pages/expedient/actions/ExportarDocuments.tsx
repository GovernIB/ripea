import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import {useRef, useState} from "react";
import GridFormField from "../../../components/GridFormField.tsx";
import {FormReportDialog} from "../../../components/FormActionDialog.tsx";

const ExportarDocumentsForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="carpetes"/>
        <GridFormField xs={12} name="versioImprimible"/>
        <GridFormField xs={12} name="nomFitxer" required/>
    </Grid>
}

const ExportarDocuments = (props:any) => {
    // const { t } = useTranslation();

    return <FormReportDialog
        resourceName={"expedientResource"}
        report={"EXPORT_DOC"}
        title={"Exportar documentos a Zip"}
        {...props}
    >
        <ExportarDocumentsForm/>
    </FormReportDialog>
}

const useExportarDocuments = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
    const [message, setMessage] = useState<any>('');

    const handleShow = (id:any, row:any) :void => {
        console.log("id", id, row);
        setMessage('individual')
        apiRef.current?.show?.(undefined, {ids: [id], massivo: false})
    }
    const handleMassiveShow = (ids:any[]) :void => {
        console.log("ids", ids);
        setMessage('maassiu')
        apiRef.current?.show?.(undefined, {ids: ids, massivo: true})
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, message, 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow('Error', error.message, 'error');
    }

    return {
        handleShow,
        handleMassiveShow,
        content: <ExportarDocuments apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useExportarDocuments;