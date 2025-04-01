import {
    GridPage,
    MuiGrid, useMuiDataGridApiRef,
} from 'reactlib';
import useAnotacioActions from "./details/AnotacioActions.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import {useTranslation} from "react-i18next";

const AnotacionsGrid = (props:any) => {
    const { id } = props;
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef()
    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

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
            headerName: t('page.anotacio.grid.destiDescripcio'),
            flex: 0.5,
        },
    ];

    const {actions, components} = useAnotacioActions(refresh);

    return <GridPage>
        <MuiGrid
            resourceName="expedientPeticioResource"
            perspectives={['REGISTRE', 'ESTAT_VIEW']}
            columns={columns}
            rowAdditionalActions={actions}
            paginationActive
            filter={`expedient.id:${id}`}
            titleDisabled
            apiRef={apiRef}
            staticSortModel={[{field: 'registreInfo.data', sort: 'desc'}]}
            disableColumnMenu
            disableColumnSorting
            readOnly
        />
        {components}
    </GridPage>
}

export default AnotacionsGrid;