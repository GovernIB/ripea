import React, {useEffect} from 'react';
import {BaseApp} from './components/BaseApp';
import ripea_logo from './assets/Drassana_RIP_DRA_COL.svg';
import governLogo from './assets/govern-logo.png'
import AppRoutes from './AppRoutes';
import {useEntitatSession} from "./components/Session.tsx";
import TitleHeaderConfigurator from "./TitleHeaderConfigurator.tsx";
import { useTranslation } from 'react-i18next';
import { useCombinedMenu } from './hooks/useCombinedMenu.ts';
import { useTheme } from '@mui/material';
import { TemporalMessageBridge } from './components/PreBaseAppProvider.tsx';
import {useThemeUserContext} from "@src/components/ThemeUserProvider.tsx";

type FaviconLink = { href: string, type?: string, sizes?: string };

// Els mateixos que declara index.html (public/): SVG i PNG per als navegadors sense suport d'SVG
const defaultFavicons: FaviconLink[] = [
    { href: `${import.meta.env.BASE_URL}favicon.svg`, type: 'image/svg+xml' },
    { href: `${import.meta.env.BASE_URL}favicon.png`, type: 'image/png', sizes: '32x32' },
];

// Substitueix tots els <link rel="icon">: si només se'n canviàs un, el navegador podria triar l'altre
const changeFavicon = (entitatFaviconUrl?: string) => {
    const head = document.getElementsByTagName('head')[0];
    head.querySelectorAll("link[rel~='icon']").forEach((link) => link.remove());

    const favicons: FaviconLink[] = entitatFaviconUrl ? [{ href: entitatFaviconUrl }] : defaultFavicons;
    favicons.forEach(({ href, type, sizes }) => {
        const link = document.createElement('link');
        link.rel = 'icon';
        if (type) link.type = type;
        if (sizes) link.setAttribute('sizes', sizes);
        link.href = href;
        head.appendChild(link);
    });
};

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
        changeFavicon(getImgFromBytes(entitat?.conf?.favicon))
    }, [entitat?.conf?.favicon]);

    return (
        <BaseApp
            code="cmd"
            logo={getImgFromBytes(entitat?.conf?.logo) || governLogo}
            logoStyle={{
                '& img': { height: '49px' },
                // pl: 2,
                pr: 4,
                mr: 4,
                borderRight: `1px solid ${entitat?.conf?.colorLletra}`
            }}
            title={<img src={ripea_logo} alt={"logo_aplicacion"} style={{ height: '65px', paddingTop: '8px'}} />}
            appbarStyle={{
                height: '64px',
                color: entitat?.conf?.colorFons ? theme.palette.getContrastText(entitat?.conf?.colorFons) : undefined,
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
