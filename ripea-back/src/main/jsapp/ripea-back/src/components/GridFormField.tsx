import React from "react";
import {Box, Button, Grid, Icon, IconButton, useMediaQuery, useTheme} from "@mui/material";
import {FormField, FormFieldProps, useFormContext} from "reactlib";
import {FormFieldDataActionType} from "../../lib/components/form/FormContext";
import Load from "./Load.tsx";
import {useTranslation} from "react-i18next";
import {useUserSession} from "./Session.tsx";
import {useCallback, useEffect, useMemo, useState} from "react";
import InputAdornment from "@mui/material/InputAdornment";

// Renders a base material icon ligature with a smaller secondary icon placed
// adjacent to its bottom-right corner (e.g. zoom_in + expand_more), without
// overlapping the base glyph. Both icons inherit the button's current color.
export const CombinedIcon = (props:any) => {
    const { base, badge, sx, badgeSx } = props;
    return <Box sx={{ display: 'inline-flex', alignItems: 'flex-end', lineHeight: 1, ...sx }}>
        <Icon>{base}</Icon>
        <Icon sx={{ fontSize: '0.85rem', lineHeight: 1, ml: '1px', ...badgeSx }}>{badge}</Icon>
    </Box>
}

export const GridButton = (props:any) => {
    const { title, icon, size, children, hidden, sx, iconOnlyBreakpoint = 'md', ...other} = props;

    const theme = useTheme();
    const iconOnly = useMediaQuery(theme.breakpoints.down(iconOnlyBreakpoint));

    const iconNode = typeof icon === 'string'
        ? <Icon sx={{mr: (!iconOnly && children) ? 0.5 : 0, ...props.iconSx}}>{icon}</Icon>
        : icon;

    return <Grid title={title} size={size} hidden={hidden}>
        <Button
            variant="outlined"
            sx={{ borderRadius: '4px', width: '100%', height: '100%', ...sx }}
            style={{margin: 0}}
            aria-label={(title && (iconOnly || !children)) ? title : undefined}
            {...other}
        >
            {icon && iconNode}
            {!iconOnly && children}
        </Button>
    </Grid>
}

export const GridButtonField = (props:any) => {
    const {name, whitLabel, icon, iconActive, title, titleActive, ...other} = props;
    const {data, apiRef, fields} = useFormContext()

    const active = !!data?.[name]
    const label = fields?.find?.(item => item?.name === name)?.label || ''
    return <Load value={apiRef} noEffect><GridButton
        onClick={()=>{
            apiRef?.current?.setFieldValue?.(name, !active)
        }}
        variant={ active ?"contained":"outlined" }
        title={(active && titleActive) ? titleActive : (title ?? label)}
        icon={(active && iconActive) ? iconActive : icon}
        {...other}
    >
        {whitLabel && label}
    </GridButton></Load>
}

const DIGITS_ALLOWED_KEYS = ['Backspace', 'Delete', 'Tab', 'Escape', 'Enter', 'ArrowLeft', 'ArrowRight', 'Home', 'End'];

type GridFormField = FormFieldProps & {
    size?: any,
    hidden?: boolean,
    digitsOnly?: boolean,
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
        digitsOnly,
        ...other
    } = props;

    const digitsOnlyProps = digitsOnly ? {
        slotProps: { htmlInput: { inputMode: 'numeric' as const, ...componentProps?.slotProps?.htmlInput } },
        onKeyDown: (e: React.KeyboardEvent) => {
            if (/^\d$/.test(e.key) || DIGITS_ALLOWED_KEYS.includes(e.key) || e.ctrlKey || e.metaKey) return;
            e.preventDefault();
        },
        ...componentProps,
    } : componentProps;
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
                className: 'input',
                ...digitsOnlyProps
            }}
            onFieldValueChange={handleFieldValueChange}
            debounce
        />
    </Grid>
}
export default GridFormField;