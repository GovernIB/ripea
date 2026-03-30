import { Typography, Icon, TypographyProps } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { getContrastRatio } from "@mui/material/styles";

interface StyledLabelProps extends TypographyProps {
    title?: string;
    icon?: string;
    backgroundColor?: string;
    color?: string;
    dashed?: boolean;
    children?: React.ReactNode;
}

export const StyledLabel = (props: StyledLabelProps) => {
    const theme = useTheme();

    const {
        title,
        icon,
        backgroundColor,
        color,
        sx = {},
        dashed,
        children,
        ...other
    } = props;

    const resolvedBg =
        backgroundColor && theme?.palette?.[backgroundColor as keyof typeof theme.palette]
            ? theme?.palette?.[backgroundColor as keyof typeof theme.palette]?.main
            : backgroundColor;

    // 2. Calcular el color del texto con validación try-catch
    let calculatedContrastColor = 'inherit';

    if (resolvedBg && typeof resolvedBg === 'string') {
        try {
            const contrast = getContrastRatio(resolvedBg, "#fff");

            // getContrastRatio devuelve -1 si no puede calcular la luminosidad
            if (contrast >= 4.5) {
                calculatedContrastColor = "#fff";
            } else if (contrast > 0) {
                calculatedContrastColor = "#000";
            }
        } catch (error) {
            console.warn(`StyledLabel: El formato del backgroundColor '${resolvedBg}' no es válido.`, error);
            calculatedContrastColor = 'inherit';
        }
    }

    // El color explícito tiene prioridad sobre el calculado
    const resolvedColor = color ?? calculatedContrastColor;

    if (dashed)
        return (
            <Typography
                title={title}
                variant={"caption"}
                className={"myLabel"}
                sx={{ border: "1px dashed #AAA", ...sx }}
                {...other}
            >
                {children}
            </Typography>
        );

    return (
        <Typography
            title={title}
            variant={"caption"}
            className={"myLabel"}
            sx={{
                color: resolvedColor,
                backgroundColor: resolvedBg,
                ...sx,
            }}
            {...other}
        >
            {icon && (
                <Icon fontSize={"inherit"} sx={{ mr: children != null ? 1 : 0 }}>
                    {icon}
                </Icon>
            )}
            {children}
        </Typography>
    );
};