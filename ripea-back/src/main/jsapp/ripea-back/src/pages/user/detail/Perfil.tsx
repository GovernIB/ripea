import {useRef} from "react";
import {CardData} from "../../../components/CardData.tsx";
import {MuiFormDialog, useBaseAppContext} from "reactlib";
import {DataFormDialogApi} from "../../../../lib/components/mui/datacommon/DataFormDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {Grid} from "@mui/material";
import {useUserSession} from "../../../components/Session.tsx";

const usePerfil = () => {
    // const { t } = useTranslation();
    const { value: user } = useUserSession();

    const formApiRef = useRef<DataFormDialogApi>();
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
            title={'Datos de usuario'}
            apiRef={formApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg'}}
        >
            <Grid container columnSpacing={1} rowSpacing={1}>
                <CardData
                    title={'Datos de usuario'}
                    cardProps={{border: '1px solid #004B99'}}
                    headerProps={{color: 'white', backgroundColor: '#004B99'}}
                >
                    <GridFormField xs={12} name="nom" disabled readOnly/>
                    <GridFormField xs={12} name="nif" disabled readOnly/>
                    <GridFormField xs={12} name="email" disabled readOnly/>
                    <GridFormField xs={12} name="emailAlternatiu"/>

                    <GridFormField xs={12} name="rols"
                                   value={user?.rols}
                                   disabled readOnly multiple/>
                    <GridFormField xs={12} name="idioma" disabled readOnly/>
                </CardData>

                <CardData title={'Envio de correos'}>
                    <GridFormField xs={12} name="rebreEmailsAgrupats"/>
                    <GridFormField xs={12} name="rebreAvisosNovesAnotacions"/>
                </CardData>

                <CardData title={'Configuración genérica'}>
                    <GridFormField xs={12} name="numElementsPagina"/>
                    <GridFormField xs={12} name="entitatPerDefecte"/>
                    <GridFormField xs={12} name="procediment"/>
                </CardData>

                <CardData title={'Configuración de columnas del listado de expedientes'}>
                    <GridFormField xs={12} name="expedientListDataDarrerEnviament"/>
                    <GridFormField xs={12} name="expedientListAgafatPer"/>
                    <GridFormField xs={12} name="expedientListInteressats"/>
                    <GridFormField xs={12} name="expedientListComentaris"/>
                    <GridFormField xs={12} name="expedientListGrup"/>
                </CardData>

                <CardData title={'Configuración vista de documentos de expedientes'}>
                    <GridFormField xs={12} name="vistaActual" required/>
                    <GridFormField xs={12} name="expedientExpandit"/>
                </CardData>

                <CardData title={'Configuración vista destino al mover documentos'}>
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