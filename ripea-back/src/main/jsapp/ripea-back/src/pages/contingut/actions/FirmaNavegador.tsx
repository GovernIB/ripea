import {useRef, useState} from "react";
import {Grid2 as Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import Iframe from "../../../components/Iframe.tsx";
import {useFirmaFinalitzadaSession} from "../../../components/SseClient.tsx";

const FirmaNavegadorForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="motiu"/>
    </Grid>
}

const FirmaNavegador = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        title={t('page.document.action.firma.title')}
        action={"FIRMA_WEB_INI"}
        formDialogButtons={[
            {icon: 'play_arrow', text: t('page.document.action.firma.button'), componentProps: { variant: 'contained', disabled: props?.disabled }, value: true },
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
    const [disabled, setDisabled] = useState<boolean>()

    const handleShow = (id: any, row: any): void => {
        setDisabled(false)
        apiRef.current?.show?.(undefined, {
            ids: [id],
            massivo: false,
            motiu: "Tramitació del expedient RIPEA: " + row?.expedient?.description
        });
    }
    const onSuccess = () => {
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
    }
    const formDialogResultProcessor = (result: any) => {
        setDisabled(true)
        return <Iframe src={result?.url}/>
    }

    return {
        handleShow,
        content: <FirmaNavegador apiRef={apiRef} onSuccess={onSuccess} formDialogResultProcessor={formDialogResultProcessor} disabled={disabled}/>
    }
}
export const useFirmaNavegadorMassive = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
    const { onChange } = useFirmaFinalitzadaSession();
    const { value: user } = useUserSession();

    const handleShow = (ids: any[]): void => {
        apiRef.current?.show?.(undefined, {
            ids,
            massivo: true,
        });
    }
    const onSuccess = () => {
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
    }
    const formDialogResultProcessor = (result: any) => {
        return <Iframe src={result?.url}/>
    }

    return {
        handleShow,
        content: <FirmaNavegador apiRef={apiRef} onSuccess={onSuccess} formDialogResultProcessor={formDialogResultProcessor}/>
    }
}
export default useFirmaNavegador;