import {useState} from "react";
import {useTranslation} from "react-i18next";
import Dialog from "../../../../lib/components/mui/Dialog.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import {Grid} from "@mui/material";
import {BasePage, MuiGrid} from "reactlib";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";

const columns = [
    {
        field: 'interessat',
        flex: 1,
    },
    {
        field: 'enviamentDatatEstat',
        flex: 0.5,
    },
    {
        field: 'enviamentDatatData',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'enviamentDatatOrigen',
        flex: 0.5,
    },
    {
        field: 'registreNumeroFormatat',
        flex: 0.75,
    },
    {
        field: 'registreData',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'enviamentCertificacioOrigen',
        flex: 0.75,
    },
    {
        field: 'enviamentCertificacioData',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value)
    },
]

const EnviamentInteressatGrid = (props:any) => {
    const {entity} = props;

    return <MuiGrid
        resourceName={'documentEnviamentInteressatResource'}
        // perspectives={['']}
        columns={columns}
        filter={builder.and(
            builder.eq('notificacio.id', entity?.id)
        )}
        titleDisabled
        staticSortModel={[{field: 'id', sort: 'asc'}]}
        toolbarHide
        disableColumnMenu
        disableColumnSorting
        readOnly
        autoHeight
    />;
}

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

            <CardData title={t('page.notificacio.acciones.notificacioInteressat')}>
                <EnviamentInteressatGrid entity={entity}/>
            </CardData>
        </Grid>
    </BasePage>
}

const Errors = () => {
    return <BasePage>

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
        <Dialog
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
        </Dialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useRemesaDetail;