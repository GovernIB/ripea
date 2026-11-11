import React, { useEffect } from 'react';
import { BaseApp } from './components/BaseApp';
import ripea_logo from './assets/Drassana_RIP_DRA_COL.svg';
import ripea_logo_blanc from './assets/Drassana_RIP_DRA_COL_blanc.svg';
import goib_logo_light from './assets/goib_logo_light.svg';
import goib_logo_dark from './assets/goib_logo_dark.svg';
import AppRoutes from './AppRoutes';
import { useCapsaleraColors, useEntitatSession } from './components/Session.tsx';
import TitleHeaderConfigurator from './TitleHeaderConfigurator.tsx';
import { useTranslation } from 'react-i18next';
import { useCombinedMenu } from './hooks/useCombinedMenu.ts';
import { useTheme } from '@mui/material';
import { TemporalMessageBridge } from './components/PreBaseAppProvider.tsx';
import { useThemeUserContext } from '@src/components/ThemeUserProvider.tsx';
import { APPBAR_PADDING_LEFT, HEADER_HEIGHT, LOGO_BOX_LEFT, LOGO_HEIGHT, LOGO_SEPARATOR_GAP, MENU_WIDTH } from './util/layout';

type FaviconLink = { href: string; type?: string; sizes?: string };

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

export const getImgFromBytes = (img: string): string | undefined => {
    return img ? `data:image/png;base64,${img}` : undefined;
};

export const App: React.FC = () => {
    const version = '1.0.1';
    const { value: entitat } = useEntitatSession();
    const capsalera = useCapsaleraColors();
    const { i18n } = useTranslation();
    const { sideMenuEntries: menuEntries, additionalContents: additionalContentsMenu } = useCombinedMenu();
    const theme = useTheme();
    const { preview } = useThemeUserContext();
    const isDark = theme.palette.mode === 'dark';

    // Sincronizar lang del <html> con el idioma activo de i18next
    useEffect(() => {
        document.documentElement.lang = i18n.language;
    }, [i18n.language]); // ← se ejecuta en el arranque y cada vez que cambie

    useEffect(() => {
        changeFavicon(getImgFromBytes(entitat?.conf?.favicon));
    }, [entitat?.conf?.favicon]);

    return (
        <BaseApp
            code="cmd"
            logo={getImgFromBytes(entitat?.conf?.logo) || (isDark ? goib_logo_dark : goib_logo_light)}
            logoStyle={{
                // El logo de l'entitat pot ser de qualsevol proporció: no ha de sortir de la caixa.
                '& img': { height: LOGO_HEIGHT + 'px', maxWidth: '100%', objectFit: 'contain', display: 'block' },
                // El separador vertical ha de caure sobre la vora dreta del menú obert, i omplir
                // l'alçada de la capçalera. El botó de menú duu un marge esquerre de -12 i un dret de
                // 16, així que la caixa del logo arrenca a LOGO_BOX_LEFT. Fixant-ne l'amplada (en
                // comptes de deixar que la mida del logo mani) la vora cau sempre a MENU_WIDTH.
                width: MENU_WIDTH - LOGO_BOX_LEFT + 'px',
                boxSizing: 'border-box',
                alignSelf: 'stretch',
                // Mateix color que la vora dreta del menú lateral (la del Drawer de MUI), perquè les
                // dues línies facin una sola.
                borderRight: `1px solid ${theme.palette.divider}`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-end',
                // Substitueix el mr/pt/pr de la llibreria (l'sx s'hi fusiona al final).
                mr: 0,
                pt: 0,
                pl: 0,
                pr: LOGO_SEPARATOR_GAP + 'px',
            }}
            title={
                <img
                    src={isDark ? ripea_logo_blanc : ripea_logo}
                    alt={'logo_aplicacion'}
                    style={{ marginLeft: LOGO_SEPARATOR_GAP + 'px', height: LOGO_HEIGHT + 'px', verticalAlign: 'middle' }}
                />
            }
            // El botó de menú duu ml -12, així que amb APPBAR_PADDING_LEFT queda centrat a la mateixa
            // columna que les icones del menú lateral. L'alçada mínima substitueix els 64px que la
            // llibreria dona a la barra.
            appbarStyle={{
                color: capsalera.lletra,
                paddingLeft: APPBAR_PADDING_LEFT + 'px',
                minHeight: HEADER_HEIGHT + 'px',
            }}
            version={version}
            appbarBackgroundColor={capsalera.fons}
            menuEntries={menuEntries}
            menuAppearance={preview?.estilMenu}
        >
            <TitleHeaderConfigurator />
            <AppRoutes />
            <TemporalMessageBridge />
            {additionalContentsMenu.map((content: React.ReactNode, index: number) => (
                <React.Fragment key={index}>{content}</React.Fragment>
            ))}
        </BaseApp>
    );
};

export default App;
