import React from 'react';

/**
 * Què ofereix un `ScreenFrame` als components de dins (veure ScreenFrame.tsx). Va en un fitxer a part
 * perquè el fitxer del component només exporti components (react-refresh).
 */
export type ScreenFrameContextValue = {
    /**
     * Node de la barra de títol, al costat del títol, on pintar-hi informació breu de la pantalla
     * (p. ex. el nombre de filtres aplicats) amb `createPortal`. És `null` fins que es munta.
     */
    infoSlot: HTMLElement | null;
    /**
     * La barra d'eines de la graella es superposa a la dreta de la barra de títol (veure
     * StyledMuiGrid.tsx): n'indica l'amplada (px) perquè el títol no hi passi per sota.
     */
    setToolbarWidth: (width: number) => void;
};

export const ScreenFrameContext = React.createContext<ScreenFrameContextValue | undefined>(undefined);

/** Marc de pantalla en què es troba el component, o `undefined` si no n'hi ha cap. */
export const useScreenFrame = () => React.useContext(ScreenFrameContext);
