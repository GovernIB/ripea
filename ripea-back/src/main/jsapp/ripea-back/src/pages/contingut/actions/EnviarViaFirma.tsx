import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid2 as Grid, Typography} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const EnviarViaFirmaForm = () => {
    const {data} = useFormContext();
    const { t } = useTranslation();

    const interessatFilter = builder.and(
        builder.eq('expedient.id', data?.expedient?.id),
        builder.eq('esRepresentant', false)
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <Grid size={12} ><Typography sx={{ borderBottom: '1px solid gray' }}>{t('page.document.detall.dataBasic')}</Typography></Grid>
        <GridFormField name="titol"/>
        <GridFormField name="descripcio"/>
        <GridFormField name="tipusDestinatari"/>
        <GridFormField name="emplenable" hidden={data?.tipusDestinatari != 'EMAIL'}/>
        <GridFormField name="codiUsuariViaFirma" hidden={data?.tipusDestinatari == 'EMAIL'}/>
        <GridFormField name="viaFirmaDispositiuCodi" hidden={!data?.isDispositiusEnabled}/>

        <Grid size={12} ><Typography sx={{ borderBottom: '1px solid gray' }}>{t('page.document.detall.dataInteressat')}</Typography></Grid>
        <GridFormField name="interessat" filter={interessatFilter}/>
        <GridFormField size={6} name="signantNif"/>
        <GridFormField size={6} name="signantNom"/>
        <GridFormField size={6} name="signantEmail" hidden={data?.tipusDestinatari != 'EMAIL'}/>

        <Grid size={12} ><Typography sx={{ borderBottom: '1px solid gray' }}>{t('page.document.detall.dataOther')}</Typography></Grid>
        <GridFormField size={4} name="firmaParcial"/>
        <GridFormField size={4} name="validateCodeEnabled"/>
        <GridFormField size={4} name="rebreCorreu"/>
        <GridFormField name="validateCode" hidden={!data?.validateCodeEnabled}/>
        <GridFormField name="observacions" type={"textarea"}/>
    </Grid>
}

const EnviarViaFirma = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"VIA_FIRMA"}
        title={t('page.document.action.viaFirma.title')}
        formDialogButtons={[
            {icon: 'send', text: t('page.document.action.viaFirma.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}        
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

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {
            expedient: row?.expedient,
        })
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