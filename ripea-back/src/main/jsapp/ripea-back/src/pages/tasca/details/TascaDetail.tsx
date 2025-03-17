import {Grid, Typography} from "@mui/material";
import {Dialog} from "reactlib";
import {useState} from "react";
import {formatDate} from "../../../util/dateUtils.ts";

const ContenidoData = (props:any) => {
    const {title, children} = props;
    return <>
        <Grid item xs={6}><Typography variant={"h6"}>{title}</Typography></Grid>
        <Grid item xs={6}>{children}</Grid>
    </>
}

const useTascaDetail = () => {
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

    const dialog =
        <Dialog
            open={open}
            closeCallback={handleClose}
            title={"Detalles de la tarea"}
            componentProps={{ fullWidth: true, maxWidth: 'xl'}}
            buttons={[
                {
                    value: 'close',
                    text: 'Cerrar',
                    icon: 'close'
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <ContenidoData title={"Tipo de tarea"}>{entity?.metaExpedientTasca?.description}</ContenidoData>
                <ContenidoData title={"Descripción tipo de tarea"}>{entity?.metaExpedientTascaDescription}</ContenidoData>
                <ContenidoData title={"Creada por"}>{entity?.createdBy}</ContenidoData>
                <ContenidoData title={"Responsables"}>{entity?.responsablesStr}</ContenidoData>
                <ContenidoData title={"Responsable actual"}>{entity?.responsableActual?.description}</ContenidoData>
                <ContenidoData title={"Delegado"}>{entity?.delegat?.description}</ContenidoData>
                <ContenidoData title={"Observadores"}>{/*entity?.observadors*/}</ContenidoData>
                <ContenidoData title={"Fecha inicio"}>{formatDate(entity?.dataInici)}</ContenidoData>
                <ContenidoData title={"Duración"}>{entity?.duracio}</ContenidoData>
                <ContenidoData title={"Fecha limite"}>{formatDate(entity?.dataLimit, "DD/MM/Y")}</ContenidoData>
                <ContenidoData title={"Estado"}>{entity?.estat}</ContenidoData>
                <ContenidoData title={"Prioridad"}>{entity?.prioritat}</ContenidoData>
            </Grid>
        </Dialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useTascaDetail;