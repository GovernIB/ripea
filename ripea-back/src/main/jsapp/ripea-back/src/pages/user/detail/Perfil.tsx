import {useMemo} from "react";
import {Grid, Box} from "@mui/material";
import {MuiFormDialog, useBaseAppContext, useMuiFormDialogApiRef, useFormContext, DialogButton} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData} from "../../../components/CardData.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import * as builder from '../../../util/springFilterUtils';
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import {StyledLabel} from "../../../components/StyledLabel.tsx";
import {TemaAplicacio, useThemeUserContext} from "../../../components/ThemeUserProvider.tsx";

const PerfilFrom = ({setTheme}: { setTheme: (value:TemaAplicacio) => void }) =>{
    const {data, fields} = useFormContext();
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    return <Grid container columnSpacing={1} rowSpacing={1}>
        <CardData
            title={t('page.user.perfil.dades')}
            cardProps={{border: '1px solid #004B99'}}
            headerProps={{color: 'white', backgroundColor: '#004B99 !important', borderBottom: 'none'}}
        >
            <MuiDetail entity={data} fields={fields} sx={{ width: '100%' }}>
                <FieldData field={"nom"} sx={{border: 'none'}} size={4}/>
                <FieldData field={"email"} sx={{borderTop: 'none'}} size={4}/>
                <FieldData field={"rols"} sx={{borderTop: 'none'}} size={4} componentTextProps={{ component: Box }} isObject>
                    <Box display={'flex'} flexWrap="wrap" gap={1}>
                        {user?.auth.map((r:string) => <StyledLabel key={r} backgroundColor={'#6e6e6e'}>{r}</StyledLabel>)}
                    </Box>
                </FieldData>
            </MuiDetail>
            <GridFormField xs={12} name="emailAlternatiu"/>
            <GridFormField xs={12} name="idioma" required/>
        </CardData>

        <CardData title={t('page.user.perfil.correu')}>
            <GridFormField name="rebreEmailsAgrupats"/>
            <GridFormField name="rebreAvisosNovesAnotacions"/>
        </CardData>

        <CardData title={t('page.user.perfil.generic')}>
            <GridFormField name="numElementsPagina" />
            <GridFormField name="entitatPerDefecte" namedQueries={[`BY_USUARI`]}/>
            <GridFormField name="procediment" filter={builder.and(
                builder.eq('entitat.id', data?.entitatPerDefecte?.id)
            )}/>
            <GridFormField name="interficieUsuari" required/>
            <GridFormField name="modeFosc"
                           onChange={(value)=>{
                               setTheme(value ?TemaAplicacio.OBSCUR :TemaAplicacio.CLAR)
                           }}/>
        </CardData>

        <CardData title={t('page.user.perfil.column')}>
            <GridFormField name="expedientListDataDarrerEnviament"/>
            <GridFormField name="expedientListAgafatPer"/>
            <GridFormField name="expedientListInteressats"/>
            <GridFormField name="expedientListComentaris"/>
            <GridFormField name="expedientListGrup"/>
        </CardData>

        <CardData title={t('page.user.perfil.vista')}>
            <GridFormField name="vistaActual" required/>
            <GridFormField name="expedientExpandit"/>
        </CardData>

        {/* <CardData title={t('page.user.perfil.moure')}>
            <GridFormField name="vistaMoureActual" required/>
        </CardData> */}
    </Grid>
}

const usePerfil = () => {
    const { t } = useTranslation();
    const { value: user, refresh } = useUserSession();
    const {setValue: setTheme, removeValue: removeTheme} = useThemeUserContext()

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
                    removeTheme();
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
    ], [formApiRef, removeTheme, tBase]);

    const dialog =
        <MuiFormDialog
            resourceName={'usuariResource'}
            title={t('page.user.perfil.title')}
            onClose={(reason?: string) => reason !== 'backdropClick'}
            dialogButtons={dialogButtons}
            apiRef={formApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg'}}
        >
            <PerfilFrom setTheme={setTheme}/>
        </MuiFormDialog>

    return {
        handleOpen,
        dialog
    }
}
export default usePerfil;