import { useTranslation } from 'react-i18next';
import { GridPage, useFormContext, useMuiDataGridApiRef } from 'reactlib';
import { useMemo, useState } from 'react';
import { ScreenFrame } from '../../../components/ScreenFrame.tsx';
import { icons } from '../../../util/icons.ts';
import StyledMuiGrid from '../../../components/StyledMuiGrid.tsx';
import { Alert, Box } from '@mui/material';
import ContingutLink from '../../../components/ContingutLink.tsx';
import StyledMuiFilter from '../../../components/StyledMuiFilter.tsx';
import * as builder from '../../../util/springFilterUtils.ts';
import GridFormField from '../../../components/GridFormField.tsx';
import { GridSortDirection } from '@mui/x-data-grid-pro';
import { StyledEstat, StyledPrioritat } from '../../expedient/ExpedientGrid.tsx';
import { useSession } from '../../../components/SessionStorageContext.tsx';
import { useUserSession } from '../../../components/Session.tsx';
import useCambiarEstat, { useCambiarEstatMassive } from '../../expedient/actions/CambiarEstat.tsx';
import { useExecucioMassivaContingut } from '../actions/ExecucioMassivaGrid.tsx';

const CanviEstatFilterFrom = (props: any) => {
    const { findExpedientByName = false } = props;
    const { data } = useFormContext();
    const { value: user } = useUserSession();

    const expedientFilter = builder.and(builder.eq('metaExpedient.id', data?.procediment?.id), builder.eq('grup.id', data?.grup?.id));

    const grupVisible = !!user?.sessionScope?.isFiltreGrupsVisible;
    // Amb cerca avançada el bloc de botons ocupa 1.5 columnes: la fila bàsica reparteix les 10.5 restants
    const sizeBasic = 10.5 / (grupVisible ? 5 : 4);
    const sizeAdv1 = 12 / (grupVisible ? 3 : 2);

    const procediment = (size: number) => <GridFormField size={{ xs: 12, sm: 6, md: size }} name="procediment" />;
    const grup = (size: number) => (
        <GridFormField
            name="grup"
            size={{ xs: 12, sm: 6, md: size }}
            namedQueries={[`BY_PROCEDIMENT#${data?.procediment?.id}`]}
            disabled={!data?.procediment}
            hidden={!grupVisible}
        />
    );
    const expedientNom = (size: number) =>
        findExpedientByName ? (
            <GridFormField size={{ xs: 12, sm: 6, md: size }} name="nom" />
        ) : (
            <GridFormField size={{ xs: 12, sm: 6, md: size }} name="expedient" filter={expedientFilter} />
        );
    const estat = (size: number) => (
        <GridFormField
            size={{ xs: 12, sm: 6, md: size }}
            name="estatCustom"
            requestParams={{ metaExpedientId: data?.procediment?.id, withoutTancar: true }}
        />
    );
    const prioritat = (size: number) => <GridFormField size={{ xs: 12, sm: 6, md: size }} name="prioritat" />;

    return (
        <>
            {!data?.advanced && (
                <>
                    {procediment(sizeBasic)}
                    {grup(sizeBasic)}
                    {expedientNom(sizeBasic)}
                    {estat(sizeBasic)}
                    {prioritat(sizeBasic)}
                </>
            )}
            {data?.advanced && (
                <>
                    {procediment(sizeAdv1)}
                    {grup(sizeAdv1)}
                    {expedientNom(sizeAdv1)}
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.625 }} name="dataCreacioInici" type={'date'} />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.625 }} name="dataCreacioFi" type={'date'} />
                    {estat(2.625)}
                    {prioritat(2.625)}
                </>
            )}
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.eq('metaExpedient.id', data?.procediment?.id),
        builder.eq('grup.id', data?.grup?.id),
        builder.eq('id', data?.expedient?.id),
        builder.like('nom', data?.nom),
        builder.betweenDates('createdDate', data?.dataCreacioInici, data?.dataCreacioFi),
        data.estatCustom && builder.equals('estat', `'TANCAT'`, data.estatCustom === '-1'),
        data.estatCustom && data.estatCustom != '0' && data.estatCustom != '-1' && builder.eq('estatAdditional.id', data.estatCustom),
        builder.eq('prioritat', `'${data?.prioritat}'`)
    );
};

export const CanviEstatFilter = (props: any) => {
    const { onSpringFilterChange, sessionKey, findExpedientByName } = props;
    return (
        <StyledMuiFilter
            resourceName={'expedientResource'}
            code={'MASSIVE_CANVI_ESTAT_FILTER'}
            sessionKey={sessionKey}
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={onSpringFilterChange}
            advancedSearch
        >
            <CanviEstatFilterFrom findExpedientByName={findExpedientByName} />
        </StyledMuiFilter>
    );
};

const namedQueries: string[] = ['MASSIVE_ACTION_QUERY'];
const sortModel: any = [{ field: 'createdDate', sort: 'desc' }];
const columns = [
    {
        field: 'metaExpedient',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 1,
        renderCell: (params: any) => <ContingutLink id={params?.id}>{params?.formattedValue}</ContingutLink>,
    },
    {
        field: 'estat',
        flex: 0.75,
        renderCell: (params: any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>,
        sortProcessor: (field: string, sort: GridSortDirection) => {
            return [
                { field: 'estatAdditional', sort },
                { field: field, sort },
                { field: 'id', sort },
            ];
        },
    },
    {
        field: 'prioritat',
        flex: 0.5,
        renderCell: (params: any) => <StyledPrioritat entity={params?.row}>{params.formattedValue}</StyledPrioritat>,
    },
    {
        field: 'createdDate',
        flex: 0.5,
    },
    {
        field: 'createdByFullName',
        flex: 0.8,
        sortProcessor: (_field: string, sort: GridSortDirection) => [{ field: 'createdBy', sort }],
    },
];

export const CanviEstatMuiGrid = (props: any) => {
    return (
        <StyledMuiGrid
            resourceName={'expedientResource'}
            persistentStateKey={'expedientResource_massiu'}
            columns={columns}
            sortModel={sortModel}
            toolbarHideCreate
            toolbarShowFilterCount
            {...props}
        />
    );
};

const perspectives: any = ['ESTAT', 'AUDITORIA', 'EN_PROCES_CANVI_ESTAT'];
const CanviEstatGrid = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const sessionKey = 'MASSIVE_CANVI_ESTAT_FILTER';
    const { value: filterData } = useSession(sessionKey);
    const haveRequirements = useMemo(() => !!filterData?.procediment, [filterData?.procediment]);

    const refresh = () => {
        apiRef?.current?.refresh?.();
    };

    const { handleShow: handleCanviEstat, content: contentCanviEstat } = useCambiarEstat(refresh);
    const { handleShow: handleCanviEstatMassive, content: contentCanviEstatMassive } = useCambiarEstatMassive(refresh);
    const { handleOpen: handleContingutOpen, dialog: dialogContingut } = useExecucioMassivaContingut();

    const actions: any[] = [
        {
            label: t('page.expedient.action.changeEstat.label'),
            icon: 'logout',
            showInMenu: false,
            onClick: handleCanviEstat,
            hidden: (row: any) => row?.execucioMassivaCanviEstatId,
        },
        {
            label: t('page.user.action.massives.pending'),
            icon: <Box sx={{ color: 'warning.main' }}>schedule</Box>,
            showInMenu: false,
            onClick: (_id: any, row: any) => handleContingutOpen(row?.execucioMassivaCanviEstatId),
            hidden: (row: any) => !row?.execucioMassivaCanviEstatId,
        },
    ];
    const massiveActions = [
        {
            label: t('page.expedient.action.changeEstat.label'),
            icon: 'logout',
            showInMenu: false,
            onClick: (ids: any[]) => handleCanviEstatMassive(ids, { metaExpedient: filterData?.procediment }),
        },
    ];

    return (
        <GridPage>
            <ScreenFrame icon={icons.massiva} title={t('navigate.massiu.canviEstat')}>
                {!haveRequirements && (
                    <Alert severity={'info'} sx={{ mb: 1 }}>
                        {t('page.expedient.alert.canviEstat')}
                    </Alert>
                )}

                <CanviEstatFilter sessionKey={sessionKey} onSpringFilterChange={setSpringFilter} />

                <CanviEstatMuiGrid
                    apiRef={apiRef}
                    filter={springFilter}
                    perspectives={perspectives}
                    namedQueries={namedQueries}
                    rowAdditionalActions={actions}
                    toolbarMassiveActions={massiveActions}
                    isRowSelectable={(params: any) => !params?.row?.execucioMassivaCanviEstatId && haveRequirements}
                    disabledMassiveDefSelector={!haveRequirements}
                />
            </ScreenFrame>
            {contentCanviEstat}
            {contentCanviEstatMassive}
            {dialogContingut}
        </GridPage>
    );
};
export default CanviEstatGrid;
