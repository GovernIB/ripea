import {
    GridPage,
    MuiGrid, useMuiDataGridApiRef,
} from 'reactlib';
import {useParams} from "react-router-dom";
import useAnotacioActions from "./details/AnotacioActions.tsx";

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

const AnotacionsGrid = () => {
    const { id } = useParams();
    const apiRef = useMuiDataGridApiRef()
    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = useAnotacioActions(refresh);

    return <GridPage>
        <MuiGrid
            resourceName="expedientPeticioResource"
            columns={columns}
            rowAdditionalActions={actions}
            paginationActive
            filter={`expedient.id:${id}`}
            titleDisabled
            apiRef={apiRef}
            readOnly
        />
        {components}
    </GridPage>
}

export default AnotacionsGrid;