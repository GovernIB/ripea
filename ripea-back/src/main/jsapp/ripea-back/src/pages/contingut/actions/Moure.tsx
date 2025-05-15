import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useRef} from "react";
import {Grid} from "@mui/material";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const MoureForm = () => {
    const { data } = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="contingut" readOnly disabled hidden={data?.massivo}/>
        <GridFormField xs={12} name="expedient" required/>
        <GridFormField xs={12} name="carpeta"
                       readOnly={!data?.expedient}
                       disabled={!data?.expedient}
                       filter={builder.and(builder.eq('expedient.id', data?.expedient?.id))}/>
        <GridFormField xs={12} name="motiu"/>
        <GridFormField xs={12} name="action" required/>
    </Grid>
}

const Moure = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"MOURE"}
        title={t('page.document.action.move')}
        {...props}
    >
        <MoureForm/>
    </FormActionDialog>
}

const useMoure = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        const carpeta = row?.expedient?.id != row?.pare?.id ?row?.pare :null
        apiRef.current?.show?.(undefined, {
            ids: [id],
            massivo: false,
            contingut: row?.nom,
            expedient: row?.expedient,
            carpeta: carpeta,
        })
    }
    const handleMassiveShow = (ids:any[], entity:any) :void => {
        apiRef.current?.show?.(undefined, {
            ids: ids,
            massivo: true,
            expedient: {
                id: entity?.id,
                description: entity?.nom,
            },
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, '', 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error.message, 'error');
    }

    return {
        handleShow,
        handleMassiveShow,
        content: <Moure apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useMoure;