import {Grid, Typography} from "@mui/material";
import {BasePage, Dialog} from "reactlib";
import {useState} from "react";
import TabComponent from "../../../components/TabComponent.tsx";
import {formatDate} from "../../../util/dateUtils.ts";

const ContenidoData = (props:any) => {
    const {title, children} = props;
    return <>
        <Grid item xs={6}><Typography variant={"h6"}>{title}</Typography></Grid>
        <Grid item xs={6}>{children}</Grid>
    </>
}

const Contenido = (props:any) => {
    const {entity} = props;
    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <ContenidoData title={"Nom del fitxer"}>{entity?.fitxerNom}</ContenidoData>
            <ContenidoData title={"Tipus de contingut"}>{entity?.fitxerContentType}</ContenidoData>
            <ContenidoData title={"Tipus de document"}>{entity?.metaDocument?.description}</ContenidoData>
            <ContenidoData title={"Data de creació"}>{formatDate(entity?.createdDate)}</ContenidoData>
            <ContenidoData title={"Estat"}>{entity?.estat}</ContenidoData>
            <ContenidoData title={"Data de captura"}>{formatDate(entity?.dataCaptura)}</ContenidoData>
            <ContenidoData title={"Orígen"}>{entity?.ntiOrigen}</ContenidoData>
            <ContenidoData title={"Tipus documental NTI"}>{entity?.ntiTipoDocumental}</ContenidoData>
            <ContenidoData title={"Estat d'elaboració"}>{entity?.ntiEstadoElaboracion}</ContenidoData>
            <ContenidoData title={"CSV"}>{entity?.ntiCsv}</ContenidoData>
            <ContenidoData title={"Regulació del CSV"}>{entity?.ntiCsvRegulacion}</ContenidoData>
            <ContenidoData title={"Tipus de firma"}>{entity?.ntiTipoFirma}</ContenidoData>
        </Grid>
    </BasePage>
}

const Versiones = () => {
    return <Typography>Versiones</Typography>;
}

const useDocumentDetail = () => {
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
            value: "resum",
            label: 'Contenido',
            content: <Contenido entity={entity}/>,
        },
        {
            value: "estat",
            label: 'Versiones',
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
                    text: 'Descargar',
                    icon: 'download'
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='download') {
                    // TODO: download action
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