import {
    GridPage,
    MuiGrid,
} from 'reactlib';
import React, {useState} from "react";
import {useParams} from "react-router-dom";
import ContingutIcon from "./ContingutIcon.tsx";

const DocumentsGrid: React.FC = () => {
    const { id } = useParams();
    const [expand, setExpand] = useState<boolean>(true);
    const [vista, setVista] = useState<string>();
    const handleChange = (event) => {
        setVista(event.target.value);
    };

    const columns = [
        {
            field: 'nom',
            flex: 0.5,
            renderCell:(params: any)=>{
                return <ContingutIcon entity={params?.row}>{params?.row.nom}</ContingutIcon>
            }
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
            filter={`expedient.id:${id}`}
            perspectives={["PATH"]}
            titleDisabled
            readOnly
            treeData
            getTreeDataPath={(row)=>{
                console.log(row);
                switch (vista){
                    case "estat":return [`${row.estat}`,`${row.nom}`];
                    case "tipus":return [`${row.metaNode?.description}`,`${row.nom}`];
                    default:return row.parentPath.map((a:any)=>a.nom);
                }
            }}
            // checkboxSelection
            isGroupExpandedByDefault={()=>expand}

        />
    </GridPage>
}

export default DocumentsGrid;