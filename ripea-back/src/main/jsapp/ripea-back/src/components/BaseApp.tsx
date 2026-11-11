import React from 'react';
import {
    useNavigate,
    useLocation,
    Link as RouterLink,
    LinkProps as RouterLinkProps,
} from 'react-router-dom';
import i18n from '../i18n/i18n';
import reactlibCa from '../i18n/reactlibCa';
import reactlibEs from '../i18n/reactlibEs';
import { useTranslation } from 'react-i18next';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import {
    MuiBaseApp,
    MuiBaseAppContext,
    MenuEntry,
    useBaseAppContext,
    useResourceApiContext,
    useResourceApiService,
} from 'reactlib';
import DrassanaFooter from './DrassanaFooter';
import UserHeadToolbar from "../pages/user/UserHeadToolbar.tsx";
import {UserMenu} from "../pages/user/UserMenu.tsx";
import AppFormFieldReference from './AppFormFieldReference';
import FormFieldToggleButtons, { FORM_FIELD_TYPE_TOGGLE_BUTTONS } from './FormFieldToggleButtons';
import {useUserSession} from "./Session";
import AvisosBanner from "./AvisosBanner.tsx";
import AvisosChips from "./AvisosChips.tsx";
import { AvisosProvider } from "./AvisosProvider.tsx";
import {alpha, Box, useTheme} from "@mui/material";
import {EstilMenuProp} from "@src/components/ThemeUserProvider.tsx";
import {FOOTER_HEIGHT, MAIN_GAP, MAIN_PADDING} from "../util/layout.ts";

export type MenuEntryWithResource = MenuEntry & {
    resourceName?: string;
}

export type HeaderBackgroundModuleItem = {
    color?: string;
    image?: string;
}

export type BaseAppProps = React.PropsWithChildren & {
    code: string;
    logo?: string;
    appbarStyle?: any;
    logoStyle?: any;
    title?: string | React.ReactElement;
    title_logo?: string;
    version: string;
    menuEntries?: MenuEntryWithResource[];
    menuAppearance?: EstilMenuProp;
    appbarBackgroundColor?: string;
    appbarBackgroundImg?: string;
};

/**
 * Enllaç que base-react fa servir a tots els components amb `to` (menú, botons, files de graella).
 * Les entrades del menú lateral són ListItemButton que la llibreria posa directament dins el <ul> del
 * menú, sense <li>: una llista mal construïda i "buida" (WCAG 1.3.1, verificació 1.3 de l'OAW). Com
 * que no es pot tocar la llibreria, quan l'enllaç el pinta un ListItemButton s'embolcalla amb un <li>
 * (veure també els selectors del menú a theme.ts). Les entrades amb fills (grups) no passen per
 * aquí: la llibreria les pinta com a <div>, igual que el contenidor del seu submenú.
 */
const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    const link = <RouterLink ref={ref} {...itemProps} role={undefined} />;
    return itemProps.className?.includes('MuiListItemButton-root') ? <li>{link}</li> : link;
});

export const getAlertSeverity = (avisNivell: string) => {
  switch (avisNivell) {
    case "OK":
      return "success"; // Verd
    case "INFO":
      return "info"; // Azul
    case "WARN":
    case "WARNING":
      return "warning"; // Amarillo
    case "ERROR":
      return "error"; // Rojo
    default:
      return "info"; // Por defecto INFO
  }
};

const useBaseAppMenuEntries = (menuEntries?: MenuEntryWithResource[]) => {
    const [processedMenuEntries, setprocessedMenuEntries] = React.useState<MenuEntry[]>();
    const { isReady: apiIsReady, indexState: apiIndex } = useResourceApiContext();
    React.useEffect(() => {
        if (apiIsReady) {
            const apiLinks = apiIndex?.links.getAll();
            const resourceNames = apiLinks?.map((link: any) => link.rel);
            const processedMenuEntries = menuEntries?.
                filter(e => e?.resourceName == null || resourceNames?.includes(e.resourceName)).
                map(e => {
                    const { resourceName, ...otherProps } = e;
                    return otherProps;
                });
            setprocessedMenuEntries(processedMenuEntries);
        }
    }, [apiIsReady, apiIndex, menuEntries]);
    return processedMenuEntries;
}

const useLocationPath = () => {
    const location = useLocation();
    return location.pathname;
}

// Espai de noms on base-react registra les seves traduccions (LIB_I18N_NS a lib/components/
// BaseApp.tsx, que la llibreria no exporta) i sobreescriptures de l'aplicació per idioma.
// L'anglès de la llibreria ja és imperatiu, per això no té paquet.
const REACTLIB_I18N_NS = 'reactlib';
const REACTLIB_OVERRIDES: Record<string, any> = { ca: reactlibCa, es: reactlibEs };

const useI18n = () => {
    const { value: currentUser } = useUserSession();
    const { isReady: apiIsReady, getOne: apiGetOne } = useResourceApiService('usuariResource');
    const [currentUserLanguage, setCurrentUserLanguage] = React.useState<string>();
    React.useEffect(() => {
        if (currentUser != null && apiIsReady) {
            apiGetOne(currentUser?.codi).then((data: any) => {
                setCurrentUserLanguage(data.idioma ?? null);
            });
        }
    }, [currentUser, apiIsReady]);
    const i18nHandleLanguageChange = (language?: string) => {
        i18n.changeLanguage(language);
    }
    const i18nAddResourceBundleCallback = (language: string, namespace: string, bundle: any) => {
        i18n.addResourceBundle(language, namespace, bundle);
        // base-react registra aquí les seves traduccions. Com que no s'hi pot tocar, els seus
        // textos es corregeixen fusionant-hi a sobre el paquet de sobreescriptures de
        // l'aplicació (veure i18n/reactlibCa.ts): fusió profunda -- només se substitueixen les
        // claus declarades -- i amb sobreescriptura, perquè la clau ja existeix.
        const overrides = namespace === REACTLIB_I18N_NS ? REACTLIB_OVERRIDES[language] : undefined;
        if (overrides != null) {
            i18n.addResourceBundle(language, namespace, overrides, true, true);
        }
    }
    return {
        i18nUseTranslation: useTranslation,
        i18nCurrentLanguage: currentUserLanguage ?? i18n.language,
        i18nHandleLanguageChange,
        i18nAddResourceBundleCallback,
        i18nInitialized: currentUserLanguage !== undefined
    }
}

const CustomLocalizationProvider = ({ children }: React.PropsWithChildren) => {
    const { currentLanguage } = useBaseAppContext();
    const adapterLocale = React.useMemo(() => {
        const languageTwoChars = currentLanguage?.substring(0, 2).toLowerCase();
        switch (languageTwoChars) {
            case 'ca':
            case 'es':
            case 'en':
                return languageTwoChars;
            default:
                return 'ca';
        }
    }, [currentLanguage]);
    const adapter = AdapterDayjs;
    return <LocalizationProvider dateAdapter={adapter} adapterLocale={adapterLocale}>
        {children}
    </LocalizationProvider>;
}

// El peu va per flux (no fix a baix): el marc de frameSx fa que ocupi sempre l'última fila.
const generateFooter = (version?:string) => {
    return (
        <div style={{ height: FOOTER_HEIGHT + 'px', overflow: 'hidden' }}>
            <DrassanaFooter title="RIPEA" version={version} backgroundColor="#5F5D5D" />
        </div>
    );
};

const getMenuColorSet = (
    theme: any,
    appearance: EstilMenuProp = EstilMenuProp.theme,
): any | undefined => {
    // `theme` no sobreescriu colors: el menú reutilitza directament la paleta activa de MUI.
    if (appearance === EstilMenuProp.footer) {
        return {
            background: '#5F5D5D',
            textPrimary: '#F6F6F6',
            textSecondary: '#E5E5E5',
            divider: '#807D7D',
            accent: '#FFFFFF',
            selectedBackground: 'rgba(255, 255, 255, 0.12)',
            hoverBackground: 'rgba(255, 255, 255, 0.08)',
        };
    }
    if (appearance !== EstilMenuProp.inverse) {
        return undefined;
    }
    // L'accent i la selecció surten de la identitat (taronja): `primary.dark` sobre el fons blanc i
    // `primary.light` sobre el fosc, com `accentColor` a theme.ts.
    if (theme.palette.mode === 'dark') {
        return {
            background: '#FFFFFF',
            textPrimary: '#1E1E1E',
            textSecondary: '#666666',
            divider: '#E0E0E0',
            accent: theme.palette.primary.dark,
            selectedBackground: alpha(theme.palette.primary.main, 0.16),
            hoverBackground: 'rgba(0, 0, 0, 0.04)',
        };
    }
    return {
        background: '#2E2E2E',
        textPrimary: '#F6F6F6',
        textSecondary: '#D0D0D0',
        divider: '#555555',
        accent: theme.palette.primary.light,
        selectedBackground: alpha(theme.palette.primary.main, 0.22),
        hoverBackground: 'rgba(255, 255, 255, 0.08)',
    };
};

// La geometria del menú lateral (amplada plegada, icones, columna on s'alineen amb el botó de la
// capçalera, panell flotant dels submenús) s'ajusta al tema, a `MuiDrawer` de theme.ts, amb les mides
// de util/layout.ts. Aquí només hi queden els colors, que depenen de l'estil de menú del perfil de
// l'usuari.

/**
 * Marc de l'aplicació: l'arrel ocupa tota l'alçada de la finestra (capçalera dalt, peu baix) i només
 * l'àrea de contingut (`<main>`) fa scroll. La llibreria només ho fa amb `100vh` a les pantalles que
 * l'activen (GridPage), i no fixa cap marge propi quan es desactiven (`marginsDisabled`). Per això:
 *  - `> div`: arrel del contingut de la llibreria (ContentComponentDefault, lib/components/BaseApp.tsx);
 *  - `> div > div > main`: l'àrea de contingut. El `gap` és la separació entre els elements apilats
 *    (la pantalla, els avisos...) i el padding, el marge de l'àrea.
 * Si la llibreria canvia aquesta estructura, aquest és l'únic lloc a revisar.
 */
const frameSx = {
    height: '100vh',
    '& > div': { height: '100%' },
    '& > div > div > main': {
        display: 'flex',
        flexDirection: 'column',
        gap: MAIN_GAP + 'px',
        padding: MAIN_PADDING + 'px',
        boxSizing: 'border-box',
        minHeight: 0,
        overflowY: 'auto',
        // GridPage torna a activar el marge de la llibreria (16px 24px, en línia) en desmuntar-se, i les
        // pantalles que no en duen en tindrien dos: el marge d'`<main>` és sempre aquest padding.
        margin: '0 !important',
    },
    // Pantalla amb ScreenFrame (dins de GridPage, que hi posa `height: 100%`): amb avisos a sobre seria
    // més alta que `<main>` i faria scroll el conjunt, títol inclòs. Es comporta com a fila flexible: ocupa
    // l'alçada que deixen els avisos i és el contingut del marc qui fa scroll. Només les pantalles amb ScreenFrame.
    '& > div > div > main > div:has(> .screen-frame)': { flex: '1 1 0', minHeight: 0 },
    // Les entrades de submenú (llista dins la llista de l'entrada pare) ja duen la seva pròpia guia
    // vertical de la llibreria (`:before` de Menu.tsx, 2px a 40px); la vora de 4px de l'entrada
    // seleccionada (theme.ts i estils de menú d'aquí baix) hi quedaria doblada. Més específic que
    // les dues, així no depèn de l'ordre. El panell flotant del menú plegat no hi entra: no és
    // dins cap `.MuiList-root`.
    '& nav .MuiDrawer-root .MuiList-root .MuiList-root .MuiListItemButton-root.Mui-selected': {
        boxShadow: 'none',
    },
} as const;

export const BaseApp: React.FC<BaseAppProps> = (props) => {
    const {
        code,
        logo,
        appbarStyle,
        logoStyle,
        title,
        version,
        menuEntries,
        menuAppearance,
        appbarBackgroundColor,
        appbarBackgroundImg,
        children
    } = props;
    const navigate = useNavigate();
    const theme = useTheme();
    const location = useLocation();
    const baseAppMenuEntries = useBaseAppMenuEntries(menuEntries);
    const {
        i18nUseTranslation,
        i18nCurrentLanguage,
        i18nHandleLanguageChange,
        i18nAddResourceBundleCallback,
        i18nInitialized,
    } = useI18n();
    const anyHistoryEntryExist = () => location.key !== 'default';
    const goBack = (fallback?: string) => {
        if (anyHistoryEntryExist()) {
            navigate(-1);
        } else if (fallback != null) {
            navigate(fallback);
        } else {
            console.warn('[BACK] Couldn\'t go back, neither fallback specified nor previous entry exists in navigation history');
        }
    }
    const menuColorSet = getMenuColorSet(theme, menuAppearance);
    const menuColorSetSx = {
        '& nav .MuiDrawer-root': {
            '& .MuiPaper-root, & .MuiList-root': {
                backgroundColor: menuColorSet?.background,
                color: menuColorSet?.textPrimary,
                '& > div .MuiBox-root': {
                    backgroundColor: menuColorSet?.background,
                    borderColor: menuColorSet?.divider,
                },
                '& > div > .MuiBox-root': {
                    borderLeft: `1px solid ${menuColorSet?.divider}`,
                },
                '& p': {
                    color: menuColorSet?.textPrimary,
                },
                '& h6': {
                    color: menuColorSet?.accent,
                },
            },
            '& .menu-item-icon': {
                color: menuColorSet?.textSecondary,
            },
            '& .MuiListItemButton-root': {
                // La icona de l'entrada seleccionada pren l'accent del joc de colors del menú (a TEMA
                // el posa el tema, veure MuiListItemButton a theme.ts).
                '&.Mui-selected .menu-item-icon': {
                    color: menuColorSet?.accent,
                },
                '&.Mui-selected': {
                    backgroundColor: menuColorSet?.selectedBackground,
                    boxShadow: `inset 4px 0 0 ${menuColorSet?.accent}`,
                },
                '&.Mui-selected:hover': {
                    backgroundColor: menuColorSet?.selectedBackground,
                },
                // Guia vertical dels submenús (la dibuixa la llibreria en blau fix)
                '&.Mui-selected :before': {
                    background: menuColorSet?.accent,
                },
                '&:hover': {
                    backgroundColor: menuColorSet?.hoverBackground,
                },
            },
        },
    };

    return (
        <AvisosProvider>
            <Box sx={menuColorSet ? { ...frameSx, ...menuColorSetSx } : frameSx}>
                <MuiBaseApp
                    code={code}
                    headerTitle={title}
                    headerLogo={logo}
                    headerLogoStyle={logoStyle}
                    headerVersion={version}
                    headerAppbarStyle={appbarStyle}
                    headerAppbarBackgroundColor={appbarBackgroundColor}
                    headerAppbarBackgroundImg={appbarBackgroundImg}
                    headerAdditionalComponents={
                        <>
                            <MuiBaseAppContext.Provider value={{ defaultMuiComponentProps: {} }}>
                                <UserHeadToolbar />
                            </MuiBaseAppContext.Provider>
                            <AvisosChips />
                        </>
                    }
                    headerAdditionalAuthComponents={
                        <MuiBaseAppContext.Provider value={{ defaultMuiComponentProps: {} }}>
                            <UserMenu />
                        </MuiBaseAppContext.Provider>
                    }
                    footer={generateFooter(version)}
                    footerHeight={FOOTER_HEIGHT}
                    persistentLanguage
                    i18nUseTranslation={i18nUseTranslation}
                    i18nCurrentLanguage={i18nCurrentLanguage}
                    i18nHandleLanguageChange={i18nHandleLanguageChange}
                    i18nAddResourceBundleCallback={i18nAddResourceBundleCallback}
                    routerGoBack={goBack}
                    routerNavigate={navigate}
                    routerUseLocationPath={useLocationPath}
                    routerAnyHistoryEntryExist={anyHistoryEntryExist}
                    linkComponent={Link}
                    // Els marges d'<main> els posa frameSx (la llibreria els deixa a 16px 24px).
                    marginsDisabled
                    formFieldComponents={[
                        { type: 'reference', component: AppFormFieldReference },
                        { type: FORM_FIELD_TYPE_TOGGLE_BUTTONS, component: FormFieldToggleButtons },
                    ]}
                    menuEntries={baseAppMenuEntries}
                >
                    <CustomLocalizationProvider>
                        <>
                            <AvisosBanner />
                            {i18nInitialized && children}
                        </>
                    </CustomLocalizationProvider>
                </MuiBaseApp>
            </Box>
        </AvisosProvider>
    );
}

export default BaseApp;
