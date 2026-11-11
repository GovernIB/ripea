import React from 'react';

/**
 * Estat dels avisos minimitzats, compartit entre el banner (damunt del contingut) i els chips de la capçalera.
 * El proveïdor és a {@link ./AvisosProvider.tsx}.
 *
 * Els avisos arriben per SSE (veure {@link ./SseClient.tsx}); aquí només s'hi afegeix quins ha
 * minimitzat l'usuari. L'estat és en memòria i no es desa enlloc: en recarregar la pàgina (F5)
 * tots els avisos tornen a sortir desplegats. Un avís que deixa d'arribar per SSE s'oblida, i un
 * de nou sempre surt desplegat.
 */

/** Avís d'usuari tal com l'envia el servidor per SSE (`avisosUsuari`). */
export type AvisSse = {
    id: number;
    assumpte: string;
    missatge: string;
    avisNivell: string;
    /** Expedients als quals fa referència l'avís (enllaços dins el detall). */
    expedients?: { id: string; texte: string; codi: string }[];
};

export type AvisNivell = 'INFO' | 'WARNING' | 'ERROR';

export const AVIS_NIVELLS: AvisNivell[] = ['INFO', 'WARNING', 'ERROR'];

/** Nivell d'un avís; el que el servidor envia i no és cap dels tres es tracta com a informació. */
export const getAvisNivell = (avis: AvisSse): AvisNivell => AVIS_NIVELLS.find((nivell) => nivell === avis.avisNivell) ?? 'INFO';

export type AvisosState = {
    /** Avisos que s'han de pintar damunt del contingut. */
    visibles: AvisSse[];
    /** Total d'avisos actius d'un nivell (visibles i minimitzats). */
    total: (nivell: AvisNivell) => number;
    /** Avisos minimitzats d'un nivell. */
    minimitzats: (nivell: AvisNivell) => number;
    minimitza: (id: number) => void;
    /**
     * Restaura els avisos minimitzats del nivell a la seva posició. Mai en minimitza cap: per
     * minimitzar-los, l'usuari ha d'anar un a un amb la X del banner.
     */
    restaura: (nivell: AvisNivell) => void;
};

export const AvisosContext = React.createContext<AvisosState>({
    visibles: [],
    total: () => 0,
    minimitzats: () => 0,
    minimitza: () => {},
    restaura: () => {},
});

export const useAvisos = () => React.useContext(AvisosContext);
