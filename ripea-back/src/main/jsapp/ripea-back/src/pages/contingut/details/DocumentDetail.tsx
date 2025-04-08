import {Grid, Typography} from "@mui/material";
import {BasePage, useResourceApiService} from "reactlib";
import {useState} from "react";
import TabComponent from "../../../components/TabComponent.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import {useTranslation} from "react-i18next";
import Dialog from "../../../../lib/components/mui/Dialog.tsx";
import {ContenidoData} from "../../../components/CardData.tsx";

const Contenido = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();
    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <ContenidoData title={t('page.document.detall.fitxerNom')}>{entity?.fitxerNom}</ContenidoData>
            <ContenidoData title={t('page.document.detall.fitxerContentType')}>{entity?.fitxerContentType}</ContenidoData>
            <ContenidoData title={t('page.document.detall.metaDocument')}>{entity?.metaDocument?.description}</ContenidoData>
            <ContenidoData title={t('page.document.detall.createdDate')}>{formatDate(entity?.createdDate)}</ContenidoData>
            <ContenidoData title={t('page.document.detall.estat')}>{entity?.estat}</ContenidoData>
            <ContenidoData title={t('page.document.detall.dataCaptura')}>{formatDate(entity?.dataCaptura)}</ContenidoData>
            <ContenidoData title={t('page.document.detall.origen')}>{t(`enum.origen.${entity?.ntiOrigen}`)}</ContenidoData>
            <ContenidoData title={t('page.document.detall.tipoDocumental')}>{entity?.ntiTipoDocumental}</ContenidoData>
            <ContenidoData title={t('page.document.detall.estadoElaboracion')}>{t(`enum.estatElaboracio.${entity?.ntiEstadoElaboracion}`)}</ContenidoData>
            <ContenidoData title={t('page.document.detall.csv')}>{entity?.ntiCsv}</ContenidoData>
            <ContenidoData title={t('page.document.detall.csvRegulacion')}>{entity?.ntiCsvRegulacion}</ContenidoData>
            <ContenidoData title={t('page.document.detall.tipoFirma')}>{t(`enum.tipoFirma.${entity?.ntiTipoFirma}`)}</ContenidoData>
        </Grid>
    </BasePage>
}

const Versiones = () => {
    return <Typography>Versiones</Typography>;
}

const useDocumentDetail = () => {
    const { t } = useTranslation();

    const {
        fieldDownload: apiDownload,
    } = useResourceApiService('documentResource');

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row);
        setEntity(row);
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    };

    const tabs = [
        {
            value: 'resum',
            label: t('page.document.tabs.resum'),
            content: <Contenido entity={entity}/>,
        },
        {
            value: "version",
            label: t('page.document.tabs.version'),
            content: <Versiones/>,
            badge: entity?.versioCount,
        },
    ]

    const dialog =
        <Dialog
            open={open}
            closeCallback={handleClose}
            title={entity?.nom}
            componentProps={{ fullWidth: true, maxWidth: 'xl'}}
            buttons={[
                {
                    value: 'download',
                    text: t('common.download'),
                    icon: 'download'
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='download') {
                    apiDownload(entity?.id,{fieldName: 'adjunt'})
                        .then((result:any)=>{
                            const url = URL.createObjectURL(result.blob);
                            const link = document.createElement('a');
                            link.href = url;
                            link.download = result.fileName; // Usa el nombre recibido
                            document.body.appendChild(link);
                            link.click();

                            // Limpieza
                            document.body.removeChild(link);
                            URL.revokeObjectURL(url);
                        })
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
export default useDocumentDetail;