import {ThemeOptions, createTheme} from '@mui/material/styles';
import merge from 'lodash.merge';
import backgroundPattern from './assets/background-pattern.png';

const base: ThemeOptions = {
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
		MuiDrawer: {
			styleOverrides: {
			    paper: {
					right: 0,
					left: 'auto',					
					backgroundColor: '#004B99',
					color: '#fff'
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
                    fontSize: '1.8rem',     // Mida de la font
                    lineHeight: 1.2,         // Alçada de línia
                    fontWeight: 400,
                },
                h4: {
                    fontSize: '1.5rem',     // Mida de la font
                    lineHeight: 1.2,         // Alçada de línia
                    fontWeight: 400,
                }
            },
        },
        MuiCardHeader : {
            styleOverrides: {
                root: {
                    padding: '5px',
                },
            },
        },
        MuiFormControl: {
            styleOverrides: {
                root: {
                    // Si algún hijo input/select está readonly o disabled
                    '&:has(.MuiInputBase-input[readonly][aria-hidden="false"]), &:has(.MuiInputBase-input.Mui-disabled), &:has(.MuiSelect-select[readonly]), &:has(.MuiSelect-select.Mui-disabled)': {
                        backgroundColor: 'rgba(231,229,229,0.6)',
                        // opacity: 0.6,
                    }
                }
            }
        },
        MuiInputBase: {
            styleOverrides: {
                root: {
                    fontSize: '14px',
                },
                input: {
                    '&.Mui-disabled': {
                        '-webkit-text-fill-color': 'black',
                    }
                }
            }
        },
        MuiFormLabel: {
            styleOverrides: {
                root: {
                    fontStyle: 'italic',
                    paddingRight: '2px',
                    fontSize: '14px',
                    fontWeight: 200,
                    color: 'black',
                    '&.Mui-disabled': {
                        fontStyle: 'italic',
                        paddingRight: '2px',
                        color: 'black',
                    },
                    '&.Mui-focused': {
                        fontStyle: 'italic',
                        paddingRight: '2px',
                        backgroundColor: 'white',
                    }
                },
                filled: {
                    fontStyle: 'italic',
                    paddingRight: '2px',
                    fontSize: '14px',
                    fontWeight: 200,
                    color: 'black',
                    // backgroundColor: 'white',
                    '&.Mui-disabled': {
                        fontStyle: 'italic',
                        paddingRight: '2px',
                        color: 'black',
                        opacity: 1,
                        // backgroundColor: 'white',
                    },
                    '&.Mui-focused': {
                        fontStyle: 'italic',
                        paddingRight: '2px',
                        // backgroundColor: 'white',
                    }
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
        MuiTab: {
            styleOverrides: {
                root: {
                    textTransform: 'none',
                    fontSize: '1 rem',
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
					backgroundColor: "#f5f5f5",
					borderBottom: "1px solid #e3e3e3",
					padding: "5px 24px",
					display: "flex",
					justifyContent: "space-between",
					alignItems: "center",
                }
            }
        },
		MuiAlert: {
            styleOverrides: {
                standardWarning: {
                    color: "#8a6d3b",
                    backgroundColor: "#fcf8e3",
                    borderColor: "#faebcc",
                }
            }
        },
    }
};

export const lightTheme = createTheme( merge( base, {
    palette: {
        mode: 'light',
    },
}));

export const darkTheme = createTheme({
  palette: {
    mode: 'dark',
    background: {
      default: '#121212', // fondo general
      paper: '#1e1e1e',   // fondo de tarjetas, diálogos, etc.
    },
    primary: {
      main: '#90caf9',    // azul claro para botones y enlaces
      contrastText: '#000000',
    },
    secondary: {
      main: '#f48fb1',    // rosa suave como color secundario
      contrastText: '#000000',
    },
    error: {
      main: '#f44336',
    },
    warning: {
      main: '#ffa726',
    },
    info: {
      main: '#29b6f6',
    },
    success: {
      main: '#66bb6a',
    },
    text: {
      primary: '#ffffff',
      secondary: '#bbbbbb',
      disabled: '#777777',
    },
    divider: '#333333',
    action: {
      active: '#ffffff',
      hover: '#333333',
      selected: '#444444',
      disabled: '#555555',
      disabledBackground: '#2c2c2c',
    },
  },
  components: {
	MuiCssBaseline: {
	      styleOverrides: {
	        'div[class*="MuiBox-root"]': {
	          backgroundColor: '#2d2d2d',
	          color: 'white',
	        },
			// Excluye los Box dentro de header
		    'header div[class*="MuiBox-root"]': {
		      backgroundColor: 'inherit',
		      color: 'inherit',
		    },
			// Excluye los Box dentro de footer
			'footer div[class*="MuiBox-root"]': {
			  backgroundColor: 'inherit',
			  color: 'inherit',
			},
			// Excluir los Box dentro de celdas de DataGrid
			'.MuiDataGrid-cell div[class*="MuiBox-root"]': {
			  backgroundColor: 'inherit',
			  color: 'inherit',
			},
			// Excluir los Box dentro de avisos
			'.MuiCollapse-wrapperInner div[class*="MuiBox-root"]': {
			  backgroundColor: 'inherit',
			  color: 'inherit',
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
	MuiDataGrid: {
	    styleOverrides: {
	        row: {
                color: 'white',
				borderBottom: '1px solid grey',
				background: '#212830 !important',
				'&:hover': {
				        backgroundColor: '#222222 !important', // color al pasar el ratón
				      },
	        },
	    }
	},
	MuiCardContent: {
	    styleOverrides: {
	        root: {
	            padding: '10px 16px', // Aquí pots ajustar el padding que vulguis
	            '&:last-child': {
	                paddingBottom: '10px', // Per modificar el padding inferior de l'últim element
	            },
				backgroundColor: '#2d2d2d !important', // fondo deshabilitado
	        },
	    },
	},
	MuiButton: {
	  styleOverrides: {
	    root: ({ ownerState, theme }) => {
	      const baseStyles = {
	        borderRadius: '0px',
	        fontSize: '14px',
	        fontWeight: 400,
	        textTransform: 'none',
	        '&:not(.MuiButtonGroup-grouped)': {
	          marginLeft: '10px',
	        },
	        '& .MuiButton-startIcon': {
	          marginRight: '0',
	        },
	      };

	      const disabledStyles = ownerState.disabled
	        ? {
	            color: 'red',
	            borderColor: theme.palette.grey[700],
	            backgroundColor: theme.palette.action.disabledBackground,
	            cursor: 'not-allowed',
	            opacity: 0.6,
	            '& .MuiChip-root': {
	              backgroundColor: theme.palette.grey[800],
	              color: '#90caf9',
	            },
	            '& .MuiIcon-root': {
	              color: '#90caf9',
	            },
	            '&.Mui-disabled': {
	              color: '#777777',
	              backgroundColor: '#2c2c2c',
	              borderColor: '#444444',
	              opacity: 0.6,
	              cursor: 'not-allowed',
	            },
	          }
	        : {};

	      return {
	        ...baseStyles,
	        ...disabledStyles,
	      };
	    },
	  },
	},
	MuiInputBase: {
		styleOverrides: {
			root: {
				fontSize: '14px',
				color: 'white !important',
				backgroundColor: '#1e1e1e !important',
			},
		},
	},
	MuiCheckbox	: {
		styleOverrides: {
			root: {
				color: 'white !important',
				backgroundColor: '#1e1e1e !important',
			},
		},
	},
	MuiTypography: {
	    styleOverrides: {
	        h5: {
	            fontSize: '1.8rem',     // Mida de la font
	            lineHeight: 1.2,         // Alçada de línia
	            fontWeight: 400,
	        },
	        h4: {
	            fontSize: '1.5rem',     // Mida de la font
	            lineHeight: 1.2,         // Alçada de línia
	            fontWeight: 400,
	        },
			body1: {
				color: 'white !important',
			},
			root: {
				color: 'darkgrey !important',
			}
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
			},			
	        grouped: {
				'&.Mui-disabled': {
					color: 'grey !important'
				}
	        }
	    }
	},
	MuiDrawer: {
		styleOverrides: {
		    paper: {
				right: 0,
				left: 'auto',
				backgroundColor: '#2d2d2d',
				color: '#fff'
		    },
		},
	},	
	MuiIcon: {
	    styleOverrides: {
	        root: {
	            fontSize: '18px', // Mida base
	            marginRight: '4px',
	        },
	    }
	},
	MuiTab: {
	    styleOverrides: {
	        root: {
	            textTransform: 'none',
	            fontSize: '1 rem',
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
	MuiGrid: {
	    styleOverrides: {
			'div[class*="MuiGrid-root"]': {
				color: 'white !important',
				backgroundColor: 'red !important',
			},
			// Excluye los Box dentro de header
			'header div[class*="MuiGrid-root"]': {
			  backgroundColor: 'inherit',
			  color: 'inherit',
			},	
	    },
	},
	MuiAlert: {
	    styleOverrides: {
	         standardSuccess: {
	             backgroundColor: '#4c5d3e',
	         },
	        standardInfo: {
	            backgroundColor: '#2e4255',
	        },
			standardWarning: {
				backgroundColor: '#554a3b',			
			},
	         standardError: {
	             backgroundColor: '#4f3333',
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
    MuiCardHeader: {
      styleOverrides: {
        root: {
			padding: '5px',
			backgroundColor: '#2a2a2a !important',
			color: 'lightgray !important'
        },
        title: {
          color: '#ffffff',
        },
        subheader: {
          color: '#aaaaaa',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        colorPrimary: {
          backgroundColor: '#1f1f1f',
        },
      },
    },
  },
});