import {useState} from "react";
import {Grid} from "@mui/material";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import Load from "../../../components/Load.tsx";
import * as builder from '../../../util/springFilterUtils.ts'
import Iframe from "../../../components/Iframe.tsx";

const SeguimentPortafirmes = (props:any) => {
    const {entity} = props;

    return <Load value={entity}>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <CardData xs={6} title={entity?.document?.description}>
                <ContenidoData title={'assumpte'}>{entity?.assumpte}</ContenidoData>
                <ContenidoData title={'enviatData'}>{formatDate(entity?.enviatData)}</ContenidoData>
                <ContenidoData title={'estat'}>{entity?.estat}</ContenidoData>
                <ContenidoData title={'prioritat'}>{entity?.prioritat}</ContenidoData>
                <ContenidoData title={'documentTipus'}>{entity?.documentTipus}</ContenidoData>
                <ContenidoData title={'fluxTipus'}>{entity?.fluxTipus}</ContenidoData>
                <ContenidoData title={'portafirmesId'}>{entity?.portafirmesId}</ContenidoData>
            </CardData>

            <Grid item xs={6}>
                <Iframe src={entity?.urlFluxSeguiment} style={{ height: '100%' }}/>
            </Grid>
        </Grid>
    </Load>
}
const useSeguimentPortafirmes = (potModificar:boolean, refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();

    const {
        isReady: apiIsReady,
        find: apiFind,
        artifactAction: apiAction,
    } = useResourceApiService('documentPortafirmesResource')
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const cancelarFirma = (id:any) => {
        apiAction(id, {code: 'CANCEL_FIRMA'})
            .then(()=>{
                refresh?.()
                temporalMessageShow(null, '', 'success');
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
            text: 'Cancelar el envío',
            icon: 'delete',
            hidden: !(entity?.estat == 'ENVIAT' && potModificar)
        },
        {
            value: 'close',
            text: t('common.close'),
            icon: 'close'
        },
    ]
        .filter((button:any)=>!button?.hidden)

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={'Detalles de la firma'}
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
            <SeguimentPortafirmes entity={entity}/>
        </MuiDialog>;

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useSeguimentPortafirmes;