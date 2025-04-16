import axios from "axios";
import {useEffect, useState} from "react";

const userUrl :string = import.meta.env.VITE_API_URL + 'usuari';
const userkey :string = 'usuario';

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
    const [value, setValue] = useState<any>(get(key));

    useEffect(() => {
        if (value) {
            save(key, value);
        }
    }, [value]);

    return {
        value,
        save: setValue,
        remove: () => remove(key),
    }
}

export const useSessionUser = () => {
    const {value, save} = useSession(userkey)

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