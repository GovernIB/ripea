import {useRef} from "react";
import {Grid2 as Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const CambiarFechaLimiteForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="dataInici" type={"date"} readOnly disabled required/>
        <GridFormField size={6} name="duracio" required/>
        <GridFormField size={6} name="dataLimit" type={"date"} componentProps={{disablePast: true}} required/>
    </Grid>
}

const CambiarDataLimit = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientTascaResource"}
        title={t('page.tasca.action.changeDataLimit.title')}
        action={'CHANGE_DATALIMIT'}
        formDialogButtons={[
            {icon: 'schedule', text: t('page.tasca.action.changeDataLimit.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <CambiarFechaLimiteForm/>
    </FormActionDialog>
}

const useCambiarDataLimit = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {
            dataInici: row?.dataInici,
            duracio: row?.duracio,
            dataLimit: row?.dataLimit,
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.tasca.action.changeDataLimit.ok'), 'success');
    }

    return {
        handleShow,
        content: <CambiarDataLimit apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useCambiarDataLimit;