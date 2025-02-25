import React from 'react';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Checkbox from '@mui/material/Checkbox';
import { FormFieldCustomProps } from '../../form/FormField';
import { useFormFieldCommon } from './FormFieldText';

type FormFieldEnumProps = FormFieldCustomProps & {
    multiple?: boolean;
    hiddenEnumValues?: string[];
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
    } = props;
    const [open, setOpen] = React.useState(false);
    const multiple = (field?.multiple || multipleProp) ?? false;
    const options = field.options;
    const filteredOptions = Object.fromEntries(Object.entries(options).filter(([key]) => hiddenEnumValues ? (Array.isArray(hiddenEnumValues) ? !hiddenEnumValues.includes(key) : hiddenEnumValues !== key) : true));
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
    return <TextField
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
                        const found = Object.entries(options).find(([key]) => key === v);
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