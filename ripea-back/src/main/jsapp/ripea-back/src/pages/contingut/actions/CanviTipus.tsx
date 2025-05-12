import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const CanviTipusForm = (props:any) => {
    const {entity} = props;

    const filter = builder.and(
        builder.eq('metaExpedient.id', entity.metaExpedient?.id),
        builder.eq('actiu', true),
        builder.eq('pinbalActiu', false),
    )

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaDocument" filter={filter} required/>
    </Grid>
}

const CanviTipus = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"MASSIVE_CANVI_TIPUS"}
        // title={''}
        {...props}
    >
        <CanviTipusForm entity={entity}/>
    </FormActionDialog>
}

const useCanviTipus = (entity:any, refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleMassiveShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, {
            ids: ids,
            massivo: true,
        })
    }
    const onSuccess = () :void => {
        refresh?.();
        temporalMessageShow(null, '', 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error?.message, 'error');
    }

    return {
        handleMassiveShow,
        content: <CanviTipus apiRef={apiRef} entity={entity} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useCanviTipus;