import {useState} from "react";
import {Alert, Grid, Icon} from "@mui/material";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import Load from "../../../components/Load.tsx";

export const InteressatDetail = (props: any) => {
    const {entity, isShowDireccio = true } = props;
    const { t } = useTranslation();
    const representant = entity?.representantInfo

    const direccion = [
        !representant ?entity?.codiPostal :representant?.codiPostal,
        entity?.municipiNom,
        entity?.provinciaNom,
        entity?.paisNom,
        // !representant ?entity?.adresa :representant?.adresa,
    ]
        .flat()               // aplana posibles arrays
        .filter(Boolean)      // elimina undefined, null, '' o false
        .join(', ');          // une con coma y espacio

    const getTipusVia = (tipusVia:string) => {
        return tipusVia ?t(`enum.tipusVia.${tipusVia}`) :'';
    }

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>

        { entity?.incapacitat == true && (!entity?.representant || entity?.representant?.incapacitat) &&
            <Alert severity="warning">{t('page.interessat.alert.incapacitat')}</Alert>
        }

        <CardData title={t('page.interessat.title')}>
            <ContenidoData title={t('page.interessat.detall.nif')}>{entity?.documentNum}</ContenidoData>
            <ContenidoData title={`${t('page.interessat.detall.nom')} / ${t('page.interessat.detall.raoSocial')}`}>{entity?.nomComplet} {entity?.raoSocial}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.llinatges')}>{entity?.llinatge1} {entity?.llinatge2}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.email')}>{entity?.email}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.telefon')}>{entity?.telefon}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.incapacitat')} hiddenIfEmpty>{entity?.incapacitat}</ContenidoData>
            <ContenidoData hidden={!!representant || !isShowDireccio} title={<><Icon sx={{mr:1}}>place</Icon>{t('page.interessat.detall.direccioPostal')}</>}>
                {(entity?.adressaTipus == "NACIONAL" || entity?.adressaTipus == "ESTRANGER") && <>{getTipusVia(entity?.adressaTipusVia)} {entity?.adresa} {entity?.adressaNumCasa}</>}
                {entity?.adressaTipus == "APARTAT_CORREUS" && <>{getTipusVia(entity?.adressaTipusVia)} {entity?.adresa} {entity?.adressaNumCasa} {entity?.adresaApartatCorreus}</>}
                {entity?.adressaTipus == "SENSE_NORMALITZAR" && <>{entity?.adresa}</>}
            </ContenidoData>
            <ContenidoData hidden={!!representant || !isShowDireccio}>{direccion}</ContenidoData>

            <CardData title={t('page.interessat.rep')} hidden={!representant}>
                <ContenidoData title={t('page.interessat.detall.nif')}>{representant?.documentNum}</ContenidoData>
                <ContenidoData title={`${t('page.interessat.detall.nom')} / ${t('page.interessat.detall.raoSocial')}`}>{representant?.nom} {representant?.raoSocial}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.llinatges')}>{representant?.llinatge1} {representant?.llinatge2}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.email')}>{representant?.email}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.telefon')}>{representant?.telefon}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.incapacitat')} hiddenIfEmpty>{representant?.incapacitat}</ContenidoData>
                <ContenidoData hidden={!isShowDireccio} title={<><Icon sx={{mr:1}}>place</Icon>{t('page.interessat.detall.direccioPostal')}</>}>
                    {(representant?.adressaTipus == "NACIONAL" || representant?.adressaTipus == "ESTRANGER") && <>{getTipusVia(representant?.adressaTipusVia)} {representant?.adresa} {representant?.adressaNumCasa}</>}
                    {representant?.adressaTipus == "APARTAT_CORREUS" && <>{getTipusVia(representant?.adressaTipusVia)} {representant?.adresa} {representant?.adressaNumCasa} {representant?.adresaApartatCorreus}</>}
                    {representant?.adressaTipus == "SENSE_NORMALITZAR" && <>{representant?.adresa}</>}
                </ContenidoData>
                <ContenidoData hidden={!isShowDireccio}>{direccion}</ContenidoData>
            </CardData>
        </CardData>
    </Grid>
}

const perspectives = ['REPRESENTANT', 'ADRESSA']
const useInteressatDetail = () => {
    const {t} = useTranslation();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('interessatResource');
    const {temporalMessageShow} = useBaseAppContext();

    const handleOpen = (id: any) => {
        if(apiIsReady && id){
            apiGetOne(id, {perspectives})
                .then((app) => setEntity(app))
                .catch((error) => {
                    handleClose()
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
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
            <Load value={entity}>
                <InteressatDetail entity={entity}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useInteressatDetail;