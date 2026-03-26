import {Box, Card, CardContent, CardHeader, Grid2 as Grid, Typography, Icon, IconButton} from "@mui/material";
import React from "react";

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

const CardHead = (props:any) => {
    const {icon, children, componentProps, ...other} = props;
    return <CardHeader title={<Box display={'flex'} alignItems={'center'} {...componentProps}>
        {icon && <Icon>{icon}</Icon>}{children}
    </Box>} {...other}/>
}

export const CardButton = (props:any) => {
    const {text, icon, onClick, flex = 12, buttonProps, hidden} = props;

    if (hidden){
        return <></>
    }

    return <Grid size={flex} display={'flex'} justifyContent={'end'}>
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

export const DetailCard = (props:DetailCardProps) => {
    const {icon, title, header, children, size = 12, hidden, cardProps = {}, headerProps = {}, variant = "overline", ...other} = props;

    if (hidden){
        return <></>
    }

    return <Grid size={size}>
        <Card sx={cardProps}>
            {(title || header) &&
                <CardHead icon={icon} className={'detail'}
                          sx={{ py: 0, px: 2, ...headerProps }}
                >
                    {title && <Typography mt={0.5} variant={variant}>{title}</Typography>}
                    {header}
                </CardHead>
            }

            <CardContent sx={{ p: '0 !important' }}>
                <Grid container {...other}>
                    {children}
                </Grid>
            </CardContent>
        </Card>
    </Grid>
}

export const DetailCardContent = (props:DetailCardContentProps) => {
    const {title, children, isObject, size = 12, titleSize = 12, textSize = 12, componentTitleProps, componentTextProps, hidden, ...other} = props;

    if (hidden){
        return <></>
    }

    return <Grid size={size} container direction={"row"}
                  {...other}
                  sx={{
                      p: 1,
                      borderLeft: "1px solid",
                      borderTop: "1px solid",
                      ...(other?.sx ?? {}),
                      borderColor: other?.sx?.borderColor || "divider",
                  }}>
        <Grid size={titleSize}><Typography variant={"body1"} color={'primary'} sx={componentTitleProps}>{title}</Typography></Grid>
        <Grid size={textSize}>
            {isEmpty(children) ? (
                " - "
            ) : isObject ? (
                children
            ) : (
                <Typography variant="inherit" color="textSecondary" sx={componentTextProps}>
                    {children}
                </Typography>
            )}
        </Grid>
    </Grid>
}

export const CardData = (props:CardDataProps) => {
    const {icon, title, header, children, size = 12, hidden, hiddenIfEmpty, buttons, cardProps, headerProps, variant = "h4", ...other} = props;

    if (hidden || (hiddenIfEmpty && isEmpty(children))){
        return <></>
    }

    return <Grid size={size}>
        <Card sx={cardProps}>
            {(title || header) &&
                <CardHead icon={icon} sx={headerProps}>
                    {title && <Typography mt={0.5} variant={variant}>{title}</Typography>}
                    {header}
                </CardHead>
            }

            <CardContent hidden={!children}>
                <Grid container columnSpacing={1} rowSpacing={1} {...other}>
                    {children}
                    {buttons?.map((button:any) => <CardButton key={button?.text} {...button}/>)}
                </Grid>
            </CardContent>
        </Card>
    </Grid>
}

export const CardPage = (props:CardPageProps) => {
    const {icon, title, header, headerProps, children, ...other} = props;
    return <Card sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column'
    }}>
        {(title || header) &&
            <CardHead icon={icon} sx={headerProps} {...other}>
                {title && <Typography mt={0.5} variant={"h4"}>{title}</Typography>}
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

    if (hidden || (hiddenIfEmpty && isEmpty(children))){
        return <></>
    }

    return <Grid container direction={"row"} columnSpacing={1} size={size} {...other}>
        <Grid size={titleSize}><Typography variant={"body1"} color={'black'} sx={componentTitleProps}>{title}</Typography></Grid>
        <Grid size={textSize}><Typography variant={"inherit"} color={'textSecondary'} sx={componentTextProps}>{children}</Typography></Grid>
    </Grid>
}