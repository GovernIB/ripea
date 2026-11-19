import React from 'react';
import { Alert, Box, Grid, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { GridPage, useResourceApiService } from 'reactlib';
import { ScreenFrame } from '@src/components/ScreenFrame.tsx';
import { useUserSession } from '@src/components/Session.tsx';
import { icons } from '@src/util/icons.ts';
import { TargetaIndicador, TargetaIndicadorCarregant } from './TargetaIndicador.tsx';

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

/** Targeta d'indicador (IniciResource.Indicador). */
export type Indicador = {
    codi: string;
    valor?: number;
    total?: number;
    /** Dies que fa servir el càlcul (configurables), per mostrar-los al text de la targeta. */
    dies?: number;
    error?: boolean;
};

/** Llista resum (IniciResource.Llista). */
export type Llista = {
    codi: string;
    files?: Record<string, any>[];
    error?: boolean;
};

/**
 * Demana un informe de la pantalla d'inici i el torna a demanar quan canvia el rol, l'entitat o l'òrgan
 * actius. Les targetes i les llistes es demanen per separat i en paral·lel: cada part es pot mostrar
 * tan bon punt arriba la seva resposta.
 */
const useInformeInici = <T,>(code: string) => {
    const { value: user } = useUserSession();
    const { isReady, artifactReport } = useResourceApiService(RESOURCE_NAME);
    const [dades, setDades] = React.useState<T[]>();
    const [error, setError] = React.useState<boolean>(false);

    React.useEffect(() => {
        if (!isReady || !user) {
            return;
        }
        // Si l'usuari canvia de rol, entitat o òrgan abans que arribi la resposta, la resposta anterior
        // s'ignora: no pot sobreescriure la del context nou.
        let vigent = true;
        setDades(undefined);
        setError(false);
        artifactReport(undefined, { code })
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
    }, [isReady, code, user?.rolActual, user?.entitatActualId, user?.organActualId]);

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

const Inici: React.FC = () => {
    const { t } = useTranslation();
    const indicadors = useInformeInici<Indicador>(REPORT_INDICADORS);
    const llistes = useInformeInici<Llista>(REPORT_LLISTES);

    const carregat = indicadors.dades != null && llistes.dades != null;
    const error = indicadors.error || llistes.error;
    const buit = carregat && !indicadors.dades?.length && !llistes.dades?.length;

    return (
        <GridPage>
            <ScreenFrame icon={icons.inici} title={t('page.inici.title')} fitContent>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                    {error && <Alert severity="error">{t('page.inici.error')}</Alert>}
                    {!indicadors.error && <Indicadors dades={indicadors.dades} />}
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
