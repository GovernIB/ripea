import {useState} from "react";
import {Grid} from "@mui/material";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import Load from "../../../components/Load.tsx";
import * as builder from '../../../util/springFilterUtils.ts'

const SeguimentViafirma = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();
    return <Load value={entity}>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <CardData xs={12} title={entity?.document?.description}>
                <ContenidoData title={t('page.contingut.action.seguimentvf.document')}>{entity?.document?.nom}</ContenidoData>
                <ContenidoData title={t('page.contingut.action.seguimentvf.titol')}>{entity?.document?.titol}</ContenidoData>
                <ContenidoData title={t('page.contingut.action.seguimentvf.descripcio')}>{entity?.document?.descripcio}</ContenidoData>
                <ContenidoData title={t('page.contingut.action.seguimentvf.enviatData')}>{formatDate(entity?.enviatData)}</ContenidoData>
                <ContenidoData title={t('page.contingut.action.seguimentvf.estat')}>{t(`enum.estat.${entity?.estat}`)}</ContenidoData>
                <ContenidoData title={t('page.contingut.action.seguimentvf.tipusDestinatari')}>{t(`enum.tipusDestinatari.${entity?.tipusDestinatari}`)}</ContenidoData>
                <ContenidoData title={t('page.contingut.action.seguimentvf.messageCode')}>{entity?.messageCode}</ContenidoData>
            </CardData>
        </Grid>
    </Load>
}

const useSeguimentViafirma = (potModificar:boolean, refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();

    const {
        isReady: apiIsReady,
        find: apiFind,
        artifactAction: apiAction,
    } = useResourceApiService('documentViaFirmaResource')
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const cancelarFirma = (id:any) => {
        apiAction(id, {code: 'CANCEL_FIRMA'})
            .then(()=>{
                refresh?.()
                temporalMessageShow(null, t('page.document.action.seguiment.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const handleOpen = (id:any) => {
        if (apiIsReady && id){
            apiFind({
                filter: builder.eq('document.id', id),
                sorts: ['createdDate', 'desc']
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
            text: t('page.document.action.seguiment.cancel'),
            icon: 'cancel',
            hidden: !(entity?.estat == 'ENVIAT' && potModificar),
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
                if (value=='close') {
                    handleClose();
                }
                if (value=='cancel' && entity?.estat == 'ENVIAT' && potModificar) {
                    cancelarFirma(entity?.id)
                    handleClose();
                }
            }}
        >
            <SeguimentViafirma entity={entity}/>
        </MuiDialog>;

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useSeguimentViafirma;