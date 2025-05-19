import {Button, Grid, Icon} from "@mui/material";
import {FormField, useFormContext} from "reactlib";
import Load from "./Load.tsx";

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

export const GridButtonField = (props:any) => {
    const {name, icon, ...other} = props;
    const {data, apiRef} = useFormContext()

    return <Load value={apiRef} noEffect><GridButton
        onClick={()=>{
            apiRef?.current?.setFieldValue(name, !data?.[name])
        }}
        buttonProps={{
            variant: data?.[name] ?"contained":"outlined"
        }}
        {...other}
    >
        <Icon sx={{m: 0}}>{icon}</Icon>
    </GridButton></Load>
}

const GridFormField = (props:any) => {
    const { xs, hidden, componentProps, ...other} = props;
    return <Grid item xs={xs} hidden={hidden}><FormField {...other} componentProps={{...componentProps, sx: {backgroundColor: 'white'}}}/></Grid>
}
export default GridFormField;