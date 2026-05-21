import {useTranslation} from "react-i18next";
import {useEffect, useMemo, useRef, useState} from "react";
import {MuiDialog, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {Chip, Checkbox, FormControlLabel, LinearProgress, Box, Icon, Tooltip} from "@mui/material";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import Load from "../../../components/Load.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import {iniciaDescargaBlob} from "../../expedient/details/CommonActions.tsx";

const useActions = () => {
    const { t } = useTranslation()
    const {
        fieldDownload: apiDownload
    } = useResourceApiService('execucioMassivaResource');
    const {temporalMessageShow} = useBaseAppContext();

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
        flex: 0.75,
    },
    {
        field: 'executades',
        flex: 0.5,
        renderCell: (params: any) => {
            const row = params?.row;
            const value = Math.round(100 - row?.pendents * 100 / row?.executades)

            return <>
                <Chip label={row?.executades} size="small"/>
                <StyledLinearProgress
                    color={'success'}
                    value={value}
                    sx={{height: '20px'}}
                >
                    {value}%
                </StyledLinearProgress>
            </>
        }
    },
    {
        field: 'errors',
        flex: 0.25,
        renderCell: (params: any) =>
            <Chip label={params?.row?.errors} size="small" color={params?.row?.errors ? 'error' : 'default'}/>
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
const ExecucioMassivaGrid = () => {
    const {t} = useTranslation();
    const [isRefresh, setRefresh] = useState(false);
    const gridApiRef = useMuiDataGridApiRef();

    const {download} = useActions();
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
        }
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
            paginationModel={{page: 0, pageSize: 5}}
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
                paginationActive
                autoHeight
                paginationModel={{page: 0, pageSize: 5}}
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