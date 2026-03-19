import {ThemeOptions, createTheme, darken} from '@mui/material/styles';
import backgroundPattern from './assets/background-pattern.png';
import type {} from '@mui/x-data-grid/themeAugmentation';

const base: ThemeOptions = {
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                body: {
                    backgroundImage: `url(${backgroundPattern})`,
                    color: '#666666'
                },
                '.multi-line-cell': {
                    display: 'flex',
                    alignItems: 'baseline !important',
                    whiteSpace: 'break-spaces !important',
                },
                '.styledFilter': {
                    marginBottom: '16px',
                    padding: '16px',
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
                    '& .MuiDataGrid-treeDataGroupingCell > *': {
                        display: 'flex',
                        alignItems: 'center'
                    }
                },
				row: {
					minHeight: '40px !important',
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
        MuiTab: {styleOverrides: {root: {textTransform: 'none', fontSize: '1rem'}}},
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
        MuiButtonGroup: {
            styleOverrides: {
                root: {
                    '& .MuiButtonGroup-grouped:first-of-type': {
                        borderTopLeftRadius: '4px',
                        borderBottomLeftRadius: '4px',
                    },
                    '& .MuiButtonGroup-grouped:last-of-type': {
                        borderTopRightRadius: '4px',
                        borderBottomRightRadius: '4px',
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

const lightPalete = {
    mode: 'light',
    primary: {main: '#337ab7', contrastText: "#fff"},
    warning: {main: '#8a6d3b'},
    action: {
        disabled: '#555555',
        selected: 'rgba(51, 122, 183, 0.28)',
        disabledBackground: 'rgba(231,229,229,0.6)',
    },
}

export const lightTheme = createTheme(base, {
    palette: { ...lightPalete },
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                '.styledFilter': {
                    backgroundColor: '#f5f5f5',
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
        MuiOutlinedInput: {
            styleOverrides: {
                root: {
                    '&.Mui-disabled': {
                        backgroundColor: lightPalete.action.disabledBackground,
                    },
                    '& input[readonly][aria-hidden="false"]': {
                        backgroundColor: lightPalete.action.disabledBackground,
                    },
                },
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    backgroundColor: lightPalete.action.disabledBackground,
                }
            }
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    border: '1px solid #e3e3e3'
                }
            },
        },
        MuiCardHeader: {
            styleOverrides: {
                root: {
                    backgroundColor: lightPalete.action.disabledBackground,
                    borderBottom: '1px solid #e3e3e3',

                    '&.detail': {
                        backgroundColor: 'rgba(234,234,234,0.31)',
                    }
                }
            },
        },
        MuiDrawer: {styleOverrides: {paper: {backgroundColor: '#004B99', color: '#fff'}}},
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
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `rgba(144, 202, 249, 0.66) !important`,
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

const darkPalette = {
    mode: 'dark',
    background: {default: '#121212', paper: '#1e1e1e'},
    primary: {main: '#90caf9', contrastText: '#000000'},
    secondary: {main: '#f48fb1', contrastText: '#000000'},
    error: {main: '#f44336'},
    warning: {main: '#ffa726'},
    info: {main: '#29b6f6'},
    success: {main: '#66bb6a'},
    text: {primary: '#ffffff', secondary: '#bbbbbb', disabled: '#777777 !important'},
    divider: '#ffffff',
    action: {
        active: '#ffffff',
        hover: '#333333',
		selected: 'rgba(51, 122, 183, 0.28)',
        disabled: '#555555',
        disabledBackground: '#2c2c2c'
    },
}

export const darkTheme = createTheme(base, {
    palette: { ...darkPalette },
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                '.styledFilter': {
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
                    color: darkPalette.background.paper,
                    backgroundColor: darkPalette.action.active,
                },
            }
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
					'& .MuiDataGrid-row.even.MuiDataGrid-row': {
					    backgroundColor: '#464646',
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
        MuiCard: {
            styleOverrides: {
                root: {
                    border: '1px solid white'
                }
            },
        },
        MuiCardHeader: {
            styleOverrides: {
                root: {
                    borderBottom: '1px solid white',
                }
            },
        },
        MuiCardContent: {
            styleOverrides: {
                root: {
                    backgroundColor: '#2d2d2d'
                }
            }
        },
        MuiInputBase: {styleOverrides: {root: {backgroundColor: darkPalette.background.paper}}},
        MuiAutocomplete: {styleOverrides: {root: {backgroundColor: 'inherit !important'}}},
        MuiTextField: {styleOverrides: {root: {backgroundColor: 'inherit !important'}}},
        MuiInputLabel: {
            styleOverrides: {
                root: {
                    color: '#fff',
                }
            }
        },
        MuiCheckbox: {
            styleOverrides: {
                root: {
                    color: 'inherit !important',
                    backgroundColor: 'inherit !important',
                    '&.Mui-disabled': {color: darkPalette.text.disabled}
                },
            }
        },
        MuiButtonGroup: {
            styleOverrides: {
                grouped: {'&.Mui-disabled': {color: 'grey !important'}}
            }
        },
        MuiDrawer: {styleOverrides: {paper: {backgroundColor: '#2d2d2d'}}},
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
