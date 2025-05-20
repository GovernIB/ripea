import {useState} from "react";
import {Grid} from "@mui/material";
import {BasePage, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";

const Dades = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={2}>
            <CardData title={t('page.notificacio.detall.notificacioDades')}
                  buttons={[
                      {
                          text: t('page.notificacio.acciones.justificant'),
                          icon: 'download',
                          onClick: ()=>{},
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
                               hidden={!(entity.notificacioEstat=='FINALITZADA' || entity.notificacioEstat=='PROCESSADA')}>
                    {formatDate(entity?.processatData)}</ContenidoData>
                <ContenidoData title={t('page.notificacio.detall.tipus')}>{entity?.tipus}</ContenidoData>
                <ContenidoData title={t('page.notificacio.detall.entregaPostal')}>{t(`enum.siNO.${entity?.entregaPostal}`)}</ContenidoData>
            </CardData>
            <CardData title={t('page.notificacio.detall.notificacioDocument')}
                      buttons={[
                          {
                              text: t('page.notificacio.acciones.documentEnviat'),
                              icon: 'download',
                              onClick: ()=>{},
                              flex: 2,
                          },
                      ]}
            >
                <ContenidoData title={t('page.notificacio.detall.fitxerNom')} xs={10}>{entity?.fitxerNom}</ContenidoData>
            </CardData>
        </Grid>
    </BasePage>
}

const Errors = () => {
    return <BasePage>
    {/*TODO: pestaña de errores de una remesa*/}
    </BasePage>
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

    const handleClose = () => {
        setOpen(false);
    };

    const tabs = [
        {
            value: 'dades',
            label: t('page.notificacio.tabs.dades'),
            content: <Dades entity={entity}/>,
        },
        {
            value: "errors",
            label: t('page.notificacio.tabs.errors'),
            content: <Errors/>,
            hidden: !entity?.error
        },
    ]

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
            <TabComponent
                indicatorColor={"primary"}
                textColor={"primary"}
                aria-label="scrollable force tabs"
                tabs={tabs}
                variant="scrollable"
            />
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useRemesaDetail;