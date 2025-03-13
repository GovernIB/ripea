import {
    GridPage,
    MuiGrid,
} from 'reactlib';
import {useParams} from "react-router-dom";
import {Button, Icon} from "@mui/material";
import useAnotacionsDetalle from "./AnotacionsDetalle.tsx";

const AnotacionsGrid: React.FC = () => {
    const { id } = useParams();

    const {handleOpen, dialog} = useAnotacionsDetalle();

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
                return <Button onClick={()=>handleOpen(params.row)}><Icon>info</Icon>Detalle</Button>;
            }
        },
    ];
    return <GridPage>
        <MuiGrid
            resourceName="expedientPeticioResource"
            columns={columns}
            paginationActive
            filter={`expedient.id:${id}`}
            titleDisabled
            readOnly
        />
        {dialog}
    </GridPage>
}

export default AnotacionsGrid;