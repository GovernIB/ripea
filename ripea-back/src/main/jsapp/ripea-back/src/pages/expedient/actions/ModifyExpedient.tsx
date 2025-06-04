import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import {ExpedientGridForm} from "../ExpedientGrid.tsx";

export const ModifyExpedient = (props: { apiRef:any }) => {
    const { t } = useTranslation();
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={t('page.expedient.action.update.title')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        apiRef={apiRef}
    >
        <ExpedientGridForm/>
    </MuiFormDialog>
}

const useModifyExpedient = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.expedient.action.update.ok', {expedient: row?.nom}), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {
        handleShow,
        content: <ModifyExpedient apiRef={apiRef}/>
    }
}

export default useModifyExpedient;