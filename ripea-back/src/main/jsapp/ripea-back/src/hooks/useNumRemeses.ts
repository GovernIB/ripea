import React from 'react';
import { useResourceApiService } from 'reactlib';
import { useSession } from '@src/components/SessionStorageContext.tsx';
import * as builder from '@src/util/springFilterUtils.ts';

/**
 * Nombre de remeses de l'expedient que mostra la pestanya "Remeses" del seu detall.
 *
 * Es guarda a la sessió, com el nombre d'interessats, perquè les accions que creen remeses des d'un altre
 * lloc (notificar un document des de la graella de contingut) l'actualitzin sense recarregar la pàgina.
 * L'inicialitza el detall de l'expedient (Expedient.tsx) i l'esborra en sortir-ne: si no hi és (p. ex. al
 * detall d'una tasca, que no té pestanya de remeses) recomptar no fa res.
 *
 * recomptar demana el total al servidor en lloc d'incrementar el valor: una notificació crea una remesa
 * per interessat i el resultat de l'acció no diu quantes se n'han creat.
 */
const useNumRemeses = (expedientId: any) => {
    const { value, save, remove } = useSession(`expedient-${expedientId}-numRemeses`);
    const { isReady, find } = useResourceApiService('documentNotificacioResource');

    // recomptar es crida des del callback d'èxit d'un diàleg, que pot ser d'un render anterior: llegim
    // sempre l'estat més recent per no comptar amb un valor o una api que ja no són els actuals.
    const latestRef = React.useRef({ expedientId, value, save, isReady, find });
    latestRef.current = { expedientId, value, save, isReady, find };

    const recomptar = React.useCallback(() => {
        const { expedientId, value, save, isReady, find } = latestRef.current;
        if (!isReady || expedientId == null || value == null) {
            return;
        }
        find({ filter: builder.eq('expedient.id', expedientId), page: 0, size: 1 })
            .then((result: any) => save(result?.page?.totalElements ?? 0))
            .catch(() => {
                // El comptador queda com estava; la pestanya el corregeix quan es torna a carregar.
            });
    }, []);

    return { value, save, remove, recomptar };
};

export default useNumRemeses;
