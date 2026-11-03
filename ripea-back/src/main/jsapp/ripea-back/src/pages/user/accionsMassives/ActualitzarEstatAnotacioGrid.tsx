import StyledMuiGrid from '../../../components/StyledMuiGrid.tsx';
import { useTranslation } from 'react-i18next';
import { GridPage, useFormContext, useMuiDataGridApiRef } from 'reactlib';
import { useState } from 'react';
import { ScreenFrame } from '../../../components/ScreenFrame.tsx';
import { icons } from '../../../util/icons.ts';
import { formatDate } from '../../../util/dateUtils.ts';
import StyledMuiFilter from '../../../components/StyledMuiFilter.tsx';
import * as builder from '../../../util/springFilterUtils.ts';
import GridFormField, { GridButtonField } from '../../../components/GridFormField.tsx';
import { useActions, useMassiveActions } from '../../anotacions/details/AnotacioActions.tsx';
import { useUserSession } from '../../../components/Session.tsx';
import { useExecucioMassivaContingut } from '../actions/ExecucioMassivaGrid.tsx';
import { Box } from '@mui/material';

const ActualitzarEstatAnotacioFilterFrom = () => {
    const { data } = useFormContext();
    const { value: user } = useUserSession();
    const expedientFilter = builder.and(builder.eq('metaExpedient.id', data?.procediment?.id), builder.eq('grup.id', data?.grup?.id));

    return (
        <>
            {!data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 3.5 }} name="procediment" />
                    <GridFormField size={{ xs: 12, sm: 4, md: 2.75 }} name="numero" />
                    <GridFormField size={{ xs: 12, sm: 4, md: 2.25 }} name="dataAltaInici" type={'date'} />
                    <GridFormField size={{ xs: 12, sm: 3, md: 2 }} name="estat" />
                </>
            )}
            {data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 3.5 }} name="procediment" />
                    <GridFormField
                        name="grup"
                        size={{ xs: 12, sm: 6, md: 3.25 }}
                        namedQueries={[`BY_PROCEDIMENT#${data?.procediment?.id}`]}
                        disabled={!data?.procediment}
                        hidden={!user?.sessionScope?.isFiltreGrupsVisible}
                    />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3.25 }} name="expedient" filter={expedientFilter} />
                    <GridFormField size={{ xs: 12, sm: 4, md: 2.5 }} name="numero" />
                    <GridFormField size={{ xs: 12, sm: 4, md: 2.75 }} name="dataAltaInici" type={'date'} />
                    <GridFormField size={{ xs: 12, sm: 4, md: 2.75 }} name="dataAltaFi" type={'date'} />
                    <GridFormField size={{ xs: 12, sm: 3, md: 2 }} name="estat" />
                    <GridButtonField size={{ xs: 12, sm: 3, md: 0.5 }} icon={'warning'} name="nomesPendents" />
                </>
            )}
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.eq('metaExpedient.id', data?.procediment?.id),
        builder.eq('grup.id', data?.grup?.id),
        builder.eq('expedient.id', data?.expedient?.id),
        builder.like('identificador', data?.numero),
        builder.betweenDates('dataAlta', data?.dataAltaInici, data?.dataAltaFi),
        builder.eq('estat', `'${data?.estat}'`),
        data.nomesPendents && builder.eq('pendentCanviEstatDistribucio', 'true')
    );
};

const ActualitzarEstatAnotacioFilter = (props: any) => {
    const { onSpringFilterChange } = props;
    return (
        <StyledMuiFilter
            resourceName={'expedientPeticioResource'}
            code={'ACTUALITZAR_ESTAT_FILTER'}
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={onSpringFilterChange}
            filterOnFieldEnterKeyPressed
            advancedSearch
        >
            <ActualitzarEstatAnotacioFilterFrom />
        </StyledMuiFilter>
    );
};

const namedQuery: string[] = ['MASSIU_ANOTACIONS_ESTAT'];
const perspectives: any = ['EN_PROCES_ACTUALITZAR_ESTAT'];
const sortModel: any = [{ field: 'dataAlta', sort: 'desc' }];
const columns = [
    {
        field: 'identificador',
        flex: 1,
    },
    {
        field: 'dataAlta',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'dataActualitzacio',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'reintentsCanviEstatDistribucio',
        flex: 0.7,
    },
    {
        field: 'estat',
        flex: 1,
    },
];

const ActualitzarEstatAnotacioGrid = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    };

    const { canviEstatDistribucio } = useActions(refresh);
    const { canviEstatDistribucio: canviMassiuEstatDis } = useMassiveActions(refresh);
    const { handleOpen: handleContingutOpen, dialog: dialogContingut } = useExecucioMassivaContingut();

    const actions: any[] = [
        {
            label: t('page.anotacio.action.canviEstatDistribucio.label'),
            icon: 'autorenew',
            showInMenu: false,
            onClick: canviEstatDistribucio,
            hidden: (row: any) => row?.execucioMassivaActualitzarEstatId,
        },
        {
            label: t('page.user.action.massives.pending'),
            icon: <Box sx={{ color: 'warning.main' }}>schedule</Box>,
            showInMenu: false,
            onClick: (_id: any, row: any) => handleContingutOpen(row?.execucioMassivaActualitzarEstatId),
            hidden: (row: any) => !row?.execucioMassivaActualitzarEstatId,
        },
    ];
    const massiveActions = [
        {
            label: t('page.anotacio.action.canviEstatDistribucio.label'),
            icon: 'autorenew',
            showInMenu: false,
            onClick: canviMassiuEstatDis,
        },
    ];

    return (
        <GridPage>
            <ScreenFrame icon={icons.massiva} title={t('navigate.massiu.expedientPeticioCanviEstatDistribucio')}>
                <ActualitzarEstatAnotacioFilter onSpringFilterChange={setSpringFilter} />

                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName={'expedientPeticioResource'}
                    persistentStateKey={'expedientPeticioResource_massActualitzarEstat'}
                    columns={columns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    sortModel={sortModel}
                    perspectives={perspectives}
                    namedQueries={namedQuery}
                    rowAdditionalActions={actions}
                    toolbarMassiveActions={massiveActions}
                    isRowSelectable={(params: any) => !params?.row?.execucioMassivaActualitzarEstatId}
                    toolbarHideCreate
                />
                {dialogContingut}
            </ScreenFrame>
        </GridPage>
    );
};
export default ActualitzarEstatAnotacioGrid;
