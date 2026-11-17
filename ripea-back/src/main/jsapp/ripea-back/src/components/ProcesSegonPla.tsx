import {useCallback, useEffect, useId, useState} from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";
import {
    Alert,
    AlertTitle,
    Box,
    Button,
    CircularProgress,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Icon,
    IconButton,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Typography,
} from "@mui/material";
import {useResourceApiContext} from "reactlib";
import {useUserSession} from "./Session.tsx";
import {icons} from "../util/icons.ts";

/**
 * Seguiment dels processos en segon pla del servidor (p.ex. el tancament d'un expedient).
 *
 * L'acció que llança el procés retorna un ProcesSegonPla amb l'operacioId; a partir d'aquí:
 *  - useProcesSegonPlaDialog / ProcesSegonPlaDialog mostren el registre de passos en temps real a l'usuari que
 *    l'ha llançat (consulta periòdica a /api/procesSegonPla/{operacioId}).
 *  - ProcesSegonPlaAlert explica a qualsevol usuari que l'element està bloquejat per un procés en curs (el
 *    resum arriba al recurs, p.ex. expedient.procesSegonPla).
 *
 * Els textos els posa el front a partir dels codis del servidor (procesSegonPla.* a les traduccions):
 *  - procesSegonPla.tipus.<TIPUS>.{titol,ok,error,bloqueig,bloqueigPropi}
 *  - procesSegonPla.pas.<codi>, procesSegonPla.fase.<fase> i procesSegonPla.resultat.<resultat>, amb els seus paràmetres.
 */

export type ProcesSegonPlaEstat = 'EN_CURS' | 'FINALITZAT' | 'ERROR';
export type ProcesSegonPlaPasEstat = 'EN_CURS' | 'OK' | 'AVIS' | 'ERROR';

export type ProcesSegonPlaPas = {
    codi: string;
    params?: Record<string, string>;
    estat: ProcesSegonPlaPasEstat;
    fase?: string | null;
    faseParams?: Record<string, string>;
    detall?: string | null;
    resultat?: string | null;
    resultatParams?: Record<string, string>;
};

export type ProcesSegonPla = {
    operacioId: string;
    tipus: string;
    elementTipus: string;
    elementId: number;
    usuariCodi: string;
    usuariNom?: string;
    estat: ProcesSegonPlaEstat;
    errorMsg?: string | null;
    passos?: ProcesSegonPlaPas[];
};

/** Cada quant es consulta el progrés mentre el procés és en curs. */
const INTERVAL_CONSULTA_MS = 1000;
/** Errors de xarxa seguits que es toleren abans de deixar de consultar. */
const MAX_ERRORS_CONSULTA = 5;

// Text només per a lectors de pantalla (mateix patró que el visuallyHidden de MUI).
const visuallyHiddenSx = {
    border: 0,
    clip: 'rect(0 0 0 0)',
    height: '1px',
    margin: '-1px',
    overflow: 'hidden',
    padding: 0,
    position: 'absolute',
    whiteSpace: 'nowrap',
    width: '1px',
} as const;

/**
 * Consulta periòdicament l'estat d'un procés fins que acaba. Amb operacioId buit no consulta res.
 * error=true vol dir que el procés ja no es pot consultar (descartat de memòria, reinici del servidor...).
 */
export const useProcesSegonPla = (operacioId?: string | null) => {
    const {apiUrl} = useResourceApiContext();
    const [proces, setProces] = useState<ProcesSegonPla>();
    const [error, setError] = useState<boolean>(false);

    useEffect(() => {
        setProces(undefined);
        setError(false);
        if (!operacioId) {
            return;
        }
        let actiu = true;
        let errorsSeguits = 0;
        let timer: ReturnType<typeof setTimeout> | undefined;
        const programar = () => {
            timer = setTimeout(consultar, INTERVAL_CONSULTA_MS);
        };
        const consultar = () => {
            axios.get(`${apiUrl}procesSegonPla/${operacioId}`)
                .then((response) => {
                    if (!actiu) return;
                    errorsSeguits = 0;
                    setProces(response.data);
                    if (response.data?.estat === 'EN_CURS') {
                        programar();
                    }
                })
                .catch((e) => {
                    if (!actiu) return;
                    errorsSeguits++;
                    // 404: no existeix o ja s'ha descartat; no té sentit tornar-ho a provar
                    if (e?.response?.status === 404 || errorsSeguits >= MAX_ERRORS_CONSULTA) {
                        setError(true);
                    } else {
                        programar();
                    }
                });
        };
        consultar();
        return () => {
            actiu = false;
            if (timer) clearTimeout(timer);
        };
    }, [apiUrl, operacioId]);

    return {proces, error};
};

const PasIcon = ({estat}: { estat: ProcesSegonPlaPasEstat }) => {
    switch (estat) {
        case 'EN_CURS':
            return <CircularProgress size={20} aria-hidden/>;
        case 'OK':
            return <Icon color="success">{icons.correcte}</Icon>;
        case 'AVIS':
            return <Icon color="warning">{icons.advertencia}</Icon>;
        default:
            return <Icon color="error">{icons.error}</Icon>;
    }
};

const ProcesSegonPlaPasItem = ({pas}: { pas: ProcesSegonPlaPas }) => {
    const {t} = useTranslation();
    // El resultat (p.ex. "3 notificacions actualitzades") va a continuació del text del pas
    const text = pas.resultat
        ? `${t(`procesSegonPla.pas.${pas.codi}`, pas.params ?? {})}: ${t(`procesSegonPla.resultat.${pas.resultat}`, pas.resultatParams ?? {})}`
        : t(`procesSegonPla.pas.${pas.codi}`, pas.params ?? {});
    // Mentre és en curs es mostra què s'està fent; un cop acabat, el detall (normalment el missatge d'error)
    const secundari = pas.estat === 'EN_CURS' && pas.fase
        ? t(`procesSegonPla.fase.${pas.fase}`, pas.faseParams ?? {})
        : pas.detall;
    return <ListItem disableGutters alignItems="flex-start">
        <ListItemIcon sx={{minWidth: 36, mt: 0.5}}>
            <PasIcon estat={pas.estat}/>
        </ListItemIcon>
        <ListItemText
            primary={<>
                {text}
                <Box component="span" sx={visuallyHiddenSx}>{` (${t(`procesSegonPla.estatPas.${pas.estat}`)})`}</Box>
            </>}
            secondary={secundari}
            slotProps={{secondary: {sx: {whiteSpace: 'pre-line', wordBreak: 'break-word'}}}}
        />
    </ListItem>;
};

type ProcesSegonPlaDialogProps = {
    operacioId?: string | null;
    open: boolean;
    onClose: (proces?: ProcesSegonPla) => void;
};

/**
 * Diàleg amb el registre de passos d'un procés en segon pla. Es pot tancar en qualsevol moment: el procés
 * continua al servidor i es pot tornar a obrir (ProcesSegonPlaAlert).
 */
export const ProcesSegonPlaDialog = (props: ProcesSegonPlaDialogProps) => {
    const {operacioId, open, onClose} = props;
    const {t} = useTranslation();
    const titolId = useId();
    const {proces, error} = useProcesSegonPla(open ? operacioId : null);
    const enCurs = !error && (proces == null || proces.estat === 'EN_CURS');
    const titol = proces?.tipus ? t(`procesSegonPla.tipus.${proces.tipus}.titol`) : t('procesSegonPla.titol');
    const passos = proces?.passos ?? [];

    return <Dialog
        open={open}
        onClose={(_event, reason) => reason !== 'backdropClick' && onClose(proces)}
        fullWidth
        maxWidth="md"
        aria-labelledby={titolId}
    >
        {/* Mateix botó de tancar que el Dialog de base-react */}
        <DialogTitle id={titolId} sx={{pr: 6}}>{titol}</DialogTitle>
        <IconButton
            aria-label={t('procesSegonPla.surt')}
            title={t('procesSegonPla.surt')}
            onClick={() => onClose(proces)}
            size="small"
            sx={(theme) => ({
                position: 'absolute',
                right: 8,
                top: 8,
                color: theme.palette.grey[500],
            })}
        >
            <Icon fontSize="small">{icons.cancela}</Icon>
        </IconButton>
        <DialogContent>
            <Box role="log" aria-live="polite" aria-relevant="additions" aria-busy={enCurs} aria-label={t('procesSegonPla.registre')}>
                {enCurs && passos.length === 0 &&
                    <Box display="flex" alignItems="center" gap={1.5} py={1}>
                        <CircularProgress size={20} aria-hidden/>
                        <Typography component="p">{t('procesSegonPla.iniciant')}</Typography>
                    </Box>}
                {passos.length > 0 &&
                    <List dense disablePadding>
                        {passos.map((pas, index) => <ProcesSegonPlaPasItem key={index} pas={pas}/>)}
                    </List>}
            </Box>
            <Box mt={2}>
                {error &&
                    <Alert severity="warning">{t('procesSegonPla.noDisponible')}</Alert>}
                {!error && enCurs &&
                    <Alert severity="info">{t('procesSegonPla.infoSegonPla')}</Alert>}
                {!error && proces?.estat === 'FINALITZAT' &&
                    <Alert severity="success">{t(`procesSegonPla.tipus.${proces.tipus}.ok`)}</Alert>}
                {!error && proces?.estat === 'ERROR' &&
                    <Alert severity="error">
                        <AlertTitle>{t(`procesSegonPla.tipus.${proces.tipus}.error`)}</AlertTitle>
                        {proces.errorMsg}
                    </Alert>}
            </Box>
        </DialogContent>
        <DialogActions>
            {/* "Surt" i no "Tanca", per no confondre-ho amb l'acció de tancar l'expedient */}
            <Button variant="outlined" color="primary" onClick={() => onClose(proces)}>
                {t('procesSegonPla.surt')}
            </Button>
        </DialogActions>
    </Dialog>;
};

/**
 * Diàleg de progrés llest per usar: show(operacioId) l'obre i onClose rep l'estat del procés en tancar-lo
 * (que pot ser encara EN_CURS si l'usuari el tanca abans que acabi).
 */
export const useProcesSegonPlaDialog = (onClose?: (proces?: ProcesSegonPla) => void) => {
    const [operacioId, setOperacioId] = useState<string | null>(null);
    const show = useCallback((id: string) => setOperacioId(id), []);
    const handleClose = (proces?: ProcesSegonPla) => {
        setOperacioId(null);
        onClose?.(proces);
    };
    return {
        show,
        dialog: <ProcesSegonPlaDialog open={operacioId != null} operacioId={operacioId} onClose={handleClose}/>,
    };
};

/**
 * Avís que l'element està bloquejat per un procés en segon pla en curs i de qui és. A l'usuari que l'ha llançat
 * li ofereix tornar a obrir el diàleg de progrés.
 */
export const ProcesSegonPlaAlert = (props: { proces?: ProcesSegonPla | null, onShowProgress?: (operacioId: string) => void }) => {
    const {proces, onShowProgress} = props;
    const {t} = useTranslation();
    const {value: user} = useUserSession();
    if (!proces) {
        return null;
    }
    const propi = proces.usuariCodi === user?.codi;
    return <Alert
        severity="warning"
        action={propi && onShowProgress
            ? <Button sx={{py: 0}} variant="outlined" onClick={() => onShowProgress(proces.operacioId)}>
                <Icon>{icons.mostra}</Icon>
                <Typography variant={'subtitle2'} component="span">{t('procesSegonPla.veureProgres')}</Typography>
            </Button>
            : undefined}
    >
        {propi
            ? t(`procesSegonPla.tipus.${proces.tipus}.bloqueigPropi`)
            : t(`procesSegonPla.tipus.${proces.tipus}.bloqueig`, {usuari: proces.usuariNom ?? proces.usuariCodi})}
    </Alert>;
};
