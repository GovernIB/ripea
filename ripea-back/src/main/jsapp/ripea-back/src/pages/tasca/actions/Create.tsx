import {MuiFormDialog, useMuiFormDialogApiRef, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import {TasquesGridForm} from "@src/pages/tasca/TasquesExpedientGrid.tsx";

const CreateForm = (props:any) => {
    return <MuiFormDialog
        resourceName={"expedientTascaResource"}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        {...props}
    >
        <TasquesGridForm/>
    </MuiFormDialog>
}
const useCreate = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const create = (additionalData?:any) => {
        apiRef.current?.show(undefined, additionalData)
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.tasca.action.new.ok'), 'success');
            })
            .catch((error:any) => {
                if (error?.message)
                    temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        handleShow: create,
        content: <CreateForm resourceTitle={t('page.tasca.title')} apiRef={apiRef}/>,
    }
}
export default useCreate;
