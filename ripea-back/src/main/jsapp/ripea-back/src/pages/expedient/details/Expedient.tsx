import {useTranslation} from 'react-i18next';
import {useParams} from 'react-router-dom';
import {
    BasePage,
    useResourceApiService,
} from 'reactlib';
import {useState, useEffect} from "react";
import {Typography, Card, CardContent, Grid, Icon, IconButton, Link} from '@mui/material';
import {formatDate} from '../../../util/dateUtils.ts';
import TabComponent from "../../../components/TabComponent.tsx";
import InteressatsGrid from "../../interessats/InteressatsGrid.tsx";
import DocumentsGrid from "../../contingut/DocumentsGrid.tsx";
import TasquesGrid from "../../tasca/TasquesGrid.tsx";
import AnotacionsGrid from "../../anotacions/AnotacionsGrid.tsx";
import ExpedientActionButton from "./ExpedientActionButton.tsx";
import MetaDadaGrid from "../../dada/MetaDadaGrid.tsx";
import {StyledEstat, StyledPrioritat} from "../ExpedientGrid.tsx";
import {CommentDialog} from "../../CommentDialog.tsx";
import RemesaGrid from "../../remesa/RemesaGrid.tsx";

const ContenidoData = (props :any) => {
    const { title, children, ...other } = props;
    return <>
        <Typography sx={{ color: 'text.secondary', fontSize: 14, mt: 1, fontStyle: "italic" }}>
            {title}
        </Typography>
        <Typography variant="body2" {...other}>{children}</Typography>
    </>;
}

const border= { border: '1px solid #e3e3e3', borderRadius: '10px' };
const backgroundColor= { backgroundColor: '#f5f5f5' };

const ExpedientsRelacionats = (props:any) => {
    const { entity: expedient } = props;

    const relacionats :any[] = [
        ...expedient?.relacionatsPer ?? [],
        ...expedient?.relacionatsAmb ?? []
    ];

    return <Card sx={{ backgroundColor, border }} hidden={relacionats?.length == 0}>
        <CardContent>
            <Typography gutterBottom variant="h5" component="div" sx={{ borderBottom: '1px solid #e3e3e3' }}>
                Expedients relacionats
            </Typography>
            {
                relacionats?.map((relacionat:any) =><>
                    <Typography key={relacionat?.id} variant={"caption"}>
                        <Icon fontSize={"inherit"}>folder</Icon>
                        <Link href={`/contingut/${relacionat?.id}`}>{relacionat?.description}</Link>
                    </Typography><br/></>
                )
            }
        </CardContent>
    </Card>
}

const ExpedientInfo = (props:any) => {
    const {entity: expedient} = props;
    const { t } = useTranslation();

    return <Card sx={{ backgroundColor, border }}>
        <CardContent>
            <Typography gutterBottom variant="h5" component="div" sx={{ borderBottom: '1px solid #e3e3e3' }}>
                Informació de l'expedient
            </Typography>

            <ContenidoData title={t('page.contingut.detalle.numero')}>{expedient?.numero}</ContenidoData>
            <ContenidoData title={t('page.contingut.detalle.titol')}>{expedient?.nom}</ContenidoData>
            <ContenidoData title={t('page.contingut.detalle.metaExpedient')}>{expedient?.metaExpedient?.description}</ContenidoData>
            <ContenidoData title={t('page.contingut.detalle.organGestor')}>{expedient?.organGestor?.description}</ContenidoData>
            <ContenidoData title={t('page.contingut.detalle.fechaApertura')}>{formatDate(expedient?.ntiFechaApertura)}</ContenidoData>
            <ContenidoData title={t('page.contingut.detalle.estat')}>
                <StyledEstat entity={expedient}/>
            </ContenidoData>
            <ContenidoData title={t('page.contingut.detalle.prioritat')}>
                <StyledPrioritat entity={expedient}/>
            </ContenidoData>
            <ContenidoData title={t('page.contingut.detalle.clasificacio')}>{expedient?.ntiClasificacionSia}</ContenidoData>

            <ExpedientsRelacionats entity={expedient}/>

            <ExpedientActionButton entity={expedient}/>
        </CardContent>
    </Card>
}

const Expedient = () => {
    const { t } = useTranslation();
    const { id } = useParams();

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('expedientResource');
    const [expedient, setExpedient] = useState<any>({id: id});

    useEffect(()=>{
        if (apiIsReady) {
            appGetOne(id, {perspectives: ['COUNT', 'ESTAT', 'RELACIONAT']}).then((app) => setExpedient(app))
        }
    },[apiIsReady])

    const [numContingut, setNumContingut] = useState<number>(expedient?.numContingut);
    const [numInteressats, setNumInteressats] = useState<number>(expedient?.numInteressats);
    const [numTasques, setNumTasques] = useState<number>(expedient?.numTasques);
    const [numDades, setNumDades] = useState<number>(expedient?.numTasques);
    const [numAnotacions, setNumAnotacions] = useState<number>(expedient?.numTasques);

    const isExperientOrCarpeta=(row:any)=>{
        return row?.tipus=="EXPEDIENT" || row?.tipus=="CARPETA"
    }

    const tabs = [
        {
            value: "contingut",
            label: t('page.contingut.tabs.contingut'),
            content: <DocumentsGrid entity={expedient} onRowCountChange={setNumContingut}/>,
            badge: numContingut ?? expedient?.numContingut,
        },
        {
            value: "dades",
            label: t('page.contingut.tabs.dades'),
            content: <MetaDadaGrid entity={expedient} onRowCountChange={setNumDades}/>,
            badge: numDades ?? expedient?.numDades,
            hidden: expedient?.numMetaDades == 0,
        },
        {
            value: "interessats",
            label: t('page.contingut.tabs.interessats'),
            content: <InteressatsGrid id={id} onRowCountChange={setNumInteressats}/>,
            badge: numInteressats ?? expedient?.numInteressats,
            hidden: !isExperientOrCarpeta(expedient),
        },
        {
            value: "remeses",
            label: t('page.contingut.tabs.remeses'),
            content: <RemesaGrid id={id}/>,
            badge: expedient?.numRemeses,
            // hidden: !isExperientOrCarpeta(expedient) || expedient?.numRemeses == 0,
        },
        {
            value: "publicacions",
            label: t('page.contingut.tabs.publicacions'),
            content: <Typography>{t('page.contingut.tabs.publicacions')}</Typography>,
            badge: expedient?.numPublicacions,
            hidden: !isExperientOrCarpeta(expedient) || expedient?.numPublicacions == 0,
        },
        {
            value: "anotacions",
            label: t('page.contingut.tabs.anotacions'),
            content: <AnotacionsGrid id={id} onRowCountChange={setNumAnotacions}/>,
            badge: numAnotacions ?? expedient?.numAnotacions,
            hidden: !isExperientOrCarpeta(expedient) || expedient?.numAnotacions == 0,
        },
        {
            value: "versions",
            label: t('page.contingut.tabs.versions'),
            content: <Typography>{t('page.contingut.tabs.versions')}</Typography>,
            badge: expedient?.numVersions,
            hidden: expedient?.tipus != "DOCUMENT" || expedient?.numVersions == 0,
        },
        {
            value: "tasques",
            label: t('page.contingut.tabs.tasques'),
            content: <TasquesGrid entity={expedient} onRowCountChange={setNumTasques}/>,
            badge: numTasques ?? expedient?.numTasques,
            hidden: !isExperientOrCarpeta(expedient),
        },
    ]

    return <BasePage>
        <Card sx={{border}}>
            <CardContent sx={{backgroundColor: '#f5f5f5', borderBottom: '1px solid #e3e3e3'}}>
                <Grid container direction={'row'} columnSpacing={1} sx={{justifyContent: "space-between", alignItems: "center"}}>
                    <Grid item xs={7}><Typography variant="h5" display={"flex"} flexDirection={"row"} alignItems={"center"}>
                        <Icon>folder</Icon>{expedient?.nom}</Typography>
                    </Grid>
                    {expedient?.agafatPer && <Grid item xs={4}>
                        <Typography variant={"subtitle1"} bgcolor={"white"} sx={{border}} px={1}>
                            {t('page.expedient.title')} {t('page.expedient.detall.agafatPer')}: {expedient?.agafatPer?.description}

                            <IconButton aria-label="lock_open" color={"inherit"}>
                                <Icon>lock_open</Icon>
                            </IconButton>
                        </Typography>
                    </Grid>}
                </Grid>
            </CardContent>
            <CardContent>
                <Grid container direction={'row'} columnSpacing={1} sx={{ alignItems: "stretch" }}>
                    <Grid item xs={3}>
                        <ExpedientInfo entity={expedient}/>
                    </Grid>

                    <Grid item xs={9}>
                        <TabComponent
                            indicatorColor={"primary"}
                            textColor={"primary"}
                            aria-label="scrollable force tabs"
                            tabs={tabs}
                            variant="scrollable"
                            headerAdditionalData={<CommentDialog
                                entity={expedient}
                                title={`${t('page.comment.expedient')}: ${expedient?.nom}`}
                                resourceName={'expedientComentariResource'}
                                resourceReference={'expedient'}
                            />}
                        />
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    </BasePage>;
}

export default Expedient;