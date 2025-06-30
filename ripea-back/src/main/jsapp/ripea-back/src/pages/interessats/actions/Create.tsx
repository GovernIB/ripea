import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {InteressatsGridForm} from "../InteressatsGrid.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";

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
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const create = (additionalData?:any, after?: (result?:any) => void) => {
        apiRef.current?.show(undefined, additionalData)
            .then((result:any) => {
                after?.(result);
                refresh?.();
                temporalMessageShow(null, t('page.interessat.action.new.ok'), 'success');
            })
            .catch((error:any) => {
                if(error) {
                    temporalMessageShow(null, error.message, 'error');
                }
            });
    }

    return {
        create,
        content: <CreateForm resourceTitle={t('page.interessat.title')} apiRef={apiRef}/>,
    }
}
export const useCreateRepresentant = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const create = (id: any, row:any) => {
        apiRef.current?.show(undefined, {
            expedient: row?.expedient,
            representat: { id: id },
            esRepresentant: true,
        })
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.interessat.action.createRep.ok'), 'success');
            })
            .catch((error:any) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }
    const update = (id: any, row: any) => {
        apiRef.current?.show(row?.representant?.id)
            .then(() => {
                refresh?.();
                temporalMessageShow(null, t('page.interessat.action.updateRep.ok'), 'success');
            })
            .catch((error:any) => {
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