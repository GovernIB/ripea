import {useState} from "react";
import {Grid} from "@mui/material";
import {BasePage, useResourceApiService, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import {formatDate} from "../util/dateUtils.ts";
import TabComponent from "../components/TabComponent.tsx";
import {CardData, ContenidoData} from "../components/CardData.tsx";
import Load from "../components/Load.tsx";

const InformacionArxiu = (props:any) => {
    const { arxiu } = props;
    const { t } = useTranslation();

    return <BasePage>
        <Load value={arxiu}>
            <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <ContenidoData title={t('page.arxiu.detall.arxiuUuid')}>{arxiu?.identificador}</ContenidoData>
                <ContenidoData title={t('page.arxiu.detall.fitxerNom')}>{arxiu?.nom}</ContenidoData>
                <ContenidoData title={t('page.arxiu.detall.serie')}>{arxiu?.serieDocumental}</ContenidoData>
                <ContenidoData title={t('page.arxiu.detall.arxiuEstat')} hiddenIfEmpty>{arxiu?.arxiuEstat}</ContenidoData>

                <CardData title={t('page.arxiu.detall.document')} hidden={!arxiu?.contingutTipusMime && !arxiu?.contingutArxiuNom}>
                    <ContenidoData title={t('page.arxiu.detall.fitxerContentType')} hiddenIfEmpty>{arxiu?.contingutTipusMime}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.fitxerNom')} hiddenIfEmpty>{arxiu?.contingutArxiuNom}</ContenidoData>
                </CardData>

                <CardData title={t('page.arxiu.detall.metadata')} hidden={!arxiu?.eniIdentificador}>
                    <ContenidoData title={t('page.arxiu.detall.versions')} hiddenIfEmpty>{arxiu?.eniVersio}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.identificador')} hiddenIfEmpty>{arxiu?.eniIdentificador}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.organ')} hiddenIfEmpty>{arxiu?.eniOrgans?.join(', ')}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.dataApertura')} hiddenIfEmpty>{formatDate(arxiu?.eniDataObertura)}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.clasificacion')} hiddenIfEmpty>{arxiu?.eniClassificacio}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.estat')} hiddenIfEmpty>{arxiu?.eniEstat}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.dataCaptura')} hiddenIfEmpty>{formatDate(arxiu?.eniDataCaptura)}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.origen')} hidden={!arxiu?.eniOrigen}>{t(`enum.origen.${arxiu?.eniOrigen}`)}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.estadoElaboracion')} hidden={!arxiu?.eniEstatElaboracio}>{t(`enum.estatElaboracio.${arxiu?.eniEstatElaboracio}`)}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.tipoDocumental')} hiddenIfEmpty>{arxiu?.eniTipusDocumental}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.tipoDocumental')} hiddenIfEmpty>{arxiu?.eniTipusDocumentalAddicional}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.format')} hiddenIfEmpty>{arxiu?.eniFormat}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.firmes')} hiddenIfEmpty>{arxiu?.firmes?.map((firma:any)=>firma?.tipus)?.join(', ')}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.interessats')} hiddenIfEmpty>{arxiu?.eniInteressats?.join(', ')}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.documentOrigen')} hiddenIfEmpty>{arxiu?.eniDocumentOrigenId}</ContenidoData>
                </CardData>
            </Grid>
        </Load>
    </BasePage>
}

const Hijos = (props:any) => {
    const { fills } = props;

    return<BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            {
                fills?.map((cont:any)=>
                    <ContenidoData key={cont?.identificador} title={cont?.tipus}>{cont?.nom}</ContenidoData>)
            }
        </Grid>
    </BasePage>
}

const Firmes = (props:any) => {
    const { firmes } = props;
    const { t } = useTranslation();

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            {
                firmes?.map((firma:any) =>
                    <CardData key={firma?.tipus} title={t('page.arxiu.firma.title') + ' ' + firma?.tipus}>
                        <ContenidoData title={t('page.arxiu.firma.perfil')} hiddenIfEmpty>{firma?.perfil}</ContenidoData>
                        <ContenidoData title={t('page.arxiu.firma.fitxerNom')} hiddenIfEmpty>{firma?.fitxerNom}</ContenidoData>
                        <ContenidoData title={t('page.arxiu.firma.tipusMime')} hiddenIfEmpty>{firma?.tipusMime}</ContenidoData>
                        <ContenidoData title={t('page.arxiu.firma.contingut')} hidden={firma?.tipus!='CSV'}>{firma?.contingutComString}</ContenidoData>
                        <ContenidoData title={t('page.arxiu.firma.csvRegulacio')} hiddenIfEmpty>{firma?.csvRegulacio}</ContenidoData>
                    </CardData>
                )
            }
        </Grid>
    </BasePage>;
}

const Metadatos = (props:any) => {
    const {metadades} = props;

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            {
                metadades && Object.entries(metadades).map(([key, value]:any[]) =>
                    // key.substring(key.indexOf(":") + 1)
                    <ContenidoData key={key} title={key} hiddenIfEmpty>
                        {
                            key.includes("fecha")
                                ?formatDate(value)
                                :value
                        }
                    </ContenidoData>)
            }
        </Grid>
    </BasePage>;
}

const useInformacioArxiu = (resourceName:string, perspective:string) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    // expedientResource 'ARXIU_EXPEDIENT'
    // documentResource 'ARXIU_DOCUMENT'

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService(resourceName);

    const handleOpen = (id:any) => {
        if (apiIsReady && id) {
            appGetOne(id, {perspectives: [perspective]}).then((app) => {
                setEntity(app?.arxiu)
            })
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
            value: "resum",
            label: t('page.arxiu.tabs.resum'),
            content: <InformacionArxiu arxiu={entity}/>
        },
        {
            value: "fills",
            label: t('page.arxiu.tabs.fills'),
            content: <Hijos fills={entity?.fills}/>,
            badge: entity?.fills?.length,
            hidden: !entity?.fills,
        },
        {
            value: "firmes",
            label: t('page.arxiu.tabs.firmes'),
            content: <Firmes firmes={entity?.firmes}/>,
            badge: entity?.firmes?.length,
            hidden: !entity?.firmes,
        },
        {
            value: "data",
            label: t('page.arxiu.tabs.data'),
            content: <Metadatos metadades={entity?.metadadesAddicionals}/>,
            badge: entity?.metadadesAddicionals ?Object.entries(entity?.metadadesAddicionals)?.length :0,
            hidden: !entity?.metadadesAddicionals,
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.arxiu.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    icon: 'close',
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
export default useInformacioArxiu;