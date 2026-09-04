import {useRef, useState, forwardRef, useImperativeHandle } from "react";
import {Alert, Grid, Icon} from "@mui/material";
import {MuiDialog, useMuiFormDialogApiRef, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField, {GridButton} from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {useFluxCreateSession} from "../../../components/SseExpedient.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import Iframe from "../../../components/Iframe.tsx";
import * as builder from '../../../util/springFilterUtils.ts';
import IconButton from "@mui/material/IconButton";
import Load from "../../../components/Load.tsx";
import {useToProgramaAntic} from "../../user/UserHeadToolbar.tsx";
import { useEffect } from "react";

const useConverdedToPDF = () => {
    const { t } = useTranslation();
    const { getUrl } = useToProgramaAntic();

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
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' }
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <Load value={entityId}>
                <Iframe isPDF src={getUrl(`document/convertir/pdf/${entityId}`)}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

// Funció genèrica per tancar transacció
const tancarTransaccio = (getUrl: (path: string) => string, idTransaccio?: string) => {
    if (!idTransaccio) return;
    
    const url = getUrl(`document/portafirmes/tancarTransaccio/${idTransaccio}`);

    if (navigator.sendBeacon) {
        navigator.sendBeacon(url);
    } else {
        fetch(url, { method: "GET", keepalive: true });
    }
};


// Hook per tancar la transacció en tancar finestra/navegador utilitzant l'event beforeunload
const useCerrarTransaccioOnUnload = (getUrl: (path: string) => string, idTransaccio?: string, openNewFlux?: boolean) => {
    useEffect(() => {
        if (!idTransaccio || !openNewFlux) return;
        
        const handleBeforeUnload = () => {
            tancarTransaccio(getUrl, idTransaccio);
        };

        window.addEventListener("beforeunload", handleBeforeUnload);
        return () => window.removeEventListener("beforeunload", handleBeforeUnload);
    }, [idTransaccio, openNewFlux]);
};

const EnviarPortafirmesForm = forwardRef((_props, ref) => {
    const { t } = useTranslation();
    const {data, apiRef} = useFormContext();
    const { onChange } = useFluxCreateSession();
    const { value: user } = useUserSession()
    const [open, setOpen] = useState<boolean>(!!data?.portafirmesEnviarFluxId);
    const [openNewFlux, setOpenNewFlux] = useState<boolean>(false);
	const { getUrl } = useToProgramaAntic();
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

    useCerrarTransaccioOnUnload(getUrl, data?.idTransaccio, openNewFlux);

    // Tancar transacció en tancar modal manualmente
    const handleCloseModal = () => {
        tancarTransaccio(getUrl, data?.idTransaccio);
    };
    
    // Exposar funció perquè es pugui tancar des del component fill
    useImperativeHandle(ref, () => ({
        handleCloseModal
    }));
    
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>

        { !data?.massivo && data?.extension != 'pdf' &&
            <Grid size={12}>
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

        <GridFormField name="dataInici" hidden={!data?.massivo} type={"date"}/>
{/*        <GridFormField name="enviarCorreu" hidden={!data?.massivo}/> */}
        <GridFormField name="motiu"/>
        <GridFormField name="prioritat" required/>

        {/* SIMPLE */}
        {data?.portafirmesFluxTipus=='SIMPLE' && <>
            <GridFormField name="responsables" multiple autocomplete filter={filterResponsables} namedQueries={[`ADD_PLUGIN_USERS`]}/>
            <GridFormField name={"nifsManuals"} multiple/>
            <GridFormField name={"carrecs"} multiple hidden={!user?.sessionScope?.isWsUsuariEntitatActiu}/>
            <GridFormField name="portafirmesSequenciaTipus" required/>
        </>}

        {/* PORTAFIB */}
        {data?.portafirmesFluxTipus=='PORTAFIB' && <>
            <GridFormField name="annexos" multiple filter={filterAnnexos} hidden={data?.massivo}/>
            <GridFormField size={10} name="portafirmesEnviarFluxId"
                           componentProps={{title: t('page.document.detall.flux')}}
                           requestParams={{additionalOption: 
                                {
                                    value: data?.fluxCreat?.fluxId,
                                    description: data?.fluxCreat?.nom +' - '+ data?.fluxCreat?.descripcio,
                               },
                               metaDocumentId: data?.metaDocumentId ?? data?.metaDocument?.id,
                            }}
                           autocomplete
                           required/>

            <GridButton
                variant={open ?"contained" :"outlined"}
                size={1} onClick={()=>{
                    setOpenNewFlux(false)
                    setOpen(!open)
                }}
                disabled={!data?.portafirmesEnviarFluxId}
            >
                <Icon sx={{m: 0}}>{open ?'visibility_off' :'visibility'}</Icon>
            </GridButton>
            <GridButton
                variant={openNewFlux ?"contained" :"outlined"}
                size={1} onClick={()=>{
                    setOpen(false)
                    setOpenNewFlux(!openNewFlux)
                }}
            >
                <Icon sx={{m: 0}}>open_in_new</Icon>
            </GridButton>
        </>}

        <GridFormField name="firmaParcial" hidden={!data?.mostrarFirmaParcial}/>
        <GridFormField name="avisFirmaParcial" hidden={!data?.mostrarAvisFirmaParcial}/>

        <Grid size={12} hidden={!data?.portafirmesFluxUrl || !open}>
            <Iframe src={data?.portafirmesFluxUrl}/>
        </Grid>

        <Grid size={12} hidden={!data?.urlInicioFlujoFirma || !openNewFlux}>
            <Iframe src={data?.urlInicioFlujoFirma}/>
        </Grid>
    </Grid>
});

const EnviarPortafirmes = (props:any) => {
    const { t } = useTranslation();
    const formRef = useRef<any>(undefined);
    
    return <FormActionDialog
        resourceName={"documentResource"}
        action={"ENVIAR_PORTAFIRMES"}
        title={t('page.document.action.portafirmes.title')}
        formDialogButtons={[
            {icon: 'send', text: t('page.document.action.portafirmes.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        initialOnChange
        onClose={() => formRef.current?.handleCloseModal?.()}
        {...props}
    >
        <EnviarPortafirmesForm ref={formRef}/>
    </FormActionDialog>
}

export const useEnviarPortafirmes = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(undefined, {
            motiu: `Tramitació de l'expedient [${row?.expedient?.description}]`,
            expedient: row?.expedient,
            metaDocument: row?.metaDocument,
            extension: row?.fitxerExtension,
            ids: [id],
            massivo: false,
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
export const useEnviarPortafirmesMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, {
            ids,
            massivo: true,
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'info');
    }

    return {
        handleShow,
        content: <EnviarPortafirmes apiRef={apiRef} onSuccess={onSuccess}/>
    }
}