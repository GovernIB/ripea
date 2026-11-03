import React, {useEffect} from 'react';
import {BaseApp} from './components/BaseApp';
import ripea_logo from './assets/Drassana_RIP_DRA_COL.svg';
import favicon from './assets/favicon.png'
import governLogo from './assets/govern-logo.png'
import AppRoutes from './AppRoutes';
import {useEntitatSession} from "./components/Session.tsx";
import TitleHeaderConfigurator from "./TitleHeaderConfigurator.tsx";
import { useTranslation } from 'react-i18next';
import { useCombinedMenu } from './hooks/useCombinedMenu.ts';
import { useTheme } from '@mui/material';
import { TemporalMessageBridge } from './components/PreBaseAppProvider.tsx';
import {useThemeUserContext} from "@src/components/ThemeUserProvider.tsx";
import {
    APPBAR_PADDING_LEFT,
    HEADER_HEIGHT,
    LOGO_BOX_LEFT,
    LOGO_HEIGHT,
    LOGO_SEPARATOR_GAP,
    MENU_WIDTH,
} from './util/layout';

const changeFavicon = (faviconUrl:any) => {
    const link:any =
        document.querySelector("link[rel~='icon']") ||
        document.createElement('link');

    link.rel = 'icon';
    link.href = faviconUrl;

    document.getElementsByTagName('head')[0].appendChild(link);
};

// Les icones de la capçalera són de 24px ('--toolbar-icon-size', a appbarStyle) i no de 20 com a la
// llibreria: el botó de menú fa 52px en lloc de 48 i la caixa del logo arrenca 4px més a la dreta.
const MENU_BUTTON_EXTRA = 4;

export const getImgFromBytes = (img:string) :string | undefined => {
    return img ?`data:image/png;base64,${img}` :undefined;
}

export const App: React.FC = () => {
    const version = '1.0.1';
    const { value: entitat } = useEntitatSession();
    const { i18n } = useTranslation();
    const { sideMenuEntries: menuEntries, additionalContents: additionalContentsMenu } = useCombinedMenu();
    const theme = useTheme();
    const {preview} = useThemeUserContext()

    // Sincronizar lang del <html> con el idioma activo de i18next
    useEffect(() => {
        document.documentElement.lang = i18n.language;
    }, [i18n.language]);  // ← se ejecuta en el arranque y cada vez que cambie

    useEffect(() => {
        changeFavicon(getImgFromBytes(entitat?.conf?.favicon) || favicon)
    }, [entitat?.conf?.favicon, favicon]);

    return (
        <BaseApp
            code="cmd"
            logo={getImgFromBytes(entitat?.conf?.logo) || governLogo}
            logoStyle={{
                // El logo de l'entitat pot ser de qualsevol proporció: no ha de sortir de la caixa.
                '& img': { height: LOGO_HEIGHT + 'px', maxWidth: '100%', objectFit: 'contain', display: 'block' },
                // El separador vertical ha de caure sobre la vora dreta del menú obert, i omplir
                // l'alçada de la capçalera. El botó de menú duu un marge esquerre de -12 i un dret de
                // 16, així que la caixa del logo arrenca a LOGO_BOX_LEFT. Fixant-ne l'amplada (en
                // comptes de deixar que la mida del logo mani) la vora cau sempre a MENU_WIDTH.
                width: MENU_WIDTH - LOGO_BOX_LEFT - MENU_BUTTON_EXTRA + 'px',
                boxSizing: 'border-box',
                alignSelf: 'stretch',
                borderRight: `1px solid ${entitat?.conf?.colorLletra ?? theme.palette.divider}`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-end',
                // Substitueix el mr/pt/pr de la llibreria (l'sx s'hi fusiona al final).
                mr: 0,
                pt: 0,
                pl: 0,
                pr: LOGO_SEPARATOR_GAP + 'px',
            }}
            title={<img
                src={ripea_logo}
                alt={"logo_aplicacion"}
                style={{ marginLeft: LOGO_SEPARATOR_GAP + 'px', height: LOGO_HEIGHT + 'px', verticalAlign: 'middle' }}
            />}
            // El botó de menú duu ml -12, així que amb APPBAR_PADDING_LEFT queda centrat a la mateixa
            // columna que les icones del menú lateral. L'alçada mínima substitueix els 64px que la
            // llibreria dona a la barra.
            appbarStyle={{
                color: entitat?.conf?.colorFons ? theme.palette.getContrastText(entitat?.conf?.colorFons) : undefined,
                paddingLeft: APPBAR_PADDING_LEFT + 'px',
                minHeight: HEADER_HEIGHT + 'px',
                '--toolbar-icon-size': '1.5rem'} as React.CSSProperties
            }
            version={version}
            appbarBackgroundColor={entitat?.conf?.colorFons}
            menuEntries={menuEntries}
            menuAppearance={preview?.estilMenu}
        >
            <TitleHeaderConfigurator/>
            <AppRoutes/>
            <TemporalMessageBridge />
            {additionalContentsMenu}
        </BaseApp>
    );
}

export default App;
