import {
    GridPage,
    MuiGrid, useMuiDataGridApiRef,
} from 'reactlib';
import {useParams} from "react-router-dom";
import useAnotacioActions from "./details/AnotacioActions.tsx";

const AnotacionsGrid: React.FC = () => {
    const { id } = useParams();
    const apiRef = useMuiDataGridApiRef()

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
    ];
    const {actions, components} = useAnotacioActions(apiRef?.current?.refresh);

    return <GridPage>
        <MuiGrid
            resourceName="expedientPeticioResource"
            columns={columns}
            rowAdditionalActions={actions}
            paginationActive
            filter={`expedient.id:${id}`}
            titleDisabled
            readOnly
        />
        {components}
    </GridPage>
}

export default AnotacionsGrid;