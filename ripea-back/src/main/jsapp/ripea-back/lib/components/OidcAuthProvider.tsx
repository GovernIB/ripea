import React from 'react';
import { UserManager, UserManagerSettings, User } from 'oidc-client-ts';
import useLogConsole from '../util/useLogConsole';
import { toAbsolutePath, isCurrentPathMatching } from '../util/url';
import AuthContext, { AuthConfig } from './AuthContext';

const LOG_PREFIX = '[OAUTH]';
// Segons de validesa restants per davall dels quals es renova el token quan la pàgina torna a ser visible
const RENEW_ON_RESUME_THRESHOLD_SECONDS = 60;
const RENEW_ERRORS_REQUIRING_SIGNIN = ['login_required', 'invalid_grant'];

type AuthProviderProps = React.PropsWithChildren & {
    /** La configuració necessària per a crear la instància del UserManager */
    config: AuthConfig;
    /** URL base de l'aplicació (per a poder configurar les uris de redirect, post_logout i silent_redirect) */
    appBaseUrl: string;
    /** Indica que l'autenticació és obligatòria (no es pot veure res si no s'està autenticat) */
    mandatory?: true;
    /** Per a poder funcionar amb servidors Keycloak antics (per a poder posar /auth a davant /realms al construir la URL per authority) */
    urlRealmsPrefix?: string;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: true;
};

const userManagerNewInstance = (config: UserManagerSettings) => {
    const userManager = new UserManager(config);
    return userManager;
};

export const AuthProvider = (props: AuthProviderProps) => {
    const { config, appBaseUrl, mandatory, urlRealmsPrefix, debug, children } = props;
    const oidcAuthConfig = {
        authority: config.url + (urlRealmsPrefix ?? '') + '/realms/' + config.realm,
        client_id: config.clientId,
        redirect_uri: toAbsolutePath('?callback', appBaseUrl),
        post_logout_redirect_uri: toAbsolutePath('', appBaseUrl),
        response_type: 'code',
        scope: 'openid profile email',
        silent_redirect_uri: toAbsolutePath('oidcSilentRenew', appBaseUrl),
        // La renovació es gestiona des d'aquest component (renewToken) per a no fer-ne dues alhora amb el
        // mateix refresh token
        automaticSilentRenew: false,
    };
    const logConsole = useLogConsole(LOG_PREFIX);
    const hasInitialized = React.useRef(false);
    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [isAuthenticated, setIsAuthenticated] = React.useState<boolean>(false);
    const tokenRef = React.useRef<string>(undefined);
    const tokenParsedRef = React.useRef<any>(undefined);
    const userManagerRef = React.useRef<UserManager>(undefined);
    const renewPromiseRef = React.useRef<Promise<string | undefined>>(undefined);
    const mandatoryRef = React.useRef(mandatory);
    mandatoryRef.current = mandatory;
    const isAuthCallback = isCurrentPathMatching(oidcAuthConfig?.redirect_uri, true);
    const isAuthSilentRedirect = oidcAuthConfig?.silent_redirect_uri
        ? isCurrentPathMatching(oidcAuthConfig?.silent_redirect_uri, false)
        : false;
    const processUser = (user: User | null) => {
        if (user != null) {
            tokenRef.current = user.access_token;
            tokenParsedRef.current = user.profile;
            setIsLoading(false);
            setIsAuthenticated(true);
        } else {
            tokenRef.current = undefined;
            tokenParsedRef.current = undefined;
            setIsLoading(!!mandatoryRef.current);
            setIsAuthenticated(false);
        }
    };
    React.useEffect(() => {
        if (hasInitialized.current) {
            return; // evitem executar-ho la segona vegada (en dev amb StrictMode)
        }
        hasInitialized.current = true;
        const userManager = userManagerNewInstance(oidcAuthConfig);
        userManagerRef.current = userManager;
        const handleAuthFlow = async () => {
            try {
                if (isAuthSilentRedirect) {
                    debug && logConsole.debug('Callback de la renovació silenciosa');
                    await userManager.signinSilentCallback();
                } else if (isAuthCallback) {
                    debug && logConsole.debug('Callback des del servidor de recursos');
                    const user = await userManager.signinRedirectCallback();
                    window.history.replaceState({}, document.title, '/');
                    processUser(user);
                } else {
                    debug && logConsole.debug("Comprovant si l'usuari ja està autenticat");
                    const loadedUser = await userManager.getUser();
                    if (loadedUser && !loadedUser.expired) {
                        debug && logConsole.debug("S'ha trobat un usuari autenticat");
                        processUser(loadedUser);
                    } else {
                        debug && logConsole.debug('Provant renovació silenciosa');
                        try {
                            const user = await userManager.signinSilent();
                            debug &&
                                logConsole.debug(
                                    'Usuari resultant de la renovació silenciosa',
                                    user
                                );
                            processUser(user);
                        } catch (error: any) {
                            const requiresFreshSignin = [
                                'login_required',
                                'invalid_grant',
                            ].includes(error.error);
                            if (mandatory && requiresFreshSignin) {
                                debug &&
                                    logConsole.debug(
                                        'La renovació silenciosa ha fallat amb un codi ' +
                                            error.error +
                                            '. Redirigint a signin.'
                                    );
                                userManager.removeUser();
                                userManager.signinRedirect();
                            } else {
                                debug &&
                                    logConsole.debug('Error en la renovació silenciosa', error);
                                processUser(null);
                            }
                        }
                    }
                }
            } catch (error) {
                logConsole.error("Error durant el flux d'autenticació", error);
                processUser(null);
            }
        };
        handleAuthFlow();
        return () => {
            userManager.stopSilentRenew();
        };
    }, [config, debug, isAuthCallback, isAuthSilentRedirect, mandatory, logConsole]);
    const renewToken = React.useCallback((): Promise<string | undefined> => {
        const userManager = userManagerRef.current;
        if (userManager == null) {
            return Promise.resolve(undefined);
        }
        // Només es fa una renovació alhora: les crides concurrents reben la mateixa promesa
        if (renewPromiseRef.current != null) {
            return renewPromiseRef.current;
        }
        debug && logConsole.debug('Renovant token');
        const renewPromise = userManager
            .signinSilent()
            .then((user) => {
                debug && logConsole.debug('Token renovat correctament', user);
                processUser(user);
                return user?.access_token;
            })
            .catch((error) => {
                logConsole.error('Error renovant el token', error);
                if (RENEW_ERRORS_REQUIRING_SIGNIN.includes(error?.error)) {
                    // La sessió del servidor d'autenticació ha caducat: cal tornar a iniciar sessió
                    debug && logConsole.debug('Redirigint cap a la pantalla de login');
                    userManager.removeUser();
                    processUser(null);
                    mandatoryRef.current && userManager.signinRedirect();
                } else {
                    debug && logConsole.debug('No redirigim cap a la pantalla de login', error?.error);
                }
                return undefined;
            })
            .finally(() => {
                renewPromiseRef.current = undefined;
            });
        renewPromiseRef.current = renewPromise;
        return renewPromise;
    }, [debug, logConsole]);
    React.useEffect(() => {
        const userManager = userManagerRef.current;
        if (userManager) {
            const onAccessTokenExpiring = () => {
                debug && logConsole.debug("Token a punt d'expirar");
                renewToken();
            };
            const onAccessTokenExpired = () => {
                debug && logConsole.debug('Token expirat');
                renewToken();
            };
            // Quan el dispositiu es bloqueja o la pestanya queda en segon pla els temporitzadors s'aturen i el
            // token pot expirar sense haver-se renovat. Quan la pàgina torna a ser visible es comprova el token
            // i, si ha expirat o està a punt d'expirar, es renova immediatament.
            const onPageResume = () => {
                if (document.visibilityState !== 'visible') {
                    return;
                }
                userManager.getUser().then((user) => {
                    if (user != null) {
                        const expiresIn = user.expires_in;
                        if (expiresIn == null || expiresIn <= RENEW_ON_RESUME_THRESHOLD_SECONDS) {
                            debug &&
                                logConsole.debug(
                                    'Pàgina visible amb el token expirat o a punt d\'expirar',
                                    expiresIn
                                );
                            renewToken();
                        }
                    }
                });
            };
            userManager.events.addAccessTokenExpiring(onAccessTokenExpiring);
            userManager.events.addAccessTokenExpired(onAccessTokenExpired);
            document.addEventListener('visibilitychange', onPageResume);
            window.addEventListener('pageshow', onPageResume);
            return () => {
                userManager.events.removeAccessTokenExpiring(onAccessTokenExpiring);
                userManager.events.removeAccessTokenExpired(onAccessTokenExpired);
                document.removeEventListener('visibilitychange', onPageResume);
                window.removeEventListener('pageshow', onPageResume);
            };
        }
    }, [debug, logConsole, renewToken]);
    const signIn = isLoading
        ? undefined
        : () => {
              userManagerRef.current?.signinRedirect();
          };
    const signOut = isLoading
        ? undefined
        : () => {
              userManagerRef.current?.signoutRedirect();
          };
    const context = {
        isLoading,
        isReady: !isLoading,
        isAuthenticated,
        bearerTokenActive: true,
        getToken: () => tokenRef.current,
        getTokenParsed: () => tokenParsedRef.current,
        getUserId: () => tokenParsedRef.current?.['preferred_username'],
        getUserName: () => tokenParsedRef.current?.['name'],
        getUserEmail: () => tokenParsedRef.current?.['email'],
        signIn,
        signOut,
        refreshToken: renewToken,
        config,
    };
    const showChildren =
        !isLoading && !isAuthSilentRedirect && (!mandatory || (mandatory && isAuthenticated));
    return (
        <AuthContext.Provider value={context}>
            {showChildren ? children : null}
        </AuthContext.Provider>
    );
};
