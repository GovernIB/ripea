import {useTranslation} from "react-i18next";
import useAnotacioActions from "./details/AnotacioActions.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import * as builder from "../../util/springFilterUtils.ts";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import useAnotacioDetail from "../anotacions/details/AnotacioDetail.tsx";
import {useMemo} from "react";

const sortModel:any = [{field: 'registreInfo.data', sort: 'desc'}];
const perspectives = ['REGISTRE', 'ESTAT_VIEW'];

const AnotacionsExpedientGrid = (props:any) => {
    const { id } = props;
    const { t } = useTranslation();

    const columns = useMemo(() => [
        {
            field: 'registreInfo.extracte',
            headerName: t('page.registre.grid.extracte'),
            sortable: false,
            flex: 1,
        },
        {
            field: 'identificador',
            sortable: false,
            flex: 0.5,
        },
        {
            field: 'registreInfo.data',
            headerName: t('page.registre.grid.data'),
            flex: 0.5,
            valueFormatter: (value: any) => formatDate(value),
            sortProcessor: (_field: string, sort: GridSortDirection) => {
                return [{field: 'registre.data', sort}];
            },
        },
        {
            field: 'registreInfo.destiDescripcio',
            headerName: t('page.registre.grid.destiDescripcio'),
            sortable: false,
            flex: 0.5,
        },
    ], [t]);

    const {actions, components} = useAnotacioActions();
    const {handleOpen, dialog} = useAnotacioDetail();

    return <>
        <StyledMuiGrid
            resourceName="expedientPeticioResource"
			persistentStateKey={"expedientPeticioResource_expedientTab"}
            filter={builder.eq('expedient.id', id)}
            sortModel={sortModel}
            perspectives={perspectives}
            columns={columns}
            rowAdditionalActions={actions}
            paginationActive={false}
            autoHeight
            onRowClick={(params: any) => handleOpen(params?.row?.id, params?.row) }
            disableColumnSorting
            readOnly
        />
        {components}
        {dialog}
    </>
}

export default AnotacionsExpedientGrid;