import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid, Typography} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const EnviarViaFirmaForm = () => {
    const {data} = useFormContext();
    const { t } = useTranslation();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <Grid item xs={12} ><Typography sx={{ borderBottom: '1px solid gray' }}>{t('page.document.detall.dataBasic')}</Typography></Grid>
        <GridFormField xs={12} name="titol"/>
        <GridFormField xs={12} name="descripcio"/>
        <GridFormField xs={12} name="codiUsuariViaFirma" required/>
        <GridFormField xs={12} name="viaFirmaDispositiuCodi" hidden={!data?.isDispositiusEnabled}/>

        <Grid item xs={12} ><Typography sx={{ borderBottom: '1px solid gray' }}>{t('page.document.detall.dataInteressat')}</Typography></Grid>
        <GridFormField xs={12} name="interessat"/>
        <GridFormField xs={6} name="signantNif"/>
        <GridFormField xs={6} name="signantNom"/>

        <Grid item xs={12} ><Typography sx={{ borderBottom: '1px solid gray' }}>{t('page.document.detall.dataOther')}</Typography></Grid>
        <GridFormField xs={4} name="firmaParcial"/>
        <GridFormField xs={4} name="validateCodeEnabled"/>
        <GridFormField xs={4} name="rebreCorreu"/>
        <GridFormField xs={12} name="validateCode" hidden={!data?.validateCodeEnabled}/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
    </Grid>
}

const EnviarViaFirma = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"VIA_FIRMA"}
        title={t('page.document.action.viaFirma.title')}
        initialOnChange
        {...props}
    >
        <EnviarViaFirmaForm/>
    </FormActionDialog>
}

const useEnviarViaFirma = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = (result:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.viaFirma.ok', {document: result?.nom}), 'success');
    }

    return {
        handleShow,
        content: <EnviarViaFirma apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useEnviarViaFirma;