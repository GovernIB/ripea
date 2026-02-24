import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useEffect, useState} from "react";
import {Tab, Tabs} from "@mui/material";

const useIntegracions = () => {
    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
    } = useResourceApiService('integracioResource');
    const {temporalMessageShow} = useBaseAppContext();
    const [integracions, setIntegracions] = useState<any[]>();

    const get = () => {
        if (apiIsReady) {
            apiAction(undefined, {code: 'INTEGRACIONS_LIST'})
                .then((response: any) => {
                    setIntegracions(response)
                })
                .catch((error) => {
                    setIntegracions(undefined)
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    useEffect(() => {
        if (apiIsReady)
            get();
    }, [apiIsReady]);

    return {
        apiIsReady,
        integracions,
        refresh: get
    }
}

export const useIntegracioTab = () => {
    const {apiIsReady, integracions} = useIntegracions();

    const [value, setValue] = useState<any>();
    const handleChange = (_event :any, newValue :string) : void => {
        if (integracions?.some?.((tab:any)=>tab?.codi==newValue)) {
            setValue(newValue);
        }
    };

    useEffect(() => {
        if (apiIsReady && integracions?.length != 0 && value == null) {
            setValue(integracions?.[0].codi)
        }
    }, [apiIsReady, integracions]);

    const tabElement =
        <Tabs
            value={value}
            onChange={handleChange}
            variant="scrollable"
            sx={{px: 1}}
        >
            {integracions?.map?.(({codi, nom}:any) => {
                return <Tab value={codi} key={"tab-" + codi} label={nom || codi}/>
            })}
        </Tabs>

    return {
        apiIsReady,
        value,
        integracions,
        tabElement,
    }
}