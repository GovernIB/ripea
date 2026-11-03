import React from 'react';
import { useFilterApiContext, useFormContext } from 'reactlib';
import { isTypingFieldType, normalizeFilterData } from './FilterAutoApplyContext';

/** Retard (ms) des que l'usuari deixa d'escriure en un camp de text fins que s'aplica el filtre. */
export const FILTER_AUTO_APPLY_DELAY = 1000;

/** Dades del formulari del filtre: valors per nom de camp. */
type FilterData = Record<string, unknown>;

type FilterAutoApplyProps = {
    /**
     * Dades normalitzades (veure normalizeFilterData) de l'últim filtre aplicat (`undefined` = encara
     * cap, equival al filtre buit). El manté el StyledMuiFilter des de tots els punts per on passa una
     * aplicació, siguin o no d'aquest component. Es comparen les dades del formulari i no el Spring
     * Filter perquè alguns `springFilterBuilder` tenen efectes secundaris (named queries)
     * i no es poden cridar només per comparar.
     */
    lastAppliedRef: React.RefObject<string | undefined>;
    /** Perquè el pare pugui cancel·lar l'aplicació pendent (p. ex. en netejar el filtre). */
    cancelRef: React.RefObject<(() => void) | undefined>;
};

const isDateType = (type?: string) => type === 'date' || type === 'datetime-local' || type === 'datetime';

const isValidDateValue = (value: unknown): boolean => {
    if (value == null || value === '') {
        // Esborrar una data també és un canvi vàlid
        return true;
    }
    if (typeof (value as { isValid?: unknown }).isValid === 'function') {
        return (value as { isValid: () => boolean }).isValid();
    }
    return !Number.isNaN(new Date(value as string | number | Date).getTime());
};

const sameValue = (a: unknown, b: unknown) => JSON.stringify(a ?? null) === JSON.stringify(b ?? null);

/**
 * Aplica el filtre sol quan l'usuari en canvia els camps, sense botó «Filtra»:
 *  - camps de text, number...: 1 s després de l'últim caràcter escrit;
 *  - desplegables, booleans, referències: immediatament;
 *  - dates: immediatament, però només si el valor és vàlid (o s'ha esborrat);
 *  - Tab: aplica el canvi pendent sense esperar;
 *  - Intro: ja ho fa la llibreria (`filterOnFieldEnterKeyPressed`); aquí només es descarta l'espera.
 *
 * No pinta res: ha d'anar com a fill del filtre (dins `MuiFilter`) per poder fer servir els seus
 * contextos. No aplica si les dades del filtre són iguals que les de l'últim aplicat, així que no
 * duplica la cerca inicial, la neteja ni la restauració des de la sessió.
 */
export const FilterAutoApply: React.FC<FilterAutoApplyProps> = ({ lastAppliedRef, cancelRef }) => {
    const { data, fields } = useFormContext();
    const filterApiRef = useFilterApiContext();
    const previousDataRef = React.useRef<FilterData | undefined>(data);
    const timerRef = React.useRef<ReturnType<typeof setTimeout> | undefined>(undefined);

    const cancel = React.useCallback(() => {
        if (timerRef.current !== undefined) {
            clearTimeout(timerRef.current);
            timerRef.current = undefined;
        }
    }, []);

    // Aplica el filtre ara, si realment ha canviat respecte de l'últim aplicat.
    const apply = React.useCallback(() => {
        cancel();
        const api = filterApiRef.current;
        const current: FilterData = api?.getData?.() ?? {};
        if (normalizeFilterData(current) !== (lastAppliedRef.current ?? normalizeFilterData(undefined))) {
            api?.filter?.();
        }
    }, [cancel, filterApiRef, lastAppliedRef]);

    React.useEffect(() => {
        cancelRef.current = cancel;
        return () => {
            cancel();
            cancelRef.current = undefined;
        };
    }, [cancel, cancelRef]);

    // Detecta quins camps han canviat i en decideix el retard.
    React.useEffect(() => {
        const previous = previousDataRef.current ?? {};
        previousDataRef.current = data;
        const current = data ?? {};
        const changed = Object.keys({ ...previous, ...current }).filter((name) => !sameValue(previous[name], current[name]));
        if (changed.length === 0) {
            return;
        }
        const typeOf = (name: string) =>
            (fields as { name?: string; type?: string }[] | undefined)?.find((f) => f?.name === name)?.type;
        const invalidDate = changed.some((name) => isDateType(typeOf(name)) && !isValidDateValue(current[name]));
        if (invalidDate) {
            // Data a mig escriure: s'espera que sigui vàlida.
            return;
        }
        const typing = changed.some((name) => isTypingFieldType(typeOf(name)));
        cancel();
        // Fins i tot l'aplicació «immediata» passa per un temporitzador de 0 ms: deixa que
        // s'acabin d'executar els efectes del filtre (cerca inicial, neteja) abans de comparar.
        timerRef.current = setTimeout(apply, typing ? FILTER_AUTO_APPLY_DELAY : 0);
    }, [data]); // eslint-disable-line react-hooks/exhaustive-deps

    // Tab: s'aplica de seguida el que hi hagi pendent. S'escolta al document (en captura) perquè
    // aquest component no pinta cap element on enganxar-hi l'escoltador.
    React.useEffect(() => {
        const onKeyDown = (event: KeyboardEvent) => {
            if (event.key === 'Tab' && timerRef.current !== undefined) {
                apply();
            } else if (event.key === 'Enter' && timerRef.current !== undefined) {
                // La llibreria ja aplica amb Intro: només es descarta l'espera.
                cancel();
            }
        };
        document.addEventListener('keydown', onKeyDown, true);
        return () => document.removeEventListener('keydown', onKeyDown, true);
    }, [apply, cancel]);

    return null;
};

export default FilterAutoApply;
