import { alpha, darken, lighten, type Theme } from '@mui/material/styles';
import { accentColor } from '../theme';

/**
 * Vora dels controls de la barra de títol: el color d'accent amb transparència, perquè es vegin
 * sobre el blanc (zona clara del degradat) i separin els botons dels grups. El gris neutre del tema
 * (`divider`) hi quedava gairebé invisible.
 */
const borderColor = (theme: Theme) => alpha(accentColor(theme), 0.4);

/**
 * Text dels controls desactivats: el 55% del text del tema. Amb `action.disabled` (30%) el botó
 * "Opcions" gairebé desapareixia (1,9:1 al clar, 2,6:1 als foscos); així es llegeix i continua
 * clarament apagat respecte dels botons actius.
 */
const disabledColor = (theme: Theme) => alpha(theme.palette.text.primary, 0.55);

/**
 * Vora dels botons outlined que van fora de la barra de títol (netejar el filtre, refrescar...) perquè
 * s'assemblin als de la barra: als temes foscos el tema els dona la vora blanca plena.
 */
export const outlinedControlBorderSx = (theme: Theme) =>
    theme.palette.mode === 'dark' ? { borderColor: borderColor(theme), '&:hover': { borderColor: borderColor(theme) } } : {};

/**
 * Estil dels controls (botons, xips, filtre ràpid) que cauen sobre la barra de títol d'un
 * ScreenFrame: la barra de la graella (StyledMuiGrid) i les `actions` del ScreenFrame. Es posa com a
 * `sx` del contenidor dels controls.
 *
 * Les eines cauen sobre la part del degradat que arriba al color principal:
 * Es pinten amb el fons del paper (blanc al tema clar, fosc als altres) i el text en el color d'accent, 
 * que és el «torrat» als temes clars i el to clar als foscos.
 */
export const titleBarControlsSx = {
    '& .MuiButton-root': {
        backgroundColor: (theme: Theme) => theme.palette.background.paper,
        // Als temes foscos el text és blanc (més contrast sobre el fons fosc del botó que l'accent)
        color: (theme: Theme) => (theme.palette.mode === 'dark' ? theme.palette.text.primary : accentColor(theme)),
        // Vora visible: els botons poden caure sobre la part clara del degradat (p. ex. si no hi ha
        // filtre ràpid i queden més a l'esquerra).
        borderColor,
        boxShadow: 'none',
        '&:hover': {
            backgroundColor: (theme: Theme) =>
                theme.palette.mode === 'dark'
                    ? lighten(theme.palette.background.paper, 0.15)
                    : darken(theme.palette.background.paper, 0.06),
            borderColor,
        },
        // Desactivat: el fons és el de la resta; només el text s'aclareix.
        '&.Mui-disabled': {
            // Sense l'opacitat que el tema o MUI donen al botó desactivat: aclaria també el fons,
            // que s'ha de veure igual que a la resta de botons.
            opacity: 1,
            backgroundColor: (theme: Theme) => theme.palette.background.paper,
            color: disabledColor,
            borderColor,
        },
    },
    // Comptador del selector d'accions massives desactivat (0 files): el xip no s'aclareix sencer,
    // només en perd el color.
    '& .MuiChip-root.Mui-disabled': {
        opacity: 1,
        color: disabledColor,
    },
    // Separació entre els botons d'un grup (selector d'accions massives).
    '& .MuiButtonGroup-grouped:not(:last-of-type)': {
        borderRightColor: borderColor,
    },
    // Camps de text (filtre ràpid)
    '& .MuiOutlinedInput-root': {
        backgroundColor: (theme: Theme) => theme.palette.background.paper,
        '& fieldset': { borderColor },
    },
} as const;
