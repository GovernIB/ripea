import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import * as builder from "../../../util/springFilterUtils.ts";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const CambiarEstatForm = () => {
    const { data } = useFormContext();

    const filterEstatAdditional = builder.eq('metaExpedient.id', data?.metaExpedient?.id)

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom" disabled readOnly hidden={data?.massivo}/>
        <GridFormField xs={12} name="estatAdditional" filter={filterEstatAdditional}/>
    </Grid>
}

export const CambiarEstat = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientResource"}
        title={t('page.expedient.action.changeEstat.title')}
        action={"CANVI_ESTAT"}
        formDialogButtons={[
            {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <CambiarEstatForm/>
    </FormActionDialog>
}

const useCambiarEstat = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(undefined, {
            ids: [id],
            massivo: false,
            nom: row?.nom,
            estatAdditional: row?.estatAdditional,
        })
    }
    const onSuccess = (response:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.action.changeEstat.ok', {expedient: response?.nom}), 'success');
    }

    return {
        handleShow,
        content: <CambiarEstat apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export const useCambiarEstatMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, {
            ids,
            massivo: true,
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'info');
    }

    return {
        handleShow,
        content: <CambiarEstat apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useCambiarEstat;