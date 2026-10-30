import React from 'react';
import axios from 'axios';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import '../i18n/i18n';

/**
 * Recuperació de la interfície REACT quan es perd la sessió del servidor.
 *
 * Les crides a l'API s'autentiquen amb la galeta de sessió. Si l'adaptador de Keycloak no pot
 * renovar el token (p. ex. una altra aplicació amb el mateix client ha invalidat el refresh token),
 * la sessió queda sense autenticar i l'API respon amb un 401 (JBossWebSecurityConfig). Llavors
 * n'hi ha prou amb recarregar la pàgina: la navegació passa pel login del contenidor i, si la
 * sessió de Keycloak encara és viva, torna a la mateixa URL sense demanar la contrasenya.
 *
 * Es recarrega una sola vegada: si després de la recàrrega la sessió continua sense arribar
 * (veure INICI_SESSIO_MARGE_MS) es mostra la pantalla de sessió caducada, per no entrar en bucle.
 *
 * La sessió no es comprova periòdicament: cada consulta allargaria la sessió HTTP i una sessió
 * inactiva no arribaria mai a caducar. Només es detecta amb els 401 de les crides que ja es fan i
 * quan cau la connexió SSE.
 */

/**
 * Marca (sessionStorage) del darrer intent de recuperar la sessió. Si en tornar la sessió encara
 * no hi és, no es torna a intentar i es mostra la pantalla de sessió caducada.
 */
const INICI_SESSIO_KEY = 'ripea.sessioCaducada.intent';
const INICI_SESSIO_MARGE_MS = 60000;

let apiBaseUrl: string | undefined;
let fetchOriginal: typeof window.fetch | undefined;
let recarregant = false;
let caducada = false;
const subscriptors = new Set<() => void>();

const llegirMarcaIniciSessio = (): number => {
    try {
        return Number(sessionStorage.getItem(INICI_SESSIO_KEY)) || 0;
    } catch {
        return 0;
    }
};

const escriureMarcaIniciSessio = (valor?: number) => {
    try {
        if (valor == null) {
            sessionStorage.removeItem(INICI_SESSIO_KEY);
        } else {
            sessionStorage.setItem(INICI_SESSIO_KEY, String(valor));
        }
    } catch {
        // Sense sessionStorage no hi ha protecció contra el bucle, però la recàrrega funciona.
    }
};

const esUrlApi = (url?: string) => {
    if (!apiBaseUrl || !url) {
        return false;
    }
    try {
        return new URL(url, window.location.href).href.startsWith(apiBaseUrl);
    } catch {
        return false;
    }
};

const getUrlPeticio = (input: RequestInfo | URL): string =>
    typeof input === 'string' ? input : input instanceof URL ? input.href : input.url;

/** Recarrega la pàgina per tornar a passar pel login del contenidor. */
export const iniciarSessio = () => {
    recarregant = true;
    escriureMarcaIniciSessio(Date.now());
    window.location.reload();
};

/** S'ha de cridar quan una crida a l'API respon que no hi ha sessió. */
export const gestionarSenseSessio = () => {
    if (recarregant || caducada) {
        return;
    }
    if (Date.now() - llegirMarcaIniciSessio() < INICI_SESSIO_MARGE_MS) {
        caducada = true;
        subscriptors.forEach((s) => s());
    } else {
        iniciarSessio();
    }
};

/** S'ha de cridar quan s'ha obtingut l'usuari de la sessió: la recuperació ha anat bé. */
export const sessioRecuperada = () => escriureMarcaIniciSessio(undefined);

/**
 * Comprova si la sessió continua viva (s'usa quan cau la connexió SSE, que no informa del codi
 * d'estat). Retorna false si no hi ha sessió, i llavors ja n'ha iniciat la recuperació. Un error
 * de xarxa o del servidor no vol dir que la sessió hagi caducat: es retorna true.
 */
export const comprovarSessio = async (): Promise<boolean> => {
    if (!apiBaseUrl || !fetchOriginal) {
        return true;
    }
    try {
        // fetch original: el 401 es gestiona aquí, no cal que el torni a gestionar l'interceptor
        const response = await fetchOriginal(apiBaseUrl + '/usuari/actual/securityInfo', {
            credentials: 'include',
            redirect: 'manual',
            cache: 'no-store',
            headers: { Accept: 'application/json' },
        });
        if (response.type === 'opaqueredirect' || response.status === 401) {
            gestionarSenseSessio();
            return false;
        }
        return true;
    } catch {
        return true;
    }
};

/**
 * Intercepta els 401 de les crides a l'API, tant les d'axios (Session.tsx i altres) com les de
 * fetch (el client Ketting del ResourceApiProvider de reactlib, que no exposa cap callback
 * d'error). Només s'instal·la una vegada.
 */
export const instalarDeteccioSenseSessio = (apiUrl: string) => {
    if (fetchOriginal) {
        return;
    }
    apiBaseUrl = new URL(apiUrl, window.location.href).href.replace(/\/+$/, '');
    const original = window.fetch.bind(window);
    fetchOriginal = original;
    window.fetch = async (input: RequestInfo | URL, init?: RequestInit) => {
        const response = await original(input, init);
        if (response.status === 401 && esUrlApi(getUrlPeticio(input))) {
            gestionarSenseSessio();
        }
        return response;
    };
    axios.interceptors.response.use(undefined, (error) => {
        if (error?.response?.status === 401 && error?.config && esUrlApi(axios.getUri(error.config))) {
            gestionarSenseSessio();
        }
        return Promise.reject(error);
    });
};

/** Indica si la sessió ha caducat i no s'ha pogut recuperar amb la recàrrega. */
export const useSessioCaducada = (): boolean =>
    React.useSyncExternalStore(
        (onChange) => {
            subscriptors.add(onChange);
            return () => {
                subscriptors.delete(onChange);
            };
        },
        () => caducada
    );

export const PantallaSessioCaducada: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Box
            component="main"
            sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '100vh', gap: 2 }}>
            <Typography component="h1" variant="h6">{t('common.sessio.caducada')}</Typography>
            <Button variant="contained" onClick={iniciarSessio}>
                {t('common.sessio.iniciar')}
            </Button>
        </Box>
    );
};
