import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const ReassignarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="usuaris" multiple required/>
    </Grid>
}

const Reassignar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientTascaResource"}
        action={'REASSIGNAR'}
        title={t('page.tasca.action.reassignar.title')}
        formDialogButtons={[
            {icon: 'person', text: t('page.tasca.action.reassignar.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <ReassignarForm/>
    </FormActionDialog>
}

const useReassignar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {usuaris: row?.responsables})
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.tasca.action.reassignar.ok'), 'success');
    }

    return {
        handleShow,
        content: <Reassignar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useReassignar;