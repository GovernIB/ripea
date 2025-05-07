import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialog, useBaseAppContext, MuiFormDialogApi} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData} from "../../../components/CardData.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {useUserSession} from "../../../components/Session.tsx";

const usePerfil = () => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    const formApiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleOpen = () => {
        formApiRef.current?.show(user?.codi)
            .then(() => {
                // refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }

    const dialog =
        <MuiFormDialog
            resourceName={'usuariResource'}
            title={t('page.user.perfil.title')}
            apiRef={formApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg'}}
        >
            <Grid container columnSpacing={1} rowSpacing={1}>
                <CardData
                    title={t('page.user.perfil.dades')}
                    cardProps={{border: '1px solid #004B99'}}
                    headerProps={{color: 'white', backgroundColor: '#004B99'}}
                >
                    <GridFormField xs={12} name="nom" disabled readOnly/>
                    <GridFormField xs={12} name="nif" disabled readOnly/>
                    <GridFormField xs={12} name="email" disabled readOnly/>
                    <GridFormField xs={12} name="emailAlternatiu"/>

                    <GridFormField xs={12} name="rols"
                                   value={user?.auth}
                                   disabled readOnly multiple/>
                    <GridFormField xs={12} name="idioma" disabled readOnly/>
                </CardData>

                <CardData title={t('page.user.perfil.correu')}>
                    <GridFormField xs={12} name="rebreEmailsAgrupats"/>
                    <GridFormField xs={12} name="rebreAvisosNovesAnotacions"/>
                </CardData>

                <CardData title={t('page.user.perfil.generic')}>
                    <GridFormField xs={12} name="numElementsPagina"/>
                    <GridFormField xs={12} name="entitatPerDefecte"/>
                    <GridFormField xs={12} name="procediment"/>
                </CardData>

                <CardData title={t('page.user.perfil.column')}>
                    <GridFormField xs={12} name="expedientListDataDarrerEnviament"/>
                    <GridFormField xs={12} name="expedientListAgafatPer"/>
                    <GridFormField xs={12} name="expedientListInteressats"/>
                    <GridFormField xs={12} name="expedientListComentaris"/>
                    <GridFormField xs={12} name="expedientListGrup"/>
                </CardData>

                <CardData title={t('page.user.perfil.vista')}>
                    <GridFormField xs={12} name="vistaActual" required/>
                    <GridFormField xs={12} name="expedientExpandit"/>
                </CardData>

                <CardData title={t('page.user.perfil.moure')}>
                    <GridFormField xs={12} name="vistaMoureActual" required/>
                </CardData>
            </Grid>
        </MuiFormDialog>

    return {
        handleOpen,
        dialog
    }
}
export default usePerfil;