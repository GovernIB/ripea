import {useMuiFormDialogApiRef, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const RetomarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="motiu"/>
    </Grid>
}

const Retomar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientTascaResource"}
        action={"RETOMAR"}
        title={t('page.tasca.action.retomar.title')}
        formDialogButtons={[
            {icon: 'close', text: t('page.tasca.action.retomar.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <RetomarForm/>
    </FormActionDialog>
}

const useRetomar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.tasca.action.retomar.ok'), 'success');
    }

    return {
        handleShow,
        content: <Retomar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useRetomar;