import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useEffect, useMemo, useState} from "react";
import {Tab, Tabs} from "@mui/material";
import Box from "@mui/material/Box";

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
    const [subValue, setSubValue] = useState<any>();

    const handleChange = (_event :any, newValue :string) : void => {
        const integ = integracions?.find?.((tab:any)=>tab?.codi==newValue)
        if (integ != null) {
            setValue(newValue);
            setSubValue(integ?.subConjunt?.[0]?.codi)
        }
    };

    useEffect(() => {
        if (apiIsReady && integracions?.length != 0 && value == null) {
            setValue(integracions?.[0].codi)
            setSubValue(integracions?.[0]?.subConjunt?.[0]?.codi)
        }
    }, [apiIsReady, integracions]);

    const tabElement = <>
        <Tabs
            value={value}
            onChange={handleChange}
            variant="scrollable"
            sx={{px: 1}}
        >
            {integracions?.map?.(({codi, nom}:any) =>
                <Tab value={codi} key={"tab-" + codi} label={nom || codi}/>
            )}
        </Tabs>

        {integracions?.map((integ) => (
            value == integ.codi && integ.subConjunt?.length > 0 &&
            <Box key={integ.codi}>
                {/* Aquí podrías poner otros Tabs si este item requiere subcategorías */}
                <Tabs value={subValue} onChange={(_e, v) => setSubValue(v)} variant="scrollable">
                    {integ.subConjunt?.map?.(({codi, nom}:any) =>
                        <Tab value={codi} key={"tab-" + codi} label={nom || codi} sx={{ fontSize: '14px' }}/>
                    )}
                </Tabs>
            </Box>
        ))}
    </>

    const integ = useMemo(() => (
        integracions
            ?.flatMap?.(i => [i, ...(i?.subConjunt || [])])
            ?.filter?.(i => i.codi != 'FIRMA')
    ), [integracions])

    return {
        apiIsReady,
        value: subValue ?? value,
        integracions: integ,
        tabElement,
    }
}
