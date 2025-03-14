import {Grid, Typography} from "@mui/material";
import {BasePage, Dialog} from "reactlib";
import {useState} from "react";
import TabComponent from "../../../components/TabComponent.tsx";
import {formatDate} from "../../../util/dateUtils.ts";

const InformacionData = (props:any) => {
    const {title, children, hxs, bsx} = props;
    return <>
        <Grid item xs={hxs ?? 6}><Typography variant={"h6"}>{title}</Typography></Grid>
        <Grid item xs={bsx ?? 6}>{children}</Grid>
    </>
}

const InformacionExpediente = (props:any) => {
    const {entity} = props;
    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <InformacionData title={"Identificador archivo"}>{entity?.arxiuUuid}</InformacionData>
            <InformacionData title={"Nombre del archivo"}>{entity?.nom}</InformacionData>
            <InformacionData title={"Serie documental"}> </InformacionData>

            <InformacionData title={"Metadatos ENI"}/>
            <InformacionData title={"Versión"}> </InformacionData>
            <InformacionData title={"Identificador"}>{entity?.ntiIdentificador}</InformacionData>
            <InformacionData title={"Órgano"}>{entity?.organGestor?.description}</InformacionData>
            <InformacionData title={"Fecha apertura"}>{formatDate(entity?.createdDate)}</InformacionData>
            <InformacionData title={"Clasificación"}>{entity?.ntiClasificacionSia}</InformacionData>
            <InformacionData title={"Estado"}>{entity?.estat}</InformacionData>
            <InformacionData title={"Interesados"}>{entity?.interessats.map((interessat:any)=>interessat?.documentNum).join(', ')}</InformacionData>
        </Grid>
    </BasePage>
}
const InformacionDocumento = (props:any) => {
    const {entity} = props;
    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <InformacionData title={"Identificador archivo"}>{entity?.arxiuUuid}</InformacionData>
            <InformacionData title={"Nombre del archivo"}>{entity?.adjunt?.name}</InformacionData>
            <InformacionData title={"Serie documental"}> </InformacionData>
            <InformacionData title={"Estado del archivo"}>{entity?.arxiuEstat}</InformacionData>

            <InformacionData title={"Contenido documento"}/>
            <InformacionData title={"Tipo MIME"}>{entity?.fitxerContentType}</InformacionData>

            <InformacionData title={"Metadatos ENI"}/>
            <InformacionData title={"Versión"}> </InformacionData>
            <InformacionData title={"Identificador"}>{entity?.ntiIdentificador}</InformacionData>
            <InformacionData title={"Órgano"}>{entity?.ntiOrgano} - {entity?.entitat?.description}</InformacionData>
            <InformacionData title={"Fecha captura"}>{entity?.dataCaptura}</InformacionData>
            <InformacionData title={"Origen"}>{entity?.ntiOrigen}</InformacionData>
            <InformacionData title={"Estado elaboración"}>{entity?.ntiEstadoElaboracion}</InformacionData>
            <InformacionData title={"Tipo documental NTI"}>{entity?.ntiTipoDocumental}</InformacionData>
            <InformacionData title={"Formato nombre"}>{entity?.fitxerNom.split('.').reverse()[0]}</InformacionData>
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
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (row:any) => {
        console.log(row)
        setEntity(row);
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    };

    const tabs = [
        {
            value: "resum",
            label: 'Información',
            content: tipo=="expediente"
                ?<InformacionExpediente entity={entity}/>
                :<InformacionDocumento entity={entity}/>,
        },
        {
            value: "fills",
            label: 'Hijos',
            content: <Hijos/>,
            // badge: entity?.,
            hidden: entity?.tipus == "DOCUMENT",
        },
        {
            value: "estat",
            label: 'Metadatos',
            content: <Metadatos/>,
            // badge: entity?.,
        },
    ]

    const dialog =
        <Dialog
            open={open}
            closeCallback={handleClose}
            title={`Información obtenida del archivo`}
            componentProps={{ fullWidth: true, maxWidth: 'xl'}}
            buttons={[
                {
                    value: 'close',
                    text: 'Cerrar',
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