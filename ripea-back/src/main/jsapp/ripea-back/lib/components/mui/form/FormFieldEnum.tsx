import React from 'react';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Checkbox from '@mui/material/Checkbox';
import TextField from '@mui/material/TextField';
import Autocomplete, { AutocompleteChangeReason } from '@mui/material/Autocomplete';
import Icon from '@mui/material/Icon';
import { FormFieldCustomProps } from '../../form/FormField';
import { useBaseAppContext } from '../../BaseAppContext';
import { useResourceApiContext } from '../../ResourceApiContext';
import { useFormFieldCommon } from './FormFieldText';

type FormFieldEnumProps = FormFieldCustomProps & {
    multiple?: boolean;
    hiddenEnumValues?: string[];
    requestParams?: any;
    autocomplete?: boolean;
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
        autocomplete,
    } = props;
    const { t } = useBaseAppContext();
    const { requestHref } = useResourceApiContext();
    const [textFieldOpen, setTextFieldOpen] = React.useState(false);
    const [enumOptions, setEnumOptions] = React.useState<EnumOption[]>();
    const [autocompleteOpen, setAutocompleteOpen] = React.useState<boolean>(false);
    const [autocompleteInputValue, setAutocompleteInputValue] = React.useState<string>('');
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
    const valueTextFieldMultipleAdapted = multiple ? (value != null ? (Array.isArray(value) ? value : [value]) : []) : (value ?? '');
    const valueAutocompleteMultipleAdapted = React.useMemo(() => {
        return multiple ? (value != null ? (Array.isArray(value) ? value : [value]) : []) : (value ?? null);
    }, [multiple, value]);
    const autocompleteInputValueMultipleAdapted = React.useMemo(() => {
        return multiple ? autocompleteInputValue : (value != null ? (enumOptions?.find(o => o.value === value)?.description ?? '') : autocompleteInputValue);
    }, [multiple, value, autocompleteInputValue, enumOptions]);
    const handleAutocompleteOnChange = (_event: Event, value: any, reason: AutocompleteChangeReason): void => {
        if (reason === 'clear') {
            setAutocompleteOpen(true);
        }
        if (multiple) {
            console.log('>>> handleAutocompleteOnChange', value)
            onChange(value?.map((v: any) => v.id));
            setAutocompleteInputValue('');
        } else {
            onChange(value?.id ?? undefined);
            setAutocompleteInputValue(value?.description ?? '');
        }
    }
    const handleAutocompleteOnInputChange = (_event: any, newValue: string) => {
        setAutocompleteInputValue(newValue);
    }
    React.useEffect(() => {
        if (field.options != null) {
            const optionsObj = { ...field.options };
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
    }, [field, requestParams, hiddenEnumValues]);
    return enumOptions && (!autocomplete ? <TextField
        select
        name={name}
        label={!inline ? label : undefined}
        placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
        value={valueTextFieldMultipleAdapted}
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
                textFieldOpen,
                readOnly,
                onClose: () => setTextFieldOpen(false),
                onOpen: () => setTextFieldOpen(true),
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
    </TextField> : <Autocomplete
        name={name}
        value={valueAutocompleteMultipleAdapted}
        onChange={handleAutocompleteOnChange}
        inputValue={autocompleteInputValueMultipleAdapted}
        onInputChange={handleAutocompleteOnInputChange}
        options={enumOptions.map(o => ({ id: o.value, description: o.description ?? o.value }))}
        multiple={multiple}
        readOnly={readOnly}
        disableCloseOnSelect={multiple}
        open={autocompleteOpen}
        onOpen={() => !disabled && !readOnly && setAutocompleteOpen(true)}
        onClose={(event: Event, reason) => {
            if (reason === 'escape') {
                // Esborra el valor de inputValue si no hi ha cap opció seleccionada
                // quan es pitja la tecla escape.
                handleAutocompleteOnInputChange(event, value?.description ?? '');
            }
            setAutocompleteOpen(false);
        }}
        getOptionLabel={(option: any) => {
            if (typeof option === 'string') {
                if (option.length) {
                    const optionFound = enumOptions.find(o => o.value === option);
                    return optionFound ? (optionFound.description ?? optionFound.value) : '';
                } else {
                    return option;
                }
            } else {
                return option?.description;
            }
        }}
        isOptionEqualToValue={(option: any, value: any) => option.id === value?.id}
        renderOption={(props, option: any, { selected }) => {
            const optionDescription = option.description;
            if (multiple) {
                const { key, ...optionProps } = props;
                return <li key={key} {...optionProps}>
                    <Checkbox
                        checked={selected}
                        icon={<Icon>check_box_outline_blank</Icon>}
                        checkedIcon={<Icon>check_box</Icon>}
                        sx={{ mr: 1 }} />
                    {optionDescription}
                </li>;
            } else {
                return <li {...props} key={option.id}>{optionDescription}</li>;
            }
        }}
        fullWidth
        {...componentProps}
        renderInput={(params) => <TextField
            {...params}
            label={!inline ? label : undefined}
            placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
            disabled={disabled}
            required={required ?? field.required}
            error={fieldError != null}
            title={componentProps?.title ?? title}
            helperText={helperText}
            sx={{
                // Sin esto, si la columna no tiene suficiente espacio para el texto y el icono,
                // coloca uno encima del otro y se ve cortado por la mitad.
                '& .MuiAutocomplete-inputRoot': {
                    flexWrap: inline ? 'nowrap' : undefined,
                },
            }}
            InputProps={{
                ...params.InputProps,
                startAdornment: params.InputProps.startAdornment ? <>
                    {startAdornment}
                    {params.InputProps.startAdornment}
                </> : startAdornment,
                endAdornment: params.InputProps.endAdornment,
            }}
            inputProps={params.inputProps}
        />}
        slotProps={{
            popper: {
                sx: {
                    minWidth: '300px',
                },
            },
        }}
        // The next prop fixes a bug in Firefox where the focus was put into the Listbox
        // container, and then lost focus of the form completely when navigating to the next input
        ListboxProps={{ tabIndex: '-1' }}
        clearText={t('form.field.enum.clear')}
        noOptionsText={t('form.field.enum.noOptions')}
    />);
}
export default FormFieldEnum;