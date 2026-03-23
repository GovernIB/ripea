import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid2 as Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const ReobrirForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="responsables" multiple required/>
        <GridFormField name="motiu"/>
    </Grid>
}

const Reobrir = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientTascaResource"}
        action={"REABRIR"}
        title={t('page.tasca.action.reobrir.title')}
        formDialogButtons={[
            {icon: 'undo', text: t('page.tasca.action.reobrir.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <ReobrirForm/>
    </FormActionDialog>
}

const useReobrir = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id,{responsables: row?.responsableActual ?[row?.responsableActual] :[]})
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.tasca.action.reobrir.ok'), 'success');
    }

    return {
        handleShow,
        content: <Reobrir apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useReobrir;