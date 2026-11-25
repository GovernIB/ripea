import {Grid} from "@mui/material";
import {useMuiFormDialogApiRef, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const RevertRebutjarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="motiu" type={"textarea"} required/>
    </Grid>
}

const RevertRebutjar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientPeticioResource"}
        action={"REVERT_REBUTJAR_ANOTACIO"}
        title={t('page.anotacio.action.revertRebutjar.title')}
        formDialogButtons={[
            {icon: 'close', text: t('page.anotacio.action.revertRebutjar.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <RevertRebutjarForm/>
    </FormActionDialog>
}

const useRevertRebutjar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.();
        temporalMessageShow(null, t('page.anotacio.action.revertRebutjar.ok'), 'success');
    }

    return {
        handleShow,
        content: <RevertRebutjar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useRevertRebutjar;
