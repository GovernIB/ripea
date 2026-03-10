import {Box, Card, CardContent, CardHeader, Grid, Grid2, Typography} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import Icon from "@mui/material/Icon";

const cardBorder= { borderRadius: '4px' };
const cardHeader= { py: 1, px: 2 };
const iconButton = { p: 0.5, borderRadius: '5px', maxWidth: 'max-content', border: '1px solid grey' }

const CardHead = (props:any) => {
    const {icon, children, componentProps, ...other} = props;
    return <CardHeader title={<Box display={'flex'} alignItems={'center'} {...componentProps}>
        {icon && <Icon>{icon}</Icon>}{children}
    </Box>} {...other}/>
}

export const CardButton = (props:any) => {
    const {text, icon, onClick, flex, buttonProps, hidden} = props;

    if (hidden){
        return <></>
    }

    return <Grid item xs={flex ?? 12} display={'flex'} justifyContent={'end'}>
        <IconButton sx={{...iconButton, ...buttonProps}} title={text} onClick={onClick}>
            <Typography sx={{display: 'flex', alignItems: 'center'}} variant={'caption'} color={'textPrimary'}>
                {icon && <Icon fontSize={'inherit'}>{icon}</Icon>}
                {text}
            </Typography>
        </IconButton>
    </Grid>
}

const isEmpty = (value:any) => {
    return !value || value?.length === 0 || value?.trim?.() === '' || value?.every?.((item:any) => isEmpty(item))
}

export const DetailCard = (props:any) => {
    const {icon, title, header, children, size = 12, hidden, cardProps = {}, headerProps = {}, variant = "overline", ...other} = props;

    if (hidden){
        return <></>
    }

    return <Grid2 size={size}>
        <Card sx={{...cardBorder, ...cardProps}}>
            {(title || header) &&
                <CardHead icon={icon} className={'detail'}
                          sx={{ py: 0, px: 2, ...headerProps }}
                >
                    {title && <Typography mt={0.5} variant={variant}>{title}</Typography>}
                    {header}
                </CardHead>
            }

            <CardContent sx={{ p: '0 !important' }}>
                <Grid2 container {...other}>
                    {children}
                </Grid2>
            </CardContent>
        </Card>
    </Grid2>
}

export const DetailCardContent = (props:any) => {
    const {title, children, size = 12, titleSize = 12, textSize = 12, componentTitleProps, componentTextProps, hidden, ...other} = props;

    if (hidden){
        return <></>
    }

    return <Grid2 item size={size} container direction={"row"}
                  {...other}
                  sx={{
                      p: 1,
                      borderLeft: "1px solid",
                      borderTop: "1px solid",
                      ...(other?.sx ?? {}),
                      borderColor: other?.sx?.borderColor || "divider",
                  }}>
        <Grid2 size={titleSize}><Typography variant={"body1"} color={'lightskyblue'} sx={componentTitleProps}>{title}</Typography></Grid2>
        <Grid2 size={textSize}><Typography variant={"inherit"} color={'textSecondary'} sx={componentTextProps}>
            {isEmpty(children) ?" - " :children}
        </Typography></Grid2>
    </Grid2>
}

export const CardData = (props:any) => {
    const {icon, title, header, children, xs, hidden, hiddenIfEmpty, buttons, cardProps, headerProps = {}, variant = "h4", ...other} = props;

    if (hidden || (hiddenIfEmpty && isEmpty(children))){
        return <></>
    }

    return <Grid item xs={xs ?? 12}>
        <Card sx={{...cardBorder, ...cardProps}}>
            {(title || header) &&
                <CardHead icon={icon} sx={{...cardHeader, ...headerProps }}>
                    {title && <Typography mt={0.5} variant={variant}>{title}</Typography>}
                    {header}
                </CardHead>
            }

            <CardContent hidden={!children}>
                <Grid container columnSpacing={1} rowSpacing={1} item xs={12} {...other}>
                    {children}
                    {buttons?.map((button:any) => <CardButton key={button?.text} {...button}/>)}
                </Grid>
            </CardContent>
        </Card>
    </Grid>
}

export const CardPage = (props:any) => {
    const {icon, title, header, headerProps, children, ...other} = props;
    return <Card sx={{
        ...cardBorder,
        height: '100%',
        display: 'flex',
        flexDirection: 'column'
    }}>
        {(title || header) &&
            <CardHead icon={icon} sx={{...cardHeader, ...headerProps }} {...other}>
                {title && <Typography mt={0.5} variant={"h4"}>{title}</Typography>}
                {header}
            </CardHead>
        }

        <CardContent sx={{height: '100%', display: 'flex', flexDirection: 'column'}}>
            {children}
        </CardContent>
    </Card>
}

export const ContenidoData = (props:any) => {
    const {title, titleXs, children, textXs, xs, componentTitleProps, componentTextProps, hidden, hiddenIfEmpty, ...other} = props;

    if (hidden || (hiddenIfEmpty && isEmpty(children))){
        return <></>
    }

    return <Grid container direction={"row"} columnSpacing={1} item xs={xs ?? 12} {...other}>
        <Grid item xs={titleXs ?? 4}><Typography variant={"body1"} color={'black'} sx={componentTitleProps}>{title}</Typography></Grid>
        <Grid item xs={textXs ?? 8}><Typography variant={"inherit"} color={'textSecondary'} sx={componentTextProps}>{children}</Typography></Grid>
    </Grid>
}