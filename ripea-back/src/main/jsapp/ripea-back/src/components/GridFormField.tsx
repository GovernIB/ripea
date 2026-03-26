import {Button, Grid2 as Grid, Icon, IconButton, useMediaQuery, useTheme} from "@mui/material";
import {FormField, FormFieldProps, FormFieldDataActionType, useFormContext} from "reactlib";
import Load from "./Load.tsx";
import {useTranslation} from "react-i18next";
import {useUserSession} from "./Session.tsx";
import {useCallback, useEffect, useMemo, useState} from "react";
import InputAdornment from "@mui/material/InputAdornment";

export const GridButton = (props:any) => {
    const { title, icon, size, children, hidden, sx, ...other} = props;

    const theme = useTheme();
    const iconOnly = useMediaQuery(theme.breakpoints.down('md'));

    return <Grid title={title} size={size} hidden={hidden}>
        <Button
            variant="outlined"
            sx={{ borderRadius: '4px', width: '100%', height: '100%', ...sx }}
            style={{margin: 0}}
            {...other}
        >
            {icon && <Icon sx={{mr: (!iconOnly && children) ? 0.5 : 0, ...props.iconSx}}>{icon}</Icon>}
            {!iconOnly && children}
        </Button>
    </Grid>
}

export const GridButtonField = (props:any) => {
    const {name, whitLabel, ...other} = props;
    const {data, apiRef, fields} = useFormContext()

    const label = fields?.find?.(item => item?.name === name)?.label || ''
    return <Load value={apiRef} noEffect><GridButton
        onClick={()=>{
            apiRef?.current?.setFieldValue?.(name, !data?.[name])
        }}
        variant={ data?.[name] ?"contained":"outlined" }
        title={label}
        {...other}
    >
        {whitLabel && label}
    </GridButton></Load>
}

type GridFormField = FormFieldProps & {
    size?: any,
    hidden?: boolean,
}

export function formatByteCount(bytes:number) {
    if (bytes < 1024) return bytes + ' B';
    const exp = Math.floor(Math.log(bytes) / Math.log(1024));
    const pre = 'KMGTPE'.charAt(exp - 1) + 'B';
    const value = bytes / Math.pow(1024, exp);
    return value.toFixed(2) + ' ' + pre;
}

export const FileFormField = (props:GridFormField) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession()
    const maxSize = useMemo(()=>user?.sessionScope?.maxUploadFileSize || 0,[user?.sessionScope?.maxUploadFileSize]);
    const mssg = useMemo(()=>t('page.contingut.alert.fileSize', {maxSize: formatByteCount(maxSize)}),[t, maxSize]);

    const adjuntValidator = (value: any) => {
        if (value && value.contentLength >= maxSize) {
            return [{
                field: props.name,
                message: mssg
            }];
        }
    }
    return <GridFormField {...props} componentProps={{...(props?.componentProps ?? {}), title: mssg}} type={"file"} validator={adjuntValidator}/>
}

export const PasswordFormField = (props:GridFormField) => {
    const [showPassword, setShowPassword] = useState<boolean>(false);

    return <GridFormField
        {...props}
        componentProps={{
            ...(props?.componentProps),
            type: showPassword ?'text' :'password',
            slotProps: { input: {
                endAdornment: (
                    <InputAdornment position="end">
                        <IconButton
                            onClick={() => setShowPassword((prev:boolean) => !prev)}
                            edge="end"
                        >
                            {showPassword ? <Icon>visibility_off</Icon> : <Icon>visibility</Icon>}
                        </IconButton>
                    </InputAdornment>)
            } },
        }}
    />
}

const GridFormField = (props:GridFormField) => {
    const {
        name,
        size = 12,
        hidden,
        componentProps = {},
        disabled,
        onChange,
        validator,
        ...other
    } = props;
    const {fields, dataDispatchAction, validationSetFieldErrors} = useFormContext()

    const [field, setField] = useState<any>();
    useEffect(() => {
        if (fields) {
            const field = fields.find(f => f.name === name);
            setField(field ?? null);
        }
    }, [fields, name]);
    const handleFieldValueChange = useCallback((value: any) => {
        const errors = validator?.(value) ?? undefined;
        validationSetFieldErrors(name, errors);
        if (errors === undefined) {
            onChange?.(value)
            dataDispatchAction({
                type: FormFieldDataActionType.FIELD_CHANGE,
                payload: { fieldName: name, field, value }
            });
        }
    }, [dataDispatchAction, field, name]);

    return <Grid size={size} hidden={!!hidden}>
        <FormField
            name={name}
            disabled={disabled}
            readOnly={disabled}
            {...other}
            componentProps={{
                sx: {color: 'black', backgroundColor: 'white'},
                ...componentProps
            }}
            onFieldValueChange={handleFieldValueChange}
            debounce
        />
    </Grid>
}
export default GridFormField;