import React, { useEffect, useRef, useState } from 'react';
import { useSession } from './SessionStorageContext';
import {useUserSession} from "./Session.tsx";

// Keys for session storage
const avisosKey = 'avisos';
const sseConnectedKey = 'sseConnected';

/**
 * Hook per a utilitzar el client SSE
 * @returns Estat de la connexió SSE
 */
export const useSseClient = () => {
  const { value: connected } = useSession(sseConnectedKey);
  return { connected };
};

/**
 * Component que gestiona la connexió SSE amb el servidor
 * Rep esdeveniments en temps real i actualitza la sessió
 */
export const SseClient: React.FC = () => {
  const eventSourceRef = useRef<EventSource | null>(null);
  const [isConnected, setIsConnected] = useState(false);
  const { save: saveConnected } = useSession(sseConnectedKey);
  const { save: saveAvisos } = useSession(avisosKey);
  const { value: user } = useUserSession();

  useEffect(() => {
    // Funció per a connectar amb el servidor SSE
    const connectToSSE = () => {
      // Tancar la connexió anterior si existeix
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }

      // Crear una nova connexió
      const apiUrl = import.meta.env.VITE_API_URL || '/api/';
      const sseUrl = `${apiUrl}sse/subscribe/user/`+user?.codi;

      const eventSource = new EventSource(sseUrl, { withCredentials: true });
      eventSourceRef.current = eventSource;

      // Gestionar l'esdeveniment de connexió
      eventSource.addEventListener('connect', (event) => {
        console.log('SSE connectat:', event.data);
        setIsConnected(true);
        saveConnected(true);
      });

      // // Gestionar l'esdeveniment de dades de sessió
      // eventSource.addEventListener('sessionData', (event) => {
      //   try {
      //     const sessionData = JSON.parse(event.data);
      //     console.log('SSE sessionData rebut:', sessionData);
      //     saveAvisos(sessionData);
      //   } catch (error) {
      //     console.error('Error processant dades de sessió SSE:', error);
      //   }
      // });

      // Gestionar l'esdeveniment d'alerta
      eventSource.addEventListener('avisos', (event) => {
        try {
          const avisosRebuts = JSON.parse(event.data);
          console.log('SSE avisos rebuts:', avisosRebuts);

          // Transformar l'objecte al format equivalent a AvisosActiusEvent
          const avisos = {
            avisosUsuari: avisosRebuts.avisosUsuari || [], // Llista d'objectes d'usuari
            avisosAdmin: avisosRebuts.avisosAdmin || {},   // Map d'alertes d'administrador
          };


          // Obtenir les alertes actuals de sessionStorage directament
          let prevAvisos;
          try {
            const storedData = sessionStorage.getItem(avisosKey);
            prevAvisos = storedData ? JSON.parse(storedData) : {};
          } catch (e) {
            prevAvisos = {};
          }

          // Actualitzar les alertes existents
          saveAvisos({
             ...prevAvisos,
             avisos: avisos.avisosUsuari,
             avisosAdmin: avisos.avisosAdmin
          });
        } catch (error) {
          console.error('Error processant avisos SSE:', error);
        }
      });

      // Gestionar errors
      eventSource.onerror = (error) => {
        console.error('Error de connexió SSE:', error);
        setIsConnected(false);
        saveConnected(false);

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
      setIsConnected(false);
      saveConnected(false);
    };
  }, []);

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
