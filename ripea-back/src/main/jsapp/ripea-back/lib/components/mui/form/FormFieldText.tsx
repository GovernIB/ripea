import React from 'react';
import TextField from '@mui/material/TextField';
import Icon from '@mui/material/Icon';
import { FormFieldCustomProps } from '../../form/FormField';
import { FormFieldError } from '../../form/FormContext';

export const useFormFieldCommon = (
    field: any,
    fieldError: FormFieldError | undefined,
    inline: boolean | undefined,
    componentProps: any,
    startAdornmentIcons?: React.ReactElement[]) => {
    const helperText = inline ? field?.helperText : fieldError?.message ?? field?.helperText;
    const title = field?.title ?? (inline ? helperText : undefined);
    const inlineErrorIconElement = fieldError && inline ? <Icon
        fontSize="small"
        color="error"
        title={fieldError.message}
        sx={{ mr: 1 }}>warning</Icon> : null;
    const startAdornment = inlineErrorIconElement || startAdornmentIcons?.length ? <>
        {inlineErrorIconElement}
        {...(startAdornmentIcons ?? [])}
        {componentProps?.slotProps?.input?.startAdornment}
    </> : componentProps?.slotProps?.input?.startAdornment;
    return {
        helperText,
        title,
        startAdornment,
    };
}

export const FormFieldText: React.FC<FormFieldCustomProps> = (props) => {
    const {
        name,
        label,
        value,
        type,
        field,
        fieldError,
        inline,
        required,
        disabled,
        readOnly,
        onChange,
        componentProps,
    } = props;
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
    const htmlInputProps = {
        maxLength: field?.maxLength,
        ...componentProps?.slotProps?.htmlInput,
    };
    const isTextAreaType = type === 'textarea' || field?.type === 'textarea';
    return <TextField
        name={name}
        label={!inline ? label : undefined}
        placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
        value={value ?? ''}
        required={required ?? field?.required}
        disabled={disabled}
        error={fieldError != null}
        title={title}
        helperText={helperText}
        onChange={(e) => onChange(e.target.value === '' ? null : e.target.value)}
        fullWidth
        multiline={isTextAreaType}
        rows={isTextAreaType ? 4 : undefined}
        {...componentProps}
        slotProps={{
            input: inputProps,
            htmlInput: htmlInputProps,
        }} />;
}
export default FormFieldText;