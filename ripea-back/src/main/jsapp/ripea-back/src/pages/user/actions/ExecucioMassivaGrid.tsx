import { useTranslation } from 'react-i18next';
import { useEffect, useMemo, useRef, useState } from 'react';
import { Chip } from '@mui/material';
import { GridPage, MuiDialog, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService, type MuiDataGridColDef } from 'reactlib';
import { tonal, tonalNeutre } from '../../../util/tonal.ts';
import { icons } from '../../../util/icons.ts';
import TonalLinearProgress from '../../../components/TonalLinearProgress.tsx';
import ScreenFrame from '../../../components/ScreenFrame.tsx';
import { ScreenFrameContext } from '../../../components/ScreenFrameContext.ts';
import GridDialogBody, { gridDialogProps } from '../../../components/GridDialogBody.tsx';
import StyledMuiGrid, { ToolbarButton } from '../../../components/StyledMuiGrid.tsx';
import Load from '../../../components/Load.tsx';
import { formatDate } from '../../../util/dateUtils.ts';
import * as builder from '../../../util/springFilterUtils.ts';
import { iniciaDescargaBlob } from '../../expedient/details/CommonActions.tsx';
import { useCloseDialogButtons, useConfirmDialogButtons } from '../../../util/buttonsOverride.tsx';
import { ExecucioMassivaContingutEstat, ExecucioMassivaEstat } from './ExecucioMassivaEstat.tsx';

const RESOURCE_NAME = 'execucioMassivaResource';
const RESOURCE_NAME_CONTINGUT = 'execucioMassivaContingutResource';
const ACCIO_CANCELAR = 'CANCELAR';
const RESULTAT_CANCELADA = 'CANCELADA';
const INTERVAL_REFRESC_MS = 10_000;
const BOTO_REFRESCA = 'refresca';
const BOTO_TANCA = 'close';

const sortModel: any = [{ field: 'createdDate', sort: 'desc' }];
const sortModelContingut: any = [{ field: 'dataFi', sort: 'desc' }];

/** Recompte dins un xip tonal: l'estil de color és el que el fa destacar. */
const XipRecompte = ({ valor = 0, color }: { valor?: number; color?: 'info' | 'error' }) => (
    <Chip label={valor} size="small" sx={color ? tonal(color) : tonalNeutre} />
);

/**
 * Percentatge d'elements tractats d'una execució. Processats = finalitzats o en error; els cancel·lats no
 * s'han executat i no compten com a fets (1 de 3 executat i 2 cancel·lats = 33%, no 100%).
 */
const percentatgeExecucio = (row?: any): number => {
    const total = row?.executades ?? 0;
    const processats = total - (row?.pendents ?? 0) - (row?.cancelats ?? 0);
    return total > 0 ? Math.round((processats * 100) / total) : 0;
};

const useActions = () => {
    const { t } = useTranslation();
    const { fieldDownload: apiDownload, artifactAction: apiAction } = useResourceApiService(RESOURCE_NAME);
    const { temporalMessageShow, messageDialogShow } = useBaseAppContext();
    const confirmButtons = useConfirmDialogButtons();

    const cancelar = (id: any, refresh?: () => void) => {
        messageDialogShow(t('page.user.action.massives.cancelar.title'), t('page.user.action.massives.cancelar.confirm'), confirmButtons, {
            maxWidth: 'sm',
            fullWidth: true,
        }).then((confirmat: any) => {
            if (!confirmat) {
                return;
            }
            apiAction(id, { code: ACCIO_CANCELAR })
                .then((result: any) => {
                    refresh?.();
                    // CANCELADA: no havia començat i ja està tancada; EN_CURS: s'aturarà en acabar l'element actual
                    temporalMessageShow(
                        null,
                        result === RESULTAT_CANCELADA
                            ? t('page.user.action.massives.cancelar.ok')
                            : t('page.user.action.massives.cancelar.sollicitadaOk'),
                        result === RESULTAT_CANCELADA ? 'success' : 'info'
                    );
                })
                .catch((error: any) => temporalMessageShow(null, error?.message, 'error'));
        });
    };

    const download = (id: any) => {
        apiDownload(id, { fieldName: 'documentNom' })
            .then((result: any) => {
                iniciaDescargaBlob(result);
                temporalMessageShow(null, t('page.user.action.massives.ok'), 'success');
            })
            .catch((error: any) => temporalMessageShow(null, error?.message, 'error'));
    };

    return { download, cancelar };
};

/**
 * Diàleg amb el detall d'una execució massiva: només el llistat dels seus elements amb el seu estat i, si han
 * fallat, el motiu. `refresh` el torna a carregar (per al refresc automàtic de la pantalla).
 * El fan servir també les pantalles d'accions massives per veure el procés d'un element pendent.
 */
export const useExecucioMassivaContingut = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const gridApiRef = useMuiDataGridApiRef();
    const closeButtons = useCloseDialogButtons(BOTO_TANCA);
    const { isReady: apiIsReady, getOne: apiGetOne, currentFields: fields } = useResourceApiService(RESOURCE_NAME);
    const { temporalMessageShow } = useBaseAppContext();

    // Refresca (a l'esquerra de Tanca): l'usuari decideix quan recarregar els elements
    const buttons = useMemo(
        () => [
            {
                value: BOTO_REFRESCA,
                text: t('common.refresh'),
                icon: icons.refresca,
                componentProps: { variant: 'contained' as const },
            },
            ...closeButtons,
        ],
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [t]
    );

    const intervalRef = useRef<any>(undefined);
    useEffect(() => {
        intervalRef.current = { open };
    }, [open]);

    const refresh = () => {
        if (intervalRef?.current?.open) {
            gridApiRef?.current?.refresh?.();
        }
    };

    const handleOpen = (id: any) => {
        if (apiIsReady && id) {
            apiGetOne(id)
                .then((app: any) => setEntity(app))
                .catch((error: any) => {
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

    const tipus = useMemo(() => fields?.find((item: any) => item?.name === 'tipus')?.options?.[entity?.tipus] ?? '', [fields, entity]);

    const columns: MuiDataGridColDef[] = useMemo(
        () => [
            { field: 'elementTipus', flex: 0.5 },
            {
                field: 'elementNom',
                flex: 1.2,
                renderCell: (params: any) => (
                    <span
                        title={params.value}
                        dangerouslySetInnerHTML={{ __html: params.value }}
                        style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', display: 'block' }}
                    />
                ),
            },
            {
                field: 'estat',
                flex: 0.6,
                minWidth: 160,
                renderCell: (params: any) => (
                    <ExecucioMassivaContingutEstat value={params?.row?.estat} label={params?.formattedValue} error={params?.row?.error} />
                ),
            },
            {
                field: 'dataFi',
                flex: 0.6,
                minWidth: 160,
                valueFormatter: (value: any) => formatDate(value),
            },
        ],
        []
    );

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.user.action.massives.detail', { tipus })}
            componentProps={gridDialogProps('lg')}
            buttons={buttons}
            buttonCallback={(value: any): void => {
                if (value === BOTO_REFRESCA) {
                    gridApiRef?.current?.refresh?.();
                } else {
                    handleClose();
                }
            }}
        >
            <Load value={entity}>
                {/* El diàleg és dins el ScreenFrame de la pantalla per context de React (els portals el travessen):
                    sense això la graella s'hi enganxaria i el recompte de filtres sortiria al títol de la pantalla. */}
                <ScreenFrameContext.Provider value={undefined}>
                    <GridDialogBody>
                        <StyledMuiGrid
                            resourceName={RESOURCE_NAME_CONTINGUT}
                            filter={builder.eq('execucioMassiva.id', `'${entity?.id}'`)}
                            apiRef={gridApiRef}
                            sortModel={sortModelContingut}
                            columns={columns}
                            toolbarHide
                            autoHeight
                            readOnly
                        />
                    </GridDialogBody>
                </ScreenFrameContext.Provider>
            </Load>
        </MuiDialog>
    );

    return { handleOpen, handleClose, dialog, refresh };
};

/**
 * Execucions massives: les accions que s'executen en segon pla sobre molts elements.
 * És una pantalla de consulta: les execucions les crea l'acció que les origina. De cada una es pot veure el detall
 * (els elements amb el seu estat), descarregar-ne el fitxer (exportacions) i, mentre no ha acabat, cancel·lar-la.
 */
const ExecucioMassivaGrid = () => {
    const { t } = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();
    const [autoRefresh, setAutoRefresh] = useState(false);
    const { download, cancelar } = useActions();
    const { handleOpen: mostrarDetall, dialog: dialogContingut, refresh: refrescaDetall } = useExecucioMassivaContingut();

    // El refresc automàtic recarrega el llistat i el detall obert (si n'hi ha). La funció de l'efecte no es torna
    // a crear a cada render, per això el que ha de ser actual es llegeix d'una referència.
    const refrescaTot = useRef<() => void>(() => undefined);
    refrescaTot.current = () => {
        gridApiRef?.current?.refresh?.();
        refrescaDetall();
    };

    useEffect(() => {
        if (!autoRefresh) {
            return undefined;
        }
        const interval = setInterval(() => refrescaTot.current(), INTERVAL_REFRESC_MS);
        return () => clearInterval(interval);
    }, [autoRefresh]);

    const actions: any[] = [
        {
            label: t('common.detail'),
            icon: icons.detall,
            showInMenu: true,
            onClick: (id: any) => mostrarDetall(id),
        },
        {
            label: t('common.download'),
            icon: 'download',
            showInMenu: true,
            onClick: download,
            hidden: (row: any) => !row?.documentNom,
        },
        {
            // Només per a execucions no acabades que l'usuari pot cancel·lar (creador o administrador d'entitat)
            // i sense cancel·lació ja sol·licitada. Exportacions i moure: visible però deshabilitada.
            label: t('page.user.action.massives.cancelar.label'),
            icon: icons.cancela,
            showInMenu: true,
            onClick: (id: any) => cancelar(id, () => refrescaTot.current()),
            hidden: (row: any) => !!row?.dataFi || !row?.potCancelar || !!row?.cancelacioData,
            disabled: (row: any) => !row?.cancelable,
            title: (row: any) => (!row?.cancelable ? t('page.user.action.massives.cancelar.noCancelable') : undefined),
        },
    ];

    const columns: MuiDataGridColDef[] = useMemo(
        () => [
            { field: 'tipus', flex: 1.2 },
            {
                field: 'estat',
                flex: 0.7,
                minWidth: 150,
                maxWidth: 170,
                sortable: false,
                renderCell: (params: any) => (
                    <ExecucioMassivaEstat
                        value={params?.row?.estat}
                        label={params?.formattedValue}
                        cancelacioData={params?.row?.cancelacioData}
                        dataFi={params?.row?.dataFi}
                    />
                ),
            },
            {
                // Nombre d'elements de l'execució
                field: 'executades',
                headerName: t('page.user.action.massives.grid.elements'),
                type: 'number',
                align: 'center',
                headerAlign: 'center',
                flex: 0.4,
                minWidth: 90,
                sortable: false,
                renderCell: (params: any) => <XipRecompte valor={params?.row?.executades} color="info" />,
            },
            {
                // Percentatge d'elements tractats
                field: 'finalitzades',
                headerName: t('page.user.action.massives.grid.progres'),
                flex: 1,
                minWidth: 160,
                sortable: false,
                renderCell: (params: any) => {
                    const percentatge = percentatgeExecucio(params?.row);
                    return (
                        <TonalLinearProgress
                            value={percentatge}
                            color={(params?.row?.errors ?? 0) > 0 ? 'warning' : 'success'}
                            boxSx={{ my: 'auto' }}
                            sx={{ height: 22 }}
                        >
                            {`${percentatge}%`}
                        </TonalLinearProgress>
                    );
                },
            },
            {
                field: 'errors',
                type: 'number',
                align: 'center',
                headerAlign: 'center',
                flex: 0.4,
                minWidth: 80,
                sortable: false,
                // Vermell només si n'hi ha; amb 0 queda un xip neutre discret
                renderCell: (params: any) => (
                    <XipRecompte valor={params?.row?.errors} color={(params?.row?.errors ?? 0) > 0 ? 'error' : undefined} />
                ),
            },
            { field: 'dataInici', flex: 0.8, minWidth: 150, valueFormatter: (value: any) => formatDate(value) },
            { field: 'dataFi', flex: 0.8, minWidth: 150, valueFormatter: (value: any) => formatDate(value) },
            { field: 'createdByFullName', flex: 1, minWidth: 180 },
            { field: 'documentNom', flex: 0.8, minWidth: 140 },
        ],
        [t]
    );

    const textRefresc = t(autoRefresh ? 'page.user.action.massives.grid.desactivaRefresc' : 'page.user.action.massives.grid.activaRefresc');

    return (
        <GridPage>
            <ScreenFrame icon={icons.massiva} title={t('navigate.massiu.execucions')}>
                <StyledMuiGrid
                    readOnly
                    resourceName={RESOURCE_NAME}
                    apiRef={gridApiRef}
                    columns={columns}
                    staticSortModel={sortModel}
                    rowAdditionalActions={actions}
                    onRowClick={(params: any) => mostrarDetall(params?.row?.id)}
                    toolbarElementsWithPositions={[
                        {
                            position: 2,
                            element: (
                                <ToolbarButton
                                    icon={autoRefresh ? icons.refrescaAturada : icons.refrescaAutomatic}
                                    title={textRefresc}
                                    variant="contained"
                                    aria-pressed={autoRefresh}
                                    onClick={() => setAutoRefresh((actiu: boolean) => !actiu)}
                                >
                                    {textRefresc}
                                </ToolbarButton>
                            ),
                        },
                    ]}
                />
            </ScreenFrame>
            {dialogContingut}
        </GridPage>
    );
};

export default ExecucioMassivaGrid;
