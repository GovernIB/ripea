import React from 'react';
import FormControl from '@mui/material/FormControl';
import FormHelperText from '@mui/material/FormHelperText';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import { FormFieldCustomProps } from '../../form/FormField';

export const FormFieldCheckbox: React.FC<FormFieldCustomProps> = (props) => {
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
    } = props;
    const helperText = inline ? field?.helperText : fieldError?.message ?? field.helperText;
    const title = field.title ?? (inline ? helperText : undefined);
    return <FormControl error={!!fieldError} sx={!inline ? { top: '7px' } : undefined}>
        <FormControlLabel
            name={name}
            required={required}
            label={!inline ? label : undefined}
            slotProps={{
                typography: {
                    color: fieldError != null ? 'error' : undefined
                },
                ...componentProps?.slotProps,
            }}
            control={<Checkbox
                checked={value ? true : false}
                color={fieldError != null ? 'error' : undefined}
                title={title}
                onChange={!readOnly ? (e) => onChange(e.target.checked) : undefined}
                disableRipple={readOnly}
                disabled={disabled}
                {...componentProps} />} />
        {helperText && <FormHelperText>{helperText}</FormHelperText>}
    </FormControl>;
}
export default FormFieldCheckbox;