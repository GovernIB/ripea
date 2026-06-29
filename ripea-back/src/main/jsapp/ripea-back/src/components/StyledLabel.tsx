import { Typography, Icon, TypographyProps } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { getContrastRatio } from '@mui/material/styles';

type ColorKey = 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success';

interface StyledLabelProps extends TypographyProps {
    title?: string;
    icon?: string;
    backgroundColor?: ColorKey | string;
    color?: string;
    dashed?: boolean;
    children?: React.ReactNode;
}

export const getReadableTextColor = (bg?: string | null): string => {
    if (!bg) return 'inherit';
    try {
        const contrast = getContrastRatio(bg, '#fff');
        if (contrast >= 4.5) return '#fff';
        if (contrast > 0) return '#000';
        return 'inherit';
    } catch {
        return 'inherit';
    }
};

export const StyledLabel = (props: StyledLabelProps) => {
    const { title, icon, backgroundColor, color, sx = {}, dashed, children, ...other } = props;
    const theme = useTheme();

    const resolvedBg = backgroundColor ? (theme.palette[backgroundColor as ColorKey]?.main ?? backgroundColor) : backgroundColor;
    const resolvedColor = color ?? getReadableTextColor(resolvedBg);

    if (dashed)
        return (
            <Typography title={title} variant={'caption'} className={'myLabel'} sx={{ border: '1px dashed #AAA', ...sx }} {...other}>
                {children}
            </Typography>
        );

    return (
        <Typography
            title={title}
            variant={'caption'}
            className={'myLabel'}
            sx={{
                color: resolvedColor,
                backgroundColor: resolvedBg,
                ...sx,
            }}
            {...other}
        >
            {icon && (
                <Icon fontSize={'inherit'} sx={{ mr: children != null ? 1 : 0 }}>
                    {icon}
                </Icon>
            )}
            {children}
        </Typography>
    );
};
