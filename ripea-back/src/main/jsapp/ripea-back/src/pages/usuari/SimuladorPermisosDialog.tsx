import {useMemo, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {FormApi, MuiDialog, MuiForm, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {
    Alert,
    Box,
    Button,
    Chip,
    Collapse,
    Divider,
    Grid,
    Icon,
    LinearProgress,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    Typography,
} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {objecteText, Origen, PermisDetall, Permisos} from "./PermisosUsuariDialog.tsx";

const ROLS_AMB_ORGAN = ['IPA_ORGAN_ADMIN', 'IPA_DISSENY'];

type Estat = 'OK' | 'KO' | 'NO_APLICA' | 'AVIS';

type Comprovacio = {
    codi: string,
    estat: Estat,
    nombre?: number,
    nombreObjectes?: number,
    parametres?: Record<string, string>,
    permisos?: PermisDetall[],
}

type Resultat = {
    usuariCodi: string,
    rol: string,
    recurs: 'EXPEDIENT' | 'ANOTACIO',
    entitatNom: string,
    organNom?: string,
    elementId?: number,
    elementDescripcio?: string,
    ambElement: boolean,
    totalReal: number,
    errorConsulta?: string,
    discrepancia: boolean,
    requisits: Comprovacio[],
    vies: Comprovacio[],
    restriccions: Comprovacio[],
}

/**
 * Formulari del simulador. L'entitat surt de l'element triat; sense element s'ha d'indicar. L'òrgan
 * només es demana per als rols que treballen amb òrgan. En canviar un camp es buiden els que en depenen.
 */
const SimuladorForm = ({usuariId}: { usuariId: string }) => {
    const {data, apiRef} = useFormContext();
    const recurs = data?.recurs;
    const element = recurs === 'EXPEDIENT' ? data?.expedient : recurs === 'ANOTACIO' ? data?.anotacio : null;
    const rolAmbOrgan = ROLS_AMB_ORGAN.includes(data?.rol);

    // Paràmetres de les opcions dels desplegables (memoritzats: FormFieldEnum recarrega si canvia la identitat)
    const usuariParams = useMemo(() => ({usuari: usuariId}), [usuariId]);
    const organParams = useMemo(() => ({
        usuari: usuariId,
        rol: data?.rol,
        recurs,
        expedient: data?.expedient?.id,
        anotacio: data?.anotacio?.id,
        entitat: element ? undefined : data?.entitat,
    }), [usuariId, data?.rol, recurs, data?.expedient?.id, data?.anotacio?.id, data?.entitat, element]);

    const buidar = (...camps: string[]) => camps.forEach((camp) => apiRef?.current?.setFieldValue?.(camp, null));

    return <Grid container direction="row" columnSpacing={1} rowSpacing={1}>
        <GridFormField name="rol" size={{xs: 12, md: 6}} required requestParams={usuariParams} onChange={() => buidar('organ')}/>
        <GridFormField name="recurs" size={{xs: 12, md: 6}} required onChange={() => buidar('expedient', 'anotacio', 'organ')}/>
        <GridFormField name="expedient" hidden={recurs !== 'EXPEDIENT'} onChange={() => buidar('organ')}/>
        <GridFormField name="anotacio" hidden={recurs !== 'ANOTACIO'} onChange={() => buidar('organ')}/>
        <GridFormField name="entitat" hidden={!recurs || !!element} required={!element} requestParams={usuariParams} onChange={() => buidar('organ')}/>
        <GridFormField name="organ" hidden={!rolAmbOrgan || (!element && !data?.entitat)} required={rolAmbOrgan} requestParams={organParams}/>
    </Grid>
}

const ESTAT_ICONA: Record<Estat, { icon: string, color: any }> = {
    OK: {icon: 'check_circle', color: 'success'},
    KO: {icon: 'cancel', color: 'error'},
    NO_APLICA: {icon: 'remove_circle_outline', color: 'default'},
    AVIS: {icon: 'warning', color: 'warning'},
};

/** Estat amb icona i text (no només color, per accessibilitat). */
const EstatChip = ({estat, seccio}: { estat: Estat, seccio: 'requisits' | 'vies' | 'restriccions' }) => {
    const {t} = useTranslation();
    const {icon, color} = ESTAT_ICONA[estat];
    return <Chip size="small" color={color} variant={estat === 'NO_APLICA' ? 'outlined' : 'filled'}
                 icon={<Icon>{icon}</Icon>}
                 label={t(`page.usuari.simulador.estat.${seccio}.${estat}`)}
                 sx={{minWidth: 130, justifyContent: 'flex-start'}}/>
}

const PARAMETRES_BOOLEANS = ['comu', 'permisDirecte', 'gestioGrups'];

const ComprovacioFila = ({comprovacio, seccio, recurs, ambElement, onRegenerarOrganpare}: {
    comprovacio: Comprovacio, seccio: 'requisits' | 'vies' | 'restriccions', recurs: string, ambElement: boolean,
    onRegenerarOrganpare?: () => void
}) => {
    const {t} = useTranslation();
    const [obert, setObert] = useState(false);
    const clau = `page.usuari.simulador.comprovacio.${comprovacio.codi}`;
    const parametres = Object.entries(comprovacio.parametres ?? {}).filter(([nom]) => nom !== 'rol');
    const permisos = comprovacio.permisos ?? [];

    return <Box component="li" sx={{listStyle: 'none', py: 1}}>
        <Box sx={{display: 'flex', gap: 2, alignItems: 'flex-start', flexWrap: {xs: 'wrap', md: 'nowrap'}}}>
            <EstatChip estat={comprovacio.estat} seccio={seccio}/>
            <Box sx={{flex: 1, minWidth: 0}}>
                <Typography component="h4" variant="subtitle2">{t(`${clau}.titol`)}</Typography>
                <Typography variant="body2" color="text.secondary">
                    {comprovacio.estat === 'NO_APLICA'
                        ? t('page.usuari.simulador.noAplica', {rol: t(`enum.rol.${comprovacio.parametres?.rol}`, {defaultValue: comprovacio.parametres?.rol ?? ''})})
                        : t(`${clau}.descripcio`)}
                </Typography>
                {comprovacio.estat !== 'NO_APLICA' && parametres.length > 0 &&
                    <Box sx={{display: 'flex', flexWrap: 'wrap', columnGap: 2, mt: 0.5}}>
                        {parametres.map(([nom, valor]) =>
                            <Typography key={nom} variant="body2">
                                <strong>{t(`page.usuari.simulador.parametre.${nom}`, {defaultValue: nom})}:</strong>{' '}
                                {PARAMETRES_BOOLEANS.includes(nom)
                                    ? t(`enum.siNO.${valor}`)
                                    : nom === 'permis' ? t(`page.usuari.permisos.permis.${valor}`, {defaultValue: valor}) : valor}
                            </Typography>)}
                    </Box>}
                {comprovacio.estat !== 'NO_APLICA' && (comprovacio.nombreObjectes != null || (!ambElement && comprovacio.nombre != null)) &&
                    <Typography variant="body2" sx={{mt: 0.5}}>
                        {!ambElement && comprovacio.nombre != null && t(`page.usuari.simulador.nombre.${recurs}`, {num: comprovacio.nombre})}
                        {!ambElement && comprovacio.nombre != null && comprovacio.nombreObjectes != null && ' · '}
                        {comprovacio.nombreObjectes != null && t('page.usuari.simulador.nombreObjectes', {num: comprovacio.nombreObjectes})}
                    </Typography>}
                {(comprovacio.estat === 'KO' || comprovacio.estat === 'AVIS') &&
                    <Typography variant="body2" sx={{mt: 0.5, display: 'flex', alignItems: 'center', gap: 0.5}}>
                        <Icon fontSize="small" color="primary" aria-hidden>lightbulb</Icon>
                        {t(`${clau}.suggeriment`)}
                    </Typography>}
                {comprovacio.codi === 'EXPEDIENT_ORGANPARE' && comprovacio.estat === 'AVIS' && onRegenerarOrganpare &&
                    <Button size="small" variant="outlined" sx={{mt: 1}} startIcon={<Icon>build</Icon>} onClick={onRegenerarOrganpare}>
                        {t('page.usuari.simulador.organpare.boto')}
                    </Button>}
            </Box>
            {comprovacio.estat !== 'NO_APLICA' && permisos.length > 0 &&
                <Button size="small" variant="text" onClick={() => setObert(!obert)} aria-expanded={obert}
                        endIcon={<Icon>{obert ? 'expand_less' : 'expand_more'}</Icon>}>
                    {t('page.usuari.simulador.veurePermisos', {num: permisos.length})}
                </Button>}
        </Box>
        {permisos.length > 0 &&
            <Collapse in={obert} unmountOnExit>
                <Table size="small" aria-label={t(`${clau}.titol`)} sx={{mt: 1, tableLayout: 'fixed', '& td, & th': {overflowWrap: 'anywhere'}}}>
                    <TableHead>
                        <TableRow>
                            <TableCell>{t('page.usuari.permisos.columna.objecte')}</TableCell>
                            <TableCell sx={{width: '25%'}}>{t('page.usuari.permisos.columna.origen')}</TableCell>
                            <TableCell sx={{width: '35%'}}>{t('page.usuari.permisos.columna.permisos')}</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {permisos.map((permis) =>
                            <TableRow key={permis.id}>
                                <TableCell>{objecteText(permis)}{permis.organ ? ` (${permis.organ})` : ''}</TableCell>
                                <TableCell><Origen permis={permis}/></TableCell>
                                <TableCell><Permisos permisos={permis.permisos}/></TableCell>
                            </TableRow>)}
                    </TableBody>
                </Table>
            </Collapse>}
    </Box>
}

const Seccio = ({titol, ajuda, comprovacions, seccio, resultat, onRegenerarOrganpare}: {
    titol: string, ajuda: string, comprovacions: Comprovacio[], seccio: 'requisits' | 'vies' | 'restriccions', resultat: Resultat,
    onRegenerarOrganpare?: () => void
}) => {
    if (!comprovacions?.length) return null;
    return <Paper variant="outlined" sx={{p: 2, mb: 2}}>
        <Typography component="h3" variant="h6">{titol}</Typography>
        <Typography variant="body2" color="text.secondary">{ajuda}</Typography>
        <Box component="ul" sx={{m: 0, p: 0}}>
            {comprovacions.map((comprovacio, index) => <Box key={comprovacio.codi}>
                {index > 0 && <Divider component="div" role="presentation"/>}
                <ComprovacioFila comprovacio={comprovacio} seccio={seccio} recurs={resultat.recurs} ambElement={resultat.ambElement}
                                 onRegenerarOrganpare={onRegenerarOrganpare}/>
            </Box>)}
        </Box>
    </Paper>
}

const SimulacioResultat = ({resultat, onRegenerarOrganpare}: { resultat: Resultat, onRegenerarOrganpare?: () => void }) => {
    const {t} = useTranslation();
    const rol = t(`enum.rol.${resultat.rol}`, {defaultValue: resultat.rol});
    const recurs = t(`page.usuari.simulador.recurs.${resultat.recurs}`);
    const visible = resultat.totalReal > 0;

    return <Box component="section" aria-live="polite" sx={{mt: 2}}>
        {resultat.errorConsulta
            ? <Alert severity="error" sx={{mb: 2}}>{t('page.usuari.simulador.veredicte.error', {error: resultat.errorConsulta})}</Alert>
            : resultat.ambElement
                ? <Alert severity={visible ? 'success' : 'error'} icon={<Icon>{visible ? 'visibility' : 'visibility_off'}</Icon>} sx={{mb: 2}}>
                    <Typography component="h3" variant="subtitle1" sx={{fontWeight: 600}}>
                        {t(visible ? 'page.usuari.simulador.veredicte.visible' : 'page.usuari.simulador.veredicte.noVisible', {
                            usuari: resultat.usuariCodi, rol, recurs, element: resultat.elementDescripcio,
                        })}
                    </Typography>
                </Alert>
                : <Alert severity="info" sx={{mb: 2}}>
                    <Typography component="h3" variant="subtitle1" sx={{fontWeight: 600}}>
                        {t(`page.usuari.simulador.veredicte.total.${resultat.recurs}`, {
                            usuari: resultat.usuariCodi, rol, num: resultat.totalReal, entitat: resultat.entitatNom,
                        })}
                    </Typography>
                </Alert>}
        {resultat.discrepancia && <Alert severity="warning" sx={{mb: 2}}>{t('page.usuari.simulador.discrepancia')}</Alert>}
        <Typography variant="body2" sx={{mb: 2}}>
            <strong>{t('page.usuari.simulador.parametre.entitat')}:</strong> {resultat.entitatNom}
            {resultat.organNom && <> · <strong>{t('page.usuari.simulador.parametre.organ')}:</strong> {resultat.organNom}</>}
        </Typography>
        <Seccio seccio="requisits" resultat={resultat} comprovacions={resultat.requisits} onRegenerarOrganpare={onRegenerarOrganpare}
                titol={t('page.usuari.simulador.seccio.requisits.titol')} ajuda={t('page.usuari.simulador.seccio.requisits.ajuda')}/>
        <Seccio seccio="vies" resultat={resultat} comprovacions={resultat.vies}
                titol={t('page.usuari.simulador.seccio.vies.titol')} ajuda={t('page.usuari.simulador.seccio.vies.ajuda')}/>
        <Seccio seccio="restriccions" resultat={resultat} comprovacions={resultat.restriccions}
                titol={t('page.usuari.simulador.seccio.restriccions.titol')} ajuda={t('page.usuari.simulador.seccio.restriccions.ajuda')}/>
    </Box>
}

export const useSimuladorPermisosDialog = () => {
    const {t} = useTranslation();
    const {temporalMessageShow, messageDialogShow} = useBaseAppContext();
    const {artifactReport, artifactAction} = useResourceApiService('usuariResource');
    const formApiRef = useRef<FormApi>(null);
    const [usuari, setUsuari] = useState<any>();
    const [resultat, setResultat] = useState<Resultat>();
    const [simulant, setSimulant] = useState(false);

    const handleOpen = (_id: any, row: any) => {
        setResultat(undefined);
        setUsuari(row);
    }
    const handleClose = () => {
        setUsuari(undefined);
        setResultat(undefined);
    }

    const simular = () => {
        const data = formApiRef.current?.getData?.();
        setSimulant(true);
        setResultat(undefined);
        artifactReport(usuari.id, {code: 'SIMULAR_PERMISOS', data})
            .then((result: any) => setResultat(result?.[0]))
            .catch((error: any) => {
                formApiRef.current?.handleSubmissionErrors?.(error);
                if (error?.message) temporalMessageShow(null, error.message, 'error');
            })
            .finally(() => setSimulant(false));
    }

    /** Regenera la cadena d'òrgans (organpare) de l'expedient simulat i torna a simular. */
    const regenerarOrganpare = () => {
        if (!resultat?.elementId) return;
        messageDialogShow(
            t('page.usuari.simulador.organpare.confirmTitol'),
            t('page.usuari.simulador.organpare.confirm', {element: resultat.elementDescripcio}),
            [
                {value: true, text: t('page.usuari.simulador.organpare.boto'), componentProps: {variant: 'contained'}},
                {value: false, text: t('common.cancel'), componentProps: {variant: 'outlined'}},
            ],
            {maxWidth: 'sm', fullWidth: true})
            .then((value: any) => {
                if (!value) return;
                artifactAction(usuari.id, {code: 'REGENERAR_ORGANPARE', data: {expedientId: resultat.elementId}})
                    .then(() => {
                        temporalMessageShow(null, t('page.usuari.simulador.organpare.ok'), 'success');
                        simular();
                    })
                    .catch((error: any) => temporalMessageShow(null, error?.message, 'error'));
            });
    }

    const dialog = <MuiDialog
        open={!!usuari}
        closeCallback={handleClose}
        title={t('page.usuari.simulador.title', {usuari: usuari?.codiAndNom ?? usuari?.codi ?? ''})}
        componentProps={{fullWidth: true, maxWidth: 'lg'}}
        buttons={[
            {value: 'simular', text: t('page.usuari.simulador.simular'), icon: 'play_arrow', componentProps: {variant: 'contained', disabled: simulant}},
            {value: 'close', text: t('common.close'), componentProps: {variant: 'outlined'}},
        ]}
        buttonCallback={(value: any) => value === 'simular' ? simular() : handleClose()}
    >
        {usuari && <>
            <Alert severity="info" sx={{mb: 2}}>{t('page.usuari.simulador.ajuda')}</Alert>
            <MuiForm
                key={usuari.id}
                resourceName="usuariResource"
                resourceType="REPORT"
                resourceTypeCode="SIMULAR_PERMISOS"
                id={usuari.id}
                apiRef={formApiRef}
                hiddenToolbar
            >
                <SimuladorForm usuariId={usuari.id}/>
            </MuiForm>
            {simulant && <LinearProgress sx={{mt: 2}} aria-label={t('common.processing')}/>}
            {resultat && <SimulacioResultat resultat={resultat} onRegenerarOrganpare={regenerarOrganpare}/>}
        </>}
    </MuiDialog>

    return {handleOpen, dialog}
}
