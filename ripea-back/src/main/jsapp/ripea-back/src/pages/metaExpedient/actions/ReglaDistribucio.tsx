import {useMemo, useState} from "react";
import {Grid, Icon} from "@mui/material";
import {useResourceApiService, MuiDialog, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import {ContenidoData} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {useActions} from "../details/MetaExpedientActions.tsx";

const ReglaDistribucio = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    return <Grid container sx={{display: "flex", flexDirection: "row", wordWrap: "break-word"}} columnSpacing={1} rowSpacing={1}>
        <ContenidoData title={t('page.metaExpedient.detall.regla.create')}>
            {entity!=null
                ?<Icon color={'success'}>check</Icon>
                :<Icon color={'error'}>close</Icon>
            }
        </ContenidoData>
        {entity!=null && <>
            <ContenidoData title={t('page.metaExpedient.detall.regla.data')}>{formatDate(entity?.data)}</ContenidoData>
            <ContenidoData title={t('page.metaExpedient.detall.regla.activa')}>
                {entity?.activa
                    ? <Icon color={'success'}>check</Icon>
                    : <Icon color={'error'}>close</Icon>
                }
            </ContenidoData>
            <ContenidoData title={t('page.metaExpedient.detall.regla.nom')}>{entity?.nom}</ContenidoData>
        </>}
    </Grid>
}

const perspectives = ["REGLA_ROLSAC"]
const useReglaDistribucio = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('metaExpedientResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const refreshEntity = (id:any) => {
        apiGetOne(id, {perspectives})
            .then((app) => setEntity(app))
            .catch((error) => {
                handleClose()
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const handleOpen = (id:any) => {
        if(apiIsReady && id){
            refreshEntity(id)
        }
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const {crearRegla, toogleRegla} = useActions(() => refreshEntity(entity?.id))
    const buttons :any[] = useMemo(() => [
        {
            value: 'create',
            text: t('page.metaExpedient.action.regla.create.label'),
            icon: 'check',
            componentProps: {
                variant: 'contained'
            },
            hidden: !!entity?.regla
        },
        {
            value: 'active',
            text: t('page.metaExpedient.action.regla.active.label'),
            icon: 'check',
            componentProps: {
                variant: 'contained'
            },
            hidden: !entity?.regla || entity?.regla?.activa
        },
        {
            value: 'desactive',
            text: t('page.metaExpedient.action.regla.desactive.label'),
            icon: 'close',
            componentProps: {
                variant: 'contained'
            },
            hidden: !entity?.regla || !entity?.regla?.activa
        },
        {
            value: 'close',
            text: t('common.close'),
            // icon: 'close',
        },
    ].filter((b:any)=>!b?.hidden), [t, entity]);

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.metaExpedient.action.regla.title')}
            componentProps={{ fullWidth: true, maxWidth: 'xs' }}
            buttons={buttons}
            buttonCallback={(value) :void => {
                switch (value) {
                    case 'create':crearRegla(entity?.id);break;
                    case 'active':toogleRegla(entity?.id, true);break;
                    case 'desactive':toogleRegla(entity?.id, false);break;
                    case 'close':refresh?.();handleClose();break;
                }
            }}
        >
            <Load value={entity}>
                <ReglaDistribucio entity={entity?.regla}/>
            </Load>
        </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
}
export default useReglaDistribucio;