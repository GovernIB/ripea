import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const ReintentarForm = () => {
    const { data } = useFormContext();
    const filterEstatAdditional = builder.eq('metaExpedient.id', data?.procediment?.id)

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="metaDocument" filter={filterEstatAdditional}/>
    </Grid>
}

export const Reintentar = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreAnnexResource"}
        title={t('page.anotacio.action.reintentar.title')}
        action={"REINTENTAR"}
        formDialogButtons={[
            {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <ReintentarForm/>
    </FormActionDialog>
}

export const useReintentar = (refresh?: () => void, procediment?: any) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(undefined, {
            ids: [id],
            massivo: false,
            procediment
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.anotacio.action.procesarAnnexosPendents.ok'), 'success');
    }
    return {
        handleShow,
        content: <Reintentar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}

export const useReintentarMassive = (refresh?: () => void, procediment?: any) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
    const handleShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, {
            ids,
            massivo: true,
            procediment,
        })
    }
    const onSuccess = (data:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.anotacio.action.procesarAnnexosPendents.massiveOk', {data}), 'info');
    }
    return {
        handleShow,
        content: <Reintentar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}