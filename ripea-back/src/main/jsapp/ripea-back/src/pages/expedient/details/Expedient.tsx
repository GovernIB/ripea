import {useTranslation} from 'react-i18next';
import {useNavigate, useParams, Link as RrLink} from 'react-router-dom';
import {GridPage, useBaseAppContext, useResourceApiService} from 'reactlib';
import {useState, useEffect} from "react";
import {Typography, Grid, Icon, IconButton, Link, Alert, Button, Box, alpha} from '@mui/material';
import {formatDate} from '../../../util/dateUtils.ts';
import TabComponent from "../../../components/TabComponent.tsx";
import InteressatsGrid from "../../interessats/InteressatsGrid.tsx";
import DocumentsGrid from "../../contingut/DocumentsGrid.tsx";
import Carpeta from "../../carpeta/details/Carpeta.tsx";
import ContingutBreadcrumb from "./ContingutBreadcrumb.tsx";
import TasquesExpedientGrid from "../../tasca/TasquesExpedientGrid.tsx";
import AnotacionsExpedientGrid from "../../anotacioExpedient/AnotacionsExpedientGrid.tsx";
import ExpedientActionButton from "./ExpedientActionButton.tsx";
import MetaDadaGrid from "../../dada/MetaDadaGrid.tsx";
import {StyledEstat, StyledPrioritat} from "../ExpedientGrid.tsx";
import {ExpedientComment} from "../../CommentDialog.tsx";
import RemesaGrid from "../../remesa/RemesaGrid.tsx";
import PublicacioGrid from "../../publicacio/PublicacioGrid.tsx";
import {CardPage, DetailCard} from "../../../components/CardData.tsx";
import Load from "../../../components/Load.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {useActions} from "./CommonActions.tsx";
import useAlerta from "./Alerta.tsx";
import useErrorValidacio, { getResumErrorsText } from "./ErrorValidacio.tsx";
import SseExpedient, {useValidacioSession} from "../../../components/SseExpedient.tsx";
import {icons} from "../../user/UserHeadToolbar.tsx";
import {setTitlePage} from "../../../TitleHeaderConfigurator.tsx";
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import {ErrorPage} from "../../../components/ErrorPage.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const border= { border: '1px solid #e3e3e3', borderRadius: '4px' };

const ExpedientsRelacionats = (props:any) => {
    const { entity: expedient } = props;
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        find: apiFind,
    } = useResourceApiService('expedientResource');
    const {temporalMessageShow} = useBaseAppContext();
    const [relacionats, setRelacionats] = useState<any[]>([]);

    const findRelacionats = () => {
        apiFind({
            filter: builder.or(
                builder.exists(builder.eq('relacionatsPer.id', expedient?.id)),
                builder.exists(builder.eq('relacionatsAmb.id', expedient?.id)),
            ),
            unpaged: true,
        })
            .then((result) => {
                setRelacionats(result?.rows)
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
                setRelacionats([])
            });
    }

    const {eliminarRelacio} = useActions(findRelacionats)

    useEffect(() => {
        if (apiIsReady) {
            findRelacionats()
        }
    }, [apiIsReady, expedient]);

    return <Load value={relacionats.length > 0} noEffect>
        <Grid size={12} p={1} my={1}>
        <DetailCard title={t('page.contingut.action.importarExpedient.title')} display={'flex'} flexDirection={'column'} sx={{ px: 1 }} hidden={relacionats?.length==0}>
            {
                relacionats?.map((relacionat:any) =>
                    <Grid key={relacionat?.id} container display={'flex'} alignItems="center">
                        <Grid size={1}>
                            <Icon sx={{ fontSize: "1.3rem", paddingTop: "4px" }}>drive_file_move</Icon>
                        </Grid>
                        <Grid size={10}>
                            <Link sx={{ fontSize: "0.9rem" }} href={`./${relacionat?.id}`}>{relacionat?.nom}</Link>
                        </Grid>
                        <Grid size={1}>
                            {expedient?.potModificar &&
                                <IconButton
                                    onClick={()=>eliminarRelacio(expedient?.id, expedient, relacionat?.id)}
                                    title={t('page.expedient.action.eliminarRelacio.label')}
                                    sx={{ color: 'black'}}>
                                    <Icon sx={{ fontSize: "1.3rem" }}>link_off</Icon>
                                </IconButton>
                            }
                        </Grid>
                    </Grid>
                )
            }
        </DetailCard>
    </Grid>
    </Load>
}

export const ExpedientInfo = (props:any) => {
    const {title, entity: expedient, fields, xs, readOnly} = props;
    const { t } = useTranslation();

    return <MuiDetail entity={expedient} fields={fields}><DetailCard title={title ?? t('page.expedient.detall.title')} size={xs}>
        <FieldData size={8} field={"numero"}/>
        <FieldData size={4} field={"ntiClasificacionSia"}/>
        <FieldData field={"nom"}/>
        <FieldData field={"metaExpedient"}/>
        <FieldData field={"organGestor"}/>
        <FieldData field={"ntiFechaApertura"}>{formatDate(expedient?.ntiFechaApertura)}</FieldData>
        <FieldData size={8} sx={{ borderBottom: "1px solid" }} field={"estat"} renderCell={(formattedValue:string) => <StyledEstat entity={expedient}>{formattedValue}</StyledEstat>}/>
        <FieldData size={4} sx={{ borderBottom: "1px solid" }} field={"prioritat"} renderCell={(formattedValue:string) => <StyledPrioritat entity={expedient}>{formattedValue}</StyledPrioritat>}/>

        {expedient?.grup && <FieldData field={"grup"}/>}

        <ExpedientsRelacionats entity={expedient}/>

        {!readOnly &&
            <Grid size={12} display={'flex'} justifyContent={'end'} p={1}>
                <ExpedientActionButton entity={expedient}/>
            </Grid>
        }
    </DetailCard></MuiDetail>
}

const ExpedientAlert = (props:any) => {
    const {entity: expedient} = props;
    const { t } = useTranslation();
    const {value: user, rol} = useUserSession();
    const {value: validacio} = useValidacioSession()

    const refresh = () => {
        window.location.reload();
    }

    const {agafar} = useActions(refresh);
    const {handleOpen: handelAlert, dialog: dialogAlert, count} = useAlerta();
    const {handleOpen: hanldeErrorValidacio, dialog: dialogErrorValidacio} = useErrorValidacio();

    return <>
        {expedient?.agafatPer?.id != user?.codi && expedient?.usuariActualWrite && !rol?.isAdminLectura && user?.rolActual != 'IPA_ADMIN' &&
            <Alert severity="info"
                   action={
                       <Button sx={{py:0}}
                       onClick={()=>agafar(expedient?.id, expedient)} variant="outlined">
                           <Icon>lock</Icon>
						   <Typography variant={"subtitle2"} component="span">{t('page.expedient.action.agafar.label')}</Typography>
                       </Button>
                   }
            >{t('page.expedient.alert.owner')}</Alert>
        }
        { expedient?.estat == "OBERT" && expedient?.hasEsborranys && user?.sessionScope?.isConvertirDefinitiuActiu &&
            <Alert severity="info">{t('page.expedient.alert.esborranys')}</Alert>
        }
		{ expedient?.estat == "OBERT" && expedient?.pendentExecucioMassiva &&
		    <Alert severity="warning">{t('page.expedient.alert.moureTot.info')}</Alert>
		}
        { expedient?.numAlert!=0 && (count === null || count !== 0) &&
            <Alert severity="warning"
                   action={
                       <Button sx={{py: 0}} variant="outlined"
                               onClick={() => handelAlert(expedient?.id, expedient)}>
                            <Icon>search</Icon>
                           <Typography variant={"subtitle2"} component="span">{t('common.consult')}</Typography>
                       </Button>
                   }
            >{t('page.expedient.alert.alert')}</Alert>
        }
        { 
        ((!expedient?.valid && validacio?.errorsValidacio == null) || (validacio?.errorsValidacio?.length > 0)) && (
            (() => {
                const llistaErrors = validacio?.errorsValidacio?.length > 0 
                    ? validacio.errorsValidacio 
                    : (expedient?.errors || []);

                const textAlerta = llistaErrors.length > 0 
                    ? getResumErrorsText(llistaErrors, t)
                    : t('page.expedient.alert.validation');

                return (
                    <Alert 
                        severity="warning"
                        action={
                            <Button sx={{py: 0}} variant="outlined"
                                    onClick={() => hanldeErrorValidacio(expedient?.id, expedient)}>
                                <Icon>search</Icon>
                                <Typography variant={"subtitle2"} component="span">{t('common.consult')}</Typography>
                            </Button>
                        }
                    >
                        {textAlerta}
                    </Alert>
                );
            })()
        )
    }
        {dialogAlert}
        {dialogErrorValidacio}
    </>
}

const CARPETA_PATH_PERSPECTIVES = ['PATH', 'RESTRICCIONS', 'RESPONSABLE_RESTRICCIO'];

const perspectives = ['BASIC', 'AVISOS', 'COUNT', 'ESTAT', 'AMB_PINBAL', "META_EXPEDIENT", "PERMIS_CONTINGUT", "AUDITORIA"];
const Expedient = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const navigate = useNavigate();
    const [error, setError] = useState<any>();

    const refresh = () => {
        window.location.reload();
    }

    const {value: user} = useUserSession();
    const {alliberar} = useActions(refresh);

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
        currentFields: fields
    } = useResourceApiService('expedientResource');
    const {
        isReady: apiCarpetaReady,
        getOne: carpetaGetOne,
    } = useResourceApiService('carpetaResource');

    const [expedient, setExpedient] = useState<any>();
    const [carpetaNode, setCarpetaNode] = useState<any>();

    useEffect(() => {
        if (!apiIsReady || id == null) {
            return;
        }
        let cancelled = false;
        setError(undefined);

        const load = async () => {
            try {
                const app = await appGetOne(id, { perspectives });
                if (cancelled) {
                    return;
                }
                setCarpetaNode(null);
                setExpedient(app);
                return;
            } catch {
                /* id no és d'un expedient */
            }

            if (user.sessionScope?.isContingutCarpetaDetallAccesActiva !== true) {
                if (!cancelled) {
                    navigate('/expedient', { replace: true });
                }
                return;
            }

            if (!apiCarpetaReady) {
                return;
            }

            try {
                const carpeta = await carpetaGetOne(id, { perspectives: CARPETA_PATH_PERSPECTIVES });
                if (cancelled) {
                    return;
                }
                const expedientId = carpeta?.expedient?.id;
                if (expedientId == null) {
                    if (!cancelled) {
                        navigate('/expedient', { replace: true });
                    }
                    return;
                }
                const app = await appGetOne(expedientId, { perspectives });
                if (cancelled) {
                    return;
                }
                setCarpetaNode(carpeta);
                setExpedient(app);
            } catch {
                if (!cancelled) {
                    navigate('/expedient', { replace: true });
                }
            }
        };

        void load();
        return () => {
            cancelled = true;
        };
    }, [apiIsReady, apiCarpetaReady, id, navigate, user, user?.sessionScope?.isContingutCarpetaDetallAccesActiva]);

    useEffect(() => {
        if (expedient) {
            const title = carpetaNode?.nom
                ? `${expedient?.nom} — ${carpetaNode.nom}`
                : expedient?.nom;
            setTitlePage(title);
        }
    }, [expedient, carpetaNode]);

    const [numContingut, setNumContingut] = useState<number>(expedient?.numContingut);
    const [numInteressats, setNumInteressats] = useState<number>(expedient?.numInteressats);
    const [numTasques, setNumTasques] = useState<number>(expedient?.numTasques);
    const [numDades, setNumDades] = useState<number>(expedient?.numDades);
    const [numAnotacions, setNumAnotacions] = useState<number>(expedient?.numAnotacions);
    const [numRemeses, setNumRemeses] = useState<number>(expedient?.numRemeses);
    const [numPublicacions, setNumPublicacions] = useState<number>(expedient?.numPublicacions);

    if (error)
        return <ErrorPage error={error}/>

    const isCarpetaUrl = carpetaNode != null;

    const tabs = [
        {
            value: "contingut",
            label: t('page.contingut.tabs.contingut'),
            content: isCarpetaUrl && id != null
                ? <Carpeta key={`carpeta-${id}`} expedient={expedient} carpetaId={id} onRowCountChange={setNumContingut}/>
                : <DocumentsGrid key={`expedient-${expedient?.id}`} entity={expedient} onRowCountChange={setNumContingut}/>,
            badge: numContingut ?? expedient?.numContingut,
            showZero: true,
        },
        {
            value: "dades",
            label: t('page.contingut.tabs.dades'),
            content: <MetaDadaGrid entity={expedient} onRowCountChange={setNumDades}/>,
            badge: numDades ?? expedient?.numDades,
            hidden: !expedient?.numMetaDades,
            showZero: true,
        },
        {
            value: "interessats",
            label: t('page.contingut.tabs.interessats'),
            content: <InteressatsGrid entity={expedient} num={numInteressats ?? expedient?.numInteressats} onRowCountChange={setNumInteressats}/>,
            badge: numInteressats ?? expedient?.numInteressats,
            showZero: true,
        },
        {
            value: "remeses",
            label: t('page.contingut.tabs.remeses'),
            content: <RemesaGrid entity={expedient} onRowCountChange={setNumRemeses}/>,
            badge: numRemeses ?? expedient?.numRemeses,
            hidden: !expedient?.numRemeses,
            showZero: true,
        },
        {
            value: "publicacions",
            label: t('page.contingut.tabs.publicacions'),
            content: <PublicacioGrid entity={expedient} onRowCountChange={setNumPublicacions}/>,
            badge: numPublicacions ?? expedient?.numPublicacions,
            hidden: !expedient?.numPublicacions,
            showZero: true,
        },
        {
            value: "anotacions",
            label: t('page.contingut.tabs.anotacions'),
            content: <AnotacionsExpedientGrid id={expedient?.id} onRowCountChange={setNumAnotacions}/>,
            badge: numAnotacions ?? expedient?.numAnotacions,
            hidden: !expedient?.numAnotacions,
            showZero: true,
        },
        {
            value: "tasques",
            label: t('page.contingut.tabs.tasques'),
            content: <TasquesExpedientGrid entity={expedient} onRowCountChange={setNumTasques}/>,
            badge: numTasques ?? expedient?.numTasques,
            hidden: expedient?.hideTasca,
            showZero: true,
        },
    ]

    const headerMain = <>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', columnGap: 1, rowGap: 0.5, width: '100%' }}>
            <Icon sx={{ fontSize: '2rem', color: 'primary.light' }}>{icons.expedient}</Icon>
            <Typography variant="h4" component="h1" sx={{ lineHeight: 1.2, flexShrink: 0 }}>
                {isCarpetaUrl && expedient?.id != null ? (
                    <Link
                        component={RrLink}
                        to={`/contingut/${expedient.id}`}
                        underline="hover"
                        color="inherit"
                        sx={{ fontWeight: 'inherit', fontSize: 'inherit', lineHeight: 'inherit' }}
                    >
                        {expedient.nom}
                    </Link>
                ) : (
                    expedient?.nom
                )}
            </Typography>
            {isCarpetaUrl && (
                <Icon sx={{ fontSize: '1.5rem', color: 'text.secondary', flexShrink: 0 }} aria-hidden>
                    chevron_right
                </Icon>
            )}
            <ContingutBreadcrumb expedient={expedient} carpetaNode={isCarpetaUrl ? carpetaNode : null} />
        </Box>
        <Box>
            <Typography variant={"subtitle1"} component="p" sx={{border, flexShrink: 0, whiteSpace: 'nowrap', bgcolor: (theme) => alpha(theme.palette.common.white, 0.9), color: (theme) => theme.palette.getContrastText(theme.palette.common.white)}} px={2} hidden={!expedient?.agafatPer}>
                {t('page.expedient.title')} {t('page.expedient.detall.agafatPer')}: {expedient?.agafatPer?.description}
                {expedient?.agafatPer?.id == user?.codi &&
                    <IconButton aria-label="lock_open" color={"inherit"} onClick={() => alliberar(expedient?.id, expedient)} title={t('page.expedient.action.lliberar.label')}>
                        <Icon>lock_open</Icon>
                    </IconButton>
                }
            </Typography>
        </Box>
    </>;
    return <GridPage autoHeight>
        {expedient?.id != null && <SseExpedient id={expedient.id}/>}
        <Load value={expedient} noEffect>
            <CardPage header={headerMain} componentProps={{ justifyContent: 'space-between' }}>
                <Grid container spacing={2}>
                    <Grid size={3}>
                        <ExpedientInfo entity={expedient} fields={fields} />
                    </Grid>
                    <Grid size={9}>
                        <ExpedientAlert entity={expedient} />
                        <Box>
                            <TabComponent
                                tabs={tabs}
                                variant="scrollable"
                                headerAdditionalData={expedient?.potModificar
                                    ?<ExpedientComment entity={expedient}/> : <></>}
                            />
                        </Box>
                    </Grid>
                </Grid>
            </CardPage>
        </Load>
    </GridPage>;
}

export default Expedient;