import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const RebutjarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="motiu" required/>
    </Grid>
}

const Rebutjar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientTascaResource"}
        action={"REBUTJAR"}
        title={t('page.tasca.action.rebutjar.title')}
        formDialogButtons={[
            {icon: 'reply', text: t('page.tasca.action.rebutjar.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <RebutjarForm/>
    </FormActionDialog>
}

const useRebutjar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id,{motiu:row?.motiuRebuig})
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.tasca.action.rebutjar.ok'), 'success');
    }

    return {
        handleShow,
        content: <Rebutjar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useRebutjar;