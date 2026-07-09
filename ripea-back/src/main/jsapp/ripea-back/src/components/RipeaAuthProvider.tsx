import React from 'react';
import { AuthContext, useResourceApiContext } from 'reactlib';
import { useUserSession } from './Session';

export const RipeaAuthProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { value: user, remove: clearSession } = useUserSession();
    const { apiUrl } = useResourceApiContext();
    const isAuthenticated = user != null;

    const signOut = () => {
        const logoutUrl = apiUrl.replace(/\/api\/?$/, '/') + 'usuari/logout';
        clearSession?.();
        window.location.href = logoutUrl;
    };

    const context = {
        isLoading: !isAuthenticated,
        isReady: isAuthenticated,
        isAuthenticated,
        bearerTokenActive: false,
        getToken: () => undefined,
        getTokenParsed: () => user != null ? ({
            preferred_username: user.codi,
            name: user.nom,
            email: user.email,
        }) : undefined,
        getUserId: () => user?.codi ?? '',
        getUserName: () => user?.nom ?? '',
        getUserEmail: () => user?.email ?? '',
        signIn: undefined,
        signOut,
    };
    return <AuthContext.Provider value={context}>{children}</AuthContext.Provider>;
};
