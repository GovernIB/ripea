import {useState} from "react";
import {Alert, Grid2 as Grid, Icon, IconButton} from "@mui/material";
import {MuiDialog, useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import Load from "../../../components/Load.tsx";
import * as builder from '../../../util/springFilterUtils.ts'
import TabComponent from "../../../components/TabComponent.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import {ErrorArea} from "../../../components/ErrorPage.tsx";

const Dades = (props:any) => {
    const {entity, fields} = props;
    return <MuiDetail entity={entity} fields={fields}>
        <DetailCard>
            <FieldData titleSize={4} textSize={8} field={'titol'}/>
            <FieldData titleSize={4} textSize={8} field={'descripcio'}/>
            <FieldData titleSize={4} textSize={8} field={'enviatData'}>{formatDate(entity?.enviatData)}</FieldData>
            <FieldData titleSize={4} textSize={8} field={'estat'}/>
            <FieldData titleSize={4} textSize={8} field={'tipusDestinatari'}/>
            <FieldData titleSize={4} textSize={8} field={'codiUsuari'} hidden={entity?.tipusDestinatari != 'TABLET'}/>
            <FieldData titleSize={4} textSize={8} field={'signantEmail'} hidden={entity?.tipusDestinatari != 'EMAIL'}/>
            <FieldData titleSize={4} textSize={8} field={'messageCode'}/>
        </DetailCard>
    </MuiDetail>
}
const Errors = (props:any) => {
    const {entity, fields} = props;
    const { t } = useTranslation();

    if (entity?.error) {
        return <MuiDetail entity={entity} fields={fields}>
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
                <FieldData titleSize={4} textSize={8} field={'intentData'}/>
                <FieldData titleSize={4} textSize={8} field={'intentNum'} sx={{ borderBottom: "1px solid" }}/>
                <Grid size={12} p={1}>
                    <ErrorArea>
                        {entity?.errorDescripcio}
                    </ErrorArea>
                </Grid>
            </DetailCard>
        </MuiDetail>
    }
}

const useSeguimentViafirma = (potModificar:boolean, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    const {
        isReady: apiIsReady,
        find: apiFind,
        artifactAction: apiAction,
        currentFields: fields,
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
            content: <Dades entity={entity} fields={fields}/>,
        },
        {
            value: "errors",
            label: t('page.documentVia.tabs.errors'),
            content: <Errors entity={entity} fields={fields}/>,
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