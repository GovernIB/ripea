import {useEffect, useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {useFirmaFinalitzadaSessio} from "../../../components/SseExpedient.tsx";

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

export const useFirmaNevegador = () => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
    const {value: firma} = useFirmaFinalitzadaSessio();

    useEffect(() => {
        if (firma) {
            const severiry =
                firma?.status == 'OK' ? 'success'
                    : firma?.status == 'WARNING' ? 'warning'
                        : firma?.status == 'ERROR' ? 'error'
                            : 'info'

            temporalMessageShow(null, firma?.msg, severiry);
        }
    }, [firma]);

    const handleShow = (id: any): void => {
        apiRef.current?.show?.(id)
    }
    const formDialogResultProcessor = (result: any) => {
        return <iframe src={result?.url} width={'100%'} height={'500px'}/>
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