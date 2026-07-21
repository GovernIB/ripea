import React, {useMemo} from "react";
import {Grid, Box, Icon, Typography, ToggleButtonGroup, ToggleButton} from "@mui/material";
import {MuiFormDialog, useBaseAppContext, useMuiFormDialogApiRef, useFormContext, DialogButton} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData} from "../../../components/CardData.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import * as builder from '../../../util/springFilterUtils';
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import {StyledLabel} from "../../../components/StyledLabel.tsx";
import {EstilMenuProp, TemaAplicacio, useThemeUserContext} from "../../../components/ThemeUserProvider.tsx";

export const TemaObscurSelector: React.FC = () => {
    const { data, apiRef, fields } = useFormContext();
    const temaAplicacioField = fields?.find?.(f => f.name == "temaAplicacio")
    const handleChange = (_event: React.MouseEvent<HTMLElement>, newValue: any | null) => {
        if (newValue !== null) {
            apiRef?.current?.setFieldValue("temaAplicacio", newValue);
        }
    };
    return (
        <>
            <Typography component="label" sx={{
                display: 'block',
                ml: 1.75,
                mb: 0.75,
                fontSize: '0.75rem',
                lineHeight: 1,
                color: 'text.secondary',
            }}>
                {temaAplicacioField.label}
            </Typography>
            <ToggleButtonGroup
                value={data?.temaAplicacio ?? "SISTEMA"}
                exclusive
                onChange={handleChange}
                size="small"
                sx={{
                    display: 'flex',
                    width: '100%',
                    justifyContent: 'center',
                }}
            >
                <ToggleButton value={TemaAplicacio.CLAR} sx={{ flex: 1, gap: 1 }}>
                    <Icon>light_mode</Icon> {temaAplicacioField.options[TemaAplicacio.CLAR]}
                </ToggleButton>
                <ToggleButton value={TemaAplicacio.OBSCUR} sx={{ flex: 1, gap: 1 }}>
                    <Icon>dark_mode</Icon> {temaAplicacioField.options[TemaAplicacio.OBSCUR]}
                </ToggleButton>
                <ToggleButton value={TemaAplicacio.DRACULA} sx={{ flex: 1, gap: 1 }}>
                    <Icon>auto_awesome</Icon> {temaAplicacioField.options[TemaAplicacio.DRACULA]}
                </ToggleButton>
                <ToggleButton value={TemaAplicacio.SISTEMA} sx={{ flex: 1, gap: 1 }}>
                    <Icon>settings_brightness</Icon> {temaAplicacioField.options[TemaAplicacio.SISTEMA]}
                </ToggleButton>
            </ToggleButtonGroup>
        </>
    );
};
export const EstilMenuSelector: React.FC = () => {
    const { data, apiRef, fields } = useFormContext();
    const estilMenuField = fields?.find?.(f => f.name == "estilMenu")
    const handleChange = (_event: React.MouseEvent<HTMLElement>, newValue: any | null) => {
        if (newValue !== null) {
            apiRef?.current?.setFieldValue("estilMenu", newValue);
        }
    };
    return (
        <>
            <Typography component="label" sx={{
                display: 'block',
                ml: 1.75,
                mb: 0.75,
                fontSize: '0.75rem',
                lineHeight: 1,
                color: 'text.secondary',
            }}>
                {estilMenuField.label}
            </Typography>
            <ToggleButtonGroup
                value={data?.estilMenu ?? "TEMA"}
                exclusive
                onChange={handleChange}
                size="small"
                sx={{
                    display: 'flex',
                    width: '100%',
                    justifyContent: 'center',
                }}
            >
                <ToggleButton value={EstilMenuProp.theme} sx={{ flex: 1, gap: 1 }}>
                    <Icon>palette</Icon> {estilMenuField.options[EstilMenuProp.theme]}
                </ToggleButton>
                <ToggleButton value={EstilMenuProp.inverse} sx={{ flex: 1, gap: 1 }}>
                    <Icon>invert_colors</Icon> {estilMenuField.options[EstilMenuProp.inverse]}
                </ToggleButton>
                <ToggleButton value={EstilMenuProp.footer} sx={{ flex: 1, gap: 1 }}>
                    <Icon>vertical_align_bottom</Icon> {estilMenuField.options[EstilMenuProp.footer]}
                </ToggleButton>
            </ToggleButtonGroup>
        </>
    );
};

const PerfilFrom = () =>{
    const { data, fields } = useFormContext();
    const { t } = useTranslation();
    const { value: user } = useUserSession();
    const rebreEmailsGlobal = data?.rebreEmailsGlobal;

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
                    <GridFormField name="rebreEmailsGlobal" componentProps={{ sx: { ml: -1 } }} />
                    <GridFormField name="rebreEmailsAgrupats" componentProps={{ sx: { ml: 2 } }} disabled={!rebreEmailsGlobal} />
                    <GridFormField name="rebreEmailsCanviEstatRevisio" componentProps={{ sx: { ml: 2 } }} disabled={!rebreEmailsGlobal} />
                    <GridFormField name="rebreAvisosNovesAnotacions" componentProps={{ sx: { ml: 2 } }} disabled={!rebreEmailsGlobal} />
                </Grid>
            </CardData>

            <CardData title={t('page.user.perfil.generic')} icon="settings" >
                <GridFormField name="entitatPerDefecte" size={6} namedQueries={[`BY_USUARI`]} />
                <GridFormField name="procediment"		size={6} filter={builder.and(builder.eq('entitat.id', data?.entitatPerDefecte?.id))} />
                <GridFormField name="numElementsPagina" size={6}/>
                <GridFormField name="interficieUsuari"	size={6} required />
            </CardData>

            <CardData title={t('page.user.perfil.tema')} icon="palette" >
                <TemaObscurSelector />
                <EstilMenuSelector />
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
};

const usePerfil = () => {
    const { t } = useTranslation();
    const { value: user, refresh } = useUserSession();
    const {setPreview, remove} = useThemeUserContext()

    const formApiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow, t: tBase } = useBaseAppContext();

    const handleOpen = () => {
        formApiRef.current
            ?.show(user?.codi)
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.user.perfil.ok', { nom: user.nom }), 'success');
            })
            .catch((error) => {
                // Qualsevol tancament sense desar (cancel·lar, 'x', Escape) rebutja
                // la promesa: revertim la previsualització del tema a la configuració desada.
                remove();
                if (error?.message)
                    temporalMessageShow(null, error?.message, 'error');
            });
    };

    const dialogButtons = useMemo<DialogButton[]>(() => [
        {
            value: false,
            text: tBase('buttons.form.cancel'),
            componentProps: {
                variant: 'outlined',
                onClick: () => {
                    remove();
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
    ], [formApiRef, remove, tBase]);

    const dialog = (
        <MuiFormDialog
            resourceName={'usuariResource'}
            title={t('page.user.perfil.title')}
            onClose={(reason?: string) => reason !== 'backdropClick'}
            dialogButtons={dialogButtons}
            apiRef={formApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg'}}
            formComponentProps={{
                onDataChange: (data: any) => {
                    setPreview({
                        temaAplicacio: data?.temaAplicacio,
                        estilMenu: data?.estilMenu,
                    });
                },
            }}
        >
            <PerfilFrom/>
        </MuiFormDialog>
    );

    return {
        handleOpen,
        dialog,
    };
};
export default usePerfil;
