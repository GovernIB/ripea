import axios from "axios";
import {useEffect} from "react";
import {useSession} from "./SessionStorageContext.tsx";
import {useResourceApiService} from "reactlib";

const userUrl :string = import.meta.env.VITE_API_URL + 'usuari';
const userkey :string = 'usuario';
const entitatKey = 'entitat';

export const useUserSession = () => {
    const {value, save, remove} = useSession(userkey)

    const refresh = () => {
        axios.get(userUrl+'/actual/securityInfo')
            .then((response) => {
                save(response.data);
            })
            .catch((error) => {
                save(undefined);
                console.log(">>>> axios error", error)
            })
    }

    const apiSave = (value:any) => {
        axios.post(userUrl+'/actual/changeInfo', value)
            .then((response) => {
                save(response.data);
            })
            .catch((error) => {
                save(undefined);
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

    useEffect(() => {
        if (!value) {
            save({});
            refresh()
        }
    }, []);

    return {
        value,
        refresh,
        save: apiSave,
        remove: apiRemove,
    };
}

export const useEntitatSession = () => {
    const { value, save, remove } = useSession(entitatKey)
    const { value: user } = useUserSession();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('entitatResource');

    const refresh = () => {
        if (apiIsReady){
            apiGetOne(user?.entitatActualId)
                .then((app) => save(app))
                .catch(() => remove())
        }
    }

    useEffect(()=>{
        if(user && user?.entitatActualId && user?.entitatActualId != value?.id){
            refresh()
        }
    },[user])

    useEffect(()=>{
        if(user?.entitatActualId && !value){
            refresh()
        }
    },[apiIsReady])

    // useEffect(()=>{
    //     console.log(">>>> entitat", value)
    // },[value])

    return { value }
}