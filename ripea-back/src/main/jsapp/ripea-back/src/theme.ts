import {ThemeOptions, createTheme, darken, alpha} from '@mui/material/styles';
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
                    right: 0,
                    left: 'auto',
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
                    color: 'black',
                    '&.Mui-disabled': {fontStyle: 'italic', paddingRight: '2px', color: 'black'},
                    '&.Mui-focused': {fontStyle: 'italic', paddingRight: '2px'},
                },
                filled: {
                    fontStyle: 'italic',
                    paddingRight: '2px',
                    fontSize: '14px',
                    fontWeight: 200,
                    color: 'black',
                    '&.Mui-disabled': {fontStyle: 'italic', paddingRight: '2px', color: 'black', opacity: 1},
                    '&.Mui-focused': {fontStyle: 'italic', paddingRight: '2px'},
                },
            },
        },
        MuiIcon: {styleOverrides: {root: {fontSize: '18px', marginRight: '4px'}}},
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
                    borderBottom: "1px solid #e3e3e3",
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
                    backgroundColor: 'inherit',
                    color: 'inherit',
                    paddingTop: '8px',
                    paddingBottom: '8px',
                    paddingLeft: '16px',
                    paddingRight: '16px',
                }
            },
        },
    }
};

const lightPalette = (main: string, secondary: string) => ({
    mode: 'light' as const,
    // Deixem que MUI derivi light/dark/contrastText a partir de `main`.
    primary: {main},
    secondary: {main: secondary},
    text: { disabled: '#555555 !important' },
    warning: {main: '#8a6d3b'},
    action: {
        disabled: 'rgba(81,81,81,0.49)',
        // El color de selecció segueix el color principal (28% d'opacitat).
        selected: alpha(main, 0.28),
        disabledBackground: 'rgba(231,229,229,0.6)',
    },
})

const createLightTheme = (main: string, secondary: string) => {
const lightPalete = lightPalette(main, secondary);
// Sidebar/Drawer i capçaleres de marca: deriven del color principal.
const drawerBg = darken(main, 0.35);
return createTheme(base, {
    palette: { ...lightPalete },
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                '.input': {
                    color: 'black',
                    backgroundColor: 'white',
                    '& .MuiInputBase-root.Mui-disabled': {
                        backgroundColor: lightPalete.action.disabledBackground,
                    }
                },
                '.styledFilter': {
                    backgroundColor: secondary,
                    border: '1px solid #e3e3e3'
                },
                '.myComment': {
                    backgroundColor: '#a5d6a7',
                },
                '.otherComment': {
                    backgroundColor: '#e0e0e0',
                },
            },
        },
        MuiButtonGroup: {styleOverrides: {grouped: {'&.Mui-disabled': { backgroundColor: lightPalete.action.disabledBackground }}}},
        MuiDialogTitle: {styleOverrides: {root: {backgroundColor: alpha(main, 0.5),}}},
        MuiCard: {styleOverrides: {root: {border: '1px solid #e3e3e3'}}},
        MuiCardHeader: {
            styleOverrides: {
                root: {
                    backgroundColor: secondary,
                    borderBottom: '1px solid #e3e3e3',

                    '&.detail': {
                        backgroundColor: 'rgba(234,234,234,0.31)',
                    }
                }
            },
        },
        MuiDrawer: {styleOverrides: {paper: {backgroundColor: drawerBg, color: '#fff'}}},
        MuiAlert: {
            styleOverrides: {
                standardWarning: {
                    color: lightPalete.warning.main,
                    backgroundColor: "#fcf8e3",
                    borderColor: "#faebcc"
                }
            }
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    // Files parells: color secundari amb alfa 0.5 (zebra striping).
                    // Exclou les seleccionades perquè conservin el ressaltat de selecció.
                    '& .MuiDataGrid-row.even:not(.Mui-selected)': {
                        backgroundColor: alpha(secondary, 0.5),
                    },
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `${alpha(main, 0.4)} !important`,
                    },
                },
                row: {
                    '&.Mui-selected': {
                        backgroundColor: `${lightPalete.action.selected}`,
                    },
                    '&.Mui-selected:hover': {
                        backgroundColor: `${darken(lightPalete.action.selected, 0.2)}`,
                    },
                },
            },
        },
    }
});
}

const darkPaletteObj = (main: string, secondary: string) => ({
    mode: 'dark' as const,
    background: {default: '#121212', paper: '#1e1e1e'},
    // En mode fosc el principal i el secundari són versions enfosquides dels
    // colors triats per l'usuari (vegeu createDarkTheme); en mode clar s'usen
    // tal qual.
    primary: {main},
    secondary: {main: secondary},
    error: {main: '#f44336'},
    warning: {main: '#ffa726'},
    info: {main: '#29b6f6'},
    success: {main: '#66bb6a'},
    text: {primary: '#ffffff', secondary: '#bbbbbb', disabled: '#777777 !important'},
    divider: '#ffffff',
    action: {
        active: '#ffffff',
        hover: '#333333',
        selected: alpha(main, 0.28),
        disabled: '#555555',
        disabledBackground: '#2c2c2c'
    },
})

// Quantitat d'enfosquiment aplicada als colors triats per l'usuari en mode
// fosc (0 = sense canvi, 1 = negre). En tornar a mode clar es fa servir el
// color original (cap transformació).
const DARK_MODE_DARKEN = 0.4;

// Si l'usuari NO ha modificat els colors per defecte, en mode fosc s'usen
// aquests valors específics en lloc d'enfosquir els per defecte.
const DARK_DEFAULT_PRIMARY = '#90caf9';
const DARK_DEFAULT_SECONDARY = '#2e2e2e';

const createDarkTheme = (mainRaw: string, secondaryRaw: string) => {
// Adaptem els colors al mode fosc. Si encara són els per defecte, s'usen els
// valors foscos predefinits; si l'usuari els ha personalitzat, s'enfosqueixen.
// La resta del builder (paleta, sidebar, títols, files...) ja parteix d'aquí.
const main = mainRaw.toLowerCase() === DEFAULT_PRIMARY_COLOR
    ? DARK_DEFAULT_PRIMARY
    : darken(mainRaw, DARK_MODE_DARKEN);
const secondary = secondaryRaw.toLowerCase() === DEFAULT_SECONDARY_COLOR
    ? DARK_DEFAULT_SECONDARY
    : darken(secondaryRaw, DARK_MODE_DARKEN);
const darkPalette = darkPaletteObj(main, secondary);
// Sidebar/Drawer en mode fosc: fons fosc tintat amb el color principal.
const drawerBg = darken(main, 0.55);
return createTheme(base, {
    palette: { ...darkPalette },
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                body: {
                    backgroundColor: '#1c1c1c',
                    backgroundImage: hatchPattern('#2a2a2a'),
                },
                '.input': {
                    '& .MuiInputBase-root, & .MuiPickersInputBase-root': {
                        color: 'inherit',
                        backgroundColor: darkPalette.background.paper,
                    }
                },
                '.styledFilter': {
                    backgroundColor: secondary,
                    border: '1px solid #e3e3e3'
                },
                '.myComment': {
                    color: 'black',
                    backgroundColor: '#a5d6a7',
                },
                '.otherComment': {
                    color: 'black',
                    backgroundColor: '#e0e0e0',
                },
            },
        },
        MuiOutlinedInput: {styleOverrides: {
            input: {
                '&:-webkit-autofill': {
                    WebkitBoxShadow: `0 0 0 100px ${darkPalette.background.paper} inset`
                }
            }
        }},
        MuiDialogContent: {styleOverrides: {root: {backgroundColor: "#2d2d2d"}}},
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    color: '#fff',
                    backgroundColor: alpha(main, 0.5),
                },
            }
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
					// Files parells: color secundari amb alfa 0.5 (zebra striping).
					'& .MuiDataGrid-row.even.MuiDataGrid-row': {
					    backgroundColor: alpha(secondary, 0.5),
					},
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `${darken(darkPalette.action.selected, 0.2)} !important`,
                    },
                },
                row: {
                    '&.MuiDataGrid-row.Mui-selected': {
						backgroundColor: `${darkPalette.action.selected} !important`,
                    },
                    '&.MuiDataGrid-row.Mui-selected:hover': {
						backgroundColor: `${darken(darkPalette.action.selected, 0.2)} !important`,                        
                    },
                },
            },
        },
        MuiCard: {styleOverrides: {root: {border: '1px solid white'}}},
        MuiCardHeader: {styleOverrides: {root: {backgroundColor: secondary, borderBottom: '1px solid white'}}},
        MuiCardContent: {styleOverrides: {root: {backgroundColor: '#2d2d2d'}}},
        MuiInputLabel: {styleOverrides: {root: {color: '#fff'}}},
        MuiCheckbox: {
            styleOverrides: {
                root: {
                    color: 'inherit !important',
                    // backgroundColor: 'inherit !important',
                    '&.Mui-disabled': {color: darkPalette.text.disabled}
                },
            }
        },
        MuiButtonGroup: {styleOverrides: {grouped: {'&.Mui-disabled': {color: darkPalette.text.disabled}}}},
        MuiDrawer: {styleOverrides: {paper: {backgroundColor: drawerBg}}},
        MuiAlert: {
            styleOverrides: {
                standardSuccess: {backgroundColor: '#4c5d3e'},
                standardInfo: {backgroundColor: '#2e4255'},
                standardWarning: {backgroundColor: '#554a3b'},
                standardError: {backgroundColor: '#4f3333'}
            }
        },
    },
});
}

// Factoria de tema: a partir dels colors principal i secundari escollits per
// l'usuari i el mode (clar/fosc) construeix el tema MUI complet, derivant
// contrast i colors de marca (sidebar, capçaleres...).
export const buildTheme = (
    primary: string | undefined,
    secondary: string | undefined,
    mode: 'light' | 'dark',
) => {
    const main = primary || DEFAULT_PRIMARY_COLOR;
    const sec = secondary || DEFAULT_SECONDARY_COLOR;
    return mode === 'dark' ? createDarkTheme(main, sec) : createLightTheme(main, sec);
};
