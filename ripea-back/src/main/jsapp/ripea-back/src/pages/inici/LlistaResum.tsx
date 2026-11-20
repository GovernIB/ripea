import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Box, Card, Link, Skeleton, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { CATALEG_LLISTES, ColumnaLlista, ContextLlista } from './catalegLlistes.tsx';
import type { Llista } from './Inici.tsx';

/**
 * Acció que obre el detall d'una tasca. La dona el component que conté les llistes (un sol diàleg per a totes),
 * només si alguna llista té tasques.
 */
export const DetallTascaContext = React.createContext<(id: number) => void>(() => {});

/** Cel·les d'una sola línia: el que no hi cap es retalla amb punts suspensius (la taula no surt de la targeta). */
const estilCella = (columna: ColumnaLlista) => ({
    ...(columna.amplada ? { width: columna.amplada } : {}),
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
    textAlign: columna.alineacio ?? 'left',
    // Les columnes secundàries s'amaguen en mòbil
    ...(columna.secundaria ? { display: { xs: 'none', sm: 'table-cell' } } : {}),
});

/**
 * Les capçaleres passen a dues línies si no hi caben; una paraula sola més ampla que la columna (en mòbil) es retalla
 * amb punts suspensius en lloc de muntar-se sobre la columna del costat. El text sencer és al tooltip i al DOM.
 */
const capcaleraSx = { fontWeight: 600, whiteSpace: 'normal', overflow: 'hidden', textOverflow: 'ellipsis', verticalAlign: 'bottom' } as const;

/**
 * Llista resum de la pantalla d'inici: títol, descripció, enllaç «Veure-ho tot» al llistat sencer i una taula
 * de com a màxim 10 files, sense ordenació, filtres ni paginació.
 *
 * Accessibilitat: és una secció amb el títol com a encapçalament (h2); la taula és semàntica (capçaleres de
 * columna), té el títol com a nom i la descripció com a descripció; cada fila enllaça a l'element des de la seva
 * cel·la principal; el nom de l'enllaç «Veure-ho tot» inclou el títol de la llista.
 */
export const LlistaResum = ({ llista }: { llista: Llista }) => {
    const { t, i18n } = useTranslation();
    const obrirTasca = React.useContext(DetallTascaContext);
    const id = React.useId();
    const presentacio = CATALEG_LLISTES[llista.codi];
    if (presentacio == null) {
        // Codi sense presentació (backend més nou que el front): no es pinta
        return null;
    }
    const idTitol = `${id}-titol`;
    const idDescripcio = `${id}-descripcio`;
    const titol = t(`page.inici.llista.${llista.codi}.titol`);
    // Una llista amb text per al cas sense minuts (excepcions sense agrupar) el fa servir quan el backend no n'envia
    const clauSenseMinuts = `page.inici.llista.${llista.codi}.descripcioSenseMinuts`;
    const clauDescripcio = llista.minuts == null && i18n.exists(clauSenseMinuts)
        ? clauSenseMinuts
        : `page.inici.llista.${llista.codi}.descripcio`;
    const veureTot = t('page.inici.veureTot');
    const context: ContextLlista = { t, obrirTasca };
    const files = llista.files ?? [];

    return (
        <Card variant="outlined" component="section" aria-labelledby={idTitol} sx={{ height: '100%', p: { xs: 2, sm: 2.5 } }}>
            {/* En mòbil, «Veure-ho tot» va sota la descripció perquè el títol no quedi encongit */}
            <Box sx={{ display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, alignItems: 'flex-start', justifyContent: 'space-between', gap: { xs: 0.5, sm: 2 }, mb: 1.5 }}>
                <Box sx={{ minWidth: 0 }}>
                    <Typography id={idTitol} component="h2" variant="subtitle1" sx={{ fontWeight: 600, lineHeight: 1.4 }}>
                        {titol}
                    </Typography>
                    <Typography id={idDescripcio} variant="body2" color="text.secondary">
                        {t(clauDescripcio, { dies: llista.dies, minuts: llista.minuts })}
                    </Typography>
                </Box>
                <Link
                    component={RouterLink}
                    to={presentacio.to}
                    aria-label={`${veureTot}: ${titol}`}
                    sx={{ flexShrink: 0, whiteSpace: 'nowrap', fontSize: '0.875rem', fontWeight: 500 }}
                >
                    {veureTot}
                    <span aria-hidden> →</span>
                </Link>
            </Box>
            {llista.error ? (
                <Typography variant="body2" color="error">
                    {t('page.inici.llistaError')}
                </Typography>
            ) : files.length === 0 ? (
                <Typography variant="body2" color="text.secondary">
                    {t('page.inici.llistaBuida')}
                </Typography>
            ) : (
                <Table
                    size="small"
                    aria-labelledby={idTitol}
                    aria-describedby={idDescripcio}
                    sx={{
                        tableLayout: 'fixed',
                        width: '100%',
                        // Separació entre columnes més justa que la del tema i contingut alineat amb el títol de la llista
                        '& .MuiTableCell-root': { px: 1 },
                        '& .MuiTableCell-root:first-of-type': { pl: 0 },
                        '& .MuiTableCell-root:last-of-type': { pr: 0 },
                        // Icona d'avís de la data límit (StyledDate) a la mida del text
                        '& .MuiIcon-root': { fontSize: '1.1rem', verticalAlign: 'text-bottom', ml: 0.5 },
                    }}
                >
                    <TableHead>
                        <TableRow>
                            {presentacio.columnes.map((columna) => {
                                const capcalera = t(`page.inici.columna.${columna.clau}`);
                                return (
                                    <TableCell key={columna.clau} title={capcalera} sx={[estilCella(columna), capcaleraSx]}>
                                        {capcalera}
                                    </TableCell>
                                );
                            })}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {files.map((fila, index) => (
                            <TableRow key={fila.id ?? index}>
                                {presentacio.columnes.map((columna) => (
                                    <TableCell key={columna.clau} title={columna.text?.(fila, context) || undefined} sx={estilCella(columna)}>
                                        {columna.render(fila, context)}
                                    </TableCell>
                                ))}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            )}
        </Card>
    );
};

/** Esquelet d'una llista mentre es carreguen. */
export const LlistaResumCarregant = () => (
    <Card variant="outlined" sx={{ height: '100%', p: { xs: 2, sm: 2.5 } }}>
        <Skeleton width="45%" height={28} />
        <Skeleton width="75%" sx={{ mb: 1.5 }} />
        {[0, 1, 2, 3, 4].map((i) => (
            <Skeleton key={i} height={32} />
        ))}
    </Card>
);

export default LlistaResum;
