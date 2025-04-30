import {Card, CardContent, Grid, Typography} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import Icon from "@mui/material/Icon";

const cardBorder= { border: '1px solid #e3e3e3', borderRadius: '4px' };
const cardHeader= { backgroundColor: '#f5f5f5', borderBottom: '1px solid #e3e3e3' };
const iconButton = { p: 0.5, borderRadius: '5px', maxWidth: 'max-content', border: '1px solid grey' }

const CardButton = (props:any) => {
    const {text, icon, onClick, flex, buttonProps, hidden} = props;

    if (hidden){
        return <></>
    }

    return <Grid item xs={flex ?? 12} display={'flex'} justifyContent={'end'}>
        <IconButton sx={{...iconButton, ...buttonProps}} title={text} onClick={onClick}>
            <Typography sx={{display: 'flex', alignItems: 'center'}} variant={'caption'} color={'textPrimary'}>
                <Icon fontSize={'inherit'} hidden={!icon}>{icon}</Icon>
                {text}
            </Typography>
        </IconButton>
    </Grid>
}

export const CardData = (props:any) => {
    const {title, header, children, xs, hidden, hiddenIfEmpty, buttons, cardProps, headerProps = cardHeader, ...other} = props;

    if (hidden || (hiddenIfEmpty && children == null)){
        return <></>
    }

    return <Grid item xs={xs ?? 12}>
        <Card sx={{...cardBorder, ...cardProps}}>
            <CardContent sx={headerProps} hidden={!title && !header}>
                <Typography variant={"h5"} hidden={!title}>{title}</Typography>
                {header}
            </CardContent>

            <CardContent hidden={!children}>
                <Grid container columnSpacing={1} rowSpacing={1} item xs={12} {...other}>
                    {children}
                    {buttons?.map((button:any) => <CardButton key={button?.text} {...button}/>)}
                </Grid>
            </CardContent>
        </Card>
    </Grid>
}

export const ContenidoData = (props:any) => {
    const {title, titleXs, children, textXs, xs, componentTitleProps, componentTextProps, hidden, hiddenIfEmpty, ...other} = props;

    if (hidden || (hiddenIfEmpty && children == null)){
        return <></>
    }

    return <Grid container direction={"row"} columnSpacing={1} item xs={xs ?? 12} {...other}>
        <Grid item xs={titleXs ?? 4}><Typography variant={"body1"} color={'black'} sx={componentTitleProps}>{title}</Typography></Grid>
        <Grid item xs={textXs ?? 8}><Typography variant={"inherit"} color={'textSecondary'} sx={componentTextProps}>{children}</Typography></Grid>
    </Grid>
}