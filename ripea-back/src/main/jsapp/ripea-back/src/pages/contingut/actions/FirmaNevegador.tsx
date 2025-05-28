import {useEffect, useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {useFirmaFinalitzadaSession} from "../../../components/SseExpedient.tsx";
import Iframe from "../../../components/Iframe.tsx";

const FirmaNevegadorForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiu"/>
    </Grid>
}

const FirmaNevegador = (props: any) => {
    const {} = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        title={'Firmar desde el navegador'}
        action={"FIRMA_WEB_INI"}
        initialOnChange
        {...props}
    >
        <FirmaNevegadorForm/>
    </FormActionDialog>
}

export const useFirmaNevegador = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
    const { value: firma } = useFirmaFinalitzadaSession();

    useEffect(() => {
        if (firma) {
            const severiry =
                firma?.status == 'OK' ? 'success'
                    : firma?.status == 'WARNING' ? 'warning'
                        : firma?.status == 'ERROR' ? 'error'
                            : 'info'

            apiRef?.current?.close();
            refresh?.()
            temporalMessageShow(null, firma?.msg, severiry);
        }
    }, [firma]);

    const handleShow = (id: any): void => {
        apiRef.current?.show?.(id)
    }
    const formDialogResultProcessor = (result: any) => {
        return <Iframe src={result?.url}/>
    }
    const onError = (error: any): void => {
        temporalMessageShow(null, error.message, 'error');
    }

    return {
        handleShow,
        content: <FirmaNevegador apiRef={apiRef}
                                 formDialogResultProcessor={formDialogResultProcessor}
                                 onError={onError}/>
    }
}
export default useFirmaNevegador;