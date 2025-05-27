import React, {createContext, useContext, useState, useEffect, useRef} from 'react';

type SessionData = Record<string, any>;

interface SessionStorageContextProps {
    data: SessionData;
    setValue: (key: string, value: any) => void;
    removeValue: (key: string) => void;
}

const SessionStorageContext = createContext<SessionStorageContextProps | undefined>(undefined);

export const SessionStorageProvider = ({ children }: { children: React.ReactNode }) => {
    const [data, setData] = useState<SessionData>(() => {
        const allKeys = Object.keys(sessionStorage);
        const initialData: SessionData = {};
        allKeys.forEach((key) => {
            try {
                initialData[key] = JSON.parse(sessionStorage.getItem(key)!);
            } catch {
                initialData[key] = sessionStorage.getItem(key);
            }
        });
        return initialData;
    });

    const setValue = (key: string, value: any) => {
        sessionStorage.setItem(key, JSON.stringify(value));
        setData((prev) => ({ ...prev, [key]: value }));
    };

    const removeValue = (key: string) => {
        sessionStorage.removeItem(key);
        setData((prev) => {
            const newData = { ...prev };
            delete newData[key];
            return newData;
        });
    };

    // Detectar cambios externos al sessionStorage (ej: otras pestañas)
    useEffect(() => {
        const handleStorage = (e: StorageEvent) => {
            if (e.storageArea === sessionStorage) {
                const newValue = e.newValue ? JSON.parse(e.newValue) : null;
                setData((prev) => ({ ...prev, [e.key!]: newValue }));
            }
        };
        window.addEventListener('storage', handleStorage);
        return () => window.removeEventListener('storage', handleStorage);
    }, []);

    return (
        <SessionStorageContext.Provider value={{ data, setValue, removeValue }}>
            {children}
        </SessionStorageContext.Provider>
    );
};

const useSessionStorage = () => {
    const ctx = useContext(SessionStorageContext);
    if (!ctx) throw new Error('useSessionStorage debe usarse dentro de <SessionStorageProvider>');
    return ctx;
};

let initialized :Map<string, boolean> = new Map();
export const useSession = (key:string) => {
    const {data, setValue, removeValue} = useSessionStorage();
    return {
        value: data[key],
        isInitialized: () => !!initialized.get(key) || !!data[key],
        save: (val:any) => {
            initialized.set(key, !!val);
            setValue(key, val)
        },
        remove: () => {
            initialized.set(key, false);
            removeValue(key)
        },
    };
};
export const useSessionList = (key:string) => {
    const { value: container, save, remove } = useSession(key);
    const containerRef = useRef(container ?? []);

    return {
        container,
        get: (key:string)=> container?.[key],
        save: (key:string, newValue:any) => {
            containerRef.current = {
                ...containerRef.current,
                [key]: newValue
            };
            save(containerRef.current)
        },
        remove: (key:string) => {
            containerRef.current = {
                ...containerRef.current,
                [key]: undefined
            };
            save(containerRef.current)
        },
        removeAll: remove
    }
}