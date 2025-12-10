import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {useFirmaFinalitzadaSession} from "../../../components/SseExpedient.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import Iframe from "../../../components/Iframe.tsx";
import {useFirmaMassivaSession} from "../../../components/SseClient.tsx";

const FirmaNavegadorForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiu"/>
    </Grid>
}

const FirmaNavegador = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        title={t('page.document.action.firma.title')}
        action={"FIRMA_WEB_INI"}
        formDialogButtons={[
            {icon: 'play_arrow', text: t('page.document.action.firma.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <FirmaNavegadorForm/>
    </FormActionDialog>
}

export const useFirmaNavegador = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
    const { onChange } = useFirmaFinalitzadaSession();
    const { value: user } = useUserSession();

    onChange((firma) => {
		if (user?.codi==firma?.usuari) {
	        const severiry =
	            firma?.status == 'OK' ? 'success'
	                : firma?.status == 'WARNING' ? 'warning'
	                    : firma?.status == 'ERROR' ? 'error'
	                        : 'info';	
	        apiRef?.current?.close?.();
	        temporalMessageShow(null, firma?.msg, severiry);
		}
		refresh?.();
    })

    const handleShow = (id: any, row: any): void => {
        apiRef.current?.show?.(undefined, {
            ids: [id],
            massivo: false,
            motiu: "Tramitació del expedient RIPEA: " + row?.expedient?.description
        });
    }
    const formDialogResultProcessor = (result: any) => {
        return <Iframe isPDF={false} src={result?.url}/>
    }

    return {
        handleShow,
        content: <FirmaNavegador apiRef={apiRef} formDialogResultProcessor={formDialogResultProcessor}/>
    }
}
export const useFirmaNavegadorMassive = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
    const { onChange } = useFirmaMassivaSession();
    const { value: user } = useUserSession();

    onChange((firma) => {
		if (user?.codi==firma?.usuari) {
            // TODO: revisar logica de firma masiva
	        const severiry =
	            firma?.status == 'OK' ? 'success'
	                : firma?.status == 'WARNING' ? 'warning'
	                    : firma?.status == 'ERROR' ? 'error'
	                        : 'info';
	        apiRef?.current?.close?.();
	        temporalMessageShow(null, firma?.msg, severiry);
		}
		refresh?.();
    })

    const handleShow = (ids: any[]): void => {
        apiRef.current?.show?.(undefined, {
            ids,
            massivo: true,
        });
    }
    const formDialogResultProcessor = (result: any) => {
        return <Iframe isPDF={false} src={result?.url}/>
    }

    return {
        handleShow,
        content: <FirmaNavegador apiRef={apiRef} formDialogResultProcessor={formDialogResultProcessor}/>
    }
}
export default useFirmaNavegador;