import {alpha, darken, lighten, Theme} from "@mui/material/styles";

/** Colors semàntics de la paleta que admeten el tractament tonal. */
export type TonalColor = 'success' | 'error' | 'warning' | 'info' | 'primary' | 'secondary';

/**
 * Color tonal per a etiquetes i chips d'estat: fons translúcid del color i text enfosquit (tema clar) o aclarit
 * (temes foscos). Més suau que el color ple i vàlid als temes clar, fosc i Dracula: el text supera 4,5:1
 * (WCAG AA) a tots (mínim 4,9:1, error a Dracula).
 *
 * No s'usen palette.dark/light: els temes foscos només defineixen main i els calculats no arriben al contrast.
 * Ús: sx={tonal('success')}
 */
export const tonal = (color: TonalColor) => (theme: Theme) => {
    const main = theme.palette[color].main;
    const fosc = theme.palette.mode === 'dark';
    return {
        bgcolor: alpha(main, fosc ? 0.20 : 0.14),
        color: fosc ? lighten(main, 0.4) : darken(main, 0.35),
        // Les icones dels chips i etiquetes agafen el color del text
        '& .MuiChip-icon, & .MuiIcon-root': {color: 'inherit'},
    };
}

/** Variant neutra (estats inactius o desactivats), amb el mateix aspecte que les tonals però sense color. */
export const tonalNeutre = (theme: Theme) => ({
    bgcolor: alpha(theme.palette.text.primary, 0.08),
    color: theme.palette.text.secondary,
    '& .MuiChip-icon, & .MuiIcon-root': {color: 'inherit'},
});

/** Si el valor és un color semàntic de la paleta amb tractament tonal. */
export const isTonalColor = (color: any): color is TonalColor =>
    ['success', 'error', 'warning', 'info', 'primary', 'secondary'].includes(color);

/**
 * Barra de progrés en to suau. La barra translúcida no arriba al contrast de 3:1 que es demana a un element
 * gràfic (WCAG 1.4.11): s'ha d'acompanyar sempre del valor en text (vegeu TonalLinearProgress).
 */
export const tonalProgress = (color: TonalColor) => (theme: Theme) => {
    const main = theme.palette[color].main;
    const fosc = theme.palette.mode === 'dark';
    return {
        bgcolor: alpha(main, fosc ? 0.12 : 0.10),
        '& .MuiLinearProgress-bar': {bgcolor: alpha(main, fosc ? 0.30 : 0.40)},
    };
}
