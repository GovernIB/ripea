import { Link as RouterLink } from 'react-router-dom';
import { Box, Card, CardActionArea, Divider, Icon, Skeleton, Tooltip, Typography, useTheme } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { tonal } from '@src/util/tonal.ts';
import { accentColor } from '@src/theme.ts';
import { useSistemaDetail } from '@src/pages/user/monitor/SistemaDetail.tsx';
import { CATALEG_INDICADORS, formatValor, PresentacioIndicador } from './catalegIndicadors.ts';
import type { Indicador } from './Inici.tsx';

type TargetaProps = {
    indicador: Indicador;
    presentacio: PresentacioIndicador;
    /** Acció en lloc de l'enllaç (targetes del monitor del sistema). */
    onClick?: () => void;
};

/**
 * Targeta d'indicador: tota la targeta és un enllaç a la pantalla de destinació (o un botó, si no en té).
 *
 * Accessibilitat: el nom de l'enllaç és «títol: valor» i la descripció és al tooltip, que s'obre amb el ratolí
 * i amb el focus de teclat i queda associat a l'enllaç com a descripció (describeChild). En pantalles tàctils
 * la descripció no es veu (acceptat).
 */
const Targeta = ({ indicador, presentacio, onClick }: TargetaProps) => {
    const { t, i18n } = useTranslation();
    const theme = useTheme();
    const colors = tonal(presentacio.color)(theme);

    const titol = t(`page.inici.indicador.${indicador.codi}.titol`);
    const descripcio = t(`page.inici.indicador.${indicador.codi}.descripcio`, { dies: indicador.dies });
    const disponible = !indicador.error && indicador.valor != null;
    const valor = disponible ? formatValor(indicador.valor as number, presentacio.format, i18n.language) : '—';
    const total = disponible && indicador.total != null
        ? t('page.inici.deTotal', { total: formatValor(indicador.total, presentacio.format, i18n.language) })
        : undefined;
    const nomAccessible = disponible
        ? [`${titol}: ${valor}`, total].filter(Boolean).join(' ')
        : `${titol}: ${t('page.inici.noDisponible')}`;

    const accio = onClick != null
        ? { component: 'button' as const, onClick }
        : { component: RouterLink, to: presentacio.to ?? '/' };

    // Disposició del disseny proposat: icona i xifra a la mateixa alçada, el títol a sota i, separat per una
    // línia, el nom del llistat de destinació amb estil d'enllaç (només visual: tota la targeta ja és l'enllaç).
    return (
        <Card variant="outlined" sx={{ height: '100%' }}>
            <Tooltip title={descripcio} describeChild arrow placement="top">
                <CardActionArea
                    {...accio}
                    aria-label={nomAccessible}
                    sx={{ height: '100%', p: 2.5, display: 'flex', flexDirection: 'column', alignItems: 'stretch', justifyContent: 'flex-start' }}
                >
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2 }}>
                        <Box
                            aria-hidden
                            sx={[
                                tonal(presentacio.color),
                                { width: 44, height: 44, borderRadius: '50%', flexShrink: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' },
                            ]}
                        >
                            <Icon>{presentacio.icon}</Icon>
                        </Box>
                        <Typography sx={{ fontSize: '2.25rem', fontWeight: 700, lineHeight: 1, color: disponible ? colors.color : 'text.secondary' }}>
                            {valor}
                        </Typography>
                    </Box>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1.75 }}>
                        {titol}
                    </Typography>
                    {total && (
                        <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5 }}>
                            {total}
                        </Typography>
                    )}
                    {!disponible && (
                        <Typography variant="caption" color="error" sx={{ mt: 0.5 }}>
                            {t('page.inici.noDisponible')}
                        </Typography>
                    )}
                    {/* Empeny el peu avall perquè totes les targetes de la fila l'alineïn */}
                    <Box sx={{ flexGrow: 1 }} />
                    <Divider sx={{ mt: 1.75, mb: 1.25 }} />
                    <Typography
                        component="span"
                        aria-hidden
                        sx={{ fontSize: '0.875rem', fontWeight: 500, color: accentColor(theme) }}
                    >
                        {`${t(`page.user.menu.${presentacio.menu}`)} →`}
                    </Typography>
                </CardActionArea>
            </Tooltip>
        </Card>
    );
};

/** Targeta del monitor del sistema: obre el mateix diàleg que l'entrada del menú del superadministrador. */
const TargetaMonitorSistema = (props: Omit<TargetaProps, 'onClick'>) => {
    const { handleOpen, dialog } = useSistemaDetail();
    return (
        <>
            <Targeta {...props} onClick={handleOpen} />
            {dialog}
        </>
    );
};

/** Targeta del codi rebut del backend. Un codi sense presentació (backend més nou que el front) no es pinta. */
export const TargetaIndicador = ({ indicador }: { indicador: Indicador }) => {
    const presentacio = CATALEG_INDICADORS[indicador.codi];
    if (presentacio == null) {
        return null;
    }
    return presentacio.monitorSistema
        ? <TargetaMonitorSistema indicador={indicador} presentacio={presentacio} />
        : <Targeta indicador={indicador} presentacio={presentacio} />;
};

/** Esquelet d'una targeta mentre es carreguen els indicadors. */
export const TargetaIndicadorCarregant = () => (
    <Card variant="outlined" sx={{ height: '100%', p: 2.5 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <Skeleton variant="circular" width={44} height={44} />
            <Skeleton width={56} height={44} />
        </Box>
        <Skeleton width="70%" sx={{ mt: 1.75 }} />
        <Divider sx={{ mt: 1.75, mb: 1.25 }} />
        <Skeleton width="45%" />
    </Card>
);

export default TargetaIndicador;
