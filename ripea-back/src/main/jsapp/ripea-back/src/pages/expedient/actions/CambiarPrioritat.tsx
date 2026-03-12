import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const CambiarPrioritatForm = () => {
    const {data} = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom" disabled readOnly hidden={data?.massivo}/>
        <GridFormField xs={12} name="prioritat" required/>
        <GridFormField xs={12} name="prioritatMotiu" type={"textarea"} hidden={data?.prioritat=='B_NORMAL'} required/>
    </Grid>
}

export const CambiarPrioritat = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientResource"}
        title={t('page.expedient.action.changePrioritat.title')}
        action={"CANVI_PRIORITAT"}
        dialogButtons={[
            {icon: 'logout', text: t('page.expedient.action.changePrioritat.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <CambiarPrioritatForm/>
    </FormActionDialog>
}

export const useCambiarPrioritat = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(undefined, {
            ids: [id],
            massivo: false,
            nom: row?.nom,
            prioritat: row?.prioritat,
            prioritatMotiu: row?.prioritatMotiu,
        })
    }
    const onSuccess = (response:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.action.changePrioritat.ok', {expedient: response?.nom}), 'success');
    }

    return {
        handleShow,
        content: <CambiarPrioritat apiRef={apiRef} onSuccess={onSuccess}/>
    }
}

export const useCambiarPrioritatMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, {
            ids,
            massivo: true,
        })
    }
    const onSuccess = (data:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.action.changePrioritat.massiveOk', {data}), 'success');
    }

    return {
        handleShow,
        content: <CambiarPrioritat apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useCambiarPrioritat;