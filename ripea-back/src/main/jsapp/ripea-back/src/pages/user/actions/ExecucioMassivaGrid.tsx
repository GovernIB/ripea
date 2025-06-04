import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {MuiDialog, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {Chip, Checkbox, FormControlLabel, LinearProgress, Box, Icon} from "@mui/material";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import Load from "../../../components/Load.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";

const useActions = () => {
    const {
        fieldDownload: apiDownload
    } = useResourceApiService('execucioMassivaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const download = (id: any) => {
        apiDownload(id, {fieldName: 'documentNom'})
            .then((result) => {
                iniciaDescargaBlob(result);
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        download
    }
}

const StyledLinearProgress = (props: any) => {
    const {sx, textColor = "white", children, ...other} = props

    return <Box position="relative" width="100%" sx={{ml: 1}}>
        <LinearProgress variant="determinate" {...other} sx={{width: '100%', borderRadius: '4px', ...sx}}/>
        <Box
            color={textColor}
            position="absolute"
            top={0}
            left={0}
            width="100%"
            height="100%"
            display="flex"
            alignItems="center"
            justifyContent="center"
        >
            {children}
        </Box>
    </Box>
}

const columns = [
    {
        field: 'tipus',
        flex: 0.5,
    },
    {
        field: 'executades',
        flex: 0.75,
        renderCell: (params: any) => {
            const row = params?.row;
            const value = 100 - row?.pendents * 100 / row?.executades

            return <>
                <Chip label={row?.executades} size="small"/>
                <StyledLinearProgress
                    color={'success'}
                    value={value}
                    sx={{height: '15px'}}
                >
                    {value}%
                </StyledLinearProgress>
            </>
        }
    },
    {
        field: 'errors',
        flex: 0.5,
        renderCell: (params: any) => <Chip label={params?.row?.errors} size="small"
                                           color={params?.row?.errors ? 'error' : 'default'}/>
    },
    {
        field: 'dataInici',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'dataFi',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'createdBy',
        flex: 0.5,
    },
    {
        field: 'documentNom',
        flex: 0.5,
    },
]
const sortModel: any = [{field: 'createdDate', sort: 'desc'}];
const useExecucioMassiva = () => {
    const {t} = useTranslation();
    const [open, setOpen] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const {value: user} = useUserSession();

    const gridApiRef = useMuiDataGridApiRef();

    const {download} = useActions();
    const {handleOpen: handleContingutOpen, dialog: dialogContingut, refresh} = useExecucioMassivaContingut();

    const handleOpen = () => {
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const actions: any = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleContingutOpen,
        },
        {
            title: t('common.download'),
            icon: 'download',
            showInMenu: true,
            onClick: download,
            hidden: (row: any) => !row?.documentNom,
        }
    ]

    const intervalRef = useRef<any>();
    useEffect(() => {
        intervalRef.current = {open, isRefresh}

        if (open && isRefresh) {
            const interval = setInterval(() => {
                const {open: openRef, isRefresh: isRefreshRef} = intervalRef.current;
                if (openRef && isRefreshRef) {
                    gridApiRef?.current?.refresh?.();
                    refresh?.()
                } else {
                    clearInterval(interval);
                }
            }, 10000); // 10000 milisegundos = 10 segundos
            return () => clearInterval(interval)
        }
    }, [open, isRefresh]);

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.user.action.masives', {name: user?.nom})}
            componentProps={{fullWidth: true, maxWidth: 'xl'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    icon: 'close'
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <Load value={user} noEffect>
                <StyledMuiGrid
                    resourceName={'execucioMassivaResource'}
                    apiRef={gridApiRef}
                    staticSortModel={sortModel}
                    columns={columns}
                    rowAdditionalActions={actions}
                    paginationActive
                    height={162 + 52 * 4}
                    readOnly

                    toolbarElementsWithPositions={[
                        {
                            position: 0,
                            element: <FormControlLabel
                                control={<Checkbox checked={isRefresh} onClick={() => setRefresh(!isRefresh)}/>}
                                label="10s refresh"/>,
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
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

const StyledEstat = (props:any) => {
    const {entity} = props;

    const estat = entity?.estat

    return <>
        {estat == 'ESTAT_FINALITZAT' && <Icon color={"success"}>check_circle</Icon>}
        {estat == 'ESTAT_ERROR' && <Icon title={entity?.error} color={"error"}>error</Icon>}
        {estat == 'ESTAT_PENDENT' && <Icon color={"warning"}>schedule</Icon>}
        {estat == 'ESTAT_CANCELAT' && <Icon color={"disabled"}>check_circle</Icon>}

        {estat}
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
    },
    {
        field: 'estat',
        flex: 0.5,
        renderCell: (params: any) => <StyledEstat entity={params.row}/>,
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
const sortModelContingut: any = [{field: 'ordre', sort: 'asc'}];

const estatColor :any = {
    'ESTAT_FINALITZAT': 'green',
    'ESTAT_ERROR': 'red',
    'ESTAT_PENDENT': 'orange',
    'ESTAT_CANCELAT': 'grey',
}

const useExecucioMassivaContingut = () => {
    const {t} = useTranslation();
    const [open, setOpen] = useState(false);
    const [entityId, setEntityId] = useState<any>();

    const gridApiRef = useMuiDataGridApiRef();

    const intervalRef = useRef<any>();

    useEffect(() => {
        intervalRef.current = {open};
    }, [open]);

    const refresh = () => {
        if (intervalRef?.current?.open) {
            gridApiRef?.current?.refresh?.();
        }
    };

    const handleOpen = (id: any, row: any) => {
        console.log(id, row)
        setEntityId(id)
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntityId(undefined);
            setOpen(false);
        }
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            componentProps={{fullWidth: true, maxWidth: 'lg'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    icon: 'close'
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <StyledMuiGrid
                resourceName={'execucioMassivaContingutResource'}
                filter={builder.eq('execucioMassiva.id', `'${entityId}'`)}
                apiRef={gridApiRef}
                sortModel={sortModelContingut}
                columns={columnsContingut}
                // paginationActive
                // height={162 + 52 * 4}
                autoHeight
                readOnly

                rowProps={(row: any) => {
                    const color = estatColor?.[row?.estat];
                    return {
                        'box-shadow': `${color} -6px 0px 0px`,
                        'border-left': `6px solid ${color}`
                    }
                }}
            />
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
        refresh
    }
}

export default useExecucioMassiva;