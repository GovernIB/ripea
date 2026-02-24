import {useTranslation} from "react-i18next";
import {MuiDialog, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useEffect, useMemo, useRef, useState} from "react";
import StyledMuiFilter from "../../../../components/StyledMuiFilter.tsx";
import GridFormField, {GridButton} from "../../../../components/GridFormField.tsx";
import {Alert, Grid, Icon, Typography} from "@mui/material";
import * as builder from "../../../../util/springFilterUtils.ts";
import {useSession} from "../../../../components/SessionStorageContext.tsx";
import Load from "../../../../components/Load.tsx";
import {getAlertSeverity} from "../../../../components/BaseApp.tsx";
import {MenuActionButton} from "../../../../components/MenuButton.tsx";

const useActions = () => {
    const {t} = useTranslation()
    const {
        isReady: apiIsReady,
        artifactAction: apiAction
    } = useResourceApiService('integracioResource');
    const {temporalMessageShow} = useBaseAppContext();

    const intervalRef = useRef<number | null>(null);

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
            let count = 0;

            intervalRef.current = setInterval(() => {
                apiDiagnostic(integracions[count], entitat, organ)

                count++;
                if (count >= integracions.length) {
                    clearInterval(intervalRef.current);
                }
                return () => clearInterval(intervalRef.current)
            }, 500);
        }
    }

    const apiDiagnostic = (codiIntegracio:any, entitat:any, organ:any) => {
        apiAction(undefined, {code: 'DIAGNOSTIC_PLUGIN', data: {codiIntegracio, entitat, organ}})
            .then((response) => putDiagnostic(codiIntegracio, response))
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
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
        clearInterval(intervalRef.current)
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
        <GridFormField xs={6} name="entitat"/>
        <GridFormField xs={6} name="organ" filter={organFilter}/>
        <GridButton xs={2} onClick={apiReiniciar} variant={'contained'} color={'warning'}>
            <Icon>cached</Icon>{t('page.integracio.action.reiniciarAll.label')}
        </GridButton>
        <Grid xs={7.6}/>
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
                sx: { borderRadius: '4px' },
            },
        },
        {
            value: 'search',
            text: t('page.integracio.action.diagnosticAll.label'),
            icon: 'filter_alt',
            componentProps: {
                variant: "contained",
                sx: { borderRadius: '4px' },
            },
        },
    ]

    return <StyledMuiFilter
        resourceName={"integracioResource"}
        code={"FILTER_PLUGIN"}
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
    const {value: filter} = useSession('FILTER_PLUGIN');

    const {apiIsReady, diagnostic, apiDiagnosticAll, apiDiagnostic, apiReiniciar, handleClose: hClose} = useActions()

    const handleOpen = () => {
        if (apiIsReady) {
            setOpen(true);
        }
    }

    useEffect(() => {
        if (apiIsReady && open) {
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
        },
    ]

    const actions = [
        {
            label: t('page.integracio.action.reiniciar.label'),
            icon: 'edit',
            onClick: (row:any) => apiDiagnostic(row.codi, filter?.entitat, filter?.organ)
        },
        {
            label: t('page.integracio.action.reiniciar.label'),
            icon: 'settings',
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

            <Load value={integracions}>
                {integracions?.map?.(i => {
                    const d = diagnostic.get(i.codi)
                    return <Grid container direction={"row"} display={'flex'} alignItems={'center'} columnSpacing={1}>
                        <Grid item xs={4}><Typography variant={"body1"} ml={1}>{i.nom || i.codi}</Typography></Grid>
                        <Load value={d}>
                            <Grid item xs={6.5}>
                                <Alert severity={getAlertSeverity(d?.nivell)}>{d?.missatge}</Alert>
                            </Grid>
                            <Grid item xs={1.5}>
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