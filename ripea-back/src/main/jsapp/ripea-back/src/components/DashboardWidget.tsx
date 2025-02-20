import React from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Icon from '@mui/material/Icon';
import Skeleton from '@mui/material/Skeleton';
import { useTheme } from '@mui/material/styles';
import {
    numberFormat,
    useBaseAppContext,
} from 'reactlib';


type DashboardWidgetProps = {
    resourceName: string;
    to?: string;
    icon?: string;
    color?: string;
};

type DashboardWidgetData = {
    title?: string;
    itemName?: string;
    itemCount?: number;
    extraItemName?: string;
    extraItemCount?: number;
    loading: boolean;
}

const useDashboardWidgetData = (_resourceName: string) => {
    const data: DashboardWidgetData = {
        title: 'Exemple',
        itemName: 'Registres',
        itemCount: 247,
        loading: true,
    };
    return data;
}

export const DashboardWidget: React.FC<DashboardWidgetProps> = (props) => {
    const {
        resourceName,
        to,
        icon,
        color: colorProp
    } = props;
    const theme = useTheme();
    const {
        currentLanguage,
        getLinkComponent
    } = useBaseAppContext();
    const {
        title,
        itemName,
        itemCount,
        extraItemName,
        extraItemCount,
        loading
    } = useDashboardWidgetData(resourceName);
    const defaultColor = theme.palette.primary.main;
    const color = colorProp ?? defaultColor;
    return <Paper
        elevation={2}
        to={to}
        component={to != null ? getLinkComponent() : undefined}
        sx={{
            textDecoration: 'none',
            position: 'relative',
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden',
            borderRadius: '.6rem',
        }}>
        <Box
            sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                pt: 1,
                px: 1,
            }}>
            <Typography sx={{
                letterSpacing: '0.025em',
                fontWeight: '500',
                fontSize: '1.4rem',
                px: 2,
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                width: '100%'
            }}>
                {loading ? <Skeleton /> : title}
            </Typography>
            {/*<IconButton
                // color="secondary"
                aria-label="more"
                size="large"
                onClick={() => to && navigate(to)}>
                <Icon>launch</Icon>
            </IconButton>*/}
        </Box>
        <Box sx={{ mt: 2 }}>
            {loading ? <Skeleton variant="circular"
                sx={{
                    mx: 3,
                    width: '60px',
                    height: '60px',
                }} /> : <Typography sx={{
                    fontSize: '3.2rem',
                    fontWeight: '700',
                    color: color,
                    px: 3
                }}>
                {itemCount && numberFormat(itemCount, {}, currentLanguage)}
            </Typography>}
            <Typography sx={{
                fontWeight: '500',
                color: color,
                px: 3
            }}>
                {loading ? <Skeleton /> : itemName}
            </Typography>
        </Box>
        <Typography sx={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'baseline',
            width: '100%',
            my: 1,
        }}
            color="text.secondary">
            {extraItemName != null && extraItemCount != null && <>
                <Typography
                    component="span"
                    sx={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {extraItemName && extraItemName + ':'}&nbsp;
                </Typography>
                <Typography component="span" sx={{ fontWeight: 'bold' }}>
                    {extraItemCount && numberFormat(extraItemCount, {}, currentLanguage)}
                </Typography>
            </>}
        </Typography>
        {!loading && <Box sx={{
            position: 'absolute',
            top: '50%',
            left: '80%',
        }}>
            <Icon sx={{
                opacity: 0.25,
                color: color,
                fontSize: '8rem',
            }}>
                {icon}
            </Icon>
        </Box>}
    </Paper>/* : <Paper
        elevation={1}
        className="relative flex flex-col flex-auto shadow rounded-2xl overflow-hidden"
        sx={{
            p: 2,
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '10em'
        }}>
        <CircularProgress sx={{ color: color }} />
    </Paper>*/;
}