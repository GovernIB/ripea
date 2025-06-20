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
    MenuEntry,
    useBaseAppContext,
    useResourceApiContext,
    useResourceApiService,
} from 'reactlib';
import Footer from './Footer';
import drassana from '../assets/drassana.png';
import UserHeadToolbar from "../pages/user/UserHeadToolbar.tsx";
import UserMenuButton, {UserMenu} from "../pages/user/UserMenu.tsx";
import {useAlertesSession} from "./SseClient.tsx";
import {useUserSession} from "./Session";
import AlertExpand from "./AlertExpand.tsx";

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
    style?: any;
    logoStyle?: any;
    title?: string | React.ReactElement;
    title_logo?: string;
    version: string;
    menuEntries?: MenuEntryWithResource[];
    appbarBackgroundColor?: string;
    appbarBackgroundImg?: string;
};

const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    return <RouterLink ref={ref} {...itemProps} role={undefined} />;
});

const getAlertSeverity = (avisNivell: string) => {
  switch (avisNivell) {
    case "INFO":
      return "info"; // Azul
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
            const resourceNames = apiLinks?.map((l: any) => l.rel);
            const processedMenuEntries = menuEntries?.
                filter(e => e?.resourceName == null || resourceNames?.includes(e.resourceName)).
                map(e => {
                    const { resourceName, ...otherProps } = e;
                    return otherProps;
                });
            setprocessedMenuEntries(processedMenuEntries);
        }
    }, [apiIsReady, apiIndex]);
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
            <div style={{ height: '36px', width: '100%' }} />
            <Footer
                title="RIPEA"
                version={version}
                logos={[drassana]}
                backgroundColor="#5F5D5D"
                style={{ position: 'fixed', height: '36px', bottom: 0, width: '100%' }}
            />
        </>
    );
};

export const BaseApp: React.FC<BaseAppProps> = (props) => {
    const {
        code,
        logo,
        style,
        logoStyle,
        title,
        version,
        menuEntries,
        appbarBackgroundColor,
        appbarBackgroundImg,
        children
    } = props;
    const navigate = useNavigate();
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
    return <MuiBaseApp
        code={code}
        headerTitle={title}
        headerLogo={logo}
        headerLogoStyle={logoStyle}
        headerVersion={version}
        headerAppbarStyle={style}
        headerAppbarBackgroundColor={appbarBackgroundColor}
        headerAppbarBackgroundImg={appbarBackgroundImg}
        headerAdditionalComponents={[<UserHeadToolbar/>, <UserMenuButton/>]}
        headerAdditionalAuthComponents={<UserMenu/>}
        footer={generateFooter(version)}
        persistentSession
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
        menuEntries={baseAppMenuEntries}>
        <CustomLocalizationProvider>
            <div>
                {
                    value?.avisosUsuari?.map((avis:any) => (
                        <AlertExpand key={avis.id} title={avis.assumpte} severity={getAlertSeverity(avis.avisNivell)}>
                            {avis.missatge}
                        </AlertExpand>
                    ))
                }
            </div>
            {i18nInitialized && children}
        </CustomLocalizationProvider>
    </MuiBaseApp>;
}

export default BaseApp;