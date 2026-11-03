import { createTheme, darken, emphasize, ThemeOptions, type Theme } from '@mui/material/styles';
import { lighten, alpha } from '@mui/material';
import type {} from '@mui/x-data-grid/themeAugmentation';
import type {} from '@mui/x-tree-view/themeAugmentation';
import type {} from '@mui/x-date-pickers/themeAugmentation';
import {
    HEADER_HEIGHT,
    ICON_SIZE,
    MENU_BORDER_WIDTH,
    MENU_COLLAPSED_BELOW,
    MENU_ICON_COLUMN,
    MENU_ITEM_PADDING_LEFT,
    MENU_WIDTH_COLLAPSED,
} from './util/layout';

// Estenem les interfícies de MUI per admetre el color atenuat de la identitat
declare module '@mui/material/styles' {
    interface Palette {
        /** Atenuat del color base de l'aplicació: fons de la capçalera de les graelles. */
        primarySoft: string;
    }
    interface PaletteOptions {
        primarySoft?: string;
    }
}

// Colors per defecte
export const DEFAULT_PRIMARY_COLOR = '#337ab7';
export const DEFAULT_SECONDARY_COLOR = '#f1efef';

// Identitat visual de RIPEA (colors de Drassana, igual que Distribucio, ConCSV i Notib). Per portar
// el disseny a una altra aplicació només cal canviar aquests tres valors:
//  - MAIN: color base. Capçaleres de pantalla, diàlegs i botons d'acció.
//  - DARK: "torrat", és el `primary.dark` de MUI: hover dels botons i, als temes clars, badges,
//    paginació, barres de desplaçament i text dels botons outlined (veure accentColor).
//  - SOFT: atenuat, és `palette.primarySoft`. Fons de la capçalera de les graelles al tema clar.
// Als temes foscos l'atenuat i el torrat s'inverteixen i es deriven de la base: l'atenuat és un to
// molt fosc (`darken`) i el torrat un to clar (`PRIMARY_LIGHT`, és el `primary.light` de MUI), perquè
// un torrat sobre fons fosc no té contrast. No hi ha cap altre color d'identitat.
const PRIMARY_MAIN = '#f9963e';
const PRIMARY_DARK = '#9c5a1f';
const PRIMARY_SOFT = '#FDF4EA';
const PRIMARY_LIGHT = lighten(PRIMARY_MAIN, 0.3);
// El color base és clar: amb text blanc no arriba ni a 3:1, així que el text que va a sobre (botons
// contained, xips, badges, capçaleres de diàleg del tema clar...) és fosc.
const PRIMARY_CONTRAST_TEXT = '#1e1e1e';

/**
 * Color "torrat" de la identitat: badges, paginació, barres de desplaçament, text dels botons
 * outlined... És `primary.dark` als temes clars; als foscos, un torrat sobre fons fosc no té
 * contrast i es fa servir `primary.light`. Tot el que ha de sortir "torrat" passa per aquí.
 */
export const accentColor = (theme: Theme): string =>
    theme.palette.mode === 'dark' ? theme.palette.primary.light : theme.palette.primary.dark;

/**
 * Color de farciment (fons) dels elements primaris petits amb text a sobre: pàgina seleccionada de la
 * paginació, badges i xips. Als temes clars és el mateix «torrat» (amb text blanc); als foscos,
 * `accentColor` (to clar) no dona prou contrast amb el text, així que es fa servir el color base.
 */
export const accentFillColor = (theme: Theme): string =>
    theme.palette.mode === 'dark' ? theme.palette.primary.main : theme.palette.primary.dark;

// Files de les graelles als temes foscos (Obscur i Dracula comparteixen la base): el hover és un to
// suau; la fila seleccionada, un to fosc, i amb el cursor a sobre una mica més viu.
const GRID_ROW_HOVER_DARK = alpha(lighten(PRIMARY_MAIN, 0.05), 0.24);
const GRID_ROW_SELECTED_DARK = alpha(darken(PRIMARY_MAIN, 0.4), 0.8);
const GRID_ROW_SELECTED_HOVER_DARK = alpha(darken(PRIMARY_MAIN, 0.2), 0.9);
const GRID_ROW_CHECKED_DARK = PRIMARY_SOFT;

// Vora inferior de la capçalera de les graelles: degradat del color base al torrat. El fons de la
// capçalera no es fixa aquí sinó a `palette.DataGrid.headerBg` de cada tema (és la via de MUI X, que
// també acoloreix el filler i el buit de la barra de desplaçament).
//
// Es declara a part, i no dins `baseComponentStyles`, perquè cada tema redefineix
// `MuiDataGrid.styleOverrides` sencer i l'ha de referenciar explícitament.
const gridColumnHeadersOverride = ({ theme }: { theme: Theme }) => ({
    position: 'relative' as const,
    '&::after': {
        content: '""',
        position: 'absolute' as const,
        left: 0,
        right: 0,
        bottom: 0,
        height: '3px',
        background: `linear-gradient(90deg, ${theme.palette.primary.main} 25%, ${accentColor(theme)} 75%)`,
        pointerEvents: 'none' as const,
    },
});

// Barres de desplaçament de tota l'aplicació (i de les graelles, que hereten la propietat) al color
// "torrat" del tema. `scrollbar-color` és herència CSS, per això n'hi ha prou amb posar-lo a `html`.
const scrollbarOverrides = (thumbColor: string) => ({
    html: { scrollbarColor: `${thumbColor} transparent` },
});

// Patró de línies en diàgonal per al fons
const hatchPattern = (lineColor: string) =>
    `repeating-linear-gradient(45deg, transparent 0, transparent 0.75px, ${lineColor} 1.25px, transparent 1.75px, transparent 3px)`;

// ── PALETA DEL CALENDARI (FullCalendar, tema "monarch") ──────────────────────
//
// El calendari no es pinta amb la paleta de MUI sinó amb variables CSS pròpies, i el paquet
// només en porta dues versions —clara i fosca, de color morat— dins palettes/purple.css.
// FullCalendar tria entre l'una i l'altra amb l'atribut data-color-scheme, al qual li passem
// theme.palette.mode (veure TascaCalendar.tsx). Això deixava dos problemes: el calendari no
// seguia els colors de la identitat (taronja), i com que Dracula també és mode 'dark' es veia igual
// que el tema obscur. Cada tema declara ara la seva paleta amb aquesta funció.
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
            '.multi-line-cell': {
                display: 'flex',
                alignItems: 'baseline !important',
                whiteSpace: 'break-spaces !important',
            },
            // Sense marges ni padding verticals: la separació amb la barra de títol i la graella és el
            // `gap` de 15px (MAIN_GAP) del cos de ScreenFrame.
            '.styledFilter': {
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
    // Menú lateral. La llibreria en té la geometria cablejada (espaiador superior de 64px, plegat a
    // 57px, icones de 20px) i no es pot tocar; s'ajusta aquí, al tema, i no des de BaseApp.tsx, perquè
    // en pantalles estretes la llibreria pinta el menú en un portal, fora del <nav> i de qualsevol
    // embolcall de l'aplicació: només els overrides de tema hi arriben. Les mides surten de
    // util/layout.ts. El color de les entrades segueix a BaseApp.tsx (depèn del perfil de l'usuari).
    MuiDrawer: {
        styleOverrides: {
            // Menú permanent: la llibreria el plega a 57px i en reserva aquest espai al layout
            docked: { minWidth: `${MENU_WIDTH_COLLAPSED}px` },
            paper: {
                right: 'auto',
                left: 0,
                minWidth: `${MENU_WIDTH_COLLAPSED}px`,
                // El menú ha de començar sota la capçalera, que és més alta que els 64px de la llibreria
                '& > .MuiBox-root:first-of-type': { marginTop: `${HEADER_HEIGHT}px` },
                '& .menu-item-icon .MuiIcon-root': { fontSize: `${ICON_SIZE}px` },
                // Cada entrada amaga el text només quan el menú és estret, sense haver de saber-ne
                // l'estat. El contenidor de la consulta és la llista i NO el paper: un contenidor crea
                // un bloc contenidor per als descendents `position: fixed`, i el panell flotant dels
                // submenús del menú plegat (veure més avall) quedaria tallat dins el paper.
                '& .MuiList-root': { containerType: 'inline-size' as const },
                // Plegat: la llibreria hi centra les icones a `espai útil / 2`, sense dependre de la
                // mida de la icona, que és la columna menys mig píxel per la vora del menú
                // (MENU_BORDER_WIDTH). La icona du un marge esquerre de -8px que aquí se li treu un
                // píxel perquè el centre caigui just a MENU_ICON_COLUMN. En plegat la llibreria
                // embolcalla la llista dins un <div> (contenidor del panell flotant).
                [`@container (max-width: ${MENU_COLLAPSED_BELOW}px)`]: {
                    '& .MuiListItemText-root': { display: 'none' },
                    // Les entrades amb enllaç van dins un <li> (Link de BaseApp.tsx); els grups, no
                    '& > div > .MuiList-root > .MuiListItemButton-root > .menu-item-icon, & > div > .MuiList-root > li > .MuiListItemButton-root > .menu-item-icon': {
                        marginLeft: `${-8 + MENU_BORDER_WIDTH}px`,
                    },
                },
                // Obert: la llibreria deixa la icona a `padding + marge` (24 + 8 = 32px de vora
                // esquerra), i amb la mida nova el centre cauria a 44px. Es retoca el marge de les
                // entrades de nivell 0 perquè el centre sigui a MENU_ICON_COLUMN, com en plegat.
                [`@container (min-width: ${MENU_COLLAPSED_BELOW + 1}px)`]: {
                    '& > .MuiList-root > .MuiListItemButton-root > .menu-item-icon, & > .MuiList-root > li > .MuiListItemButton-root > .menu-item-icon': {
                        marginLeft: `${MENU_ICON_COLUMN - ICON_SIZE / 2 - MENU_ITEM_PADDING_LEFT}px`,
                    },
                },
                // Menú plegat: el panell flotant amb els fills d'una entrada (`position: fixed`, germà de
                // la llista) la llibreria el col·loca a 64px de dalt i 57px d'esquerra (capçalera i menú
                // plegat de la llibreria): s'ajusta a la capçalera i al menú plegat d'aquesta aplicació.
                '& > div > .MuiBox-root': {
                    top: `${HEADER_HEIGHT}px`,
                    left: `${MENU_WIDTH_COLLAPSED}px`,
                },
            },
        },
    },
    // Entrada seleccionada del menú: icona en torrat, vora esquerra del color de la barra de títol de
    // ScreenFrame i guia dels submenús al mateix color (la dibuixa la llibreria en blau fix). Als estils
    // de menú amb colors propis la pren de l'accent (veure BaseApp.tsx).
    MuiListItemButton: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                // `menu-item-icon` és la classe que la llibreria posa al ListItemIcon del menú
                '&.Mui-selected .menu-item-icon': { color: accentColor(theme) },
                '&.Mui-selected': { boxShadow: `inset 4px 0 0 ${theme.palette.primary.main}` },
                '&.Mui-selected :before': { background: theme.palette.primary.main },
            }),
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
    // Els camps de data no són MuiInputBase sinó MuiPickersInputBase, així que no reben el fontSize
    // de sobre i es queden amb els 16px per defecte de MUI: això els fa 40px d'alt contra els 37px
    // de la resta de camps i els desalinea dins d'una mateixa fila. El botó del calendari s'ajusta
    // com el dels desplegables (2px de padding, 28px finals) perquè càpiga dins del camp ja ajustat.
    MuiPickersInputBase: {
        styleOverrides: {
            root: {
                fontSize: '14px',
                '& .MuiInputAdornment-root .MuiIconButton-root': { padding: '2px' },
            },
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
    // Identitat: paginació, badges i xips primaris en el color "torrat" (veure accentFillColor). El
    // botó, l'enllaç i la resta de components primaris es queden al base.
    MuiChip: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                '&.MuiChip-sizeSmall .MuiChip-label': { fontSize: '14px' },
                '&.MuiChip-sizeMedium .MuiChip-label': { fontSize: '16px' },
                '&.MuiChip-colorPrimary:not(.MuiChip-outlined)': {
                    backgroundColor: accentFillColor(theme),
                    color: theme.palette.getContrastText(accentFillColor(theme)),
                },
            }),
        },
    },
    MuiPaginationItem: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                '&.Mui-selected': {
                    backgroundColor: accentFillColor(theme),
                    color: theme.palette.getContrastText(accentFillColor(theme)),
                    '&:hover': { backgroundColor: accentFillColor(theme), filter: 'brightness(1.15)' },
                },
            }),
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
    // Separació entre els botons de les finestres modals: el marge esquerre que MUI posa entre germans
    // quedaria a la vora exterior del bloc si s'inverteix l'ordre, així que es fa amb `gap`.
    MuiDialogActions: {
        styleOverrides: {
            root: {
                gap: '8px',
                '& > :not(style) ~ :not(style)': {
                    marginLeft: 0,
                },
            },
        },
    },
    // Fulls de l'arbre amb el color de la identitat (hover, seleccionat, seleccionat amb focus).
    MuiTreeItem: {
        styleOverrides: {
            content: ({ theme }: { theme: Theme }) => ({
                '&:hover': {
                    backgroundColor: alpha(theme.palette.primary.main, 0.1),
                },
                '&.Mui-selected': {
                    backgroundColor: alpha(theme.palette.primary.main, 0.15),
                    '&:hover': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.2),
                    },
                    '&.Mui-focused': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.25),
                    },
                },
            }),
        },
    },
    MuiDialogContent: {
        styleOverrides: {
            root: {
                // El títol és una barra acolorida: el seu padding no separa el contingut.
                // lib/Dialog força pt:0 i el botó de tancar queda entre títol i contingut ('~', no '+').
                '.MuiDialogTitle-root ~ &': {
                    paddingTop: '8px',
                },
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
            badge: ({ theme }: { theme: Theme }) => ({
                '.MuiDrawer-paper &': {
                    overflowWrap: 'normal',
                    wordBreak: 'keep-all',
                    whiteSpace: 'nowrap',
                    padding: '0px 4px',
                },
                '&.MuiBadge-colorPrimary': {
                    backgroundColor: accentFillColor(theme),
                    color: theme.palette.getContrastText(accentFillColor(theme)),
                },
            }),
        },
    },
};

// ── TEMA CLAR (LIGHT) ────────────────────────────────────────────────────
const LIGHT_PRIMARY_MAIN = PRIMARY_MAIN;
const LIGHT_PRIMARY_CONTRAST_TEXT = PRIMARY_CONTRAST_TEXT;
const LIGHT_SECONDARY_MAIN = '#2E2E2E';
const LIGHT_BACKGROUND_DEFAULT = '#ffffff';
const LIGHT_BACKGROUND_PAPER = '#ffffff';
const LIGHT_TEXT_PRIMARY = '#1e1e1e';
const LIGHT_TEXT_SECONDARY = '#666666';
const LIGHT_DIVIDER = '#e0e0e0';
// El tema clar no declara `error` a la paleta, així que fa servir el vermell per defecte de MUI
// (red[700]). Es repeteix aquí perquè el calendari hi marca el dia i l'hora actuals.
const LIGHT_ERROR_MAIN = '#d32f2f';

const lightCalendarPalette = calendarPalette({
    primary: LIGHT_PRIMARY_MAIN,
    primaryForeground: LIGHT_PRIMARY_CONTRAST_TEXT,
    // Taronja molt pàl·lid: de fons del dia actual ha de deixar llegir el text a sobre.
    secondary: lighten(LIGHT_PRIMARY_MAIN, 0.88),
    secondaryForeground: darken(PRIMARY_DARK, 0.2),
    tertiary: LIGHT_PRIMARY_MAIN,
    tertiaryForeground: LIGHT_PRIMARY_CONTRAST_TEXT,
    selected: LIGHT_PRIMARY_MAIN,
    selectedForeground: LIGHT_PRIMARY_CONTRAST_TEXT,
    outline: LIGHT_PRIMARY_MAIN,
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
        primary: { main: LIGHT_PRIMARY_MAIN, dark: PRIMARY_DARK, light: PRIMARY_LIGHT, contrastText: LIGHT_PRIMARY_CONTRAST_TEXT },
        secondary: { main: LIGHT_SECONDARY_MAIN },
        background: { default: LIGHT_BACKGROUND_DEFAULT, paper: LIGHT_BACKGROUND_PAPER },
        text: { primary: LIGHT_TEXT_PRIMARY, secondary: LIGHT_TEXT_SECONDARY },
        // Ha de ser el negre translúcid per defecte de MUI (com a les altres apps de Drassana), no un
        // gris opac: MUI X en deriva el color de les vores de la graella (separadors de columna,
        // línies de cel·la) amb `lighten(alpha(divider, 1), 0.88)`, i amb #e0e0e0 queda gairebé blanc.
        divider: 'rgba(0, 0, 0, 0.12)',
        primarySoft: PRIMARY_SOFT,
        DataGrid: { headerBg: PRIMARY_SOFT },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                ...lightCalendarPalette,
                ...scrollbarOverrides(PRIMARY_DARK),
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
                    backgroundColor: LIGHT_PRIMARY_MAIN,
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
                    '&.detail': { backgroundColor: PRIMARY_SOFT },
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
                columnHeaders: gridColumnHeadersOverride,
            },
        },
        MuiTab: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiTab?.styleOverrides?.root as object),
                    color: LIGHT_TEXT_PRIMARY,
                    // El color base és massa clar per a text sobre blanc: pestanya seleccionada en torrat
                    '&.Mui-selected': { color: PRIMARY_DARK },
                },
            },
        },
        // Els enllaços també: el color base sobre blanc no arriba a 3:1.
        MuiLink: {
            defaultProps: { color: 'primary.dark' },
        },
        // Pel mateix motiu, el text i la vora dels botons outlined/text van en torrat (els contained
        // porten el color base amb text fosc, veure PRIMARY_CONTRAST_TEXT).
        MuiButton: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiButton?.styleOverrides?.root as object),
                    '&.MuiButton-outlinedPrimary, &.MuiButton-textPrimary': { color: PRIMARY_DARK },
                    '&.MuiButton-outlinedPrimary': { borderColor: alpha(PRIMARY_DARK, 0.5) },
                },
            },
        },
    },
});

// ── TEMA FOSC (DARK) ─────────────────────────────────────────────────────
const DARK_PRIMARY_LIGHT = PRIMARY_LIGHT;
const DARK_PRIMARY_MAIN = PRIMARY_MAIN;
// Opac a propòsit: la capçalera de la graella és enganxosa i les files hi passen per sota.
const DARK_PRIMARY_SOFT = darken(PRIMARY_MAIN, 0.7);
const DARK_PRIMARY_CONTRAST_TEXT = '#fff';
const DARK_SECONDARY_MAIN = '#F6F6F6';
const DARK_BACKGROUND_DEFAULT = '#121212';
const DARK_BACKGROUND_PAPER = '#1e1e1e';
const DARK_TEXT_PRIMARY = '#ffffff';
const DARK_TEXT_SECONDARY = '#bbbbbb';
// El divisor per defecte de MUI als temes foscos (com a les altres aplicacions de Drassana): amb el blanc
// pur que hi havia, les vores que en depenen (barra de títol del ScreenFrame, separadors...) es veien
// com una línia blanca.
const DARK_DIVIDER = 'rgba(255, 255, 255, 0.12)';
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
    // Sobre fons fosc l'esdeveniment ha de ser el taronja clar (`primary.light`) i no el base:
    // aquest darrer amb prou feines es distingiria del fons. El text a sobre, per tant, va en fosc.
    primary: DARK_PRIMARY_LIGHT,
    primaryForeground: DARK_BACKGROUND_DEFAULT,
    secondary: darken(DARK_PRIMARY_MAIN, 0.5),
    secondaryForeground: DARK_TEXT_PRIMARY,
    tertiary: DARK_PRIMARY_MAIN,
    tertiaryForeground: DARK_BACKGROUND_DEFAULT,
    selected: DARK_PRIMARY_MAIN,
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
    // DARK_DIVIDER és massa tènue per a la vora de les cel·les del calendari: se'n deriva de la superfície.
    border: lighten(DARK_CALENDAR_BG, 0.18),
    strongBorder: lighten(DARK_CALENDAR_BG, 0.4),
});

export const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { light: DARK_PRIMARY_LIGHT, main: DARK_PRIMARY_MAIN, dark: PRIMARY_DARK, contrastText: PRIMARY_CONTRAST_TEXT },
        secondary: { main: DARK_SECONDARY_MAIN },
        background: { default: DARK_BACKGROUND_DEFAULT, paper: DARK_BACKGROUND_PAPER },
        text: { primary: DARK_TEXT_PRIMARY, secondary: DARK_TEXT_SECONDARY },
        divider: DARK_DIVIDER,
        error: { main: DARK_ERROR_MAIN },
        info: { main: DARK_INFO_MAIN },
        success: { main: DARK_SUCCESS_MAIN },
        primarySoft: DARK_PRIMARY_SOFT,
        DataGrid: { headerBg: DARK_PRIMARY_SOFT },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                ...darkCalendarPalette,
                ...scrollbarOverrides(DARK_PRIMARY_LIGHT),
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
        // El text d'ajuda d'un camp desactivat (p. ex. el nom de la propietat a Configuració) és
        // informació, no un control: amb el color de "desactivat" (blanc al 50%) queda a 4,41:1.
        MuiFormHelperText: {
            styleOverrides: {
                root: { '&.Mui-disabled': { color: DARK_TEXT_SECONDARY } },
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
                    backgroundColor: darken(DARK_PRIMARY_MAIN, 0.5),
                    color: DARK_TEXT_PRIMARY,
                },
            },
        },
        MuiCardHeader: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiCardHeader?.styleOverrides?.root as object),
                    backgroundColor: DARK_BACKGROUND_PAPER,
                    color: DARK_SECONDARY_MAIN,
                    borderBottom: `1px solid ${DARK_DIVIDER}`,
                    '&.detail': { backgroundColor: DARK_PRIMARY_SOFT },
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
                    '& .MuiDataGrid-row:not(.Mui-selected):hover': {
                        backgroundColor: `${GRID_ROW_HOVER_DARK} !important`,
                    },
                    // Casella marcada d'una fila seleccionada: gairebé blanca, es veu sobre el fons fosc
                    '& .MuiDataGrid-row.Mui-selected .MuiCheckbox-root.Mui-checked': {
                        color: `${GRID_ROW_CHECKED_DARK} !important`,
                    },
                    // Enllaços d'una fila seleccionada: el seu taronja es confon amb el fons de la fila;
                    // en el color del text es distingeixen pel subratllat.
                    '& .MuiDataGrid-row.Mui-selected .MuiLink-root': {
                        color: `${DARK_TEXT_PRIMARY} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${GRID_ROW_SELECTED_DARK} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${GRID_ROW_SELECTED_HOVER_DARK} !important` },
                },
                columnHeaders: gridColumnHeadersOverride,
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
        // Enllaços en el to clar de la identitat
        MuiLink: {
            defaultProps: { color: 'primary.light' },
        },
    },
});

// ── TEMA DRÀCULA ────────────────────────────────────────────────────────
const DRACULA_PRIMARY_LIGHT = PRIMARY_LIGHT;
const DRACULA_PRIMARY_MAIN = PRIMARY_MAIN;
// Opac a propòsit: la capçalera de la graella és enganxosa i les files hi passen per sota.
const DRACULA_PRIMARY_SOFT = darken(PRIMARY_MAIN, 0.6);
const DRACULA_PRIMARY_CONTRAST_TEXT = '#fff';
const DRACULA_SECONDARY_MAIN = '#F8F8F2';
const DRACULA_BACKGROUND_DEFAULT = '#282A36';
const DRACULA_BACKGROUND_PAPER = '#303341';
const DRACULA_TEXT_PRIMARY = '#F8F8F2';
const DRACULA_TEXT_SECONDARY = '#D6D6C2';
const DRACULA_DIVIDER = '#44475a';
const DRACULA_ERROR_MAIN = '#FF5555';
const DRACULA_WARNING_MAIN = '#FFB86C';
const DRACULA_SUCCESS_MAIN = '#50FA7B';
const DRACULA_INFO_MAIN = '#8BE9FD';
// Colors de la paleta oficial de Dracula que no formen part de la paleta MUI però sí de la
// del calendari: "current line" (fons de ressaltat i vores) i "comment" (grisos i text apagat).
const DRACULA_CURRENT_LINE = '#44475A';
const DRACULA_COMMENT = '#6272A4';
// Superfície del calendari: el color de "paper" apujat un punt (veure la nota del tema fosc).
const DRACULA_CALENDAR_BG = lighten(DRACULA_BACKGROUND_PAPER, 0.08);

const draculaCalendarPalette = calendarPalette({
    primary: DRACULA_PRIMARY_MAIN,
    primaryForeground: DRACULA_BACKGROUND_DEFAULT,
    secondary: DRACULA_CURRENT_LINE,
    secondaryForeground: DRACULA_TEXT_PRIMARY,
    tertiary: DRACULA_PRIMARY_MAIN,
    tertiaryForeground: DRACULA_BACKGROUND_DEFAULT,
    selected: DRACULA_PRIMARY_MAIN,
    selectedForeground: DRACULA_BACKGROUND_DEFAULT,
    outline: DRACULA_PRIMARY_MAIN,
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
        primary: { light: DRACULA_PRIMARY_LIGHT, main: DRACULA_PRIMARY_MAIN, dark: PRIMARY_DARK, contrastText: PRIMARY_CONTRAST_TEXT },
        secondary: { main: DRACULA_SECONDARY_MAIN },
        background: { default: DRACULA_BACKGROUND_DEFAULT, paper: DRACULA_BACKGROUND_PAPER },
        text: { primary: DRACULA_TEXT_PRIMARY, secondary: DRACULA_TEXT_SECONDARY },
        divider: DRACULA_DIVIDER,
        error: { main: DRACULA_ERROR_MAIN },
        warning: { main: DRACULA_WARNING_MAIN },
        success: { main: DRACULA_SUCCESS_MAIN },
        info: { main: DRACULA_INFO_MAIN },
        primarySoft: DRACULA_PRIMARY_SOFT,
        DataGrid: { headerBg: DRACULA_PRIMARY_SOFT },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                ...draculaCalendarPalette,
                ...scrollbarOverrides(DRACULA_PRIMARY_LIGHT),
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
        // Veure el comentari del tema fosc: el text d'ajuda desactivat és informació.
        MuiFormHelperText: {
            styleOverrides: {
                root: { '&.Mui-disabled': { color: DRACULA_TEXT_SECONDARY } },
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
                    backgroundColor: darken(DRACULA_PRIMARY_MAIN, 0.5),
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
                    borderBottom: `1px solid ${DRACULA_DIVIDER}`,
                    '&.detail': { backgroundColor: DRACULA_PRIMARY_SOFT },
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
                    '& .MuiDataGrid-row:not(.Mui-selected):hover': {
                        backgroundColor: `${GRID_ROW_HOVER_DARK} !important`,
                    },
                    // Casella marcada d'una fila seleccionada: gairebé blanca, es veu sobre el fons fosc
                    '& .MuiDataGrid-row.Mui-selected .MuiCheckbox-root.Mui-checked': {
                        color: `${GRID_ROW_CHECKED_DARK} !important`,
                    },
                    // Enllaços d'una fila seleccionada: el seu taronja es confon amb el fons de la fila;
                    // en el color del text es distingeixen pel subratllat.
                    '& .MuiDataGrid-row.Mui-selected .MuiLink-root': {
                        color: `${DRACULA_TEXT_PRIMARY} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${GRID_ROW_SELECTED_DARK} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${GRID_ROW_SELECTED_HOVER_DARK} !important` },
                },
                columnHeaders: gridColumnHeadersOverride,
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
                                color: DRACULA_SECONDARY_MAIN,
                                borderColor: DRACULA_SECONDARY_MAIN,
                            },
                        },
                    ],
                    '.MuiDialog-paper &.MuiButton-outlinedPrimary': {
                         borderColor: DRACULA_SECONDARY_MAIN,
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
            defaultProps: { color: 'primary.light' },
        },
    },
});
