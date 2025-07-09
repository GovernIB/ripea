import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";

const CambiarPrioritatForm = () => {
    const {data} = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom" disabled readOnly/>
        <GridFormField xs={12} name="prioritat" required/>
        <GridFormField xs={12} name="prioritatMotiu" type={"textarea"} hidden={data?.prioritat=='B_NORMAL'} required/>
    </Grid>
}

export const CambiarPrioritat = (props: { apiRef:any }) => {
    const { t } = useTranslation();
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={t('page.expedient.action.changePrioritat.title')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        apiRef={apiRef}
    >
        <CambiarPrioritatForm/>
    </MuiFormDialog>
}

const useCambiarPrioritat = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.expedient.action.changePrioritat.ok', {expedient: row?.nom}), 'success');
            })
            .catch((error) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        handleShow,
        content: <CambiarPrioritat apiRef={apiRef}/>
    }
}
export default useCambiarPrioritat;