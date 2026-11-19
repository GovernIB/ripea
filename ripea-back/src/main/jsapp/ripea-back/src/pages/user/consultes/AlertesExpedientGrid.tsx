import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { Chip, Grid } from '@mui/material';
import { DialogButton, GridPage, MuiDialog, useFormContext, useMuiDataGridApiRef, useResourceApiService } from 'reactlib';
import { ScreenFrame } from '../../../components/ScreenFrame.tsx';
import { icons } from '../../../util/icons.ts';
import StyledMuiGrid from '../../../components/StyledMuiGrid.tsx';
import StyledMuiFilter from '../../../components/StyledMuiFilter.tsx';
import GridFormField from '../../../components/GridFormField.tsx';
import ContingutLink from '../../../components/ContingutLink.tsx';
import { DetailCard } from '../../../components/CardData.tsx';
import { FieldData, MuiDetail } from '../../../components/MuiDetail.tsx';
import { ErrorArea } from '../../../components/ErrorPage.tsx';
import Load from '../../../components/Load.tsx';
import { formatDate } from '../../../util/dateUtils.ts';
import * as builder from '../../../util/springFilterUtils.ts';
import { useUserSession } from '../../../components/Session.tsx';
import { useAlertaActions } from '../../expedient/details/Alerta.tsx';
import { tonal, tonalNeutre } from '../../../util/tonal.ts';

/**
 * Consulta «Alertes d'expedients».
 *
 * Mostra les alertes de l'expedient mateix (no les dels seus documents) dels expedients que l'usuari veu al
 * llistat d'expedients (named query CONSULTA_ALERTES del backend). Les no llegides són, per tant, exactament
 * les que fan aparèixer la icona d'alertes al llistat d'expedients i l'avís en entrar a l'expedient.
 */

const RESOURCE_NAME = 'alertaResource';
const FILTER_CODE = 'CONSULTA_ALERTES';
const namedQueries = ['CONSULTA_ALERTES'];
const perspectives = ['EXPEDIENT'];

/** Valors de AlertaEstatEnumDto. */
const ESTAT_NO_LLEGIDA = 'NO_LLEGIDA';
const ESTAT_LLEGIDA = 'LLEGIDA';

/** El filtre comença a «No llegides», també després de netejar-lo. */
const defaultFilterData = { estat: ESTAT_NO_LLEGIDA };

/** Selector de procediments: el mateix que el cercador d'expedients (procediments amb permís de lectura). */
const namedQueriesProcediment = ['EXPEDIENT_FILTRE#'];

// Els botons del filtre (Avançat i netejar) ocupen FILTER_BUTTONS_COLUMNS_ADVANCED (md 1,5): els camps de la
// fila on cauen sumen 10,5 perquè hi càpiguen en una sola línia.
const AlertesExpedientFilterForm = () => {
    const { data } = useFormContext();
    return (
        <>
            {!data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="estat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="expedient" />
                    <GridFormField size={{ xs: 12, sm: 12, md: 4.5 }} name="procediment" namedQueries={namedQueriesProcediment} />
                </>
            )}
            {data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="estat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="expedient" />
                    <GridFormField size={{ xs: 12, sm: 12, md: 6 }} name="procediment" namedQueries={namedQueriesProcediment} />
                    <GridFormField size={{ xs: 12, sm: 12, md: 4.5 }} name="text" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="dataInici" type={'date'} />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="dataFi" type={'date'} />
                </>
            )}
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        data?.estat === ESTAT_NO_LLEGIDA && builder.eq('llegida', false),
        data?.estat === ESTAT_LLEGIDA && builder.eq('llegida', true),
        builder.or(
            builder.like('expedient.numero', data?.expedient),
            builder.like('expedient.nom', data?.expedient)
        ),
        builder.eq('expedient.metaExpedient.id', data?.procediment?.id),
        builder.like('text', data?.text),
        builder.betweenDates('createdDate', data?.dataInici, data?.dataFi)
    );
};

const AlertesExpedientFilter = (props: any) => {
    const { onSpringFilterChange } = props;
    return (
        <StyledMuiFilter
            resourceName={RESOURCE_NAME}
            code={FILTER_CODE}
            springFilterBuilder={springFilterBuilder}
            defaultData={defaultFilterData}
            advancedSearch
            onSpringFilterChange={onSpringFilterChange}
        >
            <AlertesExpedientFilterForm />
        </StyledMuiFilter>
    );
};

/** Estat de lectura de l'alerta en color tonal (vegeu util/tonal.ts), amb text (no només color). */
const EstatAlerta = ({ llegida }: { llegida?: boolean }) => {
    const { t } = useTranslation();
    return llegida
        ? <Chip size="small" sx={tonalNeutre} label={t('page.alertaExpedient.estat.llegida')} />
        : <Chip size="small" sx={tonal('error')} label={t('page.alertaExpedient.estat.noLlegida')} />;
};

const AlertaExpedientDetail = ({ entity, currentFields }: any) => {
    return (
        <MuiDetail entity={entity} fields={currentFields}>
            <DetailCard>
                <FieldData field={'createdDate'}>{formatDate(entity?.createdDate)}</FieldData>
                <FieldData field={'llegida'}><EstatAlerta llegida={entity?.llegida} /></FieldData>
                <FieldData field={'expedientNumero'}>
                    <ContingutLink id={entity?.contingut?.id}>{entity?.expedientNumero}</ContingutLink>
                </FieldData>
                <FieldData field={'expedientNom'} />
                <FieldData field={'procedimentNom'} />
                <FieldData field={'text'} />
            </DetailCard>
            {entity?.error && (
                <Grid size={12}>
                    <ErrorArea sx={{ maxHeight: '290px' }}>{entity?.error}</ErrorArea>
                </Grid>
            )}
        </MuiDetail>
    );
};

const useAlertaExpedientDetail = (potMarcarLlegida: boolean, marcarLlegida: (id: any) => void) => {
    const { t } = useTranslation();
    const { isReady: apiIsReady, currentFields } = useResourceApiService(RESOURCE_NAME);

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (_id: any, row: any) => {
        setEntity(row);
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const buttons = useMemo<DialogButton[]>(() => [
        // Enllaç a l'expedient en una pestanya nova, com la resta d'enllaços a expedients de les consultes
        {
            value: 'expedient',
            text: t('page.alertaExpedient.detail.obrirExpedient'),
            icon: icons.obreNovaFinestra,
            componentProps: {
                variant: 'outlined',
                component: RouterLink,
                to: `/contingut/${entity?.contingut?.id}`,
                target: '_blank',
                rel: 'noopener noreferrer',
            },
        },
        ...(potMarcarLlegida && entity && !entity?.llegida ? [{
            value: 'llegit',
            text: t('page.alert.action.read.label'),
            icon: 'mark_email_read',
            componentProps: { variant: 'outlined' },
        }] : []),
        {
            value: 'close',
            text: t('buttons.misc.close'),
            componentProps: { variant: 'contained' },
        },
    ], [t, entity, potMarcarLlegida]);

    const buttonCallback = (value: any) => {
        if (value === 'llegit') {
            marcarLlegida(entity?.id);
            handleClose();
        } else if (value === 'close') {
            handleClose();
        }
    };

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.alertaExpedient.detail.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={buttons}
            buttonCallback={buttonCallback}
        >
            <Load value={entity && apiIsReady}>
                <AlertaExpedientDetail entity={entity} currentFields={currentFields} />
            </Load>
        </MuiDialog>
    );

    return { handleOpen, dialog };
};

const sortModel: any[] = [{ field: 'createdDate', sort: 'desc' }];
const columns: any[] = [
    {
        field: 'createdDate',
        flex: 0.5,
        minWidth: 140,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'expedientNumero',
        flex: 0.6,
        // Camp de la perspectiva EXPEDIENT: s'ordena pel número de l'expedient
        sortProcessor: (_field: string, sort: any) => [{ field: 'expedient.numero', sort }],
        renderCell: (params: any) => <ContingutLink id={params?.row?.contingut?.id}>{params?.value}</ContingutLink>,
    },
    {
        field: 'expedientNom',
        flex: 1,
        sortProcessor: (_field: string, sort: any) => [{ field: 'expedient.nom', sort }],
    },
    {
        field: 'procedimentNom',
        flex: 1,
        sortProcessor: (_field: string, sort: any) => [{ field: 'expedient.metaExpedient.nom', sort }],
    },
    {
        field: 'text',
        flex: 1.5,
    },
    {
        field: 'llegida',
        flex: 0.4,
        minWidth: 110,
        renderCell: (params: any) => <EstatAlerta llegida={params?.row?.llegida} />,
    },
];

const AlertesExpedientGrid = () => {
    const { t } = useTranslation();
    const { rol } = useUserSession();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    };

    // L'administrador de lectura només consulta: no pot marcar alertes com a llegides
    const potMarcarLlegida = !rol?.isAdminLectura;
    const { llegit, massiveLlegit } = useAlertaActions(refresh);
    const { handleOpen, dialog } = useAlertaExpedientDetail(potMarcarLlegida, llegit);

    // Amb més d'una acció disponible a la fila, van totes al menú de tres punts, com a la resta de graelles.
    // Si només n'hi ha una (alerta ja llegida o administrador de lectura), es mostra directament la icona.
    const potMarcarFila = (row: any) => potMarcarLlegida && !row?.llegida;
    const actions = [
        {
            label: t('common.detail'),
            icon: icons.detall,
            showInMenu: potMarcarFila,
            onClick: handleOpen,
        },
        {
            label: t('page.alert.action.read.label'),
            icon: 'mark_email_read',
            showInMenu: true,
            onClick: llegit,
            hidden: (row: any) => !potMarcarFila(row),
        },
    ];
    const massiveActions = potMarcarLlegida ? [
        {
            label: t('page.alert.action.read.label'),
            icon: 'mark_email_read',
            showInMenu: false,
            onClick: massiveLlegit,
        },
    ] : undefined;

    return (
        <GridPage>
            <ScreenFrame icon={icons.alertes} title={t('page.user.menu.alertes')}>
                <AlertesExpedientFilter onSpringFilterChange={setSpringFilter} />

                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName={RESOURCE_NAME}
                    persistentStateKey={'alertaResource_consultaAlertes'}
                    columns={columns}
                    filter={springFilter}
                    namedQueries={namedQueries}
                    perspectives={perspectives}
                    toolbarShowFilterCount
                    sortModel={sortModel}
                    rowAdditionalActions={actions}
                    toolbarMassiveActions={massiveActions}
                    toolbarHideCreate
                />
                {dialog}
            </ScreenFrame>
        </GridPage>
    );
};
export default AlertesExpedientGrid;
