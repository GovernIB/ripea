import {useRef} from "react";
import {Grid} from "@mui/material";
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
        <GridFormField xs={12} name="metaDocument" filter={filter} required/>
    </Grid>
}

const CanviTipus = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"MASSIVE_CANVI_TIPUS"}
        title={t('page.document.action.changeType.title')}
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
    const onSuccess = (result:any) :void => {
        refresh?.();
        temporalMessageShow(null, t('page.document.action.changeType.ok', {document: result?.nom}), 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error?.message, 'error');
    }

    return {
        handleMassiveShow,
        content: <CanviTipus apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useCanviTipus;