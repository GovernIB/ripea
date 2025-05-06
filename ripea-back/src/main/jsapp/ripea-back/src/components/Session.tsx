import axios from "axios";
import {useEffect, useMemo} from "react";
import {useSession} from "./SessionStorageContext.tsx";
import {useResourceApiService} from "reactlib";

const userUrl :string = import.meta.env.VITE_API_URL + 'usuari';
const userkey :string = 'usuario';
const entitatKey = 'entitat';
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
        if (user?.entitatActualId && user?.entitatActualId != value?.id) {
            refresh()
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
        if (user?.organActualId && user?.organActualId != value?.id && apiIsReady){
            apiGetOne(user?.organActualId)
                .then((app) => save(app))
                .catch(() => remove())
        }
    }

    useEffect(()=>{
        if (user && user?.organActualId) {
            refresh()
        } else {
            remove()
        }
    },[user])

    useEffect(()=>{
        if(!isInitialized()){
            save({});
            refresh()
        }
    },[apiIsReady])

    // useEffect(()=>{
    //     console.log(">>>> organ", value)
    // },[value])

    return { value, remove }
}