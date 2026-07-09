import React, { createContext, useContext } from 'react';
import { useBaseAppContext } from 'reactlib';

export const TemporalMessageBridge = () => {
    const { temporalMessageShow } = useBaseAppContext();
    const { setTemporalMessageShowImpl } = usePreBaseAppContext();

    React.useEffect(() => {
        setTemporalMessageShowImpl(temporalMessageShow);
    }, [temporalMessageShow, setTemporalMessageShowImpl]);

    return null;
};

/**
 * Context "pre-app" que exposa certes funcionalitats de BaseAppContext
 * abans que aquest estigui disponible a l'arbre de components.
 *
 * Algunes opcions del menú (menuEntries) fan servir hooks (com useSistemAction) que necessiten cridar 
 * `temporalMessageShow` però s'inicialitzen ABANS que el BaseAppContext (de reactlib) existeixi.
 *
 * Aquest provider exposa una funció ESTABLE (temporalMessageShow) des del principi.
 * Internament, aquesta funció delega cap a una ref mutable (temporalMessageShowRef).
 * Un component bridge (TemporalMessageBridge), que viu DINS del BaseAppContext,
 * actualitza la ref amb la implementació real quan el BaseAppContext ja existeix.
 */
const PreBaseAppContext = createContext<any>(null);

export const PreBaseAppProvider = ({ children }: { children: React.ReactNode }) => {

    /**
     * Ref mutable que apunta a la implementació activa de temporalMessageShow.
     */
    const temporalMessageShowRef = React.useRef<(title: any, message: any, severity?: string) => void>(
        (title, message) => console.error(message)
    );

    /** Funció ESTABLE exposada als consumidors d'aquest context. */
    const temporalMessageShow = React.useCallback((title: any, message: any, severity?: string) => {
        temporalMessageShowRef.current(title, message, severity);
    }, []);

    /**
     * Funció que permet registrar la implementació real de temporalMessageShow.
     */
    const setTemporalMessageShowImpl = React.useCallback((fn: (title: any, message: any, severity?: string) => void) => {
        temporalMessageShowRef.current = fn;
    }, []);


    const value = {
        temporalMessageShow,
        setTemporalMessageShowImpl,
    };

    return (
        <PreBaseAppContext.Provider value={value}>
            {children}
        </PreBaseAppContext.Provider>
    );
};

export const usePreBaseAppContext = () => useContext(PreBaseAppContext);