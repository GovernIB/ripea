import {MuiFormDialog, useMuiFormDialogApiRef, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import {InteressatsGridForm} from "../InteressatsGridForm.tsx";

const CreateForm = (props:any) => {
    return <MuiFormDialog
        resourceName={"interessatResource"}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        {...props}
    >
        <InteressatsGridForm/>
    </MuiFormDialog>
}
const useCreate = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const create = (additionalData?:any, after?: (result?:any) => void) => {
        apiRef.current?.show(undefined, additionalData)
            .then((result:any) => {
                after?.(result);
                refresh?.();
                temporalMessageShow(null, t('page.interessat.action.new.ok', {data: result}), 'success');
            })
            .catch((error:any) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        create,
        content: <CreateForm resourceTitle={t('page.interessat.title')} apiRef={apiRef}/>,
    }
}
export const useCreateRepresentant = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const create = (id: any, row:any) => {
        apiRef.current?.show(undefined, {
            expedient: row?.expedient,
            representat: { id: id },
            esRepresentant: true,
        })
            .then((data:any) => {
                refresh?.();
                temporalMessageShow(null, t('page.interessat.action.createRep.ok',{data}), 'success');
            })
            .catch((error) => {
                if (error?.message)
                    temporalMessageShow(null, error?.message, 'error');
            });
    }
    const update = (_id: any, row: any) => {
        apiRef.current?.show(row?.representant?.id)
            .then((data:any) => {
                refresh?.();
                temporalMessageShow(null, t('page.interessat.action.updateRep.ok',{data}), 'success');
            })
            .catch((error) => {
                if (error?.message)
                    temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        create,
        update,
        content: <CreateForm resourceTitle={t('page.interessat.rep')} apiRef={apiRef}/>,
    }
}
export default useCreate;