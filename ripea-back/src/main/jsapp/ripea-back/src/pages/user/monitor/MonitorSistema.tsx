import {useTranslation} from "react-i18next";
import {Fragment, useEffect, useState} from "react";
import {GridPage, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {ContenidoData, DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import {Grid, Box, Tab, Tabs, Typography} from "@mui/material";
import TonalLinearProgress from "../../../components/TonalLinearProgress.tsx";
import { ScreenFrame } from "../../../components/ScreenFrame.tsx";
import { icons } from "../../../util/icons.ts";

const useSistemAction = () => {
    const {
        isReady: apiIsReady,
        artifactAction: apiAction
    } = useResourceApiService('threadInfoResource');
    const {temporalMessageShow} = useBaseAppContext();
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

// Ocupació en color tonal amb el percentatge escrit: en to suau la barra sola no té prou contrast
// i el nivell (normal, avís, crític) no es pot transmetre només amb el color.
const LinearSpace = ({value, total}: { value:number, total?:number }) => {
    const v = total ?Math.round(value * 100 / total) :(value ?? 0)
    return <TonalLinearProgress
        value={v}
        color={v >= 90 ?'error' :v >= 80 ?'warning' :'success'}
        sx={{height: 18}}
    >
        <Typography variant="caption" fontWeight="inherit">{v}%</Typography>
    </TonalLinearProgress>
}

const Sistema = ({system}:any) => {
    const { t } = useTranslation();

    return <DetailCard>
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
                {system?.disksUsage?.map((disk:any) => <Fragment key={disk.nom}>
                    <Grid size={2}>{disk.nom}</Grid>
                    <Grid size={6}>
                        <LinearSpace value={disk?.usedSpace} total={disk?.totalSpace} />
                    </Grid>
                    <Grid size={1}/>
                    <Grid size={3}>{disk?.formatedFreeSpace} / {disk?.formatedTotalSpace}</Grid>
                </Fragment>)}
            </Grid>
        </DetailCardContent>
    </DetailCard>
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
        autoHeight={false}
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
        autoHeight={false}
        paginationActive={false}
        toolbarShowQuickFilter
        toolbarHideCreate
    />
}

/** Monitor del sistema: pantalla completa (com la resta de pantalles) amb les pestanyes sistema, fils i tasques. */
export const MonitorSistema = () => {
    const { t } = useTranslation();
    const [tab, setTab] = useState<string>('sistema');
    const {system, apiSystem, apiIsReady} = useSistemAction();

    useEffect(() => {
        if (apiIsReady) {
            apiSystem();
        }
    }, [apiIsReady]);

    const tabs = [
        {
            value: "sistema",
            label: t('page.sistema.tabs.sistema'),
            content: <Sistema system={system}/>
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

    return (
        <GridPage>
            <ScreenFrame
                icon={icons.monitorSistema}
                title={t('page.user.menu.monitor')}
                actions={tab === 'sistema'
                    ? <ToolbarButton title={t('common.refresh')} icon={'refresh'} onClick={apiSystem} />
                    : undefined}
            >
                <Box sx={{ flexShrink: 0, borderBottom: 1, borderColor: 'divider' }}>
                    <Tabs value={tab} onChange={(_event, value) => setTab(value)} sx={{ px: 1 }}>
                        {tabs.map(({ value, label }) => <Tab key={value} value={value} label={label} />)}
                    </Tabs>
                </Box>
                {/* Columna flex perquè les graelles (fils, tasques) ocupin l'alçada que sobra i facin scroll per dins */}
                <Box sx={{ flex: '1 1 0', minHeight: 0, display: 'flex', flexDirection: 'column' }}>
                    {tabs.find((item) => item.value === tab)?.content}
                </Box>
            </ScreenFrame>
        </GridPage>
    );
}

export default MonitorSistema;
