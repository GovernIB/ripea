import React from 'react';

export type AuthContextType = {
    isLoading: boolean,
    isReady: boolean,
    isAuthenticated: boolean,
    getToken: () => string | undefined,
    getTokenParsed: () => any | undefined,
    getUserId: () => any | string,
    getUserName: () => any | string,
    getUserEmail: () => any | string,
    signIn: (() => void) | undefined,
    signOut: (() => void) | undefined,
}

export const AuthContext = React.createContext<AuthContextType | undefined>(undefined);

export const useAuthContext = (): AuthContextType => {
    const context = React.useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuthContext must be used within a AuthProvider');
    }
    return context;
}

export const useOptionalAuthContext = (): AuthContextType | undefined => {
    return React.useContext(AuthContext);
}

export default AuthContext;