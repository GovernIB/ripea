import {useState} from "react";
import {Card, CardContent, CardHeader, Grid, Typography} from "@mui/material";
import {BasePage, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";

const ContenidoData = (props:any) => {
    const {title, children} = props;
    return <>
        <Grid item xs={6}><Typography variant={"h6"}>{title}</Typography></Grid>
        <Grid item xs={6}>{children}</Grid>
    </>
}
const CardContenido = (props:any) => {
    const {title, children} = props;
    return <Card sx={{border: '1px solid #e3e3e3', borderRadius: '5px'}}>
        <CardHeader title={title} sx={{backgroundColor: '#f5f5f5', borderBottom: '1px solid #e3e3e3'}}/>
        <CardContent>
            {children}
        </CardContent>
    </Card>
}

const Accions = () => {
    return <BasePage>
        <Typography>Accions</Typography>
    </BasePage>;
}
const Moviment = () => {
    return <BasePage>
        <Typography>Moviment</Typography>
    </BasePage>;
}
const Auditoria = (props:any) => {
    const { entity } = props;
    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <Grid item xs={6}>
                <CardContenido title={"Creació"}>
                    <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                        <ContenidoData title={"Usuario"} >{entity?.createdBy}</ContenidoData>
                        <ContenidoData title={"Fecha"} >{entity?.createdDate}</ContenidoData>
                    </Grid>
                </CardContenido>
            </Grid>
            <Grid item xs={6}>
                <CardContenido title={"Modificació"}>
                    <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                        <ContenidoData title={"Usuario"} >{entity?.lastModifiedBy}</ContenidoData>
                        <ContenidoData title={"Fecha"} >{entity?.lastModifiedDate}</ContenidoData>
                    </Grid>
                </CardContenido>
            </Grid>
        </Grid>
    </BasePage>;
}

const useHistoric = () => {
    const { t } = useTranslation();
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
            value: 'actions',
            label: t('page.document.tabs.actions'),
            content: <Accions/>,
        },
        {
            value: "move",
            label: t('page.document.tabs.move'),
            content: <Moviment/>,
        },
        {
            value: "auditoria",
            label: t('page.document.tabs.auditoria'),
            content: <Auditoria entity={entity}/>,
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={"Histórico de acciones del elemento"}
            componentProps={{ fullWidth: true, maxWidth: 'xl'}}
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
export default useHistoric;