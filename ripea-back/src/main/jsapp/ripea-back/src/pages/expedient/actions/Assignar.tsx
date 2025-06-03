import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";

const AssignarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="agafatPer" required/>
    </Grid>
}

export const Assignar = (props: { apiRef:any }) => {
    const { t } = useTranslation();
    const { apiRef } = props;

    return <MuiFormDialog
        resourceName={"expedientResource"}
        title={t('page.expedient.action.assignar')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        apiRef={apiRef}
    >
        <AssignarForm/>
    </MuiFormDialog>
}

const useAssignar = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id)
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    return {
        handleShow,
        content: <Assignar apiRef={apiRef}/>
    }
}

export default useAssignar;