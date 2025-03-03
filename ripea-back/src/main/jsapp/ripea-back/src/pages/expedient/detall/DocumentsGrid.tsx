import {
    GridPage,
    MuiGrid,
} from 'reactlib';
import ContingutActionButton from "./ContingutActionButton.tsx";
import React from "react";
import {useParams} from "react-router-dom";

const DocumentsGrid: React.FC = () => {
    const { id } = useParams();

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
        {
            field: 'id',
            headerName: '',
            sortable: false,
            disableColumnMenu: true,
            flex: 0.5,
            renderCell: (params: any) => {
                return <ContingutActionButton entity={params?.row}/>;
            }
        },
    ];
    return <GridPage>
        <MuiGrid
            resourceName="documentResource"
            columns={columns}
            paginationActive
            height={5}
            filter={`expedient.id:${id}`}
            titleDisabled
            readOnly
        />
    </GridPage>
}

export default DocumentsGrid;