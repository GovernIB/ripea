import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import {useRef} from "react";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
// import {useTranslation} from "react-i18next";

const ExportarDocumentsForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="carpetes"/>
        <GridFormField xs={12} name="versioImprimible"/>
        <GridFormField xs={12} name="nomFitxer" required/>
    </Grid>
}

const ExportarDocuments = (props:any) => {
    // const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientResource"}
        action={"EXPORT_DOC"}
        title={"Exportar documentos a Zip"}
        {...props}
    >
        <ExportarDocumentsForm/>
    </FormActionDialog>
}

const useExportarDocuments = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        console.log("id", id, row);
        apiRef.current?.show?.(id)
    }
    const handleMassiveShow = (ids:any) :void => {
        console.log("ids", ids);
        apiRef.current?.show?.(undefined, {
            ids: ids,
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, '', 'success');
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