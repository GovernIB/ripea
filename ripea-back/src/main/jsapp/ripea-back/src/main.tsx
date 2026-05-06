import React from 'react';
import ReactDOM from 'react-dom/client';
import dayjs from 'dayjs';
import duration from 'dayjs/plugin/duration';
import { BrowserRouter } from 'react-router-dom';
import { LicenseInfo } from '@mui/x-license';
import App from './App.tsx'
import {
    envVar,
    ResourceApiProvider
} from 'reactlib';
import {SessionStorageProvider} from "./components/SessionStorageContext.tsx";
import {RipeaAuthProvider} from "./components/RipeaAuthProvider.tsx";
import SseProvider from "./components/SseClient.tsx";
import {ThemeUserProvider} from "./components/ThemeUserProvider.tsx";

dayjs.extend(duration);

LicenseInfo.setLicenseKey('e0bde345c6cb2453171a44e15a0c58f5Tz0xMjQ4NTIsRT0xODAxMDk0Mzk5MDAwLFM9cHJvLExNPXN1YnNjcmlwdGlvbixQVj1pbml0aWFsLEtWPTI=');

export const envVars = {
    VITE_API_URL: import.meta.env.VITE_API_URL,
    VITE_API_PUBLIC_URL: import.meta.env.VITE_API_PUBLIC_URL,
    VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    VITE_API_SUFFIX: import.meta.env.VITE_API_SUFFIX,
    VITE_AUTH_KEYCLOAK_URL: import.meta.env.VITE_AUTH_KEYCLOAK_URL,
    VITE_AUTH_KEYCLOAK_REALM: import.meta.env.VITE_AUTH_KEYCLOAK_REALM,
    VITE_AUTH_KEYCLOAK_CLIENTID: import.meta.env.VITE_AUTH_KEYCLOAK_CLIENTID,
}

// const getAuthConfig = () => ({
//     url: envVar('VITE_AUTH_KEYCLOAK_URL', envVars),
//     realm: envVar('VITE_AUTH_KEYCLOAK_REALM', envVars),
//     clientId: envVar('VITE_AUTH_KEYCLOAK_CLIENTID', envVars),
// });

const toAbsoluteUrl = (url: string) =>
    url.startsWith('/') ? window.location.origin + url : url;

const getEnvApiUrl = () => {
    const envApiPublicUrl = envVar('VITE_API_PUBLIC_URL', envVars);
    const envApiUrl = envVar('VITE_API_URL', envVars);
    if (envApiPublicUrl || envApiUrl) {
        return toAbsoluteUrl(envApiPublicUrl ?? envApiUrl);
    } else {
        const envApiBaseUrl = envVar('VITE_API_BASE_URL', envVars);
        const envApiSuffix = envVar('VITE_API_SUFFIX', envVars) ?? '/api';
        if (envApiBaseUrl) {
            return toAbsoluteUrl(envApiBaseUrl + envApiSuffix);
        } else {
            return window.location.origin + envApiSuffix;
        }
    }
}

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        {/*<AuthProvider config={getAuthConfig()} mandatory everetAuthPatch>*/}
        <ResourceApiProvider apiUrl={getEnvApiUrl()} userSessionActive>
            <SessionStorageProvider>
                <RipeaAuthProvider>
                    <SseProvider>
                        <ThemeUserProvider>
                            {/*<CssBaseline />*/}
                            <BrowserRouter basename={import.meta.env.BASE_URL}>
                                <App />
                            </BrowserRouter>
                        </ThemeUserProvider>
                    </SseProvider>
                </RipeaAuthProvider>
            </SessionStorageProvider>
        </ResourceApiProvider>
        {/*</AuthProvider>*/}
    </React.StrictMode>,
);
