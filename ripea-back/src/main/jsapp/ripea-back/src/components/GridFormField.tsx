import {Button, Grid, Icon} from "@mui/material";
import {FormField, FormFieldProps, useFormContext} from "reactlib";
import Load from "./Load.tsx";
import {useTranslation} from "react-i18next";
import {useUserSession} from "./Session.tsx";

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

type GridFormField = FormFieldProps & {
    xs: number,
    hidden?: boolean,
}

function formatByteCount(bytes:number) {
    if (bytes < 1024) return bytes + ' B';
    const exp = Math.floor(Math.log(bytes) / Math.log(1024));
    const pre = 'KMGTPE'.charAt(exp - 1) + 'B';
    const value = bytes / Math.pow(1024, exp);
    return value.toFixed(2) + ' ' + pre;
}

export const FileFormField = (props:GridFormField) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession()
    const adjuntValidator = (value: any) => {
        const maxSize = user?.sessionScope?.maxUploadFileSize || 0;

        if (value && value.contentLength >= maxSize) {
            return [{
                field: props.name,
                message: t('page.contingut.alert.fileSize', {maxSize: formatByteCount(maxSize)})
            }];
        }
    }
    return <GridFormField {...props} type={"file"} validator={adjuntValidator}/>
}

const GridFormField = (props:GridFormField) => {
    const { xs, hidden, componentProps = {}, ...other} = props;

    return <Grid item xs={xs} hidden={!!hidden}><FormField {...other} componentProps={{...componentProps, sx: {backgroundColor: 'white'}}}/></Grid>
}
export default GridFormField;