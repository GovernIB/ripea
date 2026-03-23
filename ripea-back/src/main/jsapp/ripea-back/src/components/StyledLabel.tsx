import { Typography, Icon } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { getContrastRatio } from "@mui/material/styles";

export const StyledLabel = (props: any) => {
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
        backgroundColor && theme.palette[backgroundColor as keyof typeof theme.palette]
            ? theme.palette[backgroundColor as keyof typeof theme.palette]?.main
            : backgroundColor;

    const resolvedColor =
        color ??
        (resolvedBg &&
        (getContrastRatio(resolvedBg, "#fff") >= 4.5
            ? "#fff"
            : "#000") || 'inherit');

    if (dashed)
        return (
            <Typography
                variant={"caption"}
                className={"myLabel"}
                sx={{ border: "1px dashed #AAA" }}
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