import {useState} from "react";
import {Alert, Grid2 as Grid, Icon} from "@mui/material";
import {MuiDialog, useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import Load from "../../../components/Load.tsx";
import * as builder from '../../../util/springFilterUtils.ts'
import IconButton from "@mui/material/IconButton";
import TabComponent from "../../../components/TabComponent.tsx";
import Box from "@mui/material/Box";
import {useUserSession} from "../../../components/Session.tsx";

const Dades = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <DetailCard title={entity?.document?.description}>
            {/*<DetailCardContent title={t('page.documentVia.detall.document')}>{entity?.document?.description}</DetailCardContent>*/}
            <DetailCardContent title={t('page.documentVia.detall.titol')}>{entity?.titol}</DetailCardContent>
            <DetailCardContent title={t('page.documentVia.detall.descripcio')}>{entity?.descripcio}</DetailCardContent>
            <DetailCardContent title={t('page.documentVia.detall.enviatData')}>{formatDate(entity?.enviatData)}</DetailCardContent>
            <DetailCardContent title={t('page.documentVia.detall.estat')}>{t(`enum.estat.${entity?.estat}`)}</DetailCardContent>
            <DetailCardContent title={t('page.documentVia.detall.tipusDestinatari')}>{t(`enum.tipusDestinatari.${entity?.tipusDestinatari}`)}</DetailCardContent>
            <DetailCardContent title={t('page.documentVia.detall.codiUsuari')} hidden={entity?.tipusDestinatari != 'TABLET'}>{entity?.codiUsuari}</DetailCardContent>
            <DetailCardContent title={t('page.documentVia.detall.signantEmail')} hidden={entity?.tipusDestinatari != 'EMAIL'}>{entity?.signantEmail}</DetailCardContent>
            <DetailCardContent title={t('page.documentVia.detall.messageCode')} hiddenIfEmpty>{entity?.messageCode}</DetailCardContent>
        </DetailCard>
    </Grid>
}
const Errors = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    if (entity?.error) {
        return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <Grid size={12}>
                <Alert severity={'error'}
                       icon={<Icon>warning</Icon>}
                       action={<IconButton title={t('page.documentVia.alert.reintentar')} size="small">
                           <Icon>refresh</Icon>
                       </IconButton>}
                >
                    {entity?.estat == 'PENDENT' && t('page.documentVia.alert.enviament')}
                    {entity?.estat == 'ENVIAT' && t('page.documentVia.alert.processament')}
                    {entity?.estat == 'CANCELAT' && t('page.documentVia.alert.cancelat')}
                </Alert>
            </Grid>

            <DetailCard title={t('page.documentVia.alert.enviament')}>
                <DetailCardContent title={t('page.documentVia.detall.intentData')}>{formatDate(entity?.intentData)}</DetailCardContent>
                <DetailCardContent title={t('page.documentVia.detall.intentNum')}  sx={{ borderBottom: "1px solid" }}>{entity?.intentNum}</DetailCardContent>

                <Grid size={12} p={1}>
                    <Box
                        sx={{
                            border: 'solid 1px #e3e3e3',
                            borderRadius: '4px',
                            backgroundColor: '#f5f5f5',
                            display: 'block',
                            overflow: 'auto',
                            whiteSpace: 'pre',
                            fontFamily: 'monospace', // opcional para parecer <pre>
                            width: 'max-container',
                            p: 1
                        }}
                    >
                        {entity?.errorDescripcio}
                    </Box>
                </Grid>
            </DetailCard>
        </Grid>
    }
}

const useSeguimentViafirma = (potModificar:boolean, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    const {
        isReady: apiIsReady,
        find: apiFind,
        artifactAction: apiAction,
    } = useResourceApiService('documentViaFirmaResource')
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const cancelarFirma = (id:any) => {
        messageDialogShow(
            t('page.document.action.cancel.check'),
            t('page.document.action.cancel.description'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    apiAction(id, {code: 'CANCEL_FIRMA'})
                        .then(() => {
                            refresh?.()
                            temporalMessageShow(null, t('page.document.action.cancel.ok'), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }

    const handleOpen = (id:any) => {
        if (apiIsReady && id){
            setOpen(true)
            apiFind({
                filter: builder.eq('document.id', id),
                sorts: ['createdDate,desc']
            })
                .then((result) => {
                    if (result?.rows?.length>0){
                        setEntity(result?.rows[0])
                    }
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                    handleClose()
                });
        }
    }
    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const buttons = [
         {
            value: 'cancel',
            text: t('page.document.action.cancel.label'),
            icon: 'cancel',
            hidden: !(entity?.estat == 'ENVIAT' && potModificar && user?.rolActual != "IPA_ADMIN_LECTURA"),
            componentProps: { variant: 'contained' }
        },
        {
            value: 'close',
            text: t('common.close'),
            componentProps: { variant: 'outlined' }
        },
    ]
        .filter((button:any)=>!button?.hidden)

    const tabs = [
        {
            value: "dades",
            label: t('page.documentVia.tabs.dades'),
            content: <Dades entity={entity}/>,
        },
        {
            value: "errors",
            label: t('page.documentVia.tabs.errors'),
            content: <Errors entity={entity}/>,
            disabled: !entity?.error
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.contingut.action.seguimentvf.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md'}}
            buttons={buttons}
            buttonCallback={(value :any) :void=>{
                if (value=='cancel' && entity?.estat == 'ENVIAT' && potModificar && user?.rolActual != "IPA_ADMIN_LECTURA") {
                    cancelarFirma(entity?.id)
                }
                handleClose();
            }}
        >
            <Load value={entity}>
                <TabComponent tabs={tabs} variant="scrollable"/>
            </Load>
        </MuiDialog>;

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useSeguimentViafirma;