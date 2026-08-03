import { GridPage, MuiDialog, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService } from 'reactlib';
import { CardPage, DetailCard } from '../../../components/CardData';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import StyledMuiGrid from '../../../components/StyledMuiGrid.tsx';
import { formatDate } from '../../../util/dateUtils.ts';
import { Alert, Box, Icon, IconButton, Typography } from '@mui/material';
import GridFormField, { GridButtonField } from '../../../components/GridFormField.tsx';
import * as builder from '../../../util/springFilterUtils.ts';
import StyledMuiFilter from '../../../components/StyledMuiFilter.tsx';
import { useActions, useMassiveActions } from '../../anotacions/details/AnotacioActions.tsx';
import { FieldData, MuiDetail } from '@src/components/MuiDetail.tsx';
import { ErrorArea } from '@src/components/ErrorPage.tsx';

const useAnotacionsComunicadesDetail = () => {
    const { t } = useTranslation();

    const { isReady: apiIsReady, getOne: apiGetOne, currentFields: fields } = useResourceApiService('expedientPeticioResource');
    const { temporalMessageShow } = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id: any) => {
        if (apiIsReady && id) {
            apiGetOne(id)
                .then((app) => setEntity(app))
                .catch((error) => {
                    handleClose();
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const getLabelByName = (fields: any, name: string) => {
        const field = fields?.find((f: { name: string; label: string }) => f.name === name);
        return field?.label ?? field?.name;
    };

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.tasca.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            dialogContentProps={{ sx: { pt: 2 } }}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' },
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <MuiDetail entity={entity} fields={fields}>
                <DetailCard>
                    <FieldData field={'identificador'} size={12} />
                    <FieldData field={'estat'} size={4} />
                    <FieldData field={'dataAlta'} size={4}>
                        {formatDate(entity?.dataAlta)}
                    </FieldData>
                    <FieldData field={'consultaWsErrorDate'} size={4} sx={{ borderRight: '1px solid' }}>
                        {formatDate(entity?.consultaWsErrorDate)}
                    </FieldData>
                    <FieldData field={'consultaWsError'} size={4} sx={{ borderBottom: '1px solid' }}>
                        <Icon>{entity?.consultaWsError ? 'check' : 'close'}</Icon>
                    </FieldData>
                    <FieldData field={'pendentCanviEstatDistribucio'} size={4} sx={{ borderBottom: '1px solid' }}>
                        <Icon>{entity?.pendentCanviEstatDistribucio ? 'check' : 'close'}</Icon>
                    </FieldData>
                    <FieldData field={'reintentsCanviEstatDistribucio'} size={4} sx={{ borderRight: '1px solid', borderBottom: '1px solid' }} />
                </DetailCard>
            </MuiDetail>
            {entity?.consultaWsErrorDesc && (
                <Alert icon={false} severity="error" sx={{ mt: 2, borderRadius: '6px' }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                        <Typography variant="body1">{getLabelByName(fields, 'consultaWsErrorDesc')}</Typography>
                        <IconButton
                            title={t('common.copy')}
                            color="inherit"
                            size="small"
                            onClick={() => {
                                navigator.clipboard.writeText(entity?.consultaWsErrorDesc);
                                temporalMessageShow(null, t('common.copyToClipboard'), 'success');
                            }}
                        >
                            <Icon sx={{ m: 0 }}>content_copy</Icon>
                        </IconButton>
                    </Box>
                    <ErrorArea sx={{ maxHeight: '400px' }}>{entity?.consultaWsErrorDesc}</ErrorArea>
                </Alert>
            )}
        </MuiDialog>
    );

    return {
        handleOpen,
        handleClose,
        dialog,
    };
};

const AnotacionsComunicadesFilterForm = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 2.15 }} name="numRegistre" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.15 }} name="estat" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.15 }} name="dataAltaInici" type={'date'} />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.15 }} name="dataAltaFi" type={'date'} />
            <GridButtonField size={{ xs: 12, sm: 2, md: 1 }} name={'nomesAmbErrors'} icon={'warning'} />
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('identificador', data?.numRegistre),
        builder.eq('estat', `'${data?.estat}'`),
        builder.betweenDates('dataAlta', data?.dataAltaInici, data?.dataAltaFi),
        data?.nomesAmbErrors && builder.eq('consultaWsError', true)
    );
};

const AnotacionsComunicadesFilter = (props: any) => {
    const { onSpringFilterChange } = props;

    return (
        <StyledMuiFilter
            resourceName={'expedientPeticioResource'}
            code={'ANOTACIONS_COMUNICADES_FILTER'}
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={onSpringFilterChange}
        >
            <AnotacionsComunicadesFilterForm />
        </StyledMuiFilter>
    );
};

// Grid
const sortModel: any = [{ field: 'dataAlta', sort: 'desc' }];
const columns = [
    {
        field: 'identificador',
        flex: 0.75,
    },
    {
        field: 'dataAlta',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'estat',
        flex: 0.5,
    },
    {
        field: 'consultaWsError',
        flex: 0.5,
        renderCell: (params: any) => params?.row?.consultaWsError && <Icon>check</Icon>,
    },
    {
        field: 'consultaWsErrorDate',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'pendentCanviEstatDistribucio',
        flex: 0.5,
        renderCell: (params: any) => params?.row?.pendentCanviEstatDistribucio && <Icon>check</Icon>,
    },
    {
        field: 'reintentsCanviEstatDistribucio',
        flex: 0.5,
    },
];
const namedQueries = ['LLISTAT_ANOTACIONS', 'CONSULTA_COMUNICADES'];
const AnotacionsComunicadesGrid = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    };

    const { consultar } = useActions(refresh);
    const { consultar: consultarMassive } = useMassiveActions(refresh);

    const { handleOpen: handleOpenDetail, dialog: dialogDetail } = useAnotacionsComunicadesDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: 'info',
            showInMenu: true,
            onClick: handleOpenDetail,
        },
        {
            label: t('page.anotacio.action.consultar.label'),
            icon: 'autorenew',
            showInMenu: true,
            onClick: consultar,
        },
    ];
    const massiveActions = [
        {
            label: t('page.anotacio.action.consultar.label'),
            icon: 'autorenew',
            showInMenu: false,
            onClick: consultarMassive,
        },
    ];

    return (
        <GridPage autoHeight>
            <CardPage title={t('page.user.menu.comunicades')}>
                <AnotacionsComunicadesFilter onSpringFilterChange={setSpringFilter} />
                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName={'expedientPeticioResource'}
                    persistentStateKey={'expedientPeticioResource_anotacionsComunicades'}
                    columns={columns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    sortModel={sortModel}
                    namedQueries={namedQueries}
                    rowAdditionalActions={actions}
                    toolbarMassiveActions={massiveActions}
                    toolbarHideCreate
                />
                {dialogDetail}
            </CardPage>
        </GridPage>
    );
};
export default AnotacionsComunicadesGrid;
