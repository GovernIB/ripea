import React, {useEffect, useRef} from 'react';
import {useSessionContext} from './SessionStorageContext';
import {useResourceApiContext} from "reactlib";

// Claus dels events SSE de l'expedient.
// IMPORTANT: són senyals de temps real (transitòries), NO estat persistent. Es guarden en memòria
// (useSessionContext) i no a sessionStorage: així cada event refresca els components una sola vegada i,
// en recarregar la pàgina, preval sempre el valor recalculat pel servidor (p.ex. expedient.errors d'AVISOS)
// en lloc d'un event SSE ranci que hauria sobreviscut a la recàrrega.
const sseExpedientKey = 'sseExpedient';
const fluxCreateKey = 'flux_creat';
const scanFinalitzatKey = 'scan_finalitzat';
const validacioChangeKey = 'validacio_change';
const firmaEstatKey = 'firma_estat';
const notificacioEstatKey = 'notificacio_estat';
const sseConnectedKey = 'exp_connect';

// Bus en memòria amb la mateixa API de llista (get/save/remove/removeAll) que abans donava useSessionList,
// però recolzat en el store en memòria (useSessionContext) per no persistir res a sessionStorage.
const useSseExpedientSession = () => {
    const { value: container, save, clear } = useSessionContext(sseExpedientKey);
    const containerRef = useRef<Record<string, any>>(container ?? {});
    containerRef.current = container ?? {};

    const listSave = (key: string, newValue: any) => {
        const next = { ...containerRef.current, [key]: newValue };
        containerRef.current = next;
        save(next);
    };

    return {
        container,
        get: (key: string) => container?.[key],
        save: listSave,
        remove: (key: string) => listSave(key, undefined),
        removeAll: () => {
            containerRef.current = {};
            clear();
        },
    };
};

const useTempSession = (key:string) => {
    const { get, remove } = useSseExpedientSession();
    const onChangeRef = useRef<((newValue?:any) => void) | null>(null);
    const value = get(key)

    useEffect(() => {
        if (value && !value?.processada){
            onChangeRef?.current?.(value);
            value.processada = true;
        }
    }, [value]);

    return {
        value,
        onChange: (callback: (newValue?:any) => void) => {
            onChangeRef.current = callback;
        },
        remove: () => remove(key),
    };
}

/**
 * Igual que useTempSession, però pensat per a events amb MÉS D'UN consumidor.
 *
 * useTempSession marca l'event com a processat mutant l'objecte compartit (value.processada), de manera
 * que només el primer component que reacciona el processa. Aquí, en canvi, cada consumidor recorda
 * localment quina instància d'event ja ha tractat, així tots hi reaccionen exactament una vegada.
 * Cada event SSE arriba d'un JSON.parse nou, per tant la comparació per identitat és suficient.
 */
const useSharedTempSession = (key: string) => {
    const { get } = useSseExpedientSession();
    const onChangeRef = useRef<((newValue?: any) => void) | null>(null);
    const processatRef = useRef<any>(null);
    const value = get(key);

    useEffect(() => {
        if (value && processatRef.current !== value) {
            processatRef.current = value;
            onChangeRef?.current?.(value);
        }
    }, [value]);

    return {
        value,
        onChange: (callback: (newValue?: any) => void) => {
            onChangeRef.current = callback;
        },
    };
}

export const useFluxCreateSession = () => useTempSession(fluxCreateKey);
export const useScanFinalitzatSession = () => useTempSession(scanFinalitzatKey);
export const useValidacioSession = () => useTempSession(validacioChangeKey);
// Canvis d'estat asíncrons (callback de portafirmes / NOTIB): els consumeixen les graelles per refrescar-se.
export const useFirmaEstatSession = () => useSharedTempSession(firmaEstatKey);
export const useNotificacioEstatSession = () => useSharedTempSession(notificacioEstatKey);

/**
 * Component que gestiona la connexió SSE amb el servidor
 * Rep esdeveniments en temps real i actualitza la sessió
 */
export const SseExpedient: React.FC<any> = (props:any) => {
    
    const {id} = props
    const eventSourceRef = useRef<EventSource | null>(null);
    const { save: saveSession, removeAll } = useSseExpedientSession();
    const { apiUrl } = useResourceApiContext();

    const addEventListener = (eventSource: EventSource, key: string) => {
        eventSource.addEventListener(key, (event) => {
            try {
                const data = JSON.parse(event.data);
                // console.log(`SSE '${key}' rebuts:`, data);
                saveSession(key, data)
            } catch (error) {
                console.error(`Error processant SSE: ${key}`, error);
            }
        });
    }

    useEffect(() => {
        // Funció per a connectar amb el servidor SSE
        const connectToSSE = () => {
            // Tancar la connexió anterior si existeix
            if (eventSourceRef.current) {
                eventSourceRef.current.close();
            }

            // Crear una nova connexió
            const sseUrl = `${apiUrl}sse/subscribe/exp/${id}`;

            const eventSource = new EventSource(sseUrl, {withCredentials: true});
            eventSourceRef.current = eventSource;

            // Gestionar l'esdeveniment de connexió
            eventSource.addEventListener(sseConnectedKey, () => {
                // console.log('SSE connectat:', event.data);
                saveSession(sseConnectedKey, true)
            });

            // Gestionar l'esdeveniment de flux creat
            addEventListener(eventSource, fluxCreateKey)

			// Gestionar l'esdeveniment de scan finalitzat
			addEventListener(eventSource, scanFinalitzatKey)

			// Gestionar l'esdeveniment de validació d'expedient
			addEventListener(eventSource, validacioChangeKey)

			// Gestionar els canvis d'estat de firma i de notificació dels documents de l'expedient
			addEventListener(eventSource, firmaEstatKey)
			addEventListener(eventSource, notificacioEstatKey)

            // Gestionar errors
            eventSource.onerror = (error) => {
                console.error('Error de connexió SSE:', error);
                saveSession(sseConnectedKey, false);

                // Tancar la connexió actual
                eventSource.close();
                eventSourceRef.current = null;

                // Intentar reconnectar després d'un temps
                setTimeout(connectToSSE, 5000);
            };
        };

        // Iniciar la connexió
        connectToSSE();

        // Netejar en desmuntar el component
        return () => {
            // console.log('Netejam o desmontam el component');
            if (eventSourceRef.current) {
                // console.log('Desconnectam SSE');
                eventSourceRef.current.close();
                eventSourceRef.current = null;
            }
            removeAll()
            saveSession(sseConnectedKey, false);
        };
    }, []);

    // Aquest component no renderitza res visible
    return null;
};

export default SseExpedient;
