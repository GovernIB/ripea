import React from 'react';
import { NumericFormat, NumericFormatProps } from 'react-number-format';
import TextField, { TextFieldProps } from '@mui/material/TextField';
import { useDebounce } from '../../../util/useDebounce';
import { useBaseAppContext } from '../../BaseAppContext';
import { useFormContext } from '../../form/FormContext';
import { FormFieldCustomProps } from '../../form/FormField';
import { useFormFieldCommon } from './FormFieldText';

type FormFieldNumberProps = FormFieldCustomProps & {
    /** Indica si es permeten valors negatius (atribut de react-number-format) */
    allowNegative?: boolean;
    /** Escala dels valors decimals (atribut de react-number-format) */
    decimalScale?: number;
    /** Caràcter pel separador de decimals (atribut de react-number-format) */
    decimalSeparator?: boolean | string;
    /** Caràcter pel separador de milers (atribut de react-number-format) */
    thousandSeparator?: boolean | string;
    /** Indica si el valor és un string (atribut de react-number-format) */
    valueIsNumericString?: boolean;
    /** Prefix per a valors que representen una divisa (atribut de react-number-format) */
    prefix?: string;
    /** Suffix per a valors que representen una divisa (atribut de react-number-format) */
    suffix?: string;
    /** Indica si s'ha de fer debounce amb els valors del camp */
    debounce?: true;
};

type CustomProps = {
    name: string;
    onChange: (event: { target: { name: string; value: string } }) => void;
};

export const getDecimalSeparator = (locale?: string) => {
    return Intl.NumberFormat(locale === 'es' ? 'ca' : locale)
        .formatToParts(1.1)
        .find((p) => p.type === 'decimal')?.value;
};
export const getThousandSeparator = (locale?: string) => {
    return Intl.NumberFormat(locale === 'es' ? 'ca' : locale)
        .formatToParts(10000.1)
        .find((p) => p.type === 'group')?.value;
};

const NumericFormatCustom = React.forwardRef<NumericFormatProps, CustomProps>((props, ref) => {
    const { onChange, ...other } = props;
    const { currentLanguage } = useBaseAppContext();
    const { fields } = useFormContext();
    const field = fields?.find((f) => f.name === other.name);
    const allowNegative = (other as any).min < 0;
    const decimalSeparator = getDecimalSeparator(currentLanguage);
    const thousandSeparator = getThousandSeparator(currentLanguage);
    const valueIsNumericString = field?.type === 'decimal';
    return (
        <NumericFormat
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
            }}
        />
    );
});

export const FormFieldNumber: React.FC<
    FormFieldNumberProps & {
    overrideTextFieldProps?: TextFieldProps;
}
> = (props) => {
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
        decimalScale,
        decimalSeparator,
        thousandSeparator,
        valueIsNumericString,
        prefix,
        suffix,
        debounce,
    } = props;
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps
    );
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
        decimalScale,
        decimalSeparator,
        thousandSeparator,
        valueIsNumericString,
        prefix,
        suffix,
        style: { textAlign: 'right' },
    };

    const [localValue, setLocalValue] = React.useState<string | null>(value);
    const debounceValue = useDebounce(localValue, undefined, true)
    const changedValue = debounce ? debounceValue : localValue;
    React.useEffect(() => {
        setLocalValue(value);
    }, [value]);
    React.useEffect(() => {
        onChange?.(changedValue);
    }, [changedValue]);
    return (
        <TextField
            name={name}
            label={!inline ? label : undefined}
            placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
            value={value ?? ''}
            required={required ?? field.required}
            disabled={disabled}
            error={fieldError != null}
            title={title}
            helperText={helperText}
            onChange={(e) => setLocalValue(e.target.value === '' ? null : e.target.value)}
            fullWidth
            {...componentProps}
            slotProps={{
                input: inputProps,
                htmlInput: htmlInputProps,
            }}
        />
    );
};

export default FormFieldNumber;