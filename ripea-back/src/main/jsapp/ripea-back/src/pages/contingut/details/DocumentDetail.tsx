import {useState} from "react";
import {Box, Grid} from "@mui/material";
import {BasePage, GridPage, useResourceApiService, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import MetaDadaGrid from "../../dada/MetaDadaGrid.tsx";
import Load from "../../../components/Load.tsx";
import {useActions} from "./ContingutActions.tsx";

const Contenido = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    return <BasePage>
        <Load value={entity}>
            <Grid container sx={{ display:'flex', flexDirection: "row", wordWrap: "break-word" }} columnSpacing={1} rowSpacing={1}>
                <ContenidoData title={t('page.document.detall.fitxerNom')}>{entity?.fitxerNom}</ContenidoData>
                <ContenidoData title={t('page.document.detall.fitxerContentType')}>{entity?.fitxerContentType}</ContenidoData>
                <ContenidoData title={t('page.document.detall.metaDocument')}>{entity?.metaDocument?.description}</ContenidoData>
                <ContenidoData title={t('page.document.detall.createdDate')}>{formatDate(entity?.createdDate)}</ContenidoData>
                <ContenidoData title={t('page.document.detall.estat')}>{entity?.estat}</ContenidoData>
                <ContenidoData title={t('page.document.detall.dataCaptura')}>{formatDate(entity?.dataCaptura)}</ContenidoData>
                <ContenidoData title={t('page.document.detall.origen')}>{t(`enum.origen.${entity?.ntiOrigen}`)}</ContenidoData>
                <ContenidoData title={t('page.document.detall.tipoDocumental')}>{entity?.ntiTipoDocumental}</ContenidoData>
                <ContenidoData title={t('page.document.detall.estadoElaboracion')}>{t(`enum.estatElaboracio.${entity?.ntiEstadoElaboracion}`)}</ContenidoData>
                <ContenidoData title={t('page.document.detall.csv')}>{entity?.ntiCsv}</ContenidoData>
                <ContenidoData title={t('page.document.detall.csvRegulacion')}>{entity?.ntiCsvRegulacion}</ContenidoData>
                <ContenidoData title={t('page.document.detall.tipoFirma')}>{entity?.ntiTipoFirma && t(`enum.tipoFirma.${entity?.ntiTipoFirma}`)}</ContenidoData>
            </Grid>
        </Load>
    </BasePage>
}

const Dada = (props:any) => {
    const { entity, onRowCountChange } = props

    return <GridPage>
        <Box width={'100%'} height={162 + 52 * 4}>
            <MetaDadaGrid entity={entity} onRowCountChange={onRowCountChange}/>
        </Box>
    </GridPage>
}

const Versiones = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();
    const {descarregarVersio} = useActions()

    return <BasePage>
        {
            entity?.versions?.map((version:any) =>
                <CardData key={version?.id} title={t('page.document.versio.title') + ' ' + version?.id}
                    buttons={[
                        {
                            text: t('common.download'),
                            icon: 'download',
                            onClick: ()=>{descarregarVersio(entity?.id, version?.id)},
                            hidden: entity?.documentTipus == 'FISIC',
                        }
                    ]}
                >
                    <ContenidoData title={t('page.document.versio.data')}>{!version?.data && formatDate(version?.data)}</ContenidoData>
                    <ContenidoData title={t('page.document.versio.arxiuUuid')}>{version?.arxiuUuid}</ContenidoData>
                </CardData>
            )
        }
    </BasePage>;
}

const useDocumentDetail = () => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('documentResource');

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const [numDades, setNumDades] = useState<number>(entity?.numDades);

    const {apiDownload} = useActions()

    const handleOpen = (id:any) => {
        if(apiIsReady && id){
            apiGetOne(id, {perspectives: ['VERSIONS', 'COUNT']})
                .then((app) => setEntity(app))
        }
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const tabs = [
        {
            value: 'resum',
            label: t('page.document.tabs.resum'),
            content: <Contenido entity={entity}/>,
        },
        {
            value: "dades",
            label: t('page.contingut.tabs.dades'),
            content: <Dada entity={entity} onRowCountChange={setNumDades}/>,
            badge: numDades ?? entity?.numDades,
            hidden: !entity?.numMetaDades,
        },
        {
            value: "version",
            label: t('page.document.tabs.version'),
            content: <Versiones entity={entity}/>,
            badge: entity?.versions?.length,
            hidden: !entity?.versions || entity?.versions?.length == 0,
        },
    ]

    let buttons :any[] = [
        {
            value: 'download',
            text: t('common.download'),
            icon: 'download',
            hidden: entity?.documentTipus == 'FISIC'
        },
        {
            value: 'descarregarImprimible',
            text: t('page.document.action.descarregarImprimible.label'),
            icon: 'download',
            hidden: entity?.estat == 'CUSTODIAT'
        },
    ]
        .filter((button:any)=>!button?.hidden)

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={entity?.nom}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={buttons}
            buttonCallback={(value :any) :void => {
                switch (value){
                    case 'download':
                        apiDownload(entity?.id, 'adjunt')
                        break;
                    case 'descarregarImprimible':
                        apiDownload(entity?.id, 'imprimible', t('page.document.action.imprimible.ok'))
                        break;
                }
                handleClose();
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
export default useDocumentDetail;