import {
    GridPage,
    MuiGrid,
} from 'reactlib';
// import {useParams} from "react-router-dom";

const DocumentsGrid: React.FC = () => {
    // const { id } = useParams();

    const columns = [
        {
            field: 'nom',
            flex: 0.5,
        },
        {
            field: 'descripcio',
            flex: 0.5,
        },
        {
            field: 'metaNode',
            flex: 1,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
        {
            field: 'createdDate',
            flex: 0.5,
        },
        {
            field: 'createdBy',
            flex: 0.5,
        },
    ];
    return <GridPage>
        <MuiGrid
            resourceName="documentResource"
            columns={columns}
            paginationActive
            height={5}
            // filter={`expedient.id:${id}`}
            titleDisabled
        />
    </GridPage>
}

export default DocumentsGrid;