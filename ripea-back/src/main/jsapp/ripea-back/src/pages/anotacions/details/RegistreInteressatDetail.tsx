import {useTranslation} from "react-i18next";
import {useState} from "react";
import {Grid2} from "@mui/material";
import { MuiDialog } from "reactlib";
import {DetailCard, DetailCardContent} from "../../../components/CardData.tsx";

const RegistreInteressatDetail = (props:any) => {
    const {entity} = props;
    const {t} = useTranslation();
    const representant = entity?.representantInfo

    return <Grid2 container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <DetailCard title={t('page.interessat.title')}>
            <DetailCardContent title={t('page.interessat.detall.nif')}>{entity?.documentNumero}</DetailCardContent>
            <DetailCardContent title={`${t('page.interessat.detall.nom')} / ${t('page.interessat.detall.raoSocial')}`}>{entity?.nom} {entity?.raoSocial}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.llinatges')}>{entity?.llinatge1} {entity?.llinatge2}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.email')}>{entity?.email}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.telefon')}>{entity?.telefon}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.incapacitat')}>{entity?.incapacitat}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.direccio')}>{entity?.pais} {entity?.provincia} {entity?.municipi} {entity?.codiPostal} {entity?.adresa}</DetailCardContent>
        </DetailCard>
        <DetailCard title={t('page.interessat.rep')} hidden={!representant}>
            <DetailCardContent title={t('page.interessat.detall.nif')}>{representant?.documentNumero}</DetailCardContent>
            <DetailCardContent title={`${t('page.interessat.detall.nom')} / ${t('page.interessat.detall.raoSocial')}`}>{representant?.nom} {representant?.raoSocial}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.llinatges')}>{representant?.llinatge1} {representant?.llinatge2}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.email')}>{representant?.email}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.telefon')}>{representant?.telefon}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.incapacitat')}>{representant?.incapacitat}</DetailCardContent>
            <DetailCardContent title={t('page.interessat.detall.direccio')}>{representant?.pais} {representant?.provincia} {representant?.municipi} {representant?.codiPostal} {representant?.adresa}</DetailCardContent>
        </DetailCard>
    </Grid2>
}

const useRegistreInteressatDetail = () => {
    const {t} = useTranslation();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id: any, row: any) => {
        console.log(id, row)
        setEntity(row)
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.interessat.action.detail.title')}
            componentProps={{fullWidth: true, maxWidth: 'sm'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' }
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <RegistreInteressatDetail entity={entity}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useRegistreInteressatDetail;