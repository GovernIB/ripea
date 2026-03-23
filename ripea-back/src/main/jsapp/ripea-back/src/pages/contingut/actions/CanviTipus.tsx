import {useRef} from "react";
import {Grid2 as Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const CanviTipusForm = () => {
    const {data} = useFormContext();

    const filter = builder.and(
        builder.eq('metaExpedient.id', data?.metaExpedient?.id),
        builder.eq('actiu', true),
        builder.eq('pinbalActiu', false),
    )

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="metaDocument" filter={filter} required/>
    </Grid>
}

const CanviTipus = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"MASSIVE_CANVI_TIPUS"}
        title={t('page.document.action.changeType.title')}
        formDialogButtons={[
            {icon: 'edit', text: t('page.document.action.changeType.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <CanviTipusForm/>
    </FormActionDialog>
}

const useCanviTipus = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleMassiveShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, {
            ids: ids,
            massivo: true,
            metaExpedient: entity?.metaExpedient,
        })
    }
    const onSuccess = () :void => {
        refresh?.();
        temporalMessageShow(null, t('page.document.action.changeType.ok'), 'success');
    }

    return {
        handleMassiveShow,
        content: <CanviTipus apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useCanviTipus;