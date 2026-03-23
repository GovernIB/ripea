import {useTranslation} from 'react-i18next';
import {useParams} from 'react-router-dom';
import {GridPage, useResourceApiService} from 'reactlib';
import {useState, useEffect} from "react";
import {Typography, Grid2 as Grid, Icon, IconButton, Link, Alert, Button, Box, Grid2} from '@mui/material';
import {formatDate} from '../../../util/dateUtils.ts';
import TabComponent from "../../../components/TabComponent.tsx";
import InteressatsGrid from "../../interessats/InteressatsGrid.tsx";
import DocumentsGrid from "../../contingut/DocumentsGrid.tsx";
import TasquesExpedientGrid from "../../tasca/TasquesExpedientGrid.tsx";
import AnotacionsExpedientGrid from "../../anotacioExpedient/AnotacionsExpedientGrid.tsx";
import ExpedientActionButton from "./ExpedientActionButton.tsx";
import MetaDadaGrid from "../../dada/MetaDadaGrid.tsx";
import {StyledEstat, StyledPrioritat} from "../ExpedientGrid.tsx";
import {ExpedientComment} from "../../CommentDialog.tsx";
import RemesaGrid from "../../remesa/RemesaGrid.tsx";
import PublicacioGrid from "../../publicacio/PublicacioGrid.tsx";
import {CardPage, DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import Load from "../../../components/Load.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {useActions} from "./CommonActions.tsx";
import useAlerta from "./Alerta.tsx";
import useErrorValidacio from "./ErrorValidacio.tsx";
import SseExpedient, {useValidacioSession} from "../../../components/SseExpedient.tsx";
import {icons} from "../../user/UserHeadToolbar.tsx";
import {setTitlePage} from "../../../TitleHeaderConfigurator.tsx";

const border= { border: '1px solid #e3e3e3', borderRadius: '4px' };

const ExpedientsRelacionats = (props:any) => {
    const { entity: expedient } = props;
    const { t } = useTranslation();

    const refresh = () => {
        window.location.reload();
    }

    const {eliminarRelacio} = useActions(refresh)

    const relacionats :any[] = [...new Set([
        ...expedient?.relacionatsPer ?? [],
        ...expedient?.relacionatsAmb ?? []
    ])];

    if (relacionats.length == 0)
        return <></>

    return <Grid2 size={12} p={1} my={2}>
        <DetailCard title={t('page.contingut.action.importarExpedient.title')} display={'flex'} flexDirection={'column'} sx={{ px: 1 }} hidden={relacionats?.length==0}>
            {
                relacionats?.map((relacionat:any) =>
                    <Grid key={relacionat?.id} container alignItems="center">
                        <Grid size={1}>
                            <Icon sx={{ fontSize: "1.3rem", paddingTop: "4px" }}>drive_file_move</Icon>
                        </Grid>
                        <Grid size={10}>
                            <Link sx={{ fontSize: "0.9rem" }} href={`./${relacionat?.id}`}>{relacionat?.description}</Link>
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
    </Grid2>
}

export const ExpedientInfo = (props:any) => {
    const {title, entity: expedient, xs, readOnly} = props;
    const { t } = useTranslation();
    return <DetailCard title={title ?? t('page.expedient.detall.title')} size={xs}>
        <DetailCardContent title={t('page.contingut.detalle.numero')} size={8}>{expedient?.numero}</DetailCardContent>
        <DetailCardContent title={t('page.contingut.detalle.clasificacio')} size={4}>{expedient?.ntiClasificacionSia}</DetailCardContent>

        <DetailCardContent title={t('page.contingut.detalle.titol')}>{expedient?.nom}</DetailCardContent>
        <DetailCardContent title={t('page.contingut.detalle.metaExpedient')}>{expedient?.metaExpedient?.description}</DetailCardContent>
        <DetailCardContent title={t('page.contingut.detalle.organGestor')}>{expedient?.organGestor?.description}</DetailCardContent>
        <DetailCardContent title={t('page.contingut.detalle.fechaApertura')}>{formatDate(expedient?.ntiFechaApertura)}</DetailCardContent>
        <DetailCardContent title={t('page.contingut.detalle.estat')} size={8} sx={{ borderBottom: "1px solid" }}><StyledEstat entity={expedient}>{t(`enum.estat.${expedient?.estat}`)}</StyledEstat></DetailCardContent>
        <DetailCardContent title={t('page.contingut.detalle.prioritat')} size={4} sx={{ borderBottom: "1px solid" }}><StyledPrioritat entity={expedient}>{t(`enum.prioritat.${expedient?.prioritat}`)}</StyledPrioritat></DetailCardContent>

        <ExpedientsRelacionats entity={expedient}/>

        {!readOnly &&
            <Grid2 size={12} display={'flex'} justifyContent={'end'} p={1}>
                <ExpedientActionButton entity={expedient}/>
            </Grid2>
        }
    </DetailCard>
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
						   <Typography variant={"subtitle2"}>{t('page.expedient.action.agafar.label')}</Typography>
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
                           <Typography variant={"subtitle2"}>{t('common.consult')}</Typography>
                       </Button>
                   }
            >{t('page.expedient.alert.alert')}</Alert>
        }
        { ((!expedient?.valid && validacio?.errorsValidacio == null) || (validacio?.errorsValidacio?.length > 0)) &&
            <Alert severity="warning"
                   action={
                       <Button sx={{py: 0}} variant="outlined"
                               onClick={() => hanldeErrorValidacio(expedient?.id, expedient)}>
                            <Icon>search</Icon>
                           <Typography variant={"subtitle2"}>{t('common.consult')}</Typography>
                       </Button>
                   }
            >{t('page.expedient.alert.validation')}</Alert>
        }
        {dialogAlert}
        {dialogErrorValidacio}
    </>
}

const perspectives = ['COUNT', 'ESTAT', 'RELACIONAT', 'AMB_PINBAL', "META_EXPEDIENT", "PERMIS_CONTINGUT"]
const Expedient = () => {
    const { t } = useTranslation();
    const { id } = useParams();

    const refresh = () => {
        window.location.reload();
    }

    const {value: user} = useUserSession();
    const {alliberar} = useActions(refresh);

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('expedientResource');
    const [expedient, setExpedient] = useState<any>();

    useEffect(()=>{
        if (apiIsReady) {
            appGetOne(id, {perspectives}).then((app) => setExpedient(app))
        }
    },[apiIsReady])

    useEffect(() => {
        if (expedient) {
            setTitlePage(expedient?.nom)
        }
    }, [expedient]);

    const [numContingut, setNumContingut] = useState<number>(expedient?.numContingut);
    const [numInteressats, setNumInteressats] = useState<number>(expedient?.numInteressats);
    const [numTasques, setNumTasques] = useState<number>(expedient?.numTasques);
    const [numDades, setNumDades] = useState<number>(expedient?.numDades);
    const [numAnotacions, setNumAnotacions] = useState<number>(expedient?.numAnotacions);
    const [numRemeses, setNumRemeses] = useState<number>(expedient?.numRemeses);
    const [numPublicacions, setNumPublicacions] = useState<number>(expedient?.numPublicacions);

    const tabs = [
        {
            value: "contingut",
            label: t('page.contingut.tabs.contingut'),
            content: <DocumentsGrid entity={expedient} onRowCountChange={setNumContingut}/>,
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
            content: <AnotacionsExpedientGrid id={id} onRowCountChange={setNumAnotacions}/>,
            badge: numAnotacions ?? expedient?.numAnotacions,
            hidden: !expedient?.numAnotacions,
            showZero: true,
        },
        {
            value: "tasques",
            label: t('page.contingut.tabs.tasques'),
            content: <TasquesExpedientGrid entity={expedient} onRowCountChange={setNumTasques}/>,
            badge: numTasques ?? expedient?.numTasques,
            showZero: true,
        },
    ]

    const headerMain = <>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Icon sx={{ fontSize: '2rem' }}>{icons.expedient}</Icon>
            <Typography variant="h4" sx={{ display: 'flex' }}>{expedient?.nom}</Typography>
        </Box>
        <Box>
            <Typography variant={"subtitle1"} sx={{border}} px={2} hidden={!expedient?.agafatPer}>
                {t('page.expedient.title')} {t('page.expedient.detall.agafatPer')}: {expedient?.agafatPer?.description}
                {expedient?.agafatPer?.id == user?.codi &&
                    <IconButton aria-label="lock_open" color={"inherit"} onClick={() => alliberar(id, expedient)} title={t('page.expedient.action.lliberar.label')}>
                        <Icon>lock_open</Icon>
                    </IconButton>
                }
            </Typography>
        </Box>
    </>;
    return <GridPage disableMargins>
        <SseExpedient id={id}/>
        <Load value={expedient} noEffect>
            <CardPage header={headerMain} componentProps={{ justifyContent: 'space-between' }}>
                <Grid container spacing={2}>
                    <Grid size={3}>
                        <ExpedientInfo entity={expedient} />
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