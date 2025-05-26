import { createTheme } from '@mui/material/styles';
import backgroundPattern from './assets/background-pattern.png';

const theme = createTheme({
    palette: {
        primary: {
            main: '#337ab7',
            contrastText: "#fff"
        },
        warning: {
          main: '#8a6d3b',
        },
    },
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                body: {
                    backgroundImage: `url(${backgroundPattern})`,
                    color: '#666666'
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
                    }
                }
            }
        },
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: '0px',
                    fontSize: '14px',
                    fontWeight: 400,
                    textTransform: 'none',
                    '&:not(.MuiButtonGroup-grouped)': {
                        marginLeft: '10px',
                    },
                    '& .MuiButton-startIcon': {
                        marginRight: '0',
                    }
                },
            },
        },
        MuiPaper: {
            styleOverrides: {
                root: {
                    borderRadius: '0px',
                },
            },
        },
        MuiCardContent: {
            styleOverrides: {
                root: {
                    padding: '10px 16px', // Aquí pots ajustar el padding que vulguis
                    '&:last-child': {
                        paddingBottom: '10px', // Per modificar el padding inferior de l'últim element
                    },
                },
            },
        },
        MuiTypography: {
            styleOverrides: {
                h5: {
                    fontSize: '1.25rem',     // Mida de la font
                    lineHeight: 1.2,         // Alçada de línia
                    fontWeight: 500,
                }
            },
        },
        MuiCardHeader : {
            styleOverrides: {
                root: {
                        padding: '10px',
                },
            },
        },
        MuiInputBase: {
            styleOverrides: {
                root: {
                    fontSize: '14px',
                },
                input: {
                    fontSize: '14px',
                }
            }
        },
        MuiFormLabel: {
            styleOverrides: {
                root: {
                    fontSize: '14px',
                    fontWeight: 200,
                    color: '#666666',
                    // Altres propietats opcionals:
                    // fontWeight: 500,
                    // lineHeight: 1.5,
                }
            }
        },
        MuiIcon: {
            styleOverrides: {
                root: {
                    fontSize: '18px', // Mida base
                    marginRight: '4px',
                },
            }
        },
        MuiChip: {
            styleOverrides: {
                root: {
                    '&.MuiChip-sizeSmall .MuiChip-label': {
                        fontSize: '14px'
                    },
                    '&.MuiChip-sizeMedium .MuiChip-label': {
                        fontSize: '16px'
                    }
                }
            }
        },
        MuiDataGrid: {
            styleOverrides: {
                row: {
                    '&.even': {
                        backgroundColor: '#f9f9f9 !important',
                        '&:hover': {
                            backgroundColor: '#f5f5f5 !important',
                        }
                    }
                }
            }
        },
		MuiDialogTitle: {
            styleOverrides: {
                root: {
					marginBottom: '10px',
					backgroundColor: "#f5f5f5",
					borderBottom: "1px solid #e3e3e3",
					padding: "5px 24px",
					display: "flex",
					justifyContent: "space-between",
					alignItems: "center",
                }
            }
        }
    }
});

export default theme;