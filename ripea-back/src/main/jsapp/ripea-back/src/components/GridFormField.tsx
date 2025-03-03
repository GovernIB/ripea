import {Grid} from "@mui/material";
import {FormField} from "reactlib";

const GridFormField = (props:any) => {
    const { xs, ...other} = props;
    return <Grid item xs={xs}><FormField {...other} componentProps={{sx: {backgroundColor: 'white'}}}/></Grid>
}
export default GridFormField;