import React from 'react';
import { AuthContext } from 'reactlib';
import { useUserSession } from './Session';

export const RipeaAuthProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { value: user } = useUserSession();
    const isAuthenticated = user != null;
    const context = {
        isLoading: false,
        isReady: true,
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
        signOut: undefined,
    };
    return <AuthContext.Provider value={context}>{children}</AuthContext.Provider>;
};
