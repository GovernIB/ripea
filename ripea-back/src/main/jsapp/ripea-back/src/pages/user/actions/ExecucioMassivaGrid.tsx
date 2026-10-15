import {useTranslation} from "react-i18next";
import {useEffect, useMemo, useRef, useState} from "react";
import {MuiDialog, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {Chip, Checkbox, FormControlLabel, Icon, Tooltip} from "@mui/material";
import {tonal, TonalColor} from "../../../util/tonal.ts";
import TonalLinearProgress from "../../../components/TonalLinearProgress.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import Load from "../../../components/Load.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";

const useActions = () => {
    const { t } = useTranslation()
    const {
        fieldDownload: apiDownload,
        artifactAction: apiAction,
    } = useResourceApiService('execucioMassivaResource');
    const {temporalMessageShow, messageDialogShow} = useBaseAppContext();

    const cancelar = (id: any, refresh?: () => void) => {
        messageDialogShow(
            t('page.user.action.massives.cancelar.title'),
            t('page.user.action.massives.cancelar.confirm'),
            [{
                value: true,
                text: t('common.accepta'),
                componentProps: { variant: 'contained' }
            },
            {
                value: false,
                text: t('common.cancel'),
                componentProps: { variant: 'outlined' }
            }],
            {maxWidth: 'sm', fullWidth: true})
            .then((value: any) => {
                if (value) {
                    apiAction(id, {code: 'CANCELAR'})
                        .then((result: any) => {
                            refresh?.();
                            // CANCELADA: no havia començat i ja està tancada; EN_CURS: s'aturarà en acabar l'element actual
                            temporalMessageShow(null, result === 'CANCELADA'
                                ? t('page.user.action.massives.cancelar.ok')
                                : t('page.user.action.massives.cancelar.sollicitadaOk'), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
    }

    const download = (id: any) => {
        apiDownload(id, {fieldName: 'documentNom'})
            .then((result) => {
                iniciaDescargaBlob(result);
                temporalMessageShow(null, t('page.user.action.massives.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        download,
        cancelar,
    }
}

const estatChipColor: Record<string, TonalColor> = {
    'EN_CURS': 'info',
    'FINALITZADA': 'success',
}

// Estat del procés. La cancel·lació és al menú d'accions de la fila.
const EstatCell = (props: any) => {
    const {row, formattedValue} = props;
    const {t} = useTranslation();

    const cancelacioSolicitada = !row?.dataFi && !!row?.cancelacioData;
    const color = cancelacioSolicitada ? 'warning' : estatChipColor[row?.estat];

    // Etiqueta curta perquè la columna no s'hagi d'eixamplar per un estat poc freqüent; el tooltip ho explica
    return <Tooltip title={cancelacioSolicitada ? t('page.user.action.massives.cancelar.sollicitadaOk') : ''}>
        <Chip
            size="small"
            label={cancelacioSolicitada ? t('page.user.action.massives.cancelar.sollicitada') : formattedValue}
            variant={row?.estat === 'CANCELADA' ? 'outlined' : 'filled'}
            sx={color ? tonal(color) : undefined}
            tabIndex={cancelacioSolicitada ? 0 : undefined}
        />
    </Tooltip>
}

const columns = [
    {
        field: 'tipus',
        flex: 0.85,
    },
    {
        field: 'estat',
        flex: 0.55,
        sortable: false,
        renderCell: (params: any) => <EstatCell {...params}/>,
    },
    {
        field: 'executades',
        flex: 0.5,
        renderCell: (params: any) => {
            const row = params?.row;
            // Processats = finalitzats o en error. Els cancel·lats no s'han executat: no compten com a fets
            // (1 de 3 executat i 2 cancel·lats = 33%, no 100%)
            const processats = (row?.executades ?? 0) - (row?.pendents ?? 0) - (row?.cancelats ?? 0);
            const value = row?.executades ? Math.round(processats * 100 / row.executades) : 0

            return <>
                <Chip label={row?.executades} size="small"/>
                <TonalLinearProgress
                    color={'success'}
                    boxSx={{ml: 1}}
                    value={value}
                    sx={{height: '20px'}}
                >
                    {value}%
                </TonalLinearProgress>
            </>
        }
    },
    {
        field: 'errors',
        flex: 0.25,
        renderCell: (params: any) =>
            <Chip label={params?.row?.errors} size="small" sx={params?.row?.errors ? tonal('error') : undefined}/>
    },
    {
        field: 'dataInici',
        flex: 0.65,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'dataFi',
        flex: 0.65,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'createdByFullName',
        flex: 0.65,
    },
    {
        field: 'documentNom',
        flex: 0.5,
    },
]
const sortModel: any = [{field: 'createdDate', sort: 'desc'}];
// Constant de mòdul: amb un literal en línia cada render crea un objecte nou, i l'useEffect
// de MuiDataGrid el torna a aplicar retornant la graella a la primera pàgina.
const paginationModel = {page: 0, pageSize: 10};
const ExecucioMassivaGrid = () => {
    const {t} = useTranslation();
    const [isRefresh, setRefresh] = useState(false);
    const gridApiRef = useMuiDataGridApiRef();

    const {download, cancelar} = useActions();
    const {handleOpen: handleContingutOpen, dialog: dialogContingut, refresh} = useExecucioMassivaContingut();

    const actions: any = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleContingutOpen,
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
            icon: 'block',
            showInMenu: true,
            onClick: (id: any) => cancelar(id, () => {
                gridApiRef?.current?.refresh?.();
                refresh?.();
            }),
            hidden: (row: any) => !!row?.dataFi || !row?.potCancelar || !!row?.cancelacioData,
            disabled: (row: any) => !row?.cancelable,
            title: (row: any) => !row?.cancelable ? t('page.user.action.massives.cancelar.noCancelable') : undefined,
        },
    ]

    const intervalRef = useRef<any>(undefined);
    useEffect(() => {
        intervalRef.current = {isRefresh}

        if (isRefresh) {
            const interval = setInterval(() => {
                const {isRefresh: isRefreshRef} = intervalRef.current;
                if (isRefreshRef) {
                    gridApiRef?.current?.refresh?.();
                    refresh?.()
                } else {
                    clearInterval(interval);
                }
            }, 10000); // 10000 milisegundos = 10 segundos
            return () => clearInterval(interval)
        }
    }, [isRefresh]);

    return <>
        <StyledMuiGrid
            resourceName={'execucioMassivaResource'}
            apiRef={gridApiRef}
            staticSortModel={sortModel}
            columns={columns}
            rowAdditionalActions={actions}
            autoHeight
            paginationModel={paginationModel}
            readOnly

            toolbarElementsWithPositions={[
                {
                    position: 0,
                    element: <FormControlLabel
                        control={<Checkbox checked={isRefresh} onClick={() => setRefresh(!isRefresh)}/>}
                        label={t('page.user.massive.refresh')}/>,
                }
            ]}
            rowProps={(row: any) => {
                const color =
                    row?.errors ? 'red'
                        : row?.pendents ? 'orange'
                            : row?.finalitzades ? 'green'
                                : row?.cancelats ? 'grey'
                                    : ''
                return {
                    'box-shadow': `${color} -6px 0px 0px`,
                    'border-left': `6px solid ${color}`
                }
            }}
        />
        {dialogContingut}
    </>
}
const useExecucioMassiva = () => {
    const {t} = useTranslation();
    const [open, setOpen] = useState(false);
    const {value: user} = useUserSession();

    const handleOpen = () => {
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.user.action.massives.title', {name: user?.nom})}
            componentProps={{fullWidth: true, maxWidth: 'xl'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' }
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <Load value={user} noEffect>
                <ExecucioMassivaGrid/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

const StyledEstat = (props:any) => {
    const {entity, children} = props;

    const estat = entity?.estat

    return <>
        {estat == 'ESTAT_FINALITZAT' && <Icon color={"success"}>check_circle</Icon>}
        {estat == 'ESTAT_ERROR' && <Icon title={entity?.error} color={"error"}>error</Icon>}
        {estat == 'ESTAT_PENDENT' && <Icon color={"warning"}>schedule</Icon>}
        {estat == 'ESTAT_CANCELAT' && <Icon color={"disabled"}>check_circle</Icon>}

        {children}
    </>
}

const columnsContingut = [
    {
        field: 'elementTipus',
        flex: 0.5,
    },
    {
        field: 'elementNom',
        flex: 0.75,
		renderCell: (params: any) => (
			<Tooltip title={<span dangerouslySetInnerHTML={{ __html: params.value }} />} arrow>
			  <span
			    dangerouslySetInnerHTML={{ __html: params.value }}
			    style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', display: 'block' }}
			  />
			</Tooltip>
		  ),
    },
    {
        field: 'estat',
        flex: 0.5,
        renderCell: (params: any) => <StyledEstat entity={params.row}>{params.formattedValue}</StyledEstat>,
    },
    // {
    //     field: 'dataInici',
    //     flex: 0.75,
    //     valueFormatter: (value: any) => formatDate(value),
    // },
    {
        field: 'dataFi',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
]
const sortModelContingut: any = [{field: 'dataFi', sort: 'desc'}];
const paginationModelContingut = {page: 0, pageSize: 10};

const estatColor :any = {
    'ESTAT_FINALITZAT': 'green',
    'ESTAT_ERROR': 'red',
    'ESTAT_PENDENT': 'orange',
    'ESTAT_CANCELAT': 'grey',
}

export const useExecucioMassivaContingut = () => {
    const {t} = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const gridApiRef = useMuiDataGridApiRef();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: fields,
    } = useResourceApiService('execucioMassivaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const intervalRef = useRef<any>(undefined);

    useEffect(() => {
        intervalRef.current = {open};
    }, [open]);

    const refresh = () => {
        if (intervalRef?.current?.open) {
            gridApiRef?.current?.refresh?.();
        }
    };

    const handleOpen = (id: any) => {
        if(apiIsReady && id){
            apiGetOne(id)
                .then((app) => setEntity(app))
                .catch((error) => {
                    handleClose()
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const tipus = useMemo(() =>
        fields?.find((item: any) => item?.name === "tipus")?.options[entity?.tipus] ?? ''
    , [fields, entity]);

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.user.action.massives.detail', {tipus})}
            componentProps={{fullWidth: true, maxWidth: 'lg'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' }
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <Load value={entity}>
            <StyledMuiGrid
                resourceName={'execucioMassivaContingutResource'}
                filter={builder.eq('execucioMassiva.id', `'${entity?.id}'`)}
                apiRef={gridApiRef}
                sortModel={sortModelContingut}
                columns={columnsContingut}
                autoHeight
                paginationModel={paginationModelContingut}
                readOnly

                rowProps={(row: any) => {
                    const color = estatColor?.[row?.estat];
                    return {
                        'box-shadow': `${color} -6px 0px 0px`,
                        'border-left': `6px solid ${color}`
                    }
                }}
            />
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
        refresh
    }
}

export default useExecucioMassiva;