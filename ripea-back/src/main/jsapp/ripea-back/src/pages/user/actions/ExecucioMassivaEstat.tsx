import React from 'react';
import { Box, Icon, Tooltip, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { useBaseAppContext } from 'reactlib';
import { tonal, TonalColor } from '../../../util/tonal.ts';

type Aparenca = { color: TonalColor; icon: string };

type InsigniaProps = {
    aparenca: Aparenca;
    text: string;
    titol?: string;
    /** Si hi és, la insígnia es comporta com un botó: en clicar-la (o Intro/Espai) s'executa. */
    onClick?: () => void;
};

type ExecucioMassivaEstatProps = {
    /** Estat de l'execució (PENDENT, EN_CURS, FINALITZADA, CANCELADA). */
    value?: string | null;
    /** Text traduït de l'estat (el que serveix el backend). */
    label?: string;
    /** Si s'ha demanat la cancel·lació (data de cancel·lació informada). */
    cancelacioData?: string | null;
    /** Si l'execució ja ha acabat (data de fi informada). */
    dataFi?: string | null;
};

type ExecucioMassivaContingutEstatProps = {
    /** Estat de l'element (ESTAT_PENDENT, ESTAT_FINALITZAT, ESTAT_ERROR, ESTAT_CANCELAT). */
    value?: string | null;
    /** Text traduït de l'estat (el que serveix el backend). */
    label?: string;
    /** Motiu de l'error: es mostra com a títol. */
    error?: string | null;
};

const etiquetaSx = {
    display: 'flex',
    alignItems: 'center',
    gap: 0.5,
    p: '4px',
    px: 1,
    borderRadius: '4px',
};

const ESTATS_EXECUCIO: Record<string, Aparenca> = {
    PENDENT: { color: 'info', icon: 'schedule' },
    EN_CURS: { color: 'info', icon: 'autorenew' },
    FINALITZADA: { color: 'success', icon: 'done' },
    CANCELADA: { color: 'warning', icon: 'block' },
};

/** Cancel·lació demanada d'una execució que encara no ha acabat d'aturar-se. */
const ESTAT_CANCELANT: Aparenca = { color: 'warning', icon: 'hourglass_top' };

const ESTATS_CONTINGUT: Record<string, Aparenca> = {
    ESTAT_PENDENT: { color: 'info', icon: 'schedule' },
    ESTAT_FINALITZAT: { color: 'success', icon: 'done' },
    ESTAT_ERROR: { color: 'error', icon: 'warning_amber' },
    ESTAT_CANCELAT: { color: 'warning', icon: 'block' },
};

const Insignia: React.FC<InsigniaProps> = ({ aparenca, text, titol, onClick }) => (
    <Tooltip
        title={titol ?? ''}
        placement="top"
        arrow
        disableHoverListener={!titol}
        slotProps={{ tooltip: { sx: { maxWidth: 480, whiteSpace: 'pre-wrap', wordBreak: 'break-word' } } }}
    >
        <Box
            sx={[etiquetaSx, tonal(aparenca.color), ...(onClick ? [{ cursor: 'pointer' }] : [])]}
            {...(onClick
                ? {
                      role: 'button',
                      tabIndex: 0,
                      onClick,
                      onKeyDown: (event: React.KeyboardEvent) => {
                          if (event.key === 'Enter' || event.key === ' ') {
                              event.preventDefault();
                              onClick();
                          }
                      },
                  }
                : {})}
        >
            <Icon fontSize="small" sx={{ fontSize: '18px' }}>
                {aparenca.icon}
            </Icon>
            <Typography variant="caption">{text}</Typography>
        </Box>
    </Tooltip>
);

/**
 * Insígnia de l'estat d'una execució massiva. Una execució en curs amb la cancel·lació demanada es mostra com
 * a «Cancel·lant…»: l'aturada és efectiva abans de l'element següent, no a l'instant.
 */
export const ExecucioMassivaEstat: React.FC<ExecucioMassivaEstatProps> = ({
    value,
    label,
    cancelacioData,
    dataFi,
}) => {
    const { t } = useTranslation();

    if (!value) {
        return null;
    }

    if (!dataFi && cancelacioData && value === 'EN_CURS') {
        return (
            <Insignia
                aparenca={ESTAT_CANCELANT}
                text={t('page.user.action.massives.cancelar.sollicitada')}
                titol={t('page.user.action.massives.cancelar.sollicitadaOk')}
            />
        );
    }

    const aparenca = ESTATS_EXECUCIO[value];

    return aparenca ? <Insignia aparenca={aparenca} text={label ?? value} /> : <>{label ?? value}</>;
};

/** Insígnia de l'estat d'un element d'una execució massiva (en error, amb el motiu com a títol). */
export const ExecucioMassivaContingutEstat: React.FC<ExecucioMassivaContingutEstatProps> = ({
    value,
    label,
    error,
}) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const aparenca = value ? ESTATS_CONTINGUT[value] : undefined;

    const copiaError = () => {
        navigator.clipboard?.writeText(error ?? '');
        temporalMessageShow(null, t('common.copyToClipboard'), 'success');
    };

    if (!value || !aparenca) {
        return <>{label ?? value}</>;
    }

    return (
        <Insignia
            aparenca={aparenca}
            text={label ?? value}
            titol={error ?? undefined}
            onClick={error ? copiaError : undefined}
        />
    );
};

export default ExecucioMassivaEstat;
