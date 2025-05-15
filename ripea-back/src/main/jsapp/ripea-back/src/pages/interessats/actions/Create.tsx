import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {InteressatsGridForm} from "../InteressatsGrid.tsx";
import {useRef} from "react";

const CreateForm = (props:any) => {
    return <MuiFormDialog
        resourceName={"interessatResource"}
        {...props}
    >
        <InteressatsGridForm/>
    </MuiFormDialog>
}
const useCreate = (title:any, refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const create = (id:any, additionalData?:any, after?: (result?:any) => void) => {
        apiRef.current?.show(id, additionalData)
            .then((result:any) => {
                after?.(result);
                temporalMessageShow(null, '', 'success');
            })
            .catch((error:any) => {
                if(error) {
                    temporalMessageShow(null, error.message, 'error');
                }
            });
    }

    const createRepresentent = (id: any, row:any) => {
        apiRef.current?.show(undefined, {
            expedient: row?.expedient,
            representat: {
                id: id
            },
            esRepresentant: true,
        })
            .then(() => {
                refresh?.();
                temporalMessageShow(null, '', 'success');
            })
            .catch((error:any) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }
    const updateRepresentent = (id: any, row: any) => {
        apiRef.current?.show(row?.representant?.id)
            .then(() => {
                refresh?.();
                temporalMessageShow(null, '', 'success');
            })
            .catch((error:any) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        create,
        createRepresentent,
        updateRepresentent,
        content: <CreateForm title={title} apiRef={apiRef}/>,
    }
}
export default useCreate;