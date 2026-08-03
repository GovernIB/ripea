import {useTranslation} from "react-i18next";
import {MuiDialog, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useEffect, useMemo, useRef, useState} from "react";
import StyledMuiFilter from "../../../../components/StyledMuiFilter.tsx";
import GridFormField, {GridButton} from "../../../../components/GridFormField.tsx";
import {Alert, Grid, Icon, IconButton, Typography} from "@mui/material";
import * as builder from "../../../../util/springFilterUtils.ts";
import {useSession} from "../../../../components/SessionStorageContext.tsx";
import Load from "../../../../components/Load.tsx";
import {getAlertSeverity} from "../../../../components/BaseApp.tsx";
import {MenuActionButton} from "../../../../components/MenuButton.tsx";
import AlertExpand from "../../../../components/AlertExpand.tsx";
import {ErrorArea} from "../../../../components/ErrorPage.tsx";

const INTEGRACIO_RESOURCE = 'integracioResource';
const FILTER_PLUGIN_CODE = 'FILTER_PLUGIN';
// La clau de sessió ha de coincidir amb la que calcula StyledMuiFilter:
// [resourceName, code].join('.'). El resourceName es va prefixar per evitar
// col·lisions entre filtres; si aquí es llegís la clau "pelada" (FILTER_PLUGIN)
// el filtre sempre seria undefined i el diagnòstic es quedaria carregant sense fi.
const FILTER_PLUGIN_SESSION_KEY = `${INTEGRACIO_RESOURCE}.${FILTER_PLUGIN_CODE}`;

const useActions = () => {
    const {t} = useTranslation()
    const {
        isReady: apiIsReady,
        artifactAction: apiAction
    } = useResourceApiService(INTEGRACIO_RESOURCE);
    const {temporalMessageShow} = useBaseAppContext();

    const intervalRef = useRef<number | null>(null);
    const clean = () => {
        if (intervalRef.current) {
            clearInterval(intervalRef.current);
        }
    }

    const [diagnostic, setDiagnostic] = useState<Map<string, any>>(new Map());
    const putDiagnostic = (key:string, value:any) => {
        setDiagnostic(prev => {
            const next = new Map(prev);
            next.set(key, value);
            return next;
        });
    }

    const apiDiagnosticAll = (integracions:string[], entitat:any, organ:any) => {
        if (integracions != null && integracions.length > 0) {
            clean()
            setDiagnostic(new Map())
            let count = 0;

            intervalRef.current = setInterval(() => {
                if (integracions.length > count) {
                    apiDiagnostic(integracions[count], entitat, organ)
                } else {
                    clean()
                }

                count++;
                return clean
            }, 500);
        }
    }

    const apiDiagnostic = (codiIntegracio:any, entitat:any, organ:any) => {
        putDiagnostic(codiIntegracio, undefined)
        apiAction(undefined, {code: 'DIAGNOSTIC_PLUGIN', data: {codiIntegracio, entitat, organ}})
            .then((response) => putDiagnostic(codiIntegracio, response))
            .catch((error) => putDiagnostic(codiIntegracio, {nivell: "ERROR", missatge: error?.message}));
    }

    const apiReiniciar = (codiIntegracio:any, entitat:any, organ:any) => {
        apiAction(undefined, {code: 'REINICIAR_PLUGIN', data: {codiIntegracio, entitat, organ}})
            .then(() => {
                temporalMessageShow(null,
                    codiIntegracio
                        ? t('page.integracio.action.reiniciar.ok', {nom: codiIntegracio})
                        : t('page.integracio.action.reiniciarAll.ok')
                    , 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const handleClose = () => {
        if (intervalRef.current) {
            clearInterval(intervalRef.current)
        }
        setDiagnostic(new Map())
    }

    return {
        apiIsReady,
        diagnostic,
        apiDiagnosticAll,
        apiDiagnostic,
        apiReiniciar,
        handleClose,
    }
}

export const IntegracioDiagnosticFilterForm = ({apiReiniciar}:any) => {
    const {t} = useTranslation()
    const {data} = useFormContext();

    const organFilter = useMemo(() => builder.and(
        builder.eq('entitat.id', data?.entitat?.id)
    ), [data?.entitat?.id])

    return <>
        <GridFormField size={{xs: 12, sm: 6}} name="entitat"/>
        <GridFormField size={{xs: 12, sm: 6}} name="organ" filter={organFilter}/>
        <GridButton size={{xs: 12, sm: 3, md: 2}} icon={'cached'} onClick={apiReiniciar} variant={'contained'} color={'warning'}>
            {t('page.integracio.action.reiniciarAll.label')}
        </GridButton>
    </>

}
export const IntegracioDiagnosticFilter = ({apiReiniciar}:any) => {
    const {t} = useTranslation()

    const buttons = [
        {
            value: 'clear',
            text: t('common.clear'),
            icon: 'auto_fix_normal',
            componentProps: {
                variant: "outlined",
            },
        },
        {
            value: 'search',
            text: t('page.integracio.action.diagnostic.title'),
            icon: 'monitor_heart',
            componentProps: {
                variant: "contained",
            },
        },
    ]

    return <StyledMuiFilter
        resourceName={INTEGRACIO_RESOURCE}
        code={FILTER_PLUGIN_CODE}
        springFilterBuilder={()=>{}}
        // onSpringFilterChange={onSpringFilterChange}
        buttons={buttons}
        initOnChangeRequest
    >
        <IntegracioDiagnosticFilterForm apiReiniciar={apiReiniciar}/>
    </StyledMuiFilter>
}

export const useIntegracioDiagnostic = (integracions:any[]) => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);
    const {value: filter} = useSession(FILTER_PLUGIN_SESSION_KEY);

    const {apiIsReady, diagnostic, apiDiagnosticAll, apiDiagnostic, apiReiniciar, handleClose: hClose} = useActions()

    const handleOpen = () => {
        if (apiIsReady) {
            setOpen(true);
        }
    }

    useEffect(() => {
        if (apiIsReady && open && filter != undefined) {
            apiDiagnosticAll(integracions.map((i:any) => i.codi), filter?.entitat, filter?.organ)
        }
    }, [open, filter?.entitat, filter?.organ]);

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
            hClose()
        }
    };

    const buttons :any[] = [
        {
            value: 'close',
            text: t('common.close'),
            icon: 'close',
            componentProps: { variant: 'outlined' }
        },
    ]

    const actions = [
        {
            label: t('page.integracio.action.diagnostic.label'),
            icon: 'monitor_heart',
            onClick: (row:any) => apiDiagnostic(row.codi, filter?.entitat, filter?.organ)
        },
        {
            label: t('page.integracio.action.reiniciar.label'),
            icon: 'cached',
            onClick: (row:any) => apiReiniciar(row.codi, filter?.entitat, filter?.organ)
        }
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.integracio.action.diagnosticAll.title')}
            componentProps={{ fullWidth: true, maxWidth: 'xl' }}
            buttons={buttons}
            buttonCallback={() => {
                handleClose();
            }}
        >
            <IntegracioDiagnosticFilter apiReiniciar={() => apiReiniciar(undefined, filter?.entitat, filter?.organ)}/>

            <Load value={integracions && filter != undefined}>
                {integracions?.map?.(i => {
                    const d = diagnostic.get(i.codi)
                    return <Grid container direction={"row"} display={'flex'} columnSpacing={1}>
                        <Grid size={3}><Typography variant={"body1"} ml={1}>{i.nom || i.codi}</Typography></Grid>
                        <Load value={d}>
                            <Grid size={7.5}>
                                {d?.traza == null
                                    ? <Alert severity={getAlertSeverity(d?.nivell)}>{d?.missatge}</Alert>
                                    : <AlertExpand
                                        label={d?.missatge}
                                        severity={getAlertSeverity(d?.nivell)}
                                        action={<>
                                            <IconButton
                                                color="inherit"
                                                size="small"
                                                onClick={() => navigator.clipboard.writeText(d?.traza)}
                                            >
                                                <Icon sx={{ m: 0 }}>content_copy</Icon>
                                            </IconButton>
                                        </>}
                                    >
                                        <ErrorArea sx={{ maxHeight: '500px' }}>
                                            {d?.traza}
                                        </ErrorArea>
                                    </AlertExpand>
                                }
                            </Grid>
                            <Grid size={1.5}>
                                <MenuActionButton
                                    id={`action-${i.codi}`}
                                    entity={i}
                                    actions={actions}
                                    buttonLabel={t('common.action')}
                                    buttonProps={{
                                        startIcon:<Icon>settings</Icon>,
                                        sx: {borderRadius: 1},
                                        size: 'small',
                                        variant: "contained",
                                        disableElevation: true,
                                    }}
                                />
                            </Grid>
                        </Load>
                    </Grid>
                })}
            </Load>
        </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
}