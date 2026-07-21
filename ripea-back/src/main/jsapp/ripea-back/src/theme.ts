import {ThemeOptions, createTheme, darken, alpha, lighten} from '@mui/material/styles';
import type {} from '@mui/x-data-grid/themeAugmentation';

// Color principal per defecte: blau corporatiu equivalent al primary del tema
// clar "clàssic". És també el valor per defecte de la columna IPA_USUARI.COLOR_PRINCIPAL.
export const DEFAULT_PRIMARY_COLOR = '#337ab7';

// Color secundari per defecte: gris molt clar (neutre). L'usuari pot triar el
// seu propi color secundari. És també el valor per defecte de la columna
// IPA_USUARI.COLOR_SECUNDARI.
export const DEFAULT_SECONDARY_COLOR = '#f1efef';

// Diagonal hatch pattern reproduced with CSS (no image asset).
// Lines every 3px at 45deg. Colors are overridden per theme below.
// La línia es defineix amb una rampa suau simètrica (~0.5px a cada costat
// del pic) en lloc d'una vora dura. A 45° la separació perpendicular real
// és 3/√2 ≈ 2.12px, un valor fraccionari que no encaixa a la graella de
// píxels; amb vores dures cada línia cau en una fase sub-píxel diferent i
// el navegador les renderitza amb gruixos alternats (efecte moiré/batut).
// La transició gradual fa que totes les línies rebin el mateix anti-aliasing
// i el patró es vegi uniforme.
const hatchPattern = (line: string) =>
    `repeating-linear-gradient(45deg, transparent 0, transparent 0.75px, ${line} 1.25px, transparent 1.75px, transparent 3px)`;

// Estructura "base" del tema: tota la configuració NO cromàtica (radis,
// espaiats, tipografia, layout...). Els colors es defineixen al tema paramètric
// (buildTheme) i sobreescriuen el que calgui d'aquí.
const base: ThemeOptions = {
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                body: {
                    backgroundColor: '#ffffff',
                    backgroundImage: hatchPattern('#e1e1e1'),
                    color: '#666666'
                },
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
                    paddingLeft: '16px',
                    paddingRight: '16px',
                    borderRadius: '4px',
                    backgroundColor: 'inherit',
                },
                '.myLabel': {
                    padding: '4px 8px',
                    fontSize: '11px',
                    // fontWeight: '500',
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
                    alignSelf: 'end'
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
                '.massive-selector' : {
                    '& .MuiButton-root': {
                        borderColor: 'rgba(0, 0, 0, 0.23)' // Standard MUI outlined button border color
                    },
                    '& .MuiButton-root:hover': {
                        borderColor: 'rgba(0, 0, 0, 0.50)' // Standard MUI outlined button border color
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
                }
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
                        alignItems: 'center'
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
        MuiTab: {styleOverrides: {root: {textTransform: 'none', fontSize: '1rem', '&.Mui-disabled': {opacity: 0.4}}}},
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
                    '&:not(.MuiButtonGroup-grouped)': {marginLeft: '10px'},
                    '& .MuiButton-startIcon': {marginRight: '0'},
                    '&.Mui-disabled': {
                        opacity: 0.6,
                        cursor: 'not-allowed'
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
                root: {borderRadius: '0px'},
            },
        },
        MuiCardContent: {
            styleOverrides: {
                root: {
                    padding: '10px 16px',
                    '&:last-child': {paddingBottom: '10px'},
                },
            },
        },
        MuiTypography: {
            styleOverrides: {
                h5: {fontSize: '1.8rem', lineHeight: 1.2, fontWeight: 400},
                h4: {fontSize: '1.5rem', lineHeight: 1.2, fontWeight: 400},
                body1: {fontWeight: 500},
                overline: {fontSize: '1.2rem', letterSpacing: '0em', textTransform: 'none'},
            },
        },
        MuiInputBase: {
            styleOverrides: {
                root: {fontSize: '14px'},
            },
        },
        MuiFormLabel: {
            styleOverrides: {
                root: {
                    fontStyle: 'italic',
                    paddingRight: '2px',
                    fontSize: '14px',
                    fontWeight: 200,
                    '&.Mui-disabled': {fontStyle: 'italic', paddingRight: '2px'},
                    '&.Mui-focused': {fontStyle: 'italic', paddingRight: '2px'},
                },
                filled: {
                    fontStyle: 'italic',
                    paddingRight: '2px',
                    fontSize: '14px',
                    fontWeight: 200,
                    '&.Mui-disabled': {fontStyle: 'italic', paddingRight: '2px', opacity: 1},
                    '&.Mui-focused': {fontStyle: 'italic', paddingRight: '2px'},
                },
            },
        },
        MuiIcon: {styleOverrides: {root: {fontSize: 'var(--toolbar-icon-size, 18px)', marginRight: '4px'}}},
        MuiChip: {
            styleOverrides: {
                root: {
                    '&.MuiChip-sizeSmall .MuiChip-label': {fontSize: '14px'},
                    '&.MuiChip-sizeMedium .MuiChip-label': {fontSize: '16px'},
                },
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    padding: "5px 24px",
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                }
            }
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    borderRadius: '4px',
                }
            },
        },
        MuiCardHeader: {
            styleOverrides: {
                root: {
                    paddingTop: '8px',
                    paddingBottom: '8px',
                    paddingLeft: '16px',
                    paddingRight: '16px',
                }
            },
        },
    }
};

// ───────────────────────────────────────────────────────────────────────────
// Tema paramètric
// En lloc de dos temes (clar/fosc) hi ha un únic tema calculat a partir d'un
// nivell de foscor (0 = clar exacte, 1 = fosc exacte) i dels colors principal i
// secundari triats per l'usuari. Els colors "de base" (fons, superfícies,
// vores, accions...) s'interpolen entre el valor clar i el fosc; el text es
// calcula per contrast sobre el fons resultant.
// ───────────────────────────────────────────────────────────────────────────

const clamp01 = (v: number) => Math.min(1, Math.max(0, v));

const hexToRgb = (hex: string): [number, number, number] => {
    let h = hex.replace('#', '');
    if (h.length === 3) h = h.split('').map(c => c + c).join('');
    const n = parseInt(h, 16);
    return [(n >> 16) & 255, (n >> 8) & 255, n & 255];
};
const channelHex = (v: number) => Math.max(0, Math.min(255, Math.round(v))).toString(16).padStart(2, '0');
const rgbToHex = (r: number, g: number, b: number) => `#${channelHex(r)}${channelHex(g)}${channelHex(b)}`;

// Interpola dos colors hex en sRGB. t=0 -> a, t=1 -> b.
const mix = (a: string, b: string, t: number): string => {
    const [ar, ag, ab] = hexToRgb(a);
    const [br, bg, bb] = hexToRgb(b);
    return rgbToHex(ar + (br - ar) * t, ag + (bg - ag) * t, ab + (bb - ab) * t);
};

// Lluminositat relativa (WCAG) 0..1.
const luminance = (hex: string): number => {
    const [r, g, b] = hexToRgb(hex).map(v => {
        const c = v / 255;
        return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    });
    return 0.2126 * r + 0.7152 * g + 0.0722 * b;
};
// Text d'alt contrast sobre un fons (negre o blanc). El llindar 0.179 és el punt
// on negre i blanc ofereixen el mateix ràtio de contrast.
const onColor = (bg: string): string => (luminance(bg) > 0.179 ? '#1e1e1e' : '#ffffff');

// Tokens estructurals [valor clar, valor fosc]. S'interpolen segons el nivell.
const TOKENS = {
    pageBg:           ['#ffffff', '#1c1c1c'],
    hatch:            ['#e1e1e1', '#2a2a2a'],
    bgDefault:        ['#ffffff', '#121212'],
    bgPaper:          ['#ffffff', '#1e1e1e'],
    surface:          ['#ffffff', '#2d2d2d'],
    border:           ['#e3e3e3', '#ffffff'],
    divider:          ['#e0e0e0', '#ffffff'],
    bodyText:         ['#666666', '#bbbbbb'],
    textSecondary:    ['#666666', '#bbbbbb'],
    textDisabled:     ['#555555', '#777777'],
    actionActive:     ['#757575', '#ffffff'],
    actionHover:      ['#f5f5f5', '#333333'],
    actionDisabled:   ['#aaaaaa', '#555555'],
    actionDisabledBg: ['#e7e5e5', '#2c2c2c'],
} as const;
type TokenKey = keyof typeof TOKENS;

// Enfosquiment màxim aplicat a un color personalitzat en foscor total (nivell
// 100%). 0.9 = s'enfosqueix fins al 90%, mantenint un 10% del color original
// (mai negre del tot). Si el color encara és el per defecte, s'usa el valor
// predefinit en lloc d'enfosquir.
const DARK_MODE_DARKEN = 0.9;
const DARK_DEFAULT_PRIMARY = '#90caf9';
const DARK_DEFAULT_SECONDARY = '#2e2e2e';
const darkVariant = (color: string, def: string, darkDefault: string) =>
    color.toLowerCase() === def ? darkDefault : darken(color, DARK_MODE_DARKEN);

// Corba d'enfosquiment dels colors principal/secundari (exponent > 1). Amb la
// interpolació lineal en sRGB els colors es veuen perceptualment massa foscos a
// nivells intermedis (efecte gamma). Amb aquesta corba els colors es mantenen
// propers als triats per l'usuari durant gran part del recorregut i només
// s'acosten al seu valor fosc a prop del 100%. Manté els extrems (0 i 1) intactes.
const COLOR_DARKEN_EASE = 2;

// Factoria de tema. level ∈ [0,1]: 0 = clar exacte, 1 = fosc exacte; valors
// intermedis interpolen els colors de base i recalculen el text per contrast.
export const buildTheme = (
    primary: string | undefined,
    secondary: string | undefined,
    level: number,
) => {
    const t = clamp01(level || 0);

    const tk = {} as Record<TokenKey, string>;
    (Object.keys(TOKENS) as TokenKey[]).forEach(k => { tk[k] = mix(TOKENS[k][0], TOKENS[k][1], t); });

    // palette.mode és binari a MUI: el fixem segons el punt mitjà del nivell.
    const mode = t < 0.5 ? ('light' as const) : ('dark' as const);

    const primaryColor = primary || DEFAULT_PRIMARY_COLOR;
    const secondaryColor = secondary || DEFAULT_SECONDARY_COLOR;
    // Els colors principal/secundari s'interpolen del valor triat (clar) al seu
    // valor en foscor total (predefinit o enfosquit), però amb una corba suavitzada
    // perquè a nivells intermedis no es vegin excessivament foscos. Els fons i la
    // resta de tokens segueixen el nivell lineal `t`.
    const colorT = Math.pow(t, COLOR_DARKEN_EASE);
    const primaryMain = mix(primaryColor, darkVariant(primaryColor, DEFAULT_PRIMARY_COLOR, DARK_DEFAULT_PRIMARY), colorT);
    const secondaryMain = mix(secondaryColor, darkVariant(secondaryColor, DEFAULT_SECONDARY_COLOR, DARK_DEFAULT_SECONDARY), colorT);

    const textPrimary = onColor(tk.bgPaper);
    const selected = alpha(primaryMain, 0.28);
    const drawerBg = darken(primaryMain, 0.35 + 0.2 * t);
    const dialogTitleBg = alpha(primaryMain, 0.5);
    // Text del títol de modal: contrast sobre el fons compost (superfície + primary 50%).
    const dialogTitleColor = onColor(mix(tk.surface, primaryMain, 0.5));

    // IMPORTANT: la palette ha d'anar al PRIMER argument de createTheme perquè
    // MUI n'augmenti els colors (calcula light/dark/contrastText a partir de
    // `main`). Els arguments següents només es fusionen, sense augmentar: si la
    // palette anés al segon argument, `primary.dark` (que fa servir el hover dels
    // botons contained) es quedaria amb el valor per defecte de MUI.
    return createTheme({
        ...base,
        palette: {
            mode,
            primary: {main: primaryMain},
            secondary: {main: secondaryMain},
            background: {default: tk.bgDefault, paper: tk.bgPaper},
            text: {primary: textPrimary, secondary: tk.textSecondary, disabled: `${tk.textDisabled} !important`},
            divider: tk.divider,
            warning: {main: mix('#8a6d3b', '#ffa726', t)},
            ...(mode === 'dark'
                ? {error: {main: '#f44336'}, info: {main: '#29b6f6'}, success: {main: '#66bb6a'}}
                : {}),
            action: {
                active: tk.actionActive,
                hover: tk.actionHover,
                selected,
                disabled: tk.actionDisabled,
                disabledBackground: tk.actionDisabledBg,
            },
        },
    }, {
        components: {
            MuiCssBaseline: {
                styleOverrides: {
                    body: {
                        backgroundColor: tk.pageBg,
                        backgroundImage: hatchPattern(tk.hatch),
                        color: tk.bodyText,
                    },
                    '.input': {
                        '& .MuiInputBase-root, & .MuiPickersInputBase-root': {
                            color: textPrimary,
                            backgroundColor: tk.bgPaper,
                        },
                        '& .MuiInputBase-root.Mui-disabled': {
                            backgroundColor: tk.actionDisabledBg,
                        },
                    },
                    '.styledFilter': {
                        backgroundColor: secondaryMain,
                        border: `1px solid ${tk.border}`,
                    },
                    '.myComment': {color: 'black', backgroundColor: '#a5d6a7'},
                    '.otherComment': {color: 'black', backgroundColor: '#e0e0e0'},
                },
            },
            MuiOutlinedInput: {
                styleOverrides: {
                    input: {'&:-webkit-autofill': {WebkitBoxShadow: `0 0 0 100px ${tk.bgPaper} inset`}},
                },
            },
            MuiFormLabel: {styleOverrides: {root: {color: textPrimary}, filled: {color: textPrimary}}},
            MuiInputLabel: {styleOverrides: {root: {color: textPrimary}}},
            MuiButtonGroup: {
                styleOverrides: {grouped: {'&.Mui-disabled': {backgroundColor: tk.actionDisabledBg, color: tk.textDisabled}}},
            },
            MuiDialogContent: {styleOverrides: {root: {backgroundColor: tk.surface}}},
            MuiDialogTitle: {
                styleOverrides: {root: {backgroundColor: dialogTitleBg, color: dialogTitleColor, borderBottom: `1px solid ${tk.border}`}},
            },
            MuiCard: {styleOverrides: {root: {border: `1px solid ${tk.border}`}}},
            MuiCardHeader: {
                styleOverrides: {
                    root: {
                        backgroundColor: secondaryMain,
                        color: onColor(secondaryMain),
                        borderBottom: `1px solid ${tk.border}`,
                        '&.detail': {backgroundColor: alpha(secondaryMain, 0.5)},
                    },
                },
            },
            MuiCardContent: {styleOverrides: {root: {backgroundColor: tk.surface}}},
            MuiCheckbox: {
                styleOverrides: {root: {color: textPrimary, '&.Mui-disabled': {color: tk.textDisabled}}},
            },
            MuiDrawer: {styleOverrides: {paper: {backgroundColor: drawerBg, color: onColor(drawerBg)}}},
            MuiListItemButton: {
                styleOverrides: {
                    root: {
                        '.MuiDrawer-paper &:hover': {
                            backgroundColor:
                                onColor(drawerBg) === '#ffffff'
                                    ? lighten(drawerBg, 0.15)
                                    : darken(drawerBg, 0.15),
                        },
                        '.MuiDrawer-paper &.Mui-selected': {
                            backgroundColor: alpha(primaryMain, 0.30),
                        },
                        '.MuiDrawer-paper &.Mui-selected:hover': {
                            backgroundColor: alpha(primaryMain, 0.45),
                        },
                    },
                },
            },
            MuiListItemIcon: {
                styleOverrides: {
                    root: {
                        '.MuiDrawer-paper &': {
                            color: onColor(drawerBg),
                        },
                    },
                },
            },
            MuiBadge: {
                styleOverrides: {
                    badge: {
                        '.MuiDrawer-paper &': {
                            // border: `2px solid ${onColor(drawerBg)}`,
                            // right: -24,
                            // top: 10,
                            overflowWrap: 'normal',
                            wordBreak: 'keep-all',
                            whiteSpace: 'nowrap',
                            padding: '0px 4px',
                        },
                    },
                },
            },
            MuiAlert: {
                styleOverrides:
                    mode === 'dark'
                        ? {
                            standardSuccess: {backgroundColor: '#4c5d3e'},
                            standardInfo: {backgroundColor: '#2e4255'},
                            standardWarning: {backgroundColor: '#554a3b'},
                            standardError: {backgroundColor: '#4f3333'},
                        }
                        : {
                            standardWarning: {color: '#8a6d3b', backgroundColor: '#fcf8e3', borderColor: '#faebcc'},
                        },
            },
            MuiDataGrid: {
                styleOverrides: {
                    root: {
                        // Files parells: color secundari amb alfa 0.5 (zebra striping).
                        // Exclou les seleccionades perquè conservin el ressaltat de selecció.
                        '& .MuiDataGrid-row.even:not(.Mui-selected)': {
                            backgroundColor: alpha(secondaryMain, 0.5),
                        },
                        '& .MuiDataGrid-row:hover': {
                            backgroundColor: `${alpha(primaryMain, 0.4)} !important`,
                        },
                    },
                    row: {
                        '&.Mui-selected': {
                            backgroundColor: `${selected} !important`,
                        },
                        '&.Mui-selected:hover': {
                            backgroundColor: `${alpha(primaryMain, 0.4)} !important`,
                        },
                    },
                },
            },
        },
    });
};

export const lightTheme = createTheme(buildTheme('#004B99', '#2E2E2E', 0), {
    palette: {
        mode: 'light',
        primary: {
            main: '#004B99',
            contrastText: '#fff',
        },
        secondary: {
            main: '#2E2E2E',
        },
    },
});

export const darkTheme = createTheme(buildTheme(lighten('#004B99', 0.2), '#F6F6F6', 100), {
    palette: {
        mode: 'dark',
        primary: {
            main: lighten('#004B99', 0.2),
            contrastText: '#fff',
        },
        secondary: {
            main: '#F6F6F6',
        },
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: '0px',
                    fontSize: '14px',
                    fontWeight: 400,
                    textTransform: 'none',
                    '&:not(.MuiButtonGroup-grouped)': {marginLeft: '10px'},
                    '& .MuiButton-startIcon': {marginRight: '0'},
                    '&.Mui-disabled': {
                        opacity: 0.6,
                        cursor: 'not-allowed'
                    },
                    variants: [
                        {
                            props: ({ color, variant }) =>
                                variant === 'outlined' && color === 'primary',
                            style: props => ({
                                color: props.theme.palette.secondary.main,
                                borderColor: props.theme.palette.secondary.main,
                            }),
                        },
                    ],
                },
            },
        },
    },
});

export const draculaTheme = createTheme(buildTheme('#BD93F9', '#F8F8F2', 50), {
    palette: {
        mode: 'dark',
        primary: {
            main: '#BD93F9',
            contrastText: '#282A36',
        },
        secondary: {
            main: '#F8F8F2',
        },
        background: {
            default: '#282A36',
            paper: '#303341',
        },
        text: {
            primary: '#F8F8F2',
            secondary: '#D6D6C2',
        },
        error: {
            main: '#FF5555',
        },
        warning: {
            main: '#FFB86C',
        },
        success: {
            main: '#50FA7B',
        },
        info: {
            main: '#8BE9FD',
        },
        divider: '#44475A',
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    variants: [
                        {
                            props: ({ color, variant }:any) =>
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
    },
});
