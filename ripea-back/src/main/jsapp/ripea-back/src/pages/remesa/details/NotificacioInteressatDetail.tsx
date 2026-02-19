import {useState} from "react";
import {Box, Grid2, Typography} from "@mui/material";
import {BasePage, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";
import {CardButton, DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {useActions} from "./NotificacioInteressatActions.tsx";

const Dades = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    return <BasePage>
        <Grid2 container direction={"row"} columnSpacing={1} rowSpacing={2}>
            <DetailCard title={t('page.notificacioInteressat.detall.enviament')}>
                <DetailCardContent title={t('page.notificacio.detall.notificacioIdentificador')} hidden={!entity?.notificacioInfo} size={6}>{entity?.notificacioInfo?.notificacioIdentificador}</DetailCardContent>
                <DetailCardContent title={t('page.notificacioInteressat.detall.enviamentReferencia')} hidden={!entity?.notificacioInfo} size={6}>{entity?.enviamentReferencia}</DetailCardContent>
                <DetailCardContent title={'NOTIB'} hidden={entity?.notificacioInfo}>{entity?.enviamentReferencia}</DetailCardContent>

                <DetailCardContent title={t('page.notificacioInteressat.detall.entregaNif')}>{entity?.entregaNif}</DetailCardContent>
                <DetailCardContent title={t('page.notificacioInteressat.detall.classificacio')}>{entity?.classificacio}</DetailCardContent>
                <DetailCardContent title={t('page.interessat.detall.entregaDehObligat')} hidden={!entity?.representantInfo?.entregaDehObligat}>{t(`enum.siNO.${entity?.interessatInfo?.entregaDehObligat}`)}</DetailCardContent>
                <DetailCardContent title={t('page.notificacio.detall.serveiTipusEnum')}>{entity?.notificacioInfo?.serveiTipusEnum ?? 'NORMAL'}</DetailCardContent>
                <DetailCardContent title={t('page.notificacioInteressat.detall.enviamentDatatEstat')}>{entity?.enviamentDatatEstat}</DetailCardContent>
            </DetailCard>

            <DetailCard title={t('page.notificacioInteressat.detall.interessat')}>
                <DetailCardContent title={t('page.interessat.detall.nif')}>{entity?.interessatInfo?.documentNum}</DetailCardContent>

                <DetailCardContent title={t('page.interessat.detall.nom')} hidden={entity?.interessatInfo?.tipus != 'InteressatPersonaFisicaEntity'}
                               hiddenIfEmpty>{entity?.interessatInfo?.nom}</DetailCardContent>
                <DetailCardContent title={t('page.interessat.detall.llinatges')} hidden={entity?.interessatInfo?.tipus != 'InteressatPersonaFisicaEntity'}
                               hiddenIfEmpty>{entity?.interessatInfo?.llinatge1} {entity?.interessatInfo?.llinatge2}</DetailCardContent>

                <DetailCardContent title={t('page.interessat.detall.telefon')} hiddenIfEmpty>{entity?.interessatInfo?.telefon}</DetailCardContent>
                <DetailCardContent title={t('page.interessat.detall.email')} hiddenIfEmpty>{entity?.interessatInfo?.email}</DetailCardContent>
            </DetailCard>

            <DetailCard title={t('page.notificacioInteressat.detall.representant')} hidden={!entity?.representantInfo}>
                <DetailCardContent title={t('page.interessat.detall.nif')}>{entity?.representantInfo?.documentNum}</DetailCardContent>

                <DetailCardContent title={t('page.interessat.detall.nom')} hidden={entity?.representantInfo?.tipus != 'InteressatPersonaFisicaEntity'}
                               hiddenIfEmpty>{entity?.representantInfo?.nom}</DetailCardContent>
                <DetailCardContent title={t('page.interessat.detall.llinatges')} hidden={entity?.representantInfo?.tipus != 'InteressatPersonaFisicaEntity'}
                               hiddenIfEmpty>{entity?.representantInfo?.llinatge1} {entity?.representantInfo?.llinatge2}</DetailCardContent>

                <DetailCardContent title={t('page.interessat.detall.telefon')} hiddenIfEmpty>{entity?.representantInfo?.telefon}</DetailCardContent>
                <DetailCardContent title={t('page.interessat.detall.email')} hiddenIfEmpty>{entity?.representantInfo?.email}</DetailCardContent>
            </DetailCard>
        </Grid2>
    </BasePage>
}

const Notific = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    const {certificat} = useActions();

    return <BasePage>
        <Typography hidden={entity?.notificacioInfo?.notificacioEstat != 'PENDENT'}>{t('page.notificacioInteressat.detall.noEnviat')}</Typography>

        <DetailCard title={t('page.notificacioInteressat.detall.datat')} hidden={entity?.notificacioInfo?.notificacioEstat == 'PENDENT'}>
            <DetailCardContent title={t('page.notificacio.detall.notificacioEstat')}>{entity?.notificacioInfo?.notificacioEstat}</DetailCardContent>
        </DetailCard>

        <DetailCard title={t('page.notificacioInteressat.detall.certificacio')} hidden={!entity?.enviamentCertificacioData}
                    header={<Box ml="auto">
                        <CardButton icon={'download'}
                                    text={t('page.notificacioInteressat.action.certificat.label')}
                                    onClick={()=>certificat(entity?.id)}/>
                    </Box>}
        >
            <DetailCardContent title={t('page.notificacioInteressat.detall.enviamentCertificacioData')}>{formatDate(entity?.enviamentCertificacioData)}</DetailCardContent>
            <DetailCardContent title={t('page.notificacioInteressat.detall.enviamentCertificacioOrigen')}>{entity?.enviamentCertificacioOrigen}</DetailCardContent>
        </DetailCard>
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
export default useNotificacioInteressatDetail;