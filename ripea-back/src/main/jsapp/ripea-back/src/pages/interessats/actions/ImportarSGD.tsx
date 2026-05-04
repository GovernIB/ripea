import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const ImportarSGDForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField size={6} name="numeroRegistre" required/>
        <GridFormField size={6} name="dataPresentacio" type={"date"} required/>
    </Grid>
}

const ImportarSGD = (props:any) => {
    const { t } = useTranslation();
    return <FormActionDialog
        resourceName={"expedientResource"}
        action={"IMPORT_INTE"}
        title={t('page.interessat.action.importSGD.title')}
        formDialogButtons={[
            {icon: 'group_search', text: t('common.import'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <ImportarSGDForm/>
    </FormActionDialog>
}

const useImportarSGD = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () :void => {
        apiRef.current?.show?.(entity?.id)
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.interessat.action.importSGD.ok'), 'success');
    }

    return {
        handleShow,
        content: <ImportarSGD apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useImportarSGD;