import * as React from 'react';
import { BarChart } from '@mui/x-charts/BarChart';
import { useTheme } from '@mui/material/styles';
import {
    generateDataGroups,
    isDataInGroup,
    toXAxisDataGroups
} from '../util/dataGroup';

export type UpdownBarChartProps = {
    dataInici: string;
    agrupacio: string;
    estats?: any;
}

const getEstatsMaxData = (estats: any) => {
    let estatsMaxData = estats?.[estats.length - 1]?.data;
    const estatApps = Object.keys(estats);
    estatApps?.forEach((a: any) => {
        const maxData = estats[a][estats[a].length - 1]?.data;
        if (estatsMaxData == null || maxData > estatsMaxData) {
            estatsMaxData = maxData;
        }
    });
    return estatsMaxData;
}

const UpdownBarChart: React.FC<UpdownBarChartProps> = (props) => {
    const {
        dataInici,
        agrupacio,
        estats
    } = props;
    const theme = useTheme();
    const estatsMaxData = getEstatsMaxData(estats);
    const baseDataGroups = generateDataGroups(dataInici, estatsMaxData, agrupacio);
    const seriesUp = baseDataGroups.map(g => {
        let up = 0;
        const estatApps = Object.keys(estats);
        estatApps?.forEach((a: any) => {
            const estatForGroup = estats[a].find((ea: any) => isDataInGroup(ea.data, g, agrupacio));
            up = up + (estatForGroup?.alwaysUp ? 1 : 0);
        });
        return up;
    });
    const seriesUpDown = baseDataGroups.map(g => {
        let upDown = 0;
        const estatApps = Object.keys(estats);
        estatApps?.forEach((a: any) => {
            const estatForGroup = estats[a].find((ea: any) => isDataInGroup(ea.data, g, agrupacio));
            upDown = upDown + (estatForGroup != null && !estatForGroup?.alwaysUp && !estatForGroup?.alwaysDown ? 1 : 0);
        });
        return upDown;
    });
    const seriesDown = baseDataGroups.map(g => {
        let down = 0;
        const estatApps = Object.keys(estats);
        estatApps?.forEach((a: any) => {
            const estatForGroup = estats[a].find((ea: any) => isDataInGroup(ea.data, g, agrupacio));
            down = down + (estatForGroup?.alwaysDown ? 1 : 0);
        });
        return down;
    });
    const dataGroups = toXAxisDataGroups(baseDataGroups, agrupacio);
    const series = [{
        data: seriesUp,
        label: 'up',
        stack: 'total',
        color: theme.palette.success.main
    }, {
        data: seriesUpDown,
        label: 'up/down',
        stack: 'total',
        color: theme.palette.warning.main
    }, {
        data: seriesDown,
        label: 'down',
        stack: 'total',
        color: theme.palette.error.main
    }];
    return estats != null && <BarChart
        xAxis={[{ scaleType: 'band', data: dataGroups }]}
        series={series} />;
}

export default UpdownBarChart;