import {GridPage} from 'reactlib';
import {useTranslation} from "react-i18next";
import useAnotacioActions from "./details/AnotacioActions.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import * as builder from "../../util/springFilterUtils.ts";
import {GridSortDirection} from "@mui/x-data-grid-pro";

const sortModel:any = [{field: 'registreInfo.data', sort: 'desc'}];
const perspectives = ['REGISTRE', 'ESTAT_VIEW'];

const AnotacionsExpedientGrid = (props:any) => {
    const { id } = props;
    const { t } = useTranslation();

    const columns = [
        {
            field: 'registreInfo.extracte',
            headerName: t('page.registre.grid.extracte'),
            sortable: false,
            flex: 1,
        },
        {
            field: 'registreInfo.origenRegistreNumero',
            headerName: t('page.registre.grid.origenRegistreNumero'),
            sortable: false,
            flex: 0.5,
        },
        {
            field: 'registreInfo.data',
            headerName: t('page.registre.grid.data'),
            flex: 0.5,
            valueFormatter: (value: any) => formatDate(value),
            sortProcessor: (field: string, sort: GridSortDirection) => {
                return [{field: 'registre.data', sort}];
            },
        },
        {
            field: 'registreInfo.destiDescripcio',
            headerName: t('page.registre.grid.destiDescripcio'),
            sortable: false,
            flex: 0.5,
        },
    ];

    const {actions, components} = useAnotacioActions();

    return <>
        <StyledMuiGrid
            resourceName="expedientPeticioResource"
            filter={builder.eq('expedient.id', id)}
            sortModel={sortModel}
            perspectives={perspectives}
            columns={columns}
            rowAdditionalActions={actions}
            // paginationActive
            disableColumnSorting
            readOnly
        />
        {components}
    </>
}

export default AnotacionsExpedientGrid;