import {GridPage} from 'reactlib';
import {useTranslation} from "react-i18next";
import useAnotacioActions from "./details/AnotacioActions.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import * as builder from "../../util/springFilterUtils.ts";

const sortModel:any = [{field: 'registre.data', sort: 'desc'}];
const perspectives = ['REGISTRE', 'ESTAT_VIEW'];

const AnotacionsExpedientGrid = (props:any) => {
    const { id } = props;
    const { t } = useTranslation();

    const columns = [
        {
            field: 'registreInfo.extracte',
            headerName: t('page.registre.grid.extracte'),
            flex: 1,
        },
        {
            field: 'registreInfo.origenRegistreNumero',
            headerName: t('page.registre.grid.origenRegistreNumero'),
            flex: 0.5,
        },
        {
            field: 'registreInfo.data',
            headerName: t('page.registre.grid.data'),
            flex: 0.5,
            valueFormatter: (value: any) => formatDate(value)
        },
        {
            field: 'registreInfo.destiDescripcio',
            headerName: t('page.registre.grid.destiDescripcio'),
            flex: 0.5,
        },
    ];

    const {actions, components} = useAnotacioActions();

    return <GridPage>
        <StyledMuiGrid
            resourceName="expedientPeticioResource"
            filter={builder.eq('expedient.id', id)}
            staticSortModel={sortModel}
            perspectives={perspectives}
            columns={columns}
            rowAdditionalActions={actions}
            // paginationActive
            disableColumnSorting
            readOnly
        />
        {components}
    </GridPage>
}

export default AnotacionsExpedientGrid;