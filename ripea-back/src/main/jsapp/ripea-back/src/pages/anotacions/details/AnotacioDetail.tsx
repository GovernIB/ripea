import {Card, CardContent, Grid, Typography} from "@mui/material";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";
import {useState} from "react";
import Dialog from "../../../../lib/components/mui/Dialog.tsx";
import {BasePage, MuiGrid, useResourceApiService} from "reactlib";
import {formatDate} from "../../../util/dateUtils.ts";

const cardBorder= { border: '1px solid #e3e3e3', borderRadius: '10px' };
const cardHeader= { backgroundColor: '#f5f5f5', borderBottom: '1px solid #e3e3e3' };

const CardData = (props:any) => {
    const {title, children, xs} = props;

    return <Grid item xs={xs}>
        <Card sx={cardBorder}>
            <CardContent sx={cardHeader}>
                <Typography variant={"h5"}>{title}</Typography>
            </CardContent>
            <CardContent>{children}</CardContent>
        </Card>
    </Grid>
}

const ContenidoData = (props:any) => {
    const {title, titleXs, children, childrenXs, xs} = props;

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1} item xs={xs ?? 12}>
        <Grid item xs={titleXs ?? 4}><Typography variant={"body1"} color={'black'}>{title}</Typography></Grid>
        <Grid item xs={childrenXs ?? 8}><Typography variant={"inherit"} color={'textSecondary'}>{children}</Typography></Grid>
    </Grid>
}

const Resum = (props:any) => {
    const { entity, setNumInteressats, setNumAnnexos } = props;
    const registre = entity?.registreInfo;
    const { t } = useTranslation();

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={2}>

            {/*{ isIncorporacioJustificantActiva false &&*/}
            {/*    <ContenidoData title={'numero'} xs={6}>{registre?.identificador}</ContenidoData>*/}
            {/*    <ContenidoData title={'data'} xs={6}>{formatDate(registre?.data)}</ContenidoData>*/}
            {/*}*/}

            <ContenidoData title={t('page.registre.detall.oficina')}>{registre?.oficinaDescripcio} ({registre?.oficinaCodi})</ContenidoData>
            <ContenidoData title={t('page.registre.detall.extracte')}>{registre?.extracte}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.observacions')}>{registre?.observacions}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.identificador')} xs={4}>{registre?.identificador}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.data')} xs={4}>{formatDate(registre?.data)}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.oficinaDescripcio')} xs={4}>{registre?.oficinaDescripcio}</ContenidoData>

            <CardData xs={12} title={t('page.registre.detall.infoResumida')}>
                <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                    <ContenidoData title={t('page.registre.detall.docFisica')}>{registre?.docFisicaCodi} - {registre?.docFisicaDescripcio}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.desti')}>{registre?.destiDescripcio} ({registre?.destiCodi})</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.refExterna')}>{registre?.refExterna}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.expedientNumero')} xs={6}>{registre?.expedientNumero}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.procediment')} xs={6}>{registre?.procedimentCodi} - {entity?.metaExpedient?.description}</ContenidoData>
                </Grid>
            </CardData>

            <CardData xs={12} title={t('page.registre.detall.interessats')}>
                <Interessats entity={registre} onRowCountChange={setNumInteressats}/>
            </CardData>

            <CardData xs={12} title={t('page.registre.detall.annexos')}>
                <Annexos entity={registre} onRowCountChange={setNumAnnexos}/>
            </CardData>
        </Grid>
    </BasePage>
}

const Estat = (props:any) => {
    const { entity } = props;
    const { t } = useTranslation();

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <ContenidoData title={t('page.anotacio.detall.estatView')}>{entity?.estatView}</ContenidoData>
            <ContenidoData title={t('page.anotacio.detall.dataAlta')}>{formatDate(entity?.dataAlta)}</ContenidoData>

            {entity?.estat == 'REBUTJAT' &&
                <ContenidoData title={t('page.anotacio.detall.observacions')}>{entity?.observacions}</ContenidoData>
            }

            {entity?.estat != 'PENDENT' &&
                <>
                    <ContenidoData title={entity?.estat == 'REBUTJAT'
                        ?t('page.anotacio.detall.rejectedDate')
                        :t('page.anotacio.detall.acceptedDate')}
                    >
                        {formatDate(entity?.dataActualitzacio)}
                    </ContenidoData>

                    <ContenidoData title={t('page.anotacio.detall.usuariActualitzacio')}>{entity?.usuariActualitzacio?.description}</ContenidoData>
                </>
            }
        </Grid>
    </BasePage>
}

const InformeRegistre = (props:any) => {
    const { entity } = props;
    const { t } = useTranslation();

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <ContenidoData title={'Tipo'}>Entrada</ContenidoData>
            <ContenidoData title={t('page.registre.detall.identificador')}>{entity?.identificador}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.data')}>{formatDate(entity?.data)}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.oficina')}>{entity?.oficinaDescripcio} ({entity?.oficinaCodi})</ContenidoData>

            <CardData xs={6} title={t('page.registre.detall.required')}>
                <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                    <ContenidoData title={t('page.registre.detall.llibre')}>{entity?.llibreDescripcio} ({entity?.llibreCodi})</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.extracte')}>{entity?.extracte}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.docFisica')}>{entity?.docFisicaCodi} - {entity?.docFisicaDescripcio}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.desti')}>{entity?.destiDescripcio} ({entity?.destiCodi})</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.assumpte')}>{entity?.assumpteTipusDescripcio} ({entity?.assumpteTipusCodi})</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.idioma')}>{entity?.idiomaDescripcio} ({entity?.idiomaCodi})</ContenidoData>
                </Grid>
            </CardData>

            <CardData xs={6} title={t('page.registre.detall.optional')}>
                <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                    <ContenidoData title={t('page.registre.detall.assumpteCodi')}>{entity?.assumpteCodiCodi}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.refExterna')} xs={6}>{entity?.refExterna}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.expedientNumero')} xs={6}>{entity?.expedientNumero}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.transport')} xs={6}>{entity?.transportTipusDescripcio}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.transportNumero')} xs={6}>{entity?.transportNumero}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.origenRegistreNumero')} xs={6}>{entity?.origenRegistreNumero}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.origenData')} xs={6}>{formatDate(entity?.origenData)}</ContenidoData>
                    <ContenidoData title={t('page.registre.detall.observacions')}>{entity?.observacions}</ContenidoData>
                </Grid>
            </CardData>
        </Grid>
    </BasePage>
}

const interessatsColumns = [
    {
        field: 'tipus',
        flex: 0.5,
    },
    {
        field: 'documentNumero',
        flex: 0.5,
        valueFormatter: (value:any, row:any) => row?.documentTipus + ":" + value,
    },
    {
        field: 'nom',
        flex: 1,
        valueFormatter: (value:any, row:any) => {
            return row?.tipus == 'PERSONA_FISICA'
                ? value + " " + row?.llinatge1 + " " + row?.llinatge2
                : row?.raoSocial;
        },
    },
];

const Interessats = (props:any) => {
    const { entity, onRowCountChange } = props;

    return <MuiGrid
        resourceName="registreInteressatResource"
        columns={interessatsColumns}
        filter={`registre.id:${entity?.id}`}
        staticSortModel={[{field: 'id', sort: 'asc'}]}
        toolbarHide
        disableColumnSorting
        disableColumnMenu
        titleDisabled
        readOnly
        autoHeight
        onRowsChange={(rows) => onRowCountChange?.(rows.length)}
    />
}

const annexosColumns = [
    {
        field: 'titol',
        flex: 1,
    },
    {
        field: 'ntiTipoDocumental',
        flex: 0.5,
    },
    {
        field: 'observacions',
        flex: 1,
    },
    {
        field: 'ntiFechaCaptura',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'ntiOrigen',
        flex: 0.5,
    },
    {
        field: 'ntiEstadoElaboracion',
        flex: 0.5,
    },
    {
        field: 'annexArxiuEstat',
        flex: 0.5,
    },
];

const Annexos = (props:any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation();

    const {
        fieldDownload: apiDownload,
    } = useResourceApiService('documentResource');
    const downloadAdjunt = (id:any, row:any) :void => {
        apiDownload(row?.document?.id,{fieldName: 'adjunt'})
            .then((result) => {
                const url = URL.createObjectURL(result.blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = result.fileName; // Usa el nombre recibido
                document.body.appendChild(link);
                link.click();
                // Limpieza
                document.body.removeChild(link);
                URL.revokeObjectURL(url);
            })
    }

    const actions = [
        {
            title: t('common.download'),
            icon: "download",
            showInMenu: true,
            onClick: downloadAdjunt,
            hidden: (row:any) => !row?.document?.id,
        },
    ]

    return <MuiGrid
        resourceName="registreAnnexResource"
        columns={annexosColumns}
        filter={`registre.id:${entity?.id}`}
        staticSortModel={[{field: 'id', sort: 'asc'}]}
        toolbarHide
        disableColumnSorting
        disableColumnMenu
        titleDisabled
        rowAdditionalActions={actions}
        readOnly
        autoHeight

        onRowsChange={(rows) => onRowCountChange?.(rows.length)}
    />
}

const useAnotacioDetail = () => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const [numInteressats, setNumInteressats] = useState<number>();
    const [numAnnexos, setNumAnnexos] = useState<number>();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row);
        setEntity(row);
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    };

    const tabs = [
        {
            value: "resum",
            label: t('page.anotacio.tabs.resum'),
            content: <Resum entity={entity} setNumInteressats={setNumInteressats} setNumAnnexos={setNumAnnexos}/>,
        },
        {
            value: "estat",
            label: t('page.anotacio.tabs.estat'),
            content: <Estat entity={entity}/>,
        },
        {
            value: "registre",
            label: t('page.anotacio.tabs.registre'),
            content: <InformeRegistre entity={entity?.registreInfo}/>,
        },
        {
            value: "interessats",
            label: t('page.anotacio.tabs.interessats'),
            content: <Interessats entity={entity?.registreInfo} onRowCountChange={setNumInteressats}/>,
            badge: numInteressats,
        },
        {
            value: "annexos",
            label: t('page.anotacio.tabs.annexos'),
            content: <Annexos entity={entity?.registreInfo} onRowCountChange={setNumAnnexos}/>,
            badge: numAnnexos,
        },
    ]

    const dialog =
        <Dialog
            open={open}
            closeCallback={handleClose}
            title={t('page.anotacio.detall.title')}
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
            <TabComponent
                indicatorColor={"primary"}
                textColor={"primary"}
                aria-label="scrollable force tabs"
                tabs={tabs}
                variant="scrollable"
            />
        </Dialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

export default useAnotacioDetail;