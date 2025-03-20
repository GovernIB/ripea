import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";

const RebutjarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiuRebuig" required/>
    </Grid>
}

const Rebutjar = (props: { apiRef:any }) => {
    const { t } = useTranslation();
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientTascaResource"}
        title={t('page.tasca.action.rebutjar')}
        apiRef={apiRef}
    >
        <RebutjarForm/>
    </MuiFormDialog>
}

const useRebutjar = (refresh?: (value?:string) => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any,row:any) => {
        // TODO: change
        // apiAction(id,{code:'ACTION_REBUTJAR'})
        return apiRef.current?.show?.(id,{data: {estat: 'REBUTJADA', motiuRebuig:row?.motiuRebuig}})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }

    return {
        handleShow,
        content: <Rebutjar apiRef={apiRef}/>
    }
}
export default useRebutjar;