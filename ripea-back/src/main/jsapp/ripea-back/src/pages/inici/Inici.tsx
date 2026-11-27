import React from 'react';
import { Alert, Box, Grid, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { GridPage, useResourceApiService } from 'reactlib';
import { ScreenFrame } from '@src/components/ScreenFrame.tsx';
import { useUserSession } from '@src/components/Session.tsx';
import { icons } from '@src/util/icons.ts';
import useTascaDetail from '@src/pages/tasca/details/TascaDetail.tsx';
import { TargetaIndicador, TargetaIndicadorCarregant } from './TargetaIndicador.tsx';
import { DetallTascaContext, LlistaResum, LlistaResumCarregant } from './LlistaResum.tsx';
import { CATALEG_LLISTES } from './catalegLlistes.tsx';

/** Ruta de la pantalla d'inici. */
export const RUTA_INICI = '/inici';

/**
 * Valor de la preferència del perfil `paginaEntrada` (PaginaEntradaEnum del backend) per entrar a la
 * pantalla d'inici en lloc de la pantalla per defecte del rol.
 */
export const PAGINA_ENTRADA_INICI = 'INICI';

const RESOURCE_NAME = 'iniciResource';
const REPORT_INDICADORS = 'INDICADORS';
const REPORT_LLISTES = 'LLISTES';
const REPORT_LLISTA = 'LLISTA';

/** Targeta d'indicador (IniciResource.Indicador). */
export type Indicador = {
    codi: string;
    valor?: number;
    total?: number;
    /** Dies que fa servir el càlcul (configurables), per mostrar-los al text de la targeta. */
    dies?: number;
    error?: boolean;
    /** El càlcul ha superat el límit de temps de les consultes (també té error). */
    tempsEsgotat?: boolean;
};

/** Llista resum (IniciResource.Llista). */
export type Llista = {
    codi: string;
    files?: Record<string, any>[];
    /** Dies que fa servir el criteri de la llista (configurables), per mostrar-los a la descripció. */
    dies?: number;
    /** Minuts configurables del criteri (excepcions: finestra d'agrupació de les repetides; sense valor, no s'agrupen). */
    minuts?: number;
    error?: boolean;
    /** La consulta ha superat el límit de temps (també té error). */
    tempsEsgotat?: boolean;
};

/**
 * Demana un informe de la pantalla d'inici i el torna a demanar quan canvia el rol, l'entitat o l'òrgan
 * actius. Les targetes, el catàleg de llistes i cada llista es demanen per separat i en paral·lel: cada part
 * es pot mostrar tan bon punt arriba la seva resposta, i una llista lenta no endarrereix les altres.
 *
 * @param data paràmetres de l'informe (p. ex. el codi de la llista).
 */
const useInformeInici = <T,>(code: string, data?: Record<string, any>) => {
    const { value: user } = useUserSession();
    const { isReady, artifactReport } = useResourceApiService(RESOURCE_NAME);
    const [dades, setDades] = React.useState<T[]>();
    const [error, setError] = React.useState<boolean>(false);
    // Els paràmetres com a text: un objecte nou a cada render no ha de tornar a llançar la petició
    const parametres = data != null ? JSON.stringify(data) : undefined;

    React.useEffect(() => {
        if (!isReady || !user) {
            return;
        }
        // Si l'usuari canvia de rol, entitat o òrgan abans que arribi la resposta, la resposta anterior
        // s'ignora: no pot sobreescriure la del context nou.
        let vigent = true;
        setDades(undefined);
        setError(false);
        artifactReport(undefined, parametres != null ? { code, data: JSON.parse(parametres) } : { code })
            .then((resultat) => {
                if (vigent) {
                    setDades((resultat as T[]) ?? []);
                }
            })
            .catch(() => {
                if (vigent) {
                    setDades([]);
                    setError(true);
                }
            });
        return () => {
            vigent = false;
        };
    }, [isReady, code, parametres, user?.rolActual, user?.entitatActualId, user?.organActualId]);

    return { dades, error };
};

// Text només per a lectors de pantalla (mateix patró que el visuallyHidden de MUI i ProcesSegonPla).
const visuallyHidden = {
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

/** Files de 4 targetes en pantalla ampla, 2 en tauleta i 1 en mòbil. */
const midaTargeta = { xs: 12, sm: 6, lg: 3 };

/** Targetes del rol, o esquelets mentre es calculen. */
const Indicadors = ({ dades }: { dades?: Indicador[] }) => {
    const { t } = useTranslation();
    if (dades != null && dades.length === 0) {
        return null;
    }
    return (
        <Box component="section" aria-labelledby="inici-indicadors" aria-busy={dades == null}>
            <Typography id="inici-indicadors" component="h2" sx={visuallyHidden}>
                {t('page.inici.indicadors')}
            </Typography>
            <Grid container spacing={2}>
                {dades == null
                    ? [0, 1, 2, 3].map((i) => (
                        <Grid key={i} size={midaTargeta}>
                            <TargetaIndicadorCarregant />
                        </Grid>
                    ))
                    : dades.map((indicador) => (
                        <Grid key={indicador.codi} size={midaTargeta}>
                            <TargetaIndicador indicador={indicador} />
                        </Grid>
                    ))}
            </Grid>
        </Box>
    );
};

/** Llistes de dues en dues en pantalla ampla i una per fila en pantalles més estretes. */
const midaLlista = { xs: 12, lg: 6 };

/** Un sol diàleg de detall de tasca per a totes les llistes que en tenen. */
const AmbDetallTasca = ({ children }: { children: React.ReactNode }) => {
    const { handleOpen, dialog } = useTascaDetail();
    return (
        <DetallTascaContext.Provider value={handleOpen}>
            {children}
            {dialog}
        </DetallTascaContext.Provider>
    );
};

/**
 * Una llista del rol: es demana per separat (no espera les altres) i mostra l'esquelet fins que arriba. Si la
 * petició falla, o el backend no la torna (el rol ha canviat mentrestant), surt amb el missatge d'error.
 */
const LlistaDelRol = ({ codi }: { codi: string }) => {
    const { dades, error } = useInformeInici<Llista>(REPORT_LLISTA, { codi });
    if (dades == null) {
        return <LlistaResumCarregant />;
    }
    const llista = !error && dades.length > 0 ? dades[0] : { codi, error: true };
    return <LlistaResum llista={llista} />;
};

/** Llistes resum del rol (catàleg sense files), sota les targetes, o esquelets mentre arriba el catàleg. */
const Llistes = ({ cataleg }: { cataleg?: Llista[] }) => {
    if (cataleg != null && cataleg.length === 0) {
        return null;
    }
    const graella = (
        <Grid container spacing={2} aria-busy={cataleg == null}>
            {cataleg == null
                ? [0, 1].map((i) => (
                    <Grid key={i} size={midaLlista}>
                        <LlistaResumCarregant />
                    </Grid>
                ))
                : cataleg.map((llista) => (
                    <Grid key={llista.codi} size={midaLlista}>
                        <LlistaDelRol codi={llista.codi} />
                    </Grid>
                ))}
        </Grid>
    );
    // El diàleg només es munta si alguna llista té tasques: els altres rols no han de consultar el recurs de tasques
    const ambTasques = cataleg?.some((llista) => CATALEG_LLISTES[llista.codi]?.detallTasca);
    return ambTasques ? <AmbDetallTasca>{graella}</AmbDetallTasca> : graella;
};

const Inici: React.FC = () => {
    const { t } = useTranslation();
    const indicadors = useInformeInici<Indicador>(REPORT_INDICADORS);
    // Catàleg de llistes del rol (només els codis): cada llista es demana després per separat
    const llistes = useInformeInici<Llista>(REPORT_LLISTES);

    const carregat = indicadors.dades != null && llistes.dades != null;
    const error = indicadors.error || llistes.error;
    const buit = carregat && !indicadors.dades?.length && !llistes.dades?.length;

    return (
        <GridPage>
            <ScreenFrame icon={icons.inici} title={t('page.inici.title')} fitContent>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                    {indicadors.error && <Alert severity="error">{t('page.inici.error')}</Alert>}
                    {llistes.error && <Alert severity="error">{t('page.inici.errorLlistes')}</Alert>}
                    {!indicadors.error && <Indicadors dades={indicadors.dades} />}
                    {!llistes.error && <Llistes cataleg={llistes.dades} />}
                    {!error && buit && (
                        <Typography variant="body1" color="text.secondary">
                            {t('page.inici.buit')}
                        </Typography>
                    )}
                </Box>
            </ScreenFrame>
        </GridPage>
    );
};

export default Inici;
