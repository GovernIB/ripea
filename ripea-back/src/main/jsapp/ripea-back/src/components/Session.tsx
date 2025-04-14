import axios from "axios";
import {useEffect} from "react";

const url :string = 'http://localhost:8080/ripeaback/api/usuari/actual/securityInfo';
const userKey :string = 'usuario';
const entitatKey :string = 'entitat';
const rolKey :string = 'rol';

const get = (key:string) => {
    const datosGuardados = sessionStorage.getItem(key);

    try {
        if (datosGuardados) {
            return JSON.parse(datosGuardados);
        }
    } catch {
    }
    return null
}

const save = (key:string, value:any) => {
    sessionStorage.setItem(key, JSON.stringify(value));
}

const remove = (key:string) => {
    sessionStorage.removeItem(key);
}

export const useSession = (key:string) => {
    return {
        value: get(key),
        save: (value:any) => save(key, value),
        remove: () => remove(key),
    }
}

export const useSessionUser = () => {
    const refresh = () => {
        axios.get(url)
            .then((response) => {
                save(userKey, response.data);
            })
            .catch((error) => {
                console.log(">>>> axios error", error)
            })
    }

    useEffect(() => {
        if (!get(userKey)) {
            refresh()
        }
    }, []);

    return {
        user: get(userKey),
        refresh,
        remove: () => remove(userKey),
    };
}
export const useSessionEntitat = () => useSession(entitatKey)
export const useSessionRol = () => useSession(rolKey)