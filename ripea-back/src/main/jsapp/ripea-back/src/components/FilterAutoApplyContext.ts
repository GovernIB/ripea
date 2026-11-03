import React from 'react';

/**
 * Indica als camps d'un filtre que aquest s'aplica sol (FilterAutoApply): en lloc de gestionar-ne el
 * retard cada camp, el gestiona el filtre sencer. Va en un fitxer a part perquè el fitxer del component
 * només exporti components (react-refresh).
 */
export const FilterAutoApplyContext = React.createContext(false);

/** `true` si el component és dins un filtre amb aplicació automàtica. */
export const useFilterAutoApply = () => React.useContext(FilterAutoApplyContext);

/**
 * Tipus de camp que l'usuari escriu tecla a tecla: el filtre n'espera el final abans d'aplicar-se. La
 * resta (desplegables, booleans, referències...) són una sola acció i s'apliquen de seguida.
 */
export const TYPING_FIELD_TYPES = ['text', 'string', 'textarea', 'url', 'number', 'integer', 'decimal', 'email'];

/** Un camp sense tipus conegut es tracta com a text. */
export const isTypingFieldType = (type?: string) => TYPING_FIELD_TYPES.includes(type ?? 'text');

/** Camp del formulari que només commuta la cerca avançada (no filtra res: no compta com a canvi del filtre). */
const ADVANCED_SEARCH_FIELD = 'advanced';

/**
 * Forma canònica de les dades d'un filtre per comparar-les: sense camps buits (`undefined`, `null`,
 * text buit o llista buida), sense el commutador de cerca avançada i amb els camps ordenats per nom.
 * Un camp esborrat i un camp que no s'ha tocat són el mateix filtre, i commutar entre cerca bàsica i
 * avançada no en canvia el resultat.
 */
export const normalizeFilterData = (data?: Record<string, unknown>): string =>
    JSON.stringify(
        Object.entries(data ?? {})
            .filter(
                ([name, value]) =>
                    name !== ADVANCED_SEARCH_FIELD &&
                    value != null &&
                    value !== '' &&
                    !(Array.isArray(value) && value.length === 0)
            )
            .sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0))
    );
