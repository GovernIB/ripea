import {Button, Grid, Icon} from "@mui/material";
import {FormField, useFormContext} from "reactlib";
import Load from "./Load.tsx";

export const GridButton = (props:any) => {
    const { title, xs, children, hidden, ...other} = props;

    return <Grid item title={title} xs={xs} hidden={hidden}>
        <Button
            variant="outlined"
            sx={{ borderRadius: '4px', width: '100%', height: '100%'}}
            style={{margin: 0}}
            {...other}
        >
            {children}
        </Button>
    </Grid>
}

export const GridButtonField = (props:any) => {
    const {name, icon, ...other} = props;
    const {data, apiRef, fields} = useFormContext()

    return <Load value={apiRef} noEffect><GridButton
        onClick={()=>{
            apiRef?.current?.setFieldValue(name, !data?.[name])
        }}
        variant={ data?.[name] ?"contained":"outlined" }
        title={fields?.find?.(item => item?.name === name)?.label || ''}
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