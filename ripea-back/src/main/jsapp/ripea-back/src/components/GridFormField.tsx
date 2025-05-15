import {Button, Grid} from "@mui/material";
import {FormField} from "reactlib";

export const GridButton = (props:any) => {
    const { onClick, buttonProps, children, ...other} = props;

    return <Grid item {...other}>
        <Button
            variant="outlined"
            sx={{ borderRadius: '4px', width: '100%', height: '100%'}}
            style={{margin: 0}}
            onClick={onClick}
            {...buttonProps}
        >
            {children}
        </Button>
    </Grid>
}

const GridFormField = (props:any) => {
    const { xs, hidden, componentProps, ...other} = props;
    return <Grid item xs={xs} hidden={hidden}><FormField {...other} componentProps={{...componentProps, sx: {backgroundColor: 'white'}}}/></Grid>
}
export default GridFormField;