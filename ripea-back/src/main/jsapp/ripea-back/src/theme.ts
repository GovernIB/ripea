import { createTheme, darken, ThemeOptions } from '@mui/material/styles';
import { lighten, alpha } from '@mui/material';
import type {} from '@mui/x-data-grid/themeAugmentation';

// Colors per defecte
export const DEFAULT_PRIMARY_COLOR = '#337ab7';
export const DEFAULT_SECONDARY_COLOR = '#f1efef';

// Patró de línies en diàgonal per al fons
const hatchPattern = (lineColor: string) =>
    `repeating-linear-gradient(45deg, transparent 0, transparent 0.75px, ${lineColor} 1.25px, transparent 1.75px, transparent 3px)`;

// ── CONFIGURACIÓ BASE D'ESTILS DE RIPEA ──────────────────────────────────────
// IMPORTANT: aquest objecte NO inclou `MuiCssBaseline.styleOverrides.body`
// perquè cada tema (light/dark/dracula) el defineix pel seu compte (colors de
// fons i hatch diferents per tema). Si mai s'afegeix una clau `body` aquí,
// caldrà fer-ho amb cura, ja que als temes de sota es fa spread d'aquest
// objecte i després es sobreescriu `body` explícitament: qualsevol `body` que
// es posi aquí quedaria silenciosament ignorat.
const baseComponentStyles: ThemeOptions['components'] = {
    MuiCssBaseline: {
        styleOverrides: {
            // Sticky footer: el contenidor arrel de la pàgina (capçalera +
            // cos + peu) ha d'ocupar com a mínim tota l'alçada del viewport.
            // En els llistats amb autoHeight (scroll global del navegador) el
            // contenidor creix amb el contingut i no té alçada fixa; sense
            // aquesta regla, quan el contingut no omple la pantalla el peu
            // queda a mitja alçada. Amb min-height:100vh el cos (flex-grow)
            // omple l'espai sobrant i el peu torna a quedar enganxat a baix,
            // tot mantenint el scroll quan el contingut supera el viewport.
            'div:has(> header):has(> footer)': {
                minHeight: '100vh',
            },
            '.multi-line-cell': {
                display: 'flex',
                alignItems: 'baseline !important',
                whiteSpace: 'break-spaces !important',
            },
            '.styledFilter': {
                marginBottom: '16px',
                paddingTop: '11px',
                paddingBottom: '16px',
                // paddingLeft: '16px',
                // paddingRight: '16px',
                borderRadius: '4px',
                backgroundColor: 'inherit',
            },
            '.input': {
                '& .MuiInputBase-root, & .MuiPickersInputBase-root': {
                    backgroundColor: 'inherit',
                },
                '& .MuiInputBase-root.Mui-disabled': {
                    backgroundColor: 'inherit',
                },
            },
            '.myLabel': {
                padding: '4px 8px',
                fontSize: '11px',
                borderRadius: '5px',
                display: 'flex',
                alignItems: 'center',
                width: 'max-content',
            },
            '.comment': {
                padding: '8px 16px',
                borderRadius: '8px',
            },
            '.myComment': {
                alignSelf: 'end',
                color: 'black',
                backgroundColor: '#a5d6a7',
            },
            '.otherComment': {
                color: 'black',
                backgroundColor: '#e0e0e0',
            },
            '.multiplicitat': {
                border: '1px solid lightgray',
                display: 'flex',
                alignItems: 'center',
                width: 'max-content',
                padding: '2px 6px',
                fontSize: '11px',
                fontWeight: '500',
                borderRadius: '4px',
                color: 'white',
            },
            '.massive-selector': {
                '& .MuiButton-root': {
                    borderColor: 'rgba(0, 0, 0, 0.23)', // Standard MUI outlined button border color
                },
                '& .MuiButton-root:hover': {
                    borderColor: 'rgba(0, 0, 0, 0.50)', // Standard MUI outlined button border color
                },
                '& .MuiButton-root.Mui-disabled': {
                    borderColor: 'rgba(0, 0, 0, 0.23)',
                },
                '& .MuiButtonGroup-grouped:first-of-type': {
                    borderTopLeftRadius: '4px',
                    borderBottomLeftRadius: '4px',
                },
                '& .MuiButtonGroup-grouped:last-of-type': {
                    borderTopRightRadius: '4px',
                    borderBottomRightRadius: '4px',
                },
                '& .MuiButton': {
                    color: 'inherit',
                },
            },
            'input.Mui-disabled': {
                cursor: 'not-allowed !important',
            },
            'input:-webkit-autofill, input:-webkit-autofill:hover, input:-webkit-autofill:focus, input:-webkit-autofill:active': {
                WebkitBoxShadow: '0 0 0 100px transparent inset !important',
                caretColor: 'inherit !important',
                transition: 'background-color 5000s ease-in-out 0s !important',
            },
        },
    },
    MuiDataGrid: {
        styleOverrides: {
            root: {
                '& [class^="row-with-color-"] .MuiDataGrid-cellCheckbox': {
                    width: '48px !important',
                    maxWidth: '48px !important',
                    minWidth: '48px !important',
                    marginLeft: '-4px !important',
                },
                '& .MuiDataGrid-cell': {
                    display: 'flex',
                },
                '& .MuiDataGrid-treeDataGroupingCell': {
                    '--DataGrid-t-spacing-unit': '16px',
                },
                '& .MuiDataGrid-treeDataGroupingCell > *': {
                    display: 'flex',
                    alignItems: 'center',
                },
                '& .MuiDataGrid-treeDataGroupingCellToggle': {
                    marginRight: 0,
                },
            },
            row: {
                minHeight: '45px !important',
            },
            cell: {
                '&.MuiDataGrid-cell--withRenderer': {
                    alignItems: 'flex-start !important',
                },
            },
            columnHeader: {
                '&.MuiDataGrid-columnHeaderCheckbox': {
                    alignItems: 'flex-end !important',
                    paddingTop: '4px !important',
                },
            },
            checkboxInput: {
                transform: 'scale(0.8)',
            },
        },
    },
    MuiTab: {
        styleOverrides: {
            root: { textTransform: 'none', fontSize: '1rem', '&.Mui-disabled': { opacity: 0.4 } },
        },
    },
    MuiTabs: {
        styleOverrides: {
            scrollButtons: {
                '&.Mui-disabled': {
                    opacity: 0.3,
                },
            },
        },
    },
    MuiButton: {
        styleOverrides: {
            root: {
                borderRadius: '0px',
                fontSize: '14px',
                fontWeight: 400,
                textTransform: 'none',
                '&:not(.MuiButtonGroup-grouped)': { marginLeft: '10px' },
                '& .MuiButton-startIcon': { marginRight: '0' },
                '&.Mui-disabled': {
                    opacity: 0.6,
                    cursor: 'not-allowed',
                },
            },
        },
    },
    MuiDrawer: {
        styleOverrides: {
            paper: {
                right: 'auto',
                left: 0,
            },
        },
    },
    MuiPaper: {
        styleOverrides: {
            root: { borderRadius: '0px' },
        },
    },
    MuiCardContent: {
        styleOverrides: {
            root: {
                padding: '10px 16px',
                '&:last-child': { paddingBottom: '10px' },
            },
        },
    },
    MuiTypography: {
        styleOverrides: {
            h5: { fontSize: '1.8rem', lineHeight: 1.2, fontWeight: 400 },
            h4: { fontSize: '1.5rem', lineHeight: 1.2, fontWeight: 400 },
            body1: { fontWeight: 500 },
            overline: { fontSize: '1.2rem', letterSpacing: '0em', textTransform: 'none' },
        },
    },
    MuiInputBase: {
        styleOverrides: {
            root: { fontSize: '14px' },
        },
    },
    MuiFormLabel: {
        styleOverrides: {
            root: {
                fontStyle: 'italic',
                paddingRight: '2px',
                fontSize: '14px',
                fontWeight: 200,
                '&.Mui-disabled': { fontStyle: 'italic', paddingRight: '2px' },
                '&.Mui-focused': { fontStyle: 'italic', paddingRight: '2px' },
            },
            filled: {
                fontStyle: 'italic',
                paddingRight: '2px',
                fontSize: '14px',
                fontWeight: 200,
                '&.Mui-disabled': { fontStyle: 'italic', paddingRight: '2px', opacity: 1 },
                '&.Mui-focused': { fontStyle: 'italic', paddingRight: '2px' },
            },
        },
    },
    MuiIcon: {
        styleOverrides: {
            root: {
                fontSize: 'var(--toolbar-icon-size, 18px)',
                marginRight: '4px',
                // Icones del menú lateral a 24px. S'ancora a `.MuiDrawer-paper` (i no a la
                // variable --toolbar-icon-size, que només es defineix a l'appbar) perquè el
                // menú es renderitza dins d'un portal fora del <nav> a pantalles petites.
                // El marge es treu perquè el contenidor ja centra l'icona.
                '.MuiDrawer-paper .MuiListItemButton-root &': {
                    fontSize: '1.5rem',
                    marginRight: 0,
                },
            },
        },
    },
    MuiChip: {
        styleOverrides: {
            root: {
                '&.MuiChip-sizeSmall .MuiChip-label': { fontSize: '14px' },
                '&.MuiChip-sizeMedium .MuiChip-label': { fontSize: '16px' },
            },
        },
    },
    MuiDialogTitle: {
        styleOverrides: {
            root: {
                padding: '5px 24px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                fontSize: '1.5rem',
            },
        },
    },
    MuiCard: {
        styleOverrides: {
            root: {
                borderRadius: '4px',
            },
        },
    },
    MuiCardHeader: {
        styleOverrides: {
            root: {
                paddingTop: '8px',
                paddingBottom: '8px',
                paddingLeft: '16px',
                paddingRight: '16px',
            },
        },
    },
    // Evita que Chrome pinti de groc els camps autocompletats (autofill).
    // El color de fons que substitueix el groc es defineix a cada tema,
    // perquè ha de coincidir amb `background.paper` de la palette.
    MuiOutlinedInput: {
        styleOverrides: {},
    },
    MuiBadge: {
        styleOverrides: {
            badge: {
                '.MuiDrawer-paper &': {
                    overflowWrap: 'normal',
                    wordBreak: 'keep-all',
                    whiteSpace: 'nowrap',
                    padding: '0px 4px',
                },
            },
        },
    },
};

// ── TEMA CLAR (LIGHT) ────────────────────────────────────────────────────
const LIGHT_PRIMARY_MAIN = '#004B99';
const LIGHT_PRIMARY_CONTRAST_TEXT = '#fff';
const LIGHT_SECONDARY_MAIN = '#2E2E2E';
const LIGHT_BACKGROUND_DEFAULT = '#ffffff';
const LIGHT_BACKGROUND_PAPER = '#ffffff';
const LIGHT_TEXT_PRIMARY = '#1e1e1e';
const LIGHT_TEXT_SECONDARY = '#666666';
const LIGHT_DIVIDER = '#e0e0e0';

export const lightTheme = createTheme({
    palette: {
        mode: 'light',
        primary: { main: lighten(LIGHT_PRIMARY_MAIN, 0.2), contrastText: LIGHT_PRIMARY_CONTRAST_TEXT },
        secondary: { main: LIGHT_SECONDARY_MAIN },
        background: { default: LIGHT_BACKGROUND_DEFAULT, paper: LIGHT_BACKGROUND_PAPER },
        text: { primary: LIGHT_TEXT_PRIMARY, secondary: LIGHT_TEXT_SECONDARY },
        divider: LIGHT_DIVIDER,
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                body: {
                    backgroundColor: '#ffffff',
                    backgroundImage: hatchPattern('#e1e1e1'),
                    color: LIGHT_TEXT_SECONDARY,
                },
                '.myComment': {
                    ...((baseComponentStyles.MuiCssBaseline?.styleOverrides as any)['.myComment']),
                    backgroundColor: '#a5d6a7',
                    color: LIGHT_TEXT_PRIMARY,
                },
                '.otherComment': {
                    ...((baseComponentStyles.MuiCssBaseline?.styleOverrides as any)['.otherComment']),
                    color: LIGHT_SECONDARY_MAIN,
                    backgroundColor: LIGHT_DIVIDER,
                },
            },
        },
        MuiDialog: {
            styleOverrides: {
                paper: {
                    // Només el botó de tancar (fill directe del paper, sobre la capçalera acolorida).
                    // Sense el combinador '>' també s'aplicava a les icones del cos del diàleg.
                    '& > .MuiIconButton-root .MuiIcon-root': {
                        color: LIGHT_PRIMARY_CONTRAST_TEXT,
                    },
                },
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDialogTitle?.styleOverrides?.root as object),
                    backgroundColor: lighten(LIGHT_PRIMARY_MAIN, 0.2),
                    color: LIGHT_PRIMARY_CONTRAST_TEXT,
                    // borderBottom: '1px solid #e3e3e3',
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiCard?.styleOverrides?.root as object),
                    border: '1px solid #e3e3e3'
                },
            },
        },
        MuiCardHeader: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiCardHeader?.styleOverrides?.root as object),
                    backgroundColor: darken(LIGHT_BACKGROUND_PAPER, 0.03),
                    color: LIGHT_SECONDARY_MAIN,
                    // borderBottom: '1px solid #e3e3e3',
                    '&.detail': { backgroundColor: darken(LIGHT_BACKGROUND_PAPER, 0.03) },
                },
            },
        },
        MuiListItemButton: {
            styleOverrides: {
                root: {
                    '.MuiDrawer-paper &:hover': { backgroundColor: alpha(LIGHT_PRIMARY_MAIN, 0.2)},
                    '.MuiDrawer-paper &.Mui-selected': { backgroundColor: alpha(LIGHT_PRIMARY_MAIN, 0.15) },
                    '.MuiDrawer-paper &.Mui-selected:hover': { backgroundColor: alpha(LIGHT_PRIMARY_MAIN, 0.25) },
                },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.root as object),
                    '& .MuiDataGrid-row.even:not(.Mui-selected)': {
                        backgroundColor: alpha(LIGHT_SECONDARY_MAIN, 0.08),
                    },
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `${alpha(LIGHT_PRIMARY_MAIN, 0.15)} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${alpha(LIGHT_PRIMARY_MAIN, 0.28)} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${alpha(LIGHT_PRIMARY_MAIN, 0.4)} !important` },
                },
            },
        },
        MuiTab: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiTab?.styleOverrides?.root as object),
                    color: LIGHT_TEXT_PRIMARY,
                    '&.Mui-selected': { color: LIGHT_PRIMARY_MAIN },
                },
            },
        },
    },
});

// ── TEMA FOSC (DARK) ─────────────────────────────────────────────────────
const DARK_PRIMARY_LIGHT = '#5ea6f3';
const DARK_PRIMARY_MAIN = '#004B99';
const DARK_PRIMARY_CONTRAST_TEXT = '#fff';
const DARK_SECONDARY_MAIN = '#F6F6F6';
const DARK_BACKGROUND_DEFAULT = '#121212';
const DARK_BACKGROUND_PAPER = '#1e1e1e';
const DARK_TEXT_PRIMARY = '#ffffff';
const DARK_TEXT_SECONDARY = '#bbbbbb';
const DARK_DIVIDER = '#ffffff';
const DARK_ERROR_MAIN = '#f44336';
const DARK_INFO_MAIN = '#29b6f6';
const DARK_SUCCESS_MAIN = '#66bb6a';

export const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { light: DARK_PRIMARY_LIGHT, main: lighten(DARK_PRIMARY_MAIN, 0.2), contrastText: DARK_PRIMARY_CONTRAST_TEXT },
        secondary: { main: DARK_SECONDARY_MAIN },
        background: { default: DARK_BACKGROUND_DEFAULT, paper: DARK_BACKGROUND_PAPER },
        text: { primary: DARK_TEXT_PRIMARY, secondary: DARK_TEXT_SECONDARY },
        divider: DARK_DIVIDER,
        error: { main: DARK_ERROR_MAIN },
        info: { main: DARK_INFO_MAIN },
        success: { main: DARK_SUCCESS_MAIN },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                body: {
                    backgroundColor: '#1c1c1c',
                    backgroundImage: hatchPattern('#2a2a2a'),
                    color: DARK_TEXT_SECONDARY,
                },
                '.myComment': {
                    ...((baseComponentStyles.MuiCssBaseline?.styleOverrides as any)['.myComment']),
                    backgroundColor: '#436d44',
                    color: DARK_PRIMARY_CONTRAST_TEXT,
                },
                '.otherComment': {
                    ...((baseComponentStyles.MuiCssBaseline?.styleOverrides as any)['.otherComment']),
                    color: DARK_PRIMARY_CONTRAST_TEXT,
                    backgroundColor: DARK_BACKGROUND_PAPER,
                },
            },
        },
        MuiOutlinedInput: {
            styleOverrides: {
                root: {
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                        borderColor: DARK_PRIMARY_LIGHT,
                        borderWidth: '2px',
                    },
                    '&.Mui-disabled .MuiOutlinedInput-notchedOutline': {
                        borderColor: alpha(DARK_TEXT_SECONDARY, 0.4),
                    },
                },
            },
        },
        MuiInputLabel: {
            styleOverrides: {
                root: {
                    '&.Mui-focused': {
                        color: lighten(DARK_PRIMARY_LIGHT, 0.2),
                    },
                },
            },
        },
        MuiDialog: {
            styleOverrides: {
                paper: {
                    // Només el botó de tancar (fill directe del paper, sobre la capçalera acolorida).
                    '& > .MuiIconButton-root .MuiIcon-root': {
                        color: DARK_PRIMARY_CONTRAST_TEXT,
                    },
                },
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDialogTitle?.styleOverrides?.root as object),
                    backgroundColor: darken(DARK_PRIMARY_MAIN, 0.2),
                    color: DARK_TEXT_PRIMARY,
                    borderBottom: '1px solid #949494',
                },
            },
        },
        MuiCardHeader: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiCardHeader?.styleOverrides?.root as object),
                    backgroundColor: DARK_BACKGROUND_PAPER,
                    color: DARK_SECONDARY_MAIN,
                    borderBottom: '1px solid #949494',
                    '&.detail': { backgroundColor: alpha(DARK_BACKGROUND_PAPER, 0.5) },
                },
            },
        },
        MuiListItemButton: {
            styleOverrides: {
                root: {
                    '.MuiDrawer-paper &:hover': { backgroundColor: alpha(DARK_PRIMARY_MAIN, 0.5)},
                    '.MuiDrawer-paper &.Mui-selected': { backgroundColor: alpha(DARK_PRIMARY_MAIN, 0.4) },
                    '.MuiDrawer-paper &.Mui-selected:hover': { backgroundColor: alpha(DARK_PRIMARY_MAIN, 0.5) },
                },
            },
        },
        MuiAlert: {
            styleOverrides: {
                standardWarning: { backgroundColor: '#302519' },
                standardError: { backgroundColor: '#281111' },
                standardInfo: { backgroundColor: '#1d2a2e' },
                standardSuccess: { backgroundColor: '#16321c' },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.root as object),
                    '& .MuiDataGrid-row.even:not(.Mui-selected)': {
                        backgroundColor: lighten('#2e2e2e', 0.05),
                    },
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `${alpha(DARK_PRIMARY_MAIN, 0.15)} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${alpha(DARK_PRIMARY_MAIN, 0.28)} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${alpha(DARK_PRIMARY_MAIN, 0.5)} !important` },
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiButton?.styleOverrides?.root as object),
                    variants: [
                        {
                            props: ({ color, variant }: any) =>
                                variant === 'outlined' && color === 'primary',
                            style: {
                                color: DARK_SECONDARY_MAIN,
                                borderColor: DARK_SECONDARY_MAIN,
                            },
                        },
                    ],
                },
            },
        },
        MuiTab: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiTab?.styleOverrides?.root as object),
                    color: DARK_TEXT_PRIMARY,
                    '&.Mui-selected': { color: DARK_PRIMARY_LIGHT },
                },
            },
        },
        MuiLink: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiLink?.styleOverrides?.root as object),
                    color: '#4697e7',
                },
            },
        },
    },
});

// ── TEMA DRÀCULA ────────────────────────────────────────────────────────
const DRACULA_PRIMARY_LIGHT = '#caabf7';
const DRACULA_PRIMARY_MAIN = '#BD93F9';
const DRACULA_PRIMARY_CONTRAST_TEXT = '#fff';
const DRACULA_SECONDARY_MAIN = '#F8F8F2';
const DRACULA_BACKGROUND_DEFAULT = '#282A36';
const DRACULA_BACKGROUND_PAPER = '#303341';
const DRACULA_TEXT_PRIMARY = '#F8F8F2';
const DRACULA_TEXT_SECONDARY = '#D6D6C2';
const DRACULA_DIVIDER = '#7a7d8b';
const DRACULA_ERROR_MAIN = '#FF5555';
const DRACULA_WARNING_MAIN = '#FFB86C';
const DRACULA_SUCCESS_MAIN = '#50FA7B';
const DRACULA_INFO_MAIN = '#8BE9FD';

export const draculaTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: {light: DRACULA_PRIMARY_LIGHT, main: DRACULA_PRIMARY_MAIN, contrastText: DRACULA_PRIMARY_CONTRAST_TEXT },
        secondary: { main: DRACULA_SECONDARY_MAIN },
        background: { default: DRACULA_BACKGROUND_DEFAULT, paper: DRACULA_BACKGROUND_PAPER },
        text: { primary: DRACULA_TEXT_PRIMARY, secondary: DRACULA_TEXT_SECONDARY },
        divider: DRACULA_DIVIDER,
        error: { main: DRACULA_ERROR_MAIN },
        warning: { main: DRACULA_WARNING_MAIN },
        success: { main: DRACULA_SUCCESS_MAIN },
        info: { main: DRACULA_INFO_MAIN },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                body: {
                    backgroundColor: '#282A36',
                    backgroundImage: hatchPattern('#44475A'),
                    color: DRACULA_TEXT_PRIMARY,
                },
                '.myComment': {
                    ...((baseComponentStyles.MuiCssBaseline?.styleOverrides as any)['.myComment']),
                    backgroundColor: '#436d44',
                    color: DRACULA_PRIMARY_CONTRAST_TEXT,
                },
                '.otherComment': {
                    ...((baseComponentStyles.MuiCssBaseline?.styleOverrides as any)['.otherComment']),
                    color: DRACULA_PRIMARY_CONTRAST_TEXT,
                    backgroundColor: DRACULA_BACKGROUND_PAPER,
                },
            },
        },
        MuiOutlinedInput: {
            styleOverrides: {
                root: {
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                        borderColor: DRACULA_PRIMARY_LIGHT,
                        borderWidth: '2px',
                    },
                    '&.Mui-disabled .MuiOutlinedInput-notchedOutline': {
                        borderColor: alpha(DRACULA_TEXT_SECONDARY, 0.4),
                    },
                },
            },
        },
        MuiInputLabel: {
            styleOverrides: {
                root: {
                    '&.Mui-focused': {
                        color: lighten(DRACULA_PRIMARY_LIGHT, 0.2),
                    },
                },
            },
        },
        MuiDialog: {
            styleOverrides: {
                paper: {
                    // Només el botó de tancar (fill directe del paper, sobre la capçalera acolorida).
                    '& > .MuiIconButton-root .MuiIcon-root': {
                        color: DRACULA_PRIMARY_CONTRAST_TEXT,
                    },
                },
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDialogTitle?.styleOverrides?.root as object),
                    backgroundColor: darken(DRACULA_PRIMARY_MAIN, 0.2),
                    color: DRACULA_TEXT_PRIMARY,
                },
            },
        },
        MuiCardHeader: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiCardHeader?.styleOverrides?.root as object),
                    backgroundColor: DRACULA_BACKGROUND_PAPER,
                    color: DRACULA_SECONDARY_MAIN,
                    borderBottom: '1px solid #949494',
                    '&.detail': { backgroundColor: alpha(DRACULA_BACKGROUND_PAPER, 0.5) },
                },
            },
        },
        MuiListItemButton: {
            styleOverrides: {
                root: {
                    '.MuiDrawer-paper &:hover': { backgroundColor: alpha(DRACULA_PRIMARY_MAIN, 0.35)},
                    '.MuiDrawer-paper &.Mui-selected': { backgroundColor: alpha(DRACULA_PRIMARY_MAIN, 0.3) },
                    '.MuiDrawer-paper &.Mui-selected:hover': { backgroundColor: alpha(DRACULA_PRIMARY_MAIN, 0.4) },
                },
            },
        },
        MuiAlert: {
            styleOverrides: {
                standardWarning: { backgroundColor: darken(DRACULA_WARNING_MAIN,0.6) },
                standardError: { backgroundColor: darken(DRACULA_ERROR_MAIN,0.7) },
                standardInfo: { backgroundColor: darken(DRACULA_INFO_MAIN,0.7) },
                standardSuccess: { backgroundColor: darken(DRACULA_SUCCESS_MAIN,0.75) },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.root as object),
                    '& .MuiDataGrid-row.even:not(.Mui-selected)': {
                        backgroundColor: lighten('#282A36', 0.05),
                    },
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `${alpha(DRACULA_PRIMARY_MAIN, 0.15)} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${alpha(DRACULA_PRIMARY_MAIN, 0.2)} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${alpha(DRACULA_PRIMARY_MAIN, 0.3)} !important` },
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiButton?.styleOverrides?.root as object),
                    variants: [
                        {
                            props: ({ color, variant }: any) =>
                                variant === 'outlined' && color === 'primary',
                            style: {
                                color: '#F8F8F2',
                                borderColor: DRACULA_PRIMARY_CONTRAST_TEXT,
                            },
                        },
                    ],
                    '.MuiDialog-paper &.MuiButton-outlinedPrimary': {
                         borderColor: DRACULA_PRIMARY_CONTRAST_TEXT,
                    },
                },
            },
        },
        MuiTab: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiTab?.styleOverrides?.root as object),
                    color: DRACULA_TEXT_PRIMARY,
                    '&.Mui-selected': { color: DRACULA_PRIMARY_LIGHT },
                },
            },
        },
        MuiLink: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiLink?.styleOverrides?.root as object),
                    color: '#90c7ff',
                },
            },
        },
    },
});
