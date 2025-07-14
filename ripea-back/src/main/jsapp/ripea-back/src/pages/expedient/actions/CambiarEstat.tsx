import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";

const CambiarEstatForm = () => {
    const { data } = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom" disabled readOnly/>
        {/*<GridFormField xs={12} name="estat" disabled readOnly/>*/}
        <GridFormField xs={12} name="estatAdditional" hidden={data?.estat == "TANCAT"}/>
    </Grid>
}

export const CambiarEstat = (props: { apiRef:any }) => {
    const { t } = useTranslation();
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={t('page.expedient.action.changeEstat.title')}
        apiRef={apiRef}
        onClose={(reason?: string) => reason !== 'backdropClick'}
    >
        <CambiarEstatForm/>
    </MuiFormDialog>
}

const useCambiarEstat = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.expedient.action.changeEstat.ok', {expedient: row?.nom}), 'success');
            })
            .catch((error) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        handleShow,
        content: <CambiarEstat apiRef={apiRef}/>
    }
}
export default useCambiarEstat;