import {useState} from "react";
import {Grid, Icon} from "@mui/material";
import {BasePage, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useUserSession} from "../../../components/Session.tsx";
import {useActions, useAnexxActions} from "./AnotacioActions.tsx";
import useRegistreInteressatDetail from "./RegistreInteressatDetail.tsx";
import useVisualitzar from "../actions/Visualitzar.tsx";
import useAnnexFirma from "./AnnexFirma.tsx";
import {icons} from "../../user/UserHeadToolbar.tsx";

const Resum = (props:any) => {
    const { entity, setNumInteressats, setNumAnnexos } = props;
    const registre = entity?.registreInfo;
    const { t } = useTranslation();

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <ContenidoData title={t('page.registre.detall.identificador')}      xs={6} titleXs={4} textXs={8}>{registre?.identificador}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.data')}               xs={6} titleXs={4} textXs={8}>{formatDate(registre?.data)}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.oficina')}            xs={6} titleXs={4} textXs={8}>{registre?.oficinaDescripcio} ({registre?.oficinaCodi})</ContenidoData>
            <ContenidoData title={t('page.registre.detall.extracte')}           xs={6} titleXs={4} textXs={8}>{registre?.extracte}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.observacions')}       xs={6} titleXs={4} textXs={8}>{registre?.observacions}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.identificador')}      xs={6} titleXs={4} textXs={8}>{registre?.identificador}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.data')}               xs={6} titleXs={4} textXs={8}>{formatDate(registre?.data)}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.oficinaDescripcio')}  xs={6} titleXs={4} textXs={8}>{registre?.oficinaDescripcio}</ContenidoData>

            <CardData xs={12}  title={t('page.registre.detall.infoResumida')}>
                <ContenidoData title={t('page.registre.detall.docFisica')}          xs={6} titleXs={4} textXs={8}>{registre?.docFisicaCodi} - {registre?.docFisicaDescripcio}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.desti')}              xs={6} titleXs={4} textXs={8}>{registre?.destiDescripcio} ({registre?.destiCodi})</ContenidoData>
                <ContenidoData title={t('page.registre.detall.refExterna')}         xs={6} titleXs={4} textXs={8}>{registre?.refExterna}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.expedientNumero')}    xs={6} titleXs={4} textXs={8}>{registre?.expedientNumero}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.procediment')}        xs={12} titleXs={2} textXs={10}>{registre?.procedimentCodi} - {entity?.metaExpedient?.description}</ContenidoData>
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
            <ContenidoData title={t('page.anotacio.detall.estatView')} xs={6} titleXs={4} textXs={8}>{entity?.estatView}</ContenidoData>
            <ContenidoData title={t('page.anotacio.detall.dataAlta')} xs={6} titleXs={4} textXs={8}>{formatDate(entity?.dataAlta)}</ContenidoData>
            {entity?.estat == 'REBUTJAT' &&
                <ContenidoData title={t('page.anotacio.detall.observacions')} xs={6} titleXs={4} textXs={8}>{entity?.observacions}</ContenidoData>
            }
            {entity?.estat != 'PENDENT' &&
                <>
                    <ContenidoData title={entity?.estat == 'REBUTJAT'
                        ?t('page.anotacio.detall.rejectedDate')
                        :t('page.anotacio.detall.acceptedDate')}
                        xs={6} titleXs={4} textXs={8}
                    >
                        {formatDate(entity?.dataActualitzacio)}
                    </ContenidoData>

                    <ContenidoData title={t('page.anotacio.detall.usuariActualitzacio')} xs={6} titleXs={4} textXs={8}>{entity?.usuariActualitzacio?.description}</ContenidoData>
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
            <ContenidoData title={t('page.registre.detall.tipus')}          xs={6} titleXs={4} textXs={8}>{t('page.registre.detall.entrada')}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.identificador')}  xs={6} titleXs={4} textXs={8}>{entity?.identificador}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.data')}           xs={6} titleXs={4} textXs={8}>{formatDate(entity?.data)}</ContenidoData>
            <ContenidoData title={t('page.registre.detall.oficina')}        xs={6} titleXs={4} textXs={8}>{entity?.oficinaDescripcio} ({entity?.oficinaCodi})</ContenidoData>

            <CardData xs={6} title={t('page.registre.detall.required')}>
                <ContenidoData title={t('page.registre.detall.llibre')}     xs={12} titleXs={4} textXs={8}>{entity?.llibreDescripcio} ({entity?.llibreCodi})</ContenidoData>
                <ContenidoData title={t('page.registre.detall.extracte')}   xs={12} titleXs={4} textXs={8}>{entity?.extracte}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.docFisica')}  xs={12} titleXs={4} textXs={8}>{entity?.docFisicaCodi} - {entity?.docFisicaDescripcio}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.desti')}      xs={12} titleXs={4} textXs={8}>{entity?.destiDescripcio} ({entity?.destiCodi})</ContenidoData>
                <ContenidoData title={t('page.registre.detall.assumpte')}   xs={12} titleXs={4} textXs={8}>{entity?.assumpteTipusDescripcio} ({entity?.assumpteTipusCodi})</ContenidoData>
                <ContenidoData title={t('page.registre.detall.idioma')}     xs={12} titleXs={4} textXs={8}>{entity?.idiomaDescripcio} ({entity?.idiomaCodi})</ContenidoData>
            </CardData>

            <CardData xs={6} title={t('page.registre.detall.optional')}>
                <ContenidoData title={t('page.registre.detall.assumpteCodi')}       xs={12} titleXs={4} textXs={8}>{entity?.assumpteCodiCodi}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.refExterna')}         xs={12} titleXs={4} textXs={8}>{entity?.refExterna}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.expedientNumero')}    xs={12} titleXs={4} textXs={8}>{entity?.expedientNumero}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.transport')}          xs={12} titleXs={4} textXs={8}>{entity?.transportTipusDescripcio}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.transportNumero')}    xs={12} titleXs={4} textXs={8}>{entity?.transportNumero}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.origenRegistreNumero')} xs={12} titleXs={4} textXs={8}>{entity?.origenRegistreNumero}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.origenData')}         xs={12} titleXs={4} textXs={8}>{formatDate(entity?.origenData)}</ContenidoData>
                <ContenidoData title={t('page.registre.detall.observacions')}       xs={12} titleXs={4} textXs={8}>{entity?.observacions}</ContenidoData>
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

const interessatsPerspectives:any[] = ['REPRESENTANT']
const interessatsSortModel:any = [{field: 'id', sort: 'asc'}];
const Interessats = (props:any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation()

    const {handleOpen, dialog} = useRegistreInteressatDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: false,
            onClick: handleOpen,
            hidden: (row:any) => row?.tipus == "ADMINISTRACIO"
        },
    ]

    return <>
        <StyledMuiGrid
            resourceName={'registreInteressatResource'}
            columns={interessatsColumns}
            filter={`registre.id:${entity?.id}`}
            perspectives={interessatsPerspectives}
            staticSortModel={interessatsSortModel}
            rowAdditionalActions={actions}
            toolbarHide
            disableColumnSorting
            readOnly
            autoHeight
            disabledPagination

            onRowCountChange={onRowCountChange}
            onRowClick={(params: any) => {
                if (params?.row?.tipus != "ADMINISTRACIO") {
                    handleOpen(params?.id, params?.row)
                }
            }}
        />
        {dialog}
    </>
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

const annexosPerspective = ['FIRMES'];
const annexosSortModel:any = [{field: 'id', sort: 'asc'}];
const Annexos = (props:any) => {
    const { entity, onRowCountChange } = props;
    const { t } = useTranslation();

    const { handleOpen: handleAnnexFirma, dialog: dialogAnnexFirma } = useAnnexFirma();
    const { handleOpen: handleVisualitzar, dialog: dialogVisualitzar, isValid } = useVisualitzar()
    const {download} = useAnexxActions()

    const actions = [
        {
            label: t('page.document.action.view.label'),
            icon: "search",
            showInMenu: true,
            onClick: handleVisualitzar,
            hidden: (row:any) => !isValid(row),
        },
        {
            label: t('page.anotacio.action.firma.label'),
            icon: "edit",
            showInMenu: true,
            onClick: handleAnnexFirma,
            hidden: (row:any) => row?.firmaTipus == null || !row?.firmes?.length,
        },
        {
            label: t('page.anotacio.action.descargarAnnex.label'),
            icon: "download",
            showInMenu: true,
            onClick: download,
            hidden: (row:any) => !row?.uuid,
        },
    ]

    return <>
        <StyledMuiGrid
            resourceName="registreAnnexResource"
            columns={annexosColumns}
            filter={builder.and(builder.eq('registre.id',entity?.id))}
            perspectives={annexosPerspective}
            staticSortModel={annexosSortModel}
            toolbarHide
            disableColumnSorting
            rowAdditionalActions={actions}
            readOnly
            autoHeight
            disabledPagination

            onRowCountChange={onRowCountChange}
            onRowClick={(params: any) => {
                if (isValid(params?.row)) {
                    handleVisualitzar(params?.id)
                }
            }}
        />
        {dialogAnnexFirma}
        {dialogVisualitzar}
    </>
}

const Justificant = (props:any) => {
    const { id, entity } = props;
    const { t } = useTranslation();

    const {downloadJustificant} = useActions()

    return <CardData title={<><Icon hidden sx={{ fontSize: '1em', paddingTop: '2px', }}>description</Icon>{entity?.titol}</>}
         buttons={[
             {
                 text: t('page.anotacio.action.justificant.label'),
                 icon: 'download',
                 onClick: ()=>downloadJustificant(id),
             }
         ]}
    >
        <ContenidoData title={t('page.registre.justificant.ntiFechaCaptura')}   xs={6} titleXs={4} textXs={8}>{formatDate(entity?.ntiFechaCaptura)}</ContenidoData>
        <ContenidoData title={t('page.registre.justificant.ntiOrigen')}         xs={6} titleXs={4} textXs={8}>{entity?.ntiOrigen}</ContenidoData>
        <ContenidoData title={t('page.registre.justificant.ntiTipoDocumental')} xs={6} titleXs={4} textXs={8}>{entity?.ntiTipoDocumental}</ContenidoData>
        <ContenidoData title={t('page.registre.justificant.uuid')}              xs={6} titleXs={4} textXs={8}>{entity?.uuid}</ContenidoData>
        <ContenidoData title={t('page.registre.justificant.titol')}             xs={6} titleXs={4} textXs={8}>{entity?.titol}</ContenidoData>

        <CardData title={<><Icon hidden sx={{ fontSize: '1em', paddingTop: '2px', }}>{icons.firma}</Icon>{t('page.arxiu.firma.title')}</>}>
            <ContenidoData title={t('page.registre.justificant.firmaTipus')}    xs={6} titleXs={4} textXs={8}>{entity?.firmaTipus}</ContenidoData>
            <ContenidoData title={t('page.registre.justificant.firmaPerfil')}   xs={6} titleXs={4} textXs={8}>{entity?.firmaPerfil}</ContenidoData>
        </CardData>
    </CardData>
}

const useAnotacioDetail = () => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const [numInteressats, setNumInteressats] = useState<number>();
    const [numAnnexos, setNumAnnexos] = useState<number>();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row);
        setEntity(row);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
        }
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
        {
            value: "justificant",
            label: t('page.anotacio.tabs.justificant'),
            content: <Justificant id={entity?.id} entity={entity?.registreInfo?.justificant}/>,
            hidden: !user?.sessionScope?.isIncorporacioJustificantActiva,
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.anotacio.detall.title')}
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
            <TabComponent
                indicatorColor={"primary"}
                textColor={"primary"}
                aria-label="scrollable force tabs"
                tabs={tabs}
                variant="scrollable"
            />
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

export default useAnotacioDetail;