import {useState} from "react";
import {Grid, Typography} from "@mui/material";
import {BasePage, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";

const Dades = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={2}>
            <CardData title={t('page.notificacioInteressat.detall.enviament')}>
                <ContenidoData title={t('page.notificacio.detall.notificacioIdentificador')} hidden={!entity?.notificacioInfo} xs={6}>{entity?.notificacioInfo?.notificacioIdentificador}</ContenidoData>
                <ContenidoData title={t('page.notificacioInteressat.detall.enviamentReferencia')} hidden={!entity?.notificacioInfo} xs={6}>{entity?.enviamentReferencia}</ContenidoData>
                <ContenidoData title={'NOTIB'} hidden={entity?.notificacioInfo} xs={12}>{entity?.enviamentReferencia}</ContenidoData>

                <ContenidoData title={t('page.notificacioInteressat.detall.entregaNif')}>{entity?.entregaNif}</ContenidoData>
                <ContenidoData title={t('page.notificacioInteressat.detall.classificacio')}>{entity?.classificacio}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.entregaDehObligat')}>{t(`enum.siNO.${entity?.interessatInfo?.entregaDehObligat}`)}</ContenidoData>
                <ContenidoData title={t('page.notificacio.detall.serveiTipusEnum')}>{entity?.notificacioInfo?.serveiTipusEnum ?? 'NORMAL'}</ContenidoData>
                <ContenidoData title={t('page.notificacioInteressat.detall.enviamentDatatEstat')}>{entity?.enviamentDatatEstat}</ContenidoData>
            </CardData>

            <CardData title={t('page.notificacioInteressat.detall.interessat')}>
                <ContenidoData title={t('page.interessat.detall.nif')}>{entity?.interessatInfo?.documentNum}</ContenidoData>

                <ContenidoData title={t('page.interessat.detall.nom')} hidden={entity?.interessatInfo?.tipus != 'InteressatPersonaFisicaEntity'}
                               hiddenIfEmpty>{entity?.interessatInfo?.nom}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.llinatges')} hidden={entity?.interessatInfo?.tipus != 'InteressatPersonaFisicaEntity'}
                               hiddenIfEmpty>{entity?.interessatInfo?.llinatge1} {entity?.interessatInfo?.llinatge2}</ContenidoData>

                <ContenidoData title={t('page.interessat.detall.telefon')} hiddenIfEmpty>{entity?.interessatInfo?.telefon}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.email')} hiddenIfEmpty>{entity?.interessatInfo?.email}</ContenidoData>
            </CardData>

            <CardData title={t('page.notificacioInteressat.detall.representant')} hidden={!entity?.representantInfo}>
                <ContenidoData title={t('page.interessat.detall.nif')}>{entity?.representantInfo?.documentNum}</ContenidoData>

                <ContenidoData title={t('page.interessat.detall.nom')} hidden={entity?.representantInfo?.tipus != 'InteressatPersonaFisicaEntity'}
                               hiddenIfEmpty>{entity?.representantInfo?.nom}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.llinatges')} hidden={entity?.representantInfo?.tipus != 'InteressatPersonaFisicaEntity'}
                               hiddenIfEmpty>{entity?.representantInfo?.llinatge1} {entity?.representantInfo?.llinatge2}</ContenidoData>

                <ContenidoData title={t('page.interessat.detall.telefon')} hiddenIfEmpty>{entity?.representantInfo?.telefon}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.email')} hiddenIfEmpty>{entity?.representantInfo?.email}</ContenidoData>
            </CardData>
        </Grid>
    </BasePage>
}

const Notific = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    const {certificat} = useActions();

    return <BasePage>
        <Typography hidden={entity?.notificacioInfo?.notificacioEstat != 'PENDENT'}>{t('page.notificacioInteressat.detall.noEnviat')}</Typography>

        <CardData title={t('page.notificacioInteressat.detall.datat')} hidden={entity?.notificacioInfo?.notificacioEstat == 'PENDENT'}>
            <ContenidoData title={t('page.notificacio.detall.notificacioEstat')}>{entity?.notificacioInfo?.notificacioEstat}</ContenidoData>
        </CardData>

        <CardData title={t('page.notificacioInteressat.detall.certificacio')} hidden={!entity?.enviamentCertificacioData}
              buttons={[
                  {
                      text: t('page.notificacioInteressat.action.certificat.label'),
                      icon: 'download',
                      onClick: ()=>{certificat(entity?.id)},
                  },
              ]}
        >
            <ContenidoData title={t('page.notificacioInteressat.detall.enviamentCertificacioData')}>{formatDate(entity?.enviamentCertificacioData)}</ContenidoData>
            <ContenidoData title={t('page.notificacioInteressat.detall.enviamentCertificacioOrigen')}>{entity?.enviamentCertificacioOrigen}</ContenidoData>
        </CardData>
    </BasePage>
}

const useNotificacioInteressatDetail = () => {
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const { t } = useTranslation();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row)
        setEntity(row);
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
            value: 'dades',
            label: t('page.notificacioInteressat.tabs.dades'),
            content: <Dades entity={entity}/>,
        },
        {
            value: "notif",
            label: t('page.notificacioInteressat.tabs.notif'),
            content: <Notific entity={entity}/>,
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.notificacioInteressat.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    icon: 'close'
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
export default useNotificacioInteressatDetail;