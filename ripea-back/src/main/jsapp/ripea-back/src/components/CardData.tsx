import {Box, Card, CardContent, CardHeader, Grid, Typography, Icon, IconButton, Collapse, darken} from "@mui/material";
import React, {useState} from "react";

const iconButton = { p: 0.5, borderRadius: '5px', maxWidth: 'max-content', border: '1px solid grey' }

type CardPageProps = {
    icon?: string;
    title?: string;
    header?: React.ReactNode;
    children: React.ReactNode;
    headerProps?: any;
    [key: string]: any;
}
type DetailCardProps = CardPageProps & {
    size?: any;
    cardProps?: any;
    variant?: any;
    buttons?: any[];
    hidden?: boolean;
}
type CardDataProps = DetailCardProps & {
    hiddenIfEmpty?: boolean;
}
type ContenidoDataProps = {
    title?: string | any;
    children: React.ReactNode | any;
    size?: any;
    titleSize?: any;
    textSize?: any;
    componentTitleProps?: any;
    componentTextProps?: any;
    hidden?: boolean;
    hiddenIfEmpty?: boolean;
}
type DetailCardContentProps = ContenidoDataProps & {
    sx?: any;
    isObject?:boolean;
}

const CardHead = (props: any) => {
    const { icon, children, componentProps, action, ...other } = props;
    return (
        <CardHeader
            title={
                <Box display={'flex'} alignItems={'center'} {...componentProps}>
                    {children}
                </Box>
            }
            avatar={icon ? <Icon sx={{fontSize: '20px'}}>{icon}</Icon> : undefined}
            slotProps={{
                avatar: { sx: { mr: 1 } },
                action: { sx: { alignSelf: 'center', margin: 0, display: 'flex', alignItems: 'center' } },
            }}
            action={action}
            {...other}
        />
    );
};

export const CardButton = (props: any) => {
    const { text, icon, onClick, flex = 12, buttonProps, hidden } = props;

    if (hidden) {
        return <></>;
    }

    return (
        <Grid size={flex} display={'flex'} justifyContent={'end'}>
            <IconButton sx={{ ...iconButton, ...buttonProps }} title={text} onClick={onClick}>
                <Typography sx={{ display: 'flex', alignItems: 'center' }} variant={'caption'} color={'textPrimary'}>
                    {icon && <Icon fontSize={'inherit'}>{icon}</Icon>}
                    {text}
                </Typography>
            </IconButton>
        </Grid>
    );
};

const isEmpty = (value:any) => {
    return !value || value?.length === 0 || value?.trim?.() === '' || value?.every?.((item:any) => isEmpty(item))
}

export const DetailCard = (props: DetailCardProps) => {
    const { icon, title, header, actionHeader, children, size = 12, hidden, cardProps = {}, headerProps = {}, variant = 'overline', ...other } = props;

    if (hidden) {
        return <></>;
    }

    return (
        <Grid size={size}>
            <Card sx={cardProps}>
                {(title || header) && (
                    <CardHead icon={icon} action={actionHeader} className={'detail'} sx={{ py: 0, px: 2, ...headerProps }}>
                        {title && (
                            <Typography mt={0.5} variant={variant}>
                                {title}
                            </Typography>
                        )}
                        {header}
                    </CardHead>
                )}

                <CardContent sx={{ p: '0 !important' }}>
                    <Grid container {...other} sx={{ ...(other?.sx ?? {}), ...(!(title || header) && { paddingTop: '0 !important' }) }}>
                        {children}
                    </Grid>
                </CardContent>
            </Card>
        </Grid>
    );
};

export const DetailExpandCard = (props: DetailCardProps) => {
    const { icon, title, header, children, size = 12, hidden, cardProps = {}, headerProps = {}, variant = 'overline',
        expanded = false, ...other } = props;
    const [e, setE] = useState(expanded);

    const handleExpandClick = () => {
        setE(!e);
    };

    if (hidden) {
        return <></>;
    }

    return (
        <Grid size={size}>
            <Card sx={cardProps}>
                {(title || header) && (
                    <CardHead icon={icon} className={'detail'} sx={{ py: 0, px: 2, ...headerProps }}
                              componentProps={{width: '100%', display: 'flex', justifyContent: 'space-between'}} onClick={handleExpandClick}>
                        {title && (<>
                            <Typography mt={0.5} variant={variant}>
                                {title}
                            </Typography>

                            <IconButton
                                onClick={handleExpandClick}
                            >
                                <Icon>{e?'expand_more':'expand_less'}</Icon>
                            </IconButton>
                        </>)}
                        {header}
                    </CardHead>
                )}

                <Collapse in={e} timeout="auto" unmountOnExit>
                    <CardContent sx={{ p: '0 !important' }}>
                        <Grid container {...other} sx={{ ...(other?.sx ?? {}), ...(!(title || header) && { paddingTop: '0 !important' }) }}>
                            {children}
                        </Grid>
                    </CardContent>
                </Collapse>
            </Card>
        </Grid>
    );
};

export const DetailCardContent = (props:DetailCardContentProps) => {
    const {title, children, isObject, size = 12, titleSize = 12, textSize = 12, componentTitleProps, componentTextProps, hidden, hiddenIfEmpty, ...other} = props;

    if (hidden || (hiddenIfEmpty && isEmpty(children))) {
        return <></>;
    }

    return (
        <Grid
            size={size}
            container
            direction={'row'}
            {...other}
            sx={{
                p: 1,
                borderLeft: '1px solid',
                borderTop: '1px solid',
                borderRight: size === 12 ? '1px solid' : undefined,
                borderColor: other?.sx?.borderColor || 'divider',
                ...(other?.sx ?? {}),
            }}
        >
            <Grid size={titleSize}>
                <Typography
                    variant={'body1'}
                    sx={{ color: (theme) => (theme.palette.mode === 'dark' ? 'primary.light' : 'primary.main'), ...componentTitleProps }}
                >
                    {title}
                </Typography>
            </Grid>
            <Grid size={textSize}>
                {isEmpty(children) ? (
                    ' - '
                ) : isObject ? (
                    children
                ) : (
                    <Typography variant="inherit" color="textSecondary" sx={componentTextProps}>
                        {children}
                    </Typography>
                )}
            </Grid>
        </Grid>
    );
}

export const CardData = (props:CardDataProps) => {
    const {icon, title, header, children, size = 12, hidden, hiddenIfEmpty, buttons, cardProps, headerProps, variant = "h6", ...other} = props;

    if (hidden || (hiddenIfEmpty && isEmpty(children))){
        return <></>
    }

    return (
        <Grid size={size}>
            <Card sx={cardProps}>
                {(title || header) && (
                    <CardHead icon={icon} sx={headerProps}>
                        {title && <Typography variant={variant}>{title}</Typography>}
                        {header}
                    </CardHead>
                )}

                <CardContent hidden={!children}>
                    <Grid container columnSpacing={1} rowSpacing={1} {...other}>
                        {children}
                        {buttons?.map((button: any) => (
                            <CardButton key={button?.text} {...button} />
                        ))}
                    </Grid>
                </CardContent>
            </Card>
        </Grid>
    );
}

export const CardPage = (props:CardPageProps) => {
    const {icon, title, header, headerProps, children, ...other} = props;
    return <Card sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column'
    }}>
        {(title || header) &&
            <CardHead icon={icon} sx={(theme: any) => ({
                // Banner de cabecera de pantalla (solo afecta a CardPage, que lleva el h1)
                // bgcolor: theme.palette.primary.main,
                borderBottom: 'none',
                '& h1': { color: theme.palette.primary.contrastText, fontWeight: 600 },
                backgroundColor: theme.palette.mode === 'dark' ? darken(theme.palette.primary.main, 0.3) : theme.palette.primary.main,
                ...headerProps,
            })} {...other}>
                {title && <Typography mt={0.5} variant={"h4"} component={"h1"}>{title}</Typography>}
                {header}
            </CardHead>
        }

        <CardContent sx={{height: '100%', display: 'flex', flexDirection: 'column'}}>
            {children}
        </CardContent>
    </Card>
}

export const ContenidoData = (props:ContenidoDataProps) => {
    const {title, children, size = 12, titleSize = 4, textSize = 8, componentTitleProps, componentTextProps, hidden, hiddenIfEmpty, ...other} = props;

    if (hidden || (hiddenIfEmpty && isEmpty(children))) {
        return <></>;
    }

    return (
        <Grid container direction={'row'} columnSpacing={1} size={size} {...other}>
            <Grid size={titleSize}>
                <Typography variant={'body1'} color={'primary.contrastText'} sx={componentTitleProps}>
                    {title}
                </Typography>
            </Grid>
            <Grid size={textSize}>
                <Typography variant={'inherit'} color={'textSecondary'} sx={componentTextProps}>
                    {children}
                </Typography>
            </Grid>
        </Grid>
    );
}
