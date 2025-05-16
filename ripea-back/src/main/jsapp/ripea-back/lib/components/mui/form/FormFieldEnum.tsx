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
    const [filteredOptions, setFilteredOptions] = React.useState<any>();
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
            const options = field.options;
            const filteredOptions = options != null ? Object.fromEntries(Object.entries(options).filter(([key]) => hiddenEnumValues ? (Array.isArray(hiddenEnumValues) ? !hiddenEnumValues.includes(key) : hiddenEnumValues !== key) : true)) : null;
            setFilteredOptions(filteredOptions);
        } else if (field.dataSource != null) {
            const dataSource = field.dataSource;
            const valueField = dataSource.valueField;
            const labelField = dataSource.labelField;
            const templateData = requestParams;
            const href = dataSource.href + (templateData != null ? '{?' + Object.keys(templateData).join(',') + '}' : '');
            requestHref(href, templateData).then((state) => {
                const options: any = {};
                state.getEmbedded().forEach(e => {
                    options[e.data[valueField]] = e.data[labelField];
                });
                const filteredOptions = options != null ? Object.fromEntries(Object.entries(options).filter(([key]) => hiddenEnumValues ? (Array.isArray(hiddenEnumValues) ? !hiddenEnumValues.includes(key) : hiddenEnumValues !== key) : true)) : null;
                setFilteredOptions(filteredOptions);
            });
        } else {
            setFilteredOptions({});
        }
    }, [field, requestParams]);
    return filteredOptions && <TextField
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
                        const found = Object.entries(filteredOptions).find(([key]) => key === v);
                        return found?.[1];
                    }
                    return multiple ? value?.map((v: any) => selectedText(v)).join(', ') : selectedText(value);
                },
            }
        }}>
        {!required && !multiple && <MenuItem key='' value=''>&nbsp;</MenuItem>}
        {Object.entries(filteredOptions).map(([key]) => {
            const checked = value?.includes?.(key);
            return <MenuItem key={key} value={key}>
                {multiple && <Checkbox checked={!!checked} />}
                <ListItemText primary={filteredOptions[key] as string} />
            </MenuItem>;
        })}
    </TextField>;
}
export default FormFieldEnum;