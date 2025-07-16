import React from 'react';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Checkbox from '@mui/material/Checkbox';
import { FormFieldCustomProps } from '../../form/FormField';
import { useResourceApiContext } from '../../ResourceApiContext';
import { useFormFieldCommon } from './FormFieldText';

type FormFieldEnumProps = FormFieldCustomProps & {
    multiple?: boolean;
    hiddenEnumValues?: string[];
    requestParams?: any;
};

type EnumOption = {
    value: string;
    description?: any;
};

export const FormFieldEnum: React.FC<FormFieldEnumProps> = (props) => {
    const {
        name,
        label,
        value,
        field,
        fieldError,
        inline,
        required,
        disabled,
        readOnly,
        onChange,
        componentProps,
        multiple: multipleProp,
        hiddenEnumValues,
        requestParams,
    } = props;
    const { requestHref } = useResourceApiContext();
    const [open, setOpen] = React.useState(false);
    const [enumOptions, setEnumOptions] = React.useState<EnumOption[]>();
    const multiple = (field?.multiple || multipleProp) ?? false;
    const {
        helperText,
        title,
        startAdornment,
    } = useFormFieldCommon(field, fieldError, inline, componentProps);
    const inputProps = {
        readOnly,
        ...componentProps?.slotProps?.input,
        startAdornment,
    };
    const valueMultipleAdapted = multiple ? (value != null ? (Array.isArray(value) ? value : [value]) : []) : (value ?? '');
    React.useEffect(() => {
        if (field.options != null) {
            const optionsObj = field.options;
            hiddenEnumValues?.forEach((v: any) => {
                delete optionsObj[v];
            });
            const enumOptions = Object.entries(optionsObj).map(([value, description]) => ({ value, description }));
            setEnumOptions(enumOptions);
        } else if (field.dataSource != null) {
            const dataSource = field.dataSource;
            const valueField = dataSource.valueField;
            const labelField = dataSource.labelField;
            const templateData = requestParams;
            const href = dataSource.href + (templateData != null ? '{?' + Object.keys(templateData).join(',') + '}' : '');
            requestHref(href, templateData).then((state) => {
                const enumOptions = state.getEmbedded().
                    map((e: any) => ({
                        value: e.data[valueField],
                        description: e.data[labelField]
                    })).
                    filter(o => !hiddenEnumValues?.includes(o.value));
                setEnumOptions(enumOptions);
            });
        } else {
            setEnumOptions([]);
        }
    }, [field, requestParams]);
    return enumOptions && <TextField
        select
        name={name}
        label={!inline ? label : undefined}
        placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
        value={valueMultipleAdapted}
        required={required ?? field.required}
        disabled={disabled}
        error={fieldError != null}
        title={title}
        helperText={helperText}
        onChange={(e) => {
            const value = e.target.value;
            onChange(value !== '' ? value : null);
        }}
        fullWidth
        {...componentProps}
        slotProps={{
            input: inputProps,
            select: {
                multiple,
                open,
                readOnly,
                onClose: () => setOpen(false),
                onOpen: () => setOpen(true),
                renderValue: (value: any) => {
                    const selectedText = (v: any) => {
                        const found = enumOptions.find(o => o.value === v);
                        return found?.description ?? found?.value;
                    }
                    return multiple ? value?.map((v: any) => selectedText(v)).join(', ') : selectedText(value);
                },
            }
        }}>
        {!required && !multiple && <MenuItem key='' value=''>&nbsp;</MenuItem>}
        {enumOptions.map(o => {
            const checked = value?.includes?.(o.value);
            return <MenuItem key={o.value} value={o.value}>
                {multiple && <Checkbox checked={!!checked} />}
                <ListItemText primary={o.description ?? o.value} />
            </MenuItem>;
        })}
    </TextField>;
}
export default FormFieldEnum;