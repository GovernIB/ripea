import {Icon, Typography} from "@mui/material";
import {getContrastRatio} from "@mui/material/styles";

export const StyledLabel = (props: any) => {
    const {
        title,
        icon,
        backgroundColor,
        color = backgroundColor && getContrastRatio(backgroundColor, '#fff') >= 4.5 ? '#fff' : '#000',
        sx = {},
        dashed,
        children,
        ...other
    } = props;

    if (dashed)
        return <Typography variant="caption" className={'myLabel'}
                           sx={{border: '1px dashed #AAA'}}>{children}</Typography>

    return <Typography
        title={title}
        variant={"caption"}
        className={'myLabel'}
        sx={{
            color,
            backgroundColor,
            ...sx,
        }}
        {...other}
    >
        {icon && <Icon fontSize={"inherit"} sx={{mr: children != null ? 1 : 0}}>{icon}</Icon>}
        {children}
    </Typography>
}