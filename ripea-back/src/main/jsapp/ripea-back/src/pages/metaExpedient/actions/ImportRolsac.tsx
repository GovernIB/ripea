import {MuiFormDialogApi} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const ImportRolsacForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codiSia"/>
    </Grid>
}

const ImportRolsac = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"metaExpedientResource"}
        title={t('page.metaExpedient.action.importRolsac.title')}
        action={"IMPORT_ROLSAC"}
        // formDialogButtons={[
        //     {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
        //     {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        // ]}
        {...props}
    >
        <ImportRolsacForm/>
    </FormActionDialog>
}

export const useImportRolsac = (apiGridRef:any) => {
    const apiRef = useRef<MuiFormDialogApi>();

    const handleShow = () :void => {
        apiRef.current?.show?.()
    }
    const onSuccess = (response:any) :void => {
        apiGridRef.current?.showCreateDialog(undefined, response)
    }

    return {
        handleShow,
        content: <ImportRolsac apiRef={apiRef} onSuccess={onSuccess}/>
    }
}