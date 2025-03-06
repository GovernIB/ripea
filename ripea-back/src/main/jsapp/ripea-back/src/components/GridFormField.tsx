import {Grid} from "@mui/material";
import {FormField} from "reactlib";

const GridFormField = (props:any) => {
    const { xs, hidden, componentProps, ...other} = props;
    return <Grid item xs={xs} hidden={hidden}><FormField {...other} componentProps={{...componentProps, sx: {backgroundColor: 'white'}}}/></Grid>
}
export default GridFormField;