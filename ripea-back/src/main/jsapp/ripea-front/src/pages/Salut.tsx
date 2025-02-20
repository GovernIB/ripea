import * as React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid2';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import { Gauge, gaugeClasses } from '@mui/x-charts/Gauge';
import { useTheme } from '@mui/material/styles';
import {
    BasePage,
    useResourceApiService,
    useBaseAppContext,
    dateFormatLocale,
} from 'reactlib';
import SalutToolbar from '../components/SalutToolbar';
import UpdownBarChart from '../components/UpdownBarChart';

const useAppData = () => {
    const {
        isReady: appApiIsReady,
        find: appApiFind,
        action: appApiAction,
    } = useResourceApiService('app');
    const {
        isReady: salutApiIsReady,
        report: salutApiReport,
    } = useResourceApiService('salut');
    const [loading, setLoading] = React.useState<boolean>();
    const [apps, setApps] = React.useState<any[]>();
    const [estats, setEstats] = React.useState<Record<string, any>>({});
    const [salutLastItems, setSalutLastItems] = React.useState<any[]>();
    const [reportParams, setReportParams] = React.useState<any>();
    const refresh = (dataInici: string, dataFi: string, agrupacio: string, actionExec?: boolean) => {
        const reportParams = {
            dataInici,
            dataFi,
            agrupacio,
        };
        setReportParams(reportParams);
        if (appApiIsReady && salutApiIsReady) {
            setLoading(true);
            new Promise((resolve, reject) => {
                if (actionExec) {
                    appApiAction({ code: 'refresh' }).then(resolve).catch(reject);
                } else {
                    resolve(null);
                }
            }).then(() => {
                return appApiFind({ unpaged: true });
            }).then((response) => {
                setApps(response.rows);
                return salutApiReport({ code: 'salut_last' });
            }).then(salutLastItems => {
                setSalutLastItems(salutLastItems);
                const ps: Promise<any>[] = (salutLastItems as any[])?.map((i: any) => {
                    const reportData = {
                        ...reportParams,
                        appCodi: i.codi
                    };
                    return new Promise((resolve, reject) => {
                        salutApiReport({ code: 'estat', data: reportData }).then(ii => {
                            setEstats(e => ({ ...e, [i.codi]: ii }))
                            resolve(null);
                        }).catch(reject);
                    });
                });
                return Promise.all(ps);
            }).finally(() => setLoading(false));
        }
    }
    return {
        ready: appApiIsReady && salutApiIsReady,
        loading,
        refresh,
        apps,
        salutLastItems,
        estats,
        reportParams,
    };
}

const UpdownGaugeChart: React.FC<any> = (props: { salutLastItems: any[] }) => {
    const { salutLastItems } = props;
    const theme = useTheme();
    const upCount = salutLastItems?.filter(i => i.appUp).length;
    const upPercent = salutLastItems?.length ? (upCount / salutLastItems.length) * 100 : 0;
    return <Gauge
        value={upPercent}
        sx={() => ({
            [`& .${gaugeClasses.valueText}`]: {
                fontSize: 30,
                transform: 'translate(0px, 0px)',
            },
            [`& .${gaugeClasses.valueArc}`]: {
                fill: theme.palette.success.main,
            },
            [`& .${gaugeClasses.referenceArc}`]: {
                fill: theme.palette.error.main,
            },
        })}
        text={({ value }) => `${(value ?? 0) * salutLastItems.length / 100} / ${salutLastItems.length}`} />;
}

const ItemStateChip: React.FC<any> = (props: { up: boolean, date: string }) => {
    const { up, date } = props;
    return <>
        {up ? <Chip label="UP" size="small" color="success" /> : <Chip label="DOWN" size="small" color="error" />}
        <br />
        <Typography variant="caption">{date}</Typography>
    </>;
}

const AppDataTable: React.FC<any> = (props: { apps: any[], salutLastItems: any[] }) => {
    const { apps, salutLastItems } = props;
    const { t } = useTranslation();
    const { getLinkComponent } = useBaseAppContext();
    return <Table sx={{ minWidth: 650 }} aria-label="simple table">
        <TableHead>
            <TableRow>
                <TableCell>{t('page.salut.apps.column.estat')}</TableCell>
                <TableCell>{t('page.salut.apps.column.codi')}</TableCell>
                <TableCell>{t('page.salut.apps.column.nom')}</TableCell>
                <TableCell>{t('page.salut.apps.column.versio')}</TableCell>
                <TableCell>{t('page.salut.apps.column.bd')}</TableCell>
                <TableCell>{t('page.salut.apps.column.latencia')}</TableCell>
                <TableCell>{t('page.salut.apps.column.integ')}</TableCell>
                <TableCell>{t('page.salut.apps.column.subsis')}</TableCell>
                <TableCell>{t('page.salut.apps.column.msgs')}</TableCell>
                <TableCell></TableCell>
            </TableRow>
        </TableHead>
        <TableBody>
            {apps?.map(app => {
                const appUpdownItem = salutLastItems?.find(i => i.codi === app.codi);
                return appUpdownItem != null ? <TableRow
                    key={app.id}
                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                    <TableCell component="th" scope="row">
                        <ItemStateChip up={appUpdownItem?.appUp} date={dateFormatLocale(appUpdownItem.data, true)} />
                    </TableCell>
                    <TableCell component="th" scope="row">{app.codi}</TableCell>
                    <TableCell component="th" scope="row">{app.nom}</TableCell>
                    <TableCell component="th" scope="row">{app.versio}</TableCell>
                    <TableCell component="th" scope="row">
                        <ItemStateChip up={appUpdownItem?.bdUp} date={dateFormatLocale(appUpdownItem.data, true)} />
                    </TableCell>
                    <TableCell component="th" scope="row">
                        {appUpdownItem.appLatencia != null ? appUpdownItem.appLatencia + ' ms' : t('page.salut.nd')}
                    </TableCell>
                    <TableCell component="th" scope="row">
                        <Chip label={appUpdownItem.integracioUpCount} size="small" color="success" />&nbsp;/&nbsp;
                        <Chip label={appUpdownItem.integracioDownCount} size="small" color="error" />
                    </TableCell>
                    <TableCell component="th" scope="row">
                        <Chip label={appUpdownItem.subsistemaUpCount} size="small" color="success" />&nbsp;/&nbsp;
                        <Chip label={appUpdownItem.subsistemaDownCount} size="small" color="error" />
                    </TableCell>
                    <TableCell component="th" scope="row">
                        <Chip label={appUpdownItem.missatgeErrorCount} size="small" color="error" />&nbsp;/&nbsp;
                        <Chip label={appUpdownItem.missatgeWarnCount} size="small" color="warning" />&nbsp;/&nbsp;
                        <Chip label={appUpdownItem.missatgeInfoCount} size="small" color="info" />
                    </TableCell>
                    <TableCell component="th" scope="row">
                        <Button
                            variant="contained"
                            size="small"
                            component={getLinkComponent()}
                            to={'appinfo/' + app.id}>{t('page.salut.apps.detalls')}</Button>
                    </TableCell>
                </TableRow> : null;
            })}
        </TableBody>
    </Table>;
}

const Salut: React.FC = () => {
    const { t } = useTranslation();
    const {
        ready,
        loading,
        refresh: appDataRefresh,
        apps,
        salutLastItems,
        estats,
        reportParams,
    } = useAppData();
    const dataLoaded = ready && loading != null && !loading;
    const toolbar = <SalutToolbar
        title={t('page.salut.title')}
        ready={ready}
        onRefresh={appDataRefresh} />;
    return <BasePage toolbar={toolbar}>
        {loading ?
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    minHeight: 'calc(100vh - 80px)',
                }}>
                <CircularProgress size={100} />
            </Box> : <Grid container spacing={2}>
                <Grid size={3}>
                    {dataLoaded && <UpdownGaugeChart
                        salutLastItems={salutLastItems} />}
                </Grid>
                <Grid size={9} style={{ height: '200px' }}>
                    {dataLoaded && <UpdownBarChart
                        dataInici={reportParams?.dataInici}
                        agrupacio={reportParams?.agrupacio}
                        estats={estats} />}
                </Grid>
                <Grid size={12}>
                    {dataLoaded && <AppDataTable
                        apps={apps}
                        salutLastItems={salutLastItems} />}
                </Grid>
            </Grid>}
    </BasePage>;
}

export default Salut;
