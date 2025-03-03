import {
    GridPage,
    MuiGrid,
} from 'reactlib';
import {useParams} from "react-router-dom";

const InteressatsGrid: React.FC = () => {
    const { id } = useParams();

    const columns = [
        {
            field: 'documentTipus',
            flex: 0.5,
        },
        {
            field: 'documentNum',
            flex: 0.5,
        },
        {
            field: 'nomComplet',
            flex: 0.5,
        },
        {
            field: 'representant',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
    ];
    return <GridPage>
        <MuiGrid
            resourceName="interessatResource"
            columns={columns}
            paginationActive
            height={5}
            filter={`expedient.id:${id}`}
            titleDisabled
            readOnly
        />
    </GridPage>
}

export default InteressatsGrid;