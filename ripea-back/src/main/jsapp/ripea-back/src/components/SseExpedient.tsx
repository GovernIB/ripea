import React, {useEffect, useRef} from 'react';
import {useSessionList} from './SessionStorageContext';

// Keys for session storage
const sseExpedientKey = 'sseExpedient';
const fluxCreateKey = 'flux_creat';
const firmaFinalitzadaKey = 'firma_finalitzada';
const scanFinalitzatKey = 'scan_finalitzat';
const sseConnectedKey = 'exp_connect';

const useSseExpedientSession = () => useSessionList(sseExpedientKey)

const useTempSession = (key:string) => {
    const { get, remove } = useSseExpedientSession();
    //const value = get(key)
    
    /*
    useEffect(() => {
        if (value && !value.processada){
            //const newValue = { ...value, procesada: true };
            //save(key, newValue); //Bucle infinito en useEffect de FirmaNavegador.tsx
            //value.processada = true; //No es processa en el useEffect de FirmaNavegador.tsx
        }
    }, [value]);
    */

    return {
        value: get(key),
        remove: () => remove(key)
    };
}

export const useFluxCreateSession = () => useTempSession(fluxCreateKey);
export const useFirmaFinalitzadaSession = () => useTempSession(firmaFinalitzadaKey);
export const useScanFinalitzatSession = () => useTempSession(scanFinalitzatKey);

/**
 * Component que gestiona la connexió SSE amb el servidor
 * Rep esdeveniments en temps real i actualitza la sessió
 */
export const SseExpedient: React.FC<any> = (props:any) => {
    const {id} = props
    const eventSourceRef = useRef<EventSource | null>(null);
    const { save: saveSession, removeAll } = useSseExpedientSession();
    // saveSession("id", id)

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
            const apiUrl = import.meta.env.VITE_API_URL || '/api/';
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
            addEventListener(eventSource, firmaFinalitzadaKey)

			// Gestionar l'esdeveniment de firma finalitzada
			addEventListener(eventSource, scanFinalitzatKey)
			
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
