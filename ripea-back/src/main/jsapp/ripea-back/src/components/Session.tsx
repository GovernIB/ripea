import axios from "axios";
import {useEffect, useMemo} from "react";
import {useSession, useSessionContext} from "./SessionStorageContext.tsx";
import {useResourceApiService, useResourceApiContext} from "reactlib";
import { useTheme } from "@mui/material";

const userkey :string = 'usuario';
const entitatKey = 'entitat';
const organKey = 'organ';

export enum rols {
    SUPER = 'IPA_SUPER',
    ADMIN = 'IPA_ADMIN',
    ADMIN_LECTURA = 'IPA_ADMIN_LECTURA',
    ORGAN_ADMIN = 'IPA_ORGAN_ADMIN',
    DISSENY = 'IPA_DISSENY',
    REVISIO = 'IPA_REVISIO',
    tothom = 'tothom',
}

let alreadyRequested = false;
export const useUserSession = () => {
    axios.defaults.withCredentials = true;
    const { apiUrl } = useResourceApiContext();

    const { value, isInitialized, save, clear } = useSessionContext(userkey);

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

    const apiSave = (value:any, navigate?:any) => {
        axios.post(apiUrl + 'usuari/actual/changeInfo', value)
            .then((response) => {
                save(response.data);
                navigate?.('/')
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

    const rol = useMemo(() => ({
        isSupAdmin: value?.rolActual == rols.SUPER,
        isAdmin: value?.rolActual == rols.ADMIN,
        isAdminLectura: value?.rolActual == rols.ADMIN_LECTURA,
        isOrganAdmin: value?.rolActual == rols.ORGAN_ADMIN,
        isDissenyOrgan: value?.rolActual == rols.DISSENY,
        isRevisor: value?.rolActual == rols.REVISIO,
        isUser: value?.rolActual == rols.tothom,
    }), [value])

    return {
        value,
        // Fals mentre la crida a securityInfo està en curs. Qui decideixi sobre rols o
        // permisos ha d'esperar-lo: abans, rolActual és undefined i qualsevol comprovació
        // donaria un "no autoritzat" fals.
        isLoaded: isInitialized,
        rol,
        permisos,

        refresh,
        save: apiSave,
        remove: apiRemove,
    };
}

export const useEntitatSession = () => {
    const { value, isInitialized, save, remove } = useSession(entitatKey)
    const { value: user } = useUserSession();
    const theme = useTheme();
    const isDark = theme.palette.mode === 'dark';

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

    const logo = useMemo(() => {
        return !isDark ? value?.logoImgBytes : value?.blackLogoImgBytes
    }, [value?.logoImgBytes, value?.blackLogoImgBytes, isDark]);

    const favicon = useMemo(() => {
        return !isDark ? value?.faviconImgBytes : value?.blackFaviconImgBytes
    }, [value?.faviconImgBytes, value?.blackFaviconImgBytes, isDark]);

    const menuicon = useMemo(() => {
        return !isDark ? value?.menuImgBytes : value?.blackMenuImgBytes
    }, [value?.menuImgBytes, value?.blackMenuImgBytes, isDark]);

    const colorFons = useMemo(() => {
        return (!isDark ? value?.capsaleraColorFons : value?.blackCapsaleraColorFons) || "#FFFFFF"
    }, [value?.capsaleraColorFons, value?.blackCapsaleraColorFons, isDark]);

    const colorLletra = useMemo(() => {
        return (!isDark ? value?.capsaleraColorLletra : value?.blackCapsaleraColorLletra) || '#000'
    }, [value?.capsaleraColorLletra, value?.blackCapsaleraColorLletra, isDark]);

    return { value : {...value, conf: {logo, favicon, menuicon, colorFons, colorLletra}}, remove }
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

    return { value, remove }
}
