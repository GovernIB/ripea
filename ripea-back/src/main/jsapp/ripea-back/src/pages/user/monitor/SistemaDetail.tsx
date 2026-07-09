import {useTranslation} from "react-i18next";
import {useState} from "react";
import {MuiDialog, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import TabComponent from "../../../components/TabComponent.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {ContenidoData, DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import {LinearProgress, Grid, Box} from "@mui/material";
import { usePreBaseAppContext } from "@src/components/PreBaseAppProvider.tsx";

const useSistemAction = () => {
    const {
        isReady: apiIsReady,
        artifactAction: apiAction
    } = useResourceApiService('threadInfoResource');
    const {temporalMessageShow} = usePreBaseAppContext();
    const [system, setSystem] = useState<any>();


    const apiSystem = () => {
        setSystem(undefined)
        apiAction(undefined, {code: 'SYSTEM_INFO'})
            .then((response) => setSystem(response))
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        apiIsReady,
        system,
        apiSystem,
    }
}

const LinearSpace = ({value, total}: { value:number, total?:number }) => {
    const v = total ?Math.round(value * 100 / total) :value
    return <LinearProgress variant="determinate"
                           value={v}
                           color={v >= 90 ?'error' :v >= 80 ?'warning' :'success'}
                           sx={{width: '100%', height: 15, borderRadius: '4px'}}/>
}

const Sistema = ({system, refresh}:any) => {
    const { t } = useTranslation();

    return <>
        <Box display={'flex'} justifyContent={'end'} mb={1}>
            <ToolbarButton title={t('common.refresh')} icon={'refresh'} onClick={refresh} color={'primary'}/>
        </Box>
    <DetailCard>
        <DetailCardContent size={4} title={t('page.sistema.detail.sistemaOperatiu')}>{system?.informacioSistema?.sistemaOperatiu}</DetailCardContent>
        <DetailCardContent size={4} title={t('page.sistema.detail.arquitectura')}>{system?.arquitectura}</DetailCardContent>
        <DetailCardContent size={4} title={t('page.sistema.detail.processadors')}>{system?.informacioSistema?.processadors}</DetailCardContent>
        <DetailCardContent size={4} title={t('page.sistema.detail.jbossVersion')}>{system?.jbossVersion}</DetailCardContent>
        <DetailCardContent size={4} title={t('page.sistema.detail.applicationServerInfo')}>{system?.applicationServerInfo}</DetailCardContent>
        <DetailCardContent size={4} title={t('page.sistema.detail.tempsFuncionant')}>{system?.informacioSistema?.tempsFuncionant}</DetailCardContent>

        <DetailCardContent title={t('page.sistema.detail.jvmMemory')}>
            <Grid container display={'flex'} alignItems={'center'}>
                <ContenidoData size={6} titleSize={6} textSize={6} title={'daemonThreadCount'}>{system?.jvmInfo?.daemonThreadCount}</ContenidoData>
                <ContenidoData size={6} titleSize={6} textSize={6} title={'gcCount'}>{system?.jvmInfo?.gcCount}</ContenidoData>
                <ContenidoData size={6} titleSize={6} textSize={6} title={'gcTime'}>{system?.jvmInfo?.gcTime}</ContenidoData>
                <ContenidoData size={6} titleSize={6} textSize={6} title={'peakThreadCount'}>{system?.jvmInfo?.peakThreadCount}</ContenidoData>
                <ContenidoData size={6} titleSize={6} textSize={6} title={'threadCount'}>{system?.jvmInfo?.threadCount}</ContenidoData>
                {/*<ContenidoData size={6} titleSize={6} textSize={6} title={''}>{}</ContenidoData>*/}
                <Grid size={2}/>
                <Grid size={6}>
                    <LinearSpace value={system?.jvmMemory?.usedMemory} total={system?.jvmMemory?.totalMemory} />
                </Grid>
                <Grid size={1}/>
                <Grid size={3}>{system?.jvmMemory?.formatedFreeMemory} / {system?.jvmMemory?.formatedTotalMemory}</Grid>
            </Grid>
        </DetailCardContent>

        <DetailCardContent title={t('page.sistema.detail.disksUsage')}>
            <Grid container display={'flex'} alignItems={'center'}>
                <ContenidoData size={6} titleSize={6} textSize={6} title={'formatedLoadAverage'}>{system?.cpuUsage?.formatedLoadAverage}</ContenidoData>
                <ContenidoData size={6} titleSize={6} textSize={6} title={'loadAverage'}>{system?.cpuUsage?.loadAverage}</ContenidoData>
                <ContenidoData size={6} titleSize={6} textSize={6} title={'validProcessCpuLoad'}>{system?.cpuUsage?.validProcessCpuLoad ?"true" :"false"}</ContenidoData>
                <ContenidoData size={6} titleSize={6} textSize={6} title={'validSystemCpuLoad'}>{system?.cpuUsage?.validSystemCpuLoad ?"true" :"false"}</ContenidoData>
                {system?.disksUsage?.map((disk:any) => <>
                    <Grid size={2}>{disk.nom}</Grid>
                    <Grid size={6}>
                        <LinearSpace value={disk?.usedSpace} total={disk?.totalSpace} />
                    </Grid>
                    <Grid size={1}/>
                    <Grid size={3}>{disk?.formatedFreeSpace} / {disk?.formatedTotalSpace}</Grid>
                </>)}
            </Grid>
        </DetailCardContent>
    </DetailCard></>
}

const columnsFils = [
    {
        field: 'threadName',
        flex: 5,
    },
    {
        field: 'tiempoCPU',
        flex: 1,
    },
    {
        field: 'threadState',
        flex: 1,
    },
    {
        field: 'waitedTime',
        flex: 1,
    },
    {
        field: 'blockedTime',
        flex: 1,
    },
]
const perspectivesFils:any[] = [];
const sortModelFils:any[] = [{field: 'threadId', sort: 'asc'}];
const Fils = () => {
    return <StyledMuiGrid
        resourceName={"threadInfoResource"}
        columns={columnsFils}
        staticSortModel={sortModelFils}
        perspectives={perspectivesFils}
        autoHeight
        toolbarShowQuickFilter
        paginationActive={false}
        readOnly
    />
}

const useTaskAction = (refresh?: () => void) => {
    const {t} = useTranslation()
    const {
        isReady: apiIsReady,
        artifactAction: apiAction
    } = useResourceApiService('backGroundTaskResource');
    const {temporalMessageShow} = useBaseAppContext();

    const restart = (id:any) => {
        restartAll([id], false)
    }
    const restartAll = (ids:any[], massivo:boolean = true ) => {
        apiAction(undefined, {code: 'RESTART_TASK', data: {ids}})
            .then(() => {
                refresh?.()
                temporalMessageShow(null,
                    massivo
                        ? t('page.sistema.action.restartAll.ok')
                        : t('page.sistema.action.restart.ok')
                    , 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        apiIsReady,
        restart,
        restartAll,
    }
}

const columnsTasques = [
    {
        field: 'nom',
        flex: 2,
    },
    {
        field: 'estat',
        flex: 1,
    },
    {
        field: 'dataInici',
        flex: 1,
    },
    {
        field: 'tempsExecucio',
        flex: 1,
    },
    {
        field: 'properaExecucio',
        flex: 1,
    },
]
const perspectivesTasques:any[] = [];
const sortModelTasques:any[] = [{field: 'id', sort: 'asc'}];
const Tasques = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const { restart, restartAll } = useTaskAction(refresh);
    const actions = [
        {
            label: t('page.sistema.action.restart.label'),
            icon: 'cached',
            showInMenu: false,
            onClick: restart,
        }
    ]
    const actionsMassive = [
        {
            title: t('page.sistema.action.restartAll.label'),
            icon: 'cached',
            showInMenu: false,
            onClick: restartAll,
        }
    ]

    return <StyledMuiGrid
        apiRef={apiRef}
        resourceName={"backGroundTaskResource"}
        columns={columnsTasques}
        staticSortModel={sortModelTasques}
        perspectives={perspectivesTasques}
        rowAdditionalActions={actions}
        toolbarMassiveActions={actionsMassive}
        autoHeight
        paginationActive={false}
        toolbarShowQuickFilter
        toolbarHideCreate
    />
}

export const useSistemaDetail = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const {system, apiSystem, apiIsReady} = useSistemAction();

    const handleOpen = () => {
        if(!apiIsReady) return;
        apiSystem()
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const buttons :any[] = [
        {
            value: 'close',
            text: t('common.close'),
            icon: 'close',
        },
    ]

    const tabs = [
        {
            value: "sistema",
            label: t('page.sistema.tabs.sistema'),
            content: <Sistema system={system} refresh={apiSystem}/>
        },
        {
            value: "fils",
            label: t('page.sistema.tabs.fils'),
            content: <Fils/>
        },
        {
            value: "tasques",
            label: t('page.sistema.tabs.tasques'),
            content: <Tasques/>
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.user.menu.monitor')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={buttons}
            buttonCallback={(value) => {
                if (value === 'close') {
                    handleClose();
                }
            }}
        >
            <TabComponent tabs={tabs} />
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}