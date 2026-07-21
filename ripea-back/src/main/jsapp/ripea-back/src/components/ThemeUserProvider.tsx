import React, {createContext, useContext, useEffect, useMemo, useState} from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { useMediaQuery } from '@mui/material';
import {useUserSession} from "./Session.tsx";
import {lightTheme, darkTheme, draculaTheme} from "../theme.ts";

export enum TemaAplicacio {
    CLAR = "CLAR",
    OBSCUR = "OBSCUR",
    DRACULA = "DRACULA",
    SISTEMA = "SISTEMA",
}

export enum EstilMenuProp {
    theme = "TEMA",
    inverse = "TEMA_INVERTIT",
    footer = "PEU"
}

interface PreviewProps {
    temaAplicacio: TemaAplicacio | undefined;
    estilMenu: EstilMenuProp | undefined;
}
interface ThemeUserContextProps {
    preview: PreviewProps | undefined;
    setPreview: (value: PreviewProps) => void;
    remove: () => void;
}

const ThemeUserContext = createContext<ThemeUserContextProps | undefined>(undefined);

export const ThemeUserProvider = ({ children }: { children: React.ReactNode }) => {
    const { value: user } = useUserSession()
    const [preview, setPreview] = useState<PreviewProps | undefined>()
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');

    useEffect(() => {
        remove()
    }, [user?.conf?.temaAplicacio, user?.conf?.estilMenu]);

    const remove = () => {
        setPreview({
            temaAplicacio: user?.conf?.temaAplicacio,
            estilMenu: user?.conf?.estilMenu,
        })
    }

    const theme = useMemo(() => {
        const systemTheme = prefersDarkMode ? darkTheme : lightTheme;
        const temaAplicacio = preview?.temaAplicacio || user?.conf?.temaAplicacio;

        switch (temaAplicacio) {
            case TemaAplicacio.CLAR:
                return lightTheme;
            case TemaAplicacio.OBSCUR:
                return darkTheme;
            case TemaAplicacio.DRACULA:
                return draculaTheme;
            case TemaAplicacio.SISTEMA:
            default:
                return systemTheme;
        }
    }, [preview?.temaAplicacio, user]);

    return (
        <ThemeUserContext.Provider value={{ preview, setPreview, remove }}>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                {children}
            </ThemeProvider>
        </ThemeUserContext.Provider>
    );
}

export const useThemeUserContext = () => {
    const ctx = useContext(ThemeUserContext);
    if (!ctx) throw new Error('useThemeUserContext ha d\'usar-se dins de <ThemeUserProvider>');
    return ctx;
};
