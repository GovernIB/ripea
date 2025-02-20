import * as React from 'react';
import { useTranslation } from 'react-i18next';
import dayjs from 'dayjs';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select, { SelectChangeEvent } from '@mui/material/Select';
import InputAdornment from '@mui/material/InputAdornment';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import {
    Toolbar,
    useBaseAppContext,
} from 'reactlib';

export type SalutToolbarProps = {
    title: string;
    subtitle?: string;
    state?: React.ReactElement;
    ready: boolean;
    onRefresh: (dataInici: string, dataFi: string, agrupacio: string, execAction?: boolean) => void;
    goBackActive?: boolean;
}

const agrupacioFromMinutes = (intervalMinutes: number) => {
    if (intervalMinutes <= 60) {
        return 'MINUT';
    } else if (intervalMinutes <= 24 * 60) {
        return 'HORA';
    } else if (intervalMinutes <= 24 * 60 * 30 * 2) {
        return 'DIA';
    } else if (intervalMinutes <= 24 * 60 * 30 * 12 * 2) {
        return 'MES';
    } else {
        return 'ANY';
    }
}

const useReportInterval = (intervalMinutes?: number) => {
    if (intervalMinutes != null && intervalMinutes > 0) {
        const dataFi = dayjs().set('second', 59).set('millisecond', 999);
        const dataInici = dataFi.subtract(intervalMinutes - 1, 'm').set('second', 0).set('millisecond', 0);
        const dataIniciFormat = dataInici.format('YYYY-MM-DDTHH:mm:ss');
        const dataFiFormat = dataFi.format('YYYY-MM-DDTHH:mm:ss');
        const agrupacio = agrupacioFromMinutes(intervalMinutes);
        return {
            dataInici: dataIniciFormat,
            dataFi: dataFiFormat,
            agrupacio,
        };
    } else {
        return {
            dataInici: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
            dataFi: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
            agrupacio: 'MINUT',
        };
    }
}

const RefreshTimeoutSelect: React.FC<any> = (props: { onChange: (minutes: number) => void }) => {
    const { onChange } = props;
    const { t } = useTranslation();
    const [duration, setDuration] = React.useState<string>('PT1M');
    const callOnChange = (duration: string) => {
        if (onChange != null) {
            const minutes = dayjs.duration(duration).asMinutes()
            onChange?.(minutes);
        }
    }
    const handleChange = (event: SelectChangeEvent) => {
        const value = event.target.value as string;
        setDuration(value);
        callOnChange(value);
    }
    React.useEffect(() => {
        callOnChange(duration);
    }, []);
    return <FormControl>
        <Select
            labelId="range-select-label"
            id="range-select"
            value={duration}
            size="small"
            onChange={handleChange}
            startAdornment={<InputAdornment position="start"><Icon>update</Icon></InputAdornment>}
            sx={{ mr: 1, width: '10em' }}>
            <MenuItem value={"PT1M"}>{t('page.salut.refreshperiod.PT1M')}</MenuItem>
            <MenuItem value={"PT10M"}>{t('page.salut.refreshperiod.PT10M')}</MenuItem>
            <MenuItem value={"PT30M"}>{t('page.salut.refreshperiod.PT30M')}</MenuItem>
            <MenuItem value={"PT1H"}>{t('page.salut.refreshperiod.PT1H')}</MenuItem>
        </Select>
    </FormControl>;
}

const AppDataRangeSelect: React.FC<any> = (props: { onChange: (minutes: number) => void }) => {
    const { onChange } = props;
    const { t } = useTranslation();
    const [duration, setDuration] = React.useState<string>('PT15M');
    const callOnChange = (duration: string) => {
        if (onChange != null) {
            const minutes = dayjs.duration(duration).asMinutes();
            onChange?.(minutes);
        }
    }
    const handleChange = (event: SelectChangeEvent) => {
        const value = event.target.value as string;
        setDuration(value);
        callOnChange(value);
    }
    React.useEffect(() => {
        callOnChange(duration);
    }, []);
    return <FormControl>
        <Select
            labelId="range-select-label"
            id="range-select"
            value={duration}
            size="small"
            onChange={handleChange}
            startAdornment={<InputAdornment position="start"><Icon>date_range</Icon></InputAdornment>}
            sx={{ mr: 1, width: '20em' }}>
            <MenuItem value={"PT15M"}>{t('page.salut.timerange.PT15M')}</MenuItem>
            <MenuItem value={"PT1H"}>{t('page.salut.timerange.PT1H')}</MenuItem>
            <MenuItem value={"P1D"}>{t('page.salut.timerange.P1D')}</MenuItem>
            <MenuItem value={"P7D"}>{t('page.salut.timerange.P7D')}</MenuItem>
            <MenuItem value={"P1M"}>{t('page.salut.timerange.P1M')}</MenuItem>
        </Select>
    </FormControl>;
}

export const SalutToolbar: React.FC<SalutToolbarProps> = (props) => {
    const {
        title,
        subtitle,
        state,
        ready,
        onRefresh,
        goBackActive,
    } = props;
    const { t } = useTranslation();
    const { goBack } = useBaseAppContext();
    const theme = useTheme();
    const [refreshTimeoutMinutes, setRefreshTimeoutMinutes] = React.useState<number>();
    const [appDataRangeMinutes, setAppDataRangeMinutes] = React.useState<number>();
    const {
        dataInici,
        dataFi,
        agrupacio,
    } = useReportInterval(appDataRangeMinutes);
    const refresh = (execAction?: boolean) => {
        appDataRangeMinutes != null && onRefresh(dataInici, dataFi, agrupacio, execAction);
    }
    React.useEffect(() => {
        // Refresca les dades quan es carrega la pàgina i quan es canvien les dates o l'agrupació
        if (ready) {
            refresh();
        }
    }, [ready, dataInici, dataFi, agrupacio]);
    React.useEffect(() => {
        // Refresca la informació periòdicament
        if (refreshTimeoutMinutes) {
            const timeoutMs = refreshTimeoutMinutes * 60 * 1000;
            const intervalId = setInterval(() => {
                refresh();
            }, timeoutMs);
            return () => {
                clearInterval(intervalId);
            }
        }
    }, [refreshTimeoutMinutes, dataInici, dataFi, agrupacio]);
    const toolbarElementsWithPositions = [{
        position: 2,
        element: <RefreshTimeoutSelect onChange={setRefreshTimeoutMinutes} />
    }, {
        position: 2,
        element: <AppDataRangeSelect onChange={setAppDataRangeMinutes} />
    }, {
        position: 2,
        element: <IconButton onClick={() => refresh(true)} title={t('page.salut.refrescar')}>
            <Icon>refresh</Icon>
        </IconButton>
    }];
    state != null && toolbarElementsWithPositions.unshift({
        position: 1,
        element: state
    });
    subtitle != null && toolbarElementsWithPositions.unshift({
        position: 1,
        element: <Typography
            variant="caption"
            sx={{
                position: 'relative',
                top: '4px',
                color: theme.palette.text.disabled,
                ml: 1,
            }}>
            {subtitle}
        </Typography>
    });
    goBackActive && toolbarElementsWithPositions.unshift({
        position: 0,
        element: <IconButton onClick={() => goBack('/')} sx={{ mr: 1 }}>
            <Icon>arrow_back</Icon>
        </IconButton>
    });
    return <Toolbar
        title={title}
        //subtitle={subtitle}
        elementsWithPositions={toolbarElementsWithPositions}
        upperToolbar />;
}

export default SalutToolbar;