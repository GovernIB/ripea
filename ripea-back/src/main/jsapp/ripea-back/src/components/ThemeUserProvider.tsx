import React, {createContext, useContext, useEffect, useMemo, useState} from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import {useUserSession} from "./Session.tsx";
import {buildTheme, DEFAULT_PRIMARY_COLOR, DEFAULT_SECONDARY_COLOR} from "../theme.ts";

// Previsualització transitòria del tema mentre l'usuari edita el seu perfil.
// Es fusiona amb la configuració persistida (user.conf); si l'usuari cancel·la
// es descarta i es torna al valor desat.
export interface ThemePreview {
    primary?: string;
    secondary?: string;
    // Nivell de foscor del tema, 0 (clar) a 100 (fosc).
    foscor?: number;
}

// Nivell per defecte quan l'usuari encara no n'ha desat cap.
export const DEFAULT_FOSCOR = 0;

interface ThemeUserContextProps {
    preview: ThemePreview | undefined;
    setPreview: (value: ThemePreview) => void;
    removePreview: () => void;
}

const ThemeUserContext = createContext<ThemeUserContextProps | undefined>(undefined);

export const ThemeUserProvider = ({ children }: { children: React.ReactNode }) => {
    const { value: user } = useUserSession()
    const [preview, setPreviewState] = useState<ThemePreview | undefined>()

    // En carregar/refrescar la configuració persistida descartem la previsualització.
    useEffect(() => {
        setPreviewState(undefined)
    }, [user?.conf?.nivellFosc, user?.conf?.colorPrincipal, user?.conf?.colorSecundari]);

    const setPreview = (value: ThemePreview) =>
        setPreviewState(prev => ({ ...prev, ...value }))
    const removePreview = () => setPreviewState(undefined)

    const theme = useMemo(() => {
        const primary = preview?.primary ?? user?.conf?.colorPrincipal ?? DEFAULT_PRIMARY_COLOR;
        const secondary = preview?.secondary ?? user?.conf?.colorSecundari ?? DEFAULT_SECONDARY_COLOR;
        const foscor = preview?.foscor ?? user?.conf?.nivellFosc ?? DEFAULT_FOSCOR;
        return buildTheme(primary, secondary, foscor / 100);
    }, [preview, user]);

    return (
        <ThemeUserContext.Provider value={{ preview, setPreview, removePreview }}>
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
