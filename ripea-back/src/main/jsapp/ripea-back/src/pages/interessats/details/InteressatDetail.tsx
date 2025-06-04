import {useState} from "react";
import {Grid} from "@mui/material";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";

const InteressatDetail = (props: any) => {
    const {entity} = props;
    const {t} = useTranslation();
    const representant = entity?.representantInfo

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <CardData title={t('page.interessat.title')}>
            <ContenidoData title={t('page.interessat.detall.nif')}>{entity?.documentNum}</ContenidoData>
            <ContenidoData title={`${t('page.interessat.detall.nom')} / ${t('page.interessat.detall.raoSocial')}`}>{entity?.nom} {entity?.raoSocial}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.llinatges')}>{entity?.llinatge1} {entity?.llinatge2}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.email')}>{entity?.email}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.telefon')}>{entity?.telefon}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.incapacitat')}>{entity?.incapacitat}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.direccio')}>{entity?.pais} {entity?.provincia} {entity?.municipi} {entity?.codiPostal} {entity?.adresa}</ContenidoData>
        </CardData>
        <CardData title={t('page.interessat.rep')} hidden={!representant}>
            <ContenidoData title={t('page.interessat.detall.nif')}>{representant?.documentNum}</ContenidoData>
            <ContenidoData title={`${t('page.interessat.detall.nom')} / ${t('page.interessat.detall.raoSocial')}`}>{representant?.nom} {representant?.raoSocial}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.llinatges')}>{representant?.llinatge1} {representant?.llinatge2}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.email')}>{representant?.email}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.telefon')}>{representant?.telefon}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.incapacitat')}>{representant?.incapacitat}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.direccio')}>{representant?.pais} {representant?.provincia} {representant?.municipi} {representant?.codiPostal} {representant?.adresa}</ContenidoData>
        </CardData>
    </Grid>
}
const useInteressatDetail = () => {
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
            componentProps={{fullWidth: true, maxWidth: 'sm'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close')
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <InteressatDetail entity={entity}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useInteressatDetail;