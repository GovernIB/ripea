import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const RebutjarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="motiu" type={"textarea"} required/>
    </Grid>
}

const Rebutjar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientPeticioResource"}
        action={"REBUTJAR_ANOTACIO"}
        title={t('page.anotacio.action.rebutjar.title')}
        {...props}
    >
        <RebutjarForm/>
    </FormActionDialog>
}

const useRebutjar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
    }
    const onSuccess = () :void => {
        refresh?.();
        temporalMessageShow(null, t('page.anotacio.action.rebutjar.ok'), 'success');
    }

    return {
        handleShow,
        content: <Rebutjar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useRebutjar;