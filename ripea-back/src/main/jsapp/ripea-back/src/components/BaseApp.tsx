import React from 'react';
import {
    useNavigate,
    useLocation,
    Link as RouterLink,
    LinkProps as RouterLinkProps,
} from 'react-router-dom';
import i18n from '../i18n/i18n';
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
import Footer from './Footer';
import drassana from '../assets/drassana.png';
import UserHeadToolbar from "../pages/user/UserHeadToolbar.tsx";
import {UserMenu} from "../pages/user/UserMenu.tsx";
import AppFormFieldReference from './AppFormFieldReference';
import {useAlertesSession} from "./SseClient.tsx";
import {useUserSession} from "./Session";
import AlertExpand from "./AlertExpand.tsx";
import {useSession} from "./SessionStorageContext.tsx";
import {Box, useTheme} from "@mui/material";
import {EstilMenuProp} from "@src/components/ThemeUserProvider.tsx";

export const HEIGHT_FOOTER = 36;

/**
 * Amplada del menú lateral plegat. La de reactlib (calc(spacing(7) + 1px) = 57px) deixa
 * els icones 8px a l'esquerra dels del capçal; amb 74px queden alineats, perquè quan el
 * menú està plegat l'icona es centra dins del calaix.
 */
const MENU_COLLAPSED_WIDTH = 74;
/**
 * Selector del calaix del menú quan està plegat: en mode compacte reactlib embolcalla la
 * llista dins d'un <div> (el contenidor del panell flotant dels submenús), mentre que
 * desplegat la llista penja directament del paper.
 */
const MENU_COLLAPSED_DRAWER_SELECTOR = '& nav .MuiDrawer-root:has(.MuiDrawer-paper > div > .MuiList-root)';

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

const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    return <RouterLink ref={ref} {...itemProps} role={undefined} />;
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

const generateFooter = (version?:string) => {
    return (
        <>
            {/*<div style={{ height: `${HEIGHT_FOOTER}px`, width: '100%' }} />*/}
            <Footer
                title="RIPEA"
                version={version}
                logos={[drassana]}
                backgroundColor="#5F5D5D"
                // style={{ position: 'fixed', bottom: 0, height: `${HEIGHT_FOOTER}px`, width: '100%', zIndex: 1200 }}
                style={{ height: `${HEIGHT_FOOTER}px`, width: '100%' }}
            />
        </>
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
    if (theme.palette.mode === 'dark') {
        return {
            background: '#FFFFFF',
            textPrimary: '#1F2937',
            textSecondary: '#4B5563',
            divider: '#D1D5DB',
            accent: '#1976D2',
            selectedBackground: 'rgba(25, 118, 210, 0.12)',
            hoverBackground: 'rgba(0, 0, 0, 0.04)',
        };
    }
    return {
        background: '#1E293B',
        textPrimary: '#F8FAFC',
        textSecondary: '#CBD5E1',
        divider: '#475569',
        accent: '#60A5FA',
        selectedBackground: 'rgba(96, 165, 250, 0.18)',
        hoverBackground: 'rgba(255, 255, 255, 0.08)',
    };
};

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
    const { value } = useAlertesSession();
    const { value: read, save } = useSession('readAlerts');
    const menuColorSet = getMenuColorSet(theme, menuAppearance);
    const menuLayoutSx = {
        // Els icones del menú han de quedar alineats verticalment amb el del botó de
        // plegar/desplegar del capçal. Amb el menú desplegat reactlib posa 24px de padding a
        // l'enllaç i el contenidor de l'icona hi suma 8px de marge, així que es baixa el
        // padding a 16px. Només al primer nivell, per no tocar la sagnia dels submenús. El
        // padding és un style en línia, d'aquí l'!important.
        '& nav .MuiDrawer-paper > .MuiList-root > .MuiListItemButton-root': {
            paddingLeft: '16px !important',
        },
        // Amb el menú plegat l'icona es centra dins del calaix, per això s'alinea eixamplant-lo
        // en lloc de tocar el padding.
        [MENU_COLLAPSED_DRAWER_SELECTOR]: {
            width: MENU_COLLAPSED_WIDTH,
            '& .MuiDrawer-paper': { width: MENU_COLLAPSED_WIDTH },
        },
        // Panell flotant dels submenús del menú plegat: ha de començar on acaba el calaix i
        // estendre's fins a baix de la pantalla. Si queda separat o curt, per arribar al
        // submenú cal sortir del menú i el panell es tanca abans.
        '& nav .MuiDrawer-paper > div > .MuiBox-root': {
            bottom: 0,
            left: MENU_COLLAPSED_WIDTH,
        },
    };
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
                '&.Mui-selected': {
                    backgroundColor: menuColorSet?.selectedBackground,
                },
                '&.Mui-selected:hover': {
                    backgroundColor: menuColorSet?.selectedBackground,
                },
                '&:hover': {
                    backgroundColor: menuColorSet?.hoverBackground,
                },
            },
        },
    };

    return (<Box sx={menuColorSet ? { ...menuLayoutSx, ...menuColorSetSx } : menuLayoutSx}>
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
                <MuiBaseAppContext.Provider value={{ defaultMuiComponentProps: {} }}>
                    <UserHeadToolbar />
                </MuiBaseAppContext.Provider>
            }
            headerAdditionalAuthComponents={
                <MuiBaseAppContext.Provider value={{ defaultMuiComponentProps: {} }}>
                    <UserMenu />
                </MuiBaseAppContext.Provider>
            }
            footer={generateFooter(version)}
            // footerHeight={HEIGHT_FOOTER}
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
            marginsDisabled={false}
            formFieldComponents={[{ type: 'reference', component: AppFormFieldReference }]}
            menuEntries={baseAppMenuEntries}
        >
            <CustomLocalizationProvider>
                <div style={{ display: 'flex', flexDirection: 'column', width: '100%', height: '100%', gap: '8px' }}>
                    {value?.avisosUsuari?.map((avis: any) => {
                        if (!read?.includes?.(avis.id)) {
                            return (
                                <AlertExpand
                                    key={avis.id}
                                    title={avis.assumpte}
                                    severity={getAlertSeverity(avis.avisNivell)}
                                    sx={{ m: 0 }}
                                    onClose={() => {
                                        save([...(read ?? []), avis.id]);
                                    }}
                                    linkChildren={avis?.expedients}
                                >
                                    {avis.missatge}
                                </AlertExpand>
                            );
                        }
                    })}
                    {i18nInitialized && children}
                </div>
            </CustomLocalizationProvider>
        </MuiBaseApp>
    </Box>);
}

export default BaseApp;
