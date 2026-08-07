/**
 * Obre una ruta de l'aplicació en una pestanya nova.
 *
 * El navigate() de react-router sempre navega dins la pestanya actual, així que cal fer-ho amb
 * window.open i una URL completa: la ruta es prefixa amb el basename del router
 * (import.meta.env.BASE_URL, p.ex. "/ripeaback/reactapp/"), que és el que hi afegiria
 * react-router pel seu compte.
 */
export const openRouteInNewTab = (route: string) => {
    const base = import.meta.env.BASE_URL ?? '/';
    const url = (base.endsWith('/') ? base : base + '/') + route.replace(/^\//, '');
    window.open(url, '_blank', 'noopener,noreferrer');
};
