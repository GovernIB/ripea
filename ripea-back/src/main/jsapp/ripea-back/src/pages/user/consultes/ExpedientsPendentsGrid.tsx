import { useTranslation } from 'react-i18next';
import { useMemo, useState } from 'react';
import { GridPage } from 'reactlib';
import { CardPage } from '../../../components/CardData.tsx';
import StyledMuiGrid from '../../../components/StyledMuiGrid.tsx';
import { formatDate } from '../../../util/dateUtils.ts';
import { GridSortDirection } from '@mui/x-data-grid-pro';
import GridFormField from '../../../components/GridFormField.tsx';
import * as builder from '../../../util/springFilterUtils.ts';
import StyledMuiFilter from '../../../components/StyledMuiFilter.tsx';
import useAnotacioActions from '../../anotacioExpedient/details/AnotacioActions.tsx';

const ExpedientsPendentsFilterForm = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="numRegistre" />
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="extracte" />
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="metaExpedient" />
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="dataRecepcioInicial" type={'date'} />
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="dataRecepcioFinal" type={'date'} />
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('identificador', data.numRegistre),
        builder.like('registre.extracte', data.extracte),
        builder.eq('metaExpedient.id', data?.metaExpedient?.id),
        builder.betweenDates('registre.data', data.dataRecepcioInicial, data.dataRecepcioFinal)
    );
};

const ExpedientsPendentsFilter = (props: any) => {
    const { onSpringFilterChange } = props;

    return (
        <StyledMuiFilter
            resourceName={'expedientPeticioResource'}
            code={'ANOTACIO_FILTER'}
            sessionKey={'ANOTACIO_PENDENT_FILTER'}
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={onSpringFilterChange}
        >
            <ExpedientsPendentsFilterForm />
        </StyledMuiFilter>
    );
};

// Grid
const sortModel: any = [{ field: 'registreInfo.data', sort: 'desc' }];
const perspectives = ['REGISTRE'];
const namedQueries = ['LLISTAT_ANOTACIONS','ESTAT_PENDENT'];

const ExpedientsPendentsGrid = () => {
    const { t } = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();

    const columns = useMemo(
        () => [
            {
                field: 'identificador',
                flex: 0.75,
            },
            {
                field: 'registreInfo.extracte',
                headerName: t('page.registre.grid.extracte'),
                flex: 1,
                sortProcessor: (_field: string, sort: GridSortDirection) => {
                    return [{ field: 'registre.extracte', sort }];
                },
            },
            {
                field: 'metaExpedient',
                flex: 1,
            },
            {
                field: 'registreInfo.data',
                headerName: t('page.registre.grid.dataRecepcio'),
                flex: 0.75,
                valueFormatter: (value: any) => formatDate(value),
                sortProcessor: (_field: string, sort: GridSortDirection) => {
                    return [{ field: 'registre.data', sort }];
                },
            },
        ],
        [t]
    );

    const { actions, components } = useAnotacioActions();

    return (
        <GridPage autoHeight>
            <CardPage title={t('page.user.menu.pendents')}>
                <ExpedientsPendentsFilter onSpringFilterChange={setSpringFilter} />
                <StyledMuiGrid
                    resourceName={'expedientPeticioResource'}
                    persistentStateKey={'expedientPeticioResource_pendentsDistribucio'}
                    columns={columns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    perspectives={perspectives}
                    namedQueries={namedQueries}
                    sortModel={sortModel}
                    rowAdditionalActions={actions}
                    readOnly
                />
            </CardPage>
            {components}
        </GridPage>
    );
};
export default ExpedientsPendentsGrid;
