import {useRef} from "react";
import {Grid2 as Grid, Box} from "@mui/material";
import {MuiFormDialog, useBaseAppContext, MuiFormDialogApi, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData} from "../../../components/CardData.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import * as builder from '../../../util/springFilterUtils';
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import {StyledLabel} from "../../../components/StyledLabel.tsx";

const PerfilFrom = () =>{
    const {data, fields} = useFormContext();
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    return <Grid container columnSpacing={1} rowSpacing={1}>
        <CardData
            title={t('page.user.perfil.dades')}
            cardProps={{border: '1px solid #004B99'}}
            headerProps={{color: 'white', backgroundColor: '#004B99 !important', borderBottom: 'none'}}
        >
            <MuiDetail entity={data} fields={fields} sx={{ width: '100%'}}>
                <FieldData field={"nom"} sx={{border: 'none'}} size={4}/>
                <FieldData field={"email"} sx={{borderTop: 'none'}} size={4}/>
                <FieldData field={"rols"} sx={{borderTop: 'none'}} size={4} commponentProps={{ component: Box }} isObject>
                    <Box display={'flex'} flexWrap="wrap" gap={1}>
                        {user?.auth.map((r) => <StyledLabel backgroundColor={'#6e6e6e'}>{r}</StyledLabel>)}
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
            <GridFormField name="modeFosc"/>
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

    const formApiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleOpen = () => {
        formApiRef.current?.show(user?.codi)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.user.perfil.ok', {nom: user.nom}), 'success');
            })
            .catch((error) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    const dialog =
        <MuiFormDialog
            resourceName={'usuariResource'}
            title={t('page.user.perfil.title')}
            onClose={(reason?: string) => reason !== 'backdropClick'}
            apiRef={formApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg'}}
        >
            <PerfilFrom/>
        </MuiFormDialog>

    return {
        handleOpen,
        dialog
    }
}
export default usePerfil;