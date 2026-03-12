import {useState} from "react";
import {Grid} from "@mui/material";
import {MuiDialog, useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import Load from "../../../components/Load.tsx";
import * as builder from '../../../util/springFilterUtils.ts'
import Iframe from "../../../components/Iframe.tsx";
import {useUserSession} from "../../../components/Session.tsx";

export const SeguimentPortafirmes = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();
    return <Load value={entity}>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <CardData xs={6} title={entity?.document?.description}>
                <ContenidoData title={t('page.documentPortafirmes.detall.assumpte')}>{entity?.assumpte}</ContenidoData>
                <ContenidoData title={t('page.documentPortafirmes.detall.enviatData')}>{formatDate(entity?.enviatData)}</ContenidoData>
                <ContenidoData title={t('page.documentPortafirmes.detall.estat')}>{t(`enum.estat.${entity?.estat}`)}</ContenidoData>
                <ContenidoData title={t('page.documentPortafirmes.detall.prioritat')}>{t(`enum.prioritat.${entity?.prioritat}`)}</ContenidoData>
                <ContenidoData title={t('page.documentPortafirmes.detall.documentTipusNom')}>{entity?.documentTipusNom}</ContenidoData>
                <ContenidoData title={t('page.documentPortafirmes.detall.fluxTipus')}>{t(`enum.fluxTipus.${entity?.fluxTipus}`)}</ContenidoData>
                <ContenidoData title={t('page.documentPortafirmes.detall.responsables')} hiddenIfEmpty>{entity?.responsables}</ContenidoData>
                <ContenidoData title={t('page.documentPortafirmes.detall.sequenciaTipus')} hidden={!entity?.sequenciaTipus}>{t(`enum.tipusSequencia.${entity?.sequenciaTipus}`)}</ContenidoData>
                <ContenidoData title={t('page.documentPortafirmes.detall.portafirmesId')}>{entity?.portafirmesId}</ContenidoData>
            </CardData>

            <Grid item xs={6}>
                <Iframe src={entity?.urlFluxSeguiment} style={{ height: '100%' }}/>
            </Grid>
        </Grid>
    </Load>
}

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {
        artifactAction: apiAction,
    } = useResourceApiService('documentPortafirmesResource')
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

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

    return {
        cancelarFirma
    }
}

const useSeguimentPortafirmes = (potModificar:boolean, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    const {
        isReady: apiIsReady,
        find: apiFind,
    } = useResourceApiService('documentPortafirmesResource')
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const {cancelarFirma} = useActions(refresh)

    const handleOpen = (id:any) => {
        if (apiIsReady && id){
            apiFind({
                filter: builder.eq('document.id', id),
                sorts: ['createdDate,desc']
            })
                .then((result) => {
                    if (result?.rows?.length>0){
                        setEntity(result?.rows[0])
                    }
                })
            setOpen(true)
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

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.document.action.seguiment.title')}
            componentProps={{ fullWidth: true, maxWidth: 'xl'}}
            buttons={buttons}
            buttonCallback={(value :any) :void=>{
                if (value=='cancel' && entity?.estat == 'ENVIAT' && potModificar && user?.rolActual != "IPA_ADMIN_LECTURA") {
                    cancelarFirma(entity?.id)
                    handleClose();
                }
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <SeguimentPortafirmes entity={entity}/>
        </MuiDialog>;

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useSeguimentPortafirmes;