import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import {useUserSession} from "../../../components/Session.tsx";

export const CanviEstatRevisioForm = ({ editable = false }:any) => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        {/*rol actual “IPA_REVISIO”*/}
        <GridFormField xs={12} name="revisioEstat" disabled={!editable}/>
        <GridFormField xs={12} name="revisioComentari" type={'textarea'} disabled={!editable}/>
    </Grid>
}

export const CanviEstatRevisio = (props: { apiRef:any }) => {
    const { t } = useTranslation();
    const { apiRef } = props;
    const {rol} = useUserSession()

    return <MuiFormDialog
        resourceName={"metaExpedientResource"}
        title={t('page.metaExpedient.action.canviEstat.title')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        dialogButtons={[
            {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        apiRef={apiRef}
    >
        <CanviEstatRevisioForm editable={rol?.isRevisor || rol?.isAdmin}/>
    </MuiFormDialog>
}

const useCanviEstatRevisio = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaExpedient.action.canviEstat.ok', {expedient: row?.nom}), 'success');
            })
            .catch((error) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        handleShow,
        content: <CanviEstatRevisio apiRef={apiRef}/>
    }
}

export default useCanviEstatRevisio;