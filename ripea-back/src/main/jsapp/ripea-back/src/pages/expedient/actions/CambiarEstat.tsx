import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";

const CambiarEstatForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom" readOnly/>
        <GridFormField xs={12} name="estat" required/>
    </Grid>
}

export const CambiarEstat = (props: { apiRef:any }) => {
    const { t } = useTranslation();
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={t('page.expedient.action.changeEstat')}
        apiRef={apiRef}
    >
        <CambiarEstatForm/>
    </MuiFormDialog>
}

const useCambiarEstat = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }

    return {
        handleShow,
        content: <CambiarEstat apiRef={apiRef}/>
    }
}
export default useCambiarEstat;