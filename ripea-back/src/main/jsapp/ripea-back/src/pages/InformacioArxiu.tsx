import {useState} from "react";
import {Grid, Link, Icon} from "@mui/material";
import {BasePage, useResourceApiService, MuiDialog, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import {formatDate} from "../util/dateUtils.ts";
import TabComponent from "../components/TabComponent.tsx";
import {DetailCard, DetailCardContent} from "../components/CardData.tsx";
import Load from "../components/Load.tsx";

const InformacionArxiu = (props:any) => {
    const { arxiu } = props;
    const { t } = useTranslation();

    return <BasePage>
        <Load value={arxiu}>
            <Grid container sx={{wordWrap: "break-word" }} direction={"row"} columnSpacing={1} rowSpacing={1}>

                <DetailCard title={t('page.arxiu.detall.dades')}>
                    <DetailCardContent title={t('page.arxiu.detall.arxiuUuid')}>{arxiu?.identificador}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.fitxerNom')}>{arxiu?.nom}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.serie')}>{arxiu?.serieDocumental}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.arxiuEstat')} hiddenIfEmpty>{arxiu?.arxiuEstat}</DetailCardContent>                    
                    <DetailCardContent title={t('page.arxiu.detall.fitxerContentType')} hidden={!arxiu?.contingutTipusMime}>{arxiu?.contingutTipusMime}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.fitxerNom')} hidden={!arxiu?.contingutArxiuNom}>{arxiu?.contingutArxiuNom}</DetailCardContent>
                </DetailCard>

                <DetailCard title={t('page.arxiu.detall.metadata')} hidden={!arxiu?.eniIdentificador}>
                    <DetailCardContent title={t('page.arxiu.detall.versions')}>{arxiu?.eniVersio}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.identificador')}>{arxiu?.eniIdentificador}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.organ')}>{arxiu?.eniOrgans?.join(', ')}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.dataApertura')}>{formatDate(arxiu?.eniDataObertura)}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.clasificacion')}>{arxiu?.eniClassificacio}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.estat')} hidden={!arxiu?.eniEstat}>{t(`enum.estat.${arxiu?.eniEstat}`)}</DetailCardContent>
                    <DetailCardContent title={t('page.document.detall.csv')} hidden={!arxiu?.csv}>
                        {arxiu?.csv} {arxiu?.csvLink &&
                        <Link href={arxiu?.csvLink+arxiu?.csv} target={"_blank"} rel="noopener noreferrer"><Icon>launch</Icon></Link>}
                    </DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.dataTancament')}>{formatDate(arxiu?.eniDataTancament)}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.dataCaptura')}>{formatDate(arxiu?.eniDataCaptura)}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.origen')} hidden={!arxiu?.eniOrigen}>{t(`enum.origen.${arxiu?.eniOrigen}`)}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.estadoElaboracion')} hidden={!arxiu?.eniEstatElaboracio}>{t(`enum.estatElaboracio.${arxiu?.eniEstatElaboracio}`)}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.tipoDocumental')}>{arxiu?.eniTipusDocumental}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.tipoDocumental')}>{arxiu?.eniTipusDocumentalAddicional}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.format')}>{arxiu?.eniFormat}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.firmes')}>{arxiu?.firmes?.map((firma:any)=>firma?.tipus)?.join(', ')}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.interessats')}>{arxiu?.eniInteressats?.join(', ')}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.detall.documentOrigen')}>{arxiu?.eniDocumentOrigenId}</DetailCardContent>
                </DetailCard>
            </Grid>
        </Load>
    </BasePage>
}

const Hijos = (props:any) => {
    const { fills } = props;

    return<BasePage>
        <DetailCard>
            {
                fills?.map((cont:any)=> <DetailCardContent key={cont?.identificador} title={cont?.tipus}>{cont?.nom}</DetailCardContent>)
            }
        </DetailCard>
    </BasePage>
}

const Firmes = (props:any) => {
    const { firmes } = props;
    const { t } = useTranslation();

    return <BasePage>
        {
            firmes?.map((firma:any) =>
                <DetailCard key={firma?.tipus} title={t('page.arxiu.firma.title') + ' ' + firma?.tipus}>
                    <DetailCardContent title={t('page.arxiu.firma.perfil')} hiddenIfEmpty>{firma?.perfil}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.firma.fitxerNom')} hiddenIfEmpty>{firma?.fitxerNom}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.firma.tipusMime')} hiddenIfEmpty>{firma?.tipusMime}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.firma.contingut')} hidden={firma?.tipus!='CSV'}>{firma?.contingutComString}</DetailCardContent>
                    <DetailCardContent title={t('page.arxiu.firma.csvRegulacio')} hiddenIfEmpty>{firma?.csvRegulacio}</DetailCardContent>
                </DetailCard>
            )
        }
    </BasePage>;
}

const Metadatos = (props:any) => {
    const {metadades} = props;

    return <BasePage>
        <DetailCard>
            {
                metadades && Object.entries(metadades).map(([key, value]:any[]) =>
                    <DetailCardContent key={key} title={key} hiddenIfEmpty>
                        {
                            key.includes("fecha")
                                ?formatDate(value)
                                :value
                        }
                    </DetailCardContent>)
            }
        </DetailCard>
    </BasePage>;
}

const useInformacioArxiu = (resourceName:string, perspective:string) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const {temporalMessageShow} = useBaseAppContext();

    // expedientResource 'ARXIU_EXPEDIENT'
    // documentResource 'ARXIU_DOCUMENT'

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService(resourceName);

    const handleOpen = (id:any) => {
        if (apiIsReady && id) {
            appGetOne(id, {perspectives: [perspective]})
                .then((app) => {
                    setEntity(app?.arxiu)
                })
                .catch((error)=>{
                    temporalMessageShow(null, error?.message, 'error');
                    handleClose()
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
            disabled: !entity?.fills || entity?.fills?.length == 0,
            showZero: true,
        },
        {
            value: "firmes",
            label: t('page.arxiu.tabs.firmes'),
            content: <Firmes firmes={entity?.firmes}/>,
            badge: entity?.firmes?.length,
            hidden: !entity?.firmes || entity?.firmes?.length == 0,
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
            title={t('page.contingut.action.infoArxiu.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md'}}
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
            <Load value={entity}>
                <TabComponent
                    indicatorColor={"primary"}
                    textColor={"primary"}
                    aria-label="scrollable force tabs"
                    tabs={tabs}
                    variant="scrollable"
                />
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useInformacioArxiu;