import React, {useEffect, useRef} from 'react';
import {useSession} from './SessionStorageContext';

// Keys for session storage
const fluxCreateKey = 'flux_create';
const firmaFinalitzadaKey = 'firma_finalitzada';

export const useFluxCreateSessio = () => {
    const {value} = useSession(fluxCreateKey);
    return {value};
}
export const useFirmaFinalitzadaSessio = () => {
    const {value} = useSession(firmaFinalitzadaKey);
    return {value};
}

/**
 * Component que gestiona la connexió SSE amb el servidor
 * Rep esdeveniments en temps real i actualitza la sessió
 */
export const SseExpedient: React.FC<any> = (props:any) => {
    const {id} = props
    const eventSourceRef = useRef<EventSource | null>(null);

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
            eventSource.addEventListener('connect', (event) => {
                console.log('SSE connectat:', event.data);
                // saveConnected(true);
            });

            // EventListener
            addEventListener(eventSource, fluxCreateKey)
            addEventListener(eventSource, firmaFinalitzadaKey)

            // Gestionar errors
            eventSource.onerror = (error) => {
                console.error('Error de connexió SSE:', error);
                // saveConnected(false);

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
            console.log('Netejam o desmontam el component');
            if (eventSourceRef.current) {
                console.log('Desconnectam SSE');
                eventSourceRef.current.close();
                eventSourceRef.current = null;
            }
            // saveConnected(false);
        };
    }, []);

    // Aquest component no renderitza res visible
    return null;
};

const addEventListener = (eventSource: EventSource, key: any) => {
    eventSource.addEventListener(key, (event) => {
        try {
            const data = JSON.parse(event.data);
            console.log(`SSE '${key}' rebuts:`, data);
            // const { save } = useSession(key);
            // save(data)
        } catch (error) {
            console.error(`Error processant SSE: ${key}`, error);
        }
    });
}

export default SseExpedient;
