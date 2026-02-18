import React, {useEffect, useRef} from 'react';
import {useSessionList} from './SessionStorageContext';
import {useResourceApiContext} from "reactlib";

// Keys for session storage
const sseExpedientKey = 'sseExpedient';
const fluxCreateKey = 'flux_creat';
const scanFinalitzatKey = 'scan_finalitzat';
const validacioChangeKey = 'validacio_change';
const sseConnectedKey = 'exp_connect';

const useSseExpedientSession = () => useSessionList(sseExpedientKey)

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

export const useFluxCreateSession = () => useTempSession(fluxCreateKey);
export const useScanFinalitzatSession = () => useTempSession(scanFinalitzatKey);
export const useValidacioSession = () => useTempSession(validacioChangeKey);

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

			// Gestionar l'esdeveniment de firma finalitzada
			addEventListener(eventSource, scanFinalitzatKey)

			// Gestionar l'esdeveniment de validació d'expedient
			addEventListener(eventSource, validacioChangeKey)

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
