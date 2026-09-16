import React from 'react';
import FormControl from '@mui/material/FormControl';
import FormLabel from '@mui/material/FormLabel';
import FormHelperText from '@mui/material/FormHelperText';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import { Theme, alpha } from '@mui/material/styles';
import { FormFieldCustomProps, useResourceApiContext } from 'reactlib';

/** Tipus amb què es registra el component a BaseApp i amb què s'ha de demanar des d'un FormField. */
export const FORM_FIELD_TYPE_TOGGLE_BUTTONS = 'toggle-buttons';

type FormFieldToggleButtonsProps = FormFieldCustomProps & {
    /** Llista dels valors de l'enumerat que no s'han de mostrar */
    hiddenEnumValues?: string[];
    /** Paràmetres addicionals a enviar a l'hora de consultar els valors disponibles */
    requestParams?: any;
};

type ToggleOption = {
    value: string;
    description?: any;
};

/**
 * Selecció tintada i no plena: el botó actiu s'ha de distingir a cop d'ull però no ha de competir
 * amb el botó d'acció del peu del formulari, que és el primari sòlid. En mode fosc el text del botó
 * actiu passa a primary.light, perquè el primary.dark que MUI calcula no es llegiria sobre el fons.
 */
const toggleButtonSx = {
    textTransform: 'none',
    color: 'text.secondary',
    border: (theme: Theme) => `1px solid ${alpha(theme.palette.text.primary, 0.23)}`,
    '&.Mui-selected': {
        backgroundColor: (theme: Theme) => alpha(theme.palette.primary.main, 0.12),
        color: (theme: Theme) =>
            theme.palette.mode === 'dark' ? theme.palette.primary.light : theme.palette.primary.dark,
        border: (theme: Theme) => `1px solid ${alpha(theme.palette.primary.main, 0.55)}`,
        fontWeight: 600,
        '&:hover': {
            backgroundColor: (theme: Theme) => alpha(theme.palette.primary.main, 0.2),
        },
    },
};

/**
 * Camp d'enumerat presentat com una botonera de selecció exclusiva, equivalent als radios amb
 * aparença de botons del JSP (<rip:inputRadio ... botons="true" />). Pensat per a enumerats de
 * poques opcions on interessa que totes siguin visibles sense desplegar res.
 *
 * Les opcions s'obtenen igual que a FormFieldEnum de la llibreria: del camp mateix si les porta
 * (field.options) o, si no, de l'endpoint d'opcions que indica field.dataSource. Així les etiquetes
 * són sempre les traduïdes pel servidor i no cal duplicar-les al front.
 */
const FormFieldToggleButtons: React.FC<FormFieldToggleButtonsProps> = (props) => {
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
        hiddenEnumValues,
        requestParams,
    } = props;
    const { requestHref } = useResourceApiContext();
    const [options, setOptions] = React.useState<ToggleOption[]>();
    // L'efecte que carrega les opcions escriu estat, així que depèn d'identitats estables: si un
    // cridador passa hiddenEnumValues en línia, l'array seria nou a cada render i l'efecte no pararia.
    const hiddenValuesKey = hiddenEnumValues?.join('|') ?? '';
    // eslint-disable-next-line react-hooks/exhaustive-deps
    const hiddenValues = React.useMemo(() => hiddenEnumValues ?? [], [hiddenValuesKey]);
    React.useEffect(() => {
        if (field?.options != null) {
            const optionsObj = { ...field.options };
            hiddenValues.forEach((v: any) => {
                delete optionsObj[v];
            });
            setOptions(
                Object.entries(optionsObj).map(([value, description]) => ({ value, description }))
            );
        } else if (field?.dataSource != null) {
            const dataSource = field.dataSource;
            const templateData = requestParams;
            const href =
                dataSource.href +
                (templateData != null ? '{?' + Object.keys(templateData).join(',') + '}' : '');
            requestHref(href, templateData).then((state) => {
                setOptions(
                    state
                        .getEmbedded()
                        .map((e: any) => ({
                            value: e.data[dataSource.valueField],
                            description: e.data[dataSource.labelField],
                        }))
                        .filter((o: ToggleOption) => !hiddenValues.includes(o.value))
                );
            });
        } else {
            setOptions([]);
        }
    }, [field, requestParams, hiddenValues, requestHref]);
    const isRequired = required ?? field?.required;
    const helperText = inline ? field?.helperText : (fieldError?.message ?? field?.helperText);
    const title = inline ? (fieldError?.message ?? field?.title) : field?.title;
    const handleChange = (_event: React.MouseEvent<HTMLElement>, newValue: string | null) => {
        // Amb selecció exclusiva, tornar a pitjar el botó actiu retorna null: si el camp és
        // obligatori es manté el valor, que és el comportament d'un grup de radios.
        if (newValue == null && isRequired) {
            return;
        }
        onChange(newValue ?? undefined);
    };
    return (
        <FormControl
            fullWidth
            required={isRequired}
            disabled={disabled}
            error={fieldError != null}
            title={title}>
            {/* lineHeight va després de typography: la variant 'caption' en porta una de pròpia que
                deixaria una caixa de 20px per a un text de 12px i separaria l'etiqueta dels botons. */}
            {!inline && (
                <FormLabel id={name + '-label'} sx={{ typography: 'caption', lineHeight: 1.2, mb: '2px' }}>
                    {label}
                </FormLabel>
            )}
            <ToggleButtonGroup
                exclusive
                value={value ?? null}
                onChange={handleChange}
                disabled={disabled || readOnly}
                aria-labelledby={!inline ? name + '-label' : undefined}
                aria-label={inline ? label : undefined}
                fullWidth
                {...componentProps}>
                {options?.map((o) => (
                    <ToggleButton key={o.value} value={o.value} sx={toggleButtonSx}>
                        {o.description ?? o.value}
                    </ToggleButton>
                ))}
            </ToggleButtonGroup>
            {helperText && <FormHelperText>{helperText}</FormHelperText>}
        </FormControl>
    );
};

export default FormFieldToggleButtons;
