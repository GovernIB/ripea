import {MuiFormDialog, useMuiFormDialogApiRef, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";

const AssignarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="agafatPer" required/>
    </Grid>
}

export const Assignar = (props: { apiRef:any }) => {
    const { t } = useTranslation();
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={t('page.expedient.action.assignar.title')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        dialogButtons={[
            {icon: 'person', text: t('page.expedient.action.assignar.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        apiRef={apiRef}
    >
        <AssignarForm/>
    </MuiFormDialog>
}

const useAssignar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.expedient.action.assignar.ok', {expedient: row?.nom}), 'success');
            })
            .catch((error) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        handleShow,
        content: <Assignar apiRef={apiRef}/>
    }
}

export default useAssignar;