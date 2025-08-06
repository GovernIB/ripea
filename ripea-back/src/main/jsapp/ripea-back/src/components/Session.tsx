import axios from "axios";
import {useEffect, useMemo} from "react";
import {useSession, useSessionContext} from "./SessionStorageContext.tsx";
import {useResourceApiService, useResourceApiContext} from "reactlib";

const userkey :string = 'usuario';
const entitatKey = 'entitat';
const organKey = 'organ';

let alreadyRequested = false;
export const useUserSession = () => {
    axios.defaults.withCredentials = true;
    const { apiUrl } = useResourceApiContext();

    const { value, save, clear } = useSessionContext(userkey);

    const refresh = () => {
        axios.get(apiUrl + 'usuari/actual/securityInfo')
            .then((response) => {
                save(response.data);
            })
            .catch((error) => {
                save(null);
                console.log(">>>> axios error", error)
            })
    }

    const apiSave = (value:any) => {
        axios.post(apiUrl + 'usuari/actual/changeInfo', value)
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
        clear()
    }

    const permisos :any = useMemo(()=>{
        if (value && value?.permisosEntitat) {
            return Object.values(value?.permisosEntitat)?.find((e: any) => e?.entitatId == value?.entitatActualId)
        }
        return {}
    }, [value])

    useEffect(() => {
        if (!value && !alreadyRequested) {
            alreadyRequested = true;
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