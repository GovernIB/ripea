import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {
    Accordion,
    AccordionDetails,
    AccordionSummary,
    Alert,
    Box,
    Chip,
    Icon,
    IconButton,
    LinearProgress,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Tooltip,
    Typography,
} from "@mui/material";
import {formatDate} from "../../util/dateUtils.ts";

// Ordre de les taules dins una entitat (el mateix ordre amb què el backend ordena les files)
const TIPUS_ORDRE = ['ENTITY', 'GRUP', 'ORGAN', 'MET_EXP_ORG', 'MET_NOD'];

type PermisDetall = {
    id: string,
    tipus: string,
    objectId: number,
    objecteCodi?: string,
    objecteNom?: string,
    organ?: string,
    sidId: number,
    sid: string,
    principal: 'USUARI' | 'ROL',
    permisos: string[],
    revocable: boolean,
}

const usePermisosApi = (usuariId?: string) => {
    const {isReady, artifactReport: apiReport, artifactAction: apiAction} = useResourceApiService('usuariResource');

    const resum = () => apiReport(usuariId, {code: 'PERMISOS_RESUM'})
        .then((result: any) => result?.[0]);
    const detall = (data: { entitatId?: number, orfes?: boolean }) => apiReport(usuariId, {code: 'PERMISOS_DETALL', data})
        .then((result: any) => (result ?? []) as PermisDetall[]);
    const revocar = (permis: PermisDetall) => apiAction(usuariId, {
        code: 'REVOCAR_PERMIS',
        data: {tipus: permis.tipus, objectId: permis.objectId, sidId: permis.sidId},
    });

    return {isReady, resum, detall, revocar}
}

const Origen = ({permis}: { permis: PermisDetall }) => {
    const {t} = useTranslation();
    return permis.principal === 'USUARI'
        ? <Chip size="small" icon={<Icon>person</Icon>} label={t('page.usuari.permisos.origen.directe')}/>
        : <Chip size="small" variant="outlined" icon={<Icon>groups</Icon>}
                label={t('page.usuari.permisos.origen.rol', {rol: t(`enum.rol.${permis.sid}`, {defaultValue: permis.sid})})}/>
}

const Permisos = ({permisos}: { permisos: string[] }) => {
    const {t} = useTranslation();
    return <Box sx={{display: 'flex', flexWrap: 'wrap', gap: 0.5}}>
        {permisos?.map((permis) =>
            <Chip key={permis} size="small" color="primary" variant="outlined"
                  label={t(`page.usuari.permisos.permis.${permis}`, {defaultValue: permis})}/>)}
    </Box>
}

const RevocarButton = ({permis, onRevocar}: { permis: PermisDetall, onRevocar: (permis: PermisDetall) => void }) => {
    const {t} = useTranslation();
    if (!permis.revocable) {
        return <Tooltip title={t('page.usuari.permisos.revocar.noRevocable')}>
            <Icon color="disabled" aria-label={t('page.usuari.permisos.revocar.noRevocable')}>lock</Icon>
        </Tooltip>
    }
    return <Tooltip title={t('page.usuari.permisos.revocar.label')}>
        <IconButton size="small" color="error" aria-label={t('page.usuari.permisos.revocar.label')} onClick={() => onRevocar(permis)}>
            <Icon>remove_circle_outline</Icon>
        </IconButton>
    </Tooltip>
}

const objecteText = (permis: PermisDetall) =>
    [permis.objecteCodi, permis.objecteNom].filter(Boolean).join(' - ') || `#${permis.objectId}`;

// Amplades fixes (table-layout: fixed) perquè Origen, Permisos i Accions quedin alineades a totes les
// taules. Les columnes opcionals (Tipus, Òrgan) només resten espai a la columna Objecte.
const AMPLADA = {tipus: '14%', organ: '20%', origen: '20%', permisos: '30%', accions: 80};

/** Taula de permisos d'un tipus d'objecte. */
const PermisosTaula = ({tipus, permisos, orfes, onRevocar}: {
    tipus?: string, permisos: PermisDetall[], orfes?: boolean, onRevocar: (permis: PermisDetall) => void
}) => {
    const {t} = useTranslation();
    const ambOrgan = tipus === 'MET_EXP_ORG';
    const titol = orfes ? t('page.usuari.permisos.orfes.title') : t(`page.usuari.permisos.tipus.${tipus}`);

    return <TableContainer sx={{mb: 2}}>
        <Table size="small" aria-label={titol} sx={{tableLayout: 'fixed', '& td, & th': {overflowWrap: 'anywhere'}}}>
            {!orfes && <caption style={{captionSide: 'top', padding: '4px 0'}}>
                <Typography variant="subtitle2" component="span">{titol} ({permisos.length})</Typography>
            </caption>}
            <TableHead>
                <TableRow>
                    {orfes && <TableCell sx={{width: AMPLADA.tipus}}>{t('page.usuari.permisos.columna.tipus')}</TableCell>}
                    <TableCell>{t('page.usuari.permisos.columna.objecte')}</TableCell>
                    {ambOrgan && <TableCell sx={{width: AMPLADA.organ}}>{t('page.usuari.permisos.columna.organ')}</TableCell>}
                    <TableCell sx={{width: AMPLADA.origen}}>{t('page.usuari.permisos.columna.origen')}</TableCell>
                    <TableCell sx={{width: AMPLADA.permisos}}>{t('page.usuari.permisos.columna.permisos')}</TableCell>
                    <TableCell align="center" sx={{width: AMPLADA.accions}}>{t('common.action')}</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {permisos.map((permis) =>
                    <TableRow key={permis.id} hover>
                        {orfes && <TableCell>{t(`page.usuari.permisos.tipus.${permis.tipus}`)}</TableCell>}
                        <TableCell>{objecteText(permis)}</TableCell>
                        {ambOrgan && <TableCell>{permis.organ}</TableCell>}
                        <TableCell><Origen permis={permis}/></TableCell>
                        <TableCell><Permisos permisos={permis.permisos}/></TableCell>
                        <TableCell align="center"><RevocarButton permis={permis} onRevocar={onRevocar}/></TableCell>
                    </TableRow>)}
            </TableBody>
        </Table>
    </TableContainer>
}

/**
 * Detall de permisos d'una entitat (o dels objectes inexistents). Es munta en desplegar l'acordió,
 * de manera que la consulta només es fa quan es demana. {@code version} força la recàrrega després de revocar.
 */
const PermisosDetall = ({detall, entitatId, orfes, version, onRevocar}: {
    detall: (data: { entitatId?: number, orfes?: boolean }) => Promise<PermisDetall[]>,
    entitatId?: number, orfes?: boolean, version: number, onRevocar: (permis: PermisDetall) => void
}) => {
    const {t} = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();
    const [permisos, setPermisos] = useState<PermisDetall[]>();

    useEffect(() => {
        detall({entitatId, orfes})
            .then(setPermisos)
            .catch((error: any) => {
                setPermisos([]);
                temporalMessageShow(null, error?.message, 'error');
            });
    }, [entitatId, orfes, version]);

    if (permisos == null) {
        return <LinearProgress aria-label={t('common.processing')}/>
    }
    if (permisos.length === 0) {
        return <Typography variant="body2" color="text.secondary">{t('page.usuari.permisos.buit')}</Typography>
    }
    if (orfes) {
        return <PermisosTaula orfes permisos={permisos} onRevocar={onRevocar}/>
    }
    return <>
        {TIPUS_ORDRE
            .map((tipus) => ({tipus, files: permisos.filter((p) => p.tipus === tipus)}))
            .filter(({files}) => files.length > 0)
            .map(({tipus, files}) => <PermisosTaula key={tipus} tipus={tipus} permisos={files} onRevocar={onRevocar}/>)}
    </>
}

const EntitatIcones = ({entitat}: { entitat: any }) => {
    const {t} = useTranslation();
    const icones = [
        {actiu: entitat.administrador, icon: 'admin_panel_settings', title: t('page.usuari.permisos.entitat.administrador')},
        {actiu: entitat.administradorLectura, icon: 'manage_search', title: t('page.usuari.permisos.entitat.administradorLectura')},
        {actiu: entitat.usuari, icon: 'person', title: t('page.usuari.permisos.entitat.usuari')},
    ].filter((i) => i.actiu);

    return <>{icones.map((i) =>
        <Tooltip key={i.icon} title={i.title}>
            <Icon color="primary" fontSize="small" aria-label={i.title} role="img">{i.icon}</Icon>
        </Tooltip>)}</>
}

const Capcalera = ({usuari, resum}: { usuari: any, resum?: any }) => {
    const {t} = useTranslation();
    return <Box sx={{mb: 2}}>
        <Stack direction="row" spacing={3} useFlexGap sx={{flexWrap: 'wrap', mb: 1}}>
            <Typography><strong>{t('page.usuari.permisos.capcalera.codi')}:</strong> {usuari?.codi}</Typography>
            <Typography><strong>{t('page.usuari.permisos.capcalera.nom')}:</strong> {usuari?.nom}</Typography>
            <Typography><strong>{t('page.usuari.permisos.capcalera.nif')}:</strong> {usuari?.nif}</Typography>
            {usuari?.email && <Typography><strong>{t('page.usuari.permisos.capcalera.email')}:</strong> {usuari?.email}</Typography>}
        </Stack>
        {resum?.rols?.length > 0 && <Box sx={{display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: 0.5, mb: 1}}>
            <Typography component="span"><strong>{t('page.usuari.permisos.capcalera.rols')}:</strong></Typography>
            {resum.rols.map((rol: string) =>
                <Chip key={rol} size="small" label={t(`enum.rol.${rol}`, {defaultValue: rol})}/>)}
        </Box>}
        {usuari && !usuari.actiu && <Alert severity="warning" sx={{mb: 1}}>
            {t('page.usuari.baixa.info', {
                data: usuari.baixaData ? formatDate(usuari.baixaData) : '',
                usuari: usuari.baixaUsuari ?? '',
                motiu: usuari.baixaMotiu ?? '',
            })}
        </Alert>}
        {resum?.rolsError && <Alert severity="warning" sx={{mb: 1}}>{t('page.usuari.permisos.rolsError')}</Alert>}
        <Alert severity="info">{t('page.usuari.permisos.ajuda')}</Alert>
    </Box>
}

export const usePermisosUsuariDialog = () => {
    const {t} = useTranslation();
    const {temporalMessageShow, messageDialogShow} = useBaseAppContext();
    const [usuari, setUsuari] = useState<any>();
    const [resum, setResum] = useState<any>();
    // Recàrrega dels detalls oberts després de revocar un permís
    const [version, setVersion] = useState(0);
    const {isReady, resum: apiResum, detall: apiDetall, revocar: apiRevocar} = usePermisosApi(usuari?.id);

    const carregarResum = () => {
        apiResum()
            .then(setResum)
            .catch((error: any) => temporalMessageShow(null, error?.message, 'error'));
    }

    useEffect(() => {
        setResum(undefined);
        if (usuari?.id && isReady) {
            carregarResum();
        }
    }, [usuari?.id, isReady]);

    const handleOpen = (_id: any, row: any) => setUsuari(row);
    const handleClose = () => setUsuari(undefined);

    const revocar = (permis: PermisDetall) => {
        messageDialogShow(
            t('page.usuari.permisos.revocar.title'),
            t('page.usuari.permisos.revocar.confirm', {objecte: objecteText(permis), sid: permis.sid}),
            [
                {value: true, text: t('page.usuari.permisos.revocar.label'), componentProps: {variant: 'contained', color: 'error'}},
                {value: false, text: t('common.cancel'), componentProps: {variant: 'outlined'}},
            ],
            {maxWidth: 'sm', fullWidth: true})
            .then((value: any) => {
                if (!value) return;
                apiRevocar(permis)
                    .then(() => {
                        temporalMessageShow(null, t('page.usuari.permisos.revocar.ok'), 'success');
                        setVersion((v) => v + 1);
                        carregarResum();
                    })
                    .catch((error: any) => temporalMessageShow(null, error?.message, 'error'));
            });
    }

    const dialog = <MuiDialog
        open={!!usuari}
        closeCallback={handleClose}
        title={t('page.usuari.permisos.title', {usuari: usuari?.codiAndNom ?? usuari?.codi ?? ''})}
        componentProps={{fullWidth: true, maxWidth: 'xl'}}
        buttons={[{value: 'close', text: t('common.close'), componentProps: {variant: 'outlined'}}]}
        buttonCallback={handleClose}
    >
        {usuari && <>
            <Capcalera usuari={usuari} resum={resum}/>
            {resum == null
                ? <LinearProgress aria-label={t('common.processing')}/>
                : <>
                    {resum.entitats?.length === 0 && resum.numOrfes === 0 &&
                        <Typography color="text.secondary">{t('page.usuari.permisos.senseEntitats')}</Typography>}
                    {resum.entitats?.map((entitat: any) =>
                        <Accordion key={entitat.entitatId} slotProps={{transition: {unmountOnExit: true}}}>
                            <AccordionSummary expandIcon={<Icon>expand_more</Icon>}>
                                <Box sx={{display: 'flex', alignItems: 'center', gap: 1, flexWrap: 'wrap'}}>
                                    <Typography component="h3" sx={{fontWeight: 500}}>
                                        {entitat.entitatNom} ({entitat.entitatCodi})
                                    </Typography>
                                    <EntitatIcones entitat={entitat}/>
                                    <Chip size="small" label={t('page.usuari.permisos.entitat.numPermisos', {num: entitat.numPermisos})}/>
                                </Box>
                            </AccordionSummary>
                            <AccordionDetails>
                                <PermisosDetall detall={apiDetall} entitatId={entitat.entitatId} version={version} onRevocar={revocar}/>
                            </AccordionDetails>
                        </Accordion>)}
                    {resum.numOrfes > 0 &&
                        <Accordion slotProps={{transition: {unmountOnExit: true}}} sx={{mt: 2}}>
                            <AccordionSummary expandIcon={<Icon>expand_more</Icon>}>
                                <Box sx={{display: 'flex', alignItems: 'center', gap: 1}}>
                                    <Icon color="warning" aria-hidden>delete_sweep</Icon>
                                    <Typography component="h3" sx={{fontWeight: 500}}>
                                        {t('page.usuari.permisos.orfes.title')} ({resum.numOrfes})
                                    </Typography>
                                </Box>
                            </AccordionSummary>
                            <AccordionDetails>
                                <Alert severity="info" sx={{mb: 1}}>{t('page.usuari.permisos.orfes.ajuda')}</Alert>
                                <PermisosDetall detall={apiDetall} orfes version={version} onRevocar={revocar}/>
                            </AccordionDetails>
                        </Accordion>}
                </>}
        </>}
    </MuiDialog>

    return {handleOpen, dialog}
}
