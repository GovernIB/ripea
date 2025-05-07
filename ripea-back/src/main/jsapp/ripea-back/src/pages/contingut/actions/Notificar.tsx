import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const NotificarForm = () => {
    const { data } = useFormContext();

    const interessatsFilter: string = builder.and(
        builder.eq("expedient.id", data?.expedient?.id),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus"/>
        <GridFormField xs={12} name="estat" disabled/>
        <GridFormField xs={12} name="interessats" multiple filter={interessatsFilter}/>
        <GridFormField xs={12} name="concepte"/>
        <GridFormField xs={12} name="serveiTipus"/>
        <GridFormField xs={12} name="descripcio" type={"textarea"}/>
        <GridFormField xs={12} name="dataProgramada"/>
        <GridFormField xs={6} name="duracio"/>
        <GridFormField xs={6} name="dataCaducitat"/>
        <GridFormField xs={12} name="retard"/>
        <GridFormField xs={12} name="entregaPostal"/>
    </Grid>
}

const Notificar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"NOTIFICAR"}
        title={(data:any)=> `${t('page.document.action.notificar')}: ${data.nom}`}
        {...props}
    >
        <NotificarForm/>
    </FormActionDialog>
}

const useNotificar = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id,{
            nom: row?.nom,
            expedient: {id: row?.expedient.id}
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, '', 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow('Error', error.message, 'error');
    }

    return {
        handleShow,
        content: <Notificar apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useNotificar;