import {useEffect, useRef, useState} from "react";
import {Grid, Icon} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField, {GridButton} from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import * as builder from '../../../util/springFilterUtils.ts';
import {useFluxCreateSessio} from "../../../components/SseExpedient.tsx";

const EviarPortafirmesForm = () => {
    const { t } = useTranslation();
    const {data, apiRef} = useFormContext();
    const {value: flux} = useFluxCreateSessio()

    const [open, setOpen] = useState<boolean>(true);
    const [openNewFlux, setOpenNewFlux] = useState<boolean>(false);

    useEffect(() => {
        if(flux){
            if(!flux?.error){
                apiRef?.current?.setFieldValue("portafirmesEnviarFluxId", {
                    id: flux?.fluxId,
                    description: flux?.nom +' - '+ flux?.descripcio
                })
                setOpen(true)
                setOpenNewFlux(false)
            }
        }
    }, [flux]);

    const filterResponsables = builder.neq('nif', null)
    const filterAnnexos = builder.and(
        builder.neq('id', apiRef?.current?.getId()),
        builder.eq('expedient.id', data?.expedient?.id),
    )

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiu"/>
        <GridFormField xs={12} name="prioritat" required/>

        {/* SIMPLE */}
        <GridFormField xs={12} name="responsables" multiple
                       filter={filterResponsables}
                       hidden={data?.portafirmesFluxTipus!='SIMPLE'}/>
        <GridFormField xs={12} name={"nifsManuals"} multiple
                       hidden={data?.portafirmesFluxTipus!='SIMPLE'}/>
        <GridFormField xs={12} name={"carrecs"} multiple
                       hidden={data?.portafirmesFluxTipus!='SIMPLE'}/>
        <GridFormField xs={12} name="portafirmesSequenciaTipus" hidden={data?.portafirmesFluxTipus!='SIMPLE'} required/>

        {/* PORTAFIB */}
        <GridFormField xs={12} name="annexos" multiple
                       filter={filterAnnexos}
                       hidden={data?.portafirmesFluxTipus!='PORTAFIB'} required/>
        <GridFormField xs={10} name="portafirmesEnviarFluxId"
                       componentProps={{title: t('page.document.detall.flux')}}
                       hidden={data?.portafirmesFluxTipus!='PORTAFIB'} required/>

        <GridButton
            xs={1} onClick={()=>setOpen(!open)}
            hidden={data?.portafirmesFluxTipus!='PORTAFIB'}
        >
            <Icon sx={{m: 0}}>{open ?'visibility_off' :'visibility'}</Icon>
        </GridButton>
        <GridButton
            xs={1} onClick={()=>{
                setOpen(false)
                setOpenNewFlux(true)
            }}
            hidden={data?.portafirmesFluxTipus!='PORTAFIB'}
        >
            <Icon sx={{m: 0}}>open_in_new</Icon>
        </GridButton>

        <GridFormField xs={12} name="firmaParcial" hidden={!data?.mostrarFirmaParcial}/>
        <GridFormField xs={12} name="avisFirmaParcial" hidden={!data?.mostrarAvisFirmaParcial}/>

        <Grid item xs={12} hidden={!data?.portafirmesFluxUrl || !open}>
            <iframe src={data?.portafirmesFluxUrl} width={'100%'} height={'500px'}></iframe>
        </Grid>

        <Grid item xs={12} hidden={!data?.urlInicioFlujoFirma || !openNewFlux}>
            <iframe src={data?.urlInicioFlujoFirma} width={'100%'} height={'500px'}></iframe>
        </Grid>
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
            expedient: row?.expedient,
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