import {Grid, Typography} from "@mui/material";
import {BasePage} from "reactlib";
import {useState} from "react";
import TabComponent from "../../../components/TabComponent.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {useTranslation} from "react-i18next";
import Dialog from "../../../../lib/components/mui/Dialog.tsx";

const InformacionData = (props:any) => {
    const {title, children, hxs, bsx} = props;
    return <>
        <Grid item xs={hxs ?? 6}><Typography variant={"h6"}>{title}</Typography></Grid>
        <Grid item xs={bsx ?? 6}>{children}</Grid>
    </>
}

const InformacionExpediente = (props:any) => {
    const { t } = useTranslation();
    const {entity} = props;
    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <InformacionData title={t('page.arxiu.detall.arxiuUuid')}>{entity?.arxiuUuid}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.fitxerNom')}>{entity?.nom}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.serie')}> </InformacionData>

            <InformacionData title={t('page.arxiu.detall.metadata')}/>
            <InformacionData title={t('page.arxiu.detall.versions')}> </InformacionData>
            <InformacionData title={t('page.arxiu.detall.identificador')}>{entity?.ntiIdentificador}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.organ')}>{entity?.organGestor?.description}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.dataApertura')}>{formatDate(entity?.createdDate)}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.clasificacion')}>{entity?.ntiClasificacionSia}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.estat')}>{entity?.estat}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.interessats')}>{entity?.interessats?.map((interessat:any)=>interessat?.documentNum).join(', ')}</InformacionData>
        </Grid>
    </BasePage>
}
const InformacionDocumento = (props:any) => {
    const { t } = useTranslation();
    const {entity} = props;
    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <InformacionData title={t('page.arxiu.detall.arxiuUuid')}>{entity?.arxiuUuid}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.fitxerNom')}>{entity?.fitxerNom}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.serie')}> </InformacionData>
            <InformacionData title={t('page.arxiu.detall.arxiuEstat')}>{entity?.arxiuEstat}</InformacionData>

            <InformacionData title={t('page.arxiu.detall.document')}/>
            <InformacionData title={t('page.arxiu.detall.fitxerContentType')}>{entity?.fitxerContentType}</InformacionData>

            <InformacionData title={t('page.arxiu.detall.metadata')}/>
            <InformacionData title={t('page.arxiu.detall.versions')}> </InformacionData>
            <InformacionData title={t('page.arxiu.detall.identificador')}>{entity?.ntiIdentificador}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.organ')}>{entity?.ntiOrgano} - {entity?.entitat?.description}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.dataCaptura')}>{entity?.dataCaptura}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.origen')}>{entity?.ntiOrigen}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.estadoElaboracion')}>{entity?.ntiEstadoElaboracion}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.tipoDocumental')}>{entity?.ntiTipoDocumental}</InformacionData>
            <InformacionData title={t('page.arxiu.detall.format')}>{entity?.fitxerNom.split('.').reverse()[0]}</InformacionData>
        </Grid>
    </BasePage>
}

const Metadatos = () => {
    return <Typography>Metadatos</Typography>;
}

const Hijos = () => {
    return <Typography>Hijos</Typography>;
}

const useInformacioArxiu = (tipo:string) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

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
            value: "resum",
            label: t('page.arxiu.tabs.resum'),
            content: tipo=="expediente"
                ?<InformacionExpediente entity={entity}/>
                :<InformacionDocumento entity={entity}/>,
        },
        {
            value: "fills",
            label: t('page.arxiu.tabs.fills'),
            content: <Hijos/>,
            // badge: entity?.,
            hidden: entity?.tipus == "DOCUMENT",
        },
        {
            value: "data",
            label: t('page.arxiu.tabs.data'),
            content: <Metadatos/>,
            // badge: entity?.,
        },
    ]

    const dialog =
        <Dialog
            open={open}
            closeCallback={handleClose}
            title={t('page.arxiu.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'xl'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    icon: 'close',
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
export default useInformacioArxiu;