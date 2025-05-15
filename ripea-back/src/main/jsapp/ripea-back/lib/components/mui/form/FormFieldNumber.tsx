import React from 'react';
import { NumericFormat, NumericFormatProps } from 'react-number-format';
import TextField from '@mui/material/TextField';
import { useBaseAppContext } from '../../BaseAppContext';
import { useFormContext } from '../../form/FormContext';
import { FormFieldCustomProps } from '../../form/FormField';
import { useFormFieldCommon } from './FormFieldText';

type FormFieldNumberProps = FormFieldCustomProps & {
    allowNegative?: boolean;
    decimalSeparator?: boolean | string;
    thousandSeparator?: boolean | string;
    valueIsNumericString?: boolean;
    prefix?: string;
    suffix?: string;
}

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
    const allowNegative = (other as any).min < 0;
    const decimalSeparator = getDecimalSeparator(currentLanguage);
    const thousandSeparator = getThousandSeparator(currentLanguage);
    const valueIsNumericString = field?.type === 'decimal';
    return <NumericFormat
        allowNegative={allowNegative}
        decimalSeparator={decimalSeparator}
        thousandSeparator={thousandSeparator}
        valueIsNumericString={valueIsNumericString}
        {...other}
        getInputRef={ref}
        onValueChange={(values: any) => {
            onChange({
                target: {
                    name: props.name,
                    value: values.value,
                },
            });
        }} />;
});

export const FormFieldNumber: React.FC<FormFieldNumberProps> = (props) => {
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
        allowNegative,
        decimalSeparator,
        thousandSeparator,
        valueIsNumericString,
        prefix,
        suffix,
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
        inputComponent: NumericFormatCustom,
    };
    const htmlInputProps = {
        min: field?.min,
        max: field?.max,
        step: field?.step,
        allowNegative,
        decimalSeparator,
        thousandSeparator,
        valueIsNumericString,
        prefix,
        suffix,
        style: { textAlign: 'right' },
    };
    return <TextField
        name={name}
        label={!inline ? label : undefined}
        placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
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