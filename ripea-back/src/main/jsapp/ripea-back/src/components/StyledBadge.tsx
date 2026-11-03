import { styled } from '@mui/material/styles';
import Badge from '@mui/material/Badge';
import { accentFillColor } from '../theme';

type ThemeColorKey = 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success';

interface StyledBadgeProps {
    badgecolor?: ThemeColorKey | string;
    textcolor?: ThemeColorKey | string;
}

export const StyledBadge = styled(Badge)<StyledBadgeProps>(({ theme, badgecolor = 'primary', textcolor }) => {
    // El badge "primary" és el color de farciment de la identitat (veure accentFillColor), com la resta
    // de badges de l'aplicació, i el text s'hi adapta per tenir contrast (fosc als temes foscos); la
    // resta de colors són els del tema i el text és blanc si no se n'indica un altre.
    const isPrimary = badgecolor === 'primary';
    const resolveColor = (value: string | undefined) =>
        theme.palette[value as ThemeColorKey]?.main || value;
    const background = isPrimary ? accentFillColor(theme) : resolveColor(badgecolor);

    return {
        paddingLeft: theme.spacing(1),
        paddingRight: theme.spacing(1),
        '& .MuiBadge-badge': {
            backgroundColor: background,
            color: textcolor
                ? resolveColor(textcolor)
                : isPrimary
                  ? theme.palette.getContrastText(background as string)
                  : 'white',
        },
    };
});