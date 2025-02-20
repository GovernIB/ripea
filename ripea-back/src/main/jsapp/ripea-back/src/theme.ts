import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    palette: {
        primary: {
            main: '#004B99',
            contrastText: "#fff"
        },
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: '0px',
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
    }
});

export default theme;