import {MuiFormDialog, useMuiFormDialogApiRef, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import {ExpedientGridForm} from "../ExpedientGrid.tsx";

export const ModifyExpedient = (props: { apiRef:any }) => {
    const { t } = useTranslation();
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={t('page.expedient.action.update.title')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        dialogButtons={[
            {icon: 'save', text: t('common.update'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        apiRef={apiRef}
    >
        <ExpedientGridForm/>
    </MuiFormDialog>
}

const useModifyExpedient = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
            .then((data:any) => {
                refresh?.()
                temporalMessageShow(null, t('page.expedient.action.update.ok', {data}), 'success');
            })
            .catch((error) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        handleShow,
        content: <ModifyExpedient apiRef={apiRef}/>
    }
}

export default useModifyExpedient;