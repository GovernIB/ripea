import React, {useMemo, useState, useEffect, useRef, useCallback} from "react";
import {Grid, Box, Button, Icon, Checkbox, FormControlLabel} from "@mui/material";
import {MuiFormDialog, useBaseAppContext, useMuiFormDialogApiRef, useFormContext, DialogButton} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData} from "../../../components/CardData.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import * as builder from '../../../util/springFilterUtils';
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import {StyledLabel} from "../../../components/StyledLabel.tsx";
import {ThemePreview, useThemeUserContext} from "../../../components/ThemeUserProvider.tsx";
import {DEFAULT_PRIMARY_COLOR, DEFAULT_SECONDARY_COLOR} from "../../../theme.ts";

// Selector d'un color del tema (principal o secundari). El valor visible del
// picker s'actualitza a l'instant (estat local) per no perdre resposta mentre
// s'arrossega, però l'aplicació del tema (setPreview -> reconstrucció del tema +
// re-render global) i el desat al formulari es fan amb debounce de 200ms: el
// <input type="color"> dispara onChange contínuament durant l'arrossegament i
// reconstruir el tema a cada esdeveniment alenteix molt l'execució.
const COLOR_DEBOUNCE_MS = 200;

type ThemeColorFieldProps = {
    fieldName: 'colorPrincipal' | 'colorSecundari';
    defaultColor: string;
    label: string;
    setPreview: (value: ThemePreview) => void;
    // Clau de previsualització a actualitzar (primary/secondary).
    previewKey: 'primary' | 'secondary';
};

const ThemeColorField = ({ fieldName, defaultColor, label, setPreview, previewKey }: ThemeColorFieldProps) => {
    const { data, apiRef } = useFormContext();
    const { t } = useTranslation();
    const color = data?.[fieldName] || defaultColor;

    // Valor mostrat pel picker (immediat).
    const [localColor, setLocalColor] = useState<string>(color);
    // Sincronitza si el valor canvia des de fora (p. ex. càrrega del formulari).
    useEffect(() => { setLocalColor(color); }, [color]);

    const debounceRef = useRef<ReturnType<typeof setTimeout>>();
    useEffect(() => () => { if (debounceRef.current) clearTimeout(debounceRef.current); }, []);

    const commit = useCallback((value: string) => {
        apiRef?.current?.setFieldValue(fieldName, value);
        setPreview(previewKey === 'primary' ? { primary: value } : { secondary: value });
    }, [apiRef, fieldName, previewKey, setPreview]);

    const handlePick = (value: string) => {
        setLocalColor(value);
        if (debounceRef.current) clearTimeout(debounceRef.current);
        debounceRef.current = setTimeout(() => commit(value), COLOR_DEBOUNCE_MS);
    };

    const handleReset = () => {
        if (debounceRef.current) clearTimeout(debounceRef.current);
        setLocalColor(defaultColor);
        commit(defaultColor);
    };

    const pickerId = `${fieldName}Picker`;
    return (
        <Grid size={4}>
            <Box display="flex" alignItems="center" gap={1} sx={{ height: '100%' }}>
                <Box component="label" htmlFor={pickerId} sx={{ fontStyle: 'italic', fontSize: '14px' }}>
                    {label}
                </Box>
                <input
                    id={pickerId}
                    type="color"
                    value={localColor}
                    onChange={(e) => handlePick(e.target.value)}
                    aria-label={label}
                    style={{ width: 48, height: 32, padding: 0, border: 'none', background: 'none', cursor: 'pointer' }}
                />
                <Button size="small" variant="outlined" startIcon={<Icon>restart_alt</Icon>} onClick={handleReset}>
                    {t('page.user.perfil.colorReset')}
                </Button>
            </Box>
        </Grid>
    );
};

const PerfilFrom = ({setPreview}: { setPreview: (value: ThemePreview) => void }) =>{
    const { data, fields, apiRef } = useFormContext();
    const { t } = useTranslation();
    const { value: user } = useUserSession();
    const [ correusActius, setCorreusActius ] = React.useState(!!(data?.rebreEmailsAgrupats && data?.rebreEmailsCanviEstatRevisio && data?.rebreAvisosNovesAnotacions));
    
    const handleGlobalCheckChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const checked = event.target.checked;

        apiRef?.current?.setFieldValue("rebreEmailsAgrupats", checked);
        apiRef?.current?.setFieldValue("rebreEmailsCanviEstatRevisio", checked);
        apiRef?.current?.setFieldValue("rebreAvisosNovesAnotacions", checked);
    };

    React.useEffect(() => {
        if (data?.rebreEmailsAgrupats && data?.rebreEmailsCanviEstatRevisio && data?.rebreAvisosNovesAnotacions)
            setCorreusActius(true);
        else
            setCorreusActius(false);
    },[data?.rebreEmailsAgrupats, data?.rebreEmailsCanviEstatRevisio, data?.rebreAvisosNovesAnotacions])

    return (
        <Grid container columnSpacing={1} rowSpacing={2}>
            <CardData
                title={t('page.user.perfil.dades')}
                icon="person"
            >
                <MuiDetail entity={data} fields={fields} sx={{ width: '100%' }}>
                    <FieldData field={'nom'} sx={{ border: 'none' }} size={4} />
                    <FieldData field={'email'} sx={{ borderTop: 'none' }} size={4} />
                    <FieldData field={'rols'} sx={{ borderTop: 'none' }} size={4} componentTextProps={{ component: Box }} isObject>
                        <Box display={'flex'} flexWrap="wrap" gap={1}>
                            {user?.auth.map((rol: string) => (
                                <StyledLabel key={rol} backgroundColor={'#6e6e6e'}>
                                    {rol}
                                </StyledLabel>
                            ))}
                        </Box>
                    </FieldData>
                </MuiDetail>
                <GridFormField size={8} name="emailAlternatiu" />
                <GridFormField size={4} name="idioma" required />
            </CardData>

            <CardData title={t('page.user.perfil.correu')} icon="email">
                <Grid size={12}>
                    <FormControlLabel
                        control={<Checkbox checked={correusActius} onChange={handleGlobalCheckChange} color="primary" />}
                        label={t('page.user.perfil.activarCorreu')}
                        labelPlacement="end"
                    />
                    <GridFormField name="rebreEmailsAgrupats" componentProps={{ sx: { ml: 2 } }} />
                    <GridFormField name="rebreEmailsCanviEstatRevisio" componentProps={{ sx: { ml: 2 } }} />
                    <GridFormField name="rebreAvisosNovesAnotacions" componentProps={{ sx: { ml: 2 } }} />
                </Grid>
            </CardData>

            <CardData title={t('page.user.perfil.generic')} icon="settings" >
                <GridFormField name="entitatPerDefecte" size={6} namedQueries={[`BY_USUARI`]} />
                <GridFormField name="procediment"		size={6} filter={builder.and(builder.eq('entitat.id', data?.entitatPerDefecte?.id))} />
                <GridFormField name="numElementsPagina" size={6}/>
                <GridFormField name="interficieUsuari"	size={6} required />
            </CardData>

            <CardData title={t('page.user.perfil.tema')} icon="palette" >
                <GridFormField
                    name="modeFosc"
                    size={4}
                    onChange={(value) => {
                        setPreview({ modeFosc: !!value });
                    }}
                />
                <ThemeColorField
                    fieldName="colorPrincipal"
                    defaultColor={DEFAULT_PRIMARY_COLOR}
                    label={t('page.user.perfil.colorPrincipal')}
                    setPreview={setPreview}
                    previewKey="primary"
                />
                <ThemeColorField
                    fieldName="colorSecundari"
                    defaultColor={DEFAULT_SECONDARY_COLOR}
                    label={t('page.user.perfil.colorSecundari')}
                    setPreview={setPreview}
                    previewKey="secondary"
                />
            </CardData>

            <CardData title={t('page.user.perfil.column')} icon="checklist" >
                <GridFormField name="expedientListDataDarrerEnviament" />
                <GridFormField name="expedientListAgafatPer" />
                <GridFormField name="expedientListInteressats" />
                <GridFormField name="expedientListComentaris" />
                <GridFormField name="expedientListGrup" />
            </CardData>

            <CardData title={t('page.user.perfil.vista')} icon="account_tree" >
                <GridFormField name="vistaActual" required />
                <GridFormField name="expedientExpandit" />
            </CardData>

            {/* <CardData title={t('page.user.perfil.moure')}>
            <GridFormField name="vistaMoureActual" required/>
        </CardData> */}
        </Grid>
    );
}

const usePerfil = () => {
    const { t } = useTranslation();
    const { value: user, refresh } = useUserSession();
    const {setPreview, removePreview} = useThemeUserContext()

    const formApiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow, t: tBase } = useBaseAppContext();

    const handleOpen = () => {
        formApiRef.current?.show(user?.codi)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.user.perfil.ok', {nom: user.nom}), 'success');
            })
            .catch((error) => {
                if (error?.message)
                    temporalMessageShow(null, error?.message, 'error');
            });
    }

    const dialogButtons = useMemo<DialogButton[]>(() => [
        {
            value: false,
            text: tBase('buttons.form.cancel'),
            componentProps: {
                variant: 'outlined',
                onClick: () => {
                    removePreview();
                    formApiRef.current?.close();
                },
            },
        },
        {
            value: true,
            text: tBase('buttons.form.save'),
            icon: 'save',
            componentProps: { variant: 'contained' },
        },
    ], [formApiRef, removePreview, tBase]);

    const dialog =
        <MuiFormDialog
            resourceName={'usuariResource'}
            title={t('page.user.perfil.title')}
            onClose={(reason?: string) => reason !== 'backdropClick'}
            dialogButtons={dialogButtons}
            apiRef={formApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg'}}
        >
            <PerfilFrom setPreview={setPreview}/>
        </MuiFormDialog>

    return {
        handleOpen,
        dialog
    }
}
export default usePerfil;