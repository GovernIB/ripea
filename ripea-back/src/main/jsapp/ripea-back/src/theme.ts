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
            // Fons neutre per defecte. Cada tema el pot sobreescriure amb el
            // color secundari corresponent (veure comentari a cada tema).
            '.styledFilter': {
                marginBottom: '16px',
                paddingTop: '11px',
                paddingBottom: '16px',
                paddingLeft: '16px',
                paddingRight: '16px',
                borderRadius: '4px',
                backgroundColor: 'inherit',
            },
            // TODO: revisar més endavant si `.input` es fa servir realment
            // (classe aplicada a MuiInputBase-root / MuiPickersInputBase-root
            // en algun formulari). Recuperat de la versió amb buildTheme; si
            // no es fa servir enlloc, simplement no farà res.
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
                // Recuperat: ratllat de files parells (zebra striping) i hover
                // en color primari. El color concret (secondary/primary main)
                // es defineix a cada tema perquè depèn de la palette pròpia.

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
                // El color de fons (drawerBg) es defineix a cada tema, ja que
                // depèn del primary de cada palette.
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
            root: { fontSize: 'var(--toolbar-icon-size, 18px)', marginRight: '4px' },
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
                // El color de fons (dialogTitleBg) es defineix a cada tema.
            },
        },
    },
    MuiCard: {
        styleOverrides: {
            root: {
                borderRadius: '4px',
                // El border de color es defineix a cada tema.
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
                // El color de fons (secondaryMain) es defineix a cada tema.
            },
        },
    },
    // Evita que Chrome pinti de groc els camps autocompletats (autofill).
    // El color de fons que substitueix el groc es defineix a cada tema,
    // perquè ha de coincidir amb `background.paper` de la palette.
    MuiOutlinedInput: {
        styleOverrides: {},
    },
};

// ── 1. TEMA CLAR (LIGHT) ────────────────────────────────────────────────────
export const lightTheme = createTheme({
    palette: {
        mode: 'light',
        primary: { main: '#004B99', contrastText: '#fff' },
        secondary: { main: '#2E2E2E' },
        background: { default: '#ffffff', paper: '#ffffff' },
        text: { primary: '#1e1e1e', secondary: '#666666' },
        divider: '#e0e0e0',
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                body: {
                    backgroundColor: '#ffffff',
                    backgroundImage: hatchPattern('#e1e1e1'),
                    color: '#666666',
                },
                // '.styledFilter': {
                //     backgroundColor: '#2E2E2E',
                //     border: '1px solid #e3e3e3',
                // },
                // // TODO: revisar més endavant si `.input` es fa servir realment.
                // '.input': {
                //     '& .MuiInputBase-root, & .MuiPickersInputBase-root': {
                //         color: '#1e1e1e',
                //         backgroundColor: '#ffffff',
                //     },
                //     '& .MuiInputBase-root.Mui-disabled': {
                //         backgroundColor: '#e7e5e5',
                //     },
                // },
            },
        },
        MuiOutlinedInput: {
            styleOverrides: {
                input: { '&:-webkit-autofill': { WebkitBoxShadow: '0 0 0 100px #ffffff inset' } },
            },
        },
        // TODO: confirmar si es manté o es lleva del tot. Recuperat de la
        // versió anterior (dialogTitleBg = alpha(primaryMain, 0.5)).
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    backgroundColor: lighten('#004B99', 0.2),
                    color: '#ffffff',
                    // borderBottom: '1px solid #e3e3e3',
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: { border: '1px solid #e3e3e3' },
            },
        },
        // TODO: no queda be
        MuiCardHeader: {
            styleOverrides: {
                root: {
                    backgroundColor: '#f1f1f1',
                    color: '#000000',
                    // borderBottom: '1px solid #e3e3e3',
                    '&.detail': { backgroundColor: alpha('#2E2E2E', 0.5) },
                },
            },
        },
        // TODO: confirmar si es manté o es lleva del tot. Recuperat de la
        // versió anterior (drawerBg = darken(primaryMain, 0.35)).
        // MuiDrawer: {
        //     styleOverrides: {
        //         paper: {
        //             right: 'auto',
        //             left: 0,
        //             backgroundColor: '#00305e',
        //             color: '#ffffff',
        //         },
        //     },
        // },
        // MuiListItemIcon: {
        //     styleOverrides: {
        //         root: { '.MuiDrawer-paper &': { color: '#ffffff' } },
        //     },
        // },
        // MuiListItemButton: {
        //     styleOverrides: {
        //         root: {
        //             '.MuiDrawer-paper &:hover': { backgroundColor: '#003d7a' },
        //             '.MuiDrawer-paper &.Mui-selected': { backgroundColor: alpha('#004B99', 0.3) },
        //             '.MuiDrawer-paper &.Mui-selected:hover': { backgroundColor: alpha('#004B99', 0.45) },
        //         },
        //     },
        // },
        // MuiAlert: {
        //     styleOverrides: {
        //         standardWarning: { color: '#8a6d3b', backgroundColor: '#fce3e3', borderColor: '#faebcc' },
        //     },
        // },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.root as object),
                    '& .MuiDataGrid-row.even:not(.Mui-selected)': {
                        backgroundColor: alpha('#2E2E2E', 0.08),
                    },
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `${alpha('#004B99', 0.15)} !important`,
                    },
                },
                row: {
                    '&.Mui-selected': { backgroundColor: `${alpha('#004B99', 0.28)} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${alpha('#004B99', 0.4)} !important` },
                },
            },
        },
    },
});

// ── 2. TEMA FOSC (DARK) ─────────────────────────────────────────────────────
const DARK_PRIMARY_COLOR = '#004B99';
export const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { light: '#5ea6f3', main: lighten(DARK_PRIMARY_COLOR, 0.2), contrastText: '#fff' },
        secondary: { main: '#F6F6F6' },
        background: { default: '#121212', paper: '#1e1e1e' },
        text: { primary: '#ffffff', secondary: '#bbbbbb' },
        divider: '#ffffff',
        error: { main: '#f44336' },
        info: { main: '#29b6f6' },
        success: { main: '#66bb6a' },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: (theme) => ({
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                body: {
                    backgroundColor: '#1c1c1c',
                    backgroundImage: hatchPattern('#2a2a2a'),
                    color: theme.palette.text.secondary,
                },
            }),
        },
        MuiOutlinedInput: {
            styleOverrides: {
                input: ({ theme }: { theme: any }) => ({
                    '&:-webkit-autofill': { 
                        WebkitBoxShadow: `0 0 0 100px ${theme.palette.background.paper} inset`,
                    },
                }),
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: ({ theme }: { theme: any }) => ({
                    backgroundColor: alpha(darken(DARK_PRIMARY_COLOR, 0.2), 0.5),
                    color: theme.palette.text.primary,
                    borderBottom: '1px solid #949494',
                }),
            },
        },
        MuiCardHeader: {
            styleOverrides: {
                root: ({ theme }: { theme: any }) => ({
                    backgroundColor: theme.palette.background.paper,
                    color: theme.palette.secondary.main,
                    borderBottom: '1px solid #949494',
                    '&.detail': { backgroundColor: alpha(theme.palette.background.paper, 0.5) },
                }),
            },
        },
        // TODO: confirmar si es manté o es lleva del tot. Recuperat de la
        // versió anterior (drawerBg = darken(primaryMain, 0.35)).
        // MuiDrawer: {
        //     styleOverrides: {
        //         paper: {
        //             right: 'auto',
        //             left: 0,
        //             backgroundColor: '#00305e',
        //             color: '#ffffff',
        //         },
        //     },
        // },
        // MuiListItemIcon: {
        //     styleOverrides: {
        //         root: { '.MuiDrawer-paper &': { color: '#ffffff' } },
        //     },
        // },
        // TODO: Confirmar si es manté o es lleva del tot aquests colors
        MuiListItemButton: {
            styleOverrides: {
                root: {
                    '.MuiDrawer-paper &:hover': { backgroundColor: alpha(DARK_PRIMARY_COLOR, 0.5)},
                    '.MuiDrawer-paper &.Mui-selected': { backgroundColor: alpha(DARK_PRIMARY_COLOR, 0.4) },
                    '.MuiDrawer-paper &.Mui-selected:hover': { backgroundColor: alpha(DARK_PRIMARY_COLOR, 0.5) },
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
                        backgroundColor: `${alpha(DARK_PRIMARY_COLOR, 0.15)} !important`,
                    },
                },
                row: {
                    '&.Mui-selected': { backgroundColor: `${alpha(DARK_PRIMARY_COLOR, 0.28)} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${alpha(DARK_PRIMARY_COLOR, 0.5)} !important` },
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                root:  ({ theme }: { theme: any }) => ({
                    ...(baseComponentStyles.MuiButton?.styleOverrides?.root as object),
                    variants: [
                        {
                            props: ({ color, variant }: any) =>
                                variant === 'outlined' && color === 'primary',
                            style: {
                                color: theme.palette.secondary.main,
                                borderColor: theme.palette.secondary.main,
                            },
                        },
                    ],
                }),
            },
        },
        MuiTab: {
            styleOverrides: {
                root: ({ theme }: { theme: any }) => ({
                    ...(baseComponentStyles.MuiTab?.styleOverrides?.root as object),
                    color: theme.palette.text.primary,
                    '&.Mui-selected': { color: theme.palette.primary.light },
                }),
            },
        },
    },
});

// ── 3. TEMA DRÀCULA ────────────────────────────────────────────────────────
export const draculaTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: {light: '#caabf7', main: '#BD93F9', contrastText: '#282A36' },
        secondary: { main: '#F8F8F2' },
        background: { default: '#282A36', paper: '#303341' },
        text: { primary: '#F8F8F2', secondary: '#D6D6C2' },
        divider: '#44475A',
        error: { main: '#FF5555' },
        warning: { main: '#FFB86C' },
        success: { main: '#50FA7B' },
        info: { main: '#8BE9FD' },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                body: {
                    backgroundColor: '#282A36',
                    backgroundImage: hatchPattern('#44475A'),
                    color: '#F8F8F2',
                },
            },
        },
        MuiOutlinedInput: {
            styleOverrides: {
                input: { '&:-webkit-autofill': { WebkitBoxShadow: '0 0 0 100px #3b3d4b inset' } },
            },
        },
        MuiListItemButton: {
            styleOverrides: {
                root: {
                    '.MuiDrawer-paper &:hover': { backgroundColor: alpha('#BD93F9', 0.35)},
                    '.MuiDrawer-paper &.Mui-selected': { backgroundColor: alpha('#BD93F9', 0.3) },
                    '.MuiDrawer-paper &.Mui-selected:hover': { backgroundColor: alpha('#BD93F9', 0.4) },
                },
            },
        },
        MuiAlert: {
            styleOverrides: {
                standardWarning: { backgroundColor: '#3e2f20' },
                standardError: { backgroundColor: '#401c1c' },
                standardInfo: { backgroundColor: '#26373c' },
                standardSuccess: { backgroundColor: '#1c4024' },
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
                        backgroundColor: `${alpha('#BD93F9', 0.15)} !important`,
                    },
                },
                row: {
                    '&.Mui-selected': { backgroundColor: `${alpha('#BD93F9', 0.2)} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${alpha('#BD93F9', 0.3)} !important` },
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
                                borderColor: '#BD93F9',
                            },
                        },
                    ],
                },
            },
        },
        MuiTab: {
            styleOverrides: {
                root: ({ theme }: { theme: any }) => ({
                    ...(baseComponentStyles.MuiTab?.styleOverrides?.root as object),
                    color: theme.palette.text.primary,
                    '&.Mui-selected': { color: theme.palette.primary.light },
                }),
            },
        },
    },
});