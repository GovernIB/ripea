import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const EviarPortafirmesForm = () => {
    const {data} = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiu"/>
        <GridFormField xs={12} name="prioritat" required/>

        {/* SIMPLE */}
        <GridFormField xs={12} name="responsables" multiple hidden={data?.portafirmesFluxTipus!='SIMPLE'} required/>
        <GridFormField xs={12} name="portafirmesSequenciaTipus" hidden={data?.portafirmesFluxTipus!='SIMPLE'} required/>

        {/* PORTAFIB */}
        <GridFormField xs={12} name="annexos" multiple hidden={data?.portafirmesFluxTipus!='PORTAFIB'} required/>
        <GridFormField xs={12} name="fluxFirma" hidden={data?.portafirmesFluxTipus!='PORTAFIB'} required/>

        <GridFormField xs={12} name="firmaParcial" hidden={!data?.mostrarFirmaParcial}/>
        <GridFormField xs={12} name="avisFirmaParcial" hidden={!data?.mostrarAvisFirmaParcial}/>
    </Grid>
}

const EviarPortafirmes = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"ENVIAR_PORTAFIRMES"}
        title={t('page.document.action.enviarPortafirmes')}
        initialOnChange
        {...props}
    >
        <EviarPortafirmesForm/>
    </FormActionDialog>
}

const useEviarPortafirmes = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {
            motiu: `Tramitació de l'expedient [${row?.expedient?.description}]`,
            metaDocument: row?.metaDocument,
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
        content: <EviarPortafirmes apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useEviarPortafirmes;