import React from 'react';
import { NumericFormat, NumericFormatProps } from 'react-number-format';
import TextField from '@mui/material/TextField';
import Icon from '@mui/material/Icon';
import { useBaseAppContext } from '../../BaseAppContext';
import { useFormContext } from '../../form/FormContext';
import { FormFieldCustomProps } from '../../form/FormField';

type CustomProps = {
    name: string;
    onChange: (event: { target: { name: string; value: string } }) => void;
};

export const getDecimalSeparator = (locale?: string) => {
    return Intl.NumberFormat(locale === 'es' ? 'ca' : locale).formatToParts(1.1).find((p) => p.type === 'decimal')?.value;
}
export const getThousandSeparator = (locale?: string) => {
    return Intl.NumberFormat(locale === 'es' ? 'ca' : locale).formatToParts(10000.1).find((p) => p.type === 'group')?.value;
}

const NumericFormatCustom = React.forwardRef<NumericFormatProps, CustomProps>((props, ref) => {
    const { onChange, ...other } = props;
    const { currentLanguage } = useBaseAppContext();
    const { fields } = useFormContext();
    const field = fields?.find(f => f.name === other.name);
    const valueIsNumericString = field?.type === 'decimal';
    const decimalSeparator = getDecimalSeparator(currentLanguage);
    const thousandSeparator = getThousandSeparator(currentLanguage);
    const allowNegative = (other as any).min < 0;
    return <NumericFormat
        {...other}
        getInputRef={ref}
        onValueChange={(values: any) => {
            onChange({
                target: {
                    name: props.name,
                    value: values.value,
                },
            });
        }}
        allowNegative={allowNegative}
        valueIsNumericString={valueIsNumericString}
        decimalSeparator={decimalSeparator}
        thousandSeparator={thousandSeparator}
        //prefix="$"
        //suffix="€"
        />;
});

export const FormFieldNumber: React.FC<FormFieldCustomProps> = (props) => {
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
    const inlineErrorIconElement = fieldError && inline ? <Icon fontSize="small" color="error" title={fieldError.message} sx={{ mr: 1 }}>
        warning
    </Icon> : null;
    const startAdornment = inlineErrorIconElement ? <>
        {inlineErrorIconElement}
        {componentProps?.slotProps?.input?.startAdornment}
    </> : componentProps?.slotProps?.input?.startAdornment;
    const inputProps = {
        readOnly,
        ...componentProps?.slotProps?.input,
        startAdornment,
        inputComponent: NumericFormatCustom,
    };
    const htmlInputProps = {
        min: field?.min,
        max: field?.max,
        step: field?.step,
        style: { textAlign: 'right' },
    };
    return <TextField
        name={name}
        label={!inline ? label : undefined}
        placeholder={inline ? label : undefined}
        value={value ?? ''}
        required={required ?? field.required}
        disabled={disabled}
        error={fieldError != null}
        title={title}
        helperText={helperText}
        onChange={(e) => onChange(e.target.value === '' ? null : e.target.value)}
        fullWidth
        {...componentProps}
        slotProps={{
            input: inputProps,
            htmlInput: htmlInputProps,
        }} />;
}
export default FormFieldNumber;