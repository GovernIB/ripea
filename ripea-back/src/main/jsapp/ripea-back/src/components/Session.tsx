import axios from "axios";
import {useEffect, useMemo} from "react";
import {useSession, useSessionContext} from "./SessionStorageContext.tsx";
import {useResourceApiService, useResourceApiContext} from "reactlib";

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
        return !(user?.conf?.modeFosc) ? value?.logoImgBytes : value?.blackLogoImgBytes
    }, [value?.logoImgBytes, value?.blackLogoImgBytes, user?.conf?.modeFosc]);
    const favicon = useMemo(() => {
        return !(user?.conf?.modeFosc) ? value?.faviconImgBytes : value?.blackFaviconImgBytes
    }, [value?.faviconImgBytes, value?.blackFaviconImgBytes, user?.conf?.modeFosc]);
    const menuicon = useMemo(() => {
        return !(user?.conf?.modeFosc) ? value?.menuImgBytes : value?.blackMenuImgBytes
    }, [value?.menuImgBytes, value?.blackMenuImgBytes, user?.conf?.modeFosc]);
    const colorFons = useMemo(() => {
        return (!(user?.conf?.modeFosc) ? value?.capsaleraColorFons : value?.blackCapsaleraColorFons) || "#FFFFFF"
    }, [value?.capsaleraColorFons, value?.blackCapsaleraColorFons, user?.conf?.modeFosc]);
    const colorLletra = useMemo(() => {
        return (!(user?.conf?.modeFosc) ? value?.capsaleraColorLletra : value?.blackCapsaleraColorLletra) || '#000'
    }, [value?.capsaleraColorLletra, value?.blackCapsaleraColorLletra, user?.conf?.modeFosc]);

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