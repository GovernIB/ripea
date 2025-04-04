import {useTranslation} from 'react-i18next';
import {useParams} from 'react-router-dom';
import {
    BasePage, GridPage,
    useResourceApiService,
} from 'reactlib';
import {useState, useEffect} from "react";
import {Typography, Grid, Icon, IconButton, Link} from '@mui/material';
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
import PublicacioGrid from "../../publicacio/PublicacioGrid.tsx";
import {CardData, ContenidoData} from "../../../components/DetailComponents.tsx";

const Contenido = (props :any) => {
    const { title, children } = props;
    return <ContenidoData
        title={title}
        componentTitleProps={{ color: 'text.secondary', fontSize: 14, fontStyle: "italic" }}
        componentTextProps={{ color: 'text.primary', wordWrap: "break-word" }}
        direction={'column'}
    >
        {children}
    </ContenidoData>
}

const border= { border: '1px solid #e3e3e3', borderRadius: '10px' };

const ExpedientsRelacionats = (props:any) => {
    const { entity: expedient } = props;

    const relacionats :any[] = [
        ...expedient?.relacionatsPer ?? [],
        ...expedient?.relacionatsAmb ?? []
    ];

    return <CardData title={'Expedients relacionats'} hidden={relacionats?.length==0}>
        {
            relacionats?.map((relacionat:any) =><>
                <Typography key={relacionat?.id} variant={"caption"}>
                    <Icon fontSize={"inherit"}>folder</Icon>
                    <Link href={`/contingut/${relacionat?.id}`}>{relacionat?.description}</Link>
                </Typography><br/></>
            )
        }
    </CardData>
}

const ExpedientInfo = (props:any) => {
    const {entity: expedient} = props;
    const { t } = useTranslation();

    return <CardData title={"Informació de l'expedient"} direction={'column'}>
        <Contenido title={t('page.contingut.detalle.numero')} direction={'column'}>{expedient?.numero}</Contenido>
        <Contenido title={t('page.contingut.detalle.titol')} direction={'column'}>{expedient?.nom}</Contenido>
        <Contenido title={t('page.contingut.detalle.metaExpedient')} direction={'column'}>{expedient?.metaExpedient?.description}</Contenido>
        <Contenido title={t('page.contingut.detalle.organGestor')} direction={'column'}>{expedient?.organGestor?.description}</Contenido>
        <Contenido title={t('page.contingut.detalle.fechaApertura')} direction={'column'}>{formatDate(expedient?.ntiFechaApertura)}</Contenido>
        <Contenido title={t('page.contingut.detalle.estat')} direction={'column'}>
            <StyledEstat entity={expedient}/>
        </Contenido>
        <Contenido title={t('page.contingut.detalle.prioritat')} direction={'column'}>
            <StyledPrioritat entity={expedient}/>
        </Contenido>
        <Contenido title={t('page.contingut.detalle.clasificacio')} direction={'column'}>{expedient?.ntiClasificacionSia}</Contenido>

        <ExpedientsRelacionats entity={expedient}/>

        <Grid item xs={12} display={'flex'} justifyContent={'end'}>
            <ExpedientActionButton entity={expedient}/>
        </Grid>
    </CardData>
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
            appGetOne(id, {perspectives: ['COUNT', 'ESTAT', 'RELACIONAT', 'INTERESSATS_RESUM']}).then((app) => setExpedient(app))
        }
    },[apiIsReady])

    const [numContingut, setNumContingut] = useState<number>(expedient?.numContingut);
    const [numInteressats, setNumInteressats] = useState<number>(expedient?.numInteressats);
    const [numTasques, setNumTasques] = useState<number>(expedient?.numTasques);
    const [numDades, setNumDades] = useState<number>(expedient?.numTasques);
    const [numAnotacions, setNumAnotacions] = useState<number>(expedient?.numTasques);
    const [numRemeses, setNumRemeses] = useState<number>(expedient?.numRemeses);
    const [numPublicacions, setNumPublicacions] = useState<number>(expedient?.numPublicacions);

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
            hidden: !expedient?.numMetaDades,
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
            content: <RemesaGrid id={id} onRowCountChange={setNumRemeses}/>,
            badge: numRemeses ?? expedient?.numRemeses,
            hidden: !isExperientOrCarpeta(expedient) || !expedient?.numRemeses,
        },
        {
            value: "publicacions",
            label: t('page.contingut.tabs.publicacions'),
            content: <PublicacioGrid id={id} onRowCountChange={setNumPublicacions}/>,
            badge: numPublicacions ?? expedient?.numPublicacions,
            hidden: !isExperientOrCarpeta(expedient) || !expedient?.numPublicacions,
        },
        {
            value: "anotacions",
            label: t('page.contingut.tabs.anotacions'),
            content: <AnotacionsGrid id={id} onRowCountChange={setNumAnotacions}/>,
            badge: numAnotacions ?? expedient?.numAnotacions,
            hidden: !isExperientOrCarpeta(expedient) || !expedient?.numAnotacions,
        },
        {
            value: "versions",
            label: t('page.contingut.tabs.versions'),
            content: <Typography>{t('page.contingut.tabs.versions')}</Typography>,
            badge: expedient?.numVersions,
            hidden: expedient?.tipus != "DOCUMENT" || !expedient?.numVersions,
        },
        {
            value: "tasques",
            label: t('page.contingut.tabs.tasques'),
            content: <TasquesGrid entity={expedient} onRowCountChange={setNumTasques}/>,
            badge: numTasques ?? expedient?.numTasques,
            hidden: !isExperientOrCarpeta(expedient),
        },
    ]

    return <GridPage>
        <CardData header={
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
        }>
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
        </CardData>
    </GridPage>
}

export default Expedient;