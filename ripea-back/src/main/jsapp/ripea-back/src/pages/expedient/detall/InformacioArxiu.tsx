import {Grid, Typography} from "@mui/material";
import {BasePage, Dialog} from "reactlib";
import {useState} from "react";
import TabComponent from "../../../components/TabComponent.tsx";

const InformacionData = (props:any) => {
    const {title, children} = props;
    return <>
        <Grid item xs={6}><Typography variant={"h6"}>{title}</Typography></Grid>
        <Grid item xs={6}>{children}</Grid>
    </>
}

const Informacion = (props:any) => {
    const {entity} = props;
    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            {/*<InformacionData title={"Identificador archivo"}>{entity?.}</InformacionData>*/}
            <InformacionData title={"Identificador"}>{entity?.ntiIdentificador}</InformacionData>
        </Grid>
    </BasePage>
}

const Metadatos = () => {
    return <Typography>Metadatos</Typography>;
}

const Hijos = () => {
    return <Typography>Hijos</Typography>;
}

const useInformacioArxiu = () => {
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
            content: <Informacion entity={entity}/>,
        },
        entity?.tipus!="DOCUMENT" && {
            value: "fills",
            label: 'Hijos',
            content: <Hijos/>,
            // badge: entity?.,
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