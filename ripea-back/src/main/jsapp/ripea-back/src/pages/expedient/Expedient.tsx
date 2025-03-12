import {useTranslation} from 'react-i18next';
import {useParams} from 'react-router-dom';
import {
    BasePage,
    useResourceApiService,
} from 'reactlib';
import React, {useState} from "react";
import {Box, Typography, Card, CardContent, Grid, Icon, IconButton} from '@mui/material';
import {formatDate} from '../../util/dateUtils';
import TabComponent from "../../components/TabComponent";
import InteressatsGrid from "./detall/InteressatsGrid.tsx";
import DocumentsGrid from "./detall/DocumentsGrid.tsx";
import TasquesGrid from "./detall/TasquesGrid.tsx";
import AnotacionsGrid from "./detall/AnotacionsGrid.tsx";
import ExpedientActionButton from "./ExpedientActionButton.tsx";
import CommentDialog from "./CommentDialog.tsx";

const CardProp = (props :any) => {
    const { title, children, ...other } = props;
    return <>
        <Typography sx={{ color: 'text.secondary', fontSize: 14, mt: 1, fontStyle: "italic" }}>
            {title}
        </Typography>
        <Typography variant="body2" {...other}>{children}</Typography>
    </>;
}

const Expedient: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();

    const {
        isReady: appApiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('expedientResource');
    const [expedient, setExpedient] = useState<any>();
    if (appApiIsReady && !expedient) {
        appGetOne(id, {perspectives: ['COUNT']}).then((app) => setExpedient(app))
    }

    const border= { border: '1px solid #e3e3e3', borderRadius: 10 };
    const backgroundColor= { backgroundColor: '#f5f5f5' };
    const [numContingut, setNumContingut] = useState<number>(expedient?.numContingut);
    const [numInteressats, setNumInteressats] = useState<number>(expedient?.numInteressats);

    const tabs = [
        {
            value: "contingut",
            label: t('page.contingut.tabs.contingut'),
            content: <DocumentsGrid id={id} onRowCountChange={setNumContingut}/>,
            badge: numContingut ?? expedient?.numContingut,
        },
        {
            value: "dades",
            label: t('page.contingut.tabs.dades'),
            content: <Typography>{t('page.contingut.tabs.dades')}</Typography>,
            badge: expedient?.numDades,
        },
        {
            value: "interessats",
            label: t('page.contingut.tabs.interessats'),
            content: <InteressatsGrid id={id} onRowCountChange={setNumInteressats}/>,
            badge: numInteressats ?? expedient?.numInteressats,
        },
        {
            value: "remeses",
            label: t('page.contingut.tabs.remeses'),
            content: <Typography>{t('page.contingut.tabs.remeses')}</Typography>,
            badge: expedient?.numRemeses,
        },
        {
            value: "publicacions",
            label: t('page.contingut.tabs.publicacions'),
            content: <Typography>{t('page.contingut.tabs.publicacions')}</Typography>,
            badge: expedient?.numPublicacions,
        },
        {
            value: "anotacions",
            label: t('page.contingut.tabs.anotacions'),
            content: <AnotacionsGrid/>,
            badge: expedient?.numAnotacions,
        },
        {
            value: "versions",
            label: t('page.contingut.tabs.versions'),
            content: <Typography>{t('page.contingut.tabs.versions')}</Typography>,
            badge: expedient?.numVersions,
        },
        {
            value: "tasques",
            label: t('page.contingut.tabs.tasques'),
            content: <TasquesGrid/>,
            badge: expedient?.numTasques,
        },
    ]

    return expedient && <BasePage>
        <div style={border}>
            <Box sx={{backgroundColor, borderBottom: '1px solid #e3e3e3', borderTopRightRadius: 10, borderTopLeftRadius: 10, p: 1}}>
                <Grid container sx={{
                    direction: "row",
                    columnSpacing:1,
                    rowSpacing:1,
                    justifyContent:"space-between",
                    alignItems:"center"
                }} >
                    <Grid item xs={7}><Typography variant="h5" display={"flex"} flexDirection={"row"} alignItems={"center"}>
                        <Icon>folder</Icon>{expedient.nom}</Typography>
                    </Grid>
                    {expedient?.agafatPer && <Grid item xs={4} >
                        <Typography variant={"subtitle1"} bgcolor={"white"} sx={{border}} px={1}>
                            Expediente cogido por: {expedient?.agafatPer?.description}

                                <IconButton aria-label="lock_open" color={"inherit"}>
                                    <Icon>lock_open</Icon>
                                </IconButton>
                        </Typography>
                    </Grid>}
                </Grid>
            </Box>

            <Grid container direction={"row"} sx={{ p: 1, alignItems: "stretch" }}>
                <Grid item xs={3}>
                    <Card sx={{ backgroundColor, border }}>
                        <CardContent>
                            <Typography gutterBottom variant="h5" component="div" sx={{ borderBottom: '1px solid #e3e3e3' }}>
                                Informació de l'expedient
                            </Typography>

                            <CardProp title={t('page.contingut.detalle.numero')}>{expedient.numero}</CardProp>
                            <CardProp title={t('page.contingut.detalle.titol')}>{expedient.nom}</CardProp>
                            <CardProp title={t('page.contingut.detalle.metaExpedient')}>{expedient.metaExpedient.description}</CardProp>
                            <CardProp title={t('page.contingut.detalle.organGestor')}>{expedient.organGestor.description}</CardProp>
                            <CardProp title={t('page.contingut.detalle.fechaApertura')}>{formatDate(expedient.ntiFechaApertura)}</CardProp>
                            <CardProp title={t('page.contingut.detalle.estat')}
                                      sx={{borderLeft: `3px solid ${'red'}`, pl: 1}}>{expedient.estat}</CardProp>
                            <CardProp title={t('page.contingut.detalle.prioritat')}
                                      sx={{borderLeft: `3px solid ${'green'}`, pl: 1}}>{expedient.prioritat}</CardProp>
                            <CardProp title={t('page.contingut.detalle.clasificacio')}>{expedient.ntiClasificacionSia}</CardProp>

                            <ExpedientActionButton entity={expedient}/>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={9}>
                    <TabComponent
                        indicatorColor={"primary"}
                        textColor={"primary"}
                        aria-label="scrollable force tabs"
                        tabs={tabs}
                        variant="scrollable"
                        headerAdditionalData={<CommentDialog entity={expedient}/>}
                    />
                </Grid>
            </Grid>
        </div>
    </BasePage>;
}

export default Expedient;