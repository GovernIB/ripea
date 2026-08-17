import {useTranslation} from "react-i18next";
import {useState} from "react";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {Box, Grid, Icon, IconButton, Table, TableBody, TableCell, TableHead, TableRow, Tooltip, Typography} from "@mui/material";
import {DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import Load from "../../../components/Load.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {useToProgramaAntic} from "../../user/UserHeadToolbar.tsx";

const iconButtonSx = { p: 0.5, borderRadius: '5px', border: '1px solid grey' };

const FirmaDetalls = (props:any) => {
    const { detalls } = props;
    const { t } = useTranslation();

    return <Table size={"small"}>
        <TableHead>
            <TableRow>
                <TableCell>{t('page.anotacio.action.firma.detalls.data')}</TableCell>
                <TableCell>{t('page.anotacio.action.firma.detalls.nif')}</TableCell>
                <TableCell>{t('page.anotacio.action.firma.detalls.nom')}</TableCell>
                <TableCell>{t('page.anotacio.action.firma.detalls.emissor')}</TableCell>
            </TableRow>
        </TableHead>
        <TableBody>
            {detalls?.map((detall:any, index:number) => (
                <TableRow key={index}>
                    <TableCell>{detall?.data ? formatDate(detall?.data) : t('page.anotacio.action.firma.detalls.dataNd')}</TableCell>
                    <TableCell>{detall?.responsableNif}</TableCell>
                    <TableCell>{detall?.responsableNom}</TableCell>
                    <TableCell>{detall?.emissorCertificat}</TableCell>
                </TableRow>
            ))}
        </TableBody>
    </Table>
}

const AnnexFirma = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();
    const { getUrl } = useToProgramaAntic();

    const downloadFirma = () => {
        const link = document.createElement('a');
        link.href = getUrl(`expedientPeticio/descarregarFirma/${entity?.id}`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    if (!entity?.firmes?.length) {
        //El llistat només mira firmaTipus per oferir aquesta acció, així que es pot arribar aquí sense
        //firmes si l'Arxiu o el plugin de validació no han respost.
        return <Typography variant={"body2"} color={"textSecondary"}>{t('page.anotacio.action.firma.senseFirmes')}</Typography>
    }

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={2}>
        {
            entity?.firmes?.map((firma:any, index:number)=>{
                const autoFirmaHeader = firma?.autofirma
                    ? <Box ml={1} display={"flex"} alignItems={"center"}>
                        <Typography variant={"caption"}>({t('page.anotacio.action.firma.autoFirma')})</Typography>
                        <Tooltip title={t('page.anotacio.action.firma.autoFirmaInfo')}>
                            <Icon fontSize={"small"} sx={{ ml: 0.5 }}>info</Icon>
                        </Tooltip>
                    </Box>
                    : undefined;

                return <DetailCard key={index}
                                   title={`${t('page.anotacio.action.firma.firmaTitol')} ${index + 1}`}
                                   header={autoFirmaHeader}>
                    <DetailCardContent size={6} title={t('page.anotacio.action.firma.firmaTipus')}>{firma?.tipus}</DetailCardContent>
                    <DetailCardContent size={6} title={t('page.anotacio.action.firma.firmaPerfil')}>{firma?.perfil}</DetailCardContent>
                    {firma?.tipus === 'CADES_DET' &&
                        <DetailCardContent isObject size={12} title={t('page.anotacio.action.firma.fitxer')}>
                            <Box display={"flex"} alignItems={"center"} justifyContent={"space-between"}>
                                <Typography variant={"inherit"} color={"textSecondary"}>{firma?.fitxerNom}</Typography>
                                <IconButton sx={iconButtonSx} title={t('page.anotacio.action.firma.descarregar')} onClick={downloadFirma}>
                                    <Icon fontSize={"small"}>download</Icon>
                                </IconButton>
                            </Box>
                        </DetailCardContent>
                    }
                    <DetailCardContent size={12} hiddenIfEmpty title={t('page.anotacio.action.firma.firmaCsvRegulacio')}>{firma?.csvRegulacio}</DetailCardContent>
                    {firma?.detalls?.length > 0 &&
                        <DetailCardContent isObject size={12} title={t('page.anotacio.action.firma.firmaDetalls')}>
                            <FirmaDetalls detalls={firma?.detalls}/>
                        </DetailCardContent>
                    }
                </DetailCard>
            })
        }
    </Grid>
}

// Obtenir les firmes d'un annex costa dues crides remotes —descarregar el document de l'Arxiu i validar-lo
// amb el plugin de validació de signatura—, així que la perspectiva es demana només en obrir aquest
// diàleg i no des del llistat d'annexos, que no en mostra cap dada.
const firmesPerspectives = ['FIRMES'];

const useAnnexFirma = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    //undefined = firmes encara no consultades; null = consultades i sense resultat
    const [entity, setEntity] = useState<any>();
    const { getOne } = useResourceApiService('registreAnnexResource');
    const { temporalMessageShow } = useBaseAppContext();

    const handleOpen = (id: any, row:any) => {
        setEntity(undefined);
        setOpen(true);
        getOne(id ?? row?.id, {perspectives: firmesPerspectives})
            .then((annex:any) => setEntity(annex ?? null))
            .catch((error:any) => {
                temporalMessageShow(null, error.message, 'error');
                setEntity(null);
            });
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
            title={t('page.anotacio.action.firma.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md'}}
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
            <Load value={entity !== undefined} noEffect={!open}>
                <AnnexFirma entity={entity}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useAnnexFirma;
