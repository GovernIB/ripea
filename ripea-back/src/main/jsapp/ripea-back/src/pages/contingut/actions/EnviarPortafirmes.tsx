import {useRef, useState} from "react";
import {Alert, Grid, Icon} from "@mui/material";
import {MuiDialog, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField, {GridButton} from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {useFluxCreateSession} from "../../../components/SseExpedient.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import Iframe from "../../../components/Iframe.tsx";
import * as builder from '../../../util/springFilterUtils.ts';
import IconButton from "@mui/material/IconButton";
import Load from "../../../components/Load.tsx";

const useConverdedToPDF = () => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);
    const [entityId, setEntityId] = useState<any>();

    const handleOpen = (id:any) => {
        setEntityId(id);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntityId(undefined);
            setOpen(false);
        }
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.document.action.toPDF.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close')
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <Load value={entityId}>
                <Iframe src={`${import.meta.env.VITE_BASE_URL}document/convertir/pdf/${entityId}`}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

const EnviarPortafirmesForm = () => {
    const { t } = useTranslation();
    const {data, apiRef} = useFormContext();
    const { onChange } = useFluxCreateSession();
    const { value: user } = useUserSession()
    const [open, setOpen] = useState<boolean>(true);
    const [openNewFlux, setOpenNewFlux] = useState<boolean>(false);

    const {handleOpen, dialog} = useConverdedToPDF();

    onChange((flux) => {
        if(!flux?.error && user?.codi==flux?.usuari) {
            apiRef?.current?.setFieldValue("fluxCreat", flux);
            apiRef?.current?.setFieldValue("portafirmesEnviarFluxId", flux?.fluxId);
            setOpen(false);
            setOpenNewFlux(false);
        }
    });

    const filterResponsables = builder.neq('nif', null)
    const filterAnnexos = builder.and(
        builder.neq('id', apiRef?.current?.getId()),
        builder.eq('expedient.id', data?.expedient?.id),
        builder.eq('esborrat', 0),
    )

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>

        { data?.extension != 'pdf' &&
            <Grid xs={12}>
                <Alert severity={'warning'}
                       action={
                           <IconButton onClick={()=>handleOpen(apiRef?.current?.getId())}>
                               <Icon sx={{ m: 0 }}>info</Icon>
                           </IconButton>
                       }
                >
                    {t('page.document.action.toPDF.description')}
                </Alert>
            </Grid>
        }
        {dialog}

        <GridFormField xs={12} name="motiu"/>
        <GridFormField xs={12} name="prioritat" required/>

        {/* SIMPLE */}
        {data?.portafirmesFluxTipus=='SIMPLE' && <>
            <GridFormField xs={12} name="responsables" multiple filter={filterResponsables}/>
            <GridFormField xs={12} name={"nifsManuals"} multiple/>
            <GridFormField xs={12} name={"carrecs"} multiple hidden={!user?.sessionScope?.isWsUsuariEntitatActiu}/>
            <GridFormField xs={12} name="portafirmesSequenciaTipus" required/>
        </>}

        {/* PORTAFIB */}
        {data?.portafirmesFluxTipus=='PORTAFIB' && <>
            <GridFormField xs={12} name="annexos" multiple filter={filterAnnexos} required/>
            <GridFormField xs={10} name="portafirmesEnviarFluxId"
                           componentProps={{title: t('page.document.detall.flux')}}
                           requestParams={{additionalOption: {
                               value: data?.fluxCreat?.fluxId,
                               description: data?.fluxCreat?.nom +' - '+ data?.fluxCreat?.descripcio,
                           }}}
                           required/>
        </>}

        <GridButton
            variant={open ?"contained" :"outlined"}
            xs={1} onClick={()=>{
                setOpenNewFlux(false)
                setOpen(!open)
            }}
            hidden={data?.portafirmesFluxTipus!='PORTAFIB'}
        >
            <Icon sx={{m: 0}}>{open ?'visibility_off' :'visibility'}</Icon>
        </GridButton>
        <GridButton
            variant={openNewFlux ?"contained" :"outlined"}
            xs={1} onClick={()=>{
                setOpen(false)
                setOpenNewFlux(!openNewFlux)
            }}
            hidden={data?.portafirmesFluxTipus!='PORTAFIB'}
        >
            <Icon sx={{m: 0}}>open_in_new</Icon>
        </GridButton>

        <GridFormField xs={12} name="firmaParcial" hidden={!data?.mostrarFirmaParcial}/>
        <GridFormField xs={12} name="avisFirmaParcial" hidden={!data?.mostrarAvisFirmaParcial}/>

        <Grid item xs={12} hidden={!data?.portafirmesFluxUrl || !open}>
            <Iframe src={data?.portafirmesFluxUrl}/>
        </Grid>

        <Grid item xs={12} hidden={!data?.urlInicioFlujoFirma || !openNewFlux}>
            <Iframe src={data?.urlInicioFlujoFirma}/>
        </Grid>
    </Grid>
}

const EnviarPortafirmes = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"ENVIAR_PORTAFIRMES"}
        title={t('page.document.action.portafirmes.title')}
        initialOnChange
        {...props}
    >
        <EnviarPortafirmesForm/>
    </FormActionDialog>
}

const useEnviarPortafirmes = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {
            motiu: `Tramitació de l'expedient [${row?.expedient?.description}]`,
            expedient: row?.expedient,
            metaDocument: row?.metaDocument,
            extension: row?.fitxerExtension,
        })
    }
    const onSuccess = (result:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.portafirmes.ok', {document: result?.nom}), 'success');
    }

    return {
        handleShow,
        content: <EnviarPortafirmes apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useEnviarPortafirmes;