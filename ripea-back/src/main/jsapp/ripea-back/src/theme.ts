import { createTheme, darken, emphasize, ThemeOptions } from '@mui/material/styles';
import { lighten, alpha } from '@mui/material';
import type {} from '@mui/x-data-grid/themeAugmentation';

// Colors per defecte
export const DEFAULT_PRIMARY_COLOR = '#337ab7';
export const DEFAULT_SECONDARY_COLOR = '#f1efef';

// Patró de línies en diàgonal per al fons
const hatchPattern = (lineColor: string) =>
    `repeating-linear-gradient(45deg, transparent 0, transparent 0.75px, ${lineColor} 1.25px, transparent 1.75px, transparent 3px)`;

// ── PALETA DEL CALENDARI (FullCalendar, tema "monarch") ──────────────────────
//
// El calendari no es pinta amb la paleta de MUI sinó amb variables CSS pròpies, i el paquet
// només en porta dues versions —clara i fosca, de color morat— dins palettes/purple.css.
// FullCalendar tria entre l'una i l'altra amb l'atribut data-color-scheme, al qual li passem
// theme.palette.mode (veure TascaCalendar.tsx). Això deixava dos problemes: el calendari no
// seguia el blau corporatiu, i com que Dracula també és mode 'dark' es veia igual que el tema
// obscur. Cada tema declara ara la seva paleta amb aquesta funció.
//
// Dos detalls del selector:
//  - Porta "html" al davant només per pujar l'especificitat per damunt de la paleta del paquet
//    ([data-color-scheme=dark]) i no dependre de l'ordre en què es carreguin els fulls d'estil.
//  - S'aplica a qualsevol [data-color-scheme], no només al del calendari, perquè FullCalendar
//    posa l'atribut tant a l'arrel del calendari com als popovers de "+N més" i a l'element
//    fantasma d'arrossegar, que es renderitzen fora (portal) i no en són descendents.
// El @media not print replica el del paquet: en imprimir es torna a la paleta clara.
type CalendarPalette = {
    /** Fons dels esdeveniments i dels botons principals. */
    primary: string;
    primaryForeground: string;
    /** Botons secundaris; també és la base del ressaltat del dia actual i de la selecció. */
    secondary: string;
    secondaryForeground: string;
    /** Botons d'accent de la barra d'eines. */
    tertiary: string;
    tertiaryForeground: string;
    /** Botó de vista actiu (mes/setmana/any). */
    selected: string;
    selectedForeground: string;
    /** Anell de focus. */
    outline: string;
    /** Marca del dia i de l'hora actuals. */
    now: string;
    background: string;
    /** Fons del desplegable "+N més". */
    popover: string;
    foreground: string;
    mutedForeground: string;
    /** Text més apagat que el "muted" (nombres de setmana, dies d'altres mesos...). */
    faintForeground: string;
    /** Base dels fons translúcids: capçaleres, hover de les cel·les, franges horàries... */
    neutral: string;
    border: string;
    strongBorder: string;
};
const calendarPalette = (c: CalendarPalette) => ({
    '@media not print': {
        'html [data-color-scheme]': {
            // Els estats "over" (passar-hi per damunt) i "down" (prémer) s'obtenen amb
            // emphasize(), que aclareix els colors foscos i enfosqueix els clars: és el mateix
            // criteri que segueix la paleta del paquet, on tot tendeix cap al to mitjà.
            '--fc-monarch-primary': c.primary,
            '--fc-monarch-primary-foreground': c.primaryForeground,
            '--fc-monarch-primary-over': emphasize(c.primary, 0.1),
            '--fc-monarch-primary-down': emphasize(c.primary, 0.2),
            '--fc-monarch-secondary': c.secondary,
            '--fc-monarch-secondary-foreground': c.secondaryForeground,
            '--fc-monarch-secondary-over': emphasize(c.secondary, 0.1),
            '--fc-monarch-secondary-down': emphasize(c.secondary, 0.2),
            '--fc-monarch-tertiary': c.tertiary,
            '--fc-monarch-tertiary-foreground': c.tertiaryForeground,
            '--fc-monarch-tertiary-over': emphasize(c.tertiary, 0.1),
            '--fc-monarch-tertiary-down': emphasize(c.tertiary, 0.2),
            // Contingut del calendari. Els esdeveniments de tasques porten color propi segons
            // la data límit (veure TascaCalendar.renderEvent), així que aquestes dues només
            // afecten els que no en tenen.
            '--fc-monarch-event': 'var(--fc-monarch-primary)',
            '--fc-monarch-event-contrast': 'var(--fc-monarch-primary-foreground)',
            '--fc-monarch-highlight': alpha(c.secondary, 0.3),
            '--fc-monarch-now': c.now,
            // Controls
            '--fc-monarch-selected': c.selected,
            '--fc-monarch-selected-foreground': c.selectedForeground,
            '--fc-monarch-selected-over': emphasize(c.selected, 0.1),
            '--fc-monarch-selected-down': emphasize(c.selected, 0.2),
            '--fc-monarch-outline': c.outline,
            // Popover
            '--fc-monarch-popover': c.popover,
            '--fc-monarch-popover-foreground': c.foreground,
            // Fons neutres
            '--fc-monarch-background': c.background,
            '--fc-monarch-faint': alpha(c.neutral, 0.1),
            '--fc-monarch-muted': alpha(c.neutral, 0.15),
            '--fc-monarch-strong': alpha(c.neutral, 0.3),
            '--fc-monarch-stronger': alpha(c.neutral, 0.35),
            '--fc-monarch-strongest': alpha(c.neutral, 0.4),
            // Textos neutres
            '--fc-monarch-foreground': c.foreground,
            '--fc-monarch-faint-foreground': c.faintForeground,
            '--fc-monarch-muted-foreground': c.mutedForeground,
            // Vores
            '--fc-monarch-border': c.border,
            '--fc-monarch-strong-border': c.strongBorder,
        },
    },
});

// ── CONFIGURACIÓ BASE D'ESTILS DE RIPEA ──────────────────────────────────────
// IMPORTANT: aquest objecte NO inclou `MuiCssBaseline.styleOverrides.body`
// perquè cada tema (light/dark/dracula) el defineix pel seu compte (colors de
// fons i hatch diferents per tema). Si mai s'afegeix una clau `body` aquí,
// caldrà fer-ho amb cura, ja que als temes de sota es fa spread d'aquest
// objecte i després es sobreescriu `body` explícitament: qualsevol `body` que
// es posi aquí quedaria silenciosament ignorat.
// Vores dels botons del selector d'accions massives. Es volen neutres (no tenyides del color
// primari, com faria MUI amb els outlined) però amb el contrast que toca segons el tema: amb
// el negre fix que hi havia abans, als temes foscos els botons es veien sempre com si
// estiguessin deshabilitats encara que hi hagués files seleccionades. Els valors són els
// mateixos que fa servir MUI per als botons outlined (0.23 normal, 0.5 hover, 0.12 disabled),
// de manera que l'estat actiu i el deshabilitat es distingeixen.
const massiveSelectorBorders = (rgb: string) => ({
    '& .MuiButton-root': { borderColor: `rgba(${rgb}, 0.23)` },
    '& .MuiButton-root:hover': { borderColor: `rgba(${rgb}, 0.5)` },
    '& .MuiButton-root.Mui-disabled': { borderColor: `rgba(${rgb}, 0.12)` },
});

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
                ...massiveSelectorBorders('0, 0, 0'),
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
// El tema clar no declara `error` a la paleta, així que fa servir el vermell per defecte de MUI
// (red[700]). Es repeteix aquí perquè el calendari hi marca el dia i l'hora actuals.
const LIGHT_ERROR_MAIN = '#d32f2f';
// Blau corporatiu aclarit: és el que acaba sent palette.primary.main del tema.
const LIGHT_PRIMARY = lighten(LIGHT_PRIMARY_MAIN, 0.2);

const lightCalendarPalette = calendarPalette({
    primary: LIGHT_PRIMARY,
    primaryForeground: LIGHT_PRIMARY_CONTRAST_TEXT,
    // Blau molt pàl·lid: de fons del dia actual ha de deixar llegir el text a sobre.
    secondary: lighten(LIGHT_PRIMARY_MAIN, 0.88),
    secondaryForeground: darken(LIGHT_PRIMARY_MAIN, 0.2),
    tertiary: LIGHT_PRIMARY_MAIN,
    tertiaryForeground: LIGHT_PRIMARY_CONTRAST_TEXT,
    selected: LIGHT_SECONDARY_MAIN,
    selectedForeground: LIGHT_PRIMARY_CONTRAST_TEXT,
    outline: LIGHT_PRIMARY,
    now: LIGHT_ERROR_MAIN,
    // Color de "paper": el calendari va dins la targeta de la pàgina (veure la nota del tema
    // fosc, on la diferència sí que es nota; aquí totes dues superfícies són blanques).
    background: LIGHT_BACKGROUND_PAPER,
    popover: LIGHT_BACKGROUND_PAPER,
    foreground: LIGHT_TEXT_PRIMARY,
    mutedForeground: LIGHT_TEXT_SECONDARY,
    // Just un punt més apagat que el "muted": aquest color pinta els dies dels mesos veïns,
    // que són text, i ha de mantenir el 4,5:1 de la WCAG AA sobre el fons (4,61:1).
    faintForeground: lighten(LIGHT_TEXT_SECONDARY, 0.1),
    neutral: LIGHT_TEXT_SECONDARY,
    border: LIGHT_DIVIDER,
    strongBorder: darken(LIGHT_DIVIDER, 0.2),
});

export const lightTheme = createTheme({
    palette: {
        mode: 'light',
        primary: { main: LIGHT_PRIMARY, contrastText: LIGHT_PRIMARY_CONTRAST_TEXT },
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
                ...lightCalendarPalette,
                body: {
                    backgroundColor: LIGHT_BACKGROUND_DEFAULT,
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
                    backgroundColor: LIGHT_PRIMARY,
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

// Superfície del calendari. Es dibuixa DINS la targeta de la pàgina, no sobre el fons del
// document: amb background.default (més fosc que la targeta) es veia com un forat negre al mig,
// just al revés del que toca en un tema fosc, on les superfícies elevades han de ser més clares.
// Es parteix del color de "paper" i s'apuja un punt perquè el calendari se n'acabi de separar.
// El popover i les vores es deriven d'aquest color, no de la targeta, per no perdre'ls de vista
// quan es toqui: han de mantenir el contrast amb la superfície que tenen a sota.
const DARK_CALENDAR_BG = lighten(DARK_BACKGROUND_PAPER, 0.08);

const darkCalendarPalette = calendarPalette({
    // Sobre fons fosc l'esdeveniment ha de ser el blau clar, no el corporatiu: aquest darrer
    // amb prou feines es distingiria del fons. El text a sobre, per tant, va en fosc.
    primary: DARK_PRIMARY_LIGHT,
    primaryForeground: DARK_BACKGROUND_DEFAULT,
    secondary: DARK_PRIMARY_MAIN,
    secondaryForeground: DARK_TEXT_PRIMARY,
    tertiary: DARK_SECONDARY_MAIN,
    tertiaryForeground: DARK_BACKGROUND_DEFAULT,
    selected: DARK_TEXT_SECONDARY,
    selectedForeground: DARK_BACKGROUND_DEFAULT,
    outline: DARK_PRIMARY_LIGHT,
    now: DARK_ERROR_MAIN,
    background: DARK_CALENDAR_BG,
    popover: lighten(DARK_CALENDAR_BG, 0.1),
    foreground: DARK_TEXT_PRIMARY,
    mutedForeground: DARK_TEXT_SECONDARY,
    // Prou apagat per distingir-se del "muted", però mantenint el 4,5:1 de la WCAG AA sobre la
    // superfície del calendari (4,90:1): pinta els dies dels mesos veïns, que són text.
    faintForeground: darken(DARK_TEXT_SECONDARY, 0.15),
    neutral: DARK_TEXT_SECONDARY,
    // DARK_DIVIDER és blanc pur: com a vora de totes les cel·les seria un reticulat massa dur.
    border: lighten(DARK_CALENDAR_BG, 0.18),
    strongBorder: lighten(DARK_CALENDAR_BG, 0.4),
});

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
                ...darkCalendarPalette,
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
                '.massive-selector': {
                    ...((baseComponentStyles.MuiCssBaseline?.styleOverrides as any)['.massive-selector']),
                    ...massiveSelectorBorders('255, 255, 255'),
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
// Colors de la paleta oficial de Dracula que no formen part de la paleta MUI però sí de la
// del calendari: "current line" (fons de ressaltat i vores) i "comment" (grisos i text apagat).
const DRACULA_CURRENT_LINE = '#44475A';
const DRACULA_COMMENT = '#6272A4';
const DRACULA_PINK = '#FF79C6';
// Superfície del calendari: el color de "paper" apujat un punt (veure la nota del tema fosc).
const DRACULA_CALENDAR_BG = lighten(DRACULA_BACKGROUND_PAPER, 0.08);

const draculaCalendarPalette = calendarPalette({
    primary: DRACULA_PRIMARY_MAIN,
    primaryForeground: DRACULA_BACKGROUND_DEFAULT,
    secondary: DRACULA_CURRENT_LINE,
    secondaryForeground: DRACULA_TEXT_PRIMARY,
    tertiary: DRACULA_PINK,
    tertiaryForeground: DRACULA_BACKGROUND_DEFAULT,
    selected: DRACULA_COMMENT,
    selectedForeground: DRACULA_TEXT_PRIMARY,
    outline: DRACULA_PINK,
    now: DRACULA_ERROR_MAIN,
    background: DRACULA_CALENDAR_BG,
    popover: lighten(DRACULA_CALENDAR_BG, 0.1),
    foreground: DRACULA_TEXT_PRIMARY,
    mutedForeground: DRACULA_TEXT_SECONDARY,
    // El "comment" de Dracula tal qual es queda per sota del 4,5:1 de la WCAG AA sobre la
    // superfície del calendari, i pinta els dies dels mesos veïns, que són text: s'aclareix
    // fins a arribar-hi (4,96:1).
    faintForeground: lighten(DRACULA_COMMENT, 0.5),
    neutral: DRACULA_COMMENT,
    // El "current line" queda pràcticament igual que la superfície del calendari i el reticulat
    // desapareixeria: les vores es deriven de la superfície, com al tema fosc.
    border: lighten(DRACULA_CALENDAR_BG, 0.18),
    strongBorder: lighten(DRACULA_CALENDAR_BG, 0.4),
});

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
                ...draculaCalendarPalette,
                body: {
                    backgroundColor: DRACULA_BACKGROUND_DEFAULT,
                    backgroundImage: hatchPattern(DRACULA_CURRENT_LINE),
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
                '.massive-selector': {
                    ...((baseComponentStyles.MuiCssBaseline?.styleOverrides as any)['.massive-selector']),
                    ...massiveSelectorBorders('255, 255, 255'),
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
