import React, {createContext, useContext, useEffect, useMemo, useState} from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import {useUserSession} from "./Session.tsx";
import {darkTheme, lightTheme} from "../theme.ts";

export enum TemaAplicacio {
    CLAR = "CLAR",
    OBSCUR = "OBSCUR",
}


interface ThemeUserContextProps {
    value: TemaAplicacio|undefined;
    setValue: (value: TemaAplicacio) => void;
    removeValue: () => void;
}

const ThemeUserContext = createContext<ThemeUserContextProps | undefined>(undefined);

export const ThemeUserProvider = ({ children }: { children: React.ReactNode }) => {
    const { value: user } = useUserSession()
    const [th, setTh] = useState<TemaAplicacio|undefined>()

    useEffect(() => {
        setTh(undefined)
    }, [user?.conf?.modeFosc]);

    const theme = useMemo(() => {
        switch (th) {
            case TemaAplicacio.CLAR:
                return lightTheme;
            case TemaAplicacio.OBSCUR:
                return darkTheme;
            default:
                return (user?.conf?.modeFosc) ? darkTheme : lightTheme;
        }
    }, [th]);

    return (
        <ThemeUserContext.Provider value={{
            value: th,
            setValue: setTh,
            removeValue: () => setTh(undefined)
        }}>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                {children}
            </ThemeProvider>
        </ThemeUserContext.Provider>
    );
}

export const useThemeUserContext = () => {
    const ctx = useContext(ThemeUserContext);
    if (!ctx) throw new Error('useSessionStorage debe usarse dentro de <ThemeUserProvider>');
    return ctx;
};