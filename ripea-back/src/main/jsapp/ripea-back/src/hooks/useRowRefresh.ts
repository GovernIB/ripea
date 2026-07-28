import React from 'react';
import { useResourceApiService } from 'reactlib';

type UseRowRefreshOptions = {
    perspectives?: string[];
    fallback?: () => void;
};

/**
 * Actualitza NOMÉS la fila afectada d'una graella: la torna a llegir del servidor amb getOne i
 * l'aplica amb updateRows, en lloc de refrescar tot el llistat.
 *
 * Es fa amb getOne, i no enviant els camps dins l'event SSE, perquè així la fila arriba sencera i
 * recalculada pel servidor: no cal duplicar enlloc quins camps pinta cada cel·la ni mantenir aquesta
 * còpia sincronitzada amb els afterConversion i les perspectives del recurs. Cal passar-hi les
 * mateixes perspectives que fa servir la graella perquè la fila tingui la mateixa forma que la resta.
 *
 * Sempre cau al fallback (típicament el refresc complet) si no es pot fer el parell getOne+updateRows:
 * val més una petició de més que deixar una cel·la mostrant informació antiga.
 */
const useRowRefresh = (
    resourceName: string,
    datagridApiRef: React.RefObject<any>,
    options?: UseRowRefreshOptions
) => {
    const { isReady, getOne } = useResourceApiService(resourceName);
    const { perspectives, fallback } = options ?? {};
    // El fallback es torna a crear a cada render del component que ens crida; el guardam a un ref
    // perquè la funció retornada no canviï d'identitat ni es quedi amb una versió antiga.
    const fallbackRef = React.useRef(fallback);
    fallbackRef.current = fallback;

    return React.useCallback(
        (id: any) => {
            const runFallback = () => fallbackRef.current?.();
            if (id == null || !isReady) {
                runFallback();
                return;
            }
            // Si la fila no està carregada a la graella, updateRows l'afegiria fora de l'ordenació i,
            // en mode arbre, sense agrupar-la correctament: en aquest cas refrescam el llistat sencer.
            if (datagridApiRef?.current?.getRow?.(id) == null) {
                runFallback();
                return;
            }
            getOne(id, perspectives ? { perspectives } : undefined)
                .then((row: any) => {
                    if (row == null) {
                        runFallback();
                        return;
                    }
                    datagridApiRef?.current?.updateRows?.([row]);
                })
                .catch(() => runFallback());
        },
        [isReady, getOne, datagridApiRef, perspectives]
    );
};

export default useRowRefresh;
