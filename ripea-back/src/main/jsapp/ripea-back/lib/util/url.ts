export const toAbsolutePath = (relativePath: string, envBaseUrl: string = '/') => {
    const locationRelativePath =
        envBaseUrl.replace(/\/+$/, '') + '/' + relativePath.replace(/^\/+/, '');
    return window.location.origin + locationRelativePath;
};

export const isCurrentPathMatching = (path: string, withParams: boolean | undefined = false) => {
    if (withParams) {
        const currentPath =
            window.location.origin + window.location.pathname + window.location.search;
        // El servidor d'autorització sempre afegeix els seus propis paràmetres (code, state, session_state...) al
        // final de la query string del redirect_uri indicat, així que mai hi haurà una coincidència exacta amb la
        // URL original: només podem comprovar que hi comença.
        return currentPath === path || currentPath.startsWith(path + '&');
    } else {
        const currentPath = window.location.origin + window.location.pathname;
        return path === currentPath;
    }
};
