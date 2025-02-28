import {
    GridPage,
    MuiGrid,
} from 'reactlib';
import {useParams} from "react-router-dom";
import {formatDate} from "../../../util/dateUtils.ts";
import Button from "@mui/material/Button";

const InteressatsGrid: React.FC = () => {
    const { id } = useParams();

    const columns = [
        {
            field: 'metaExpedientTasca',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
        {
            field: 'dataInici',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return formatDate(value);
            }
        },
        {
            field: 'dataLimit',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return formatDate(value);
            }
        },
        {
            field: 'titol',
            flex: 0.5,
        },
        {
            field: 'observacions',
            flex: 0.5,
        },
        {
            field: 'responsablesStr',
            flex: 0.5,
        },
        {
            field: 'responsableActual',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
        {
            field: 'estat',
            flex: 0.5,
        },
        {
            field: 'prioritat',
            flex: 0.5,
        },
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            disableColumnMenu: true,
            flex: 0.5,
            valueFormatter: (value: any) => {
                return <Button>{value}</Button>;
            }
        },
    ];
    return <GridPage>
        <MuiGrid
            resourceName="expedientTascaResource"
            columns={columns}
            paginationActive
            height={5}
            filter={`expedient.id:${id}`}
            titleDisabled
            readOnly
            perspectives={["RESPONSABLES_RESUM"]}
        />
    </GridPage>
}

export default InteressatsGrid;