import React, { useEffect, useRef } from 'react';
import {useSessionList} from './SessionStorageContext';
import {useUserSession} from "./Session.tsx";
import {useResourceApiContext} from "reactlib";

// Keys for session storage
const sseClientKey = 'sseClient';
const avisosKey = 'avisos';
const notificacionsKey = 'notificacions';
const tasquesKey = 'tasques';
const sseFirmaMassivaKey = 'firma_massiva';
const sseConnectedKey = 'user_connect';

const useSseClientSession = () => useSessionList(sseClientKey)
const useTempSession = (key:string) => {
    const { get, remove } = useSseClientSession();
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
 * Hook per a utilitzar el client SSE
 * @returns Estat de la connexió SSE
 */
export const useSseClient = () => {
  const { get } = useSseClientSession();
  return { connected: get(sseConnectedKey) };
};
export const useAlertesSession = () => {
    const { get } = useSseClientSession();
    return { value: get(avisosKey) };
}
export const useNotificacionsSession = () => {
    const { get } = useSseClientSession();
    return { value: get(notificacionsKey) };
}
export const useTasquesSession = () => {
    const { get } = useSseClientSession();
    return { value: get(tasquesKey) };
}
export const useFirmaMassivaSession = () => useTempSession(sseFirmaMassivaKey);

/**
 * Component que gestiona la connexió SSE amb el servidor
 * Rep esdeveniments en temps real i actualitza la sessió
 */
export const SseClient: React.FC = () => {
    
  const eventSourceRef = useRef<EventSource | null>(null);
  const { get, save: saveSession, removeAll } = useSseClientSession();
  const { value: user } = useUserSession();
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
      if(user && user?.codi) {
          const alreadyConnected = get(sseConnectedKey);
          if (alreadyConnected) return;

          // Funció per a connectar amb el servidor SSE
          const connectToSSE = () => {
              // Tancar la connexió anterior si existeix
              if (eventSourceRef.current) {
                  eventSourceRef.current.close();
              }

              const sseUrl = `${apiUrl}sse/subscribe/user/${user?.codi}`;

              const eventSource = new EventSource(sseUrl, {withCredentials: true});
              eventSourceRef.current = eventSource;

              // Gestionar l'esdeveniment de connexió
              eventSource.addEventListener(sseConnectedKey, (event) => {
                  console.log('SSE connectat:', event.data);
                  saveSession(sseConnectedKey, true)
              });

              // Gestionar l'esdeveniment d'alerta
              addEventListener(eventSource, avisosKey)

              // Gestionar l'esdeveniment de notificació
              addEventListener(eventSource, notificacionsKey)

              // Gestionar l'esdeveniment de tasques
              addEventListener(eventSource, tasquesKey)
			  
			  // Gestionar l'esdeveniment de finma massiva finalitzada
			  addEventListener(eventSource, sseFirmaMassivaKey)

              // Gestionar errors
              eventSource.onerror = (error) => {
                  console.error('Error de connexió SSE:', error);
                  saveSession(sseConnectedKey, false)

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
              removeAll()
              saveSession(sseConnectedKey, false)
          };
      }
  }, [user?.codi]);

  // Aquest component no renderitza res visible
  return null;
};

/**
 * Component d'ordre superior per a proporcionar la connexió SSE a l'aplicació
 */
export const SseProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <>
      <SseClient />
      {children}
    </>
  );
};

export default SseProvider;
