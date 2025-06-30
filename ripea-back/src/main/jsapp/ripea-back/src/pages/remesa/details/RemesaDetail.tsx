import {useState} from "react";
import {Grid} from "@mui/material";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import AlertExpand from "../../../components/AlertExpand.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {useActions} from "./RemesaActions.tsx";
import Load from "../../../components/Load.tsx";

const Dades = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    const {justificant, descarregarDocumentEnviat} = useActions()

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={2}>
        <CardData title={t('page.notificacio.detall.notificacioDades')}
              buttons={[
                  {
                      text: t('page.notificacio.action.justificant.label'),
                      icon: 'download',
                      onClick: ()=>{justificant(entity?.id)},
                      hidden: entity?.notificacioEstat == 'PENDENT',
                  },
              ]}
        >
            <ContenidoData title={t('page.notificacio.detall.emisor')}>{entity?.emisor?.description}</ContenidoData>
            <ContenidoData title={t('page.notificacio.detall.assumpte')}>{entity?.assumpte}</ContenidoData>
            <ContenidoData title={t('page.notificacio.detall.observacions')}>{entity?.observacions}</ContenidoData>
            <ContenidoData title={t('page.notificacio.detall.notificacioEstat')}>{entity?.notificacioEstat}</ContenidoData>
            <ContenidoData title={t('page.notificacio.detall.createdDate')}>{formatDate(entity?.createdDate)}</ContenidoData>
            <ContenidoData title={t('page.notificacio.detall.processatData')}
                           hidden={!(entity?.notificacioEstat=='FINALITZADA' || entity?.notificacioEstat=='PROCESSADA')}>
                {formatDate(entity?.processatData)}</ContenidoData>
            <ContenidoData title={t('page.notificacio.detall.tipus')}>{entity?.tipus}</ContenidoData>
            <ContenidoData title={t('page.notificacio.detall.entregaPostal')}>{t(`enum.siNO.${entity?.entregaPostal}`)}</ContenidoData>
        </CardData>
        <CardData title={t('page.notificacio.detall.notificacioDocument')}
                  buttons={[
                      {
                          text: t('page.notificacio.action.documentEnviat.label'),
                          icon: 'download',
                          onClick: ()=>{descarregarDocumentEnviat(entity?.id)},
                          flex: 2,
                      },
                  ]}
        >
            <ContenidoData title={t('page.notificacio.detall.fitxerNom')} xs={10}>{entity?.fitxerNom}</ContenidoData>
        </CardData>
    </Grid>
}

const useRemesaDetail = () => {
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const { t } = useTranslation();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row)
        setEntity(row);
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
            title={t('page.notificacio.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    icon: 'close'
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <Load value={entity}>
                {entity?.error &&
                    <AlertExpand severity={"error"} label={t('page.notificacio.detall.error')}>{entity?.errorDescripcio}</AlertExpand>
                }
                <Dades entity={entity}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useRemesaDetail;