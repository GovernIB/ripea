import React from 'react';
import FormControl from '@mui/material/FormControl';
import FormHelperText from '@mui/material/FormHelperText';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Switch from '@mui/material/Switch';
import { FormFieldCustomProps } from '../../form/FormField';
import { useFormFieldCommon } from './FormFieldText';

type FormFieldCheckboxProps = FormFieldCustomProps & {
    typeSwitch?: true;
};

export const FormFieldCheckbox: React.FC<FormFieldCheckboxProps> = (props) => {
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
        typeSwitch,
        componentProps,
    } = props;
    const {
        helperText,
        title,
    } = useFormFieldCommon(field, fieldError, inline, componentProps);
    const control = typeSwitch ? <Switch
        checked={value ? true : false}
        color={fieldError != null ? 'error' : undefined}
        title={title}
        onChange={!readOnly ? (e) => onChange(e.target.checked) : undefined}
        disabled={disabled}
        sx={{ ml: 1 }}
        {...componentProps}
    /> : <Checkbox
        checked={value ? true : false}
        color={fieldError != null ? 'error' : undefined}
        title={title}
        onChange={!readOnly ? (e) => onChange(e.target.checked) : undefined}
        disableRipple={readOnly}
        disabled={disabled}
        sx={{ ml: 1 }}
        {...componentProps} />;
    return <FormControl error={!!fieldError} sx={!inline ? { top: '7px', mb: 3.9 } : undefined}>
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
            control={control} />
        {helperText && <FormHelperText>{helperText}</FormHelperText>}
    </FormControl>;
}
export default FormFieldCheckbox;