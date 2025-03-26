import React from 'react';
import componentsCa from '../i18n/componentsCa';
import componentsEn from '../i18n/componentsEn';
import componentsEs from '../i18n/componentsEs';
import { usePersistentState } from '../util/usePersistentState';
import { FormFieldComponent } from './form/FormField';
import { ResourceApiFormFieldDefault } from './form/FormFieldDefault';
import {
    BaseAppContext,
    MessageDialogShowFn,
    DialogButton,
    TemporalMessageShowFn,
    TemporalMessageSeverity,
    RouterNavigateFunction,
} from './BaseAppContext';
import { useResourceApiContext } from './ResourceApiContext';
import { ResourceApiUserSessionValuePair } from './ResourceApiContext';

export const MARGIN_UNIT_PX = 8;
export const LIB_I18N_NS = 'reactlib';
export const PERSISTENT_LANGUAGE_KEY = 'lang';
export const PERSISTENT_SESSION_KEY = 'user-session';

type I18nHandleLanguageChangeFn = (lang?: string) => void;
type I18nAddResourceBundleCallback = (lang: string, ns: string, bundle: any) => void;

type ContentComponentSlots = {
    appbar: React.ReactElement;
    footer?: React.ReactElement;
    menu?: React.ReactElement;
    offline: React.ReactElement;
};

export type BaseAppProps = React.PropsWithChildren & {
    code: string;
    persistentSession?: boolean;
    persistentLanguage?: boolean;
    i18nUseTranslation: (ns: string) => { t: any };
    i18nCurrentLanguage?: string;
    i18nHandleLanguageChange?: I18nHandleLanguageChangeFn;
    i18nAddResourceBundleCallback?: I18nAddResourceBundleCallback;
    routerGoBack: (fallback?: string) => void;
    routerNavigate: RouterNavigateFunction;
    routerAnyHistoryEntryExist: () => boolean;
    routerUseLocationPath: () => string;
    linkComponent: React.ElementType;
    saveAs?: (data: Blob | string, filename?: string) => void;
    formFieldComponents?: FormFieldComponent[];
    contentComponentSlots: ContentComponentSlots;
};

export type BaseAppContentComponentProps = React.PropsWithChildren & {
    offline: boolean;
    appReady: boolean;
    marginsDisabled: boolean;
    contentExpandsToAvailableHeight: boolean;
    appbarComponent?: React.ReactElement;
    footerComponent?: React.ReactElement;
    menuComponent?: React.ReactElement;
    offlineComponent?: React.ReactElement;
    legacyMargins?: boolean;
};

const useDialog = () => {
    const dialogShowFn = React.useRef<MessageDialogShowFn>(undefined);
    const setMessageDialogShow = (fn: MessageDialogShowFn) => {
        dialogShowFn.current = fn;
    }
    const messageDialogShow: MessageDialogShowFn = (
        title: string | null,
        message: string | React.ReactElement,
        dialogButtons?: DialogButton[],
        componentProps?: any) => {
        if (dialogShowFn.current) {
            return dialogShowFn.current(title, message, dialogButtons, componentProps);
        } else {
            console.warn('Dialog component not configured in BaseApp');
            return new Promise((_resolve, reject) => reject());
        }
    }
    return {
        setMessageDialogShow,
        messageDialogShow,
    };
}

const useTemporalMessage = () => {
    const temporalMessageShowFn = React.useRef<TemporalMessageShowFn>(undefined);
    const setTemporalMessageShow = (fn: TemporalMessageShowFn) => {
        temporalMessageShowFn.current = fn;
    }
    const temporalMessageShow: TemporalMessageShowFn = (
        title: string | null,
        message: string,
        severity?: TemporalMessageSeverity,
        additionalComponents?: React.ReactElement[]) => {
        if (temporalMessageShowFn.current) {
            temporalMessageShowFn.current(title, message, severity, additionalComponents);
        } else {
            console.warn('Temporal message component not configured in BaseApp');
        }
    }
    return {
        setTemporalMessageShow,
        temporalMessageShow,
    };
}

const useFormFieldComponents = (formFieldComponents?: FormFieldComponent[]) => {
    const formFieldComponentsMap: any = {};
    formFieldComponents?.forEach(ffc => {
        formFieldComponentsMap[ffc.type] = ffc.component;
    });
    const formFieldComponentsRef = React.useRef<any>(formFieldComponentsMap);
    const getFormFieldComponent = (type?: string) => {
        if (type && formFieldComponentsRef.current && formFieldComponentsRef.current[type]) {
            return formFieldComponentsRef.current[type];
        } else {
            console.warn('Form field type ' + type + ' not found, using default')
            return ResourceApiFormFieldDefault;
        }
    }
    return getFormFieldComponent;
}

const useI18n = (
    code: string,
    persistentLanguage: boolean,
    i18nUseTranslation: (ns: string) => { t: any },
    i18nCurrentLanguage?: string,
    i18nHandleLanguageChange?: I18nHandleLanguageChangeFn,
    i18nAddResourceBundleCallback?: I18nAddResourceBundleCallback) => {
    const {
        persistentStateReady,
        persistentStateGet,
        persistentStateSet,
    } = usePersistentState(code);
    const { t: tI18Next } = i18nUseTranslation(LIB_I18N_NS);
    const {
        currentLanguage,
        setCurrentLanguage,
    } = useResourceApiContext();
    React.useEffect(() => {
        i18nAddResourceBundleCallback?.('ca', LIB_I18N_NS, componentsCa);
        i18nAddResourceBundleCallback?.('es', LIB_I18N_NS, componentsEs);
        i18nAddResourceBundleCallback?.('en', LIB_I18N_NS, componentsEn);
    }, []);
    React.useEffect(() => {
        if (persistentLanguage && persistentStateReady && currentLanguage == null) {
            const lang = persistentStateGet(PERSISTENT_LANGUAGE_KEY);
            setCurrentLanguage(lang ?? '');
        }
    }, [persistentStateReady]);
    React.useEffect(() => {
        if (persistentLanguage && persistentStateReady) {
            persistentStateSet(PERSISTENT_LANGUAGE_KEY, currentLanguage);
        }
    }, [currentLanguage]);
    React.useEffect(() => {
        i18nCurrentLanguage && setCurrentLanguage(i18nCurrentLanguage);
    }, [i18nCurrentLanguage]);
    React.useEffect(() => {
        currentLanguage && i18nHandleLanguageChange?.(currentLanguage);
    }, [currentLanguage]);
    const t = (key: string, params?: any) => tI18Next(key, params);
    return {
        currentLanguage,
        setCurrentLanguage,
        t
    };
}

const useUserSession = (code: string, persistentSession: boolean) => {
    const {
        userSession,
        setUserSession,
        setUserSessionAttributes
    } = useResourceApiContext();
    const {
        persistentStateReady,
        persistentStateGet,
        persistentStateSet,
        persistentStateRemove,
    } = usePersistentState(code);
    React.useEffect(() => {
        if (persistentSession && persistentStateReady && userSession == null) {
            const session = persistentStateGet(PERSISTENT_SESSION_KEY);
            setUserSession(session ?? {});
        }
    }, [persistentStateReady]);
    React.useEffect(() => {
        if (persistentSession && persistentStateReady) {
            persistentStateSet(PERSISTENT_SESSION_KEY, userSession);
        }
    }, [userSession]);
    const localSetUserSessionAttribute = (attribute: string, value: any): boolean => {
        return localSetUserSessionAttributes([{ attribute, value }])
    }
    const localSetUserSessionAttributes = (attributeValuePairs: ResourceApiUserSessionValuePair[]): boolean => {
        if (persistentSession) {
            const session = persistentStateGet(PERSISTENT_SESSION_KEY);
            const changes: any = {};
            attributeValuePairs.forEach(c => changes[c.attribute] = c.value);
            persistentStateSet(PERSISTENT_SESSION_KEY, { ...session, ...changes });
        }
        return setUserSessionAttributes(attributeValuePairs);
    }
    return {
        userSession,
        setUserSessionAttribute: localSetUserSessionAttribute,
        setUserSessionAttributes: localSetUserSessionAttributes,
        persistentStateReady,
        persistentStateGet,
        persistentStateSet,
        persistentStateRemove
    };
}

const ContentComponentDefault: React.FC<BaseAppContentComponentProps> = (props) => {
    const {
        offline,
        appReady,
        marginsDisabled,
        contentExpandsToAvailableHeight,
        appbarComponent,
        footerComponent,
        menuComponent,
        offlineComponent,
        children,
    } = props;
    const margins = {
        margin: '16px 24px',
    };
    const mainBoxHeight = contentExpandsToAvailableHeight ? '100vh' : undefined;
    const childrenOrOfflineComponent = !offline ? children : offlineComponent;
    return <div style={{ display: 'flex', flexDirection: 'column', height: mainBoxHeight }}>
        {appbarComponent}
        <div style={{
            display: 'flex',
            flexGrow: 1,
            minHeight: 0,
            ...(!marginsDisabled ? margins : null)
        }}>
            {menuComponent}
            <main style={{
                flexGrow: 1,
                minWidth: 0,
            }}>
                {appReady ? childrenOrOfflineComponent : null}
            </main>
        </div>
        {footerComponent}
    </div>;
}

export const BaseApp: React.FC<BaseAppProps> = (props) => {
    const {
        code,
        persistentSession,
        persistentLanguage,
        i18nUseTranslation,
        i18nCurrentLanguage,
        i18nHandleLanguageChange,
        i18nAddResourceBundleCallback,
        routerGoBack,
        routerNavigate,
        routerUseLocationPath,
        routerAnyHistoryEntryExist,
        linkComponent,
        saveAs,
        formFieldComponents,
        contentComponentSlots,
        children,
    } = props;
    // TODO: undo
    const { offline } = useResourceApiContext();
    // const offline  = false;
    const [marginsDisabled, setMarginsDisabled] = React.useState<boolean>(false);
    const [contentExpandsToAvailableHeight, setContentExpandsToAvailableHeight] = React.useState<boolean>(false);
    const getLinkComponent = () => linkComponent;
    const {
        setMessageDialogShow,
        messageDialogShow,
    } = useDialog();
    const {
        setTemporalMessageShow,
        temporalMessageShow
    } = useTemporalMessage();
    const getFormFieldComponent = useFormFieldComponents(formFieldComponents);
    const {
        currentLanguage,
        setCurrentLanguage,
        t,
    } = useI18n(
        code,
        persistentLanguage ?? false,
        i18nUseTranslation,
        i18nCurrentLanguage,
        i18nHandleLanguageChange,
        i18nAddResourceBundleCallback);
    const {
        userSession,
        setUserSessionAttribute,
        setUserSessionAttributes,
        persistentStateReady,
        persistentStateGet,
        persistentStateSet,
        persistentStateRemove
    } = useUserSession(code, persistentSession ?? false);
    const context = {
        getFormFieldComponent,
        setMarginsDisabled,
        contentExpandsToAvailableHeight,
        setContentExpandsToAvailableHeight,
        getLinkComponent,
        goBack: routerGoBack,
        navigate: routerNavigate,
        anyHistoryEntryExist: routerAnyHistoryEntryExist,
        useLocationPath: routerUseLocationPath,
        setMessageDialogShow,
        messageDialogShow,
        setTemporalMessageShow,
        temporalMessageShow,
        userSession,
        setUserSessionAttribute,
        setUserSessionAttributes,
        currentLanguage,
        setCurrentLanguage,
        t,
        persistentStateReady,
        persistentStateGet,
        persistentStateSet,
        persistentStateRemove,
        saveAs,
    };
    const sessionReady = !persistentSession || userSession != null;
    const languageReady = !persistentLanguage || currentLanguage != null;
    const appReady = sessionReady && languageReady;
    return <BaseAppContext.Provider value={context}>
        <ContentComponentDefault
            offline={offline}
            appReady={appReady}
            marginsDisabled={marginsDisabled}
            contentExpandsToAvailableHeight={contentExpandsToAvailableHeight}
            appbarComponent={contentComponentSlots.appbar}
            footerComponent={contentComponentSlots.footer}
            menuComponent={contentComponentSlots.menu}
            offlineComponent={contentComponentSlots.offline}>
            {children}
        </ContentComponentDefault>
    </BaseAppContext.Provider>;
}

export default BaseApp;