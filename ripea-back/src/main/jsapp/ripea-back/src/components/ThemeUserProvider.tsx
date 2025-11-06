import React, { useMemo } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import {useUserSession} from "./Session.tsx";
import {darkTheme, lightTheme} from "../theme.ts";

export const ThemeUserProvider = ({ children }: { children: React.ReactNode }) => {
    const { value: user } = useUserSession()

    const theme = useMemo(() => {
        // console.log(">>> user", user)
        return (user?.conf?.modeFosc) ? darkTheme : lightTheme
    }, [user?.conf?.modeFosc]);

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            {children}
        </ThemeProvider>
    );
}
