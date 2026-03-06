import {ThemeOptions, createTheme} from '@mui/material/styles';
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
        MuiTab: {styleOverrides: {root: {textTransform: 'none', fontSize: '1rem'}}},
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
    }
};

export const lightTheme = createTheme(base, {
    palette: {
        mode: 'light',
        primary: {main: '#337ab7', contrastText: "#fff"},
        warning: {main: '#8a6d3b'},
        action: {
            disabled: '#555555',
            disabledBackground: 'rgba(231,229,229,0.6)',
        },
    },
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                '.cardHeader': {
                    backgroundColor: 'rgba(231,229,229,0.6) !important',
                    borderBottom: '1px solid #e3e3e3',
                },
            },
        },
        MuiOutlinedInput: {
            styleOverrides: {
                root: ({ theme }:any) => ({
                    '&.Mui-disabled': {
                        backgroundColor: theme.palette.action.disabledBackground,
                    },
                    '& input[readonly][aria-hidden="false"]': {
                        backgroundColor: theme.palette.action.disabledBackground,
                    },
                }),
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    backgroundColor: "rgba(231,229,229,0.6)",
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
                    backgroundColor: 'rgba(231,229,229,0.6)',
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
                    color: "#8a6d3b",
                    backgroundColor: "#fcf8e3",
                    borderColor: "#faebcc"
                }
            }
        },
    }
});

export const darkTheme = createTheme(base, {
    palette: {
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
            selected: '#444444',
            disabled: '#555555',
            disabledBackground: '#2c2c2c'
        },
    },
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                'div[class*="MuiBox-root"]': {backgroundColor: 'inherit'},
                '.cardHeader': {
                    backgroundColor: 'inherit !important',
                    borderBottom: '1px solid white',
                },
            },
        },
        MuiOutlinedInput: {styleOverrides: {input: {'&:-webkit-autofill': {WebkitBoxShadow: '0 0 0 100px #1e1e1e inset'}}}},
        MuiDialogContent: {styleOverrides: {root: {backgroundColor: "#2d2d2d"}}},
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    color: '#1e1e1e',
                    backgroundColor: '#ffffff'
                }
            }
        },
        MuiDataGrid: {
            styleOverrides: {
                row: {
                    background: '#212830 !important',
                    '&:hover': {backgroundColor: '#222222 !important'},
                }
            }
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
                    backgroundColor: 'inherit',
                    color: 'inherit',
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
        MuiButton: {
            styleOverrides: {
                root: {
                    '&.Mui-disabled': {
                        '& .MuiChip-root': {color: '#90caf9'},
                        '& .MuiIcon-root': {color: '#90caf9'},
                        backgroundColor: '#2c2c2c',
                        borderColor: '#444444',
                        opacity: 0.6,
                        cursor: 'not-allowed'
                    },
                },
            },
        },
        MuiInputBase: {styleOverrides: {root: {backgroundColor: '#1e1e1e'}}},
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
                    '&.Mui-disabled': {color: '#7777 !important'}
                }
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
