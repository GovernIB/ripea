import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Box, Link, Typography } from '@mui/material';
import { formatDate, formatDia, formatDiaHora } from '@src/util/dateUtils.ts';
import { StyledDate } from '@src/pages/tasca/TasquesGrid.tsx';
import { StyledEstat } from '@src/pages/user/monitor/integracio/IntegracioGrid.tsx';

/** Fila d'una llista resum: les claus són les de FilesLlistesInici (backend). */
export type FilaLlista = Record<string, any>;

/** Context dels renderitzadors de cel·la. */
export type ContextLlista = {
    t: (clau: string, opcions?: any) => string;
    /** Obre el detall de la tasca en un diàleg (el mateix que «Les meves tasques» i «Seguiment de tasques»). */
    obrirTasca: (id: number) => void;
};

/** Amplada fixa o diferent en mòbil (xs) i a partir de tauleta (sm). */
export type Amplada = string | { xs: string; sm: string };

export type ColumnaLlista = {
    /** Capçalera: page.inici.columna.<clau>. */
    clau: string;
    /**
     * Amplada de la columna (taula d'amplada fixa: les cel·les massa llargues es retallen amb punts suspensius).
     * Les dates en tenen una de fixa perquè no es retallin mai; les columnes sense amplada es reparteixen la resta.
     */
    amplada?: Amplada;
    /** Columna que s'amaga en pantalles estretes perquè la taula no surti de la pantalla. */
    secundaria?: boolean;
    /** Alineació del contingut (per defecte, a l'esquerra). */
    alineacio?: 'right';
    render: (fila: FilaLlista, context: ContextLlista) => React.ReactNode;
    /** Text sencer de la cel·la, per al tooltip del ratolí quan es retalla. */
    text?: (fila: FilaLlista, context: ContextLlista) => string | undefined;
};

/**
 * Presentació d'una llista resum de la pantalla d'inici. Quines llistes veu cada rol ho decideix el backend
 * (LlistaInici); aquí només hi ha com es pinta cada codi. El títol i la descripció són a les traduccions:
 * page.inici.llista.<CODI>.titol i .descripcio (la descripció pot fer servir {{dies}}).
 */
export type PresentacioLlista = {
    /** Llistat sencer («Veure-ho tot»), obert com des del menú. */
    to: string;
    columnes: ColumnaLlista[];
    /** La llista té files de tasques, que obren el diàleg de detall de la tasca. */
    detallTasca?: boolean;
};

/** Amplades fixes de les columnes de data: hi caben senceres (i la icona d'avís de la data límit). */
const AMPLADA_DIA = '6.25rem';
// En mòbil, la data i l'hora van en dues línies (veure DiaHora)
const AMPLADA_DIA_HORA: Amplada = { xs: '5.75rem', sm: '8.5rem' };
const AMPLADA_DATA_LIMIT = '7.5rem';

// --- Cel·les comunes ---------------------------------------------------------------------------------

/** Text retallat a una línia: la cel·la ja hi posa els punts suspensius. */
const text = (valor?: string | number | null) => (valor == null ? '' : String(valor));

/** Text secundari (gris) després del valor principal, a la mateixa línia. */
const Secundari = ({ children }: { children?: React.ReactNode }) =>
    children ? (
        <Typography component="span" variant="inherit" color="text.secondary">
            {' · '}
            {children}
        </Typography>
    ) : null;

/** Enllaç dins d'una cel·la retallada: ha d'ocupar com a màxim l'amplada de la cel·la. */
const estilEnllac = { maxWidth: '100%', verticalAlign: 'bottom' } as const;

/** Tasca: botó amb aspecte d'enllaç que obre el diàleg de detall. */
const columnaTasca = (amplada?: string): ColumnaLlista => ({
    clau: 'tasca',
    amplada,
    render: (fila, { obrirTasca }) => (
        <Link
            component="button"
            type="button"
            onClick={() => obrirTasca(fila.id)}
            sx={{ ...estilEnllac, textAlign: 'left', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}
        >
            {text(fila.titol)}
        </Link>
    ),
    text: (fila) => text(fila.titol),
});

/** Expedient: enllaç amb el número (nom accessible) i el títol al costat, en gris. */
const columnaExpedient = (amplada: string | undefined, clauId: string, clauNumero: string, clauNom: string): ColumnaLlista => ({
    clau: 'expedient',
    amplada,
    render: (fila) => (
        <>
            <Link component={RouterLink} to={`/contingut/${fila[clauId]}`} sx={estilEnllac}>
                {text(fila[clauNumero])}
            </Link>
            <Secundari>{fila[clauNom]}</Secundari>
        </>
    ),
    text: (fila) => [fila[clauNumero], fila[clauNom]].filter(Boolean).join(' · '),
});

/** Procediment: enllaç a la pantalla dels seus elements (metadades, tipus de document, estats...). */
const enllacProcediment = (id: number, nom?: string) => (
    <Link component={RouterLink} to={`/metaExpedient/${id}/metaDocument`} sx={estilEnllac}>
        {text(nom)}
    </Link>
);

const columnaTextLlarg = (clau: string, camp: string, amplada: string | undefined, secundaria?: boolean): ColumnaLlista => ({
    clau,
    amplada,
    secundaria,
    render: (fila) => text(fila[camp]),
    text: (fila) => text(fila[camp]),
});

const columnaData = (clau: string, camp: string, amplada: Amplada, format: (valor?: string) => string): ColumnaLlista => ({
    clau,
    amplada,
    render: (fila) => format(fila[camp]),
});

/** Data i hora: l'hora passa a una segona línia en mòbil perquè la columna sigui més estreta. */
const DiaHora = ({ valor }: { valor?: string }) =>
    valor ? (
        <>
            {formatDia(valor)}
            <Box component="span" sx={{ display: { xs: 'block', sm: 'inline' } }}>
                {' '}
                {formatDate(valor, 'HH:mm')}
            </Box>
        </>
    ) : null;

const columnaDataHora = (clau: string, camp: string): ColumnaLlista => ({
    clau,
    amplada: AMPLADA_DIA_HORA,
    render: (fila) => <DiaHora valor={fila[camp]} />,
    text: (fila) => formatDiaHora(fila[camp]),
});

// --- Catàleg -----------------------------------------------------------------------------------------

export const CATALEG_LLISTES: Record<string, PresentacioLlista> = {
    TASQUES_PER_DATA_LIMIT: {
        to: '/usuariTasca',
        detallTasca: true,
        columnes: [
            columnaTasca(),
            columnaExpedient(undefined, 'expedientId', 'expedientNumero', 'expedientNom'),
            {
                clau: 'dataLimit',
                amplada: AMPLADA_DATA_LIMIT,
                // Mateix color i icona que la columna del llistat de tasques (vençuda o dins del preavís)
                render: (fila) => (
                    <StyledDate entity={{ dataLimitExpirada: fila.foraTermini, shouldNotifyAboutDeadline: fila.vencimentProper }}>
                        {formatDia(fila.dataLimit)}
                    </StyledDate>
                ),
            },
        ],
    },
    EXPEDIENTS_LLESTOS_TANCAR: {
        to: '/massiu/tancament',
        columnes: [
            {
                clau: 'numero',
                amplada: '30%',
                render: (fila) => (
                    <Link component={RouterLink} to={`/contingut/${fila.id}`} sx={estilEnllac}>
                        {text(fila.numero)}
                    </Link>
                ),
                text: (fila) => text(fila.numero),
            },
            columnaTextLlarg('titol', 'nom', undefined),
            columnaTextLlarg('procediment', 'procediment', undefined, true),
            columnaData('dataCreacio', 'createdDate', AMPLADA_DIA, formatDia),
        ],
    },
    TASQUES_SENSE_ACTIVITAT: {
        to: '/seguimentTasques',
        detallTasca: true,
        columnes: [
            columnaTasca(),
            columnaExpedient(undefined, 'expedientId', 'expedientNumero', 'expedientNom'),
            columnaTextLlarg('responsables', 'responsables', undefined, true),
            columnaData('darreraActivitat', 'darreraActivitat', AMPLADA_DIA, formatDia),
        ],
    },
    DARRERES_INTEGRACIONS: {
        to: '/integracio',
        columnes: [
            columnaDataHora('data', 'data'),
            columnaTextLlarg('integracio', 'integracioNom', '22%'),
            columnaTextLlarg('descripcio', 'descripcio', undefined, true),
            {
                clau: 'estat',
                amplada: { xs: '6.5rem', sm: '7rem' },
                // Mateixa etiqueta (colors tonals) que la columna d'estat del monitor d'integracions
                render: (fila, { t }) => (
                    <StyledEstat entity={fila}>{t(`page.inici.estatIntegracio.${fila.estat}`, { defaultValue: fila.estat })}</StyledEstat>
                ),
            },
        ],
    },
    DARRERES_EXCEPCIONS: {
        to: '/excepcio',
        columnes: [
            columnaDataHora('darreraOcurrencia', 'data'),
            {
                // Només el nom de la classe: el paquet sencer queda al tooltip
                clau: 'tipus',
                amplada: '30%',
                render: (fila) => text(fila.tipus?.split('.').pop()),
                text: (fila) => text(fila.tipus),
            },
            columnaTextLlarg('missatge', 'missatge', undefined, true),
            {
                clau: 'ocurrencies',
                amplada: { xs: '3.75rem', sm: '5rem' },
                alineacio: 'right',
                render: (fila) => text(fila.numOcurrencies),
            },
        ],
    },
    DARRERS_COMENTARIS: {
        to: '/metaExpedient',
        columnes: [
            {
                clau: 'procediment',
                amplada: '30%',
                render: (fila) => enllacProcediment(fila.procedimentId, fila.procedimentNom),
                text: (fila) => [fila.procedimentCodi, fila.procedimentNom].filter(Boolean).join(' - '),
            },
            columnaTextLlarg('autor', 'autor', '20%', true),
            columnaDataHora('data', 'data'),
            columnaTextLlarg('comentari', 'text', undefined),
        ],
    },
    PROCEDIMENTS_PENDENTS_REVISIO: {
        to: '/metaExpedient',
        columnes: [
            columnaTextLlarg('codi', 'codi', '18%'),
            {
                clau: 'nom',
                amplada: '36%',
                render: (fila) => enllacProcediment(fila.id, fila.nom),
                text: (fila) => text(fila.nom),
            },
            columnaTextLlarg('organ', 'organ', undefined, true),
            columnaData('darreraModificacio', 'data', AMPLADA_DIA, formatDia),
        ],
    },
    PROCEDIMENTS_REBUTJATS: {
        to: '/metaExpedient',
        columnes: [
            columnaTextLlarg('codi', 'codi', '16%', true),
            {
                clau: 'nom',
                amplada: '32%',
                render: (fila) => enllacProcediment(fila.id, fila.nom),
                text: (fila) => text(fila.nom),
            },
            columnaData('darreraModificacio', 'data', AMPLADA_DIA, formatDia),
            {
                clau: 'darrerComentari',
                render: (fila) => text(fila.comentari),
                text: (fila) => fila.comentari
                    ? `${fila.comentari} (${[fila.comentariAutor, formatDiaHora(fila.comentariData)].filter(Boolean).join(', ')})`
                    : undefined,
            },
        ],
    },
};
