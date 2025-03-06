import {
    Dialog,
    GridPage,
    MuiGrid,
} from 'reactlib';
import {useParams} from "react-router-dom";
import {useState} from "react";
import {Button, Icon} from "@mui/material";
import AnotacionsDetalle from "./AnotacionsDetalle.tsx";

const AnotacionsGrid: React.FC = () => {
    const { id } = useParams();
    const [open, setOpen] = useState(false);
    const [expedientPeticio, setExpedientPeticio] = useState<any>();

    const handleClickOpen = (param :any) :void => {
        setExpedientPeticio(param)
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const columns = [
        {
            field: 'registre.extracte',
            headerName: 'Extracto',
            flex: 0.5,
        },
        {
            field: 'registre.origenRegistreNumero',
            headerName: 'Numero registro',
            flex: 0.5,
        },
        {
            field: 'registre.data',
            headerName: 'Fecha registro',
            flex: 0.5,
        },
        {
            field: 'registre.destiDescripcio',
            headerName: 'Destino',
            flex: 0.5,
        },
        {
            field: 'id',
            headerName: '',
            flex: 0.5,
            renderCell: (params: any) => {
                return <Button onClick={()=>handleClickOpen(params.row)}><Icon>info</Icon>Detalle</Button>;
            }
        },
    ];
    return <GridPage>
        <Dialog
            open={open}
            closeCallback={handleClose}
            title={"Detalles de la anotación de registro"}
            componentProps={{ fullWidth: true, maxWidth: 'xl' }}
            buttons={[
                {
                    value: 'close',
                    text: 'Close'
                },
            ]}
            buttonCallback={(value :any) :void=>{
                console.log(value);
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <AnotacionsDetalle entity={expedientPeticio}/>
        </Dialog>

        <MuiGrid
            resourceName="expedientPeticioResource"
            columns={columns}
            paginationActive
            filter={`expedient.id:${id}`}
            titleDisabled
            readOnly
        />
    </GridPage>
}

export default AnotacionsGrid;