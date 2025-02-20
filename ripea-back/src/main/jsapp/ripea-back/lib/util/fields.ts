import { dateFormatLocale, timeFormatLocale } from './dateFormat';
import { numberFormatCurrency, numberFormatField } from './numberFormat';

/*config: {
type,
currentLanguage,
currencyCode,
currencyDecimalPlaces,
currencyLocale,
decimalPlaces,
noSeconds,
formatterParams,
booleanTextTrue,
booleanTextFalse,
booleanNullAsFalse
}*/

export const isFieldNumericType = (field: any, forcedType?: any) => {
    const type = forcedType ?? field?.type;
    const isNumeric = type === 'number' ||
        type === 'decimal' ||
        type === 'currency' ||
        type === 'date' ||
        type === 'time' ||
        type === 'datetime-local' ||
        type === 'duration';
    return isNumeric;
}

export const formattedFieldValue = (value: any, field?: any, config?: any): string => {
    const processedType = config?.type ?? field?.type;
    if (processedType === 'date') {
        return value ? dateFormatLocale(value) : value;
    } else if (processedType === 'time') {
        return value ? timeFormatLocale(value, config?.noSeconds) : value;
    } else if (processedType === 'datetime-local') {
        return value ? dateFormatLocale(value, true) : value;
    } else if (processedType === 'number' || processedType === 'decimal') {
        return (value === 0 || value) ? numberFormatField(value, field, config?.currentLanguage) : value;
    } else if (processedType === 'currency') {
        const currencyCode = typeof config?.currencyCode === 'function' ? config?.currencyCode(config?.formatterParams) : config?.currencyCode;
        const currencyDecimalPlaces = typeof config?.currencyDecimalPlaces === 'function' ? config?.currencyDecimalPlaces(config?.formatterParams) : config?.currencyDecimalPlaces;
        const decimalPlaces = config?.decimalPlaces;
        const currencyLocale = typeof config?.currencyLocale === 'function' ? config?.currencyLocale(config?.formatterParams) : config?.currencyLocale;
        return (value === 0 || value) ? numberFormatCurrency(value, currencyCode, currencyDecimalPlaces ?? decimalPlaces, currencyLocale ?? config?.currentLanguage) : value;
    } else if (processedType === 'reference') {
        return value?.description;
    } else if (processedType === 'enum') {
        const enumOption = field?.options?.inline?.find((o: any) => o.id === value);
        return enumOption ? enumOption.description : value;
    } else if (processedType === 'checkbox' && (value != null || config?.booleanNullAsFalse)) {
        return value ? config?.booleanTextTrue ?? 'Si' : config?.booleanTextFalse ?? 'No';
    } else {
        return value;
    }
}