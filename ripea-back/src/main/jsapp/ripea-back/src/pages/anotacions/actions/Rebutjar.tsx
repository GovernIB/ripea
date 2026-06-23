import {Grid} from "@mui/material";
import {useMuiFormDialogApiRef, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const RebutjarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="motiu" type={"textarea"} required/>
    </Grid>
}

const Rebutjar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientPeticioResource"}
        action={"REBUTJAR_ANOTACIO"}
        title={t('page.anotacio.action.rebutjar.title')}
        formDialogButtons={[
            {icon: 'close', text: t('page.anotacio.action.rebutjar.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <RebutjarForm/>
    </FormActionDialog>
}

const useRebutjar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.();
        temporalMessageShow(null, t('page.anotacio.action.rebutjar.ok'), 'success');
    }

    return {
        handleShow,
        content: <Rebutjar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useRebutjar;