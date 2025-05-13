import axios from "axios";
import {useEffect, useMemo} from "react";
import {useSession} from "./SessionStorageContext.tsx";
import {useResourceApiService} from "reactlib";

const userUrl :string = import.meta.env.VITE_API_URL + 'usuari';
const userkey :string = 'usuario';
const entitatKey = 'entitat';
const avisosKey = 'avisos';
const organKey = 'organ';

export const useUserSession = () => {
    axios.defaults.withCredentials = true;

    const {value, isInitialized, save, remove} = useSession(userkey)

    const refresh = () => {
        axios.get(userUrl+'/actual/securityInfo')
            .then((response) => {
                save(response.data);
            })
            .catch((error) => {
                save(null);
                console.log(">>>> axios error", error)
            })
    }

    const apiSave = (value:any) => {
        axios.post(userUrl+'/actual/changeInfo', value)
            .then((response) => {
                save(response.data);
            })
            .catch((error) => {
                save(null);
                console.log(">>>> axios error", error)
            })
    }

    const apiRemove = () => {
        // axios.delete(url)
        //     .then(() => {
        //         changeValue({});
        //     })
        //     .catch((error) => {
        //         console.log(">>>> axios error", error)
        //     })
        remove()
    }

    const permisos :any = useMemo(()=>{
        if (value && value?.permisosEntitat) {
            return Object.values(value?.permisosEntitat)?.find((e: any) => e?.entitatId == value?.entitatActualId)
        }
        return {}
    }, [value])

    useEffect(() => {
        if (!isInitialized()) {
            save({})
            refresh();
        }
    }, []);

    return {
        value,
        permisos,

        refresh,
        save: apiSave,
        remove: apiRemove,
    };
}

export const useAlertesSessio = () => {
    const { value, save } = useSession(avisosKey);
    // const { value: user } = useUserSession();
    //
    // Recuperar les alertes generals de l'aplicació.
    // No depenen de cap acció del usuari, s'han de consultar periòdicament.
    // const fetchAlerta = async () => {
    //     if (user) {
    //     axios.get(userUrl+'/syncStoredSessionData')
    //         .then((response) => {
    //             save(response.data);
    //         })
    //         .catch((error) => {
    //             console.error("Error al obtenir les alertes:", error);
    //         });
    //     }
    // };
    //
    // useEffect(() => {
    //     fetchAlerta(); // Cridada inicial
    //     const interval = setInterval(fetchAlerta, 10000); //Cada 10 segons refrescar info
    //     return () => clearInterval(interval);
    // }, []);

    useEffect(() => {}, []);

    return { value, save };
}

export const useEntitatSession = () => {
    const { value, isInitialized, save, remove } = useSession(entitatKey)
    const { value: user } = useUserSession();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('entitatResource');

    const refresh = () => {
        apiGetOne(user?.entitatActualId)
            .then((app) => save(app))
            .catch(() => remove())
    }

    useEffect(()=>{
        if (user && user?.entitatActualId) {
            if (user?.entitatActualId != value?.id) {
                refresh()
            }
        } else {
            remove()
        }
    },[user])

    useEffect(()=>{
        if(!isInitialized() && user?.entitatActualId && apiIsReady){
            save({});
            refresh()
        }
    },[apiIsReady])

    // useEffect(()=>{
    //     console.log(">>>> entitat", value)
    // },[value])

    return { value, remove }
}

export const useOrganSession = () => {
    const { value, isInitialized, save, remove } = useSession(organKey)
    const { value: user } = useUserSession();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('organGestorResource');

    const refresh = () => {
        apiGetOne(user?.organActualId)
            .then((app) => save(app))
            .catch(() => remove())
    }

    useEffect(()=>{
        if (user && user?.organActualId) {
            if (user?.organActualId != value?.id) {
                refresh()
            }
        } else {
            remove()
        }
    },[user])

    useEffect(()=>{
        if(!isInitialized() && user?.organActualId && apiIsReady){
            save({});
            refresh()
        }
    },[apiIsReady])

    // useEffect(()=>{
    //     console.log(">>>> organ", value)
    // },[value])

    return { value, remove }
}